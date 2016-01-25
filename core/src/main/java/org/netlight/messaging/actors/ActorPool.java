package org.netlight.messaging.actors;

import org.netlight.channel.ChannelContext;
import org.netlight.messaging.Message;
import org.netlight.util.CommonUtils;
import org.netlight.util.MaxMinHolder;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;

/**
 * @author ahmad
 */
public final class ActorPool {

    private final int parallelism;
    private final ExecutorService executorService;
    private final ActorFactory actorFactory;
    private final ConcurrentMap<Long, Actor> actors = new ConcurrentHashMap<>();
    private final ConcurrentMap<Actor, RunnableActor> runnableActors = new ConcurrentHashMap<>();
    private final ActorFutureListener actorFutureListener = new ActorFutureListenerImpl();

    public ActorPool(ActorFactory actorFactory) {
        this(Runtime.getRuntime().availableProcessors() * 2, actorFactory);
    }

    public ActorPool(int parallelism, ActorFactory actorFactory) {
        this.parallelism = parallelism;
        this.actorFactory = actorFactory;
        executorService = new ForkJoinPool(parallelism);
    }

    public int getParallelism() {
        return parallelism;
    }

    public Actor next() {
        final MaxMinHolder<Integer, Actor> holder = new MaxMinHolder<>();
        for (Actor actor : actors.values()) {
            holder.in(isStateEqualTo(actor, RunnableActorState.IDLE) ? actor.load() - 1 : actor.load(), actor);
        }
        Map.Entry<Integer, Actor> nextEntry = holder.getMin();
        if (nextEntry == null) {
            return createActor();
        }
        Actor next = nextEntry.getValue();
        if (next.load() > 0) {
            Actor newActor = createActor();
            if (newActor != null) {
                next = newActor;
            }
        }
        return next;
    }

    private Actor createActor() {
        synchronized (actors) {
            if (actors.size() < parallelism) {
                Actor actor = new ActorDecorator(actorFactory.create());
                actors.put(actor.id(), actor);
                return actor;
            }
        }
        return null;
    }

    private void activate(Actor actor) {
        RunnableActor r = CommonUtils.getOrPutConcurrent(runnableActors, actor, actor::makeRunnable);
        if (r.state() == RunnableActorState.IN_ACTIVE) {
            final Lock l = actor.lock();
            l.lock();
            try {
                if (!r.isFirstRun()) {
                    runnableActors.put(actor, r = actor.makeRunnable());
                }
                r.future().addListener(actorFutureListener);
                executorService.execute(r);
            } finally {
                l.unlock();
            }
        }
    }

    public RunnableActorState getState(Actor actor) {
        return CommonUtils.computeIf(runnableActors.get(actor), CommonUtils.notNull(), RunnableActor::state);
    }

    public boolean isStateEqualTo(Actor actor, RunnableActorState expect) {
        return CommonUtils.computeIf(getState(actor), CommonUtils.notNull(), state -> state == expect, false);
    }

    public void shutdown() {
        executorService.shutdown();
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(15L, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    private final class ActorFutureListenerImpl implements ActorFutureListener {

        @Override
        public void onComplete(ActorFuture future) {
            final Actor actor = future.actor();
            if (actor instanceof ActorDecorator && actors.containsKey(actor.id())) {
                if (future.isCompletedExceptionally() || actor.load() > 0) {
                    // TODO log
                    activate(actor);
                }
            }
        }

    }

    private final class ActorDecorator implements Actor {

        private final Actor decoratedActor;

        private ActorDecorator(Actor decoratedActor) {
            this.decoratedActor = decoratedActor;
        }

        @Override
        public long id() {
            return decoratedActor.id();
        }

        @Override
        public void tell(Message message, ChannelContext ctx, int weight) {
            decoratedActor.tell(message, ctx, weight);
            activate(this);
        }

        @Override
        public int load() {
            return decoratedActor.load();
        }

        @Override
        public Lock lock() {
            return decoratedActor.lock();
        }

        @Override
        public RunnableActor makeRunnable() {
            return decoratedActor.makeRunnable();
        }

    }

}

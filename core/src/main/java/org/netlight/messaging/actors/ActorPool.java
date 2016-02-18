package org.netlight.messaging.actors;

import org.netlight.channel.ChannelContext;
import org.netlight.messaging.*;
import org.netlight.util.CommonUtils;
import org.netlight.util.MapEntry;
import org.netlight.util.MaxMinHolder;
import org.netlight.util.TimeProperty;
import org.netlight.util.concurrent.AtomicBooleanField;
import org.netlight.util.concurrent.AtomicIntegerField;
import org.netlight.util.concurrent.AtomicLongField;
import org.netlight.util.concurrent.AtomicReferenceField;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * @author ahmad
 */
public final class ActorPool {

    private final int parallelism;
    private final MessageListener messageListener;
    private final ActorExecutorService actorExecutorService;
    private final AtomicIntegerField poolSize = new AtomicIntegerField();
    private final Set<Actor> actors = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public ActorPool(MessageListener messageListener) {
        this(Runtime.getRuntime().availableProcessors() * 2, messageListener);
    }

    public ActorPool(int parallelism, MessageListener messageListener) {
        Objects.requireNonNull(messageListener);
        this.parallelism = parallelism;
        this.messageListener = messageListener;
        actorExecutorService = new ActorExecutorService(parallelism);
    }

    public int getParallelism() {
        return parallelism;
    }

    public int getPoolSize() {
        return poolSize.get();
    }

    public Actor next() {
        final MaxMinHolder<Integer, Actor> holder = new MaxMinHolder<>();
        for (Actor actor : actors) {
            int load = actor.load();
            if (load == 0 && actorExecutorService.isStateEqualTo(actor, RunnableActorState.IDLE)) {
                holder.in(-1, actor);
            } else {
                holder.in(load, actor);
            }
        }
        final MapEntry<Integer, Actor> nextEntry = holder.getMin();
        if (nextEntry == null) {
            return createActor();
        }
        Actor next = nextEntry.getValue();
        if (next.load() > 0) {
            final Actor newActor = createActor();
            if (newActor != null) {
                next = newActor;
            }
        }
        return next;
    }

    private Actor createActor() {
        if (poolSize.getAndIncrement() < parallelism) {
            final Actor actor = new DefaultActor(messageListener, actorExecutorService);
            if (actors.add(actor)) {
                return actor;
            }
        }
        poolSize.getAndDecrement();
        return null;
    }

    public void shutdown() {
        actorExecutorService.shutdown();
    }

    private static final class ActorExecutorService implements Function<Actor, RunnableActor>, ActorFutureListener {

        private final ExecutorService executorService;
        private final ConcurrentMap<Actor, RunnableActor> runnableActors = new ConcurrentHashMap<>();

        private ActorExecutorService(int parallelism) {
            executorService = new ForkJoinPool(parallelism);
        }

        private void execute(Actor actor) {
            runnableActors.computeIfAbsent(actor, this);
        }

        @Override
        public RunnableActor apply(Actor actor) {
            final RunnableActor r = actor.makeRunnable();
            r.future().addListener(this);
            executorService.execute(r);
            return r;
        }

        @Override
        public void onComplete(ActorFuture future) {
            final Actor actor = future.actor();
            if (runnableActors.remove(actor) != null && (future.isCompletedExceptionally() || actor.load() > 0)) {
                // TODO log
                execute(actor);
            }
        }

        private RunnableActorState getState(Actor actor) {
            return CommonUtils.computeIf(runnableActors.get(actor), CommonUtils.notNull(), RunnableActor::state);
        }

        private boolean isStateEqualTo(Actor actor, RunnableActorState expect) {
            return CommonUtils.computeIf(getState(actor), CommonUtils.notNull(), state -> state == expect, false);
        }

        private void shutdown() {
            executorService.shutdown();
            executorService.shutdownNow();
            try {
                executorService.awaitTermination(15L, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
        }

    }

    private static final class DefaultActor implements Actor {

        private static final AtomicLongField ACTOR_ID = new AtomicLongField();

        private final long id = ACTOR_ID.getAndIncrement();
        private final MessageListener messageListener;
        private final ActorExecutorService actorExecutorService;
        private final Queue<RichMessage> mailbox = new ConcurrentQueue<>();
        private final AtomicIntegerField load = new AtomicIntegerField();

        private DefaultActor(MessageListener messageListener, ActorExecutorService actorExecutorService) {
            this.messageListener = messageListener;
            this.actorExecutorService = actorExecutorService;
        }

        @Override
        public long id() {
            return id;
        }

        @Override
        public void tell(Message message, ChannelContext ctx, int weight) {
            if (mailbox.add(new RichMessage(message, ctx, System.currentTimeMillis(), weight))) {
                load.getAndAdd(weight);
                actorExecutorService.execute(this);
            }
        }

        @Override
        public int load() {
            return load.get();
        }

        @Override
        public RunnableActor makeRunnable() {
            return new OneTimeRunnableActor(this, messageListener);
        }

        @Override
        public int hashCode() {
            return Long.hashCode(id);
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this || obj != null && obj instanceof DefaultActor && id == ((DefaultActor) obj).id;
        }

    }

    private static final class OneTimeRunnableActor implements RunnableActor {

        private static final TimeProperty IDLE_TIMEOUT = TimeProperty.seconds(60L);

        private final DefaultActor actor;
        private final MessageListener messageListener;
        private final ActorPromise promise;
        private final AtomicReferenceField<RunnableActorState> state = new AtomicReferenceField<>(RunnableActorState.IN_ACTIVE);
        private final AtomicBooleanField firstRun = new AtomicBooleanField(true);

        private OneTimeRunnableActor(DefaultActor actor, MessageListener messageListener) {
            this.actor = actor;
            this.messageListener = messageListener;
            promise = new DefaultActorPromise(actor);
        }

        @Override
        public ActorFuture future() {
            return promise;
        }

        @Override
        public RunnableActorState state() {
            return state.get();
        }

        @Override
        public void run() {
            if (!firstRun.compareAndSet(true, false)) {
                throw new IllegalStateException("This Runnable can be run only once.");
            }
            if (state.compareAndSet(RunnableActorState.IN_ACTIVE, RunnableActorState.ACTIVE)) {
                try {
                    RichMessage richMessage;
                    while (!Thread.currentThread().isInterrupted()) {
                        if ((richMessage = actor.mailbox.poll()) == null) {
                            state.set(RunnableActorState.IDLE);
                            if ((richMessage = actor.mailbox.poll(IDLE_TIMEOUT)) == null) {
                                break;
                            }
                            state.set(RunnableActorState.ACTIVE);
                        }
                        actor.load.getAndAdd(-richMessage.getWeight());
                        messageListener.onMessage(richMessage.getMessage(), richMessage.getChannelContext());
                    }
                } catch (Throwable cause) {
                    promise.setFailure(cause);
                } finally {
                    state.set(RunnableActorState.IN_ACTIVE);
                    promise.complete();
                }
            }
        }

    }

}

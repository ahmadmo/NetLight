package org.netlight.messaging.actors;

import org.netlight.util.CommonUtils;
import org.netlight.util.concurrent.AtomicBooleanField;

import java.util.concurrent.*;

/**
 * @author ahmad
 */
public final class ActorPool implements ActorFutureListener {

    private final int parallelism;
    private final ExecutorService executorService;
    private final ConcurrentMap<Actor, AtomicBooleanField> actors = new ConcurrentHashMap<>();

    public ActorPool(int parallelism) {
        this.parallelism = parallelism;
        executorService = new ForkJoinPool(parallelism);
    }

    public int getParallelism() {
        return parallelism;
    }

    public Actor next() {
        return new Actor(null);
    }

    private void activate(Actor actor) {
        final AtomicBooleanField active = actors.get(actor);
        if (active.compareAndSet(false, true)) {
            ActorPromise actorPromise = new DefaultActorPromise(actor);
            actorPromise.addListener(this);
            executorService.execute(new RunnableActor(actorPromise));
        }
    }

    public void shutdown() {
        executorService.shutdown();
        executorService.shutdownNow();
        try {
            executorService.awaitTermination(15L, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void onComplete(ActorFuture future) {
        CommonUtils.computeIf(actors.get(future.actor()), CommonUtils.notNull(), active -> {
            active.set(false);
            if (future.isCompletedExceptionally()) {
                activate(future.actor());
            }
        });
    }

}

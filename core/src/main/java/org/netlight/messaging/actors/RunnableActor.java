package org.netlight.messaging.actors;

import org.netlight.util.TimeProperty;

import java.util.Objects;

/**
 * @author ahmad
 */
public final class RunnableActor implements Runnable {

    private static final TimeProperty IDLE_TIMEOUT = TimeProperty.seconds(30L);

    private final Actor actor;
    private final ActorPromise actorPromise;

    public RunnableActor(ActorPromise actorPromise) {
        Objects.requireNonNull(actorPromise);
        this.actorPromise = actorPromise;
        actor = actorPromise.actor();
    }

    public Actor getActor() {
        return actor;
    }

    public ActorPromise getActorPromise() {
        return actorPromise;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                if (!actor.handleNext(IDLE_TIMEOUT)) {
                    break;
                }
            }
        } catch (Throwable cause) {
            actorPromise.setFailure(cause);
        } finally {
            actorPromise.complete();
        }
    }

    @Override
    public int hashCode() {
        return actor.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj instanceof RunnableActor && actor.equals(((RunnableActor) obj).actor);
    }

}

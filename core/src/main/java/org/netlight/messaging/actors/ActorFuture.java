package org.netlight.messaging.actors;

/**
 * @author ahmad
 */
public interface ActorFuture {

    Actor actor();

    boolean isDone();

    Throwable cause();

    boolean isCompletedExceptionally();

    ActorFuture addListener(ActorFutureListener listener);

    ActorFuture removeListener(ActorFutureListener listener);

}

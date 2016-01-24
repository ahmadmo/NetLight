package org.netlight.messaging.actors;

/**
 * @author ahmad
 */
interface ActorPromise extends ActorFuture {

    ActorPromise complete();

    ActorPromise setFailure(Throwable cause);

    @Override
    ActorPromise addListener(ActorFutureListener listener);

    @Override
    ActorPromise removeListener(ActorFutureListener listener);

}

package org.netlight.messaging.actors;

/**
 * @author ahmad
 */
public interface RunnableActor extends Runnable {

    ActorFuture future();

    RunnableActorState state();

    boolean isFirstRun();

}

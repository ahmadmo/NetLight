package org.netlight.messaging.actors;

import org.netlight.messaging.MessageListener;

/**
 * @author ahmad
 */
public final class ActorFactory {

    private final MessageListener messageListener;

    public static ActorFactory listener(MessageListener messageListener) {
        return new ActorFactory(messageListener);
    }

    private ActorFactory(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public Actor create() {
        return new Actor(messageListener);
    }

}

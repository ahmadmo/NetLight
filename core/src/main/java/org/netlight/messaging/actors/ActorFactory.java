package org.netlight.messaging.actors;

import org.netlight.messaging.MessageListener;

/**
 * @author ahmad
 */
public final class ActorFactory {

    private final MessageListener messageListener;

    public ActorFactory(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public Actor create() {
        return new DefaultActor(messageListener);
    }

}

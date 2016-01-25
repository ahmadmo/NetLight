package org.netlight.messaging.actors;

import org.netlight.channel.ChannelContext;
import org.netlight.messaging.Message;

import java.util.Objects;

/**
 * @author ahmad
 */
public final class ActorController {

    private final ActorPool actorPool;

    public ActorController(ActorPool actorPool) {
        Objects.requireNonNull(actorPool);
        this.actorPool = actorPool;
    }

    public void handleMessage(Message message, ChannelContext ctx) {
        handleMessage(message, ctx, 1);
    }

    public void handleMessage(Message message, ChannelContext ctx, int weight) {
        actorPool.next().tell(message, ctx, weight);
    }

}

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

    public void handleMessage(ChannelContext ctx, Message message) {
        handleMessage(ctx, message, 1);
    }

    public void handleMessage(ChannelContext ctx, Message message, int weight) {
        actorPool.next().tell(ctx, message, weight);
    }

}

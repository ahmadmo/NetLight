package org.netlight.messaging.actors;

import org.netlight.channel.ChannelContext;
import org.netlight.messaging.Message;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ahmad
 */
public final class ActorController {

    private final ActorPool actorPool;
    private final Set<Actor> actors = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public ActorController(ActorPool actorPool) {
        Objects.requireNonNull(actorPool);
        this.actorPool = actorPool;
    }

    public void handleMessage(ChannelContext ctx, Message message) {
        actorPool.next().tell(ctx, message);
    }

}

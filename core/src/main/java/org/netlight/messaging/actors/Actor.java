package org.netlight.messaging.actors;

import org.netlight.channel.ChannelContext;
import org.netlight.messaging.Message;

import java.util.concurrent.locks.Lock;

/**
 * @author ahmad
 */
public interface Actor {

    long id();

    void tell(ChannelContext ctx, Message message, int weight);

    int load();

    Lock lock();

    RunnableActor makeRunnable();

}

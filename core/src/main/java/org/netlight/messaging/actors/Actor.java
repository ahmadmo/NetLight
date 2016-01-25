package org.netlight.messaging.actors;

import org.netlight.channel.ChannelContext;
import org.netlight.messaging.Message;

import java.util.concurrent.locks.Lock;

/**
 * @author ahmad
 */
public interface Actor {

    long id();

    void tell(Message message, ChannelContext ctx, int weight);

    int load();

    Lock lock();

    RunnableActor makeRunnable();

}

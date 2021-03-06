package org.netlight.messaging;

import org.netlight.channel.ChannelContext;

/**
 * @author ahmad
 */
public interface MessageListener {

    void onMessage(Message message, ChannelContext ctx);

}

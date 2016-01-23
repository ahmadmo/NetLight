package org.netlight.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.netlight.messaging.Message;
import org.netlight.messaging.MessagePromise;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.Collection;

/**
 * @author ahmad
 */
public interface ChannelContext extends Serializable {

    String id();

    ChannelHandlerContext channelHandlerContext();

    Channel channel();

    RichChannelHandler channelHandler();

    SocketAddress remoteAddress();

    MessagePromise newPromise(Message message);

    Collection<MessagePromise> newPromises(Collection<Message> messages);

    MessagePromise voidPromise(Message message);

    Collection<MessagePromise> voidPromises(Collection<Message> messages);

    MessagePromise sendMessage(Message message);

    MessagePromise sendMessage(MessagePromise promise);

    Collection<MessagePromise> sendMessages0(Collection<Message> messages);

    Collection<MessagePromise> sendMessages(Collection<MessagePromise> promises);

}

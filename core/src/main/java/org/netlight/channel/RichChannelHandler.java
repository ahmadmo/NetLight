package org.netlight.channel;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.netlight.messaging.Message;
import org.netlight.messaging.MessagePromise;

import java.net.SocketAddress;
import java.util.Collection;

/**
 * @author ahmad
 */
public interface RichChannelHandler extends ChannelHandler {

    MessagePromise newPromise(SocketAddress remoteAddress, Message message);

    Collection<MessagePromise> newPromises(SocketAddress remoteAddress, Collection<Message> messages);

    MessagePromise voidPromise(SocketAddress remoteAddress, Message message);

    Collection<MessagePromise> voidPromises(SocketAddress remoteAddress, Collection<Message> messages);

    MessagePromise sendMessage(ChannelHandlerContext ctx, SocketAddress remoteAddress, Message message);

    MessagePromise sendMessage(ChannelHandlerContext ctx, MessagePromise promise);

    Collection<MessagePromise> sendMessages(ChannelHandlerContext ctx, SocketAddress remoteAddress, Collection<Message> messages);

    Collection<MessagePromise> sendMessages(ChannelHandlerContext ctx, Collection<MessagePromise> promises);

}

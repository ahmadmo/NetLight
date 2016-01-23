package org.netlight.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.netlight.messaging.DefaultMessagePromise;
import org.netlight.messaging.Message;
import org.netlight.messaging.MessagePromise;
import org.netlight.messaging.VoidMessagePromise;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author ahmad
 */
public abstract class AbstractRichChannelHandler<T> extends SimpleChannelInboundHandler<T> implements RichChannelHandler {

    @Override
    public MessagePromise newPromise(SocketAddress remoteAddress, Message message) {
        return new DefaultMessagePromise(message, remoteAddress);
    }

    @Override
    public Collection<MessagePromise> newPromises(SocketAddress remoteAddress, Collection<Message> messages) {
        return messages.stream()
                .map(message -> new DefaultMessagePromise(message, remoteAddress))
                .collect(Collectors.toList());
    }

    @Override
    public MessagePromise voidPromise(SocketAddress remoteAddress, Message message) {
        return new VoidMessagePromise(message, remoteAddress);
    }

    @Override
    public Collection<MessagePromise> voidPromises(SocketAddress remoteAddress, Collection<Message> messages) {
        return messages.stream()
                .map(message -> new VoidMessagePromise(message, remoteAddress))
                .collect(Collectors.toList());
    }

    @Override
    public MessagePromise sendMessage(ChannelHandlerContext ctx, SocketAddress remoteAddress, Message message) {
        MessagePromise promise = newPromise(remoteAddress, message);
        sendMessage(ctx, promise);
        return promise;
    }

    @Override
    public Collection<MessagePromise> sendMessages(ChannelHandlerContext ctx, SocketAddress remoteAddress, Collection<Message> messages) {
        Collection<MessagePromise> promises = newPromises(remoteAddress, messages);
        sendMessages(ctx, promises);
        return promises;
    }

}

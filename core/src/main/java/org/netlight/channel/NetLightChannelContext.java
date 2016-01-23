package org.netlight.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.netlight.messaging.Message;
import org.netlight.messaging.MessagePromise;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.Objects;

/**
 * @author ahmad
 */
public final class NetLightChannelContext implements ChannelContext {

    private static final long serialVersionUID = 5593016923444804597L;

    private final String id;
    private final ChannelHandlerContext channelHandlerContext;
    private final RichChannelHandler channelHandler;

    public NetLightChannelContext(String id, ChannelHandlerContext channelHandlerContext, RichChannelHandler channelHandler) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(channelHandlerContext);
        Objects.requireNonNull(channelHandler);
        this.id = id;
        this.channelHandlerContext = channelHandlerContext;
        this.channelHandler = channelHandler;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public ChannelHandlerContext channelHandlerContext() {
        return channelHandlerContext;
    }

    @Override
    public Channel channel() {
        return channelHandlerContext.channel();
    }

    @Override
    public RichChannelHandler channelHandler() {
        return channelHandler;
    }

    @Override
    public SocketAddress remoteAddress() {
        return channelHandlerContext.channel().remoteAddress();
    }

    @Override
    public MessagePromise newPromise(Message message) {
        return channelHandler.newPromise(remoteAddress(), message);
    }

    @Override
    public Collection<MessagePromise> newPromises(Collection<Message> messages) {
        return channelHandler.newPromises(remoteAddress(), messages);
    }

    @Override
    public MessagePromise voidPromise(Message message) {
        return channelHandler.voidPromise(remoteAddress(), message);
    }

    @Override
    public Collection<MessagePromise> voidPromises(Collection<Message> messages) {
        return channelHandler.voidPromises(remoteAddress(), messages);
    }

    @Override
    public MessagePromise sendMessage(Message message) {
        return channelHandler.sendMessage(channelHandlerContext, remoteAddress(), message);
    }

    @Override
    public MessagePromise sendMessage(MessagePromise promise) {
        return channelHandler.sendMessage(channelHandlerContext, promise);
    }

    @Override
    public Collection<MessagePromise> sendMessages0(Collection<Message> messages) {
        return channelHandler.sendMessages(channelHandlerContext, remoteAddress(), messages);
    }

    @Override
    public Collection<MessagePromise> sendMessages(Collection<MessagePromise> promises) {
        return channelHandler.sendMessages(channelHandlerContext, promises);
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj instanceof NetLightChannelContext && id.equals(((NetLightChannelContext) obj).id);
    }

}

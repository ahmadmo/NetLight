package org.netlight.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import org.netlight.messaging.protocol.MessagingProtocol;

import java.util.Objects;

/**
 * @author ahmad
 */
public final class TcpChannelInitializer extends ChannelInitializer<Channel> {

    private final MessagingProtocol messagingProtocol;
    private final RichChannelHandler channelHandler;

    public TcpChannelInitializer(MessagingProtocol messagingProtocol, RichChannelHandler channelHandler) {
        Objects.requireNonNull(messagingProtocol);
        Objects.requireNonNull(channelHandler);
        this.messagingProtocol = messagingProtocol;
        this.channelHandler = channelHandler;
    }

    @Override
    public void initChannel(Channel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast("decoder", messagingProtocol.decoder());
        p.addLast("encoder", messagingProtocol.encoder());
        p.addLast("handler", channelHandler);
    }

    public MessagingProtocol getMessagingProtocol() {
        return messagingProtocol;
    }

    public RichChannelHandler getChannelHandler() {
        return channelHandler;
    }

}

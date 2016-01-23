package org.netlight.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import org.netlight.encoding.MessageEncodingProtocol;

import java.util.Objects;

/**
 * @author ahmad
 */
public final class TcpChannelInitializer extends ChannelInitializer<Channel> {

    private final MessageEncodingProtocol messageEncodingProtocol;
    private final RichChannelHandler channelHandler;

    public TcpChannelInitializer(MessageEncodingProtocol messageEncodingProtocol, RichChannelHandler channelHandler) {
        Objects.requireNonNull(messageEncodingProtocol);
        Objects.requireNonNull(channelHandler);
        this.messageEncodingProtocol = messageEncodingProtocol;
        this.channelHandler = channelHandler;
    }

    @Override
    public void initChannel(Channel ch) {
        ChannelPipeline p = ch.pipeline();
        p.addLast("decoder", messageEncodingProtocol.decoder());
        p.addLast("encoder", messageEncodingProtocol.encoder());
        p.addLast("handler", channelHandler);
    }

    public MessageEncodingProtocol getMessageEncodingProtocol() {
        return messageEncodingProtocol;
    }

    public RichChannelHandler getChannelHandler() {
        return channelHandler;
    }

}

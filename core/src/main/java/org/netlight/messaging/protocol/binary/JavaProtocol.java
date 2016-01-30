package org.netlight.messaging.protocol.binary;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import org.netlight.messaging.protocol.MessagingProtocol;
import org.netlight.messaging.serialization.JavaMessageSerializer;

/**
 * @author ahmad
 */
public final class JavaProtocol extends MessagingProtocol {

    private static final JavaMessageSerializer JAVA_MESSAGE_SERIALIZER = new JavaMessageSerializer();

    @Override
    public ChannelInboundHandler decoder() {
        return new BinaryMessageDecoder(JAVA_MESSAGE_SERIALIZER);
    }

    @Override
    protected ChannelOutboundHandler initEncoder() {
        return new BinaryMessageEncoder(JAVA_MESSAGE_SERIALIZER);
    }

}

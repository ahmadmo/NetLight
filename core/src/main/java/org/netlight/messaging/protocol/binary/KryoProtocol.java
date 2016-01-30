package org.netlight.messaging.protocol.binary;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import org.netlight.messaging.protocol.MessagingProtocol;
import org.netlight.messaging.serialization.KryoMessageSerializer;

/**
 * @author ahmad
 */
public final class KryoProtocol extends MessagingProtocol {

    private final KryoMessageSerializer serializer;

    public KryoProtocol() {
        serializer = KryoMessageSerializer.builder().build();
    }

    public KryoProtocol(KryoMessageSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public ChannelInboundHandler decoder() {
        return new BinaryMessageDecoder(serializer);
    }

    @Override
    protected ChannelOutboundHandler initEncoder() {
        return new BinaryMessageEncoder(serializer);
    }

}

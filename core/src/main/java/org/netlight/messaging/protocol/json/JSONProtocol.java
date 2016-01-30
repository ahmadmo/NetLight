package org.netlight.messaging.protocol.json;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import org.netlight.messaging.protocol.MessagingProtocol;
import org.netlight.messaging.serialization.JSONMessageSerializer;

/**
 * @author ahmad
 */
public final class JSONProtocol extends MessagingProtocol {

    private final JSONMessageSerializer serializer;

    public JSONProtocol() {
        serializer = new JSONMessageSerializer();
    }

    public JSONProtocol(JSONMessageSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public ChannelInboundHandler decoder() {
        return new JsonMessageDecoder(serializer);
    }

    @Override
    protected ChannelOutboundHandler initEncoder() {
        return new JsonMessageEncoder(serializer);
    }

}

package org.netlight.messaging.protocol.json;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.netlight.messaging.Message;
import org.netlight.messaging.serialization.JSONMessageSerializer;

import java.nio.CharBuffer;
import java.util.List;
import java.util.Objects;

/**
 * @author ahmad
 */
@ChannelHandler.Sharable
public final class JsonMessageEncoder extends MessageToMessageEncoder<Message> {

    private final JSONMessageSerializer serializer;

    public JsonMessageEncoder(JSONMessageSerializer serializer) {
        Objects.requireNonNull(serializer);
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        if (msg != null) {
            out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(serializer.serialize(msg)), serializer.charset()));
        }
    }

}

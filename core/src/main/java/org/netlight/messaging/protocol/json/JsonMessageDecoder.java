package org.netlight.messaging.protocol.json;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.RecyclableArrayList;
import org.netlight.messaging.serialization.JSONMessageSerializer;

import java.util.List;
import java.util.Objects;

/**
 * @author ahmad
 */
public final class JsonMessageDecoder extends MessageToMessageDecoder<ByteBuf> {

    private static final ThreadLocal<JsonObjectDecoder> JSON_OBJECT_DECODER = new ThreadLocal<JsonObjectDecoder>() {
        @Override
        protected JsonObjectDecoder initialValue() {
            return new JsonObjectDecoder();
        }
    };

    private final JSONMessageSerializer serializer;

    public JsonMessageDecoder(JSONMessageSerializer serializer) {
        Objects.requireNonNull(serializer);
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        RecyclableArrayList buffers = RecyclableArrayList.newInstance();
        JSON_OBJECT_DECODER.get().decode(ctx, msg, buffers);
        for (Object buf : buffers) {
            out.add(serializer.deserialize(((ByteBuf) buf).toString(serializer.charset())));
        }
        buffers.recycle();
    }

}

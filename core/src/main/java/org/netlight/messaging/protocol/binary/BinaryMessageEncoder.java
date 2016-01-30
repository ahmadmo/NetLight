package org.netlight.messaging.protocol.binary;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.netlight.messaging.Message;
import org.netlight.messaging.serialization.BinaryMessageSerializer;

import java.util.Objects;

/**
 * @author ahmad
 */
@ChannelHandler.Sharable
public final class BinaryMessageEncoder extends MessageToByteEncoder<Message> {

    private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

    private final BinaryMessageSerializer serializer;

    public BinaryMessageEncoder(BinaryMessageSerializer serializer) {
        Objects.requireNonNull(serializer);
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        int startIdx = out.writerIndex();
        out.writeBytes(LENGTH_PLACEHOLDER);
        out.writeBytes(serializer.serialize(msg));
        int endIdx = out.writerIndex();
        out.setInt(startIdx, endIdx - startIdx - 4);
    }

}
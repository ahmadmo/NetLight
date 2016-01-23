package org.netlight.encoding;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import org.netlight.util.concurrent.AtomicReferenceField;

/**
 * @author ahmad
 */
public enum MessageEncodingProtocol {

    JAVA {
        @Override
        public ChannelInboundHandler decoder() {
            return new BinaryMessageDecoder(StandardMessageSerializers.JAVA);
        }

        @Override
        protected ChannelOutboundHandler initEncoder() {
            return new BinaryMessageEncoder(StandardMessageSerializers.JAVA);
        }
    }, KRYO {
        @Override
        public ChannelInboundHandler decoder() {
            return new BinaryMessageDecoder(StandardMessageSerializers.KRYO);
        }

        @Override
        protected ChannelOutboundHandler initEncoder() {
            return new BinaryMessageEncoder(StandardMessageSerializers.KRYO);
        }
    }, JSON {
        @Override
        public ChannelInboundHandler decoder() {
            return new JsonMessageDecoder();
        }

        @Override
        protected ChannelOutboundHandler initEncoder() {
            return new JsonMessageEncoder();
        }
    };

    private final AtomicReferenceField<ChannelOutboundHandler> encoder = new AtomicReferenceField<>();

    public abstract ChannelInboundHandler decoder();

    protected abstract ChannelOutboundHandler initEncoder();

    public ChannelOutboundHandler encoder() {
        encoder.compareAndSet(null, initEncoder());
        return encoder.get();
    }

}

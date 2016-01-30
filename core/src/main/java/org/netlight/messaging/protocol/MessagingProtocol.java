package org.netlight.messaging.protocol;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import org.netlight.util.concurrent.AtomicReferenceField;

/**
 * @author ahmad
 */
public abstract class MessagingProtocol {

    private final AtomicReferenceField<ChannelOutboundHandler> encoder = new AtomicReferenceField<>();

    public abstract ChannelInboundHandler decoder();

    protected abstract ChannelOutboundHandler initEncoder();

    public ChannelOutboundHandler encoder() {
        encoder.compareAndSet(null, initEncoder());
        return encoder.get();
    }

}

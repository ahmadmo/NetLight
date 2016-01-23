package org.netlight.messaging.serialization;

/**
 * @author ahmad
 */
public interface BinaryMessageSerializer extends MessageSerializer<byte[]> {

    @Override
    default Class<byte[]> getOutputType() {
        return byte[].class;
    }

}

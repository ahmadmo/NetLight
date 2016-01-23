package org.netlight.messaging.serialization;

import java.nio.charset.Charset;

/**
 * @author ahmad
 */
public interface TextMessageSerializer extends MessageSerializer<String> {

    Charset charset();

    @Override
    default Class<String> getOutputType() {
        return String.class;
    }

}

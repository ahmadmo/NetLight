package org.netlight.encoding;

import org.netlight.messaging.serialization.*;

/**
 * @author ahmad
 */
public final class StandardMessageSerializers {

    private StandardMessageSerializers() {
    }

    public static final BinaryMessageSerializer JAVA = new JavaMessageSerializer();
    public static final BinaryMessageSerializer KRYO = new KryoMessageSerializer();
    public static final TextMessageSerializer JSON = new JSONMessageSerializer();

}
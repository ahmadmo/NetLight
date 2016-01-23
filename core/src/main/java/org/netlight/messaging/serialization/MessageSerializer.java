package org.netlight.messaging.serialization;

import org.netlight.messaging.Message;

/**
 * @author ahmad
 */
public interface MessageSerializer<T> {

    T serialize(Message message) throws Exception;

    Message deserialize(T data) throws Exception;

    Class<T> getOutputType();

}

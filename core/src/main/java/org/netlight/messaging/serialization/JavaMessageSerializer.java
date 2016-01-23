package org.netlight.messaging.serialization;

import org.netlight.messaging.Message;

import java.io.*;

/**
 * @author ahmad
 */
public final class JavaMessageSerializer implements BinaryMessageSerializer {

    @Override
    public byte[] serialize(Message message) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(message);
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public Message deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)) {
            return (Message) objectInputStream.readObject();
        }
    }

}

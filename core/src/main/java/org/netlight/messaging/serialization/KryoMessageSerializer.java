package org.netlight.messaging.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.netlight.messaging.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author ahmad
 */
public final class KryoMessageSerializer implements BinaryMessageSerializer {

    private static final KryoFactory KRYO_FACTORY = () -> {
        Kryo kryo = new Kryo();
        kryo.register(Message.class);
        return kryo;
    };
    private static final KryoPool KRYO_POOL = new KryoPool.Builder(KRYO_FACTORY).softReferences().build();

    @Override
    public byte[] serialize(Message message) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            writeMessage(output, message);
            output.flush();
            return byteArrayOutputStream.toByteArray();
        }
    }

    @Override
    public Message deserialize(byte[] bytes) throws IOException {
        try (Input input = new Input(bytes)) {
            return readMessage(input);
        }
    }

    private static void writeMessage(Output output, Message message) {
        Kryo kryo = KRYO_POOL.borrow();
        try {
            kryo.writeObject(output, message);
        } finally {
            KRYO_POOL.release(kryo);
        }
    }

    private static Message readMessage(Input input) {
        Kryo kryo = KRYO_POOL.borrow();
        try {
            return kryo.readObject(input, Message.class);
        } finally {
            KRYO_POOL.release(kryo);
        }
    }

}
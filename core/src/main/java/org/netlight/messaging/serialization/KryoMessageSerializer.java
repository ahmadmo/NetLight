package org.netlight.messaging.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import org.netlight.messaging.Message;
import org.netlight.util.MapEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ahmad
 */
public final class KryoMessageSerializer implements BinaryMessageSerializer {

    private static final KryoFactory DEFAULT_KRYO_FACTORY = () -> {
        Kryo kryo = new Kryo();
        kryo.register(Message.class);
        return kryo;
    };

    private final KryoPool kryoPool;

    public KryoMessageSerializer() {
        this(DEFAULT_KRYO_FACTORY);
    }

    public KryoMessageSerializer(KryoFactory kryoFactory) {
        kryoPool = new KryoPool.Builder(kryoFactory).softReferences().build();
    }

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

    private void writeMessage(Output output, Message message) {
        final Kryo kryo = kryoPool.borrow();
        try {
            kryo.writeObject(output, message);
        } finally {
            kryoPool.release(kryo);
        }
    }

    private Message readMessage(Input input) {
        final Kryo kryo = kryoPool.borrow();
        try {
            return kryo.readObject(input, Message.class);
        } finally {
            kryoPool.release(kryo);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final List<Class> classes = new ArrayList<>();
        private final Map<Integer, Class> classIdMap = new HashMap<>();
        private final List<MapEntry<Class, Serializer>> classSerializers = new ArrayList<>();
        private final Map<Integer, MapEntry<Class, Serializer>> classSerializerIdMap = new HashMap<>();

        public <S> Builder register(Class<S> type) {
            classes.add(type);
            return this;
        }

        public <S> Builder register(Class<S> type, int id) {
            classIdMap.put(id, type);
            return this;
        }

        public <S> Builder register(Class<S> type, Serializer<S> serializer) {
            classSerializers.add(new MapEntry<>(type, serializer));
            return this;
        }

        public <S> Builder register(Class<S> type, Serializer<S> serializer, int id) {
            classSerializerIdMap.put(id, new MapEntry<>(type, serializer));
            return this;
        }

        public KryoMessageSerializer build() {
            return new KryoMessageSerializer(() -> {
                Kryo kryo = DEFAULT_KRYO_FACTORY.create();
                classes.forEach(kryo::register);
                classIdMap.forEach((id, type) -> kryo.register(type, id));
                classSerializers.forEach(e -> kryo.register(e.getKey(), e.getValue()));
                classSerializerIdMap.forEach((id, e) -> kryo.register(e.getKey(), e.getValue(), id));
                return kryo;
            });
        }

    }

}
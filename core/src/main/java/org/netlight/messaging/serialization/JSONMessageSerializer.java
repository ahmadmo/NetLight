package org.netlight.messaging.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.netlight.messaging.Message;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author ahmad
 */
public final class JSONMessageSerializer implements TextMessageSerializer {

    private final ObjectWriter writer;
    private final ObjectReader reader;
    private final Charset charset;

    public JSONMessageSerializer() {
        this(StandardCharsets.UTF_8);
    }

    public JSONMessageSerializer(Charset charset) {
        Objects.requireNonNull(charset);
        this.charset = charset;
        final ObjectMapper mapper = new ObjectMapper();
        writer = mapper.writerFor(Message.class);
        reader = mapper.readerFor(Message.class);
    }

    @Override
    public String serialize(Message message) throws Exception {
        return writer.writeValueAsString(message);
    }

    @Override
    public Message deserialize(String t) throws Exception {
        return reader.readValue(t);
    }

    @Override
    public Charset charset() {
        return charset;
    }

}

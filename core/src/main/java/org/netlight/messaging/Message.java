package org.netlight.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.netlight.util.CommonUtils;

import java.util.*;

import static org.netlight.util.CommonUtils.notNull;

/**
 * @author ahmad
 */
@JsonIgnoreProperties(ignoreUnknown = true)
//@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
public class Message extends HashMap<String, Object> {

    private static final long serialVersionUID = 1045967313327361741L;

    public Message() {
        super();
    }

    public Message(Map<String, Object> map) {
        super(map);
    }

    public <T> T get(String key, Class<T> type) {
        return CommonUtils.castOrDefault(get(key), type, null);
    }

    public String getString(String key) {
        return get(key, String.class);
    }

    public Integer getInt(String key) {
        return get(key, Integer.class);
    }

    public Long getLong(String key) {
        return get(key, Long.class);
    }

    public Float getFloat(String key) {
        return get(key, Float.class);
    }

    public Double getDouble(String key) {
        return get(key, Double.class);
    }

    public Number getNumber(String key) {
        return get(key, Number.class);
    }

    public Date getDate(String key) {
        Object o = get(key);
        return o == null ? null
                : o instanceof Date ? (Date) o
                : o instanceof Long ? new Date(Long.parseLong(String.valueOf(o)))
                : o instanceof Integer ? new Date(Integer.parseInt(String.valueOf(o)) * 1000L) //unix-time
                : null;
    }

    public Object[] getArray(String key) {
        return get(key, Object[].class);
    }

    public List getList(String key) {
        return get(key, List.class);
    }

    public Map getMap(String key) {
        return get(key, Map.class);
    }

    public boolean has(String key, Class type) {
        return get(key, type) != null;
    }

    public boolean hasDate(String key) {
        return getDate(key) != null;
    }

    public boolean hasArray(String key) {
        Object[] array = getArray(key);
        return array != null && array.length != 0;
    }

    public boolean hasList(String key) {
        List list = getList(key);
        return list != null && !list.isEmpty();
    }

    public boolean hasMap(String key) {
        Map map = getMap(key);
        return map != null && !map.isEmpty();
    }

    @SuppressWarnings("unchecked")
    public static Message toMessage(Object o) {
        return o != null && o instanceof Map ? new Message((Map<String, Object>) o) : null;
    }

    public MessageBuilder builder(){
        return newBuilder(this);
    }

    public static MessageBuilder newBuilder() {
        return newBuilder(new Message());
    }

    public static MessageBuilder newBuilder(Message message) {
        return new MessageBuilder(message);
    }

    public static final class MessageBuilder {

        private final Message message;

        public MessageBuilder(Message message) {
            Objects.requireNonNull(message);
            this.message = message;
        }

        public <T> MessageBuilder put(String name, T value) {
            if (notNull(value)) {
                Objects.requireNonNull(name);
                message.put(name, value);
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> MessageBuilder putToList(String name, T value) {
            if (notNull(value)) {
                Objects.requireNonNull(name);
                List<Object> list = message.getList(name);
                if (list == null) {
                    message.put(name, list = new ArrayList<>());
                }
                list.add(value);
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> MessageBuilder putToList(String name, T... values) {
            if (values.length > 0) {
                Objects.requireNonNull(name);
                List<Object> list = message.getList(name);
                if (list == null) {
                    message.put(name, list = new ArrayList<>());
                }
                Collections.addAll(list, values);
            }
            return this;
        }

        public boolean isEmpty() {
            return message.isEmpty();
        }

        public boolean has(String name) {
            return notNull(message.get(name));
        }

        public boolean hasList(String name) {
            return notNull(message.getList(name));
        }

        public Message build() {
            return message;
        }

        @Override
        public String toString() {
            return message.toString();
        }

    }

}

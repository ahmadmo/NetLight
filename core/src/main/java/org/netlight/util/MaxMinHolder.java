package org.netlight.util;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;

/**
 * @author ahmad
 */
public final class MaxMinHolder<K, V> {

    private final Comparator<? super K> comparator;

    private Entry<K, V> max;
    private Entry<K, V> min;

    public MaxMinHolder() {
        comparator = null;
    }

    public MaxMinHolder(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    public MaxMinHolder<K, V> in(K key, V value) {
        if (max == null) {
            compare(key, key);
            max = min = new Entry<>(key, value);
        } else {
            if (compare(key, max.key) > 0) {
                max = new Entry<>(key, value);
            }
            if (compare(key, min.key) < 0) {
                min = new Entry<>(key, value);
            }
        }
        return this;
    }

    public Map.Entry<K, V> getMax() {
        return max;
    }

    public Map.Entry<K, V> getMin() {
        return min;
    }

    @SuppressWarnings("unchecked")
    private int compare(K a, K b) {
        return comparator == null ? ((Comparable<? super K>) a).compareTo(b) : comparator.compare(a, b);
    }

    private static final class Entry<K, V> implements Map.Entry<K, V> {

        private final K key;
        private V value;

        private Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V newValue) {
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        @Override
        public final int hashCode() {
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @Override
        public final boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                if (Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public final String toString() {
            return key + "=" + value;
        }

    }

}

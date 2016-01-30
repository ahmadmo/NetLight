package org.netlight.util;

import java.util.Comparator;

/**
 * @author ahmad
 */
public final class MaxMinHolder<K, V> {

    private final Comparator<? super K> comparator;

    private MapEntry<K, V> max;
    private MapEntry<K, V> min;

    public MaxMinHolder() {
        comparator = null;
    }

    public MaxMinHolder(Comparator<? super K> comparator) {
        this.comparator = comparator;
    }

    public MaxMinHolder<K, V> in(K key, V value) {
        if (max == null) {
            compare(key, key);
            max = min = new MapEntry<>(key, value);
        } else {
            if (compare(key, max.getKey()) > 0) {
                max = new MapEntry<>(key, value);
            }
            if (compare(key, min.getKey()) < 0) {
                min = new MapEntry<>(key, value);
            }
        }
        return this;
    }

    public MapEntry<K, V> getMax() {
        return max;
    }

    public MapEntry<K, V> getMin() {
        return min;
    }

    @SuppressWarnings("unchecked")
    private int compare(K a, K b) {
        return comparator == null ? ((Comparable<? super K>) a).compareTo(b) : comparator.compare(a, b);
    }

}

package org.netlight.util;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.function.*;

/**
 * @author ahmad
 */
public final class CommonUtils {

    private CommonUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T castOrDefault(Object o, Class<T> type, T def) {
        return o == null || !type.isInstance(o) ? def : (T) o;
    }

    public static <T> int compareNotNull(Comparable<T> a, T b) {
        return a == null ? b == null ? 0 : -1 : b == null ? 1 : a.compareTo(b);
    }

    public static <T> void computeIf(T t, Predicate<T> predicate, Consumer<T> consumer) {
        if (predicate.test(t)) {
            consumer.accept(t);
        }
    }

    public static <T> void computeIf(T t, Predicate<T> predicate, Consumer<T> consumer, Consumer<T> def) {
        if (predicate.test(t)) {
            consumer.accept(t);
        } else {
            def.accept(t);
        }
    }

    public static <T, R> R computeIf(T t, Predicate<T> predicate, Function<T, R> function) {
        return predicate.test(t) ? function.apply(t) : null;
    }

    public static <T, R> R computeIf(T t, Predicate<T> predicate, Function<T, R> function, R def) {
        return predicate.test(t) ? function.apply(t) : def;
    }

    public static <T, R> R computeIf(T t, Predicate<T> predicate, Function<T, R> function, Supplier<R> def) {
        return predicate.test(t) ? function.apply(t) : def.get();
    }

    public static <T, R> R computeIf(T t, Predicate<T> predicate, Function<T, R> function, Function<T, R> def) {
        return predicate.test(t) ? function.apply(t) : def.apply(t);
    }

    public static <T, U> void computeIf(T t, U u, Predicate<T> predicate, BiConsumer<T, U> consumer) {
        if (predicate.test(t)) {
            consumer.accept(t, u);
        }
    }

    public static <T, U> void computeIf(T t, U u, Predicate<T> predicate, BiConsumer<T, U> consumer, BiConsumer<T, U> def) {
        if (predicate.test(t)) {
            consumer.accept(t, u);
        } else {
            def.accept(t, u);
        }
    }

    public static <T, U, R> R computeIf(T t, U u, Predicate<T> predicate, BiFunction<T, U, R> function) {
        return predicate.test(t) ? function.apply(t, u) : null;
    }

    public static <T, U, R> R computeIf(T t, U u, Predicate<T> predicate, BiFunction<T, U, R> function, R def) {
        return predicate.test(t) ? function.apply(t, u) : def;
    }

    public static <T, U, R> R computeIf(T t, U u, Predicate<T> predicate, BiFunction<T, U, R> function, Supplier<R> def) {
        return predicate.test(t) ? function.apply(t, u) : def.get();
    }

    public static <T, U, R> R computeIf(T t, U u, Predicate<T> predicate, BiFunction<T, U, R> function, Function<U, R> def) {
        return predicate.test(t) ? function.apply(t, u) : def.apply(u);
    }

    public static <T, U, R> R computeIf(T t, U u, Predicate<T> predicate, BiFunction<T, U, R> function, BiFunction<T, U, R> def) {
        return predicate.test(t) ? function.apply(t, u) : def.apply(t, u);
    }

    public static <T, U> void computeIf(T t, U u, BiPredicate<T, U> predicate, BiConsumer<T, U> consumer) {
        if (predicate.test(t, u)) {
            consumer.accept(t, u);
        }
    }

    public static <T, U, R> R computeIf(T t, U u, BiPredicate<T, U> predicate, BiFunction<T, U, R> function) {
        return predicate.test(t, u) ? function.apply(t, u) : null;
    }

    public static <T, U, R> R computeIf(T t, U u, BiPredicate<T, U> predicate, BiFunction<T, U, R> function, R def) {
        return predicate.test(t, u) ? function.apply(t, u) : def;
    }

    public static <T, U, R> R computeIf(T t, U u, BiPredicate<T, U> predicate, BiFunction<T, U, R> function, Supplier<R> def) {
        return predicate.test(t, u) ? function.apply(t, u) : def.get();
    }

    public static <T, U, R> R computeIf(T t, U u, BiPredicate<T, U> predicate, BiFunction<T, U, R> function, Function<U, R> def) {
        return predicate.test(t, u) ? function.apply(t, u) : def.apply(u);
    }

    public static <T, U, R> R computeIf(T t, U u, BiPredicate<T, U> predicate, BiFunction<T, U, R> function, BiFunction<T, U, R> def) {
        return predicate.test(t, u) ? function.apply(t, u) : def.apply(t, u);
    }

    public static <T> void forEachIf(T[] array, Predicate<T> predicate, Consumer<T> consumer) {
        for (T t : array) {
            if (predicate.test(t)) {
                consumer.accept(t);
            }
        }
    }

    @SafeVarargs
    public static <T> void forEachIf(Predicate<T> predicate, Consumer<T> consumer, T... array) {
        for (T t : array) {
            if (predicate.test(t)) {
                consumer.accept(t);
            }
        }
    }

    public static <T> void forEachIf(Collection<T> collection, Predicate<T> predicate, Consumer<T> consumer) {
        collection.stream().filter(predicate).forEach(consumer);
    }

    public static <K, V> void forEachIf(Map<K, V> map, Predicate<K> predicate, BiConsumer<K, V> consumer) {
        map.forEach((k, v) -> {
            if (predicate.test(k)) {
                consumer.accept(k, v);
            }
        });
    }

    public static <K, V> void forEachIf(Map<K, V> map, BiConsumer<K, V> consumer, BiPredicate<K, V> predicate) {
        map.forEach((k, v) -> {
            if (predicate.test(k, v)) {
                consumer.accept(k, v);
            }
        });
    }

    public static <T> T getOrDefault(T t, T def) {
        return t == null ? def : t;
    }

    public static <T> T getOrDefault(T t, Optional<T> defOptional) {
        return t != null ? t : defOptional.isPresent() ? defOptional.get() : null;
    }

    public static <T> T getOrDefault(Optional<T> optional, T def) {
        return optional.isPresent() ? optional.get() : def;
    }

    public static <T> T getOrDefault(Optional<T> optional, Optional<T> defOptional) {
        return optional.isPresent() ? optional.get() : defOptional.isPresent() ? defOptional.get() : null;
    }

    public static <T> T getOrDefault(T t, Supplier<T> defSupplier) {
        return t == null ? defSupplier.get() : t;
    }

    public static <T> T getOrDefault(Supplier<T> supplier, T def) {
        T t = null;
        try {
            t = supplier.get();
        } catch (NullPointerException ignored) {
        }
        return t == null ? def : t;
    }

    public static <T> T getOrDefault(Supplier<T> supplier, Supplier<T> defSupplier) {
        T t = null;
        try {
            t = supplier.get();
        } catch (NullPointerException ignored) {
        }
        return t == null ? defSupplier.get() : t;
    }

    public static <K, V> V getOrPutConcurrent(ConcurrentMap<K, V> map, K key, Supplier<V> valueSupplier) {
        V value = map.get(key);
        if (value == null) {
            final V v = map.putIfAbsent(key, value = valueSupplier.get());
            if (v != null) {
                value = v;
            }
        }
        return value;
    }

    public static <K, V> V getOrPutConcurrent(ConcurrentMap<K, V> map, K key, Supplier<V> valueSupplier, Consumer<V> actionIfAbsent) {
        V value = map.get(key);
        if (value == null) {
            final V v = map.putIfAbsent(key, value = valueSupplier.get());
            if (v != null) {
                value = v;
            } else {
                actionIfAbsent.accept(value);
            }
        }
        return value;
    }

    public static <T> boolean isNull(T t) {
        return t == null || (t instanceof String ? isNull0((String) t)
                : t instanceof Object[] ? isNull0(((Object[]) t))
                : t instanceof Collection ? isNull0((Collection) t)
                : t instanceof Map && isNull0((Map) t));
    }

    private static boolean isNull0(String s) {
        return s == null || (s = s.trim()).isEmpty() || s.equalsIgnoreCase("null");
    }

    private static boolean isNull0(Object[] array) {
        return array == null || array.length == 0;
    }

    private static boolean isNull0(Collection collection) {
        return collection == null || collection.isEmpty();
    }

    private static boolean isNull0(Map map) {
        return map == null || map.isEmpty();
    }

    public static <T> boolean notNull(T t) {
        return !isNull(t);
    }

    public static <T> T notNull(T a, T b) {
        return isNull(a) ? b : a;
    }

    public static <T> T notNull(T t, Supplier<T> def) {
        return isNull(t) ? def.get() : t;
    }

    public static <T> Predicate<T> notNull() {
        return CommonUtils::notNull;
    }

}

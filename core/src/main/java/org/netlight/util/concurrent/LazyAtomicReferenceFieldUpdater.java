package org.netlight.util.concurrent;

import org.netlight.util.TimeProperty;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author ahmad
 */
public final class LazyAtomicReferenceFieldUpdater<V> {

    private final AtomicReferenceField<V> value;
    private final Function<V, V> updaterFunction;
    private final AtomicLongField updateInterval;
    private final UpdatePolicy policy;
    private final AtomicLongField lastSet;
    private final AtomicLongField lastUpdate;

    public LazyAtomicReferenceFieldUpdater(V value, Function<V, V> updaterFunction, TimeProperty updateInterval) {
        this(value, updaterFunction, updateInterval, UpdatePolicy.AFTER_LAST_SET);
    }

    public LazyAtomicReferenceFieldUpdater(V value, Function<V, V> updaterFunction, TimeProperty updateInterval, UpdatePolicy policy) {
        Objects.requireNonNull(updaterFunction);
        Objects.requireNonNull(updateInterval);
        Objects.requireNonNull(policy);
        this.value = new AtomicReferenceField<>(value);
        this.updaterFunction = updaterFunction;
        this.updateInterval = new AtomicLongField(updateInterval.convert(TimeUnit.MILLISECONDS));
        this.policy = policy;
        final long now = System.currentTimeMillis();
        lastSet = new AtomicLongField(now);
        lastUpdate = new AtomicLongField(now);
    }

    public V get() {
        update();
        return value.get();
    }

    private void update() {
        long n = (System.currentTimeMillis() - (policy == UpdatePolicy.AFTER_LAST_UPDATE ? lastUpdate.get() : lastSet.get()))
                / updateInterval.get();
        if (n > 0) {
            final V e = value.get();
            V u = e;
            for (; n > 0; n--) {
                u = updaterFunction.apply(u);
            }
            compareAndSet0(e, u);
            lastUpdate.set(System.currentTimeMillis());
        }
    }

    private boolean compareAndSet0(V expect, V update) {
        boolean modified = value.compareAndSet(expect, update);
        if (modified) {
            lastSet.set(System.currentTimeMillis());
        }
        return modified;
    }

    public void set(V value) {
        this.value.set(value);
        lastSet.set(System.currentTimeMillis());
    }

    public boolean compareAndSet(V expect, V update) {
        update();
        return compareAndSet0(expect, update);
    }

    public V getAndSet(V value) {
        final V v = get();
        set(value);
        return v;
    }

    public boolean isPresent() {
        return get() != null;
    }

    public void setUpdateInterval(TimeProperty interval) {
        updateInterval.set(interval.convert(TimeUnit.MILLISECONDS));
    }

    public TimeProperty getUpdateInterval() {
        return new TimeProperty(updateInterval.get(), TimeUnit.MICROSECONDS);
    }

}

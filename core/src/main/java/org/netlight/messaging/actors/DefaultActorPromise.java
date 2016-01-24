package org.netlight.messaging.actors;

import org.netlight.util.concurrent.AtomicBooleanField;
import org.netlight.util.concurrent.AtomicReferenceField;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author ahmad
 */
final class DefaultActorPromise implements ActorPromise {

    private final Actor actor;
    private final AtomicBooleanField done = new AtomicBooleanField();
    private final AtomicReferenceField<Throwable> cause = new AtomicReferenceField<>();
    private final List<ActorFutureListener> listeners = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = lock.readLock();
    private final Lock w = lock.readLock();

    DefaultActorPromise(Actor actor) {
        Objects.requireNonNull(actor);
        this.actor = actor;
    }

    @Override
    public Actor actor() {
        return actor;
    }

    @Override
    public boolean isDone() {
        return done.get();
    }

    @Override
    public Throwable cause() {
        return cause.get();
    }

    @Override
    public boolean isCompletedExceptionally() {
        return cause.isPresent();
    }

    @Override
    public ActorPromise complete() {
        if (done.compareAndSet(false, true)) {
            fireOnComplete();
        }
        return this;
    }

    @Override
    public ActorPromise setFailure(Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("cause");
        }
        if (done.compareAndSet(false, true)) {
            this.cause.set(cause);
            fireOnComplete();
        }
        return this;
    }

    @Override
    public ActorPromise addListener(ActorFutureListener listener) {
        w.lock();
        try {
            listeners.add(listener);
            if (isDone()) {
                listener.onComplete(this);
            }
        } finally {
            w.unlock();
        }
        return this;
    }

    @Override
    public ActorPromise removeListener(ActorFutureListener listener) {
        w.lock();
        try {
            listeners.remove(listener);
        } finally {
            w.unlock();
        }
        return this;
    }

    private void fireOnComplete() {
        r.lock();
        try {
            for (ActorFutureListener listener : listeners) {
                listener.onComplete(this);
            }
        } finally {
            r.unlock();
        }
    }

}

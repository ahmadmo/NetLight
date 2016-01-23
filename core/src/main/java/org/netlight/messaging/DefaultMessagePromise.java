package org.netlight.messaging;

import org.netlight.util.concurrent.AtomicBooleanField;
import org.netlight.util.concurrent.AtomicReferenceField;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author ahmad
 */
public final class DefaultMessagePromise implements MessagePromise {

    private final Message message;
    private final SocketAddress remoteAddress;
    private final AtomicBooleanField done = new AtomicBooleanField();
    private final AtomicBooleanField success = new AtomicBooleanField();
    private final AtomicBooleanField cancellable = new AtomicBooleanField();
    private final AtomicBooleanField cancelled = new AtomicBooleanField();
    private final AtomicReferenceField<Throwable> cause = new AtomicReferenceField<>();
    private final AtomicReferenceField<Message> response = new AtomicReferenceField<>();
    private final List<MessageFutureListener> listeners = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock r = lock.readLock();
    private final Lock w = lock.readLock();

    public DefaultMessagePromise(Message message, SocketAddress remoteAddress) {
        Objects.requireNonNull(message);
        Objects.requireNonNull(remoteAddress);
        this.message = message;
        this.remoteAddress = remoteAddress;
    }

    @Override
    public Message message() {
        return message;
    }

    @Override
    public SocketAddress remoteAddress() {
        return remoteAddress;
    }

    @Override
    public boolean isDone() {
        return done.get();
    }

    @Override
    public boolean isSuccess() {
        return success.get();
    }

    @Override
    public boolean isCancellable() {
        return cancellable.get();
    }

    @Override
    public void cancel() {
        if (cancellable.get() && done.compareAndSet(false, true)) {
            cancelled.set(true);
            fireOnComplete();
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled.get();
    }

    @Override
    public Throwable cause() {
        return cause.get();
    }

    @Override
    public boolean hasResponse() {
        return response.get() != null;
    }

    @Override
    public Message getResponse() {
        return response.get();
    }

    @Override
    public DefaultMessagePromise setSuccess() {
        setSuccess(true);
        return this;
    }

    @Override
    public DefaultMessagePromise setSuccess(boolean success) {
        if (done.compareAndSet(false, true)) {
            this.success.set(success);
            fireOnComplete();
        }
        return this;
    }

    @Override
    public DefaultMessagePromise setCancellable(boolean cancellable) {
        if (!done.get()) {
            this.cancellable.set(cancellable);
            if (!cancellable) {
                cancelled.set(false);
            }
        }
        return this;
    }

    @Override
    public DefaultMessagePromise setFailure(Throwable cause) {
        if (cause == null) {
            throw new NullPointerException("cause");
        }
        if (done.compareAndSet(false, true)) {
            success.set(false);
            this.cause.set(cause);
            fireOnComplete();
        }
        return this;
    }

    @Override
    public DefaultMessagePromise setResponse(Message response) {
        if (response == null) {
            throw new NullPointerException("response");
        }
        if (this.response.compareAndSet(null, response)) {
            setSuccess(true);
            fireOnResponse();
        }
        return this;
    }

    @Override
    public MessagePromise addListener(MessageFutureListener listener) {
        w.lock();
        try {
            listeners.add(listener);
            if (isDone()) {
                listener.onComplete(this);
            }
            if (hasResponse()) {
                listener.onResponse(this, response.get());
            }
        } finally {
            w.unlock();
        }
        return this;
    }

    @Override
    public MessagePromise removeListener(MessageFutureListener listener) {
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
            for (MessageFutureListener listener : listeners) {
                listener.onComplete(this);
            }
        } finally {
            r.unlock();
        }
    }

    private void fireOnResponse() {
        final Message resp = response.get();
        r.lock();
        try {
            for (MessageFutureListener listener : listeners) {
                listener.onResponse(this, resp);
            }
        } finally {
            r.unlock();
        }
    }

}

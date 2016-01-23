package org.netlight.messaging;

import java.net.SocketAddress;
import java.util.Objects;

/**
 * @author ahmad
 */
public final class VoidMessagePromise implements MessagePromise {

    private final Message message;
    private final SocketAddress remoteAddress;

    public VoidMessagePromise(Message message, SocketAddress remoteAddress) {
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
        return false;
    }

    @Override
    public boolean isSuccess() {
        return false;
    }

    @Override
    public boolean isCancellable() {
        return false;
    }

    @Override
    public void cancel() {
        fail();
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public Throwable cause() {
        return null;
    }

    @Override
    public boolean hasResponse() {
        return false;
    }

    @Override
    public Message getResponse() {
        return null;
    }

    @Override
    public VoidMessagePromise setSuccess() {
        return this;
    }

    @Override
    public VoidMessagePromise setSuccess(boolean success) {
        return this;
    }

    @Override
    public VoidMessagePromise setCancellable(boolean cancellable) {
        return this;
    }

    @Override
    public VoidMessagePromise setFailure(Throwable cause) {
        return this;
    }

    @Override
    public VoidMessagePromise setResponse(Message response) {
        return this;
    }

    @Override
    public MessagePromise addListener(MessageFutureListener listener) {
        fail();
        return this;
    }

    @Override
    public MessagePromise removeListener(MessageFutureListener listener) {
        fail();
        return this;
    }

    private static void fail() {
        throw new IllegalStateException("void future");
    }

}

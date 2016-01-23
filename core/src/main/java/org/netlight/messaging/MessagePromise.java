package org.netlight.messaging;

import io.netty.util.concurrent.Future;

/**
 * @author ahmad
 */
public interface MessagePromise extends MessageFuture {

    MessagePromise setSuccess();

    MessagePromise setSuccess(boolean success);

    MessagePromise setCancellable(boolean cancellable);

    MessagePromise setFailure(Throwable cause);

    MessagePromise setResponse(Message response);

    default void complete(Future<? super Void> future) {
        if (future.isSuccess()) {
            setSuccess();
        } else {
            setFailure(future.cause());
        }
    }

    @Override
    MessagePromise addListener(MessageFutureListener listener);

    @Override
    MessagePromise removeListener(MessageFutureListener listener);

}

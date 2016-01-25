package org.netlight.messaging.actors;

import org.netlight.channel.ChannelContext;
import org.netlight.messaging.*;
import org.netlight.util.TimeProperty;
import org.netlight.util.concurrent.AtomicBooleanField;
import org.netlight.util.concurrent.AtomicIntegerField;
import org.netlight.util.concurrent.AtomicLongField;
import org.netlight.util.concurrent.AtomicReferenceField;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ahmad
 */
final class DefaultActor implements Actor {

    private static final AtomicLongField ACTOR_ID = new AtomicLongField();
    private static final TimeProperty IDLE_TIMEOUT = TimeProperty.seconds(30L);

    private final long id = ACTOR_ID.getAndIncrement();

    private final MessageListener listener;
    private final Queue<RichMessage> mailbox = new ConcurrentQueue<>();
    private final AtomicIntegerField load = new AtomicIntegerField();
    private final AtomicReferenceField<Lock> lock = new AtomicReferenceField<>();

    DefaultActor(MessageListener listener) {
        Objects.requireNonNull(listener);
        this.listener = listener;
    }

    @Override
    public long id() {
        return id;
    }

    @Override
    public void tell(Message message, ChannelContext ctx, int weight) {
        if (mailbox.add(new RichMessage(message, ctx, System.currentTimeMillis(), weight))) {
            load.getAndAdd(weight);
        }
    }

    @Override
    public int load() {
        return load.get();
    }

    @Override
    public Lock lock() {
        lock.compareAndSet(null, new ReentrantLock());
        return lock.get();
    }

    @Override
    public RunnableActor makeRunnable() {
        return new OneTimeRunnableActor();
    }

    private final class OneTimeRunnableActor implements RunnableActor {

        private final ActorPromise promise;
        private final AtomicReferenceField<RunnableActorState> state = new AtomicReferenceField<>(RunnableActorState.IN_ACTIVE);
        private final AtomicBooleanField firstRun = new AtomicBooleanField(true);

        private OneTimeRunnableActor() {
            promise = new DefaultActorPromise(DefaultActor.this);
        }

        @Override
        public ActorFuture future() {
            return promise;
        }

        @Override
        public RunnableActorState state() {
            return state.get();
        }

        @Override
        public boolean isFirstRun() {
            return firstRun.get();
        }

        @Override
        public void run() {
            if (!firstRun.compareAndSet(true, false)) {
                throw new IllegalStateException("This Runnable can be run only once.");
            }
            if (state.compareAndSet(RunnableActorState.IN_ACTIVE, RunnableActorState.ACTIVE)) {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        RichMessage richMessage;
                        if ((richMessage = mailbox.poll()) == null) {
                            state.set(RunnableActorState.IDLE);
                            if ((richMessage = mailbox.poll(IDLE_TIMEOUT)) == null) {
                                break;
                            }
                            state.set(RunnableActorState.ACTIVE);
                        }
                        load.getAndAdd(-richMessage.getWeight());
                        listener.onMessage(richMessage.getMessage(), richMessage.getChannelContext());
                    }
                } catch (Throwable cause) {
                    promise.setFailure(cause);
                } finally {
                    state.set(RunnableActorState.IN_ACTIVE);
                    promise.complete();
                }
            }
        }

    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj instanceof DefaultActor && id == ((DefaultActor) obj).id;
    }

}

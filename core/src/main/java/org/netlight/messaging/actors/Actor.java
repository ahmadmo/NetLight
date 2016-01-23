package org.netlight.messaging.actors;

import org.netlight.channel.ChannelContext;
import org.netlight.messaging.*;
import org.netlight.util.TimeProperty;
import org.netlight.util.concurrent.AtomicLongField;

/**
 * @author ahmad
 */
public final class Actor {

    private static final AtomicLongField ACTOR_ID = new AtomicLongField();

    private final long id = ACTOR_ID.getAndIncrement();
    private final MessageListener listener;
    private final MessageQueue<RichMessage> mailbox = new ConcurrentMessageQueue<>();

    public Actor(MessageListener listener) {
        this.listener = listener;
    }

    public long id() {
        return id;
    }

    public void tell(ChannelContext ctx, Message message) {
        if (message instanceof RichMessage) {
            mailbox.add((RichMessage) message);
        } else {
            mailbox.add(new RichMessage(message, ctx, System.currentTimeMillis()));
        }
    }

    public int load() {
        return mailbox.size();
    }

    public boolean handleNext() {
        return handleNext(null);
    }

    public boolean handleNext(TimeProperty timeout) {
        RichMessage message = timeout == null ? mailbox.take() : mailbox.poll(timeout);
        if (message == null) {
            return false;
        }
        listener.onMessage(message);
        return true;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj instanceof Actor && id == ((Actor) obj).id;
    }

}

package org.netlight.messaging;

import org.netlight.channel.ChannelContext;

/**
 * @author ahmad
 */
public final class RichMessage {

    private final Message message;
    private final ChannelContext channelContext;
    private final long timeStamp;
    private final int weight;

    public RichMessage(Message message, ChannelContext channelContext, long timeStamp, int weight) {
        this.message = message;
        this.channelContext = channelContext;
        this.timeStamp = timeStamp;
        this.weight = weight;
    }

    public Message getMessage() {
        return message;
    }

    public ChannelContext getChannelContext() {
        return channelContext;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "{" +
                "message=" + message +
                ", channelContext=" + channelContext +
                ", timeStamp=" + timeStamp +
                ", weight=" + weight +
                '}';
    }

}

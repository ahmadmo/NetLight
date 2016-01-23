package org.netlight.messaging;

import org.netlight.channel.ChannelContext;

import java.util.Map;

/**
 * @author ahmad
 */
public final class RichMessage extends Message implements Comparable<RichMessage> {

    private final ChannelContext channelContext;
    private final long timeStamp;

    public RichMessage(ChannelContext channelContext, long timeStamp) {
        this.channelContext = channelContext;
        this.timeStamp = timeStamp;
    }

    public RichMessage(Map<String, Object> map, ChannelContext channelContext, long timeStamp) {
        super(map);
        this.channelContext = channelContext;
        this.timeStamp = timeStamp;
    }

    public ChannelContext getChannelContext() {
        return channelContext;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public int compareTo(RichMessage o) {
        return Long.compare(timeStamp, o.timeStamp);
    }

    @Override
    public String toString() {
        return "{" +
                "channelContext=" + channelContext +
                ", message=" + super.toString() +
                ", timeStamp=" + timeStamp +
                '}';
    }

}

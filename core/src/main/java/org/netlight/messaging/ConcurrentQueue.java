package org.netlight.messaging;

import org.netlight.util.CommonUtils;
import org.netlight.util.TimeProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author ahmad
 */
public final class ConcurrentQueue<M> implements Queue<M> {

    private final LinkedBlockingQueue<M> queue = new LinkedBlockingQueue<>();

    @Override
    public boolean add(M message) {
        return CommonUtils.notNull(message) && queue.offer(message);
    }

    @Override
    public boolean addAll(List<M> messages) {
        if (messages == null || messages.isEmpty()) {
            return false;
        }
        boolean modified = false;
        for (M message : messages) {
            modified |= add(message);
        }
        return modified;
    }

    @Override
    public M peek() {
        return queue.peek();
    }

    @Override
    public M poll() {
        return queue.poll();
    }

    @Override
    public M poll(TimeProperty timeout) {
        try {
            return queue.poll(timeout.getTime(), timeout.getUnit());
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public M take() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public Iterator<M> iterator() {
        return queue.iterator();
    }

    @Override
    public List<M> removeAll() {
        List<M> messages = new ArrayList<>();
        M message;
        while ((message = queue.poll()) != null) {
            messages.add(message);
        }
        return messages;
    }

}

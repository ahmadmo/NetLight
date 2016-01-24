package org.netlight.messaging;

import org.netlight.util.TimeProperty;

import java.util.Iterator;
import java.util.List;

/**
 * @author ahmad
 */
public interface Queue<M> {

    boolean add(M message);

    boolean addAll(List<M> messages);

    M peek();

    M poll();

    M poll(TimeProperty timeout);

    M take();

    int size();

    boolean isEmpty();

    Iterator<M> iterator();

    List<M> removeAll();

}

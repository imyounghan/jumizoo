/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.infrastructure;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class SingleEntryGate {
    private final static int NOT_ENTERED = 0;
    private final static int ENTERED = 1;

    private AtomicInteger atomicInteger = new AtomicInteger(NOT_ENTERED);

    // returns true if this is the first call to tryEnter(), false otherwise
    public boolean tryEnter()
    {
        return atomicInteger.compareAndSet(NOT_ENTERED, ENTERED);
    }
}

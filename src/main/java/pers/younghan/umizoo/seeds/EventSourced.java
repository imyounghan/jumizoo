/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.seeds;

import pers.younghan.umizoo.messaging.VersionedEvent;

import java.util.Collection;


/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface EventSourced extends AggregateRoot {
    int getVersion();

    Collection<VersionedEvent> getChanges();

    void loadFrom(Iterable<VersionedEvent> events);
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.seeds;

import pers.younghan.umizoo.messaging.Command;
import pers.younghan.umizoo.messaging.Envelope;


/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface Repository {
    <T extends AggregateRoot> T find(Class<T> aggregateRootType, String aggregateRootId);

    void save(AggregateRoot aggregateRoot);

    void save(EventSourced eventSourced, Envelope<Command> command);

    void delete(AggregateRoot aggregateRoot);
}

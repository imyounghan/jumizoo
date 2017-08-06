/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.seeds;

/**
 * Created by young.han with IntelliJ IDEA on 2017-08-01.
 */
public interface AggregateStorage {
    <T extends AggregateRoot> T get(Class<T> aggregateRootType, String aggregateRootId);

    void save(AggregateRoot aggregateRoot);

    void delete(AggregateRoot aggregateRoot);
}

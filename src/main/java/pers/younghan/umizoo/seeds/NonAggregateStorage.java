/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.seeds;

/**
 * Created by young.han with IntelliJ IDEA on 2017-08-01.
 */
public class NonAggregateStorage implements AggregateStorage {
    @Override
    public <T extends AggregateRoot> T get(Class<T> aggregateRootType, String aggregateRootId) {
        return null;
    }

    @Override
    public void save(AggregateRoot aggregateRoot) {

    }

    @Override
    public void delete(AggregateRoot aggregateRoot) {

    }
}

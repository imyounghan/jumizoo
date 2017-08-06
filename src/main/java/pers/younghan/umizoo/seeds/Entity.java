/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.seeds;

import pers.younghan.umizoo.infrastructure.UniquelyIdentifiable;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-30.
 */
public abstract class Entity<TIdentify> implements UniquelyIdentifiable {
    private TIdentify id;

    protected Entity() {
    }

    protected Entity(TIdentify id) {
        this.id = id;
    }

    public TIdentify getRawId() {
        return this.id;
    }

    @Override
    public String getId() {
        return this.id.toString();
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) return true;
        if(object == null || getClass() != object.getClass()) return false;

        Entity<?> entity = (Entity<?>)object;

        return id.equals(entity.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.infrastructure;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface Cache {
    <T> T get(Class<T> type, Object key);

    <T> void set(T object, Object key);

    <T> void remove(Class<T> type, Object key);
}

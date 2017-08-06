/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.infrastructure;

import com.google.common.base.Verify;

import java.util.HashMap;
import java.util.Objects;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class LocalCache implements Cache {

    HashMap<String, Object> map;

    public LocalCache() {
        this.map = new HashMap<>();
    }

    @Override
    public <T> T get(Class<T> type, Object key) {
        Verify.verifyNotNull(type);
        Verify.verifyNotNull(key);

        String cacheKey = buildCacheKey(type, key);

        Object value = map.get(cacheKey);

        if (Objects.isNull(value)) {
            return null;
        } else if (value.getClass().isAssignableFrom(type)) {
            return (T) value;
        } else {
            return null;
        }
    }

    @Override
    public <T> void set(T object, Object key) {
        Verify.verifyNotNull(key);

        String cacheKey = buildCacheKey(object.getClass(), key);
        map.put(cacheKey, object);
    }

    @Override
    public <T> void remove(Class<T> type, Object key) {
        Verify.verifyNotNull(type);
        Verify.verifyNotNull(key);

        String cacheKey = buildCacheKey(type, key);
        map.remove(cacheKey);
    }

    private static String buildCacheKey(Class type, Object key) {
        return String.format("Class:{0}:{1}", type.getName(), key);
    }
}

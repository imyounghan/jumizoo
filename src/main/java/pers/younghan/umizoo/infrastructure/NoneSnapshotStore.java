/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.infrastructure;

import pers.younghan.umizoo.messaging.SourceInfo;

import java.lang.reflect.Type;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-31.
 */
public class NoneSnapshotStore implements SnapshotStore {
    @Override
    public Object getLatest(Class sourceType, Object sourceId) {
        return null;
    }

    @Override
    public void delete(SourceInfo sourceInfo) {

    }
}

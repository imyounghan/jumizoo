/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.infrastructure;

import pers.younghan.umizoo.messaging.SourceInfo;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface SnapshotStore {

    Object getLatest(Class sourceType, Object sourceId);

    void delete(SourceInfo sourceInfo);
}

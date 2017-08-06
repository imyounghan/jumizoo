/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import java.util.Collection;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface EventStore {
    boolean save(SourceInfo sourceInfo, Collection<VersionedEvent> events, String correlationId);

    Collection<VersionedEvent> find(SourceInfo sourceInfo, String correlationId);

    Collection<VersionedEvent> findAll(SourceInfo sourceInfo, int startVersion);

    void delete(SourceInfo sourceInfo);
}

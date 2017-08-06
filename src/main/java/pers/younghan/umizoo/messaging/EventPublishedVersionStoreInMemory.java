/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class EventPublishedVersionStoreInMemory implements EventPublishedVersionStore {
    private ConcurrentMap<SourceInfo, Integer>[] versionCaches;

    public EventPublishedVersionStoreInMemory() {
        this(5);
    }

    protected EventPublishedVersionStoreInMemory(int mapCount) {
        this.versionCaches = new ConcurrentMap[mapCount];
        for(int index = 0; index < mapCount; index++) {
            versionCaches[index] = new ConcurrentHashMap<>();
        }
    }

    @Override
    public void addOrUpdatePublishedVersion(SourceInfo sourceInfo, int version) {
        versionCaches[Math.abs(sourceInfo.hashCode() % versionCaches.length)].computeIfPresent(sourceInfo, (key, value) -> version == value + 1 ? version : value);
    }

    @Override
    public int getPublishedVersion(SourceInfo sourceInfo) {
        return versionCaches[Math.abs(sourceInfo.hashCode() % versionCaches.length)].getOrDefault(sourceInfo, 0);
    }
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class EventStoreInMemory implements EventStore {

    private final ConcurrentMap<SourceInfo, HashSet<EventCollection>> collection;

    public EventStoreInMemory() {
        this.collection = new ConcurrentHashMap<>();
    }


    @Override
    public boolean save(SourceInfo sourceInfo, Collection<VersionedEvent> events, String correlationId) {
        return collection.computeIfAbsent(sourceInfo, key -> new HashSet<>()).add(new EventCollection(correlationId, events));
    }

    @Override
    public Collection<VersionedEvent> find(SourceInfo sourceInfo, String correlationId) {
        if(!collection.containsKey(sourceInfo)) {
            return Collections.EMPTY_LIST;
        }

        return collection.get(sourceInfo).stream().filter(item -> item.getCorrelationId().equals(correlationId)).findFirst().get();
    }

    @Override
    public Collection<VersionedEvent> findAll(SourceInfo sourceInfo, int startVersion) {
        if(!collection.containsKey(sourceInfo)) {
            return Collections.EMPTY_LIST;
        }

        return collection.get(sourceInfo).stream()
                .flatMap(item -> item.stream())
                .filter(item -> item.getVersion() > startVersion)
                .sorted(Comparator.comparingInt(VersionedEvent::getVersion))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(SourceInfo sourceInfo) {
        collection.remove(sourceInfo);
    }

    class EventCollection extends AbstractCollection<VersionedEvent> {
        private Collection<VersionedEvent> eventList;
        private String correlationId;

        public EventCollection(String correlationId, Collection<VersionedEvent> events) {
            this.correlationId = correlationId;
            this.eventList = new ArrayList<>(events);
        }


        @Override
        public Iterator<VersionedEvent> iterator() {
            return eventList.iterator();
        }

        @Override
        public int size() {
            return eventList.size();
        }

        @Override
        public int hashCode() {
            return this.correlationId.hashCode();
        }

        public String getCorrelationId() {
            return correlationId;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) {
                return true;
            }
            else if(!(obj instanceof EventCollection)) {
                return false;
            }
            else {
                EventCollection other = (EventCollection)obj;
                return this.correlationId.equals(other.correlationId);
            }
        }
    }
}

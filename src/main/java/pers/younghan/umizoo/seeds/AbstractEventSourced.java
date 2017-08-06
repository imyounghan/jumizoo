/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.seeds;


import pers.younghan.umizoo.messaging.VersionedEvent;
import pers.younghan.umizoo.messaging.VersionedEventBase;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-31.
 */
public abstract class AbstractEventSourced<TIdentify> extends AbstractAggregateRoot<TIdentify> implements EventSourced {
    private int version;

    protected AbstractEventSourced() {
    }

    protected AbstractEventSourced(TIdentify id) {
        super(id);
    }

    public int getVersion() {
        return this.version;
    }

    //@Override
    protected <TEvent extends VersionedEventBase> void raiseEvent(TEvent event) {
        event.setVersion(this.version + 1);
        if(!super.raiseEvent(event)) {
            throw new RuntimeException(String.format("Event Handler not found on '%s' for '%s'.", this.getClass(), event.getClass()));
        }
        this.version = event.getVersion();
    }

    @Override
    public Collection<VersionedEvent> getChanges() {
        return getEvents().stream().filter(event -> event instanceof VersionedEvent).map(event -> (VersionedEvent)event).collect(Collectors.toList());
    }

    @Override
    public void loadFrom(Iterable<VersionedEvent> events) {
        for(VersionedEvent event : events) {
            if(event.getVersion() != this.version + 1) {
                throw new RuntimeException(String.format("Cannot load because the version '{0}' is not equal to the AggregateRoot version '{1}' on '{2}' of id '{3}'.", event.getVersion(), this.version, this.getClass().getName(), this.getRawId()));
            }
            if(!super.raiseEvent(event)) {
                throw new RuntimeException(String.format("Event Handler not found on '{0}' for '{1}'.", this.getClass(), event.getClass()));
            }
            this.version = event.getVersion();
        }

        this.clearEvents();
    }
}

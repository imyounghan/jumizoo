/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.seeds;

import pers.younghan.umizoo.messaging.Event;
import pers.younghan.umizoo.messaging.EventPublisher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-30.
 */
public abstract class AbstractAggregateRoot<TIdentify> extends Entity<TIdentify> implements AggregateRoot, EventPublisher {

    private ArrayList<Event> pendingEvents;

    protected AbstractAggregateRoot() {
    }

    protected AbstractAggregateRoot(TIdentify id) {
        super(id);
    }

    protected <TEvent extends Event> boolean raiseEvent(TEvent event) {
        if(Objects.isNull(pendingEvents)) {
            pendingEvents = new ArrayList<>();
        }
        pendingEvents.add(event);

        return this.applyEvent(event);
    }

    private boolean applyEvent(Event event) {
        Class eventType = event.getClass();
        Class aggregateRootType = this.getClass();

        BiConsumer<AggregateRoot, Event> handler = AggregateInternalHandlerProvider.Instance.getEventHandler(aggregateRootType, eventType);

        if(handler != null) {
            handler.accept(this, event);
            return true;
        }

        return false;
    }


    protected void clearEvents() {
        if(Objects.isNull(pendingEvents) || pendingEvents.size() == 0) {
            return;
        }

        pendingEvents.clear();
    }

    @Override
    public Collection<Event> getEvents() {
        if(Objects.isNull(pendingEvents)) {
            return Collections.EMPTY_LIST;
        }

        return pendingEvents;
    }
}

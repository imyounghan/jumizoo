/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import pers.younghan.umizoo.seeds.AggregateRoot;
import pers.younghan.umizoo.seeds.EntityNotFoundException;
import pers.younghan.umizoo.seeds.EventSourced;

import java.util.Objects;
import java.util.function.Function;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface CommandContext {
    String getCommandId();

    void add(AggregateRoot aggregateRoot);

    default <TEventSourced extends EventSourced> TEventSourced get(Class<TEventSourced> type, Object id) throws EntityNotFoundException {
        TEventSourced eventSourced = this.find(type, id);
        if(Objects.isNull(eventSourced)) {
            throw new EntityNotFoundException(id, type);
        }

        return eventSourced;
    }

    <TAggregateRoot extends AggregateRoot> TAggregateRoot find(Class<TAggregateRoot> type, Object id);

    void complete(Object result, Function<Object, String> serializer);
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import pers.younghan.umizoo.infrastructure.StandardMetadata;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public interface QueryBus extends MessageBus<Query> {
    default void send(Query query, TraceInfo traceInfo){
        Envelope<Query> envelope = new Envelope<>(query, traceInfo.getId());
        envelope.items().put(StandardMetadata.TraceInfo, traceInfo);

        this.send(envelope);
    }
}

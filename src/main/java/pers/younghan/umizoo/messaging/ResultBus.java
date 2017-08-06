/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import pers.younghan.umizoo.infrastructure.StandardMetadata;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface ResultBus extends MessageBus<Result> {
    default void send(Result result, TraceInfo traceInfo){
        Envelope<Result> envelope = new Envelope<>(result, traceInfo.getId());
        envelope.items().put(StandardMetadata.TraceInfo, traceInfo);

        this.send(envelope);
    }

    default void send(Result result, String traceId){
        Envelope<Result> envelope = new Envelope<>(result,traceId);

        this.send(envelope);
    }
}

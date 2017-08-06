/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import pers.younghan.umizoo.common.ObjectId;
import pers.younghan.umizoo.infrastructure.StandardMetadata;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface CommandBus extends MessageBus<Command> {
    default void send(Command command, TraceInfo traceInfo) {
        Envelope<Command> envelope = new Envelope<>(command, ObjectId.get().toString());
        envelope.items().put(StandardMetadata.TraceInfo, traceInfo);

        this.send(envelope);
    }

    default void send(Collection<Command> commands, TraceInfo traceInfo) {
        Collection<Envelope<Command>> envelopes = commands.stream().map(command -> {
            Envelope<Command> envelope = new Envelope<>(command, ObjectId.get().toString());
            envelope.items().put(StandardMetadata.TraceInfo, traceInfo);
            return envelope;
        }).collect(Collectors.toList());

        this.send(envelopes);
    }

}

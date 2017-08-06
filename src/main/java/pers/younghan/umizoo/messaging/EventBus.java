/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import pers.younghan.umizoo.common.ObjectId;
import pers.younghan.umizoo.infrastructure.StandardMetadata;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface EventBus extends MessageBus<Event> {
    default void publish(Event event) {
        this.send(cast(event));
    }

    default void publish(Collection<Event> events) {
        this.send(events.stream().map(EventBus::cast).collect(Collectors.toList()));
    }

    default void publish(SourceInfo sourceInfo, Collection<VersionedEvent> events, Envelope<Command> command) {
        this.send(events.stream().map(event -> cast(event, sourceInfo, command)).collect(Collectors.toList()));
    }

    static Envelope<Event> cast(Event event) {
        return new Envelope<>(event, ObjectId.get().toString());
    }

    static Envelope<Event> cast(VersionedEvent event, SourceInfo sourceInfo, Envelope<Command> command) {
        Envelope<Event> envelope = new Envelope<>(event, md5(String.format("{0}&{1}", sourceInfo.getSourceId(), command.id())));
        if (command.items().containsKey(StandardMetadata.TraceInfo)) {
            envelope.items().put(StandardMetadata.TraceInfo, command.items().get(StandardMetadata.TraceInfo));
        }
        envelope.items().put(StandardMetadata.SourceInfo, sourceInfo);
        envelope.items().put(StandardMetadata.CommandInfo, new SourceInfo(command.id(), command.body().getClass()));

        return envelope;
    }

    static String md5(String source) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64en = new BASE64Encoder();

            return base64en.encode(md5.digest(source.getBytes("utf-8")));
        } catch (Exception ex) {
            return ObjectId.get().toString();
        }
    }
}

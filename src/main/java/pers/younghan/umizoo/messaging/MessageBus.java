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
public interface MessageBus<TMessage extends Message> {
    void send(final Envelope<TMessage> envelopedMessage);

    void send(final Collection<Envelope<TMessage>> envelopedMessages);
}

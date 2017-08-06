/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import pers.younghan.umizoo.messaging.Envelope;
import pers.younghan.umizoo.messaging.Message;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface EnvelopedMessageHandler<TMessage extends Message> extends Handler {
    void handle(Envelope<TMessage> envelope);
}

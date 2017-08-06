/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import pers.younghan.umizoo.common.ObjectId;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface PublishableExceptionBus extends MessageBus<PublishableException> {
    default void publish(PublishableException publishableException){
        Envelope<PublishableException> envelope = new Envelope<>(publishableException, ObjectId.get().toString());

        this.send(envelope);
    }
}

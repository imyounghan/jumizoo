/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import com.google.inject.Inject;
import pers.younghan.umizoo.common.composition.ObjectContainer;
import pers.younghan.umizoo.configurations.ProcessingFlags;
import pers.younghan.umizoo.infrastructure.Initializer;
import pers.younghan.umizoo.infrastructure.TypeUtils;
import pers.younghan.umizoo.messaging.Envelope;
import pers.younghan.umizoo.messaging.MessageReceiver;
import pers.younghan.umizoo.messaging.PublishableException;

import java.util.Collection;

/**
 * Created by young.han with IntelliJ IDEA on 2017-08-01.
 */
public class PublishableExceptionConsumer extends MessageConsumer<PublishableException> implements Initializer {
    @Inject
    public PublishableExceptionConsumer(MessageReceiver<Envelope<PublishableException>> exceptionReceiver)
    {
        super(exceptionReceiver, ProcessingFlags.PublishableException);
    }

    @Override
    public void initialize(ObjectContainer container, Collection<Class<?>> types) {
        types.stream().filter(TypeUtils::isPublishableException).forEach(exceptionType -> {
            this.initialize(container, exceptionType);
        });
    }
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;


import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-31.
 */
public class PublishableExceptionBroker extends MessageBroker<PublishableException> implements PublishableExceptionBus {
    public PublishableExceptionBroker() {
        super(new LinkedBlockingQueue<>());
    }
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public class QueryBroker extends MessageBroker<Query> implements QueryBus {
    public QueryBroker() {
        super(new LinkedBlockingQueue<>());
    }
}

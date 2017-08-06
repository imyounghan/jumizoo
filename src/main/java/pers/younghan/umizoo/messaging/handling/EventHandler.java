/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import pers.younghan.umizoo.messaging.VersionedEvent;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface EventHandler<TEvent extends VersionedEvent> extends Handler  {
    void handle(EventContext context, TEvent event);
}

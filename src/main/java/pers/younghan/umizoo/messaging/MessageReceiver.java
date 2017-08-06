/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface MessageReceiver<TMessage> {
    void addListener(MessageReceivedListener<TMessage> listener);

    void removeListener(MessageReceivedListener<TMessage> listener);

    void start();

    void stop();
}

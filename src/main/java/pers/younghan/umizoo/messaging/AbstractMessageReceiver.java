/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public abstract class AbstractMessageReceiver<TMessage extends Message> implements MessageReceiver<Envelope<TMessage>> {
    private ConcurrentHashMap<Class, MessageReceivedListener<Envelope<TMessage>>> listeners;
    private CompletableFuture future;
    private boolean cancelled;

    protected AbstractMessageReceiver() {
        this.listeners = new ConcurrentHashMap<>();
    }


    protected void process(Envelope<TMessage> envelope) {
        for (MessageReceivedListener<Envelope<TMessage>> listener : listeners.values()) {
            listener.onMessageReceived(envelope);
        }
    }

    protected abstract Envelope<TMessage> receiveMessage();

    @Override
    public void addListener(MessageReceivedListener<Envelope<TMessage>> listener) {
        listeners.put(listener.getClass(), listener);
    }

    @Override
    public void removeListener(MessageReceivedListener<Envelope<TMessage>> listener) {
        listeners.remove(listener.getClass());
    }

    private void longTask() {
        while (!this.cancelled) {
            try {
                Envelope<TMessage> envelope = this.receiveMessage();
                this.process(envelope);
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start() {
        if (Objects.isNull(future)) {
            this.cancelled = false;
            this.future = CompletableFuture.runAsync(this::longTask, Executors.newWorkStealingPool());
        }
    }

    @Override
    public void stop() {
        if (Objects.nonNull(future)) {
            this.future.cancel(true);
            this.future = null;
            this.cancelled = true;
        }
    }
}

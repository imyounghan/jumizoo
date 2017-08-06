/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import pers.younghan.umizoo.common.LogManager;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-30.
 */
public class MessageBroker<TMessage extends Message> extends AbstractMessageReceiver<TMessage> implements MessageBus<TMessage> {

    private final BlockingQueue<Envelope<TMessage>> queue;

    public MessageBroker() {
        this(new SynchronousQueue<>(false));
    }

    protected MessageBroker(BlockingQueue<Envelope<TMessage>> queue) {
        this.queue = queue;
    }


    @Override
    protected Envelope<TMessage> receiveMessage() {
        try {
            Envelope<TMessage> envelope = queue.take();
            if(LogManager.getDefault().isDebugEnabled()) {
                LogManager.getDefault().debug("Take an envelope '%s' from local queue.", envelope);
            }

            return envelope;
        }
        catch(InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void send(Envelope<TMessage> envelope) {
        if(LogManager.getDefault().isDebugEnabled()) {
            LogManager.getDefault().debug("Prepare to add an envelope '%s' in local queue.", envelope);
        }
        boolean success = this.queue.add(envelope);
        if(!success && LogManager.getDefault().isDebugEnabled()) {
            LogManager.getDefault().debug("Failed to Add an envelope '%s' in local queue.", envelope);
        }
    }

    @Override
    public void send(Collection<Envelope<TMessage>> envelopes) {
        envelopes.forEach(this::send);
    }
}

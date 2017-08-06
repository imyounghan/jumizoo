/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import pers.younghan.umizoo.common.LogManager;
import pers.younghan.umizoo.configurations.ProcessingFlags;
import pers.younghan.umizoo.infrastructure.Processor;
import pers.younghan.umizoo.messaging.Envelope;
import pers.younghan.umizoo.messaging.MessageReceiver;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public abstract class Consumer<TMessage> implements Processor {

    private final MessageReceiver<Envelope<TMessage>> receiver;
    private final ProcessingFlags processingFlag;

    private boolean started;

    protected Consumer(MessageReceiver<Envelope<TMessage>> receiver, ProcessingFlags processingFlag) {
        this.receiver = receiver;
        this.processingFlag = processingFlag;
    }

    protected abstract void onMessageArrived(Envelope<TMessage> envelope) throws Exception;

    private void listeningEnvelopedMessage(Envelope<TMessage> envelope) {
        try {
            this.onMessageArrived(envelope);
        }
        catch (Exception ex) {
            LogManager.getDefault().error(ex,
                    "An exception happened while handling '%s' through handler, Error will be ignored and message receiving will continue.", envelope);
        }
    }

    @Override
    public final synchronized void start() {
        if (!this.started) {
            this.receiver.addListener(this::listeningEnvelopedMessage);
            this.receiver.start();
            this.started = true;

            LogManager.getDefault().info("%s Consumer Started!", processingFlag);
        }
    }

    @Override
    public final synchronized void stop() {
        if (this.started) {
            this.receiver.removeListener(this::listeningEnvelopedMessage);
            this.receiver.stop();
            this.started = false;

            LogManager.getDefault().info("%s Consumer Stopped!", processingFlag);
        }
    }
}

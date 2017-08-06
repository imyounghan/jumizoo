/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import pers.younghan.umizoo.common.LogManager;
import pers.younghan.umizoo.common.composition.ObjectContainer;
import pers.younghan.umizoo.configurations.ConfigurationSettings;
import pers.younghan.umizoo.configurations.ProcessingFlags;
import pers.younghan.umizoo.infrastructure.Initializer;
import pers.younghan.umizoo.infrastructure.Processor;
import pers.younghan.umizoo.messaging.Envelope;
import pers.younghan.umizoo.messaging.Message;
import pers.younghan.umizoo.messaging.MessageReceiver;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public abstract class MessageConsumer<TMessage extends Message> extends Consumer<TMessage> {

    private HashMap<Type, Collection<Handler>> envelopeHandlers;
    private HashMap<Type, Collection<Handler>> messageHandlers;
    private CheckHandlerMode checkHandlerMode;

    protected MessageConsumer(MessageReceiver<Envelope<TMessage>> receiver, ProcessingFlags processingFlag) {
        this(receiver, CheckHandlerMode.Ignored, processingFlag);
    }

    protected MessageConsumer(MessageReceiver<Envelope<TMessage>> receiver, CheckHandlerMode checkHandlerMode, ProcessingFlags processingFlag) {
        super(receiver, processingFlag);

        this.checkHandlerMode = checkHandlerMode;
        this.envelopeHandlers = new HashMap<>();
        this.messageHandlers = new HashMap<>();
    }

    protected final void initialize(ObjectContainer container, Class messageType) {
        ParameterizedType envelopeHandlerType = ParameterizedTypeImpl.make(EnvelopedMessageHandler.class, new Type[]{ messageType }, null);

        List<Handler> handlers = container.resolveAll(envelopeHandlerType).stream().map(this::cast).filter(Objects::nonNull).collect(Collectors.toList());
        if (handlers.size() > 0) {
            this.envelopeHandlers.put(messageType, handlers);

            if (this.checkHandlerMode == CheckHandlerMode.OnlyOne) {
                if (handlers.size() > 1) {
                    throw new RuntimeException(String.format("Found more than one handler for this type('%s') with EnvelopedMessageHandler<>.", messageType.getName()));
                }

                return;
            }
        }

        ParameterizedType messageHandlerType = ParameterizedTypeImpl.make(MessageHandler.class, new Type[]{ messageType }, null);
        handlers = container.resolveAll(messageHandlerType).stream().map(this::cast).filter(Objects::nonNull).collect(Collectors.toList());

        if (this.checkHandlerMode == CheckHandlerMode.OnlyOne) {
            switch (handlers.size()) {
                case 0:
                    throw new RuntimeException(String.format("The handler of this type('%s') is not found.", messageType.getName()));
                case 1:
                    break;
                default:
                    throw new RuntimeException(String.format("Found more than one handler for '%s' with MessageHandler<>.", messageType.getName()));
            }
        }

        if (handlers.size() > 0) {
            this.messageHandlers.put(messageType, handlers);
        }
    }


    private Handler cast(Object object) {
        if (object instanceof Handler) {
            return (Handler)object;
        }

        return null;
    }

    protected List<Handler> getHandlers(Type messageType) {
        ArrayList<Handler> combinedHandlers = new ArrayList<>();

        combinedHandlers.addAll(this.envelopeHandlers.getOrDefault(messageType, Collections.EMPTY_LIST));
        combinedHandlers.addAll(this.messageHandlers.getOrDefault(messageType, Collections.EMPTY_LIST));

        return combinedHandlers;
    }

    @Override
    protected void onMessageArrived(Envelope<TMessage> envelope) throws Exception {
        Collection<Handler> combinedHandlers = this.getHandlers(envelope.body().getClass());
        if (combinedHandlers.isEmpty()) {
            LogManager.getDefault().warn("There is no handler of type('%s') with MessageHandler<> or EnvelopedMessageHandler<>.", envelope.body().getClass().getName());
            return;
        }

        for (Handler handler : combinedHandlers) {
            if (handler instanceof EnvelopedMessageHandler) {
                EnvelopedMessageHandler<TMessage> envelopeHandler = (EnvelopedMessageHandler<TMessage>)handler;
                this.tryMultipleInvoke(this::invokeHandler, envelopeHandler, envelope);
            }
            else if (handler instanceof MessageHandler) {
                MessageHandler<TMessage> messageHandler = (MessageHandler<TMessage>)handler;
                this.tryMultipleInvoke(this::invokeHandler, messageHandler, envelope.body());
            }
        }
    }

    protected <THandler, TParameter> void tryMultipleInvoke(BiConsumer<THandler, TParameter> retryAction, THandler handler, TParameter parameter) throws InterruptedException {
        int retryTimes = ConfigurationSettings.HandleRetryTimes;
        long retryInterval = ConfigurationSettings.HandleRetryInterval;

        int count = 0;
        while (count++ < retryTimes) {
            try {
                retryAction.accept(handler, parameter);
                break;
            }
            catch (RuntimeException ex) {
                LogManager.getDefault().error(ex,
                        "UnrecoverableException raised when handling '%s' on '%s', exit retry and throw.", parameter, handler.getClass().getName());

                throw ex;
            }
            catch (Exception ex) {
                if (count == retryTimes) {
                    LogManager.getDefault().error(ex,
                            "Exception raised when handling '%s' on '%s', the retry count has been reached.", parameter, handler.getClass().getName());
                    throw ex;
                }

                Thread.sleep(retryInterval);
            }
        }

        if (LogManager.getDefault().isDebugEnabled()) {
            LogManager.getDefault().debug("Handle '%s' on '%s' successfully.", parameter, handler.getClass().getName());
        }
    }

    protected void invokeHandler(MessageHandler<TMessage> handler, TMessage message) {
        handler.handle(message);
    }

    protected void invokeHandler(EnvelopedMessageHandler<TMessage> handler, Envelope<TMessage> envelopedMessage) {
        handler.handle(envelopedMessage);
    }


    /**
     * 检查处理器的方式
     */
    protected enum CheckHandlerMode {
        OnlyOne, Ignored,
    }
}

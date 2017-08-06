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
import pers.younghan.umizoo.messaging.*;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by young.han with IntelliJ IDEA on 2017-08-01.
 */
public class ResultConsumer extends Consumer<Result> implements Initializer {
    private final HashMap<Type, EnvelopedMessageHandler> resultHandlers;

    @Inject
    public ResultConsumer(MessageReceiver<Envelope<Result>> resultReceiver) {
        super(resultReceiver, ProcessingFlags.Result);

        this.resultHandlers = new HashMap<>();
    }


    public static ParameterizedType makeEnvelopedHandlerType(Class<?> resultType) {
        ParameterizedType envelopeType = ParameterizedTypeImpl.make(Envelope.class, new Type[]{ resultType }, null);
        return ParameterizedTypeImpl.make(EnvelopedMessageHandler.class, new Type[]{ envelopeType }, null);
    }


    @Override
    public void initialize(ObjectContainer container, Collection<Class<?>> types) {
        types.stream().filter(TypeUtils::isResult).forEach(resultType -> {
            ParameterizedType resultHandlerType = makeEnvelopedHandlerType(resultType);

            Object handler = container.resolve(resultHandlerType);
            if (handler instanceof EnvelopedMessageHandler) {
                this.resultHandlers.put(resultType, (EnvelopedMessageHandler)handler);
            }
            else {
                throw new RuntimeException(String.format("not found the handler of this type('%s') with EnvelopedMessageHandler<>.", resultType.getName()));
            }
        });
    }

    @Override
    protected void onMessageArrived(Envelope<Result> envelope) throws Exception {
        Class resultType = envelope.body().getClass();

        EnvelopedMessageHandler handler = this.resultHandlers.get(resultType);
        handler.handle(envelope);
    }
}

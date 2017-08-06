/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.seeds;

import org.reflections.ReflectionUtils;
import pers.younghan.umizoo.infrastructure.TypeUtils;
import pers.younghan.umizoo.messaging.Event;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.function.BiConsumer;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.04.
 */
public final class AggregateInternalHandlerProvider {
    public static final AggregateInternalHandlerProvider Instance = new AggregateInternalHandlerProvider();
    private static final String HANDLE_METHOD_NAME = "handle";

    private final HashMap<Class, HashMap<Class, Method>> innerHandlers;
    private boolean initialized;

    private AggregateInternalHandlerProvider() {
        innerHandlers = new HashMap<>();
    }

    private void registerInnerHandler(Class aggregateRootType) {
        HashMap<Class, Method> eventMethodMap = new HashMap<>();

        ReflectionUtils.getAllMethods(aggregateRootType,
                ReflectionUtils.withName(HANDLE_METHOD_NAME),
                ReflectionUtils.withParametersCount(1))
                .forEach(method -> {
                    Class<?> eventType = method.getParameterTypes()[0];

                    if(!TypeUtils.isEvent(eventType)){
                        return;
                    }

                    if (eventMethodMap.containsKey(eventType)) {
                        String errorMessage = String.format("found duplicated handler from '{0}' on '{1}'.", eventType.getName(), aggregateRootType.getName());
                        throw new RuntimeException(errorMessage);
                    }

                    method.setAccessible(true);
                    eventMethodMap.put(eventType, method);
                });

        if (eventMethodMap.size() > 0) {
            innerHandlers.put(aggregateRootType, eventMethodMap);
        }
    }

    public void initialize(Collection<Class<?>> types) {
        if (initialized) {
            return;
        }

        types.stream().filter(TypeUtils::isAggregateRoot).forEach(this::registerInnerHandler);
        initialized = true;
    }

    public BiConsumer<AggregateRoot, Event> getEventHandler(Class<? extends AggregateRoot> aggregateRootType, Class<? extends Event> eventType) {

        HashMap<Class, Method> eventMethodMap = innerHandlers.get(aggregateRootType);
        if (eventMethodMap == null) {
            return null;
        }

        Method method = eventMethodMap.get(eventType);

        if (method == null) {
            return null;
        }

        return (aggregateRoot, domainEvent) -> {
            try {
                method.invoke(aggregateRoot, domainEvent);
            }
            catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        };
    }
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common;

import pers.younghan.umizoo.common.composition.*;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Supplier;


/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public final class ServiceLocator {
    private static ObjectContainer container;

    public static Object getInstance(Type serviceType) {
        return getContainer().resolve(serviceType, null);
    }

    public static Object getInstance(Type serviceType, String key) {
        return getContainer().resolve(serviceType, key);
    }

    public static <TService> TService getInstance(Class<TService> serviceType) {
        return getContainer().resolve(serviceType, null);
    }

    public static <TService> TService getInstance(Class<TService> serviceType, String key) {
        return getContainer().resolve(serviceType, key);
    }

    public static <TService> Iterable<TService> getAllInstances(Class<TService> serviceType) {
        return getContainer().resolveAll(serviceType);
//        Iterable<Object> iterable = container.resolveAll(serviceType);
//        return Lists.newArrayList(iterable)
//                .stream().map(obj -> (TService) obj)
//                .collect(Collectors.toList());

    }

    private static ObjectContainer getContainer() {
        if (Objects.isNull(container)) {
            synchronized (ServiceLocator.class) {
                if (Objects.isNull(container)) {
                    container = ObjectContainerImpl.INSTANCE;
                }
            }
        }

        return container;
    }

    public static void setLocatorProvider(ObjectContainer provider) {
        if (Objects.isNull(container)) {
            synchronized (ServiceLocator.class) {
                if (Objects.isNull(container)) {
                    container = provider;
                }
            }
        }
    }

    public static void setLocatorProvider(Supplier<ObjectContainer> provider) {
        if (Objects.isNull(container)) {
            synchronized (ServiceLocator.class) {
                if (Objects.isNull(container)) {
                    container = provider.get();
                    container.complete();
                }
            }
        }
    }
}

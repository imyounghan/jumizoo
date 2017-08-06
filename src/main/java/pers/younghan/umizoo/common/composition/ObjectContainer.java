/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common.composition;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 表示对象容器的接口
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface ObjectContainer {
    Collection<TypeRegistration> registeredTypes();

    default <TService> boolean isRegistered(Class<TService> serviceType) {
        return this.isRegistered(serviceType, null);
    }

    <TService> boolean isRegistered(Class<TService> serviceType, String serviceName);

    default boolean override(Type serviceType, Object instance) {
        return this.override(serviceType, null, instance);
    }

    boolean override(Type serviceType, String serviceName, Object instance);


    default boolean override(Type type) {
        return this.override(type, null, Lifecycle.Singleton);
    }

    default boolean override(Type type, String name) {
        return this.override(type, name, Lifecycle.Singleton);
    }

    default boolean override(Type type, Lifecycle lifecycle) {
        return this.override(type, null, lifecycle);
    }

    boolean override(Type type, String name, Lifecycle lifecycle);

    default boolean override(Type serviceType, Type implementerType) {
        return this.override(serviceType, null, Lifecycle.Singleton, implementerType);
    }

    default boolean override(Type serviceType, String serviceName, Type implementerType) {
        return this.override(serviceType, serviceName, Lifecycle.Singleton, implementerType);
    }

    default boolean override(Type serviceType, Lifecycle lifecycle, Type implementerType) {
        return this.override(serviceType, null, Lifecycle.Singleton, implementerType);
    }

    boolean override(Type serviceType, String serviceName, Lifecycle lifecycle, Type implementerType);


    default <TService> boolean register(Class<TService> serviceType, TService instance) {
        return this.register(serviceType, null, instance);
    }

    <TService> boolean register(Class<TService> serviceType, String serviceName, TService instance);


    default <T> boolean register(Class<T> type) {
        return this.register(type, null, Lifecycle.Singleton);
    }

    default <T> boolean register(Class<T> type, String name) {
        return this.register(type, name, Lifecycle.Singleton);
    }

    default <T> boolean register(Class<T> type, Lifecycle lifecycle) {
        return this.register(type, null, lifecycle);
    }

    <T> boolean register(Class<T> type, String name, Lifecycle lifecycle);



    default <TService, TImplementer extends TService> boolean register(Class<TService> serviceType, Class<TImplementer> implementerType) {
        return this.register(serviceType, null, Lifecycle.Singleton, implementerType);
    }

    default <TService, TImplementer extends TService> boolean register(Class<TService> serviceType, String serviceName, Class<TImplementer> implementerType) {
        return this.register(serviceType, serviceName, Lifecycle.Singleton, implementerType);
    }

    default <TService, TImplementer extends TService> boolean register(Class<TService> serviceType, Lifecycle lifecycle, Class<TImplementer> implementerType) {
        return this.register(serviceType, null, lifecycle, implementerType);
    }

    <TService, TImplementer extends TService> boolean register(Class<TService> serviceType, String serviceName, Lifecycle lifecycle, Class<TImplementer> implementerType);


    default <TService> TService resolve(Class<TService> type) {
        return this.resolve(type, null);
    }

    <TService> TService resolve(Class<TService> type, String name);

    default Object resolve(Type type) {
        return this.resolve(type, null);
    }

    Object resolve(Type type, String name);

    <TService> Collection<TService> resolveAll(Class<TService> type);

    Collection<Object> resolveAll(Type type);

    void complete();
}

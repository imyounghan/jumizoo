/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common.composition;


import com.google.common.base.Strings;
import com.google.common.base.Verify;
import com.google.inject.*;
import com.google.inject.internal.MoreTypes;
import com.google.inject.name.Names;
import pers.younghan.umizoo.infrastructure.Initializer;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class ObjectContainerImpl implements ObjectContainer {
    public final static ObjectContainer INSTANCE = new ObjectContainerImpl();

    private final HashMap<Type, ArrayList<Key<?>>> multiMap;
    private final ObjectContainerImpl parent;
    private HashMap<Key, Module> iocMap;
    private HashSet<TypeRegistration> registeredTypes;
    private Injector injector;

    public ObjectContainerImpl() {
        this(null);
    }

    private ObjectContainerImpl(ObjectContainerImpl parent) {
        this.iocMap = new HashMap<>();
        this.multiMap = new HashMap<>();
        this.registeredTypes = new HashSet<>();
        this.parent = parent;
    }

    private static Scope toScope(Lifecycle lifecycle) {
        if (lifecycle == null)
            return Scopes.SINGLETON;
        switch (lifecycle) {
            case Singleton:
                return Scopes.SINGLETON;
            default:
                return Scopes.NO_SCOPE;
        }
    }

    private static <TService> Key<TService> getKey(TypeLiteral<TService> typeLiteral, String name) {
        return Strings.isNullOrEmpty(name) ? Key.get(typeLiteral) : Key.get(typeLiteral, Names.named(name));
    }

//    private static <TService> Key<TService> getKey(Class<TService> type, String name) {
//        return getKey(TypeLiteral.get(type), name);
//    }

    private boolean isRegistered(Key key) {
        return multiMap.values().stream().anyMatch(list -> list.contains(key));
    }

    @Override
    public Collection<TypeRegistration> registeredTypes() {
        return Collections.unmodifiableSet(registeredTypes);
    }

    @Override
    public boolean isRegistered(Class serviceType, String serviceName) {
        Verify.verifyNotNull(serviceType);

        TypeLiteral typeLiteral = TypeLiteral.get(serviceType);

        return isRegistered(getKey(typeLiteral, serviceName));
    }

    @Override
    public boolean override(Type serviceType, String serviceName, Object instance) {
        if(Objects.isNull(iocMap)) {
            throw new RuntimeException("can not register type because this container is completed, please invoke before 'complete' method.");
        }

        Verify.verifyNotNull(serviceType);
        Verify.verifyNotNull(instance);

        TypeLiteral<?> typeLiteral = TypeLiteral.get(serviceType);
        Key key = getKey(typeLiteral, serviceName);

        if(this.iocMap.containsKey(key)) {
            //String errorMessage = String.format("the type of '{0}' as name '{1}' has been registered.", type.getTypeName(), name);
            //throw new IllegalArgumentException(errorMessage);
            return false;
        }

        if(!typeLiteral.getRawType().isInstance(instance)) {
            String errorMessage = String.format("the type of '{0}' does not extends the type of '{1}'.", instance.getClass(), serviceType);
            throw new IllegalArgumentException(errorMessage);
        }

        iocMap.computeIfAbsent(key, sourceKey -> new AbstractModule() {
            @Override
            protected void configure() {
                super.binder().bind(sourceKey).toInstance(instance);
            }
        });
        multiMap.computeIfAbsent(serviceType, type -> new ArrayList<>()).add(key);
        this.registeredTypes.add(new TypeRegistration(serviceType, serviceName, instance instanceof Initializer));

        return true;
    }

    @Override
    public boolean override(Type type, String name, Lifecycle lifecycle) {
        if(Objects.isNull(iocMap)) {
            throw new RuntimeException("can not register type because this container is completed, please invoke before 'complete' method.");
        }

        Verify.verifyNotNull(type);

        TypeLiteral typeLiteral = TypeLiteral.get(type);

        if(typeLiteral.getRawType().isInterface() || Modifier.isAbstract(typeLiteral.getRawType().getModifiers())) {
            String errorMessage = String.format("the type of '{0}' must be a class and cannot be abstract.", type);
            throw new IllegalArgumentException(errorMessage);
        }

        Key key = getKey(typeLiteral, name);

        if(this.iocMap.containsKey(key)) {
            //String errorMessage = String.format("the type of '{0}' as name '{1}' has been registered.", type.getTypeName(), name);
            //throw new IllegalArgumentException(errorMessage);
            return false;
        }

        iocMap.computeIfAbsent(key, (sourceKey) -> new AbstractModule() {
            @Override
            protected void configure() {
                super.binder().bind(sourceKey).to(typeLiteral.getRawType()).in(toScope(lifecycle));
            }
        });
        multiMap.computeIfAbsent(type, clazz -> new ArrayList<>()).add(key);

        this.registeredTypes.add(new TypeRegistration(type, name, lifecycle == Lifecycle.Singleton && Initializer.class.isAssignableFrom(typeLiteral.getRawType())));

        return true;
    }

    @Override
    public boolean override(Type serviceType, String serviceName, Lifecycle lifecycle, Type implementerType) {
        if(Objects.isNull(iocMap)) {
            throw new RuntimeException("can not register type because this container is completed, please invoke before 'complete' method.");
        }

        Verify.verifyNotNull(serviceType);
        Verify.verifyNotNull(implementerType);

        TypeLiteral implementerTypeLiteral = TypeLiteral.get(implementerType);
        if(implementerTypeLiteral.getRawType().isInterface() || Modifier.isAbstract(implementerTypeLiteral.getRawType().getModifiers())) {
            String errorMessage = String.format("the type of '{0}' must be a class and cannot be abstract.", implementerType.getTypeName());
            throw new IllegalArgumentException(errorMessage);
        }

        TypeLiteral serviceTypeLiteral = TypeLiteral.get(serviceType);
        if(!serviceTypeLiteral.getRawType().isAssignableFrom(implementerTypeLiteral.getRawType())) {
            String errorMessage = String.format("the type of '{0}' does not extends the type of '{1}'.", serviceType, implementerType);
            throw new IllegalArgumentException(errorMessage);
        }

        Key key = getKey(serviceTypeLiteral, serviceName);

        if(this.iocMap.containsKey(key)) {
            //String errorMessage = String.format("the type of '{0}' as name '{1}' has been registered.", type.getTypeName(), name);
            //throw new IllegalArgumentException(errorMessage);
            return false;
        }

        iocMap.computeIfAbsent(key, (sourceKey) -> new AbstractModule() {
            @Override
            protected void configure() {
                super.binder().bind(sourceKey).to(implementerTypeLiteral.getRawType()).in(toScope(lifecycle));
            }
        });
        multiMap.computeIfAbsent(serviceType, clazz -> new ArrayList<>()).add(key);

        this.registeredTypes.add(new TypeRegistration(serviceType, serviceName, lifecycle == Lifecycle.Singleton && Initializer.class.isAssignableFrom(implementerTypeLiteral.getRawType())));

        return true;
    }

    @Override
    public <TService> boolean register(Class<TService> serviceType, String serviceName, TService instance) {
        return this.override(serviceType, serviceName, instance);
    }

    @Override
    public <TService> boolean register(Class<TService> type, String name, Lifecycle lifecycle) {
        return this.override(type, name, lifecycle);
    }

    @Override
    public <TService, TImplementer extends TService> boolean register(Class<TService> serviceType, String serviceName, Lifecycle lifecycle, Class<TImplementer> implementerType) {
        return this.override(serviceType, serviceName, lifecycle, implementerType);
    }

    @Override
    public <TService> TService resolve(Class<TService> type, String name) {
        Verify.verifyNotNull(type);

        Key<TService> key = getKey(TypeLiteral.get(type), name);

        if(!this.isRegistered(key)) {
            return null;
        }
        return injector.getInstance(key);
    }

    @Override
    public Object resolve(Type type, String name) {
        Verify.verifyNotNull(type);

        Key<?> key = getKey(TypeLiteral.get(type), name);

        return injector.getInstance(key);
    }

    @Override
    public <TService> Collection<TService> resolveAll(Class<TService> type) {
        Verify.verifyNotNull(type);

        ArrayList<TService> list = new ArrayList<>();
        if(multiMap.containsKey(type)) {
            multiMap.get(type).forEach(key -> list.add(injector.getInstance((Key<TService>)key)));
        }

        return list;
    }

    @Override
    public Collection<Object> resolveAll(Type type) {
        Verify.verifyNotNull(type);

        ArrayList<Object> list = new ArrayList<>();
        if(multiMap.containsKey(type)) {
            multiMap.get(type).forEach(key -> list.add(injector.getInstance(key)));
        }

        return list;
    }

    @Override
    public void complete() {
        if(Objects.isNull(iocMap) || iocMap.isEmpty()) {
            return;
        }

        Module[] modules = iocMap.values().toArray(new Module[0]);

        if(Objects.isNull(parent)) {
            this.injector = Guice.createInjector(modules);
        }
        else {
            this.injector = parent.injector.createChildInjector(modules);
        }

        iocMap.clear();
        iocMap = null;
    }

    public ObjectContainer getParent() throws NullPointerException {
        return parent;
    }

    public ObjectContainer createChildContainer() {
        return new ObjectContainerImpl(this);
    }
}
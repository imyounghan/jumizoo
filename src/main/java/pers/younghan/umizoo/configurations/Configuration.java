/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.configurations;

import com.google.common.base.Stopwatch;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import pers.younghan.umizoo.common.LogManager;
import pers.younghan.umizoo.common.ServiceLocator;
import pers.younghan.umizoo.common.composition.*;
import pers.younghan.umizoo.infrastructure.*;
import pers.younghan.umizoo.messaging.*;
import pers.younghan.umizoo.messaging.handling.*;
import pers.younghan.umizoo.seeds.*;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class Configuration {
    public static Configuration create() {
        return create(new ObjectContainerImpl());
    }

    public static Configuration create(ObjectContainer container) {
        return new Configuration(container);
    }

    private static final String[] UMIZOO_PACKAGE_SCAN = new String[]{
            "pers.younghan.umizoo.seeds",
            "pers.younghan.umizoo.messaging"
    };

    private final ObjectContainer container;

    private Collection<Class<?>> classes;
    private Stopwatch stopwatch;

    protected Configuration(ObjectContainer container) {
        this.stopwatch = Stopwatch.createStarted();
        this.container = container;
    }

    public Configuration register(Consumer<ObjectContainer> action) {
        action.accept(container);
        return this;
    }

    public Configuration loadPackages(String... packages) {
        String[] scans = new String[packages.length + UMIZOO_PACKAGE_SCAN.length];
        System.arraycopy(packages, 0, scans, 0, packages.length);
        System.arraycopy(UMIZOO_PACKAGE_SCAN, 0, scans, packages.length, UMIZOO_PACKAGE_SCAN.length);

        FilterBuilder filter = new FilterBuilder();
        filter.include(FilterBuilder.prefix("pers.younghan.umizoo.seeds.AbstractAggregateRoot"));
        filter.include(FilterBuilder.prefix("pers.younghan.umizoo.seeds.AbstractEventSourced"));
        //filter.include(FilterBuilder.prefix("pers.younghan.umizoo.seeds.AggregateRoot"));
        //filter.include(FilterBuilder.prefix("pers.younghan.umizoo.seeds.EventSourced"));
        filter.include(FilterBuilder.prefix("pers.younghan.umizoo.seeds.Entity"));
        //filter.include(FilterBuilder.prefix("pers.younghan.umizoo.infrastructure.UniquelyIdentifiable"));
        //filter.include(FilterBuilder.prefix("pers.younghan.umizoo.messaging.EventPublisher"));
        filter.include(FilterBuilder.prefix("pers.younghan.umizoo.messaging.CommandBase"));
        filter.include(FilterBuilder.prefix("pers.younghan.umizoo.messaging.EventBase"));
        filter.include(FilterBuilder.prefix("pers.younghan.umizoo.messaging.VersionedEventBase"));
        //filter.include(FilterBuilder.prefix("pers.younghan.umizoo.messaging.VersionedEvent"));
        //filter.include(FilterBuilder.prefix("pers.younghan.umizoo.messaging.Event"));
        filter.include(FilterBuilder.prefix("pers.younghan.umizoo.messaging.HandleResult"));
        filter.include(FilterBuilder.prefix("pers.younghan.umizoo.messaging.CommandResultGenerated"));
        filter.include(FilterBuilder.prefix("pers.younghan.umizoo.messaging.QueryResultBuilt"));
        filter.include(FilterBuilder.prefix("pers.younghan.umizoo.messaging.PagedQuery"));
        for (String qualifiedName : packages) {
            filter.include(FilterBuilder.prefix(qualifiedName));
        }

        ConfigurationBuilder configuration = new ConfigurationBuilder()
                .forPackages(scans)
                .filterInputsBy(filter)
                .setScanners(new SubTypesScanner(false));
        this.classes = new Reflections(configuration).getSubTypesOf(Object.class).stream().filter(TypeUtils::isClass).collect(Collectors.toList());

        return this;
    }

//    public Configuration useLocalQueue() {
//        return this.useLocalQueue(EnumSet.allOf(ProcessingFlags.class));
//    }

    public Configuration useLocalQueue(EnumSet<ProcessingFlags> processingFlags) {
        if (processingFlags.contains(ProcessingFlags.Command)) {
            CommandBroker commandBroker = new CommandBroker();
            container.register(CommandBus.class, commandBroker);
            container.override(makeMessageReceiverType(Command.class), commandBroker);
        }

        if (processingFlags.contains(ProcessingFlags.Event)) {
            EventBroker eventBroker = new EventBroker();
            container.register(EventBus.class, eventBroker);
            container.override(makeMessageReceiverType(Event.class), eventBroker);
        }

        if (processingFlags.contains(ProcessingFlags.PublishableException)) {
            PublishableExceptionBroker exceptionBroker = new PublishableExceptionBroker();
            container.register(PublishableExceptionBus.class, exceptionBroker);
            container.override(makeMessageReceiverType(PublishableException.class), exceptionBroker);
        }

        if (processingFlags.contains(ProcessingFlags.Query)) {
            QueryBroker queryBroker = new QueryBroker();
            container.register(QueryBus.class, queryBroker);
            container.override(makeMessageReceiverType(Query.class), queryBroker);
        }

        return this.enableProcessors(processingFlags);
    }

    public Configuration enableProcessors(EnumSet<ProcessingFlags> processingFlags) {
        if (processingFlags.contains(ProcessingFlags.Command)) {
            container.register(Processor.class, "command", CommandConsumer.class);
        }

        if (processingFlags.contains(ProcessingFlags.Event)) {
            container.register(Processor.class, "event", EventConsumer.class);
        }

        if (processingFlags.contains(ProcessingFlags.PublishableException)) {
            container.register(Processor.class, "exception", PublishableExceptionConsumer.class);
        }

        if (processingFlags.contains(ProcessingFlags.Query)) {
            container.register(Processor.class, "query", QueryConsumer.class);
        }

        return this;
    }

    public Configuration enableService(ConnectionMode connectionMode) {
        container.register(Processor.class, "result", ResultConsumer.class);

        ParameterizedType commandResultHandlerType = ResultConsumer.makeEnvelopedHandlerType(CommandResultGenerated.class);
        switch (connectionMode) {
            case Local:
                container.override(commandResultHandlerType, CommandResultNotifyHandler.class);
                container.override(ResultConsumer.makeEnvelopedHandlerType(QueryResultBuilt.class), QueryResultNotifyHandler.class);
                this.useLocalQueue(EnumSet.allOf(ProcessingFlags.class));
                container.register(CommandService.class, CommandServiceImpl.class);
                container.register(QueryService.class, QueryServiceImpl.class);
                break;
            case Rmi:
                break;

        }

        return this;
    }

    public void done() {
        this.registerComponents(classes);
        this.registerDefaultComponents();
        this.registerHandlers(classes);

        container.complete();
        container.registeredTypes().stream()
                .filter(TypeRegistration::initializationRequired)
                .map(component -> (Initializer) component.getInstance(container))
                .distinct()
                .forEach(initializer -> initializer.initialize(container, classes));

        ServiceLocator.setLocatorProvider(container);

        this.start();

        this.stopwatch.stop();

        LogManager.getDefault().debug("system is working, used time:%s ms", this.stopwatch.elapsed(TimeUnit.MILLISECONDS));

        this.stopwatch = null;
    }

    public void start() {
        container.resolveAll(Processor.class).forEach(Processor::start);
    }

    private static ParameterizedType makeMessageReceiverType(Class<? extends Message> messageType) {
        ParameterizedType envelopeType = ParameterizedTypeImpl.make(Envelope.class, new Type[]{ messageType }, null);
        return ParameterizedTypeImpl.make(MessageReceiver.class, new Type[]{ envelopeType }, null);
    }

    private void registerDefaultComponents() {
        container.register(TextSerializer.class, TextSerializerImpl.instance);
        container.register(EventStore.class, EventStoreInMemory.class);
        container.register(EventPublishedVersionStore.class, EventPublishedVersionStoreInMemory.class);
        container.register(SnapshotStore.class, NoneSnapshotStore.class);
        container.register(Cache.class, LocalCache.class);
        container.register(AggregateStorage.class, NonAggregateStorage.class);
        container.register(Repository.class, RepositoryImpl.class);
        container.register(ResultManager.class, ResultManagerImpl.class);

        ResultBroker resultBroker = new ResultBroker();
        container.register(ResultBus.class, resultBroker);
        container.override(makeMessageReceiverType(Result.class), resultBroker);
    }

    private static boolean isComponent(Class type) {
        return !Modifier.isAbstract(type.getModifiers()) && type.isAnnotationPresent(RegisterAnnotation.class);
    }

    private static Lifecycle getLifecycle(Class type) {
        LifecycleAnnotation annotation = (LifecycleAnnotation) type.getAnnotation(LifecycleAnnotation.class);
        if (annotation != null) {
            return annotation.lifecycle();
        }

        return Lifecycle.Singleton;
    }

    private void registerComponent(Class type) {
        Annotation[] annotations = type.getAnnotationsByType(RegisterAnnotation.class);
        Lifecycle lifecycle = getLifecycle(type);
        for (Annotation annotation : annotations) {
            RegisterAnnotation component = (RegisterAnnotation) annotation;
            if (component != null) {
                Class contractType = component.type();
                if (contractType == null) {
                    container.override(type, component.name(), lifecycle);
                }
                else {
                    container.override(contractType, component.name(), lifecycle, type);
                }
            }
        }
    }

    private void registerComponents(Collection<Class<?>> types) {
        types.stream().filter(Configuration::isComponent).forEach(this::registerComponent);
    }

    private void registerHandler(Type interfaceType, Class implementerType, Lifecycle lifecycle) {
        Type contactType = ((ParameterizedType) interfaceType).getRawType();

        if (contactType.equals(CommandHandler.class) || contactType.equals(EventHandler.class) || contactType.equals(QueryHandler.class) ||
                contactType.equals(MessageHandler.class) || contactType.equals(EnvelopedMessageHandler.class)) {
            container.override(interfaceType, implementerType.getName(), lifecycle, implementerType);
        }
    }

    private void registerHandlers(Collection<Class<?>> types) {
        types.stream().filter(TypeUtils::isHandler).forEach(type -> {
            Type[] interfaceTypes = type.getGenericInterfaces();
            Lifecycle lifecycle = getLifecycle(type);

            for (Type interfaceType : interfaceTypes) {
                registerHandler(interfaceType, type, lifecycle);
            }
        });


        AggregateInternalHandlerProvider.Instance.initialize(types);
    }
}

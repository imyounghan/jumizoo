/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.seeds;

import com.google.common.base.Verify;
import com.google.inject.Inject;
import pers.younghan.umizoo.common.LogManager;
import pers.younghan.umizoo.common.ObjectId;
import pers.younghan.umizoo.infrastructure.Cache;
import pers.younghan.umizoo.infrastructure.SnapshotStore;
import pers.younghan.umizoo.infrastructure.StandardMetadata;
import pers.younghan.umizoo.messaging.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by young.han with IntelliJ IDEA on 2017-08-01.
 */
public class RepositoryImpl implements Repository {
    private final EventBus eventBus;
    private final Cache cache;
    private final AggregateStorage storage;
    private final EventStore eventStore;
    private final SnapshotStore snapshotStore;

    @Inject
    public RepositoryImpl(EventStore eventStore, SnapshotStore snapshotStore, EventBus eventBus, Cache cache, AggregateStorage storage) {
        this.eventBus = eventBus;
        this.cache = cache;
        this.eventStore = eventStore;
        this.snapshotStore = snapshotStore;
        this.storage = storage;
    }

    private EventSourced create(Class sourceType, Object sourceId) throws Exception {
        Class<?> idType = sourceId.getClass();
        Constructor<?> constructor = sourceType.getConstructor(idType);

        if(constructor == null) {
            String errorMessage = String.format("Type '{0}' must have a constructor with the following signature: .ctor({1} id)", sourceType.getName(), idType.getName());
            throw new RuntimeException(errorMessage);
        }

        return (EventSourced)constructor.newInstance(sourceId);
    }

    private EventSourced restore(Class aggregateRootType, Object aggregateRootId) throws Exception {
        EventSourced eventSourced = null;
        int startVersion = 0;

        try {
            Object snapshot = this.snapshotStore.getLatest(aggregateRootType, aggregateRootId);
            if(snapshot instanceof EventSourced) {
                eventSourced = (EventSourced)snapshot;
                startVersion = eventSourced.getVersion();
            }
            if(Objects.nonNull(eventSourced)) {
                if(LogManager.getDefault().isDebugEnabled()) {
                    LogManager.getDefault().debug("Find the aggregate root '{0}' of id '{1}' from snapshot.", aggregateRootType.getName(), aggregateRootId);
                }
            }
        }
        catch(Exception ex) {
            LogManager.getDefault().warn(ex, "Get the latest snapshot failed. aggregateRootId:{0},aggregateRootType:{1}.", aggregateRootId, aggregateRootType.getName());
        }


        Collection<VersionedEvent> events = this.eventStore.findAll(new SourceInfo(aggregateRootId, aggregateRootType), startVersion);
        if(!events.isEmpty()) {
            if(Objects.isNull(eventSourced)) {
                eventSourced = this.create(aggregateRootType, aggregateRootId);
            }
            eventSourced.loadFrom(events);

            if(LogManager.getDefault().isDebugEnabled()) {
                LogManager.getDefault().debug("Restore the aggregate root '{0}' of id '{1}' from event stream. version:{2} ~ {3}",
                        aggregateRootType.getName(), aggregateRootId,
                        events.stream().min(Comparator.comparingInt(VersionedEvent::getVersion)).get().getVersion(),
                        events.stream().max(Comparator.comparingInt(VersionedEvent::getVersion)).get().getVersion());
            }
        }

        if(Objects.nonNull(eventSourced)) {
            this.cache.set(eventSourced, eventSourced.getId());
        }

        return eventSourced;
    }

    @Override
    public <T extends AggregateRoot> T find(Class<T> aggregateRootType, String aggregateRootId) {
        Verify.verifyNotNull(aggregateRootType);
        Verify.verifyNotNull(aggregateRootId);

        if(aggregateRootType.isInterface() || Modifier.isAbstract(aggregateRootType.getModifiers())) {
            String errorMessage = String.format("the type of '{0}' must be a non abstract class.", aggregateRootType.getName());
            throw new IllegalArgumentException(errorMessage);
        }

        T aggregateRoot = this.cache.get(aggregateRootType, aggregateRootId);

        if(Objects.nonNull(aggregateRoot)) {
            if(LogManager.getDefault().isDebugEnabled()) {
                LogManager.getDefault().debug("Find the aggregate root {0} of id {1} from cache.", aggregateRootType.getName(), aggregateRootId);
            }

            return aggregateRoot;
        }

        if(EventSourced.class.isAssignableFrom(aggregateRootType)) {
            try {
                aggregateRoot = (T)this.restore(aggregateRootType, aggregateRootId);
            }
            catch(Exception ex) {
                //TODO
                aggregateRoot = null;
            }
        }

        if(Objects.isNull(aggregateRoot)) {
            aggregateRoot = storage.get(aggregateRootType, aggregateRootId);
        }

        return aggregateRoot;
    }

    @Override
    public void save(AggregateRoot aggregateRoot) {
        Verify.verifyNotNull(aggregateRoot);

        this.storage.save(aggregateRoot);

        this.cache.set(aggregateRoot, aggregateRoot.getId());

        if(!(aggregateRoot instanceof EventPublisher)) {
            return;
        }

        EventPublisher eventPublisher = (EventPublisher)aggregateRoot;

        SourceInfo sourceInfo = new SourceInfo(aggregateRoot.getId(), aggregateRoot.getClass());
        Collection<Envelope<Event>> envelopes = eventPublisher.getEvents().stream().map(event -> {
            Envelope<Event> envelope = new Envelope<>(event, ObjectId.get().toString());
            envelope.items().put(StandardMetadata.SourceInfo, sourceInfo);

            return envelope;
        }).collect(Collectors.toList());


        eventBus.send(envelopes);
    }

    @Override
    public void save(EventSourced eventSourced, Envelope<Command> command) {
        Verify.verifyNotNull(eventSourced);
        Verify.verifyNotNull(command);

        Class aggregateRootType = eventSourced.getClass();
        String aggregateRootId = eventSourced.getId();

        SourceInfo sourceInfo = new SourceInfo(aggregateRootId, aggregateRootType);
        Collection<VersionedEvent> changedEvents = eventSourced.getChanges();

        boolean saved = false;
        try {
            if(this.eventStore.save(sourceInfo, changedEvents, command.id())) {
                if(LogManager.getDefault().isDebugEnabled()) {
                    LogManager.getDefault().debug("Persistent domain events successfully. aggregateRootType:%s,aggregateRootId:%s,version:%s~%s.",
                            aggregateRootType.getName(), aggregateRootId,
                            changedEvents.stream().min(Comparator.comparingInt(VersionedEvent::getVersion)).get().getVersion(),
                            changedEvents.stream().max(Comparator.comparingInt(VersionedEvent::getVersion)).get().getVersion());
                }

                saved = true;
            }
        }
        catch(Exception ex) {
            LogManager.getDefault().error(ex, "Persistent domain events failed. aggregateRootType:%s,aggregateRootId:%s,version:%s~%s.",
                    aggregateRootType.getName(), aggregateRootId,
                    changedEvents.stream().min(Comparator.comparingInt(VersionedEvent::getVersion)).get().getVersion(),
                    changedEvents.stream().max(Comparator.comparingInt(VersionedEvent::getVersion)).get().getVersion());
            throw ex;
        }

        if(!saved) { //由该命令产生的事件如果已保存，则取出之前的事件重新发送
            changedEvents = this.eventStore.find(sourceInfo, command.id());

            this.eventBus.publish(sourceInfo, changedEvents, command);
            return;
        }

        try {
            this.cache.set(eventSourced, aggregateRootId);
        }
        catch(Exception ex) {
            LogManager.getDefault().warn(ex,
                    "Failed to refresh aggregate root to memory cache. aggregateRootType:%s,aggregateRootId:%s,commandId:%s.",
                    aggregateRootId, aggregateRootType.getName(), command.id());
        }

        this.eventBus.publish(sourceInfo, changedEvents, command);
        if(LogManager.getDefault().isDebugEnabled()) {
            LogManager.getDefault().debug("Publish domain events successfully. aggregateRootType:%s,aggregateRootId:%s.", aggregateRootType.getName(), aggregateRootId);
        }
    }

    @Override
    public void delete(AggregateRoot aggregateRoot) {
        Verify.verifyNotNull(aggregateRoot);

        this.storage.delete(aggregateRoot);

        //Class aggregateRootType = aggregateRoot.getClass();
        SourceInfo sourceInfo = new SourceInfo(aggregateRoot.getId(), aggregateRoot.getClass());
        this.eventStore.delete(sourceInfo);
        this.snapshotStore.delete(sourceInfo);
    }
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import com.google.inject.Inject;
import pers.younghan.umizoo.common.LogManager;
import pers.younghan.umizoo.common.composition.ObjectContainer;
import pers.younghan.umizoo.configurations.ProcessingFlags;
import pers.younghan.umizoo.infrastructure.Initializer;
import pers.younghan.umizoo.infrastructure.StandardMetadata;
import pers.younghan.umizoo.infrastructure.TextSerializer;
import pers.younghan.umizoo.infrastructure.TypeUtils;
import pers.younghan.umizoo.messaging.*;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-31.
 */
public class EventConsumer extends MessageConsumer<Event> implements Initializer {
    private final CommandBus commandBus;
    private final EventBus eventBus;
    private final ResultBus resultBus;
    private final EventPublishedVersionStore publishedVersionStore;
    private final TextSerializer serializer;
    private HashMap<Type, EventHandler> eventHandlers;

    @Inject
    public EventConsumer(CommandBus commandBus,
                         EventBus eventBus,
                         ResultBus resultBus,
                         EventPublishedVersionStore publishedVersionStore,
                         TextSerializer serializer,
                         MessageReceiver<Envelope<Event>> eventReceiver) {
        super(eventReceiver, ProcessingFlags.Event);

        this.publishedVersionStore = publishedVersionStore;
        this.resultBus = resultBus;
        this.commandBus = commandBus;
        this.eventBus = eventBus;
        this.serializer = serializer;

        this.eventHandlers = new HashMap<>();
    }

    @Override
    public void initialize(ObjectContainer container, Collection<Class<?>> types) {
        types.stream().filter(TypeUtils::isVersionEvent).forEach(eventType -> {
            ParameterizedType eventHandlerType = ParameterizedTypeImpl.make(EventHandler.class, new Type[]{eventType}, null);

            List<EventHandler> handlers = container.resolveAll(eventHandlerType).stream().map(this::cast).filter(Objects::nonNull).collect(Collectors.toList());

            switch(handlers.size()) {
                case 0:
                    break;
                case 1:
                    this.eventHandlers.put(eventType, handlers.get(0));
                    break;
                default:
                    throw new RuntimeException(String.format("Found more than one handler for this type('%s') with EventHandler<>.", eventType.getName()));
            }

            this.initialize(container, eventType);
        });
    }

    private EventHandler cast(Object object) {
        if(object instanceof EventHandler) {
            return (EventHandler)object;
        }

        return null;
    }

    @Override
    protected void onMessageArrived(Envelope<Event> envelope) throws Exception {
        EventHandler eventHandler = this.eventHandlers.get(envelope.body().getClass());
        if(Objects.nonNull(eventHandler)) {
            if(!this.processEvent(eventHandler, (VersionedEvent)envelope.body(), envelope.id(), envelope.items())) {
                return;
            }
        }

        super.onMessageArrived(envelope);
    }

    private boolean processEvent(EventHandler eventHandler, VersionedEvent event, String eventId, Map<String, Object> items) {
        SourceInfo sourceInfo = (SourceInfo)items.get(StandardMetadata.SourceInfo);

        if(event.getVersion() > 1) {
            int lastPublishedVersion = this.publishedVersionStore.getPublishedVersion(sourceInfo) + 1;
            if(lastPublishedVersion < event.getVersion()) {
                eventBus.send(new Envelope<>(event, eventId, items));

                if(LogManager.getDefault().isDebugEnabled()) {
                    LogManager.getDefault().debug("The event cannot be process now as the version is not the next version, it will be handle later. aggregateRootType=%s,aggregateRootId=%s,lastPublishedVersion=%s,eventVersion=%s,eventType=%s.",
                            sourceInfo.getSourceType().getName(), sourceInfo.getSourceId(), lastPublishedVersion, event.getVersion(), event.getClass().getName());
                }

                return false;
            }

            if(lastPublishedVersion > event.getVersion()) {
                if(LogManager.getDefault().isDebugEnabled()) {
                    LogManager.getDefault().debug("The event is ignored because it is obsoleted. aggregateRootType=%s,aggregateRootId=%s,lastPublishedVersion=%s,eventVersion=%s,eventType=%s.",
                            sourceInfo.getSourceType().getName(), sourceInfo.getSourceId(), lastPublishedVersion, event.getVersion(), event.getClass().getName());
                }

                return false;
            }
        }

        try {
            this.tryMultipleInvoke(this::invokeHandler, eventHandler, new Envelope<>(event, eventId, items));
        }
        catch(Exception ex) {
            CommandResultGenerated commandResult = new CommandResultGenerated(HandleStatus.SyncFailed, ex.getMessage());
            commandResult.setReplyMode(CommandReturnMode.EventHandled);
            TraceInfo traceInfo = (TraceInfo)items.get(StandardMetadata.TraceInfo);
            this.resultBus.send(commandResult, traceInfo);
        }

        this.publishedVersionStore.addOrUpdatePublishedVersion(sourceInfo, event.getVersion());

        return true;
    }

    private void invokeHandler(EventHandler eventHandler, Envelope<VersionedEvent> envelope) {
        EventContextImpl context = new EventContextImpl(this.commandBus, this.resultBus);
        context.setSourceInfo((SourceInfo)envelope.items().get(StandardMetadata.SourceInfo));
        context.setTraceInfo((TraceInfo)envelope.items().get(StandardMetadata.TraceInfo));
        context.setCommandInfo((SourceInfo)envelope.items().get(StandardMetadata.CommandInfo));

        eventHandler.handle(context, envelope.body());
        context.commit();
    }
}

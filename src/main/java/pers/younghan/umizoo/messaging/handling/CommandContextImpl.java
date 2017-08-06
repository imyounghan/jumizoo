/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import com.google.common.base.Verify;
import pers.younghan.umizoo.common.LogManager;
import pers.younghan.umizoo.infrastructure.*;
import pers.younghan.umizoo.messaging.*;
import pers.younghan.umizoo.seeds.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class CommandContextImpl implements CommandContext {
    private final Hashtable<String, AggregateRoot> trackingAggregateRoots;
    private final ResultBus resultBus;
    private final Repository repository;

    private boolean replied;
    private Envelope<Command> command;

    public CommandContextImpl(ResultBus resultBus, Repository repository, Envelope<Command> command) {
        this.resultBus = resultBus;
        this.repository = repository;
        this.command = command;

        this.trackingAggregateRoots = new Hashtable<>();
    }

    public Collection<AggregateRoot> getTrackingOjbects() {
        return Collections.unmodifiableCollection(trackingAggregateRoots.values());
    }

    @Override
    public String getCommandId() {
        return this.command.id();
    }

    public Command getCommand() {
        return this.command.body();
    }

    public TraceInfo getTraceInfo() {
        return (TraceInfo)this.command.items().get(StandardMetadata.TraceInfo);
    }

    @Override
    public void add(AggregateRoot aggregateRoot) {
        Verify.verifyNotNull(aggregateRoot);

        String key = aggregateRoot.getClass().getName().concat("@").concat(aggregateRoot.getId());
        this.trackingAggregateRoots.putIfAbsent(key, aggregateRoot);
    }

    @Override
    public <TAggregateRoot extends AggregateRoot> TAggregateRoot find(Class<TAggregateRoot> type, Object id) {
        Verify.verifyNotNull(type);
        Verify.verifyNotNull(id);

        String key = type.getName().concat("@").concat(id.toString());
        AggregateRoot aggregateRoot = this.trackingAggregateRoots.get(key);
        if(aggregateRoot == null) {
            aggregateRoot = this.repository.find(type, id.toString());

            if(aggregateRoot != null) {
                this.trackingAggregateRoots.putIfAbsent(key, aggregateRoot);
            }
        }

        return (TAggregateRoot)aggregateRoot;
    }

    public void commit() {
        int dirtyAggregateRootCount = 0;
        EventSourced dirtyAggregateRoot = null;
        Collection<VersionedEvent> changedEvents = Collections.EMPTY_LIST;

        for(AggregateRoot aggregateRoot : this.trackingAggregateRoots.values()) {
            if(aggregateRoot instanceof EventSourced) {
                dirtyAggregateRoot = (EventSourced)aggregateRoot;
                changedEvents = dirtyAggregateRoot.getChanges();
                if(!changedEvents.isEmpty()) {
                    dirtyAggregateRootCount++;
                }
            }
        }

        if(dirtyAggregateRootCount == 0) {
            LogManager.getDefault().warn("Not found aggregate to be created or modified by command. commandType:%s,commandId:%s.",
                    this.getCommand().getClass().getName(), this.getCommandId());
            CommandResultGenerated commandResult = new CommandResultGenerated(HandleStatus.Nothing, "Not found aggregate to be created or modified.");
            commandResult.setReplyMode(CommandReturnMode.CommandExecuted);
            this.resultBus.send(commandResult, this.getTraceInfo());
            return;
        }

        if(dirtyAggregateRootCount > 1) {
            LogManager.getDefault().error("Detected more than one aggregate created or modified by command. commandType:%s,commandId:%s.",
                    this.getCommand().getClass().getName(), this.getCommandId());

            CommandResultGenerated commandResult = new CommandResultGenerated(HandleStatus.Failed, "Detected more than one aggregate created or modified.");
            commandResult.setReplyMode(CommandReturnMode.CommandExecuted);
            this.resultBus.send(commandResult, this.getTraceInfo());
            return;
            //throw new RuntimeException(errorMessage);
        }
        this.repository.save(dirtyAggregateRoot, command);

        if(replied) {
            return;
        }
        CommandResultGenerated commandResult = new CommandResultGenerated(CommandReturnMode.CommandExecuted);
        commandResult.producedEventCount(changedEvents.size());
        this.resultBus.send(commandResult, this.getTraceInfo());
    }

    @Override
    public void complete(Object result, Function<Object, String> serializer) {
        if(this.replied) {
            return;
        }

        this.replied = true;

        CommandResultGenerated commandResult = (CommandResultGenerated)CommandResultGenerated.FINISHED;
        if(!Objects.isNull(result)) {
            commandResult = new CommandResultGenerated(CommandReturnMode.Manual);
            if(!Objects.isNull(serializer)) {
                commandResult.setResult(serializer.apply(result));
            }
        }

        this.resultBus.send(commandResult, this.getTraceInfo());
    }
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import pers.younghan.umizoo.messaging.*;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Function;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-31.
 */
public class EventContextImpl implements EventContext {

    private final ArrayList<Command> commands;
    private final CommandBus commandBus;
    private final ResultBus resultBus;

    private boolean replied;
    private SourceInfo sourceInfo;
    private SourceInfo commandInfo;
    private TraceInfo traceInfo;

    public EventContextImpl(CommandBus commandBus, ResultBus resultBus) {
        this.commandBus = commandBus;
        this.resultBus = resultBus;

        this.commands = new ArrayList<>();
    }

    public void setSourceInfo(SourceInfo sourceInfo) {
        this.sourceInfo = sourceInfo;
    }

    public void setCommandInfo(SourceInfo commandInfo) {
        this.commandInfo = commandInfo;
    }

    public void setTraceInfo(TraceInfo traceInfo) {
        this.traceInfo = traceInfo;
    }

    @Override
    public SourceInfo getSourceInfo() {
        return this.sourceInfo;
    }

    @Override
    public SourceInfo getCommandInfo() {
        return this.commandInfo;
    }

    @Override
    public void addCommand(Command command) {
        this.commands.add(command);
    }

    public void commit() {
        if(!this.commands.isEmpty()) {
            commandBus.send(this.commands, traceInfo);
            return;
        }

        if(!this.replied) {
            resultBus.send(CommandResultGenerated.EVENT_HANDLED, this.traceInfo);
        }
    }

    @Override
    public void notifyCommandCompleted(Object result, Function<Object, String> serializer) {
        if(replied) {
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

        resultBus.send(commandResult, this.traceInfo);
    }
}

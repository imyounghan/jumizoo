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
import pers.younghan.umizoo.infrastructure.*;
import pers.younghan.umizoo.messaging.*;
import pers.younghan.umizoo.seeds.Repository;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class CommandConsumer extends MessageConsumer<Command> implements Initializer {

    private HashMap<Type, Handler> commandHandlers;
    private PublishableExceptionBus exceptionBus;
    private Repository repository;
    private ResultBus resultBus;

    @Inject
    public CommandConsumer(PublishableExceptionBus exceptionBus, ResultBus resultBus, Repository repository, MessageReceiver<Envelope<Command>> commandReceiver) {
        super(commandReceiver, CheckHandlerMode.OnlyOne, ProcessingFlags.Command);

        this.exceptionBus = exceptionBus;
        this.resultBus = resultBus;
        this.repository = repository;

        this.commandHandlers = new HashMap<>();
    }

    @Override
    public void initialize(ObjectContainer container, Collection<Class<?>> types) {
        types.stream().filter(TypeUtils::isCommand).forEach(commandType -> {
            ParameterizedType commandHandlerType = ParameterizedTypeImpl.make(CommandHandler.class, new Type[]{commandType}, null);

            List<CommandHandler> handlers = container.resolveAll(commandHandlerType).stream().map(this::cast).filter(Objects::nonNull).collect(Collectors.toList());

            switch(handlers.size()) {
                case 0:
                    break;
                case 1:
                    this.commandHandlers.put(commandType, handlers.get(0));
                    return;
                default:
                    throw new RuntimeException(String.format("Found more than one handler for this type('%s') with CommandHandler<>.", commandType.getName()));
            }

            this.initialize(container, commandType);
        });
    }

    private CommandHandler cast(Object object) {
        if(object instanceof CommandHandler) {
            return (CommandHandler)object;
        }

        return null;
    }

    @Override
    protected void onMessageArrived(Envelope<Command> envelope) throws Exception {
        Class commandType = envelope.body().getClass();

        TraceInfo traceInfo = (TraceInfo)envelope.items().get(StandardMetadata.TraceInfo);

        Handler handler = this.commandHandlers.get(commandType);
        if(handler == null) {
            List<Handler> handlers = this.getHandlers(commandType);
            if(handlers.isEmpty()) {
                String errorMessage = String.format("The handler of this type('%s') is not found.", commandType.getName());
                LogManager.getDefault().debug(errorMessage);
                this.notifyResult(traceInfo, new CommandResultGenerated(HandleStatus.Failed, errorMessage));
                return;
            }
            handler = handlers.get(0);
        }

        try {
            Object result = this.processCommand(handler, envelope);
            this.notifyResult(traceInfo, result);
        }
        catch(Exception ex) {
            this.notifyResult(traceInfo, ex);
            throw ex;
        }
    }

    private void invokeCommandHandler(CommandHandler commandHandler, Envelope<Command> envelope) {
        CommandContextImpl context = new CommandContextImpl(this.resultBus, this.repository, envelope);
        commandHandler.handle(context, envelope.body());

        context.commit();
    }

    private Object processCommand(Handler handler, Envelope<Command> envelope) throws InterruptedException {
        if(handler instanceof CommandHandler) {
            CommandHandler commandHandler = (CommandHandler)handler;
            this.tryMultipleInvoke(this::invokeCommandHandler, commandHandler, envelope);
            return null;
        }

        if(handler instanceof EnvelopedMessageHandler) {
            EnvelopedMessageHandler<Command> envelopeHandler = (EnvelopedMessageHandler<Command>)handler;
            this.tryMultipleInvoke(this::invokeHandler, envelopeHandler, envelope);
        }
        else if(handler instanceof MessageHandler) {
            MessageHandler<Command> messageHandler = (MessageHandler<Command>)handler;
            this.tryMultipleInvoke(this::invokeHandler, messageHandler, envelope.body());
        }
        return CommandResultGenerated.COMMAND_EXECUTED;
    }

    private void notifyResult(TraceInfo traceInfo, Object result) {
        if(result instanceof CommandResult) {
            CommandResult commandResult = (CommandResult)result;

            this.resultBus.send(commandResult, traceInfo);
            return;
        }

        if(result instanceof PublishableException) {
            PublishableException publishableException = (PublishableException)result;

            this.exceptionBus.publish(publishableException);

            CommandResultGenerated commandResult = new CommandResultGenerated(HandleStatus.Failed, publishableException.getMessage(), publishableException.getCode());
            commandResult.setReplyMode(CommandReturnMode.CommandExecuted);
            this.resultBus.send(commandResult, traceInfo);
            return;
        }

        if(result instanceof Exception) {
            Exception ex = (Exception)result;

            CommandResultGenerated commandResult = new CommandResultGenerated(HandleStatus.Failed, ex.getMessage());
            commandResult.setReplyMode(CommandReturnMode.CommandExecuted);
            this.resultBus.send(commandResult, traceInfo);
        }
    }
}

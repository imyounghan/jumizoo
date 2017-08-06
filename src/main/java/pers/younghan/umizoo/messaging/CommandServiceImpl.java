/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import com.google.inject.Inject;
import pers.younghan.umizoo.common.LogManager;
import pers.younghan.umizoo.common.ObjectId;
import pers.younghan.umizoo.configurations.ConfigurationSettings;

import java.util.concurrent.CompletableFuture;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public class CommandServiceImpl implements CommandService {
    private final ResultManager resultManger;
    private final CommandBus commandBus;

    @Inject
    public CommandServiceImpl(ResultManager resultManger, CommandBus commandBus) {
        this.resultManger = resultManger;
        this.commandBus = commandBus;
    }

    @Override
    public CompletableFuture<CommandResult> executeAsync(final Command command, final CommandReturnMode returnMode, final int timeoutMs) {
        String commandId = ObjectId.get().toString();
        CompletableFuture<CommandResult> future = resultManger.registerProcessingCommand(commandId, command, returnMode, timeoutMs);

        CompletableFuture.runAsync(() -> {
            try {
                commandBus.send(command, new TraceInfo(commandId, ConfigurationSettings.InnerAddress));
                resultManger.setCommandResult(commandId, CommandResultGenerated.SENT_DELIVERED);
            }
            catch(Exception ex) {
                LogManager.getDefault().error(ex.getMessage());
                resultManger.setCommandResult(commandId, CommandResultGenerated.SENT_FAILED);
            }
        });

        return future;
    }

    @Override
    public CommandResult execute(Command command, CommandReturnMode returnMode, int timeoutMs) {
        try {
            return this.executeAsync(command, returnMode, timeoutMs).get();
        }
        catch(Exception exception) {
            return new CommandResultGenerated(HandleStatus.Failed, exception.getMessage());
        }
    }
}

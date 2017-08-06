/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import java.util.concurrent.CompletableFuture;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface CommandService {

    default CompletableFuture<CommandResult> executeAsync(Command command){
        return this.executeAsync(command, CommandReturnMode.CommandExecuted, 120000);
    }

    default CompletableFuture<CommandResult> executeAsync(Command command, CommandReturnMode returnMode){
        return this.executeAsync(command, returnMode, 120000);
    }

    default CompletableFuture<CommandResult> executeAsync(Command command, int timeoutMs){
        return this.executeAsync(command, CommandReturnMode.CommandExecuted, timeoutMs);
    }

    CompletableFuture<CommandResult> executeAsync(final Command command, final CommandReturnMode returnMode, final int timeoutMs);


    default CommandResult execute(Command command){
        return this.execute(command, CommandReturnMode.CommandExecuted, 120000);
    }

    default CommandResult execute(Command command, CommandReturnMode returnMode){
        return this.execute(command, returnMode, 120000);
    }

    default CommandResult execute(Command command, int timeoutMs){
        return this.execute(command, CommandReturnMode.CommandExecuted, timeoutMs);
    }

    CommandResult execute(final Command command, final CommandReturnMode returnMode, final int timeoutMs);
}
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
public interface ResultManager {
    CompletableFuture<CommandResult> registerProcessingCommand(String commandId, Command command, CommandReturnMode commandReturnMode, int timeoutMs);

    boolean setCommandResult(String commandId, CommandResult commandResult);

    CompletableFuture<QueryResult> registerProcessingQuery(String queryId, Query query, int timeoutMs);

    boolean setQueryResult(String queryId, QueryResult queryResult);

    int getWaitingCommands();

    int getWaitingQueries();
}
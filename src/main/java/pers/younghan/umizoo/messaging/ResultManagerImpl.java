/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */
package pers.younghan.umizoo.messaging;

import pers.younghan.umizoo.infrastructure.SingleEntryGate;

import java.security.Key;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class ResultManagerImpl implements ResultManager {
    private final ConcurrentMap<String, CommandTaskCompletionSource> commandTaskMap;
    private final ConcurrentMap<String, QueryTaskCompletionSource> queryTaskMap;

    public ResultManagerImpl() {

        this.commandTaskMap = new ConcurrentHashMap<>();
        this.queryTaskMap = new ConcurrentHashMap<>();
    }

    private void removeCommandTask(String commandId) {
        commandTaskMap.remove(commandId);
    }

    @Override
    public CompletableFuture<CommandResult> registerProcessingCommand(String commandId, Command command, CommandReturnMode commandReturnMode, int timeoutMs) {
        CommandTaskCompletionSource taskCompletionSource = new CommandTaskCompletionSource(commandReturnMode, timeoutMs, commandId, this::removeCommandTask);

        if (Objects.nonNull(commandTaskMap.put(commandId, taskCompletionSource))) {
            throw new RuntimeException(String.format("Duplicate processing command registration, type:%s, id:%s.", command.getClass().getName(), commandId));
        }

        return taskCompletionSource.getTask();
    }

    @Override
    public boolean setCommandResult(String commandId, CommandResult commandResult) {
        CommandResultGenerated target = null;
        if (commandResult instanceof CommandResultGenerated) {
            target = (CommandResultGenerated) commandResult;
        }

        if (Objects.nonNull(target)) {
            CommandTaskCompletionSource taskCompletionSource = commandTaskMap.get(commandId);
            if (Objects.nonNull(taskCompletionSource)) {
                return taskCompletionSource.trySetResult(target);
            }
        }

        return false;
    }

    private void removeQueryTask(String queryId) {
        queryTaskMap.remove(queryId);
    }

    @Override
    public CompletableFuture<QueryResult> registerProcessingQuery(String queryId, Query query, int timeoutMs) {
        QueryTaskCompletionSource taskCompletionSource = new QueryTaskCompletionSource(timeoutMs, queryId, this::removeQueryTask);

        if (Objects.nonNull(queryTaskMap.put(queryId, taskCompletionSource))) {
            throw new RuntimeException(String.format("Duplicate processing query registration, type:%s, id:%s", query.getClass().getName(), queryId));
        }

        return taskCompletionSource.getTask();
    }

    @Override
    public boolean setQueryResult(String queryId, QueryResult queryResult) {
        QueryResultBuilt target = null;
        if (queryResult instanceof QueryResultBuilt) {
            target = (QueryResultBuilt) queryResult;
        }

        if (Objects.nonNull(target)) {
            QueryTaskCompletionSource taskCompletionSource = queryTaskMap.get(queryId);
            if (Objects.nonNull(taskCompletionSource)) {
                return taskCompletionSource.trySetResult(target);
            }
        }

        return false;
    }

    @Override
    public int getWaitingCommands() {
        return commandTaskMap.size();
    }

    @Override
    public int getWaitingQueries() {
        return queryTaskMap.size();
    }

    abstract class WrappedTaskCompletionSource<T1, T2 extends T1> {
        private final CompletableFuture<T1> taskCompletionSource;
        private final SingleEntryGate setResultGate;
        private final Consumer<String> callback;

        private final String sourceId;
        private Timer timer;

        protected WrappedTaskCompletionSource(int timeoutMs, String sourceId, Consumer<String> callback) {
            this.taskCompletionSource = new CompletableFuture<>();
            this.setResultGate = new SingleEntryGate();
            this.sourceId = sourceId;
            this.callback = callback;
            if (timeoutMs > 0) {
                this.timer = new Timer();
                this.timer.schedule(new TimerTask() {
                    public void run() {
                        WrappedTaskCompletionSource.this.setTimeoutResult();
                    }
                }, timeoutMs);
            }
        }

        protected abstract T1 getTimeoutResult();

        protected abstract boolean canSetResult(T2 result);

        public boolean trySetResult(T2 result) {
            if (!this.canSetResult(result)) {
                return false;
            }

            if (!setResultGate.tryEnter()) {
                return false;
            }

            if (this.callback != null) {
                this.callback.accept(sourceId);
            }
            if (this.timer != null) {
                this.timer.cancel();
            }

            return taskCompletionSource.complete(result);
        }

        private void setTimeoutResult() {
            if (!setResultGate.tryEnter()) {
                return;
            }

            taskCompletionSource.complete(this.getTimeoutResult());

            if (this.callback != null) {
                callback.accept(sourceId);
            }
            if (this.timer != null) {
                timer.cancel();
            }
        }

        public CompletableFuture<T1> getTask() {
            return this.taskCompletionSource;
        }
    }

    class CommandTaskCompletionSource extends WrappedTaskCompletionSource<CommandResult, CommandResultGenerated> {
        private CommandReturnMode returnMode;
        private int eventCount;


        public CommandTaskCompletionSource(CommandReturnMode returnMode, int timeoutMs, String sourceId, Consumer<String> callback) {
            super(timeoutMs, sourceId, callback);
            this.returnMode = returnMode;
        }

        @Override
        protected CommandResult getTimeoutResult() {
            return CommandResultGenerated.TIMEOUT;
        }

        @Override
        protected boolean canSetResult(CommandResultGenerated result) {
            if (result.getStatus() != HandleStatus.Success || result.getReplyMode() == CommandReturnMode.Manual) {
                return true;
            }

            boolean completed = false;
            CommandReturnMode replyMode = result.getReplyMode();

            if (replyMode == CommandReturnMode.CommandExecuted) {
                completed = this.returnMode == CommandReturnMode.CommandExecuted;
                if (!completed) {
                    this.eventCount = result.producedEventCount();
                }
            }
            else if (replyMode == CommandReturnMode.EventHandled) {
                completed = --this.eventCount <= 0;
            }
            else if (replyMode == CommandReturnMode.Delivered) {
                completed = this.returnMode == CommandReturnMode.Delivered;

            }

            return completed;
        }
    }

    class QueryTaskCompletionSource extends WrappedTaskCompletionSource<QueryResult, QueryResultBuilt> {
        public QueryTaskCompletionSource(int timeoutMs, String sourceId, Consumer<String> callback) {
            super(timeoutMs, sourceId, callback);
        }

        @Override
        protected boolean canSetResult(QueryResultBuilt result) {
            return true;
        }

        @Override
        protected QueryResult getTimeoutResult() {
            return QueryResultBuilt.TIMEOUT;
        }
    }
}

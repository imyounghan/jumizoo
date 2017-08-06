/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.05.
 */
public class CommandResultGenerated extends HandleResult implements CommandResult {
    public static final CommandResult TIMEOUT = new CommandResultGenerated(HandleStatus.Timeout, "Operation is timeout.", "-1");
    public static final CommandResult SENT_FAILED = new CommandResultGenerated(HandleStatus.Failed, "Send to bus failed.");
    public static final CommandResult SENT_DELIVERED = new CommandResultGenerated(CommandReturnMode.Delivered);
    public static final CommandResult COMMAND_EXECUTED = new CommandResultGenerated(CommandReturnMode.CommandExecuted);
    public static final CommandResult EVENT_HANDLED = new CommandResultGenerated(CommandReturnMode.EventHandled);
    public static final CommandResult FINISHED = new CommandResultGenerated(CommandReturnMode.Manual);

    private String errorCode;
    private String result;

    private CommandReturnMode replyMode;
    private int producedEventCount;

    public CommandResultGenerated() {
        this(HandleStatus.Success, null, "-1");
    }

    public CommandResultGenerated(HandleStatus status, String errorMessage) {
        this(status, errorMessage, "-1");
    }

    public CommandResultGenerated(HandleStatus status, String errorMessage, String errorCode) {
        super(status, errorMessage);
        this.errorCode = errorCode;
        if (!status.equals(HandleStatus.Success)) {
            this.replyMode = CommandReturnMode.Manual;
        }
    }

    public CommandResultGenerated(CommandReturnMode replyMode) {
        this();
        this.replyMode = replyMode;
    }

    @Override
    public String getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getResult() {
        return this.result;
    }

    public CommandReturnMode getReplyMode() {
        return this.replyMode;
    }

    public void setReplyMode(CommandReturnMode replyMode) {
        this.replyMode = replyMode;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int producedEventCount() {
        return this.producedEventCount;
    }

    public void producedEventCount(int count) {
        this.producedEventCount = count;
    }
}

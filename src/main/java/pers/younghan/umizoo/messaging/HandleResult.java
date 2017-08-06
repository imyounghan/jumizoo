/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import pers.younghan.umizoo.configurations.ConfigurationSettings;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public abstract class HandleResult {
    private HandleStatus status;
    private String errorMessage;
    private String replyServer;

    public HandleResult() {
        this(HandleStatus.Success, null);
    }

    public HandleResult(HandleStatus status, String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;

        this.replyServer = String.format("%s@%s", ConfigurationSettings.InnerAddress, ConfigurationSettings.ServiceName);
    }

    public HandleStatus getStatus() {
        return this.status;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public String getReplyServer() {
        return this.replyServer;
    }
}

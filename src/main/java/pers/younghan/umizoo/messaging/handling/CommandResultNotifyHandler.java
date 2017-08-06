/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import com.google.inject.Inject;
import pers.younghan.umizoo.common.LogManager;
import pers.younghan.umizoo.messaging.CommandResultGenerated;
import pers.younghan.umizoo.messaging.Envelope;
import pers.younghan.umizoo.messaging.ResultManager;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public class CommandResultNotifyHandler implements EnvelopedMessageHandler<CommandResultGenerated> {
    private final ResultManager resultManager;

    @Inject
    public CommandResultNotifyHandler(ResultManager resultManager) {
        this.resultManager = resultManager;
    }

    @Override
    public void handle(Envelope<CommandResultGenerated> envelope) {
        if(resultManager.setCommandResult(envelope.id(), envelope.body())) {
            if(LogManager.getDefault().isDebugEnabled()) {
                LogManager.getDefault().debug("Command(%s) is completed.", envelope.id());
            }
        }
    }
}

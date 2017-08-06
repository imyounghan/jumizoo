/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import pers.younghan.umizoo.messaging.Command;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface CommandHandler<TCommand extends Command> extends Handler  {
    void handle(CommandContext context, TCommand command);
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.samples.commands;

import com.google.inject.Inject;
import pers.younghan.umizoo.messaging.handling.CommandContext;
import pers.younghan.umizoo.messaging.handling.CommandHandler;
import pers.younghan.umizoo.samples.UniqueLoginNameService;
import pers.younghan.umizoo.samples.User;
import pers.younghan.umizoo.samples.UserRegisterService;

public class RegisterUserHandler implements CommandHandler<RegisterUser> {
    private final UniqueLoginNameService uniqueService;

    @Inject
    public RegisterUserHandler(UniqueLoginNameService uniqueService)
    {
        this.uniqueService = uniqueService;
    }

    @Override
    public void handle(CommandContext context, RegisterUser command) {
        User user = new UserRegisterService(uniqueService, context.getCommandId())
                .register(command.getLoginId(), command.getName(), command.getEmail());
        context.add(user);
    }
}

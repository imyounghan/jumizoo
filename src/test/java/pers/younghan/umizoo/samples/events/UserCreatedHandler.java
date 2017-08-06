/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.samples.events;

import com.google.inject.Inject;
import pers.younghan.umizoo.messaging.handling.EventContext;
import pers.younghan.umizoo.messaging.handling.EventHandler;
import pers.younghan.umizoo.samples.readmodels.UserDao;
import pers.younghan.umizoo.samples.readmodels.UserModel;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.04.
 */
public class UserCreatedHandler implements EventHandler<UserCreated> {
    private final UserDao userDao;

    @Inject
    public UserCreatedHandler(UserDao userDao)
    {
        this.userDao = userDao;
    }

    @Override
    public void handle(EventContext context, UserCreated event) {
        UserModel model = new UserModel();
        model.setLoginId(event.getLoginId());
        model.setUsername(event.getName());
        model.setEmail(event.getEmail());

        userDao.save(model);
    }
}

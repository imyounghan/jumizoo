/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.samples;

import pers.younghan.umizoo.common.ObjectId;
import pers.younghan.umizoo.samples.events.UserCreated;
import pers.younghan.umizoo.seeds.AbstractEventSourced;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.04.
 */
public class User extends AbstractEventSourced<String> {
    private String loginId;
    //private String password;
    private String userName;
    private String email;

    public User(String id)
    {
        super(id);
    }

    public User(String loginId, String userName, String email)
    {
        this(ObjectId.get().toString());
        UserCreated userCreated = new UserCreated(loginId, userName, email);

        raiseEvent(userCreated);
    }

    private void handle(UserCreated event)
    {
        this.loginId = event.getLoginId();
        this.userName = event.getName();
        this.email = event.getName();
    }

    public String getLoginId() {
        return loginId;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }
}

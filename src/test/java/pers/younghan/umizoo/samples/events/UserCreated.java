/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.samples.events;

import pers.younghan.umizoo.messaging.VersionedEventBase;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.04.
 */
public class UserCreated extends VersionedEventBase {
    private String loginId;
    private String name;
    private String email;

    public  UserCreated(String loginId, String name, String email){
        this.loginId = loginId;
        this.name = name;
        this.email = email;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

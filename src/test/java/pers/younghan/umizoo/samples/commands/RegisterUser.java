/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.samples.commands;

import pers.younghan.umizoo.messaging.Command;

public class RegisterUser implements Command {

    private String loginId;

    private String name;

    private String email;

    public  RegisterUser(String loginId, String name, String email){
        this.loginId = loginId;
        this.name = name;
        this.email = email;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

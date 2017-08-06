/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.samples;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.04.
 */
public class UserRegisterService {
    private final UniqueLoginNameService uniqueService;
    private final String commandId;
    public UserRegisterService(UniqueLoginNameService uniqueService, String commandId)
    {
        this.uniqueService = uniqueService;
        this.commandId = commandId;
    }


    public User register(String loginId,String userName, String email)
    {
        if (!uniqueService.validate(loginId, commandId)) {
            throw new RuntimeException("用户名已存在！");
        }

        return new User(loginId, userName, email);
    }
}

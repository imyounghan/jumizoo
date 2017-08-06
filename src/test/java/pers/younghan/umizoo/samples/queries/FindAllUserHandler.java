/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.samples.queries;


import com.google.inject.Inject;
import pers.younghan.umizoo.messaging.handling.QueryHandler;
import pers.younghan.umizoo.samples.readmodels.UserDao;
import pers.younghan.umizoo.samples.readmodels.UserModel;

import java.util.Collection;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.05.
 */
public class FindAllUserHandler implements QueryHandler<FindAllUser, Collection<UserModel>> {
    private final UserDao dao;

    @Inject
    public FindAllUserHandler(UserDao dao){
        this.dao = dao;
    }

    @Override
    public Collection<UserModel> handle(FindAllUser parameter) {
        return dao.findAll();
    }
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.samples.readmodels;

import pers.younghan.umizoo.common.composition.RegisterAnnotation;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.04.
 */
@RegisterAnnotation(type = UserDao.class)
public class UserDaoImpl implements UserDao {
    HashSet<UserModel> cache = new HashSet<>();

    @Override
    public void save(UserModel user) {
        cache.add(user);
    }

    @Override
    public UserModel find(String loginId) {
        return cache.stream().filter(model -> model.getLoginId().equals(loginId)).findFirst().get();
    }

    @Override
    public Collection<UserModel> findAll() {
        return cache;
    }
}

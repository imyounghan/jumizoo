/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.samples;

import com.google.common.base.Strings;
import pers.younghan.umizoo.common.composition.RegisterAnnotation;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.04.
 */
@RegisterAnnotation(type = UniqueLoginNameService.class)
public class UniqueLoginNameServiceImpl implements UniqueLoginNameService {
    private final ConcurrentMap<String, String> dict = new ConcurrentHashMap();

    @Override
    public boolean validate(String loginName, String correlationId) {
        String commandId = dict.putIfAbsent(loginName, correlationId);
        return Strings.isNullOrEmpty(commandId) || commandId.equals(correlationId);
    }
}

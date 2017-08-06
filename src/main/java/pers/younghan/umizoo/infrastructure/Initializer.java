/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.infrastructure;

import pers.younghan.umizoo.common.composition.ObjectContainer;

import java.util.Collection;

/**
 * 表示继承该接口的是一个需要初始化的程序
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface Initializer {
    void initialize(ObjectContainer container, Collection<Class<?>> types);
}

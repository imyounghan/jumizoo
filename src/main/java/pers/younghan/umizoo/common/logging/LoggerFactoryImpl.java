/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common.logging;


import org.apache.log4j.LogManager;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class LoggerFactoryImpl implements LoggerFactory {

    @Override
    public Logger createLogger(String name) {
        return new LoggerImpl(LogManager.getLogger(name));
    }

    @Override
    public Logger createLogger(Class type) {
        return new LoggerImpl(LogManager.getLogger(type));
    }

}

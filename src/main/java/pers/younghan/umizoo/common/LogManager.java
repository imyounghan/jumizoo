/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common;

import pers.younghan.umizoo.common.logging.Logger;
import pers.younghan.umizoo.common.logging.LoggerFactory;
import pers.younghan.umizoo.common.logging.LoggerFactoryImpl;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public final class LogManager {
    private static HashMap<String, Logger> loggerMap;

    private static LoggerFactory loggerFactory;

    static {
        loggerMap = new HashMap<>();
    }

    private static LoggerFactory getLoggerFactory() {
        if (Objects.isNull(loggerFactory)) {
            synchronized (LogManager.class) {
                if (Objects.isNull(loggerFactory)) {
                    loggerFactory = new LoggerFactoryImpl();
                }
            }
        }
        return loggerFactory;
    }

    public static void setLoggerFactory(Supplier<LoggerFactory> provider){
        if (Objects.isNull(loggerFactory)) {
            synchronized (ServiceLocator.class) {
                if (Objects.isNull(loggerFactory)) {
                    loggerFactory = provider.get();
                }
            }
        }
    }

    public static Logger getDefault() {
        return getLogger("Umizoo");
    }

    public static Logger getLogger(String name) {
        return loggerMap.computeIfAbsent(name, getLoggerFactory()::createLogger);
    }

    public static Logger getLogger(Type type) {

        return loggerMap.computeIfAbsent(type.getTypeName(), getLoggerFactory()::createLogger);
    }
}

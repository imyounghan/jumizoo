/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common.logging;


import org.apache.log4j.Level;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class LoggerImpl implements Logger {
    private final org.apache.log4j.Logger logger;

    public LoggerImpl(org.apache.log4j.Logger logger){
        this.logger = logger;
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }


    @Override
    public void debug(Object message) {
        if(isDebugEnabled()){
            logger.debug(message);
        }
    }

    @Override
    public void debug(Object message, Exception exception) {
        if(isDebugEnabled()){
            logger.debug(message, exception);
        }
    }

    @Override
    public void info(Object message) {
        if(isInfoEnabled()){
            logger.info(message);
        }
    }

    @Override
    public void info(Object message, Exception exception) {
        if(isInfoEnabled()){
            logger.debug(message, exception);
        }
    }

    @Override
    public void warn(Object message) {
        logger.warn(message);
    }

    @Override
    public void warn(Object message, Exception exception) {
        logger.warn(message, exception);
    }

    @Override
    public void error(Object message) {
        logger.error(message);
    }

    @Override
    public void error(Object message, Exception exception) {
        logger.error(message, exception);
    }

    @Override
    public void fatal(Object message) {
        logger.fatal(message);
    }

    @Override
    public void fatal(Object message, Exception exception) {
        logger.fatal(message, exception);
    }
}

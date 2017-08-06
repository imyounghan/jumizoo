/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common.logging;

/**
 * 写日志接口
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface Logger {

    boolean isDebugEnabled();

    boolean isInfoEnabled();

    void debug(Object message);

    void debug(Object message, Exception exception);

    default void debug(Exception exception){
        this.debug(exception.getMessage(), exception);
    }

    default void debug(String format, Object... args) {
        this.debug(String.format(format, args));
    }

    default void debug(Exception exception, String format, Object... args) {
        this.debug(String.format(format, args), exception);
    }


    void info(Object message);

    void info(Object message, Exception exception);

    default void info(String format, Object... args) {
        this.info(String.format(format, args));
    }

    default void info(Exception exception, String format, Object... args) {
        this.info(String.format(format, args), exception);
    }


    void warn(Object message);

    void warn(Object message, Exception exception);

    default void warn(String format, Object... args) {
        this.info(String.format(format, args));
    }

    default void warn(Exception exception, String format, Object... args) {
        this.info(String.format(format, args), exception);
    }


    void error(Object message);

    void error(Object message, Exception exception);

    default void error(String format, Object... args) {
        this.info(String.format(format, args));
    }

    default void error(Exception exception, String format, Object... args) {
        this.info(String.format(format, args), exception);
    }

    void fatal(Object message);

    void fatal(Object message, Exception exception);

    default void fatal(String format, Object... args) {
        this.info(String.format(format, args));
    }

    default void fatal(Exception exception, String format, Object... args) {
        this.info(String.format(format, args), exception);
    }
}

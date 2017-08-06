/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common.logging;

/**
 * 获取写日志程序的工厂接口
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface LoggerFactory {
    Logger createLogger(String name);

    Logger createLogger(Class type);
}

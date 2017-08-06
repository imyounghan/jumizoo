/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import pers.younghan.umizoo.messaging.Command;
import pers.younghan.umizoo.messaging.SourceInfo;

import java.util.function.Function;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface EventContext {
    /**
     * @return 返回聚合根信息
     */
    SourceInfo getSourceInfo();

    /**
     * @return 返回命令信息
     */
    SourceInfo getCommandInfo();

    void addCommand(Command command);

    void notifyCommandCompleted(Object result, Function<Object, String> serializer);
}

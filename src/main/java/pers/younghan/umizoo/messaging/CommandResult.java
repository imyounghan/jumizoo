/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface CommandResult extends Result {
    /**
     * @return 获取错误编码
     */
    String getErrorCode();

    /**
     * @return 处理结果
     */
    String getResult();
}

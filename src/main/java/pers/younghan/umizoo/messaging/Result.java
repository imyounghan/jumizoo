/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface Result extends Message {
    /**
     * 获取状态
     * @return
     */
    HandleStatus getStatus();

    /**
     * 获取异常消息
     * @return
     */
    String getErrorMessage();
}

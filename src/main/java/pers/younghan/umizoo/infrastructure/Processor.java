/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.infrastructure;

/**
 * 表示这是一个任务
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface Processor {
    /**
     * 启动任务
      */
    void start();

    /**
     * 停止任务
     */
    void stop();
}

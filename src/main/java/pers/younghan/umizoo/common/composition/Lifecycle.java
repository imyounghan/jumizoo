/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common.composition;


/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public enum Lifecycle {
    /**
     * 单例
     */
    Singleton,
    /**
     * 每次都创建一个新实例
     */
    Transient
}

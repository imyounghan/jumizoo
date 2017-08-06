/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.05.
 */
public interface QueryResult extends Result {
    Object getData();
}

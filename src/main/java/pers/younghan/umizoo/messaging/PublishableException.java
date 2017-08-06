/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import java.io.Serializable;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public interface PublishableException extends Message, Serializable {
    String getMessage();

    String getCode();
}

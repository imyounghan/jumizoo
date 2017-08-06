/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.configurations;

/**
 * Created by young.han with IntelliJ IDEA on 2017-08-01.
 */
public enum ConnectionMode {
    Local(0),

    Socket(1),

    Rmi(2);

    private int value;
    ConnectionMode(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}

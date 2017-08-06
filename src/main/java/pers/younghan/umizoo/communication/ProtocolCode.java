/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.communication;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public enum  ProtocolCode {
    Command(1),
    Query(2),
    Notify(3);

    private int value;
    ProtocolCode(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public enum HandleStatus {
    Success(0),
    Failed(1),
    SyncFailed(2),
    Nothing(3),
    Timeout(4);

    private int value;
    HandleStatus(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}

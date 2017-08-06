/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public enum CommandReturnMode {
    /**
     * 表示命令发送成功就返回
     */
    Delivered(0),
    /**
     * 表示命令执行完成
     */
    CommandExecuted(1),
    /**
     * 表示由命令引发的事件处理完成
     */
    EventHandled(2),
    /**
     * 表示需要手动回复
     */
    Manual(3);

    private int value;
    CommandReturnMode(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }

    public static CommandReturnMode valueOf(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case 1:
                return CommandExecuted;
            case 2:
                return EventHandled;
            case 3:
                return Manual;
            default:
                return Delivered;
        }
    }
}
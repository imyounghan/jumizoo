/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common.socketing;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.05.
 */
public class SocketSetting {
    public int SendBufferSize = 1024 * 16;
    public int ReceiveBufferSize = 1024 * 16;

    public int MaxSendPacketSize = 1024 * 64;
    public int SendMessageFlowControlThreshold = 1000;
    public int SendMessageFlowControlStepPercent = 1;
    public int SendMessageFlowControlWaitMilliseconds = 1;

    public int ReceiveDataBufferSize = 1024 * 16;
    public int ReceiveDataBufferPoolSize = 50;
}

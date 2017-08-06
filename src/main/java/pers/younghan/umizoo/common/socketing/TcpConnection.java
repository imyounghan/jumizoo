/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common.socketing;

import java.net.SocketAddress;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.05.
 */
public interface TcpConnection {
    boolean isConnected();

    SocketAddress getLocalAddress();

    SocketAddress getRemotingAddress();

    void queueMessage(byte[] message);

    void close();
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common.socketing;


/**
 * Created by young.han with IntelliJ IDEA on 2017.08.05.
 */
public interface ConnectionEventListener {
    void onConnectionAccepted(TcpConnection connection);

    void onConnectionEstablished(TcpConnection connection);

    void onConnectionFailed(Throwable cause);

    void onConnectionClosed(TcpConnection connection, Throwable cause);
}

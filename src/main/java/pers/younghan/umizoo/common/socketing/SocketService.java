/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common.socketing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketAddress;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.05.
 */
public class SocketService {
    private final SocketAddress  listeningSocketAddress;

    private ServerSocket socket;

    public SocketService(SocketAddress socketAddress, SocketSetting setting) throws IOException {
        this.listeningSocketAddress = socketAddress;

        this.socket = new ServerSocket();
    }

    public void start(){
        //socket.accept();
        //socket.bind();
    }
}

/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.communication;

import pers.younghan.umizoo.infrastructure.Processor;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public abstract class RmiChannelBase extends UnicastRemoteObject implements RmiChannel, Processor {

    private String host;
    private int port;
    private ProtocolCode protocol;

    private boolean started;

    protected RmiChannelBase() throws RemoteException {
        super();
    }

    protected RmiChannelBase(String host, int port, ProtocolCode protocol) throws RemoteException {
        super();
        this.host = host;
        this.port = port;
        this.protocol = protocol;
    }

    /**
     * 获取用于远程访问的地址。如rmi://localhost:8888/hello
     * @return
     */
    protected String getAddress() {
        return String.format("rmi://%s:%s/%s", host, port, protocol);
    }

    @Override
    public abstract Response execute(Request request);

    @Override
    public final synchronized void start() {
        if(started){
            return;
        }

        try {
            Naming.bind(this.getAddress(), this);
            this.started = true;
        }
        catch (RemoteException ex) {
        }
        catch (AlreadyBoundException ex) {
        }
        catch (MalformedURLException ex) {
        }
    }

    @Override
    public final synchronized void stop() {
        if(!started){
            return;
        }

        try {
            Naming.unbind(this.getAddress());
            this.started = false;
        }
        catch (RemoteException ex) {
        }
        catch (NotBoundException ex) {

        }
        catch (MalformedURLException ex) {
        }
    }
}

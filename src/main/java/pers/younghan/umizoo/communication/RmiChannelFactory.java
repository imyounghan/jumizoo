/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.communication;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public class RmiChannelFactory implements ChannelFactory {
    private final ConcurrentMap<String, Remote> clientsMap;

    public RmiChannelFactory() {
        this.clientsMap = new ConcurrentHashMap<>();
    }

    private Remote CreateChannel(String url, Remote original) {
        if (Objects.nonNull(original)) {
            return original;
        }

        try {
            return Naming.lookup(url);
        }
        catch (NotBoundException ex) {
            ex.printStackTrace();
        }
        catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        catch (RemoteException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public Channel getChannel(String address, ProtocolCode protocol) {
        String url = String.format("rmi://%s/%s", address, protocol.name());
        return (Channel)clientsMap.computeIfPresent(url, this::CreateChannel);
    }
}

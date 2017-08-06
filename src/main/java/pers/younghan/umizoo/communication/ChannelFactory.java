/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.communication;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public interface ChannelFactory {
    Channel getChannel(String address, ProtocolCode protocol);
}

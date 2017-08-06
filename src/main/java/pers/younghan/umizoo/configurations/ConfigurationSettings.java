/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.configurations;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public final class ConfigurationSettings {

    public static String ServiceName;

    public static String InnerAddress;

    public static String OuterAddress;

    public static int Port = 8888;

    public static int HandleRetryTimes = 5;

    public static long HandleRetryInterval = 1000;

}

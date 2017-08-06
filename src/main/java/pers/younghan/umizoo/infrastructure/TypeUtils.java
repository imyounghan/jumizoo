/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.infrastructure;

import pers.younghan.umizoo.messaging.*;
import pers.younghan.umizoo.messaging.handling.Handler;
import pers.younghan.umizoo.messaging.handling.QueryHandler;
import pers.younghan.umizoo.seeds.AggregateRoot;

import java.lang.reflect.Modifier;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public final class TypeUtils {
    public static boolean isAggregateRoot(Class type) {
        return isAssignableFrom(AggregateRoot.class, type);
    }

    public static boolean isCommand(Class type) {
        return isAssignableFrom(Command.class, type);
    }

    public static boolean isEvent(Class type){
        return isAssignableFrom(Event.class, type);
    }

    public static boolean isVersionEvent(Class type){
        return isAssignableFrom(VersionedEvent.class, type);
    }

    public static boolean isPublishableException(Class type){
        return isAssignableFrom(PublishableException.class, type);
    }

    public static boolean isResult(Class type){
        return isAssignableFrom(Result.class, type);
    }

    public static boolean isQuery(Class type){
        return isAssignableFrom(Query.class, type);
    }

    public static boolean isHandler(Class type){
        return isAssignableFrom(Handler.class, type);
    }

    public static boolean isQueryHandler(Class type){
        return isAssignableFrom(QueryHandler.class, type);
    }

    public static boolean isClass(Class type){
        return !type.isInterface() && !Modifier.isAbstract(type.getModifiers());
    }

    private static boolean isAssignableFrom(Class baseType, Class extendedType) {
        return isClass(extendedType) && baseType.isAssignableFrom(extendedType);
    }
}

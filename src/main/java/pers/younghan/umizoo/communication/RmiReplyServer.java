/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.communication;

import com.google.inject.Inject;
import pers.younghan.umizoo.common.composition.ObjectContainer;
import pers.younghan.umizoo.configurations.ConfigurationSettings;
import pers.younghan.umizoo.infrastructure.Initializer;
import pers.younghan.umizoo.infrastructure.TextSerializer;
import pers.younghan.umizoo.infrastructure.TypeUtils;
import pers.younghan.umizoo.messaging.CommandService;
import pers.younghan.umizoo.messaging.Result;
import pers.younghan.umizoo.messaging.ResultBus;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Objects;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public class RmiReplyServer extends RmiChannelBase implements Initializer {
    private ResultBus resultBus;
    private TextSerializer serializer;
    private Hashtable<String, Class<?>> resultTypes;

    @Inject
    public RmiReplyServer(ResultBus resultBus, TextSerializer serializer) throws RemoteException {
        super(ConfigurationSettings.InnerAddress, ConfigurationSettings.Port, ProtocolCode.Query);
        this.resultBus = resultBus;
        this.serializer = serializer;
        this.resultTypes = new Hashtable<>();
    }

    @Override
    public Response execute(Request request) {
        Class<?> resultType = resultTypes.get(request.getHeader().get("Type"));
        if (Objects.isNull(resultType)) {
            return Response.UnknownType;
        }

        Result result;
        try {
            result = (Result)serializer.deserialize(request.getBody(), resultType);
        }
        catch (Exception ex) {
            //TODO
            return Response.ParsingFailure;
        }

        resultBus.send(result, request.getHeader().get("TraceId"));

        return Response.Success;
    }

    @Override
    public void initialize(ObjectContainer container, Collection<Class<?>> types) {
        types.stream().filter(TypeUtils::isResult).forEach(type -> resultTypes.put(type.getSimpleName(), type));
    }
}

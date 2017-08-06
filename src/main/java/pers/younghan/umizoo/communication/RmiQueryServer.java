/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.communication;

import com.google.common.primitives.Ints;
import com.google.inject.Inject;
import pers.younghan.umizoo.common.composition.ObjectContainer;
import pers.younghan.umizoo.configurations.ConfigurationSettings;
import pers.younghan.umizoo.infrastructure.Initializer;
import pers.younghan.umizoo.infrastructure.TextSerializer;
import pers.younghan.umizoo.infrastructure.TypeUtils;
import pers.younghan.umizoo.messaging.*;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Objects;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public class RmiQueryServer extends RmiChannelBase implements Initializer {
    private QueryService queryService;
    private TextSerializer serializer;
    private Hashtable<String, Class<?>> queryTypes;

    @Inject
    public RmiQueryServer(QueryService queryService, TextSerializer serializer) throws RemoteException {
        super(ConfigurationSettings.OuterAddress, ConfigurationSettings.Port, ProtocolCode.Query);
        this.queryService = queryService;
        this.serializer = serializer;
        this.queryTypes = new Hashtable<>();
    }

    @Override
    public Response execute(Request request) {
        Class<?> queryType = queryTypes.get(request.getHeader().get("Type"));
        if (Objects.isNull(queryType)) {
            return Response.UnknownType;
        }

        Query query;
        try {
            query = (Query)serializer.deserialize(request.getBody(), queryType);
        }
        catch (Exception ex) {
            //TODO
            return Response.ParsingFailure;
        }

        QueryResult queryResult = queryService.fetch(query,
                Ints.tryParse(request.getHeader().get("Timeout"), 120000));
        return new Response(100, serializer.serialize(queryResult));
    }

    @Override
    public void initialize(ObjectContainer container, Collection<Class<?>> types) {
        types.stream().filter(TypeUtils::isQuery).forEach(type -> queryTypes.put(type.getSimpleName(), type));
    }
}

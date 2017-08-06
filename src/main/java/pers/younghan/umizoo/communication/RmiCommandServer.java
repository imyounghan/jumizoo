/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.communication;

import com.google.common.primitives.Ints;
import com.google.inject.Inject;
import com.google.inject.util.Types;
import pers.younghan.umizoo.common.composition.ObjectContainer;
import pers.younghan.umizoo.configurations.ConfigurationSettings;
import pers.younghan.umizoo.infrastructure.Initializer;
import pers.younghan.umizoo.infrastructure.TextSerializer;
import pers.younghan.umizoo.infrastructure.TypeUtils;
import pers.younghan.umizoo.messaging.Command;
import pers.younghan.umizoo.messaging.CommandResult;
import pers.younghan.umizoo.messaging.CommandReturnMode;
import pers.younghan.umizoo.messaging.CommandService;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Objects;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public class RmiCommandServer extends RmiChannelBase implements Initializer {
    private CommandService commandService;
    private TextSerializer serializer;
    private Hashtable<String, Class<?>> commandTypes;

    @Inject
    public RmiCommandServer(CommandService commandService, TextSerializer serializer) throws RemoteException {
        super(ConfigurationSettings.OuterAddress, ConfigurationSettings.Port, ProtocolCode.Command);
        this.commandService = commandService;
        this.serializer = serializer;
        this.commandTypes = new Hashtable<>();
    }

    @Override
    public Response execute(Request request) {
        Class<?> commandType = commandTypes.get(request.getHeader().get("Type"));
        if (Objects.isNull(commandType)) {
            return Response.UnknownType;
        }

        Command command;
        try {
            command = (Command)serializer.deserialize(request.getBody(), commandType);
        }
        catch (Exception ex) {
            //TODO
            return Response.ParsingFailure;
        }

        CommandResult commandResult = commandService.execute(command,
                CommandReturnMode.valueOf(Ints.tryParse(request.getHeader().get("Timeout"), 0)),
                Ints.tryParse(request.getHeader().get("Timeout"), 120000));
        return new Response(100, serializer.serialize(commandResult));
    }

    @Override
    public void initialize(ObjectContainer container, Collection<Class<?>> types) {
        types.stream().filter(TypeUtils::isCommand).forEach(type -> commandTypes.put(type.getSimpleName(), type));
    }
}

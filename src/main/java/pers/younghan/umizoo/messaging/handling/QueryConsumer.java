/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import com.google.inject.Inject;
import pers.younghan.umizoo.common.composition.ObjectContainer;
import pers.younghan.umizoo.configurations.ProcessingFlags;
import pers.younghan.umizoo.infrastructure.Initializer;
import pers.younghan.umizoo.infrastructure.StandardMetadata;
import pers.younghan.umizoo.infrastructure.TypeUtils;
import pers.younghan.umizoo.messaging.*;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.05.
 */
public class QueryConsumer extends Consumer<Query> implements Initializer {
    private final ResultBus resultBus;
    private final HashMap<Type, QueryHandler> queryHandlers;

    @Inject
    public QueryConsumer(MessageReceiver<Envelope<Query>> queryReceiver, ResultBus resultBus)
    {
        super(queryReceiver, ProcessingFlags.Query);

        this.resultBus = resultBus;
        this.queryHandlers = new HashMap<>();
    }

    private QueryHandler cast(Object object) {
        if(object instanceof QueryHandler) {
            return (QueryHandler)object;
        }

        return null;
    }

    @Override
    public void initialize(ObjectContainer container, Collection<Class<?>> types) {
        HashMap<Type, ParameterizedType> contactTypeMap = new HashMap<>();

        types.stream().filter(TypeUtils::isQueryHandler).forEach(handlerType->{
            Type[] interfaceTypes = handlerType.getGenericInterfaces();

            for (Type interfaceType : interfaceTypes) {
                if(!(interfaceType instanceof ParameterizedType)){
                    continue;
                }

                ParameterizedType parameterizedType = (ParameterizedType) interfaceType;
                if(!parameterizedType.getRawType().equals(QueryHandler.class)){
                    continue;
                }

                Type queryType = parameterizedType.getActualTypeArguments()[0];
                if(Objects.nonNull(contactTypeMap.put(queryType, parameterizedType))){
                    throw new RuntimeException(String.format("Found more than one handler for this type('%s') with QueryHandler<>.", queryType.getTypeName()));
                }
            }
        });

        types.stream().filter(TypeUtils::isQuery).forEach(queryType -> {
            ParameterizedType contactType = contactTypeMap.get(queryType);

            if(Objects.isNull(contactType)){
                throw new RuntimeException(String.format("The handler of this type('%s') is not found.", queryType.getName()));
            }

            queryHandlers.put(queryType, container.resolveAll(contactType).stream().map(this::cast).filter(Objects::nonNull).findFirst().get());
        });
    }

    private void notifyResult(TraceInfo traceInfo, Object result) {
        QueryResult queryResult;

        if(Objects.isNull(result)){
            queryResult = QueryResultBuilt.NOTHING;
        }
        else if(result instanceof Throwable){
            queryResult = new QueryResultBuilt(HandleStatus.Failed, ((Throwable)result).getMessage());
        }
        else{
            queryResult = new QueryResultBuilt(result);
        }

        this.resultBus.send(queryResult, traceInfo);
    }

    @Override
    protected void onMessageArrived(Envelope<Query> envelope) throws Exception {
        Class resultType = envelope.body().getClass();
        TraceInfo traceInfo = (TraceInfo)envelope.items().get(StandardMetadata.TraceInfo);

        QueryHandler handler = this.queryHandlers.get(resultType);
        Object result = handler.handle(envelope.body());

        this.notifyResult(traceInfo, result);
    }
}

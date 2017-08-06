/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import pers.younghan.umizoo.communication.ChannelFactory;
import pers.younghan.umizoo.communication.ProtocolCode;
import pers.younghan.umizoo.communication.Request;
import pers.younghan.umizoo.communication.Response;
import pers.younghan.umizoo.infrastructure.StandardMetadata;
import pers.younghan.umizoo.infrastructure.TextSerializer;
import pers.younghan.umizoo.messaging.*;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public class QueryResultReplyHandler implements EnvelopedMessageHandler<QueryResultBuilt> {
    private ResultBus resultBus;
    private ChannelFactory channelFactory;
    private TextSerializer serializer;

    public QueryResultReplyHandler(ResultBus resultBus,
                                   TextSerializer serializer,
                                   ChannelFactory channelFactory) {
        this.resultBus = resultBus;
        this.serializer = serializer;
        this.channelFactory = channelFactory;
    }

    @Override
    public void handle(Envelope<QueryResultBuilt> envelope) {
        TraceInfo traceInfo = (TraceInfo)envelope.items().get(StandardMetadata.TraceInfo);

        Request request = new Request(envelope.body(), serializer::serialize);
        request.getHeader().put("Type", envelope.body().getClass().getSimpleName());
        request.getHeader().put("TraceId", traceInfo.getId());

        boolean success;
        try {
            Response response = channelFactory.getChannel(traceInfo.getAddress(), ProtocolCode.Notify).execute(request);

            success = response.getStatus() == 200;
        }
        catch (Throwable ex) {
            success = false;
        }

        if (!success) {
            resultBus.send(envelope.body(), traceInfo);
        }
    }
}

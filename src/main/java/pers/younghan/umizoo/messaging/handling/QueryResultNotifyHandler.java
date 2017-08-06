/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging.handling;

import com.google.inject.Inject;
import pers.younghan.umizoo.common.LogManager;
import pers.younghan.umizoo.messaging.Envelope;
import pers.younghan.umizoo.messaging.QueryResultBuilt;
import pers.younghan.umizoo.messaging.ResultManager;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public class QueryResultNotifyHandler implements EnvelopedMessageHandler<QueryResultBuilt> {
    private final ResultManager resultManager;

    @Inject
    public QueryResultNotifyHandler(ResultManager resultManager) {
        this.resultManager = resultManager;
    }


    @Override
    public void handle(Envelope<QueryResultBuilt> envelope) {
        if(resultManager.setQueryResult(envelope.id(), envelope.body())) {
            if(LogManager.getDefault().isDebugEnabled()) {
                LogManager.getDefault().debug("query(%s) is completed.", envelope.id());
            }
        }
    }
}

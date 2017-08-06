/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import com.google.inject.Inject;
import pers.younghan.umizoo.common.LogManager;
import pers.younghan.umizoo.common.ObjectId;
import pers.younghan.umizoo.configurations.ConfigurationSettings;

import java.util.concurrent.CompletableFuture;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public class QueryServiceImpl implements QueryService {
    private final ResultManager resultManger;
    private final QueryBus queryBus;


    @Inject
    public QueryServiceImpl(ResultManager resultManger, QueryBus queryBus) {
        this.resultManger = resultManger;
        this.queryBus = queryBus;
    }

    @Override
    public CompletableFuture<QueryResult> fetchAsync(Query query, int timeoutMs) {
        String queryId = ObjectId.get().toString();
        CompletableFuture<QueryResult> future = resultManger.registerProcessingQuery(queryId, query, timeoutMs);

        CompletableFuture.runAsync(() -> {
            try {
                queryBus.send(query, new TraceInfo(queryId, ConfigurationSettings.InnerAddress));
            }
            catch(Exception ex) {
                LogManager.getDefault().error(ex.getMessage());
                resultManger.setQueryResult(queryId, QueryResultBuilt.SENT_FAILD);
            }
        });

        return future;
    }
}

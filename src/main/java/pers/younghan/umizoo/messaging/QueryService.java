/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import java.util.concurrent.CompletableFuture;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.05.
 */
public interface QueryService {
    default QueryResult fetch(final Query query){
        return this.fetch(query, 120000);
    }

    default QueryResult fetch(final Query query, final int timeoutMs){
        try {
            return this.fetchAsync(query, timeoutMs).get();
        }
        catch(Exception exception) {
            return null;
        }
    }


    default  CompletableFuture<QueryResult> fetchAsync(final Query query){
        return this.fetchAsync(query, 120000);
    }

    CompletableFuture<QueryResult> fetchAsync(final Query query, final int timeoutMs);
}

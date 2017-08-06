/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.05.
 */
public class QueryResultBuilt extends HandleResult implements QueryResult {
    public final static QueryResult SENT_FAILD = new QueryResultBuilt(HandleStatus.Failed, "Send to bus failed.");
    public final static QueryResult TIMEOUT = new QueryResultBuilt(HandleStatus.Timeout, "Operation is timeout.");
    public final static QueryResult NOTHING = new QueryResultBuilt(HandleStatus.Nothing, "No Data.");

    private Object result;

    public QueryResultBuilt()
    { }

    public QueryResultBuilt(HandleStatus status, String errorMessage)
    {
        super(status, errorMessage);
    }

    public QueryResultBuilt(Object result)
    {
        this.result = result;
    }

    @Override
    public Object getData() {
        return this.result;
    }
}

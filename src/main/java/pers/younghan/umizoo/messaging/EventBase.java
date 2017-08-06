/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import java.util.Date;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public abstract class EventBase implements Event, RoutingProvider {

    private String sourceId;
    private Date timestamp;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getRoutingKey() {
        return this.sourceId;
    }
}

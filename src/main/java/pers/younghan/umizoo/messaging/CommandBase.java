/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import java.util.Date;
import java.util.Objects;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.06.
 */
public abstract class CommandBase<TTargetId> implements Command, RoutingProvider {

    private Date timestamp;
    private TTargetId targetId;

    protected CommandBase()
    {
        this.timestamp = new Date();
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public TTargetId getTargetId() {
        return targetId;
    }

    public void setTargetId(TTargetId targetId) {
        this.targetId = targetId;
    }

    @Override
    public String getRoutingKey() {
        if(Objects.nonNull(targetId)){
            return targetId.toString();
        }

        return null;
    }
}

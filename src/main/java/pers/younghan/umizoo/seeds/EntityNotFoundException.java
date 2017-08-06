/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.seeds;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class EntityNotFoundException extends Exception {
    private Object entityId;
    private Class entityType;

    public EntityNotFoundException(Object entityId, Class entityType)
    {
        this.entityId = entityId;
        this.entityType = entityType;
    }

    @Override
    public String getMessage() {
        return String.format("Cannot find the entity '{0}' of id '{1}'.",
                entityType.getName(), entityId);
    }

    public Object getEntityId() {
        return entityId;
    }

    public Class getEntityType() {
        return entityType;
    }
}

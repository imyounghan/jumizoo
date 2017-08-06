/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public final class SourceInfo {
    private String sourceId;
    private Class sourceType;

    public SourceInfo(Object sourceId, Class sourceType){
        this.sourceId = sourceId.toString();
        this.sourceType = sourceType;
    }

    public String getSourceId() {
        return sourceId;
    }

    public Class getSourceType() {
        return sourceType;
    }

    @Override
    public String toString() {
        return sourceType +"@"+ sourceId;
    }

    @Override
    public int hashCode() {
        return this.sourceType.hashCode() * 31 + this.sourceId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        else if (!(obj instanceof SourceInfo)) {
            return false;
        }
        else {
            SourceInfo other = (SourceInfo)obj;
            return this.sourceType.equals(other.sourceType) && this.sourceId.equals(other.sourceId);
        }
    }
}

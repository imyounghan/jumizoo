/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.common.composition;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * Created by young.han with IntelliJ IDEA on 2017-08-02.
 */
public class TypeRegistration {
    private String name;
    private Type type;
    private boolean initialization;

    public TypeRegistration(Type type, boolean initialization) {
        this(type, null, initialization);
    }

    public TypeRegistration(Type type, String name, boolean initialization) {
        this.type = type;
        this.name = name;
        this.initialization = initialization;
    }

    public String name() {
        return this.name;
    }

    public Type type() {
        return this.type;
    }

    public boolean initializationRequired() {
        return this.initialization;
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) return true;

        TypeRegistration that = null;
        if(object instanceof TypeRegistration) {
            that = (TypeRegistration)object;
        }

        if(Objects.isNull(that)) return false;

        if(!this.type.equals(that.type)) return false;

        return !Objects.isNull(this.name) ? this.name.equals(that.name) : Objects.isNull(that.name);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public Object getInstance(ObjectContainer container) {
        return container.resolve(this.type, this.name);
    }
}

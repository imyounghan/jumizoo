/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.messaging;

import pers.younghan.umizoo.infrastructure.TextSerializerImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class Envelope<T> {
    private T body;
    private String messageId;
    private Map<String, Object> items;

    public Envelope(T body, String id) {
        this(body, id, new HashMap<>());
    }

    public Envelope(T body, String id, Map<String, Object> items) {
        this.body = body;
        this.messageId = id;
        this.items = items;
    }

    public T body() {
        return this.body;
    }

    public String id() {
        return this.messageId;
    }

    public Map<String, Object> items() {
        return this.items;
    }

    @Override
    public String toString() {
        return String.format("%s(%s)",
                this.body.getClass().getName(),
                TextSerializerImpl.instance.serialize(this.body));
    }
}

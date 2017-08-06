/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.communication;

import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.05.
 */
public class Request {

    private String body;
    private Map<String, String> header;

    public Request()
    {
        this.header = new Hashtable<>();
    }

    public Request(String body)
    {
        this();
        this.body = body;
    }

    public Request(String body, Map<String, String> header)
    {
        this.header = header;
        this.body = body;
    }

    public Request(Object body, Function<Object, String> serializer)
    {
        this();
        this.body = serializer.apply(body);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }
}

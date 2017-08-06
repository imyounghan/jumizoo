/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.communication;

/**
 * Created by young.han with IntelliJ IDEA on 2017.08.05.
 */
public class Response {
    public static final Response Success = new Response(200, null);
    public static final Response UnknownType = new Response(404,"Unknown Type.");
    public static final Response ParsingFailure = new Response(500,"Serialization failure.");

    private int status;
    private String message;

    public Response()
    { }

    public Response(int status, String message)
    {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

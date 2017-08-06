/*
 * Copyright © 2017 Sunsoft Studio.
 *
 * Umizoo is a framework can help you develop DDD and CQRS style applications.
 */

package pers.younghan.umizoo.infrastructure;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Date;

/**
 * Created by young.han with IntelliJ IDEA on 2017-07-29.
 */
public class TextSerializerImpl implements TextSerializer {
    public final static TextSerializer instance = new TextSerializerImpl();


    private final Gson gson;

    public TextSerializerImpl() {
        this.gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateSerializer())
                .registerTypeAdapter(Date.class, new DateDeserializer()).create();
    }

    @Override
    public String serialize(Object object) {
        return this.gson.toJson(object);
    }

    @Override
    public <T> T deserialize(String serialized, Class<T> type) {
        return this.gson.fromJson(serialized, type);
    }

    private class DateSerializer implements JsonSerializer<Date> {
        public JsonElement serialize(Date source, Type typeOfSource, JsonSerializationContext context) {
            return new JsonPrimitive(Long.toString(source.getTime()));
        }
    }

    private class DateDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfTarget, JsonDeserializationContext context) throws JsonParseException {
            long time = Long.parseLong(json.getAsJsonPrimitive().getAsString());
            return new Date(time);
        }
    }
}

package com.quantxt.types;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * Created by matin on 4/30/17.
 */

public class DateTimeTypeConverter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {

    @Override
    public JsonElement serialize(LocalDateTime src, Type srcType, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

    @Override
    public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        return LocalDateTime.parse(json.getAsString());
    }
}
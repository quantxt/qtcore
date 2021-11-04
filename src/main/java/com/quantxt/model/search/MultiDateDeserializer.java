package com.quantxt.model.search;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MultiDateDeserializer extends StdDeserializer<LocalDateTime> {
    private static final long serialVersionUID = 1L;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public MultiDateDeserializer() {
        this(null);
    }

    public MultiDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String date = node.textValue();
        //remove partial second part
        date = date.replaceAll("\\.\\d+$", "");

        try {
            return LocalDateTime.parse(date, dateTimeFormatter);
        } catch (Exception e) {

        }

        throw new JsonParseException(jp, "Unparseable date: \"" + date + "\"");
    }
}

package org.myproject.ecommerce.hvdfclient;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

public class ActivityIdKeyJsonDeserializer extends JsonDeserializer<Activity.IdKey> {
    @Override
    public Activity.IdKey deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonValue = p.getValueAsString();
        Objects.requireNonNull(jsonValue);
        return objectMapper.readValue(jsonValue, Activity.IdKey.class);
    }
}

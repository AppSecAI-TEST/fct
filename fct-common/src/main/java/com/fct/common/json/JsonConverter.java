package com.fct.common.json;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fct.common.exceptions.Exceptions;

import java.io.IOException;

/**
 * @author ningyang
 */
public class JsonConverter {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T toObject(String json, final Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            throw Exceptions.unchecked(e);
        }
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw Exceptions.unchecked(e);
        }
    }
}

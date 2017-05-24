package com.fct.thirdparty.http.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

/**
 * json和java对象的转换器
 * @author ningyang
 */
public class JsonConverter {
    private static final ObjectMapper mapper = new ObjectMapper();

    public JsonConverter() {
    }

    public static <T> T toObject(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T toObject(InputStream stream, Class<T> type) {
        try {
            return mapper.readValue(stream, type);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static JsonNode toJsonNode(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static JsonNode toJsonNode(InputStream stream) {
        try {
            return mapper.readTree(stream);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }
}

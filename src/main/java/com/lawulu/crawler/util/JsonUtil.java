package com.lawulu.crawler.util;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public final class JsonUtil {
    static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule())
            .registerModule(new GuavaModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

    private JsonUtil() {
    }

    public static <T> String writeAsString(T t) {
        try {
            return MAPPER.writeValueAsString(t);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> byte[] writeAsBytes(T t) {
        try {
            return MAPPER.writeValueAsBytes(t);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readAs(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readAs(byte[] bytes, Class<T> clazz) {
        try {
            return MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readAs(String json, TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T readAs(byte[] bytes, TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(bytes, typeReference);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
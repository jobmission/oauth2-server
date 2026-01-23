package com.revengemission.sso.oauth2.server.utils;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer;
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer;
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {

    private static final ObjectMapper objectMapper;

    static {
        SimpleModule localDateTimeModule = new SimpleModule();
        localDateTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        localDateTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        localDateTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        localDateTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        localDateTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        localDateTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
        objectMapper = JsonMapper.builder()
                .addModule(localDateTimeModule)
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .changeDefaultVisibility(builder -> builder.withFieldVisibility(JsonAutoDetect.Visibility.ANY))
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.NON_NULL))
                .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)
                .configure(JsonReadFeature.ALLOW_UNQUOTED_PROPERTY_NAMES, true)
                .configure(JsonReadFeature.ALLOW_SINGLE_QUOTES, true)
                .configure(JsonReadFeature.ALLOW_TRAILING_COMMA, true)
                .configure(JsonReadFeature.ALLOW_JAVA_COMMENTS, true)
                .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
                .build();
    }

    public static String objectToJsonString(Object object) {
        //Object to JSON in String
        return objectMapper.writeValueAsString(object);
    }

    public static String multiValueMapToJsonString(Map<String, String[]> object) {
        Map<String, String> newMap = new HashMap<>(16);
        if (object != null && !object.isEmpty()) {
            object.forEach((k, v) -> {
                if (v != null && v.length > 0) {
                    newMap.put(k, Arrays.toString(v));
                }
            });
        }
        //Object to JSON in String
        return objectMapper.writeValueAsString(newMap);
    }

    public static <T> T jsonStringToObject(String jsonString, Class<T> t) {
        //JSON from String to Object
        return objectMapper.readValue(jsonString, t);
    }

    /**
     * string转map,list等
     * Map<String, List<String>> result = JsonUtil.jsonStringToObject("{\"a\":[1],\"b\":[2],\"c\":[\"d\",\"e\",\"f\"]}", new TypeReference<Map<String, List<String>>>() {
     * });
     *
     * @param str           json字符串
     * @param typeReference 被转对象引用类型
     * @param <T>
     * @return
     */
    public static <T> T jsonStringToObject(String str, TypeReference<T> typeReference) {
        return objectMapper.readValue(str, typeReference);
    }

    /**
     * string转object 用于转为集合对象
     *
     * @param str             json字符串
     * @param collectionClass 被转集合class
     * @param elementClasses  被转集合中对象类型class
     * @param <T>
     * @return
     */
    public static <T> T jsonStringToObject(String str, Class<?> collectionClass, Class<?>... elementClasses) throws IOException {
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        return objectMapper.readValue(str, javaType);
    }

    public static Map<String, Object> jsonNodeToMap(JsonNode jsonNode) {
        String jsonString = jsonNode.toPrettyString();
        return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
        });
    }

    public static Map<String, Map<String, Object>> jsonNodeToMapMap(JsonNode jsonNode) {
        String jsonString = jsonNode.toPrettyString();
        return objectMapper.readValue(jsonString, new TypeReference<>() {
        });
    }

    public static Map<String, Object> jsonStringToMap(String jsonString) {
        return objectMapper.readValue(jsonString, new TypeReference<>() {
        });
    }

    public static Map<String, String> jsonStringToStringMap(String jsonString) {
        return objectMapper.readValue(jsonString, new TypeReference<>() {
        });
    }

    public static Map<String, Map<String, Object>> jsonStringToMapMap(String jsonString) {
        return objectMapper.readValue(jsonString, new TypeReference<>() {
        });
    }

    public static JsonNode jsonStringToJsonNode(String jsonString) {
        return objectMapper.readTree(jsonString);
    }

    public static JsonNode objectToJsonNode(Object object) {
        return objectMapper.readTree(objectMapper.writeValueAsString(object));
    }

    public static ObjectNode createObjectNode() {
        return objectMapper.createObjectNode();
    }

    public static ArrayNode createArrayNode() {
        return objectMapper.createArrayNode();
    }

}

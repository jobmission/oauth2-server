package com.revengemission.sso.oauth2.server.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        // 美化输出
        ///mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JavaTimeModule timeModule = new JavaTimeModule();

        timeModule.addDeserializer(LocalDate.class,
            new LocalDateDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        timeModule.addDeserializer(LocalDateTime.class,
            new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        timeModule.addSerializer(LocalDate.class,
            new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        timeModule.addSerializer(LocalDateTime.class,
            new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        mapper.registerModule(timeModule);
    }

    public static String objectToJsonString(Object object) throws JsonProcessingException {
        //Object to JSON in String
        return mapper.writeValueAsString(object);
    }

    public static String multiValueMapToJsonString(Map<String, String[]> object) throws JsonProcessingException {
        Map<String, String> newMap = new HashMap<>(16);
        if (object != null && object.size() > 0) {
            object.forEach((k, v) -> {
                if (v != null && v.length > 0) {
                    newMap.put(k, Arrays.toString(v));
                }
            });
        }
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        //Object to JSON in String
        return mapper.writeValueAsString(newMap);
    }

    public static <T> T jsonStringToObject(String jsonString, Class<T> t) throws IOException {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        //JSON from String to Object
        return mapper.readValue(jsonString, t);
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
    public static <T> T jsonStringToObject(String str, TypeReference<T> typeReference) throws IOException {
        return mapper.readValue(str, typeReference);
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
        JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        return mapper.readValue(str, javaType);
    }
}

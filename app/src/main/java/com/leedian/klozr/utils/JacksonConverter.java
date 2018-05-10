package com.leedian.klozr.utils;

/**
 * JacksonConverter
 *
 * @author Franco
 */
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class JacksonConverter<T extends Object> {
    private ObjectMapper mapper = new ObjectMapper();
    private Class<T> type;

    public JacksonConverter(Class<T> toValueType) {

        type = toValueType;
    }

    public List<T> getJsonObjectListObject(String jsonString) throws IOException {

        List<T>      OutObjects = new ArrayList<T>();
        List<Object> Objects;
        Objects = mapper.readValue(jsonString, new TypeReference<List<T>>() {});

        for (Object obj : Objects) {
            T model = this.getJsonObject(obj, type);
            OutObjects.add(model);
        }
        return OutObjects;
    }

    public T getJsonObject(String jsonString) throws IOException {

        T Object = mapper.readValue(jsonString, new TypeReference<T>() {});
        return getJsonObject(Object, type);
    }

    public T getJsonObject(Object object, Class<T> toValueType) throws IOException {

        T Object = mapper.convertValue(object, toValueType);
        return Object;
    }

    public String getJsonString(Object object) throws IOException {

        return mapper.writeValueAsString(object);
    }

    public String getListJsonString(List list) throws IOException {

        final OutputStream out    = new ByteArrayOutputStream();
        final ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(out, list);
        return out.toString();
    }

    public String getJsonStringFromJsonNodeName(String node, String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        mapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);
        JsonNode arrNode = mapper.readTree(json).get(node);

        return mapper.writeValueAsString(arrNode);
    }

    public JsonNode getJsonNodeFromJsonNodeName(String node, String json) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        mapper.getFactory().configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, true);

        return mapper.readTree(json).get(node);
    }
}

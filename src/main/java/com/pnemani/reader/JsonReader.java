package com.pnemani.reader;

import java.lang.reflect.*;
import java.util.*;

import com.pnemani.annotations.Json;
import com.pnemani.parser.JsonParser;
import com.pnemani.types.impl.JsonElement;

public class JsonReader {

    private final JsonParser parser = new JsonParser();

    public <T> T readValue(String json, Class<T> classType) throws Exception {
        JsonElement element = parser.parse(json);
        return convertElement(element, classType);
    }

    @SuppressWarnings("unchecked")
    private <T> T convertElement(JsonElement element, Class<T> classType) throws Exception {
        if (element == null) return null;

        // Handle primitives & simple wrappers
        if (classType == String.class) {
            return (T) element.getText();
        } else if (classType == int.class || classType == Integer.class) {
            return (T) element.getNumberAsInt();
        } else if (classType == long.class || classType == Long.class) {
            return (T) element.getNumberAsLong();
        } else if (classType == double.class || classType == Double.class) {
            return (T) element.getNumberAsDouble();
        } else if (classType == boolean.class || classType == Boolean.class) {
            return (T) element.getBoolean();
        } else if (Number.class.isAssignableFrom(classType)) {
            return (T) element.getNumberAsDouble();
        }

        // When the classType is List
        if (List.class.isAssignableFrom(classType)) {
            List<JsonElement> list = element.getList();
            List<Object> result = new ArrayList<>();
            for (JsonElement e : list) {
                result.add(convertElement(e, e.getClassType())); // could infer generic type if passed
            }
            return (T) result;
        }

        // Parse an object
        if (element.isObject()) {
            return convertObjectElement(element, classType);
        }

        throw new IllegalArgumentException("Unsupported type: " + classType);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> convertListElement(JsonElement element, Class<T> classType) throws Exception {
        List<JsonElement> list = element.getList();
        List<Object> result = new ArrayList<>();
        for (JsonElement e : list) {
            result.add(convertElement(e, classType)); // could infer generic type if passed
        }
        return (List<T>) result;
    }

    private <T> T convertObjectElement(JsonElement element, Class<T> classType) throws Exception {
        Map<String, JsonElement> map = element.getMap();

        // Handle Java Records
        if (classType.isRecord()) {
            // Get canonical constructor parameter types in order
            RecordComponent[] components = classType.getRecordComponents();
            Object[] args = new Object[components.length];

            for (int i = 0; i < components.length; i++) {
                RecordComponent component = components[i];
                Field field = classType.getDeclaredField(component.getName());
                field.setAccessible(true);
                String fieldName;
                var jsonAnnotation = field.getAnnotation(Json.class);
                if (jsonAnnotation != null) {
                    fieldName = jsonAnnotation.name();
                } else {
                    fieldName = component.getName();
                }

                JsonElement fieldValue = map.get(fieldName);
                if (fieldValue != null) {
                    Class<?> typeOfClass = component.getType();
                    if(List.class.isAssignableFrom(typeOfClass)) {
                        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                        Class<?> listType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                        args[i] = convertListElement(fieldValue, listType); 
                    }
                    else  args[i] = convertElement(fieldValue, component.getType());
                } else {
                    args[i] = null; // or default value handling
                }
            }

            // Create new record instance with canonical constructor
            Constructor<T> canonical = classType.getDeclaredConstructor(
                Arrays.stream(components)
                    .map(RecordComponent::getType)
                    .toArray(Class[]::new)
            );
            canonical.setAccessible(true);
            try{
            T newInstance = canonical.newInstance(args);
            return newInstance;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.getMessage());
                return null;
            }
        }

        T instance = classType.getDeclaredConstructor().newInstance();

        for (Field field : classType.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName;
            var jsonAnnotation = field.getAnnotation(Json.class);
            if (jsonAnnotation != null){
                fieldName = jsonAnnotation.name();
            } else {
                fieldName = field.getName();
            }
            
            JsonElement fieldValue = map.get(fieldName);
            if (fieldValue != null) {
                Class<?> typeOfClass = field.getType();
                Object converted;
                if(List.class.isAssignableFrom(typeOfClass)){
                    ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
                    Class<?> listType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    converted = convertListElement(fieldValue, listType);
                } 
                else converted = convertElement(fieldValue, typeOfClass);                     
                field.set(instance, converted);
            }
        }
        return instance;
    }
}


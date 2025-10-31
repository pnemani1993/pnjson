package com.pnemani.parser;

import java.lang.reflect.*;
import java.util.*;

import com.pnemani.annotations.Json;
import com.pnemani.exceptions.JsonConversionException;
import com.pnemani.exceptions.JsonParserException;
import com.pnemani.types.JsonElement;

public sealed abstract class JsonReader extends JsonParserImpl permits ParserImpl {

    protected JsonReader() {
        super();
    }

    protected <T> T readValue(String json, Class<T> classType) throws JsonParserException, JsonConversionException {
        JsonElement element = super.parseString(json);
        return convertElement(element, classType);
    }

    @SuppressWarnings("unchecked")
    private <T> T convertElement(JsonElement element, Class<T> classType) throws JsonConversionException {
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

        throw new JsonConversionException("Unsupported class type: " + classType);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> convertListElement(JsonElement element, Class<T> classType) throws JsonConversionException {
        List<JsonElement> list = element.getList();
        List<Object> result = new ArrayList<>();
        for (JsonElement e : list) {
            result.add(convertElement(e, classType)); // could infer generic type if passed
        }
        return (List<T>) result;
    }

    private <T> T convertObjectElement(JsonElement element, Class<T> classType) throws JsonConversionException {
        Map<String, JsonElement> map = element.getMap();

        // Handle Java Records
        if (classType.isRecord()) {
            // Get canonical constructor parameter types in order
            RecordComponent[] components = classType.getRecordComponents();
            Object[] args = new Object[components.length];

            for (int i = 0; i < components.length; i++) {
                RecordComponent component = components[i];
                Field field;
                try{
                    field = classType.getDeclaredField(component.getName());
                } catch (NoSuchFieldException | SecurityException ex){
                    System.out.println(ex.getMessage());
                    throw new JsonConversionException("The field does not exist or is not accessible: " + component.getName());
                }
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
            Constructor<T> canonical;
            
            try {
                canonical = classType.getDeclaredConstructor(
                    Arrays.stream(components)
                        .map(RecordComponent::getType)
                        .toArray(Class[]::new)
                );
            } catch (NoSuchMethodException | SecurityException ex) {
                System.out.println(ex.getMessage());
                throw new JsonConversionException("The constructor cannot be accessed: " + classType);
            }
            canonical.setAccessible(true);
            try{
            T newInstance = canonical.newInstance(args);
            return newInstance;
            } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException ex) {
                System.out.println(ex.getMessage());
                throw new JsonConversionException("The object cannot be instantiated: " + canonical.getName());
            }
        }

        T instance;
        try {
            instance = classType.getDeclaredConstructor().newInstance();
        } catch(IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex){
            System.out.println(ex.getMessage());
            throw new JsonConversionException("The object cannot be instantiated: " + classType);
        }

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
                try { 
                    field.set(instance, converted); 
                } catch(IllegalAccessException | IllegalArgumentException ex) {
                    System.out.println(ex.getMessage());
                    throw new JsonConversionException("The field value cannot be set: " + field.getName());
                }
            }
        }
        return instance;
    }
}


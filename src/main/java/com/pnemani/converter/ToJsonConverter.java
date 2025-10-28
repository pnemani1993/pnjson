package com.pnemani.converter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.pnemani.annotations.Json;

public class ToJsonConverter {

    public static String toJson(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof Class){
            return null;
        }

        Class<?> clazz = obj.getClass();

        // Strings
        if (obj instanceof String) {
            return "\"" + escape((String) obj) + "\"";
        }

        // Primitives and wrappers
        if (clazz.isPrimitive() || obj instanceof Number || obj instanceof Boolean) {
            return String.valueOf(obj);
        }

        // Arrays
        if (clazz.isArray()) {
            int length = Array.getLength(obj);
            List<String> elements = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                elements.add(toJson(Array.get(obj, i)));
            }
            return "[" + String.join(",", elements) + "]";
        }

        // Collections
        if (obj instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) obj;
            List<String> elements = new ArrayList<>();
            for (Object element : collection) {
                elements.add(toJson(element));
            }
            return "[" + String.join(",", elements) + "]";
        }

        // Maps
        if (obj instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) obj;
            List<String> entries = new ArrayList<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                entries.add("\"" + escape(key) + "\":" + toJson(entry.getValue()));
            }
            return "{" + String.join(",", entries) + "}";
        }

        // Objects (use reflection)
        List<String> fields = new ArrayList<>();
        // System.out.println();
        // System.out.println(clazz.getName());
        Field[] allFields = clazz.getDeclaredFields();
        // Arrays.stream(allFields).forEach(field -> System.out.println("Field: " + field.getName()));
        for (Field field : allFields) {
            int modifierValue = field.getModifiers();
            if(Modifier.isStatic(modifierValue) || Modifier.isTransient(modifierValue)) continue;
            field.setAccessible(true);
            try {
                String fieldName;
                var jsonAnnotation = field.getAnnotation(Json.class);
                if (jsonAnnotation != null){
                    fieldName = jsonAnnotation.name();
                } else {
                    fieldName = field.getName();
                }
                Object value = field.get(obj);
                fields.add("\"" + escape(fieldName) + "\":" + toJson(value));
            } catch (IllegalAccessException e) {
                // skip field
            }
        }
        return "{" + String.join(",", fields) + "}";
    }

    // Escape special characters for JSON strings
    private static String escape(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public String convertToJson(Object obj) {
        if (obj == null) {
            return "null";
        } else if (obj instanceof Class){
            return null;
        }

        Class<?> clazz = obj.getClass();

        // Strings
        if (obj instanceof String) {
            return "\"" + escapeString((String) obj) + "\"";
        }

        // Primitives and wrappers
        if (clazz.isPrimitive() || obj instanceof Number || obj instanceof Boolean) {
            return String.valueOf(obj);
        }

        // Arrays
        if (clazz.isArray()) {
            int length = Array.getLength(obj);
            List<String> elements = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                elements.add(toJson(Array.get(obj, i)));
            }
            return "[" + String.join(",", elements) + "]";
        }

        // Collections
        if (obj instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) obj;
            List<String> elements = new ArrayList<>();
            for (Object element : collection) {
                elements.add(toJson(element));
            }
            return "[" + String.join(",", elements) + "]";
        }

        // Maps
        if (obj instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) obj;
            List<String> entries = new ArrayList<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                String key = String.valueOf(entry.getKey());
                entries.add("\"" + escapeString(key) + "\":" + toJson(entry.getValue()));
            }
            return "{" + String.join(",", entries) + "}";
        }

        // Objects (use reflection)
        List<String> fields = new ArrayList<>();
        // System.out.println();
        // System.out.println(clazz.getName());
        Field[] allFields = clazz.getDeclaredFields();
        // Arrays.stream(allFields).forEach(field -> System.out.println("Field: " + field.getName()));
        for (Field field : allFields) {
            int modifierValue = field.getModifiers();
            if(Modifier.isStatic(modifierValue) || Modifier.isTransient(modifierValue)) continue;
            field.setAccessible(true);
            try {
                String fieldName;
                var jsonAnnotation = field.getAnnotation(Json.class);
                if (jsonAnnotation != null){
                    fieldName = jsonAnnotation.name();
                } else {
                    fieldName = field.getName();
                }
                Object value = field.get(obj);
                fields.add("\"" + escapeString(fieldName) + "\":" + toJson(value));
            } catch (IllegalAccessException e) {
                // skip field
            }
        }
        return "{" + String.join(",", fields) + "}";
    }

    private String escapeString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}

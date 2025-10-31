package com.pnemani.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.pnemani.exceptions.InvalidOperationException;
import com.pnemani.types.JsonElement;

public class JsonElementTest {
    static final JsonElement stringElement = new JsonElement("PLACEHOLDER");
    static final JsonElement booleanTrueElement = new JsonElement(Boolean.TRUE);
    static final JsonElement booleanFalseElement = new JsonElement(Boolean.FALSE);
    static final JsonElement numberElement = new JsonElement(new BigDecimal(111));

    final static Map<String, JsonElement> map = new LinkedHashMap<>();
    final static List<JsonElement> list = new ArrayList<>();
    
    @BeforeAll
    static void setup() {
        map.put("key1", stringElement);
        map.put("key2", booleanTrueElement);
        map.put("key3", booleanFalseElement);
        map.put("key4", numberElement);
        map.put("key5", null);

        list.add(stringElement);
        list.add(booleanTrueElement);
        list.add(booleanFalseElement);
        list.add(numberElement);
    }

    @Test
    void stringElementTest() {
        assertEquals(String.class, stringElement.getClassType());
        assertEquals("PLACEHOLDER", stringElement.getText());
        assertThrows(InvalidOperationException.class, () -> {stringElement.get("key");});
    }

}

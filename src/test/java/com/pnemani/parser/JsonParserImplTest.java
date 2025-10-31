package com.pnemani.parser;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import com.pnemani.exceptions.JsonParserException;
import com.pnemani.types.JsonElement;

public class JsonParserImplTest {

    private static final String JSON = """
            {
                "name": "value",
                "age" : 32,
                "banks": ["chase", "chime", "wells_fargo"],
                "isCitizen": false,
                "location": null,
                "occupation": {
                        "organization": "chase",
                        "position": "developer"
                    }
            }
            """; 

    private static final String INVALID_JSON = """
        {
            "name": "value",
            "age" : 32,
        }
            """;

    private static final String CHAR_JSON = """
            {
                "message": "Hello\\nWorld\\tTabbed",
                "quote": "He said: \\\"JSON is fun!\\\"",
                "unicode": "Snowman: \\u2603"
            }
            """;

    private static final String MISSING_FIELDS_JSON = """
            [
                { "id": 1, "name": "A" },
                { "id": 2 },
                { "name": "C", "active": true }
            ]
            """;

    private static final String NESTED_JSON = """
            {
                "a": {
                    "b": {
                        "c": {
                            "d": {
                                "e": 42
                            }
                        }
                    }
                }
            }
            """;

    private static final String MIXED_JSON = """
            [
                1,
                [2, 3],
                { "x": 4 },
                "five",
                null
            ]

            """;

    JsonParserImpl parser;

    @Test 
    void constructorTest() {
        // Test if the string is empty
        parser = new ParserImpl(); 
        try {
            // accessing the private 'in' field
            Field inField = JsonParserImpl.class.getDeclaredField("in");
            inField.setAccessible(true);
            var inFieldValue = inField.get(parser);

            // accessing the private 'pos' field
            Field posField = JsonParserImpl.class.getDeclaredField("pos");
            posField.setAccessible(true);
            var posFieldValue = posField.get(parser);

            assertEquals("", inFieldValue);
            assertEquals(0, posFieldValue);
        } catch(NoSuchFieldException | IllegalAccessException ex ){
            ex.printStackTrace();
            throw new AssertionError("Error thrown while accessing field: " + ex.getMessage());
       }
    }

    @Test 
    void validParseStringTest() {
        // Test if the string is empty
        parser = new ParserImpl(); 
        try { 
            assertDoesNotThrow(() -> parser.parseString(JSON));
            JsonElement readValue = parser.parseString(JSON);
            
            assertNotNull(readValue);
            assertEquals("value", readValue.get("name").getText());
            assertEquals(32, readValue.get("age").getNumberAsInt());
            assertFalse(readValue.get("isCitizen").getBoolean());
            assertNull(readValue.get("location"));
            assertEquals("chase", readValue.get("banks").get(0).getText());
            assertEquals("chime", readValue.get("banks").get(1).getText());
            assertEquals("wells_fargo", readValue.get("banks").get(2).getText());
            assertEquals("chase", readValue.get("occupation").get("organization").getText());
            assertEquals("developer", readValue.get("occupation").get("position").getText());
        } catch (JsonParserException ex) {
            ex.printStackTrace();
            throw new AssertionError("Json cannot be parsed");
        }
    }

    @Test
    void invalidParseStringTest() {
        parser = new ParserImpl();
            assertThrows(JsonParserException.class, () -> {parser.parseString(INVALID_JSON);});
    }

    @Test
    void characterJsonTest() {
        parser = new ParserImpl();
        JsonElement element;
        try{ 
            element = parser.parseString(CHAR_JSON);
        } catch (JsonParserException ex){
            throw new AssertionFailedError();
        }
        assertEquals("Hello\nWorld\tTabbed", element.get("message").getText());
        assertEquals("He said: \"JSON is fun!\"", element.get("quote").getText());
        assertEquals("Snowman: \u2603", element.get("unicode").getText());
    }

    @Test
    void missingFieldsJsonTest() {
        parser = new ParserImpl();
        JsonElement element;
        try{ 
            element = parser.parseString(MISSING_FIELDS_JSON);
        } catch (JsonParserException ex){
            throw new AssertionFailedError();
        }
        assertEquals(1, element.get(0).get("id").getNumberAsInt());
        assertEquals("A", element.get(0).get("name").getText());

        assertEquals(2, element.get(1).get("id").getNumberAsInt());

        assertEquals("C", element.get(2).get("name").getText());
        assertTrue(element.get(2).get("active").getBoolean());
    }

    @Test
    void nestedJsonTest() {
        parser = new ParserImpl();
        JsonElement element;
        try{ 
            element = parser.parseString(NESTED_JSON);
        } catch (JsonParserException ex){
            throw new AssertionFailedError();
        }
        assertEquals(42, element.get("a").get("b").get("c").get("d").get("e").getNumberAsInt());
    }

    @Test
    void mixedJsonTest() {
        parser = new ParserImpl();
        JsonElement element;
        try{ 
            element = parser.parseString(MIXED_JSON);
        } catch (JsonParserException ex){
            throw new AssertionFailedError();
        }
        assertEquals(1, element.get(0).getNumberAsInt());
        assertEquals(2, element.get(1).get(0).getNumberAsInt());
        assertEquals(3, element.get(1).get(1).getNumberAsInt());
        assertEquals(4, element.get(2).get("x").getNumberAsInt());
        assertEquals("five", element.get(3).getText());
        assertNull(element.get(4));        
    }
}

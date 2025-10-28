package com.pnemani.parser;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.pnemani.exceptions.JsonParserException;
import com.pnemani.types.impl.JsonElement;

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
    JsonParserImpl parser;

    // @BeforeAll
    // void setup() {
    //     parser = new JsonParserImpl(JSON);
    // }

    @Test 
    void constructorTest() {
        // Test if the string is empty
        parser = new JsonParserImpl(); 
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
        parser = new JsonParserImpl(); 
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
        parser = new JsonParserImpl();
            assertThrows(JsonParserException.class, () -> {parser.parseString(INVALID_JSON);});
    }
}

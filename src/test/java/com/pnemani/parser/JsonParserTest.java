package com.pnemani.parser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import com.pnemani.exceptions.JsonParserException;
import com.pnemani.types.JsonElement;

public class JsonParserTest {
    private final File VALID_FILE = new File("src/test/resources/parserTest.json");

    private final String VALID_JSON = """
            {
                "name": "value",
                "age": 32,
                "banks": [
                    "chase",
                    "chime",
                    "wells_fargo"
                ],
                "isCitizen": false,
                "location": null,
                "occupation": {
                    "organization": "chase",
                    "position": "developer"
                }
            }
            """;

    private final String INVALID_JSON = """
        {
            "name": "value",
            "age" : 32,
        }
            """;
    
    private ParserImpl parser;

    @Test 
    void validParseTest() {
        // Test if the string is empty
        parser = new ParserImpl(); 
        try { 
            assertDoesNotThrow(() -> parser.parse(VALID_JSON));
            JsonElement readValue = parser.parse(VALID_JSON);
            
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
    void invalidParseTest() {
        parser = new ParserImpl();
            assertThrows(JsonParserException.class, () -> {parser.parse(INVALID_JSON);});
    }

    @Test
    void validParseFileTest() {
        parser = new ParserImpl();

        try {
            assertDoesNotThrow(() -> parser.parse(VALID_FILE));
            JsonElement readValue = parser.parse(VALID_FILE);

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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new AssertionError("File not found in the specified location");
        } catch (JsonParserException ex){
            ex.printStackTrace();
            throw new AssertionError("Json cannot be parsed");
        }
    }
    
}

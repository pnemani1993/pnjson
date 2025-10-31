package com.pnemani.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import com.pnemani.exceptions.JsonConversionException;
import com.pnemani.exceptions.JsonParserException;
import com.pnemani.model.Occupation;
import com.pnemani.model.Person;
import com.pnemani.model.PersonRecord;
import com.pnemani.model.Student;
import com.pnemani.parser.JsonReader;

public class JsonReaderTest {
    
    private final JsonReader reader = new ParserImpl();

    private final String VALID_JSON = """
            {
                "name": "value",
                "age" : 32,
                "banks": ["fetch", "ring", "wells_neargo"],
                "isCitizen": false,
                "location": "London",
                "occupation": {
                        "organization": "byte_went",
                        "position": "developer"
                    }, 
                "personnel": [
                    {
                        "name": "value_inner_1",
                        "age" : 33,
                        "banks": ["ruralbank", "mex_express"],
                        "isCitizen": true,
                        "location": "Paris",
                        "occupation": {
                                "organization": "tmov",
                                "position": "marketing"
                            }
                    },
                    {
                        "name": "value_inner_2",
                        "age" : 34,
                        "banks": ["liabilityOne", "axes"],
                        "isCitizen": true,
                        "location": "Vienna",
                        "occupation": {
                                "organization": "perizon",
                                "position": "sales"
                            }
                    }
                ]
            }
            """; 

        private final String INVALID_JSON = """
                    {
                "name": "value",
                "age" : 32,
                "banks": ["fetch", "ring", "wells_neargo"],
                "isCitizen": false,
                "location": "London",
                "occupation": {
                        "organization": "byte_went",
                        "position": "developer"
                    },
                "personnel": [
                    {
                        "name": "value_inner_1",
                        "age" : 33,
                        "banks": ["ruralbank", "mex_express"],
                        "isCitizen": true,
                        "location": "Paris",
                        "occupation": {
                                "organization": "tmov",
                                "position": "marketing",
                            }
                    },
                    {
                        "name": "value_inner_2",
                        "age" : 34,
                        "banks": ["liabilityOne", "axes"],
                        "isCitizen": true,
                        "location": "Vienna",
                        "occupation": {
                                "organization": "perizon",
                                "position": "sales"
                            }
                    }
                ]
            } 
                """;

        @Test
        void readValueTest() {
            Person person;
            try{
                person = reader.readValue(VALID_JSON, Person.class);
            } catch (JsonConversionException | JsonParserException ex){
                throw new AssertionFailedError("The json cannot be processed: " + ex.getClass() + "\n" + ex.getMessage());
            }
            assertEquals(32, person.getAge());
            assertEquals("value", person.getName());
            assertEquals("fetch", person.getBank().get(0));
            assertEquals("London", person.getLocation());
            assertEquals("byte_went", person.getOccupation().getOrganization());
            assertEquals("developer", person.getOccupation().getPosition());
            assertEquals("value_inner_1", person.getPersonnel().get(0).name_value());
            assertEquals(33, person.getPersonnel().get(0).age_number());
            assertEquals("ruralbank", person.getPersonnel().get(0).bank_names().get(0));
            assertEquals("Paris", person.getPersonnel().get(0).location_name());
            assertEquals("marketing", person.getPersonnel().get(0).occupation_details().getPosition());
        }

        @Test
        void readInvalidValueTest() {
            assertThrows(JsonParserException.class, () -> reader.readValue(INVALID_JSON, Person.class));
        }

        @Test
        void readValueInvalidClassTest() {
            try{
                Student student = reader.readValue(VALID_JSON, Student.class);
                assertNull(student.getAddress());
                assertNull(student.getNameOfStudent());
                assertNull(student.getRollNo());

                PersonRecord personRecord = reader.readValue(VALID_JSON, PersonRecord.class);
                assertEquals("value", personRecord.name_value());
                assertEquals(32, personRecord.age_number());
                assertEquals("fetch", personRecord.bank_names().get(0));
                assertEquals("London", personRecord.location_name());
            } catch (JsonParserException | JsonConversionException ex){
                throw new AssertionFailedError();
            }
        }
}

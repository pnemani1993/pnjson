package com.pnemani.converter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.pnemani.model.Occupation;
import com.pnemani.model.Person;
import com.pnemani.model.PersonRecord;
import com.pnemani.converter.JsonConverter;

public class JsonConverterTest {
    private JsonConverter jsonConverter;
    private Person person;
    private Occupation occupation;
    private Occupation occupation_1;
    private Occupation occupation_2;
    private PersonRecord personRecord_1;
    private PersonRecord personRecord_2;
    
    @BeforeEach
    void setup() {
        occupation_1 = new Occupation("coffee-immobile\n", "destroyer");
        occupation_2 = new Occupation("pox\t", "intuitor");
        personRecord_1 = new PersonRecord("value_1", 33, List.of("fetch, ruralbank"), "Paris", occupation_1);
        personRecord_2 = new PersonRecord("value_2", 43, List.of("canadian_passenger, hills_nearcome"), "Cairo", occupation_2);
        occupation = new Occupation("buddy_sac\r", "team_follower");
        person = new Person("value", 32, List.of("liability_two, second_alien_bank"), "London", occupation, List.of(personRecord_1, personRecord_2));    
    }

    @Test
    void toJsonTest() {      
        String json = JsonConverter.convertToJson(person);
        assertTrue(json.contains("value"));
        assertTrue(json.contains("liability_two"));
        assertTrue(json.contains("canadian_passenger"));
    }

    @Test
    void convertToJsonTest() {
        jsonConverter = new JsonConverter();
        String json = jsonConverter.toJson(person);
        assertTrue(json.contains("value"));
        assertTrue(json.contains("liability_two"));
        assertTrue(json.contains("canadian_passenger"));
    }


}

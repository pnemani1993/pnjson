package com.pnemani.converter;

import java.util.List;

import com.pnemani.annotations.Json;

public class Person {
    @Json(name="name")
    private String name_value;
    @Json(name="age")
    private int age_number;
    @Json(name = "banks")
    private List<String> bank_names;
    @Json(name = "location")
    private String location_name;
    @Json(name="occupation")
    private Occupation occupation_details;
    @Json(name = "personnel")
    private List<PersonRecord> personnel;

    public Person() {}

    public Person(String name, int age, List<String> banks, String location, Occupation occupation, List<PersonRecord> personnel) {
        this.name_value = name;
        this.age_number = age;
        this.bank_names = banks;
        this.location_name = location;
        this.occupation_details = occupation;
        this.personnel = personnel;
    }

    public String getName() {
        return name_value;
    }

    public void setName(String name) {
        this.name_value = name;
    }

    public int getAge() {
        return age_number;
    }

    public void setAge(int age) {
        this.age_number = age;
    }

    public List<String> getBank() {
        return this.bank_names;
    }

    public void setBank(List<String> banks) {
        this.bank_names = banks;
    }

    public String getLocation() {
        return location_name;
    }

    public void setLocation(String location) {
        this.location_name = location;
    }

    public Occupation getOccupation() {
        return occupation_details;
    }

    public void setOccupation(Occupation occupation) {
        this.occupation_details = occupation;
    }

    public List<PersonRecord> getPersonnel() {
        return this.personnel;
    }

    public void setPersonnel(List<PersonRecord> personnel){
        this.personnel = personnel;
    }
}

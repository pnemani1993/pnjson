package com.pnemani.converter;

import java.util.List;

import com.pnemani.annotations.Json;

public record PersonRecord(
    @Json(name="name") String name_value,
    @Json(name="age") int age_number,
    @Json(name = "banks") List<String> bank_names,
    @Json(name = "location") String location_name,
    @Json(name="occupation") Occupation occupation_details) {}


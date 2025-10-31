package com.pnemani.parser;

import java.io.File;
import java.io.FileNotFoundException;

import com.pnemani.exceptions.JsonConversionException;
import com.pnemani.exceptions.JsonParserException;
import com.pnemani.types.JsonElement;

public interface JsonParser {
    
    JsonElement parse(String json) throws JsonParserException;

    JsonElement parse(File file) throws JsonParserException, FileNotFoundException;

    <T> T parse(String json, Class<T> classType) throws JsonParserException, JsonConversionException;

    <T> T parse(File file, Class<T> classType) throws JsonParserException, JsonConversionException, FileNotFoundException;
}

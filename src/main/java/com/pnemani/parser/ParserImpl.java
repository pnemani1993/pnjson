package com.pnemani.parser;

import com.pnemani.exceptions.JsonParserException;
import com.pnemani.types.JsonElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import com.pnemani.exceptions.JsonConversionException;

public final class ParserImpl extends JsonReader {

  public ParserImpl() {
    super();
  }

  @Override
  public JsonElement parse(String json) throws JsonParserException {
    return super.parseString(json);
  }

  @Override
  public JsonElement parse(File file) throws JsonParserException, FileNotFoundException {
    try (Scanner scan = new Scanner(file)) {
      StringBuilder sb = new StringBuilder();
      while (scan.hasNextLine()) {
        sb.append(scan.nextLine());
      }
      return super.parseString(sb.toString());
    } catch (FileNotFoundException ex) {
      throw new FileNotFoundException(ex.getMessage());
    }
  }

  @Override
  public <T> T parse(String json, Class<T> classType) throws JsonParserException, JsonConversionException {
    return super.readValue(json, classType);
  }

  @Override
  public <T> T parse(File file, Class<T> classType) throws JsonParserException, JsonConversionException, FileNotFoundException {
      try (Scanner scan = new Scanner(file)) {
        StringBuilder sb = new StringBuilder();
        while (scan.hasNextLine()) {
          sb.append(scan.nextLine());
        }
        return super.readValue(sb.toString(), classType);
      } catch (FileNotFoundException ex) {
          throw new FileNotFoundException(ex.getMessage());
      }
  }
}

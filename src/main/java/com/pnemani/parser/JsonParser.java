package com.pnemani.parser;

import com.pnemani.exceptions.JsonParserException;
import com.pnemani.model.impl.JsonElement;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public final class JsonParser extends JsonParserImpl {

  public JsonParser() {
    super();
  }

  public JsonElement parse(String json) throws JsonParserException {
    return super.parseString(json);
  }

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
}

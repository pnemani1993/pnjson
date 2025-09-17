package com.pnemani.parser;

import com.pnemani.exceptions.JsonParserException;
import com.pnemani.model.impl.JsonElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonParserImpl {

  private String in;
  private int pos = 0;

  protected JsonParserImpl(String json) {
    this.in = json;
  }

  protected JsonParserImpl() {
    this.in = "";
  }

  protected JsonElement parseString(String json) throws JsonParserException {
    this.in = json;
    this.pos = 0;
    JsonElement value = this.parseValue();
    this.skipWhitespace();
    if (!this.isEOF()) {
      throw this.error("Unexpected trailing characters");
    }
    return value;
  }

  private JsonElement parseValue() throws JsonParserException {
    skipWhitespace();
    if (isEOF()) throw error("Unexpected end of input while parsing value");
    char c = peek();
    switch (c) {
      case '{' -> {
        return parseObject();
      }
      case '[' -> {
        return parseArray();
      }
      case '"' -> {
        return parseString();
      }
      case 't' -> {
        return parseLiteral("true", Boolean.TRUE);
      }
      case 'T' -> {
        return parseLiteral("True", Boolean.TRUE);
      }
      case 'f' -> {
        return parseLiteral("false", Boolean.FALSE);
      }
      case 'F' -> {
        return parseLiteral("False", Boolean.FALSE);
      }
      case 'n' -> {
        return parseLiteral("null", null);
      }
      case 'N' -> {
        return parseLiteral("Null", null);
      }
      default -> {
        if (c == '-' || isDigit(c)) {
          return parseNumber();
        }
        throw error("Unexpected character while parsing value: " + c);
      }
    }
  }

  private JsonElement parseObject() throws JsonParserException {
    expect('{');
    skipWhitespace();
    Map<String, JsonElement> map = new LinkedHashMap<>();
    if (peek() == '}') {
      expect('}');
      return new JsonElement(map);
    }
    OUTER:
    while (true) {
      skipWhitespace();
      if (peek() != '"') throw error("Expected string for object key");
      String key = parseStringKey();
      skipWhitespace();
      expect(':');
      JsonElement val = parseValue();
      map.put(key, val);
      skipWhitespace();
      char c = peek();
      switch (c) {
        case ',' -> expect(',');
        case '}' -> {
          expect('}');
          break OUTER;
        }
        default -> throw error("Expected ',' or '}' in object, got: " + c);
      }
    }
    return new JsonElement(map);
  }

  private JsonElement parseArray() throws JsonParserException {
    expect('[');
    skipWhitespace();
    List<JsonElement> list = new ArrayList<>();
    if (peek() == ']') {
      expect(']');
      return new JsonElement(list);
    }
    OUTER:
    while (true) {
      JsonElement v = parseValue();
      list.add(v);
      skipWhitespace();
      char c = peek();
      switch (c) {
        case ',' -> expect(',');
        case ']' -> {
          expect(']');
          break OUTER;
        }
        default -> throw error("Expected ',' or ']' in array, got: " + c);
      }
    }
    return new JsonElement(list);
  }

  private JsonElement parseString() throws JsonParserException {
    expect('"');
    StringBuilder sb = new StringBuilder();
    while (true) {
      if (isEOF()) throw error("Unterminated string");
      char c = next();
      if (c == '"') break;
      if (c == '\\') {
        if (isEOF()) throw error("Unterminated escape sequence in string");
        char esc = next();
        switch (esc) {
          case '"' -> sb.append('"');
          case '\\' -> sb.append('\\');
          case '/' -> sb.append('/');
          case 'b' -> sb.append('\b');
          case 'f' -> sb.append('\f');
          case 'n' -> sb.append('\n');
          case 'r' -> sb.append('\r');
          case 't' -> sb.append('\t');
          case 'u' -> sb.append(parseUnicodeEscape());
          default -> throw error("Invalid escape sequence: \\" + esc);
        }
      } else {
        sb.append(c);
      }
    }
    return new JsonElement(sb.toString());
  }

  private String parseStringKey() throws JsonParserException {
    expect('"');
    StringBuilder sb = new StringBuilder();
    while (true) {
      if (isEOF()) throw error("Unterminated string");
      char c = next();
      if (c == '"') break;
      if (c == '\\') {
        if (isEOF()) throw error("Unterminated escape sequence in string");
        char esc = next();
        switch (esc) {
          case '"' -> sb.append('"');
          case '\\' -> sb.append('\\');
          case '/' -> sb.append('/');
          case 'b' -> sb.append('\b');
          case 'f' -> sb.append('\f');
          case 'n' -> sb.append('\n');
          case 'r' -> sb.append('\r');
          case 't' -> sb.append('\t');
          case 'u' -> sb.append(parseUnicodeEscape());
          default -> throw error("Invalid escape sequence: \\" + esc);
        }
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  private char parseUnicodeEscape() throws JsonParserException {
    int val = 0;
    for (int i = 0; i < 4; i++) {
      if (isEOF()) throw error("Incomplete \\u escape");
      char h = next();
      int digit = hexDigit(h);
      if (digit < 0) throw error("Invalid hex digit in \\u escape: " + h);
      val = (val << 4) + digit;
    }
    return (char) val;
  }

  private int hexDigit(char c) {
    if (c >= '0' && c <= '9') return c - '0';
    if (c >= 'a' && c <= 'f') return 10 + (c - 'a');
    if (c >= 'A' && c <= 'F') return 10 + (c - 'A');
    return -1;
  }

  private JsonElement parseNumber() throws JsonParserException {
    int start = pos;
    char c = peek();
    if (c == '-') pos++;
    boolean atLeastOneDigit = false;
    while (!isEOF() && isDigit(peek())) {
      pos++;
      atLeastOneDigit = true;
    }
    if (!atLeastOneDigit) throw error("Invalid number, expected digits");

    // fraction
    if (!isEOF() && peek() == '.') {
      pos++;
      if (isEOF() || !isDigit(peek())) throw error("Invalid number, expected digits after '.'");
      while (!isEOF() && isDigit(peek())) pos++;
    }

    // exponent
    if (!isEOF() && (peek() == 'e' || peek() == 'E')) {
      pos++;
      if (!isEOF() && (peek() == '+' || peek() == '-')) pos++;
      if (isEOF() || !isDigit(peek()))
        throw error("Invalid number, expected digits after exponent");
      while (!isEOF() && isDigit(peek())) pos++;
    }

    String numStr = in.substring(start, pos);
    try {
      return new JsonElement(new BigDecimal(numStr));
    } catch (NumberFormatException ex) {
      throw error("Invalid number format: " + numStr);
    }
  }

  private JsonElement parseLiteral(String literal, Boolean value) throws JsonParserException {
    for (int i = 0; i < literal.length(); i++) {
      if (isEOF() || next() != literal.charAt(i)) throw error("Unexpected literal");
    }
    return value != null ? new JsonElement(value) : null;
  }

  private void skipWhitespace() {
    while (!isEOF()) {
      char c = peek();
      if (c == ' ' || c == '\t' || c == '\n' || c == '\r') pos++;
      else break;
    }
  }

  private boolean isEOF() {
    return pos >= in.length();
  }

  private char peek() {
    return in.charAt(pos);
  }

  private char next() {
    return in.charAt(pos++);
  }

  private void expect(char c) throws JsonParserException {
    if (isEOF() || next() != c) throw error("Expected '" + c + "'");
  }

  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  private JsonParserException error(String msg) {
    int line = 1, col = 1;
    for (int i = 0; i < Math.min(pos, in.length()); i++) {
      if (in.charAt(i) == '\n') {
        line++;
        col = 1;
      } else col++;
    }
    return new JsonParserException(
        msg + " at position " + pos + " (line " + line + ", col " + col + ")", pos);
  }
}

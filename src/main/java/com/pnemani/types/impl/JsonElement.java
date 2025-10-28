package com.pnemani.types.impl;

import com.pnemani.exceptions.InvalidOperationException;
import com.pnemani.types.JsonArray;
import com.pnemani.types.JsonBoolean;
import com.pnemani.types.JsonNumber;
import com.pnemani.types.JsonObject;
import com.pnemani.types.JsonString;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonElement implements JsonArray, JsonBoolean, JsonNumber, JsonObject, JsonString {
  private final Map<String, JsonElement> mapElement;
  private final String textElement;
  private final List<JsonElement> listElement;
  private final BigDecimal numberElement;
  private final Boolean booleanElement;
  private final Class<?> classType;
  private final String declaredField;

  public JsonElement(Map<String, JsonElement> map) {
    this.mapElement = map;
    this.textElement = null;
    this.listElement = null;
    this.numberElement = null;
    this.booleanElement = null;
    this.classType = map.getClass();
    this.declaredField = "mapElement";
  }

  public JsonElement(List<JsonElement> list) {
    this.mapElement = null;
    this.textElement = null;
    this.listElement = list;
    this.numberElement = null;
    this.booleanElement = null;
    this.classType = list.getClass();
    this.declaredField = "listElement";
  }

  public JsonElement(String text) {
    this.mapElement = null;
    this.textElement = text;
    this.listElement = null;
    this.numberElement = null;
    this.booleanElement = null;
    this.classType = text.getClass();
    this.declaredField = "textElement";
  }

  public JsonElement(Boolean value) {
    this.mapElement = null;
    this.textElement = null;
    this.listElement = null;
    this.numberElement = null;
    this.booleanElement = value;
    this.classType = booleanElement.getClass();
    this.declaredField = "booleanElement";
  }

  public JsonElement(BigDecimal number) {
    this.mapElement = null;
    this.textElement = null;
    this.listElement = null;
    this.numberElement = number;
    this.booleanElement = null;
    this.classType = number.getClass();
    this.declaredField = "numberElement";
  }

  public Class<?> getClassType() {
    return this.classType;
  }

  public boolean isObject() {
    return this.mapElement != null;
  }

  public Map<String, JsonElement> getMap() {
    if (isObject()) return this.mapElement;
    else return null;
  }

  public boolean isIterable() {
    return this.listElement != null;
  }

  public boolean isText() {
    return this.textElement != null;
  }

  public boolean isNumber() {
    return this.numberElement != null;
  }

  public boolean isBoolean() {
    return this.booleanElement != null;
  }

  @Override
  public Boolean getBoolean() {
    if (!isBoolean()) {
      throw new InvalidOperationException(errorString("getBoolean()"));
    }
    return this.booleanElement;
  }

  @Override
  public Iterator<JsonElement> getIterator() {
    if (!isIterable()) {
      throw new InvalidOperationException(errorString("getIterator()"));
    }
    return this.listElement.iterator();
  }

  @Override
  public JsonElement get(int index) {
    if (!isIterable()) {
      throw new InvalidOperationException(errorString("get(int index)"));
    }
    return this.listElement.get(index);
  }

  @Override
  public List<JsonElement> getList() {
    if (!isIterable()) {
      throw new InvalidOperationException(errorString("getList()"));
    }
    return this.listElement;
  }

  @Override
  public Integer getNumberAsInt() {
    if (!isNumber()) {
      throw new InvalidOperationException(errorString("getNumberAsInt()"));
    }
    return this.numberElement.intValue();
  }

  @Override
  public Long getNumberAsLong() {
    if (!isNumber()) {
      throw new InvalidOperationException(errorString("getNumberAsLong()"));
    }
    return this.numberElement.longValue();
  }

  @Override
  public Double getNumberAsDouble() {
    if (!isNumber()) {
      throw new InvalidOperationException(errorString("getNumberAsDouble()"));
    }
    return this.numberElement.doubleValue();
  }

  @Override
  public JsonElement get(String key) {
    if (!isObject()) {
      throw new InvalidOperationException(errorString("get(String key)"));
    }
    return this.mapElement.get(key);
  }

  @Override
  public String getText() {
    if (!isText()) {
      throw new InvalidOperationException(errorString("getText()"));
    }
    return this.textElement;
  }

  @SuppressWarnings("unchecked")
  public boolean equals(JsonElement element) throws InvalidOperationException {
    if (element instanceof JsonElement) {
      if (element.getClassType() != this.getClassType()) return false;
      try {
        Field comparisonField = element.getClass().getDeclaredField(this.declaredField);
        comparisonField.setAccessible(true);
        var compareObject = comparisonField.get(element);
        if (element.isObject()) {
          return compareMaps(this.mapElement, (Map<String, JsonElement>) compareObject);
        } else if (element.isIterable()) {
          return compareLists(this.listElement, (List<JsonElement>) compareObject);
        } else if (element.isBoolean()) {
          return this.booleanElement.compareTo((Boolean) compareObject) == 0;
        } else if (element.isNumber()) {
          return this.numberElement.compareTo((BigDecimal) compareObject) == 0;
        } else if (element.isText()) {
          return this.textElement.contentEquals(compareObject.toString());
        }
      } catch (IllegalAccessException
          | IllegalArgumentException
          | NoSuchFieldException
          | SecurityException ex) {
        throw new InvalidOperationException(ex.getMessage());
      }
      return true;
    } else {
      return false;
    }
  }

  private boolean compareMaps(
      Map<String, JsonElement> element1, Map<String, JsonElement> element2) {
    Set<String> keySet1 = element1.keySet();
    Set<String> keySet2 = element2.keySet();
    if (keySet1.size() != keySet2.size() || !keySet1.containsAll(keySet2)) return false;
    for (String key : keySet1) {
      if (element1.get(key) == null && element2.get(key) == null)
        ;
      else if (element1.get(key) == null && element2.get(key) != null) return false;
      else if (element1.get(key) != null && element2.get(key) == null) return false;
      else if (!element1.get(key).equals(element2.get(key))) return false;
    }
    return true;
  }

  private boolean compareLists(List<JsonElement> list1, List<JsonElement> list2) {
    if (list1.size() != list2.size()) return false;
    for (JsonElement element : list1) {
      if (!list2.stream().anyMatch(e -> e.equals(element))) return false;
    }
    return true;
  }

  private String errorString(String methodName) {
    return String.format(
        "Method %s cannot be called on JsonElement of type: %s", methodName, this.getClassType());
  }

  @Override
  public String toString() {
    if (isObject()) return this.convertMapToString();
    else if (isIterable()) return this.convertListToString();
    else if (isText()) return this.convertTextToString();
    else if (isNumber()) return this.numberElement.toString();
    else if (isBoolean()) return this.booleanElement.toString();
    else return null;
  }

  private String convertMapToString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    this.mapElement.forEach((key, value) -> {
      if (value != null) sb.append("\"" + key + "\"" + ": " + value.toString() + ",");
      else sb.append("\"" + key + "\"" + ": null," );
    });
    sb.deleteCharAt(sb.length() - 1);
    sb.append("}");
    return sb.toString();
  }

  private String convertListToString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    this.listElement.forEach(value -> {
      sb.append(value.toString() + ",");
    });
    sb.deleteCharAt(sb.length()-1);
    sb.append("]");
    return sb.toString();
  }

  private String convertTextToString() {
    return "\"" + this.textElement  + "\"";
  }

}

package com.pnemani.model;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

public class JsonElement 
implements JsonArray, JsonBoolean, JsonNumber, JsonObject, JsonString
{
    private final Map<String, JsonElement> mapElement;
    private final String textElement;
    private final List<JsonElement> listElement;
    private final BigDecimal number;
    private final Boolean bool;
    private final Class<?> classType;

    public JsonElement(Map<String, JsonElement> map){
        this.mapElement = map;
        this.textElement = null;
        this.listElement = null;
        this.number = null;
        this.bool = null;
        this.classType = map.getClass();
    }

    public JsonElement(List<JsonElement> list) {
        this.mapElement = null;
        this.textElement = null;
        this.listElement = list;
        this.number = null;
        this.bool = null;
        this.classType = list.getClass();
    }

    public JsonElement(String text) {
        this.mapElement = null;
        this.textElement = text;
        this.listElement = null;
        this.number = null;
        this.bool = null;
        this.classType = text.getClass();
    }

    public JsonElement(Boolean value) {
        this.mapElement = null;
        this.textElement = null;
        this.listElement = null;
        this.number = null;
        this.bool = value;
        this.classType = bool.getClass();
    }

    public JsonElement(BigDecimal number) {
        this.mapElement = null;
        this.textElement = null;
        this.listElement = null;
        this.number = number;
        this.bool = null;
        this.classType = number.getClass();
    }

    public Class<?> getClassType() {
        return this.classType;
    }

        public boolean isObject() {return this.mapElement != null;}

        public boolean isIterable() {return this.listElement != null;}

        public boolean isText() {return this.textElement != null;}

        public boolean isNumber() {return this.number != null;}

        public boolean isBoolean() {return this.bool != null;}

        @Override
        public String toString() {
            if (isObject()) return this.mapElement.toString();
            else if (isIterable()) return this.listElement.toString();
            else if (isText()) return this.textElement;
            else if (isNumber()) return this.number.toString();
            else if (isBoolean()) return this.bool.toString();
            else return null;
        }

        @Override
        public Boolean getBoolean() {
            return this.bool;
        }

    @Override
    public Iterator<JsonElement> getIterator() {
        if (this.listElement == null) return null;
        return this.listElement.iterator();
    }

    @Override
    public JsonElement get(int index) {
        if (this.listElement == null) return null;
        return this.listElement.get(index);
    }

    @Override
    public List<JsonElement> getList() {
        if (this.listElement == null) return null;
        return this.listElement;
    }

    @Override
    public int getNumberAsInt() {
        if (this.number == null) return -1;
        return this.number.intValue();
    }

    @Override
    public long getNumberAsLong() {
        if (this.number == null) return -1L;
        return this.number.longValue();
    }

    @Override
    public double getNumberAsDouble() {
        if (this.number == null) return -1;
        return this.number.doubleValue();
    }

    @Override
    public JsonElement getValue(String key) {
        if(this.mapElement == null) return null;
        return this.mapElement.get(key);
    }

    @Override
    public String getText() {
        if(this.textElement == null) return null;
        return this.textElement;
    }
}

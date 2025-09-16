package com.pnemani.model;

import java.util.Map;
import java.util.List;

public class JsonElement {
    private final Map<String, JsonElement> mapElement;
    private final String textElement;
    private final List<JsonElement> listElement;
    private final Class classType;

    public JsonElement(Map<String, JsonElement> map){
        this.mapElement = map;
        this.textElement = null;
        this.listElement = null;
        this.classType = map.getClass();

    }

    public JsonElement(List<JsonElement> list) {
        this.mapElement = null;
        this.textElement = null;
        this.listElement = list;
        this.classType = list.getClass();
    }

    public JsonElement(String text) {
        this.mapElement = null;
        this.textElement = text;
        this.listElement = null;
        this.classType = text.getClass();
    }

}

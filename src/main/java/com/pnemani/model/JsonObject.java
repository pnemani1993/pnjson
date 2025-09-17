package com.pnemani.model;

import com.pnemani.model.impl.JsonElement;

public interface JsonObject {

  JsonElement get(String key);
}

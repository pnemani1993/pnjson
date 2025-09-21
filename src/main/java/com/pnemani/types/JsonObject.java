package com.pnemani.types;

import com.pnemani.types.impl.JsonElement;

public interface JsonObject {

  JsonElement get(String key);
}

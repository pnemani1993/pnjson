package com.pnemani.types;

import java.util.Iterator;
import java.util.List;

import com.pnemani.types.impl.JsonElement;

public interface JsonArray {

  Iterator<JsonElement> getIterator();

  JsonElement get(int index);

  List<JsonElement> getList();
}

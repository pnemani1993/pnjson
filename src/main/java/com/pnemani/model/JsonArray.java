package com.pnemani.model;

import com.pnemani.model.impl.JsonElement;
import java.util.Iterator;
import java.util.List;

public interface JsonArray {

  Iterator<JsonElement> getIterator();

  JsonElement get(int index);

  List<JsonElement> getList();
}

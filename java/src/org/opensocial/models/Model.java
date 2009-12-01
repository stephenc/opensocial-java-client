package org.opensocial.models;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Model extends JSONObject {

  public String[] getFieldNames() {
    int i = 0;
    String[] fieldNames = new String[size()];

    Set<Map.Entry<String, Object>> fields = entrySet();
    for (Map.Entry<String, Object> field : fields) {
      fieldNames[i] = field.getKey();
      i++;
    }

    return fieldNames;
  }

  public boolean hasField(String fieldName) {
    return containsKey(fieldName);
  }

  public Object getField(String fieldName) {
    return get(fieldName);
  }

  public Map getFieldAsMap(String fieldName) {
    return (Map) get(fieldName);
  }

  public List getFieldAsList(String fieldName) {
    return (List) get(fieldName);
  }

  public String getFieldAsString(String fieldName) {
    return (String) get(fieldName);
  }

  public boolean isFieldMultikeyed(String fieldName) {
    Object field = get(fieldName);
    if (field.getClass().equals(String.class) ||
        field.getClass().equals(JSONArray.class)) {
      return false;
    }

    return true;
  }

  public boolean isFieldMultivalued(String fieldName) {
    Object field = get(fieldName);
    if (field.getClass().equals(JSONArray.class)) {
      return true;
    }

    return false;
  }
}

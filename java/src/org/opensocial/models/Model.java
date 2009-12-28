/* Copyright (c) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

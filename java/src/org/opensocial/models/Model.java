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

import java.util.ArrayList;
import java.util.HashMap;
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
    try {
      return (String) get(fieldName);
    } catch (ClassCastException e) {
      return "" + get(fieldName);
    }
  }

  protected String getTemplateParameter(String key) {
    if (containsKey("templateParameters")) {
      List<Map<String, String>> templateParameters =
        getFieldAsList("templateParameters");

      for (Map<String, String> parameter : templateParameters) {
        if (parameter.get("key").equals(key)) {
          return parameter.get("value");
        }
      }
    }

    return null;
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

  protected void addToListField(String fieldName, Object item) {
    List<Object> listField = null;

    if (containsKey(fieldName)) {
      listField = getFieldAsList(fieldName);
    } else {
      listField = new ArrayList<Object>();
    }

    listField.add(item);
    put(fieldName, listField);
  }

  protected void addTemplateParameter(String key, String value) {
    List<Map<String, String>> templateParameters = null;

    if (containsKey("templateParameters")) {
      templateParameters = getFieldAsList("templateParameters");
    } else {
      templateParameters = new ArrayList<Map<String, String>>();
    }

    Map<String, String> templateParameter = new HashMap<String, String>();
    templateParameter.put("key", key);
    templateParameter.put("value", value);

    templateParameters.add(templateParameter);
    put("templateParameters", templateParameters);
  }
}

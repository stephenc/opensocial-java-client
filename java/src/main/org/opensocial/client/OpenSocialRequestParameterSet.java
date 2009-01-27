/* Copyright (c) 2008 Google Inc.
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


package org.opensocial.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OpenSocialRequestParameterSet {

  private Map<String, OpenSocialRequestParameter> parameters;

  public OpenSocialRequestParameterSet() {
    this.parameters = new HashMap<String, OpenSocialRequestParameter>();
  }

  public void addParameters(OpenSocialRequestParameterSet set) {
    for (Map.Entry<String, OpenSocialRequestParameter> entry : set.parameters.entrySet()) {
      this.parameters.put(entry.getKey(), entry.getValue());
    }
  }

  public void addParameter(String key, String[] values) {
    this.parameters.put(key, new OpenSocialRequestParameter(values));
  }

  public void addParameter(String key, String value) {
    this.parameters.put(key, new OpenSocialRequestParameter(value));    
  }

  public void addParameter(String key, int value) {
    this.addParameter(key, String.valueOf(value));
  }

  public boolean hasParameter(String key) {
    return this.parameters.containsKey(key);
  }

  public Map<String, OpenSocialRequestParameter> getParameters() {
    return this.parameters;
  }

  public String getParameter(String key) {
    OpenSocialRequestParameter param = this.parameters.get(key);
    
    if (param != null) {
      return param.getValuesString();
    }
    
    return null;
  }

  public void removeParameter(String key) {
    this.parameters.remove(key);
  }

  public String toJson() {
    Map<String, Object> valuesMap = new HashMap<String, Object>();

    try {
      for (Map.Entry<String, OpenSocialRequestParameter> entry : this.parameters.entrySet()) {
        if (entry.getValue().isMultivalued()) {
          valuesMap.put(entry.getKey(), new JSONArray(entry.getValue().getValuesArray()));
        } else {
          valuesMap.put(entry.getKey(), entry.getValue().getValuesString());
        }
      }
    } catch (JSONException e) {}

    return new JSONObject(valuesMap).toString();
  }
}

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

package org.opensocial.client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * An object which represents a single OpenSocial REST/JSON-RPC request, which
 * are collected and submitted using one or more OpenSocialBatch instances.
 * Instances should not be created directly but should instead be returned
 * from static OpenSocialClient methods which take care to set all
 * properties appropriately given the request type.
 *
 * @author apijason@google.com (Jason Cooper)
 */
public class OpenSocialRequest {

  private String id;
  private String rpcMethod;
  private String restMethod;
  private String restPathComponent;
  private Map<String, OpenSocialRequestParameter> parameters;

  public OpenSocialRequest(String restPathComponent, String restMethod,
      String rpcMethod) {
    this.parameters = new HashMap<String, OpenSocialRequestParameter>();
    this.restPathComponent = restPathComponent;
    this.restMethod = restMethod;
    this.rpcMethod = rpcMethod;
  }

  public OpenSocialRequest(String restPathComponent, String rpcMethod) {
    this(restPathComponent, "GET", rpcMethod);
  }

  /**
   * Sets instance variable id to passed String.
   */
  public void setId(String id) {
    this.id = id;
  }

  public void setParameters(Map<String, OpenSocialRequestParameter> params) {
    this.parameters = params;
  }

  public void addParameter(String key, Map<String, String> valuesMap) {
    this.parameters.put(key, new OpenSocialRequestParameter(valuesMap));
  }

  public void addParameter(String key, String[] values) {
    this.parameters.put(key, new OpenSocialRequestParameter(values));
  }

  public void addParameter(String key, String value) {
    this.parameters.put(key, new OpenSocialRequestParameter(value));
  }

  /**
   * @return true if a parameter with the given key is registered, false
   *         otherwise
   */
  public boolean hasParameter(String key) {
    return this.parameters.containsKey(key);
  }

  /**
   * @return value of the parameter with the given name or null if no
   *         parameter with that name exists
   */
  public String getParameter(String key) {
    OpenSocialRequestParameter param = this.parameters.get(key);

    if (param != null) {
      return param.getValuesString();
    }

    return null;
  }

  public Set<Map.Entry<String, OpenSocialRequestParameter>> getParameters() {
    return this.parameters.entrySet();
  }

  public String popParameter(String key) {
    String value = getParameter(key);
    this.parameters.remove(key);

    return value;
  }

  /**
   * @return instance variable restPathComponent with trailing backslash
   */
  public String getRestPathComponent() {
    return this.restPathComponent;
  }

  public String getRestMethod() {
    return this.restMethod;
  }

  public String getId() {
    return this.id;
  }

  /**
   * Returns a JSON-RPC serialization of the request including ID, RPC method,
   * and all added parameters. Used by other classes when preparing to submit
   * an RPC batch request.
   *
   * @throws OpenSocialRequestException
   */
  public String toJson() throws OpenSocialRequestException {
    JSONObject o = new JSONObject();

    try {
      if (this.id != null) {
        o.put("id", this.id);
      }
      o.put("method", this.rpcMethod);

      JSONObject params = new JSONObject();
      for (Map.Entry<String, OpenSocialRequestParameter> entry :
          this.parameters.entrySet()) {
        OpenSocialRequestParameter parameter = entry.getValue();
        String parameterName = entry.getKey();

        if (parameter.isMultikeyed()) {
          params.put(parameterName, new JSONObject(parameter.getValuesMap()));
        } else if (parameter.isMultivalued()) {
          params.put(parameterName, new JSONArray(parameter.getValuesList()));
        } else {
          params.put(parameterName, parameter.getValuesString());
        }
      }

      o.put("params", params);
    } catch (org.json.JSONException e) {
      throw new OpenSocialRequestException(
          "Unable to convert request to JSON string");
    }

    return o.toString();
  }
}

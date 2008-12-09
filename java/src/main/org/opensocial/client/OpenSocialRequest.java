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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * An object which represents a single OpenSocial REST/JSON-RPC request, which
 * are collected and submitted using one or more OpenSocialBatch instances.
 * Instances should not be created directly but should instead be returned
 * from static OpenSocialClient methods which take care to set all
 * properties appropriately given the request type.
 *
 * @author Jason Cooper
 */
public class OpenSocialRequest {

  private String id;
  private String rpcMethodName;
  private String restPathComponent;
  private Map<String, String> parameters;

  public OpenSocialRequest(String pathComponent, String methodName) {
    this.parameters = new HashMap<String, String>();

    this.restPathComponent = pathComponent;
    this.rpcMethodName = methodName;
    this.id = null;
  }

  /**
   * Returns instance variable: id.
   */
  public String getId() {
    return this.id;
  }

  /** 
   * Sets instance variable id to passed String.
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Creates a new entry in parameters Map with the passed key and value;
   * used for setting request-specific parameters such as appId, userId,
   * and groupId.
   */
  public void addParameter(String key, String value) {
    this.parameters.put(key, value);
  }

  /**
   * Returns the value of the parameter with the given name or null if
   * no parameter with that name exists.
   */
  public String getParameter(String parameter) {
    return this.parameters.get(parameter);
  }

  /**
   * Returns instance variable: restPathComponent. If path component does not
   * have a trailing backslash, one is added before being returned.
   */
  public String getRestPathComponent() {
    String component = this.restPathComponent;

    if (component.charAt(component.length()-1) != '/') {
      return (component + "/");
    }

    return component;
  }

  /**
   * Returns a JSON-RPC serialization of the request including ID, RPC method,
   * and all added parameters. Used by other classes when preparing to submit
   * an RPC batch request.
   * 
   * @throws JSONException
   */
  public String getJsonEncoding() throws JSONException {
    JSONObject o = new JSONObject();

    if (this.id != null) {
      o.put("id", this.id);      
    }

    o.put("method", this.rpcMethodName);
    o.put("params", new JSONObject(this.parameters));

    return o.toString();
  }
}

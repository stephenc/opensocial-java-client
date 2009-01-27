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

import java.util.Map;
import java.util.Set;

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
  private String rpcMethod;
  private String restMethod;
  private String restPathComponent;
  private OpenSocialRequestParameterSet parameters;

  public OpenSocialRequest(String restPathComponent, String restMethod, String rpcMethod) {
    this.restPathComponent = restPathComponent;
    this.restMethod = restMethod;
    this.rpcMethod = rpcMethod;

    this.parameters = new OpenSocialRequestParameterSet();
    this.id = null;
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

  public void setParameters(OpenSocialRequestParameterSet parameters) {
    this.parameters = parameters;
  }

  /**
   * Returns true if a parameter with the given key is registered, false
   * otherwise.
   */
  public boolean hasParameter(String key) {
    return this.parameters.hasParameter(key);
  }

  /**
   * Returns the value of the parameter with the given name or null if
   * no parameter with that name exists.
   */
  public String getParameter(String key) {
    return this.parameters.getParameter(key);
  }

  public Set<Map.Entry<String, OpenSocialRequestParameter>> getParameters() {
    return this.parameters.getParameters().entrySet();
  }

  public String popParameter(String key) {
    String value = this.parameters.getParameter(key);
    this.parameters.removeParameter(key);

    return value;
  }

  /**
   * Returns instance variable: restPathComponent. If path component does not
   * have a trailing backslash, one is added before being returned.
   */
  public String getRestPathComponent() {
    return this.restPathComponent;
  }
  
  public String getRestMethod() {
    return this.restMethod;
  }

  /**
   * Returns instance variable: id.
   */
  public String getId() {
    return this.id;
  }

  /**
   * Returns a JSON-RPC serialization of the request including ID, RPC method,
   * and all added parameters. Used by other classes when preparing to submit
   * an RPC batch request.
   * 
   * @throws JSONException
   */
  public String toJson() throws JSONException {
    JSONObject o = new JSONObject();

    if (this.id != null) {
      o.put("id", this.id);      
    }
    o.put("method", this.rpcMethod);
    o.put("params", new JSONObject(this.parameters.toJson()));

    return o.toString();
  }
}

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

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * An internal object responsible for constructing HTTP requests, opening
 * connections to specified endpoints, and returning the raw response
 * strings which can later be parsed into more meaningful objects.
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
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public void addParameter(String name, String value) {
    this.parameters.put(name, value);
  }
  
  public String getParameter(String parameter) {
    return this.parameters.get(parameter);
  }
  
  public String getRestPathComponent() {
    String component = this.restPathComponent;
    
    if (component.charAt(component.length()-1) != '/') {
      return (component + "/");
    }
    
    return component;
  }
  
  public String getJsonEncoding() throws JSONException {
    JSONObject o = new JSONObject();
    
    if (this.id != null) {
      o.put("id", this.id);      
    }
    
    o.put("method", this.rpcMethodName);
    o.put("params", new JSONObject(this.parameters));
    
    return o.toString();
  }
  
  public void addRestContextPath(OpenSocialUrl url) {
    url.addPathComponent(this.getRestPathComponent());
    
    if (this.getParameter("userId") != null) {
      url.addPathComponent(this.getParameter("userId"));        
    }
    if (this.getParameter("groupId") != null) {
      url.addPathComponent(this.getParameter("groupId"));        
    }
    if (this.getParameter("appId") != null) {
      url.addPathComponent(this.getParameter("appId"));        
    }
  }
}

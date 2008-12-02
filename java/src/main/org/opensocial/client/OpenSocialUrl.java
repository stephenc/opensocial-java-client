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
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class OpenSocialUrl {
  
  private String base;
  private List<String> components;
  private Map<String, String> queryStringParameters;
  
  public OpenSocialUrl(String base) {
    this.base = base;
    this.components = new Vector<String>();
    this.queryStringParameters = new HashMap<String, String>();
  }
  
  public void addPathComponent(String component) {
    components.add(component);
  }
  
  public void addStandardRestPathComponents(OpenSocialRequest r) {
    this.addPathComponent(r.getRestPathComponent());
    
    if (r.getParameter("userId") != null) {
      this.addPathComponent(r.getParameter("userId"));        
    }
    if (r.getParameter("groupId") != null) {
      this.addPathComponent(r.getParameter("groupId"));        
    }
    if (r.getParameter("appId") != null) {
      this.addPathComponent(r.getParameter("appId"));        
    }
  }
  
  public void addQueryStringParameter(String name, String value) {
    queryStringParameters.put(name, value);
  }
  
  public String toString() {
    StringBuilder s = new StringBuilder(this.base);
    
    if (s.charAt(s.length() - 1) != '/') {
      s.append("/");
    }
    
    for (String pathComponent : this.components) {
      if (s.charAt(s.length() - 1) != '/') {
        s.append("/");
      }
      s.append(pathComponent);
    }
    
    String connector = "?";
    for (Map.Entry<String, String> e : this.queryStringParameters.entrySet()) {
      s.append(connector);
      s.append(e.getKey());
      s.append("=");
      s.append(e.getValue());
      connector = "&";
    }
    
    return s.toString();
  }
}

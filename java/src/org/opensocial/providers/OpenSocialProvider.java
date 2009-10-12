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
package org.opensocial.providers;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensocial.client.OpenSocialHttpResponseMessage;
import org.opensocial.client.OpenSocialRequest;
import org.opensocial.data.OpenSocialAppData;

public class OpenSocialProvider 
{
  public String requestTokenUrl;
  public Map<String, String> requestTokenParams;
  public String authorizeUrl;
  public String accessTokenUrl;
  public String restEndpoint;
  public String rpcEndpoint;
  public String providerName;
  public boolean signBodyHash;
  public boolean isOpenSocial;
  public String contentType = "application/json";
  
  public OpenSocialProvider( String requestTokenUrl,String authorizeUrl, 
      String accessTokenUrl, String restEndpoint, String rpcEndpoint, 
      String providerName, boolean signBodyHash, boolean isOpenSocial ) {
      
    //TODO: check for vaild urls when setting. 
    this.requestTokenUrl = requestTokenUrl;
    this.authorizeUrl = authorizeUrl;
    this.accessTokenUrl = accessTokenUrl;
    this.restEndpoint = restEndpoint;
    this.rpcEndpoint = rpcEndpoint;
    this.providerName = providerName;
    this.isOpenSocial = isOpenSocial;
    this.signBodyHash = signBodyHash;
  }
  
  OpenSocialProvider(){}
  
  public void preRequest(OpenSocialRequest request) {
    _formatOutBoundAppData(request);
  }
  
  public void postRequest(OpenSocialRequest request, 
      OpenSocialHttpResponseMessage response) {
    _fixInBoundAppData(request, response);
  }
  
  protected void _fixInBoundAppData(OpenSocialRequest request, 
      OpenSocialHttpResponseMessage response) {

    if(request.getRestPathComponent().equals("appdata")) {
      try{
        String body = response.getBodyString();
        
        if(body.startsWith("{") && body.endsWith("}")) {
          JSONObject obj = new JSONObject(response.getBodyString());
          JSONArray entry = new JSONArray();
          
          if(obj.has("entry")) {
            obj = obj.getJSONObject("entry");
            
            JSONArray personIds = obj.names();
            OpenSocialAppData userAppData;
            
            if(personIds != null) {
              for(int i=0; i< personIds.length(); i++) {
                // Set person Id
                userAppData = new OpenSocialAppData();
                userAppData.setPersonId(personIds.getString(i));
                
                // Push all keys into appData as key value pairs
                obj = obj.getJSONObject(personIds.getString(i));
                JSONArray keys = obj.names();
                
                for(int j=0; j<keys.length(); j++) {
                  userAppData.setField(keys.getString(j), 
                      obj.getString(keys.getString(j)));
                }
                
                // Add to entry Array
                entry.put(userAppData);
                
                // Roll entry Array into response object
                obj = new JSONObject();
                obj.put("entry", entry);
              }
            }
          }else {
            obj = new JSONObject();
            obj.put("entry", entry);
          }
          response.setOpenSocialDataString(obj.toString());
        }
      } catch(JSONException e) {
        e.printStackTrace();
      }
    }
  }
  
  protected void _formatOutBoundAppData(OpenSocialRequest request) {
    
    if(request.getRestPathComponent().equals("appdata")) {
      if(request.getRestMethod().equals("POST") || 
          request.getRestMethod().equals("PUT")) {
        if(request.hasParameter("appdata")) {
          try {
            OpenSocialAppData appData = 
              new OpenSocialAppData(request.getParameter("appdata"));
            request.addParameter("data", appData.getJSONObject().toString());
            request.addParameter("appdata", 
                appData.getJSONObject().toString());
          }catch(JSONException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}

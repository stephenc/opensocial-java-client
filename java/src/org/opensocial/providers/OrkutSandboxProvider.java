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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensocial.client.OpenSocialRequest;

public class OrkutSandboxProvider extends OpenSocialProvider {
  
  public OrkutSandboxProvider() {
    super();
    
    restEndpoint = "http://sandbox.orkut.com/social/rest/";
    rpcEndpoint = "http://sandbox.orkut.com/social/rpc/";
    providerName = "orkut.com";
    signBodyHash = true;
    isOpenSocial = true;
  }
  
  public void preRequest(OpenSocialRequest request) {
    super.preRequest(request);
    _fixFields(request);
  }
  
  private void _fixFields(OpenSocialRequest request) {
    
    if(request.getRestPathComponent().equals("appdata")) {
      if(request.getRestMethod().equals("POST") || 
          request.getRestMethod().equals("PUT")) {
        if(request.hasParameter("appdata")) {
          try{
            JSONObject data = 
              new JSONObject(request.getParameter("appdata"));
            String fields = "";

            JSONArray keys = data.names();
            for(int i=0; i<keys.length(); i++) {
              fields+=","+keys.getString(i);
            }
            if(fields.length() > 0){
              fields = fields.substring(1);
            }
            request.addParameter("fields", fields);
          }catch(JSONException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }
}

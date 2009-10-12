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
package org.opensocial.services;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.opensocial.client.OpenSocialClient;
import org.opensocial.client.OpenSocialHttpResponseMessage;
import org.opensocial.client.OpenSocialRequest;

/**
 * OpenSocialService - base level class for each service endpoint to extend.
 * @author jle.edwards@gmail.com (Jesse Edwards)
 * 
 */
public class OpenSocialService {
  
  protected void _checkDefaultParams(Map <String, String> params) {
    if(!params.containsKey("userId")) params.put("userId", OpenSocialClient.ME);
    if(!params.containsKey("groupId")) params.put("groupId", OpenSocialClient.SELF);
  }
  
  /**
   * _addParamsToRequest - adds parameters to request
   * @param r
   * @param params
   */
  protected void _addParamsToRequest(OpenSocialRequest r, 
      Map<String, String> params) {
    
    for(String s : params.keySet()) {
      r.addParameter(s, params.get(s));
    }
  }
  
  public void formatResponse(OpenSocialHttpResponseMessage response) {
    String data = response.getOpenSocialDataString();
    
    try{
      if(data.startsWith("[") && data.endsWith("]")) {
        JSONArray supportedFields = new JSONArray(data);
        ArrayList<String> collection = new ArrayList<String>();
        
        for(int i=0; i<supportedFields.length(); i++) {
          collection.add(supportedFields.getString(i));
        }
        response.setCollection(collection);
      }
    }catch(JSONException e) {
      e.printStackTrace();
    }
  }
}

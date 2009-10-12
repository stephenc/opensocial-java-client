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
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensocial.client.OpenSocialHttpResponseMessage;
import org.opensocial.client.OpenSocialRequest;
import org.opensocial.client.OpenSocialRequestException;
import org.opensocial.data.OpenSocialGroup;

/**
 * GroupsService - service class for groups endpoint.
 * @author jle.edwards@gmail.com (Jesse Edwards)
 *
 */
public class GroupsService extends OpenSocialService {
  
  /**
   * getSupportedFields - sends request for supported fields.
   * @return OpenSocialRequest 
   * @throws OpenSocialRequestException
   */
  public OpenSocialRequest getSupportedFields() 
      throws OpenSocialRequestException {
    
    OpenSocialRequest r = new OpenSocialRequest("groups", 
        "GET", "groups.getSupportedFields");
    
    Map<String, String> params = new HashMap<String, String>();
    params.put("userId", "@supportedFields");
    
    _addParamsToRequest(r, params);
    return r;
  }
  
  /**
   * get - method used for fetching items in this service.
   * @param Map<String, String> params 
   * @return OpenSocialRequest
   * @throws OpenSocialRequestException
   */
  public OpenSocialRequest get(Map<String, String> params) 
      throws OpenSocialRequestException {

    super._checkDefaultParams(params);
    OpenSocialRequest r = new OpenSocialRequest("groups", 
        "GET", "groups.get");
    _addParamsToRequest(r, params);
    return r;
  }
  
  /**
   * update - method used for updating items in this service.
   * @param Map<String, String> params 
   * @return OpenSocialRequest
   * @throws OpenSocialRequestException
   */
  public OpenSocialRequest update(Map<String, String> params) 
      throws OpenSocialRequestException {
    throw new OpenSocialRequestException("This method is not supported.");
  }
  
  /**
   * create - method used for creating items in this service.
   * @param Map<String, String> params 
   * @return OpenSocialRequest
   * @throws OpenSocialRequestException
   */
  public OpenSocialRequest create(Map<String, String> params) 
      throws OpenSocialRequestException {
    throw new OpenSocialRequestException("This method is not supported.");
  }
  
  /**
   * delete - method used for deleting items in this service.
   * @param Map<String, String> params 
   * @return OpenSocialRequest
   * @throws OpenSocialRequestException
   */
  public OpenSocialRequest delete(Map<String, String> params) 
      throws OpenSocialRequestException {
    throw new OpenSocialRequestException("This method is not supported.");
  }
  
  /**
   * convertResponse - function used to convert response json into the expected
   * collection of objects or object.
   */
  public void formatResponse(OpenSocialHttpResponseMessage response) {
    super.formatResponse(response);

    String data= response.getOpenSocialDataString();
    OpenSocialGroup item = new OpenSocialGroup();
    ArrayList<OpenSocialGroup> collection = new ArrayList<OpenSocialGroup>();
    
    try{
      if(data.startsWith("{") && data.endsWith("}")) {
        JSONObject obj = new JSONObject(data);
        
        if(obj.has("entry")) {
          if(obj.getString("entry").startsWith("[") && 
              obj.getString("entry").endsWith("]")) {
            JSONArray entry = obj.getJSONArray("entry");
            
            for(int i=0; i<entry.length(); i++) {
              item = new OpenSocialGroup(entry.getJSONObject(i).toString());
              collection.add(item);
            }
          }else {
            collection.add(new OpenSocialGroup(obj.getString("entry")));
          }
        }
        response.setCollection(collection);
      }
    }catch(JSONException e) {
      e.printStackTrace();
      System.out.println(data);
    }
  }
}

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

package org.opensocial.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model object for Notification
 * @author Jesse Edwards
 */
public class MySpaceNotification extends OpenSocialModel
{
  public MySpaceNotification(){}

  public MySpaceNotification(String json) throws JSONException {
    super(json);
  }

  public void addRecipient(String recipient) {
    
    try {
      if(!this.has("recipientIds")) {
          this.put("recipientIds", new JSONArray());
      }
      JSONArray recp = this.getJSONArray("recipientIds");
      recp.put(recipient);
      this.put("recipientIds", recp);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public void addMediaItem(OpenSocialMediaItem item) { 
    try {
      if(!this.has("mediaItems")) {
          this.put("mediaItems", new JSONArray());
      }
      JSONArray mi = this.getJSONArray("mediaItems");
      mi.put(item);
      this.put("mediaItems", mi);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public void setTemplateParameter(String key, String value) {
    
    try {
      if(!this.has("templateParameters")) {
          this.put("templateParameters", new JSONArray());
      }
      
      JSONArray params = this.getJSONArray("templateParameters");
      JSONObject kvp = new JSONObject();
      kvp.put("key", key);
      kvp.put("value", value);
      params.put(kvp);
      this.put("templateParameters", params);
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public String getTemplateParameter(String key) {
    try {
      JSONArray params = this.getJSONArray("templateParameters");

      for (int i=0; i < params.length(); i++) {
        JSONObject obj = (JSONObject)params.get(i);
        if(obj.get("key").equals(key)) {
          return obj.getString("value");
        }
      }
    }catch(JSONException e) {
      e.printStackTrace();
    }
    return null;
  }
}


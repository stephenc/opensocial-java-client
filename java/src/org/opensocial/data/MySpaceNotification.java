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
  private JSONArray templateParameters = null;
  private JSONArray mediaItems = new JSONArray();
  private JSONArray recipients = new JSONArray();

  public MySpaceNotification(){}

  public MySpaceNotification(String json) throws JSONException {
    super(json);
  }

  public void addRecipient(String recipient) {
    recipients.put(recipient);
  }

  public void addMediaItem(OpenSocialMediaItem item) {
    mediaItems.put(item);
  }

  public void setTemplateParameter(String key, String value) 
      throws JSONException {
    
    if(templateParameters == null) {
      templateParameters = new JSONArray();
    }

    JSONObject kvp = new JSONObject();
    kvp.put(key, value);
    templateParameters.put(kvp);
  }

  public String getTemplateParameter(String key) throws JSONException {

    for (int i=0; i < templateParameters.length(); i++) {
      JSONObject obj = (JSONObject)templateParameters.get(i);

      if(obj.get("key").equals(key)) {
        return obj.getString("value");
      }
    }
    return null;
  }
}


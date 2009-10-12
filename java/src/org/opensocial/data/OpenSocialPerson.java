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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model object for Person
 * @author Jesse Edwards
 */
public class OpenSocialPerson extends OpenSocialModel
{
  public OpenSocialPerson(){}
  
  public OpenSocialPerson(String json) throws JSONException {
    super(json);
  }
  
  /**
   * Retrieves the OpenSocial ID associated with the instance. Returns an
   * empty string if no ID has been set.
   */
  public String getId() {
    try{
      if(this.has("id")) {
        return this.getString("id");
      }
    }catch(JSONException e) {
      e.printStackTrace();
    }
    return "";
  }

  /**
   * Retrieves the display name (typically given name followed by family name)
   * associated with the instance. Returns an empty string if no name has been
   * set.
   */
  public String getDisplayName() {
    try{
      if(this.has("displayName"))
        return this.getString("displayName");
      else if(this.has("name")) {
        JSONObject name = this.getJSONObject("name");
        String displayName = "";
        displayName+= name.has("givenName") ? name.getString("givenName") : "";
        displayName+= name.has("familyName") ? " "+name.getString("familyName") : "";
        
        return displayName;
      }
    }catch(JSONException e) {
      e.printStackTrace();
    }
    
    return "Unknown Person";
  }
  
  /**
   * Retrieves the thumbnailUrl associated with the instance. 
   * Returns an empty string if no thumbnailUrl has been set.
   */
  public String getThumbnailUrl() {
    try{
      if(this.has("thumbnailUrl")) {
        return this.getString("thumbnailUrl");
      }
    }catch(JSONException e) {
      e.printStackTrace();
    }
    return "";
  }
}

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
 * Class representing the OpenSocialActivity object which is used in the REST calls
 * for fetching and creating activities
 *
 * @author jle.edwards@gmail.com (Jesse Edwards)
 *
 */
public class OpenSocialActivity extends OpenSocialModel {
  
  public OpenSocialActivity(){}
  
  public OpenSocialActivity(String json) throws JSONException {
    super(json);
  }
  
  public void addTemplateParameter(String key, String value) {
    try{
      if(!this.has("templateParameters")) {
        this.put("templateParameters", new JSONArray());
      }
      JSONArray templateParams = this.getJSONArray("templateParameters");
      JSONObject kvp = new JSONObject();
      kvp.put("key", key);
      kvp.put("value", value);
      templateParams.put(kvp);
    } catch(JSONException e) {
      e.printStackTrace();
    }
  }
}

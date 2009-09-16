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
 * Base model class for each of the OpenSocialObjects to extend.
 * @author jle.edwards@gmail.com (Jesse Edwards)
 *
 */
public class OpenSocialModel extends JSONObject {
  public OpenSocialModel() {
    super();
  }
  
  public OpenSocialModel(String json) throws JSONException {
    super(json);
  }
  
  /**
   * Adds a value to the current object.
   * 
   * @param String key
   * @param Object value
   */
  public void setField(String key, Object value) {
    try {
      this.put(key, value);
    }catch(JSONException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Gets requested variable
   * 
   * @param String key
   * @return
   */
  public Object getField(String key) {
    try {
      return this.get(key);
    }catch(JSONException e) {
      return null;
    }
  }
}

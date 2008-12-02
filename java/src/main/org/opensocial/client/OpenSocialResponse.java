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

import org.json.JSONException;
import org.opensocial.data.OpenSocialAppData;
import org.opensocial.data.OpenSocialPerson;

public class OpenSocialResponse {

  private Map<String, String> items;
  
  public OpenSocialResponse() {
    this.items = new HashMap<String, String>();
  }
  
  public OpenSocialPerson getItemAsPerson(String id) throws OpenSocialRequestException, JSONException, InstantiationException, IllegalAccessException {
    String item = this.items.get(id);
    
    return OpenSocialJsonParser.parseAsPerson(item);
  }
  
  public List<OpenSocialPerson> getItemAsPersonCollection(String id) throws OpenSocialRequestException, JSONException, InstantiationException, IllegalAccessException {
    String item = this.items.get(id);
    
    return OpenSocialJsonParser.parseAsPersonCollection(item);
  }
  
  public OpenSocialAppData getItemAsAppData(String id) throws OpenSocialRequestException, JSONException, InstantiationException, IllegalAccessException {
    String item = this.items.get(id);

    return OpenSocialJsonParser.parseAsAppData(item);
  }
  
  public void addItem(String id, String item) {
    this.items.put(id, item);
  }
}

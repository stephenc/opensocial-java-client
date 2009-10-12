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

import static org.junit.Assert.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

/**
 * Unit test suite for OpenSocialPerson.
 *
 * @author apijason@google.com (Jason Cooper)
 */
public class OpenSocialPersonTest {

  @Test
  public void testGetId() {
    OpenSocialPerson p = new OpenSocialPerson();
    assertTrue(p.getField("id") == null);

    String id = "0000000000";
    p.setField("id", id);
    assertTrue(p.getField("id").equals(id));
  }

  @Test
  public void testGetDisplayName() {
    OpenSocialPerson p1 = new OpenSocialPerson();
    OpenSocialPerson p2 = new OpenSocialPerson();
    String givenName = "Sample";
    String familyName = "Testington";
    String displayName = givenName + " " + familyName;

    assertTrue(p1.getDisplayName().equals("Unknown Person"));

    p1.setField("displayName", displayName);
    assertTrue(p1.getDisplayName().equals(displayName));

    try{
      JSONObject name = new JSONObject();
      name.put("givenName", givenName);
      
      p2.setField("name", name);
      System.out.println(p2.getDisplayName());
      assertTrue(p2.getDisplayName().equals(givenName));
  
      name.put("familyName", familyName);
      p2.setField("name", name);
      assertTrue(p2.getDisplayName().equals(displayName));
    }catch(JSONException e) {
      e.printStackTrace();
    }
  }
}

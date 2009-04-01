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
    String id = "0000000000";

    assertTrue(p.getId().equals(""));

    OpenSocialField f = new OpenSocialField(false);
    p.setField("id", f);
    f.addValue(id);

    assertTrue(p.getId().equals(id));
  }

  @Test
  public void testGetDisplayName() {
    OpenSocialPerson p1 = new OpenSocialPerson();
    OpenSocialPerson p2 = new OpenSocialPerson();
    String givenName = "Sample";
    String familyName = "Testington";
    String displayName = givenName + " " + familyName;

    assertTrue(p1.getDisplayName().equals("Unknown Person"));

    OpenSocialField f1 = new OpenSocialField(false);
    f1.addValue(displayName);
    p1.setField("name", f1);

    assertTrue(p1.getDisplayName().equals(displayName));

    OpenSocialObject name = new OpenSocialObject();
    OpenSocialField nf1 = new OpenSocialField(false);
    OpenSocialField nf2 = new OpenSocialField(false);
    nf1.addValue(givenName);
    name.setField("givenName", nf1);

    OpenSocialField f2 = new OpenSocialField(true);
    p2.setField("name", f2);
    f2.addValue(name);

    assertTrue(p2.getDisplayName().equals(givenName));

    nf2.addValue(familyName);
    name.setField("familyName", nf2);

    assertTrue(p2.getDisplayName().equals(displayName));
  }
}

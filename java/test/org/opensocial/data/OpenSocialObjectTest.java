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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.opensocial.data.OpenSocialField;
import org.opensocial.data.OpenSocialObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Unit test suite for OpenSocialObject.
 *
 * @author apijason@google.com (Jason Cooper)
 */
public class OpenSocialObjectTest {

  @Test
  public void testCreate() {
    Map<String,OpenSocialField> m = new HashMap<String,OpenSocialField>();
    OpenSocialField f1 = new OpenSocialField(false);
    OpenSocialField f2 = new OpenSocialField(false);
    String k1 = "k1";
    String k2 = "k2";

    m.put(k1, f1);
    m.put(k2, f2);

    OpenSocialObject o = new OpenSocialObject(m);
    assertTrue(o.hasField(k1));
    assertTrue(o.hasField(k2));
  }

  @Test
  public void testHasField() {
    OpenSocialField f1 = new OpenSocialField(false);
    OpenSocialField f2 = new OpenSocialField(false);
    OpenSocialObject o = new OpenSocialObject();
    String k1 = "k1";
    String k2 = "k2";

    assertFalse(o.hasField(k1));
    assertFalse(o.hasField(k2));

    o.setField(k1, f1);
    assertTrue(o.hasField(k1));

    o.setField(k2, f2);
    assertTrue(o.hasField(k1));
    assertTrue(o.hasField(k2));
  }

  @Test
  public void testSetField() {
    OpenSocialField f1 = new OpenSocialField(false);
    OpenSocialField f2 = new OpenSocialField(false);
    OpenSocialObject o = new OpenSocialObject();
    String k1 = "k1";
    String k2 = "k2";

    f1.addValue("Foo");
    f1.addValue("Bar");

    assertEquals(null, o.getField(k1));
    assertEquals(null, o.getField(k2));

    o.setField(k1, f1);
    assertTrue(o.getField(k1).equals(f1));
    assertEquals(null, o.getField(k2));

    o.setField(k2, f2);
    assertTrue(o.getField(k1).equals(f1));
    assertTrue(o.getField(k2).equals(f2));
  }

  @Test
  public void testFieldNames() {
    OpenSocialField f1 = new OpenSocialField(false);
    OpenSocialField f2 = new OpenSocialField(false);
    OpenSocialObject o = new OpenSocialObject();
    String k1 = "k1";
    String k2 = "k2";

    assertEquals(0, o.fieldNames().length);

    o.setField(k1, f1);
    String[] a1 = o.fieldNames();
    assertEquals(1, a1.length);
    assertTrue(a1[0].equals(k1));

    o.setField(k2, f2);
    String[] a2 = o.fieldNames();
    assertEquals(2, a2.length);
    assertTrue(a2[0].equals(k1));
    assertTrue(a2[1].equals(k2));
  }
}

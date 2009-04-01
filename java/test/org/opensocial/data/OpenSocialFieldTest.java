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

import java.util.ArrayList;
import java.util.List;

/**
 * Unit test suite for OpenSocialField.
 *
 * @author apijason@google.com (Jason Cooper)
 */
public class OpenSocialFieldTest {

  @Test
  public void testCreate() {
    assertFalse(new OpenSocialField(false).isComplex());
    assertTrue(new OpenSocialField(true).isComplex());
  }

  @Test
  public void testAddValue() {
    String s1 = "Foo";
    String s2 = "Bar";

    OpenSocialField f = new OpenSocialField(false);

    assertFalse(f.isMultivalued());

    f.addValue(s1);
    assertFalse(f.isMultivalued());

    f.addValue(s2);
    assertTrue(f.isMultivalued());
  }

  @Test
  public void testGetStringValue() {
    String s1 = "Foo";
    String s2 = "Bar";

    OpenSocialField f1 = new OpenSocialField(false);
    OpenSocialField f2 = new OpenSocialField(true);

    assertEquals(null, f1.getStringValue());

    f1.addValue(s1);
    assertTrue(f1.getStringValue().equals(s1));

    f1.addValue(s2);
    assertTrue(f1.getStringValue().equals(s1));

    assertEquals(null, f2.getStringValue());

    f2.addValue(s1);
    assertTrue(f2.getStringValue().equals(s1));

    f2.addValue(s2);
    assertTrue(f2.getStringValue().equals(s1));
  }

  @Test
  public void testGetValue() throws OpenSocialException {
    OpenSocialObject o1 = new OpenSocialObject();
    OpenSocialObject o2 = new OpenSocialObject();

    OpenSocialField f1 = new OpenSocialField(true);

    assertEquals(null, f1.getValue());

    f1.addValue(o1);
    assertTrue(f1.getValue().equals(o1));

    f1.addValue(o2);
    assertTrue(f1.getValue().equals(o1));
  }

  @Test(expected = OpenSocialException.class)
  public void testGetComplexValue() throws OpenSocialException {
    OpenSocialField f1 = new OpenSocialField(false);
    OpenSocialObject o1 = new OpenSocialObject();

    f1.addValue(o1);
    f1.getValue();
  }

  @Test
  public void testGetStringValues() {
    List<String> c1 = new ArrayList<String>();
    String s1 = "Foo";
    String s2 = "Bar";

    OpenSocialField f1 = new OpenSocialField(false);

    assertTrue(f1.getStringValues().equals(c1));

    c1.add(s1);
    f1.addValue(s1);
    assertTrue(f1.getStringValues().equals(c1));

    c1.add(s2);
    f1.addValue(s2);
    assertTrue(f1.getStringValues().equals(c1));
  }

  @Test
  public void testGetValues() throws OpenSocialException {
    List<OpenSocialObject> c1 = new ArrayList<OpenSocialObject>(2);
    OpenSocialObject o1 = new OpenSocialObject();
    OpenSocialObject o2 = new OpenSocialObject();

    OpenSocialField f1 = new OpenSocialField(true);

    assertTrue(f1.getValues().equals(c1));

    c1.add(o1);
    f1.addValue(o1);
    assertTrue(f1.getValues().equals(c1));

    c1.add(o2);
    f1.addValue(o2);
    assertTrue(f1.getValues().equals(c1));
  }
}

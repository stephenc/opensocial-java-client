package org.opensocial.data;
import junit.framework.TestCase;

import java.util.Collection;
import java.util.Vector;

import org.opensocial.data.OpenSocialField;
import org.opensocial.data.OpenSocialObject;

public class OpenSocialFieldTest extends TestCase {
  public OpenSocialFieldTest(String name) {
    super(name);
  }

  public void testCreate() {
    assertEquals(false, new OpenSocialField(false).isComplex());
    assertEquals(true, new OpenSocialField(true).isComplex());
  }

  public void testAddValue() {
    String s1 = "Foo";
    String s2 = "Bar";

    OpenSocialField f = new OpenSocialField(false);

    assertEquals(false, f.isMultivalued());

    f.addValue(s1);
    assertEquals(false, f.isMultivalued());

    f.addValue(s2);
    assertEquals(true, f.isMultivalued());
  }

  public void testGetStringValue() {
    String s1 = "Foo";
    String s2 = "Bar";

    OpenSocialField f1 = new OpenSocialField(false);
    OpenSocialField f2 = new OpenSocialField(true);

    assertEquals(null, f1.getStringValue());

    f1.addValue(s1);
    assertEquals(true, f1.getStringValue().equals(s1));

    f1.addValue(s2);
    assertEquals(true, f1.getStringValue().equals(s1));

    assertEquals(null, f2.getStringValue());

    f2.addValue(s1);
    assertEquals(true, f2.getStringValue().equals(s1));

    f2.addValue(s2);
    assertEquals(true, f2.getStringValue().equals(s1));
  }

  public void testGetValue() {
    OpenSocialObject o1 = new OpenSocialObject();
    OpenSocialObject o2 = new OpenSocialObject();

    OpenSocialField f1 = new OpenSocialField(false);
    OpenSocialField f2 = new OpenSocialField(true);

    try {
      assertEquals(null, f2.getValue());

      f2.addValue(o1);
      assertEquals(o1, f2.getValue());

      f2.addValue(o2);
      assertEquals(o1, f2.getValue());
    } catch (OpenSocialException e) {
    }

    try {
      f1.addValue(o1);
      f1.getValue();
      fail("Exception: Cannot return complex value for this field");
    } catch (OpenSocialException e) {
    }
  }

  public void testGetStringValues() {
    Collection<String> c1 = new Vector<String>();
    String s1 = "Foo";
    String s2 = "Bar";

    OpenSocialField f1 = new OpenSocialField(false);

    assertEquals(c1, f1.getStringValues());

    c1.add(s1);
    f1.addValue(s1);
    assertEquals(c1, f1.getStringValues());

    c1.add(s2);
    f1.addValue(s2);
    assertEquals(c1, f1.getStringValues());
  }

  public void testGetValues() {
    Collection<OpenSocialObject> c1 = new Vector<OpenSocialObject>(2);
    OpenSocialObject o1 = new OpenSocialObject();
    OpenSocialObject o2 = new OpenSocialObject();

    OpenSocialField f1 = new OpenSocialField(true);

    try {
      assertEquals(c1, f1.getValues());

      c1.add(o1);
      f1.addValue(o1);
      assertEquals(c1, f1.getValues());

      c1.add(o2);
      f1.addValue(o2);
      assertEquals(c1, f1.getValues());
    } catch (OpenSocialException e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}

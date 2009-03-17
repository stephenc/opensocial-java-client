package org.opensocial.data;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

import org.opensocial.data.OpenSocialField;
import org.opensocial.data.OpenSocialObject;

public class OpenSocialObjectTest extends TestCase {
  public OpenSocialObjectTest(String name) {
    super(name);
  }

  public void testCreate() {
    Map<String,OpenSocialField> m = new HashMap<String,OpenSocialField>();
    OpenSocialField f1 = new OpenSocialField(false);
    OpenSocialField f2 = new OpenSocialField(false);
    String k1 = "k1";
    String k2 = "k2";

    m.put(k1, f1);
    m.put(k2, f2);

    OpenSocialObject o = new OpenSocialObject(m);
    assertEquals(true, o.hasField(k1));
    assertEquals(true, o.hasField(k2));
  }

  public void testHasField() {
    OpenSocialField f1 = new OpenSocialField(false);
    OpenSocialField f2 = new OpenSocialField(false);
    OpenSocialObject o = new OpenSocialObject();
    String k1 = "k1";
    String k2 = "k2";

    assertEquals(false, o.hasField(k1));
    assertEquals(false, o.hasField(k2));

    o.setField(k1, f1);
    assertEquals(true, o.hasField(k1));

    o.setField(k2, f2);
    assertEquals(true, o.hasField(k1));
    assertEquals(true, o.hasField(k2));
  }

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
    assertEquals(f1, o.getField(k1));
    assertEquals(null, o.getField(k2));

    o.setField(k2, f2);
    assertEquals(f1, o.getField(k1));
    assertEquals(f2, o.getField(k2));
  }

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
    assertEquals(k1, a1[0]);

    o.setField(k2, f2);
    String[] a2 = o.fieldNames();
    assertEquals(2, a2.length);
    assertEquals(k1, a2[0]);
    assertEquals(k2, a2[1]);
  }
}

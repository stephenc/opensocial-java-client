package org.opensocial.data;

import junit.framework.TestCase;

public class OpenSocialPersonTest extends TestCase {
  public void testGetId() {
    OpenSocialPerson p = new OpenSocialPerson();
    String id = "0000000000";

    assertEquals("", p.getId());

    OpenSocialField f = new OpenSocialField(false);
    p.setField("id", f);
    f.addValue(id);

    assertEquals(id, p.getId());
  }

  public void testGetDisplayName() {
    OpenSocialPerson p1 = new OpenSocialPerson();
    OpenSocialPerson p2 = new OpenSocialPerson();
    String givenName = "Sample";
    String familyName = "Testington";
    String displayName = givenName + " " + familyName;

    assertEquals("", p1.getDisplayName());

    OpenSocialField f1 = new OpenSocialField(false);
    f1.addValue(displayName);
    p1.setField("name", f1);

    assertEquals(displayName, p1.getDisplayName());

    OpenSocialObject name = new OpenSocialObject();
    OpenSocialField nf1 = new OpenSocialField(false);
    OpenSocialField nf2 = new OpenSocialField(false);
    nf1.addValue(givenName);
    name.setField("givenName", nf1);

    OpenSocialField f2 = new OpenSocialField(true);
    p2.setField("name", f2);
    f2.addValue(name);

    assertEquals(givenName, p2.getDisplayName());

    nf2.addValue(familyName);
    name.setField("familyName", nf2);

    assertEquals(displayName, p2.getDisplayName());
  }
}

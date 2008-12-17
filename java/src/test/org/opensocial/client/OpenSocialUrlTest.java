package org.opensocial.client;

import junit.framework.TestCase;

public class OpenSocialUrlTest extends TestCase {
  public OpenSocialUrlTest(String name) {
    super(name);
  }
  
  public void testToString() {
    String s1 = "http://www.opensocial.org";
    String s2 = "http://www.opensocial.org/test";
    String s3 = "http://www.opensocial.org/test?a=b";
    String s4 = "http://www.opensocial.org/test?a=b&x=y";
    
    OpenSocialUrl u = new OpenSocialUrl(s1);
    assertEquals(true, u.toString().equals(s1));
    
    u.addPathComponent("test");
    assertEquals(true, u.toString().equals(s2));
    
    u.addQueryStringParameter("a", "b");
    assertEquals(true, u.toString().equals(s3));
    
    u.addQueryStringParameter("x", "y");
    assertEquals(true, u.toString().equals(s4));
  }
}

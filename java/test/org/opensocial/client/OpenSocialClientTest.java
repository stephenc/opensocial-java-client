package org.opensocial.client;

import junit.framework.TestCase;

public class OpenSocialClientTest extends TestCase {
  public OpenSocialClientTest(String name) {
    super(name);
  }
  
  public void testGetProperty() {
    OpenSocialClient.Property n = OpenSocialClient.Property.DOMAIN;
    String v = "orkut.com";
    
    OpenSocialClient c = new OpenSocialClient();
    c.setProperty(n, v);
    
    assertEquals(true, c.getProperty(n).equals(v));
  }
  
  public void testNewFetchPersonRequest() {
    String userId = "user123";
    
    OpenSocialRequest r = OpenSocialClient.newFetchPersonRequest(userId);
    
    assertEquals(true, r.getRestPathComponent().equals("people"));
    assertEquals(true, r.getParameter("groupId").equals("@self"));
    assertEquals(true, r.getParameter("userId").equals(userId));
  }
}

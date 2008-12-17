package org.opensocial.client;

import junit.framework.TestCase;

public class OpenSocialClientTest extends TestCase {
  public OpenSocialClientTest(String name) {
    super(name);
  }
  
  public void testGetProperty() {
    String n = OpenSocialClient.Properties.DOMAIN;
    String v = "orkut.com";
    
    OpenSocialClient c = new OpenSocialClient();
    c.setProperty(n, v);
    
    assertEquals(true, c.getProperty(n).equals(v));
  }
  
  public void testNewFetchPeopleRequest() {
    String userId = "user123";
    String groupId = "group456";
    
    OpenSocialClient c = new OpenSocialClient();
    OpenSocialRequest r = c.newFetchPeopleRequest(userId, groupId);
    
    assertEquals(true, r.getRestPathComponent().equals("people"));
    assertEquals(true, r.getParameter("groupId").equals(groupId));
    assertEquals(true, r.getParameter("userId").equals(userId));
  }
}

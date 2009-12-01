package org.opensocial.services;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.opensocial.Request;
import org.opensocial.models.Person;

public class PeopleServiceTest {

  @Test
  public void retrieveWithZeroArguments() {
    Request request = PeopleService.retrieve();

    testCommonAttributes(request);
    testCommonRetrieveAttributes(request);
    assertTrue(request.getGuid().equals(Service.VIEWER));
    assertTrue(request.getSelector().equals(Service.SELF));
    
  }

  @Test
  public void retrieveWithOneArgument() {
    final String id = "03067092798963641994";
    Request request = PeopleService.retrieve(id);

    testCommonAttributes(request);
    testCommonRetrieveAttributes(request);
    assertTrue(request.getGuid().equals(id));
    assertTrue(request.getSelector().equals(Service.SELF));
  }

  @Test
  public void retrieveWithTwoArguments() {
    Request request = PeopleService.retrieve(Service.VIEWER, Service.FRIENDS);

    testCommonAttributes(request);
    testCommonRetrieveAttributes(request);
    assertTrue(request.getGuid().equals(Service.VIEWER));
    assertTrue(request.getSelector().equals(Service.FRIENDS));
  }

  private void testCommonAttributes(Request request) {
    assertTrue(request.getModelClass().equals(Person.class));
    assertTrue(request.getTemplate().equals("people/{guid}/{selector}/{pid}"));
  }

  private void testCommonRetrieveAttributes(Request request) {
    assertTrue(request.getRestMethod().equals("GET"));
    assertTrue(request.getRpcMethod().equals("people.get"));
  }
}

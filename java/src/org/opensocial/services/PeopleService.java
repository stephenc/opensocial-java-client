package org.opensocial.services;

import org.opensocial.Request;
import org.opensocial.models.Person;

public class PeopleService extends Service {

  private static final String restTemplate = "people/{guid}/{selector}/{pid}";

  public static Request retrieve() {
    return retrieve(VIEWER);
  }

  public static Request retrieve(String guid) {
    return retrieve(guid, SELF);
  }

  public static Request retrieve(String guid, String selector) {
    Request request = new Request(restTemplate, "people.get", "GET");
    request.setModelClass(Person.class);
    request.setSelector(selector);
    request.setGuid(guid);

    return request;
  }
}

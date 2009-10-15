package org.opensocial.services;

import org.opensocial.Request;
import org.opensocial.data.Person;

public class PeopleService extends Service {

  private static final String restTemplate = "people/{guid}/{selector}/{pid}";

  public static Request get() {
    return get(VIEWER);
  }

  public static Request get(String guid) {
    return get(guid, SELF);
  }

  public static Request get(String guid, String selector) {
    Request request = new Request(restTemplate, "people.get", "GET");
    request.setModelClass(Person.class);
    request.setSelector(selector);
    request.setGuid(guid);

    return request;
  }
}

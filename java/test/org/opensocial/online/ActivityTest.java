package org.opensocial.online;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.opensocial.Client;
import org.opensocial.Request;
import org.opensocial.Response;
import org.opensocial.auth.OAuth2LeggedScheme;
import org.opensocial.models.Activity;
import org.opensocial.providers.MySpaceProvider;
import org.opensocial.services.ActivityService;

import java.util.Date;
import java.util.List;

public class ActivityTest {

  private static final String MYSPACE_KEY = "http://www.myspace.com/495182150";
  private static final String MYSPACE_SECRET =
    "20ab52223e684594a8050a8bfd4b06693ba9c9183ee24e1987be87746b1b03f8";
  private static final String MYSPACE_ID = "495184236";

  @Test
  public void createMySpace() {
    Activity activity = new Activity();
    activity.setTitle("opensocial-java-client test activity at " +
        new Date().getTime());
    activity.setBody("opensocial-java-client test activity body");
    activity.setTitleId("test");

    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));
      Request request = ActivityService.create(activity);
      client.send(request);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void retrieveFromMySpace() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));
      Request request = ActivityService.retrieve();
      Response response = client.send(request);

      List<Activity> activities = response.getEntries();
      assertTrue(activities.size() > 0);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }
}

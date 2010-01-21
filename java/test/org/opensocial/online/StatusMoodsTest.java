/* Copyright (c) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensocial.online;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.opensocial.Client;
import org.opensocial.Request;
import org.opensocial.Response;
import org.opensocial.auth.OAuth2LeggedScheme;
import org.opensocial.models.myspace.StatusMood;
import org.opensocial.providers.MySpaceProvider;
import org.opensocial.services.myspace.StatusMoodsService;

import java.util.List;

public class StatusMoodsTest {

  private static final String MYSPACE_KEY = "http://www.myspace.com/495182150";
  private static final String MYSPACE_SECRET =
    "20ab52223e684594a8050a8bfd4b06693ba9c9183ee24e1987be87746b1b03f8";
  private static final String MYSPACE_ID = "495184236";

  @Test
  public void retrieve() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

      Request request = StatusMoodsService.retrieve(MYSPACE_ID);
      Response response = client.send(request);

      StatusMood statusMood = response.getEntry();
      assertTrue(statusMood.getMoodId() != null);
      assertTrue(statusMood.getStatus() != null);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void retrieveSupportedMood() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

      Request request = StatusMoodsService.retrieveMood(90);
      Response response = client.send(request);

      StatusMood mood = response.getEntry();
      assertTrue(mood.getMoodId() != null);
      assertTrue(mood.getMoodId().equals("90"));
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void retrieveSupportedMoods() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

      Request request = StatusMoodsService.retrieveSupportedMoods();
      Response response = client.send(request);

      List<StatusMood> moods = response.getEntries();
      assertTrue(moods != null);
      assertTrue(moods.size() > 0);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void retrieveStatusMoodHistoryForSelf() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

      Request request = StatusMoodsService.retrieveStatusMoodHistoryForSelf();
      Response response = client.send(request);

      List<StatusMood> statusMoods = response.getEntries();
      assertTrue(statusMoods != null);
      assertTrue(statusMoods.size() > 0);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void retrieveStatusMoodHistoryForFriends() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

      Request request =
        StatusMoodsService.retrieveStatusMoodHistoryForFriends();
      Response response = client.send(request);

      List<StatusMood> statusMoods = response.getEntries();
      assertTrue(statusMoods != null);
      assertTrue(statusMoods.size() > 0);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void retrieveStatusMoodHistoryForFriend() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

      Request request = StatusMoodsService.retrieveStatusMoodHistoryForFriend(
          "myspace.com.person.63129100");
      Response response = client.send(request);

      List<StatusMood> statusMoods = response.getEntries();
      assertTrue(statusMoods != null);
      assertTrue(statusMoods.size() > 0);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void update() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

      StatusMood statusMood = new StatusMood();
      statusMood.setStatus("Working on the Java SDK");
      statusMood.setMoodId(90);

      Request request = StatusMoodsService.update(MYSPACE_ID, statusMood);
      Response response = client.send(request);

      //assertTrue(response.getStatusLink() != null);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }
}

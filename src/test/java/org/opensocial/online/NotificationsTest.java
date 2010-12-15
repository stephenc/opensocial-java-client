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
import org.opensocial.models.MediaItem;
import org.opensocial.models.myspace.Notification;
import org.opensocial.providers.MySpaceProvider;
import org.opensocial.services.myspace.NotificationsService;

public class NotificationsTest {

  private static final String MYSPACE_KEY = "http://www.myspace.com/495182150";
  private static final String MYSPACE_SECRET =
    "20ab52223e684594a8050a8bfd4b06693ba9c9183ee24e1987be87746b1b03f8";
  private static final String MYSPACE_ID = "495184236";

  @Test
  public void create() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

      MediaItem mediaItem = new MediaItem();
      mediaItem.setUrl("http://api.myspace.com/v1/users/63129100");

      Notification notification = new Notification();
      notification.setContent("Hi ${recipient}, here's a notification from " +
          "${canvasUrl}");
      notification.addRecipient("495184236");
      notification.addMediaItem(mediaItem);

      Request request = NotificationsService.createNotification(notification);
      Response response = client.send(request);

      assertTrue(response.getStatusLink() != null);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }
}

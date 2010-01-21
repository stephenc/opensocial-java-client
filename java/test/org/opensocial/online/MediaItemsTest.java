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
import org.opensocial.providers.MySpaceProvider;
import org.opensocial.services.MediaItemsService;

import java.io.File;
import java.util.List;

public class MediaItemsTest {

  private static final String MYSPACE_KEY = "http://www.myspace.com/495182150";
  private static final String MYSPACE_SECRET =
    "20ab52223e684594a8050a8bfd4b06693ba9c9183ee24e1987be87746b1b03f8";
  private static final String MYSPACE_ID = "495184236";

  @Test
  public void retrieveMediaItems() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));
      Request request = MediaItemsService.retrieve("myspace.com.album.81886");
      Response response = client.send(request);

      List<MediaItem> mediaItems = response.getEntries();
      assertTrue(mediaItems != null);
      assertTrue(mediaItems.size() > 0);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void retrieveMediaItem() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));
      Request request = MediaItemsService.retrieve(
          "myspace.com.mediaItem.image.646364", "myspace.com.album.81886");
      Response response = client.send(request);

      MediaItem mediaItem = response.getEntry();
      assertTrue(mediaItem != null);
      assertTrue(mediaItem.getId().equals(
          "myspace.com.mediaItem.image.646364"));
      assertTrue(mediaItem.getUrl() != null);
      assertTrue(mediaItem.getType() != null);
      assertTrue(mediaItem.getTitle() != null);
      assertTrue(mediaItem.getThumbnailUrl() != null);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void createMediaItem() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

      MediaItem mediaItem = new MediaItem();
      mediaItem.setTitle("value");
      mediaItem.setDescription("my description goes here");

      Request request = MediaItemsService.create(mediaItem,
          "myspace.com.album.81886");
      Response response = client.send(request);

      assertTrue(response.getStatusLink() != null);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void updateMediaItem() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

      MediaItem mediaItem = new MediaItem();
      mediaItem.setTitle("This is my updated caption");
      mediaItem.setDescription("my description goes here");

      Request request = MediaItemsService.update(
          "myspace.com.mediaItem.image.646364", "myspace.com.album.81886",
          mediaItem);
      Response response = client.send(request);

      assertTrue(response.getStatusLink() != null);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  /*@Test
  public void uploadMediaItem() {
    File f = new File("");

    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

      MediaItem mediaItem = new MediaItem();
      mediaItem.setContentType("image/jpg");
      mediaItem.setType("image");

      Request request = MediaItemService.upload(mediaItem, f,
          "myspace.com.album.706610");
      Response response = client.send(request);

      //assertTrue(response.getStatusLink() != null);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }*/
}

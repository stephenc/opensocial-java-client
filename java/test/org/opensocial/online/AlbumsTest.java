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
import org.opensocial.RequestException;
import org.opensocial.Response;
import org.opensocial.auth.OAuth2LeggedScheme;
import org.opensocial.models.Album;
import org.opensocial.providers.MySpaceProvider;
import org.opensocial.services.AlbumsService;

import java.io.IOException;
import java.util.List;

public class AlbumsTest {

  private static final String MYSPACE_KEY = "http://www.myspace.com/495182150";
  private static final String MYSPACE_SECRET =
    "20ab52223e684594a8050a8bfd4b06693ba9c9183ee24e1987be87746b1b03f8";
  private static final String MYSPACE_ID = "495184236";

  @Test
  public void retrieveAlbums() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));
      Request request = AlbumsService.getAlbums();
      Response response = client.send(request);

      List<Album> albums = response.getEntries();
      assertTrue(albums != null);
      assertTrue(albums.size() > 0);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void retrieveAlbum() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));
      Request request = AlbumsService.getAlbum("myspace.com.album.81886");
      Response response = client.send(request);

      Album album = response.getEntry();
      assertTrue(album != null);
      assertTrue(album.getId().equals("myspace.com.album.81886"));
      assertTrue(album.getThumbnailUrl() != null);
      assertTrue(album.getCaption() != null);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void createAlbum() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

      Album album = new Album();
      album.setCaption("value");
      album.setDescription("my description goes here");

      Request request = AlbumsService.createAlbum(album);
      Response response = client.send(request);

      assertTrue(response.getStatusLink() != null);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void updateAlbum() {
    try {
      Client client = new Client(new MySpaceProvider(),
          new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

      Album album = new Album();
      album.setId("myspace.com.album.81886");
      album.setCaption("This is my updated caption");
      album.setDescription("my description goes here");

      Request request = AlbumsService.updateAlbum(album);
      Response response = client.send(request);

      assertTrue(response.getStatusLink() != null);
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test(expected=RequestException.class)
  public void updateAlbumWithoutId() throws RequestException, IOException {
    Client client = new Client(new MySpaceProvider(),
        new OAuth2LeggedScheme(MYSPACE_KEY, MYSPACE_SECRET, MYSPACE_ID));

    Album album = new Album();
    album.setCaption("This is my updated caption");
    album.setDescription("my description goes here");

    Request request = AlbumsService.updateAlbum(album);
    Response response = client.send(request);

    assertTrue(response.getStatusLink() != null);
  }
}

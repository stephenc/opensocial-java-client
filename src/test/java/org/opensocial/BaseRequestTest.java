/* Copyright (c) 2010 Google Inc.
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

package org.opensocial;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.opensocial.auth.OAuth2LeggedScheme;
import org.opensocial.auth.OAuth3LeggedScheme;
import org.opensocial.auth.OAuth3LeggedScheme.Token;
import org.opensocial.models.Activity;
import org.opensocial.models.Album;
import org.opensocial.models.AppData;
import org.opensocial.models.Group;
import org.opensocial.models.MediaItem;
import org.opensocial.models.Person;
import org.opensocial.providers.OrkutProvider;
import org.opensocial.services.ActivitiesService;
import org.opensocial.services.AlbumsService;
import org.opensocial.services.AppDataService;
import org.opensocial.services.GroupsService;
import org.opensocial.services.MediaItemsService;
import org.opensocial.services.PeopleService;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

/**
 * Add common request test cases here. These test cases will be run on multiple
 * Provider, AuthScheme, Protocol (RPC/REST) combination.
 *
 * @author Guibin Kong
 *
 */
public class BaseRequestTest extends AbstractRequestTest {
  @BeforeClass
  public static void init() {
    client = new Client(new OrkutProvider(false), new OAuth2LeggedScheme(
        "consumerKey", "consumerSecret"));
  }

  @Before
  public void setUp() throws Exception {
    if (client.getAuthScheme() instanceof OAuth3LeggedScheme) {
      OAuth3LeggedScheme scheme = (OAuth3LeggedScheme) client.getAuthScheme();
      // for 3-legged OAuth, ignore the process to get access token
      Token token = new Token("key", "secret");
      scheme.setAccessToken(token);
    }

    clear();
  }

  @Test
  public void testPeopleGetViewerProfile() throws RequestException,
      IOException {
    request = PeopleService.getViewer();
    assertRequestValid(Person.class, "people.get", "GET");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@self", Request.SELECTOR);

    super.toHttpMessage();
    assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"people.get\"", "\"userId\":\"@me\"",
      "\"groupId\":\"@self\"");
    } else {
      assertRestUrlValid("GET", "/people/@me/@self");
    }
  }

  @Test
  public void testPeopleGetUserProfile() throws RequestException, IOException {
    String guid = "test_user_id";
    request = PeopleService.getUser(guid);
    assertRequestValid(Person.class, "people.get", "GET");
    assertRequestComponent(guid, Request.GUID);
    assertRequestComponent("@self", Request.SELECTOR);

    super.toHttpMessage();
    assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"people.get\"", "\"userId\":\"" + guid
          + "\"", "\"groupId\":\"@self\"");
    } else {
      assertRestUrlValid("GET", "/people/" + guid + "/@self");
    }
  }

  @Test
  public void testPeopleGetViewerFriends() throws RequestException,
      IOException {
    request = PeopleService.getFriends();
    assertRequestValid(Person.class, "people.get", "GET");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@friends", Request.SELECTOR);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"people.get\"", "\"userId\":\"@me\"",
      "\"groupId\":\"@friends\"");
    } else {
      assertRestUrlValid("GET", "/people/@me/@friends");
    }
  }

  @Test
  public void testPeopleGetUserFriends() throws RequestException, IOException {
    String guid = "test_user_id";
    request = PeopleService.getFriends(guid);
    assertRequestValid(Person.class, "people.get", "GET");
    Assert.assertEquals(guid, request.getComponent(Request.GUID));
    Assert.assertEquals("@friends", request.getComponent(Request.SELECTOR));

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"people.get\"", "\"userId\":\"" + guid
          + "\"", "\"groupId\":\"@friends\"");
    } else {
      assertRestUrlValid("GET", "/people/" + guid + "/@friends");
    }
  }

  @Test
  public void testGroupGetViewerGroups() throws RequestException, IOException {
    request = GroupsService.getGroups();
    assertRequestValid(Group.class, "groups.get", "GET");
    assertRequestComponent("@me", Request.GUID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"groups.get\"", "\"userId\":\"@me\"");
    } else {
      assertRestUrlValid("GET", "/groups/@me");
    }
  }

  @Test
  public void testGroupGetUserGroups() throws RequestException, IOException {
    String guid = "test_user_id";
    request = GroupsService.getGroups(guid);
    assertRequestValid(Group.class, "groups.get", "GET");
    assertRequestComponent(guid, Request.GUID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"groups.get\"", "\"userId\":\"" + guid
          + "\"");
    } else {
      assertRestUrlValid("GET", "/groups/" + guid);
    }
  }

  @Test
  public void testActivitiesGetViewerAppActivities() throws RequestException,
      IOException {
    request = ActivitiesService.getActivities();
    assertRequestValid(Activity.class, "activities.get", "GET");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@self", Request.SELECTOR);
    assertRequestComponent("@app", Request.APP_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"activities.get\"", "\"userId\":\"@me\"",
          "\"groupId\":\"@self\"", "\"appId\":\"@app\"");
    } else {
      assertRestUrlValid("GET", "/activities/@me/@self/@app");
    }
  }

  @Test
  public void testActivitiesGetFriendsAppActivities() throws RequestException,
      IOException {
    request = ActivitiesService.getFriendActivities("@me");
    assertRequestValid(Activity.class, "activities.get", "GET");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@friends", Request.SELECTOR);
    assertRequestComponent("@app", Request.APP_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"activities.get\"", "\"userId\":\"@me\"",
          "\"groupId\":\"@friends\"", "\"appId\":\"@app\"");
    } else {
      assertRestUrlValid("GET", "/activities/@me/@friends/@app");
    }
  }

  @Test
  public void testActivitiesGetUserAppActivities() throws RequestException,
      IOException {
    String guid = "test_user_id";
    request = ActivitiesService.getActivities(guid);
    assertRequestValid(Activity.class, "activities.get", "GET");
    assertRequestComponent(guid, Request.GUID);
    assertRequestComponent("@self", Request.SELECTOR);
    assertRequestComponent("@app", Request.APP_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"activities.get\"", "\"userId\":\"" + guid
          + "\"", "\"groupId\":\"@self\"", "\"appId\":\"@app\"");
    } else {
      assertRestUrlValid("GET", "/activities/" + guid + "/@self/@app");
    }
  }

  @Test
  public void testActivitiesGetUserFriendsActivities() throws RequestException,
      IOException {
    String guid = "test_user_id";
    request = ActivitiesService.getFriendActivities(guid);
    assertRequestValid(Activity.class, "activities.get", "GET");
    assertRequestComponent(guid, Request.GUID);
    assertRequestComponent("@friends", Request.SELECTOR);
    assertRequestComponent("@app", Request.APP_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"activities.get\"", "\"userId\":\"" + guid
          + "\"", "\"groupId\":\"@friends\"", "\"appId\":\"@app\"");
    } else {
      assertRestUrlValid("GET", "/activities/" + guid + "/@friends/@app");
    }
  }

  @Test
  public void testActivitiesCreate() throws RequestException, IOException {
    Activity activity = new Activity();
    activity.setBody("activity body");
    activity.setTitleId("titleId");
    request = ActivitiesService.createActivity(activity);
    assertRequestValid(null, "activities.create", "POST");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@self", Request.SELECTOR);
    assertRequestComponent("@app", Request.APP_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"activities.create\"",
          "\"userId\":\"@me\"", "\"groupId\":\"@self\"", "\"appId\":\"@app\"");
    } else {
      assertRestUrlValid("POST", "/activities/@me/@self/@app");
    }
  }

  @Test
  public void testAppDataGetViewerAppData() throws RequestException,
      IOException {
    request = AppDataService.getAppData();
    assertRequestValid(AppData.class, "appdata.get", "GET");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@self", Request.SELECTOR);
    assertRequestComponent("@app", Request.APP_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"appdata.get\"", "\"userId\":\"@me\"",
          "\"groupId\":\"@self\"", "\"appId\":\"@app\"");
    } else {
      assertRestUrlValid("GET", "/appdata/@me/@self/@app");
    }
  }

  @Test
  public void testAppDataGetUserAppData() throws RequestException,
      IOException {
    String guid = "test_user_id";
    request = AppDataService.getAppData(guid);
    assertRequestValid(AppData.class, "appdata.get", "GET");
    assertRequestComponent(guid, Request.GUID);
    assertRequestComponent("@self", Request.SELECTOR);
    assertRequestComponent("@app", Request.APP_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"appdata.get\"", "\"userId\":\"" + guid
          + "\"", "\"groupId\":\"@self\"", "\"appId\":\"@app\"");
    } else {
      assertRestUrlValid("GET", "/appdata/" + guid + "/@self/@app");
    }
  }

  @Test
  public void testAppDataGetUserFriendsAppData() throws RequestException,
      IOException {
    String guid = "@me";
    request = AppDataService.getFriendAppData(guid);
    assertRequestValid(AppData.class, "appdata.get", "GET");
    assertRequestComponent(guid, Request.GUID);
    assertRequestComponent("@friends", Request.SELECTOR);
    assertRequestComponent("@app", Request.APP_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"appdata.get\"", "\"userId\":\"@me\"",
          "\"groupId\":\"@friends\"", "\"appId\":\"@app\"");
    } else {
      assertRestUrlValid("GET", "/appdata/@me/@friends/@app");
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testAppDataUpdate() throws RequestException, IOException {
    String key = "app_data_key";
    String value = "my_app_data";
    request = AppDataService.updateAppData(key, value);
    assertRequestValid(null, "appdata.update", "PUT");
    assertRequestComponent("@viewer", Request.GUID);
    assertRequestComponent("@self", Request.SELECTOR);
    assertRequestComponent("@app", Request.APP_ID);
    Assert.assertEquals(value, request.getRestPayloadParameters().get(key));
    Map<String, String> data = (Map<String, String>) request
        .getRpcPayloadParameters().get("data");
    Assert.assertEquals(value, data.get(key));
    List<String> fields = (List<String>) request.getRpcPayloadParameters().get(
        "fields");
    Assert.assertEquals(key, fields.get(0));

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"appdata.update\"",
          "\"userId\":\"@viewer\"", "\"groupId\":\"@self\"",
          "\"appId\":\"@app\"", "\"data\":{\"app_data_key\":\"my_app_data\"}",
          "\"fields\":[\"app_data_key\"]");
    } else {
      assertRestUrlValid("PUT", "/appdata/@viewer/@self/@app",
          "fields=app_data_key");
    }
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testAppDataUpdateBatch() throws RequestException, IOException {
    Map<String, String> data = new HashMap<String, String>();
    data.put("key1", "value1");
    data.put("key2", "value2");
    request = AppDataService.updateAppData(data);
    assertRequestValid(null, "appdata.update", "PUT");
    assertRequestComponent("@viewer", Request.GUID);
    assertRequestComponent("@self", Request.SELECTOR);
    assertRequestComponent("@app", Request.APP_ID);
    data = (Map<String, String>) request.getRpcPayloadParameters().get("data");
    Assert.assertEquals("value1", data.get("key1"));
    Assert.assertEquals("value2", data.get("key2"));
    List<String> fields = (List<String>) request.getRpcPayloadParameters().get(
        "fields");
    Assert.assertEquals(2, fields.size());
    Assert.assertTrue(fields.contains("key1") && fields.contains("key2"));

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"appdata.update\"",
          "\"userId\":\"@viewer\"", "\"groupId\":\"@self\"",
          "\"appId\":\"@app\"");
      assertMsgBodyHasEither(
          "\"data\":{\"key1\":\"value1\",\"key2\":\"value2\"}",
          "\"data\":{\"key2\":\"value2\",\"key1\":\"value1\"}");
      assertMsgBodyHasEither("\"fields\":[\"key1\",\"key2\"]",
          "\"fields\":[\"key2\",\"key1\"]");
    } else {
      // TODO:add message list
      assertRestUrlValid("PUT", "/appdata/@viewer/@self/@app");
      String url = message.url.toExternalForm();
      String url1 = "fields=" + URLEncoder.encode("key1,key2", "UTF-8");
      String url2 = "fields=" + URLEncoder.encode("key2,key1", "UTF-8");
      Assert.assertTrue(url.contains(url1) || url.contains(url2));
    }
  }

  @Ignore("ModuleClass should be AppData.class")
  @SuppressWarnings("unchecked")
  @Test
  public void testAppDataDelete() throws RequestException, IOException {
    String key = "app_data_key";
    request = AppDataService.deleteAppData(key);
    assertRequestValid(AppData.class, "appdata.delete", "DELETE");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@self", Request.SELECTOR);
    assertRequestComponent("@app", Request.APP_ID);
    List<String> fields = (List<String>) request.getRpcPayloadParameters().get(
        "fields");
    Assert.assertEquals(1, fields.size());
    Assert.assertTrue(fields.contains("app_data_key"));

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"appdata.delete\"", "\"userId\":\"@me\"",
          "\"groupId\":\"@self\"", "\"appId\":\"@app\"",
          "\"fields\":[\"app_data_key\"]");
    } else {
      assertRestUrlValid("DELETE", "/appdata/@me/@self/@app",
          "fields=app_data_key");
    }
  }

  @Ignore("ModuleClass should be AppData.class")
  @SuppressWarnings("unchecked")
  @Test
  public void testAppDataDeleteList() throws RequestException, IOException {
    List<String> keys = new ArrayList<String>();
    keys.add("key1");
    keys.add("key2");
    request = AppDataService.deleteAppData(keys);
    assertRequestValid(AppData.class, "appdata.delete", "DELETE");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@self", Request.SELECTOR);
    assertRequestComponent("@app", Request.APP_ID);
    List<String> fields = (List<String>) request.getRpcPayloadParameters().get(
        "fields");
    Assert.assertEquals(2, fields.size());
    Assert.assertTrue(fields.contains("key1") && fields.contains("key2"));

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"appdata.delete\"", "\"userId\":\"@me\"",
          "\"groupId\":\"@self\"", "\"appId\":\"@app\"");
      assertMsgBodyHasEither("\"fields\":[\"key1\",\"key2\"]",
          "\"fields\":[\"key2\",\"key1\"]");
    } else {
      assertRestUrlValid("DELETE", "/appdata/@me/@self/@app");
      String url = message.url.toExternalForm();
      String url1 = "fields=" + URLEncoder.encode("key1,key2", "UTF-8");
      String url2 = "fields=" + URLEncoder.encode("key2,key1", "UTF-8");
      Assert.assertTrue(url.contains(url1) || url.contains(url2));
    }
  }

  @Ignore("ModuleClass should be AppData.class")
  @SuppressWarnings("unchecked")
  @Test
  public void testAppDataDeleteArray() throws RequestException, IOException {
    String[] keys = new String[2];
    keys[0] = "key1";
    keys[1] = "key2";
    request = AppDataService.deleteAppData(keys);
    assertRequestValid(AppData.class, "appdata.delete", "DELETE");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@self", Request.SELECTOR);
    assertRequestComponent("@app", Request.APP_ID);
    List<String> fields = (List<String>) request.getRpcPayloadParameters().get(
        "fields");
    Assert.assertEquals(2, fields.size());
    Assert.assertTrue(fields.contains("key1") && fields.contains("key2"));

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"appdata.delete\"", "\"userId\":\"@me\"",
          "\"groupId\":\"@self\"", "\"appId\":\"@app\"");
      assertMsgBodyHasEither("\"fields\":[\"key1\",\"key2\"]",
          "\"fields\":[\"key2\",\"key1\"]");
    } else {
      // TODO:add message list
      assertRestUrlValid("DELETE", "/appdata/@me/@self/@app");
      String url = message.url.toExternalForm();
      String url1 = "fields=" + URLEncoder.encode("key1,key2", "UTF-8");
      String url2 = "fields=" + URLEncoder.encode("key2,key1", "UTF-8");
      Assert.assertTrue(url.contains(url1) || url.contains(url2));
    }
  }

  @Test
  public void testAlbumsList() throws RequestException, IOException {
    request = AlbumsService.getAlbums();
    assertRequestValid(Album.class, "albums.get", "GET");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@self", Request.GROUP_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"albums.get\"", "\"userId\":\"@me\"",
          "\"groupId\":\"@self\"");
    } else {
      assertRestUrlValid("GET", "/albums/@me/@self");
    }
  }


  @Ignore("albumId isn't copied to HttpMessage Body")
  @Test
  public void testAlbumGet() throws RequestException, IOException {
    String albumId = "albumId";
    request = AlbumsService.getAlbum(albumId);
    assertRequestValid(Album.class, "albums.get", "GET");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@self", Request.GROUP_ID);
    assertRequestComponent(albumId, Request.ALBUM_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"albums.get\"", "\"userId\":\"@me\"",
          "\"groupId\":\"@self\"", "\"albumId\":\"" + albumId + "\"");
    } else {
      assertRestUrlValid("GET", "/albums/@me/@self/" + albumId);
    }
  }

  @Ignore("albumId isn't copied to Request")
  @Test
  public void testAlbumCreate() throws RequestException, IOException {
    Album album = new Album();
    String albumId = "myNewAlbum001";
    String caption = "Happy new Year!";
    String description = "Photos of the new year's party.";
    String thumbnailUrl = "http://thumbnailUrl";
    album.setId(albumId);
    album.setCaption(caption);
    album.setDescription(description);
    album.setThumbnailUrl(thumbnailUrl);
    request = AlbumsService.createAlbum(album);
    assertRequestValid(null, "albums.create", "POST");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@self", Request.GROUP_ID);

    Assert.assertEquals(albumId, request.getRestPayloadParameters().get("id"));
    Assert.assertEquals(caption, request.getRestPayloadParameters().get(
        "caption"));
    Assert.assertEquals(thumbnailUrl, request.getRestPayloadParameters().get(
        "thumbnailUrl"));
    Assert.assertEquals(description, request.getRestPayloadParameters().get(
        "description"));

    Map<String, Object> parameters = request.getRpcPayloadParameters();
    Assert.assertEquals(albumId, parameters.get("id"));
    Assert.assertEquals(caption, parameters.get("caption"));
    Assert.assertEquals(thumbnailUrl, parameters.get("thumbnailUrl"));
    Assert.assertEquals(description, parameters.get("description"));

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"albums.create\"", "\"userId\":\"@me\"",
          "\"groupId\":\"@self\"", "\"albumId\":\"" + albumId + "\"",
          "\"caption\":\"" + caption + "\"", "\"description\":\"" +
          description + "\"", "\"thumbnailUrl\":\"" + thumbnailUrl + "\"");
    } else {
      assertRestUrlValid("PUT", "/albums/@me/@self/" + albumId + "?");
    }
  }

  @Ignore("albumId isn't copied to Request")
  @Test
  public void testAlbumUpdate() throws RequestException, IOException {
    Album album = new Album();
    String albumId = "myNewAlbum001";
    String caption = "Happy new Year!";
    String description = "Photos of the new year's party.";
    String thumbnailUrl = "http://thumbnailUrl";
    album.setId(albumId);
    album.setCaption(caption);
    album.setDescription(description);
    album.setThumbnailUrl(thumbnailUrl);
    request = AlbumsService.updateAlbum(album);
    assertRequestValid(null, "albums.update", "PUT");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@self", Request.GROUP_ID);

    Assert.assertEquals(albumId, request.getRestPayloadParameters().get("id"));
    Assert.assertEquals(caption, request.getRestPayloadParameters().get(
        "caption"));
    Assert.assertEquals(thumbnailUrl, request.getRestPayloadParameters().get(
        "thumbnailUrl"));
    Assert.assertEquals(description, request.getRestPayloadParameters().get(
        "description"));

    Map<String, Object> parameters = request.getRpcPayloadParameters();
    Assert.assertEquals(albumId, parameters.get("id"));
    Assert.assertEquals(caption, parameters.get("caption"));
    Assert.assertEquals(thumbnailUrl, parameters.get("thumbnailUrl"));
    Assert.assertEquals(description, parameters.get("description"));

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"albums.update\"", "\"userId\":\"@me\"",
          "\"groupId\":\"@self\"", "\"albumId\":\"" + albumId + "\"",
          "\"caption\":\"" + caption + "\"", "\"description\":\"" +
          description + "\"", "\"thumbnailUrl\":\"" + thumbnailUrl + "\"");
    } else {
      assertRestUrlValid("PUT", "/albums/@me/@self/" + albumId);
    }
  }

  @Ignore("albumId isn't copied to HttpMessage Body")
  @Test
  public void testAlbumDelete() throws RequestException, IOException {
    String albumId = "myNewAlbum001";
    request = AlbumsService.deleteAlbum(albumId);
    assertRequestValid(null, "albums.delete", "DELETE");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent("@self", Request.GROUP_ID);
    assertRequestComponent(albumId, Request.ALBUM_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"albums.delete\"", "\"userId\":\"@me\"",
          "\"groupId\":\"@self\"", "\"albumId\":\"" + albumId + "\"");
    } else {
      assertRestUrlValid("DELETE", "/albums/@me/@self/" + albumId);
    }
  }

  @Ignore("albumId isn't copied to HttpMessage Body")
  @Test
  public void testMediaItemsList() throws RequestException, IOException {
    String albumId = "myNewAlbum001";
    request = MediaItemsService.getMediaItems(albumId);
    assertRequestValid(MediaItem.class, "mediaItems.get", "GET");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent(albumId, Request.ALBUM_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"mediaItems.get\"", "\"userId\":\"@me\"",
          "\"albumId\":\"" + albumId + "\"");
    } else {
      assertRestUrlValid("GET", "/mediaitems/@me/@self/" + albumId);
    }
  }

  @Ignore("albumId isn't copied to HttpMessage Body")
  @Test
  public void testMediaItemsGet() throws RequestException, IOException {
    String albumId = "myNewAlbum001";
    String itemId = "first";
    request = MediaItemsService.getMediaItem(itemId, albumId);
    assertRequestValid(MediaItem.class, "mediaItems.get", "GET");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent(albumId, Request.ALBUM_ID);
    assertRequestComponent(itemId, Request.ITEM_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"mediaItems.get\"", "\"userId\":\"@me\"",
          "\"albumId\":\"" + albumId + "\"", "\"itemId\":\"" + itemId + "\"");
    } else {
      assertRestUrlValid("GET", "/mediaitems/@me/@self/" + albumId + "/" +
          itemId);
    }
  }

  @Ignore("albumId isn't copied to Request")
  @Test
  public void testMediaItemsCreate() throws RequestException, IOException {
    String albumId = "myNewAlbum001";
    String itemId = "first";
    String caption = "item caption";
    String description = "item description";
    String mimeType = "image/jpeg";
    String thumbnailUrl = "http://thumbnail_url";
    String type = "image";
    String url = "http://url";
    MediaItem item = new MediaItem();
    item.setId(itemId);
    item.setAlbumId(albumId);
    item.setCaption(caption);
    item.setDescription(description);
    item.setMimeType(mimeType);
    item.setThumbnailUrl(thumbnailUrl);
    item.setType(type);
    item.setUrl(url);

    request = MediaItemsService.createMediaItem(item);
    assertRequestValid(null, "mediaItems.create", "POST");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent(albumId, Request.ALBUM_ID);

    Assert.assertEquals(itemId, request.getRestPayloadParameters().get(
        MediaItem.ID));
    Assert.assertEquals(albumId, request.getRestPayloadParameters().get(
        MediaItem.ALBUM_ID));
    Assert.assertEquals(caption, request.getRestPayloadParameters().get(
        MediaItem.CAPTION));
    Assert.assertEquals(thumbnailUrl, request.getRestPayloadParameters().get(
        MediaItem.THUMBNAIL_URL));
    Assert.assertEquals(description, request.getRestPayloadParameters().get(
        MediaItem.DESCRIPTION));
    Assert.assertEquals(mimeType, request.getRestPayloadParameters().get(
        MediaItem.MIME_TYPE));
    Assert.assertEquals(type, request.getRestPayloadParameters().get(
        MediaItem.TYPE));
    Assert.assertEquals(url, request.getRestPayloadParameters().get(
        MediaItem.URL));

    Map<String, Object> parameters = request.getRpcPayloadParameters();
    Assert.assertEquals(itemId, parameters.get(MediaItem.ID));
    Assert.assertEquals(albumId, parameters.get(MediaItem.ALBUM_ID));
    Assert.assertEquals(caption, parameters.get(MediaItem.CAPTION));
    Assert.assertEquals(thumbnailUrl, parameters.get(MediaItem.THUMBNAIL_URL));
    Assert.assertEquals(description, parameters.get(MediaItem.DESCRIPTION));
    Assert.assertEquals(mimeType, parameters.get(MediaItem.MIME_TYPE));
    Assert.assertEquals(type, parameters.get(MediaItem.TYPE));
    Assert.assertEquals(url, parameters.get(MediaItem.URL));

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"mediaItems.create\"",
          "\"userId\":\"@me\"", "\"albumId\":\"" + albumId + "\"",
          "\"itemId\":\"" + itemId + "\"", "\"caption\":\"" + caption + "\"",
          "\"description\":\"" + description + "\"", "\"mime_type\":\"" +
          mimeType + "\"", "\"thumbnailUrl\":\"" + thumbnailUrl + "\"",
          "\"type\":\"" + type + "\"", "\"url\":\"" + url + "\"");
    } else {
      assertRestUrlValid("POST", "/mediaitems/@me/@self/" + albumId + "/" +
          itemId);
    }
  }

  @Ignore("albumId isn't copied to HttpMessage Body")
  @Test
  public void testMediaItemsDelete() throws RequestException, IOException {
    String albumId = "myNewAlbum001";
    String itemId = "first";
    request = MediaItemsService.deleteMediaItem(itemId, albumId);
    assertRequestValid(null, "mediaItems.delete", "DELETE");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent(albumId, Request.ALBUM_ID);
    assertRequestComponent(itemId, Request.ITEM_ID);

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"mediaItems.delete\"",
          "\"userId\":\"@me\"", "\"albumId\":\"" + albumId + "\"",
          "\"itemId\":\"" + itemId + "\"");
    } else {
      assertRestUrlValid("DELETE", "/mediaitems/@me/@self/" + albumId + "/"
          + itemId);
    }
  }

  @Ignore("albumId isn't copied to Request")
  @Test
  public void testMediaItemsUpdate() throws RequestException, IOException {
    String albumId = "myNewAlbum001";
    String itemId = "first";
    String caption = "item caption";
    String description = "item description";
    String mimeType = "image/jpeg";
    String thumbnailUrl = "http://thumbnail_url";
    String type = "image";
    String url = "http://url";
    MediaItem item = new MediaItem();
    item.setId(itemId);
    item.setAlbumId(albumId);
    item.setCaption(caption);
    item.setDescription(description);
    item.setMimeType(mimeType);
    item.setThumbnailUrl(thumbnailUrl);
    item.setType(type);
    item.setUrl(url);

    request = MediaItemsService.updateMediaItem(item);
    assertRequestValid(null, "mediaItems.update", "PUT");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent(albumId, Request.ALBUM_ID);

    Assert.assertEquals(itemId, request.getRestPayloadParameters().get(
        MediaItem.ID));
    Assert.assertEquals(albumId, request.getRestPayloadParameters().get(
        MediaItem.ALBUM_ID));
    Assert.assertEquals(caption, request.getRestPayloadParameters().get(
        MediaItem.CAPTION));
    Assert.assertEquals(thumbnailUrl, request.getRestPayloadParameters().get(
        MediaItem.THUMBNAIL_URL));
    Assert.assertEquals(description, request.getRestPayloadParameters().get(
        MediaItem.DESCRIPTION));
    Assert.assertEquals(mimeType, request.getRestPayloadParameters().get(
        MediaItem.MIME_TYPE));
    Assert.assertEquals(type, request.getRestPayloadParameters().get(
        MediaItem.TYPE));
    Assert.assertEquals(url, request.getRestPayloadParameters().get(
        MediaItem.URL));

    Map<String, Object> parameters = request.getRpcPayloadParameters();
    Assert.assertEquals(itemId, parameters.get(MediaItem.ID));
    Assert.assertEquals(albumId, parameters.get(MediaItem.ALBUM_ID));
    Assert.assertEquals(caption, parameters.get(MediaItem.CAPTION));
    Assert.assertEquals(thumbnailUrl, parameters.get(MediaItem.THUMBNAIL_URL));
    Assert.assertEquals(description, parameters.get(MediaItem.DESCRIPTION));
    Assert.assertEquals(mimeType, parameters.get(MediaItem.MIME_TYPE));
    Assert.assertEquals(type, parameters.get(MediaItem.TYPE));
    Assert.assertEquals(url, parameters.get(MediaItem.URL));

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"mediaItems.update\"",
          "\"userId\":\"@me\"", "\"albumId\":\"" + albumId + "\"",
          "\"itemId\":\"" + itemId + "\"", "\"caption\":\"" + caption + "\"",
          "\"description\":\"" + description + "\"", "\"mime_type\":\"" +
          mimeType + "\"", "\"thumbnailUrl\":\"" + thumbnailUrl + "\"",
          "\"type\":\"" + type + "\"", "\"url\":\"" + url + "\"");
    } else {
      assertRestUrlValid("PUT", "/mediaitems/@me/@self/" + albumId + "/"
          + itemId);
    }
  }

  @Ignore("need set the content file to a valid image file")
  @Test
  public void testMediaItemsUploadImage() throws RequestException,
      IOException {
    String albumId = "myNewAlbum001";
    String itemId = "first";
    String caption = "item caption";
    String description = "item description";
    String mimeType = "image/jpeg";
    String thumbnailUrl = "http://thumbnail_url";
    String type = "image";
    String url = "http://url";
    MediaItem item = new MediaItem();
    item.setId(itemId);
    item.setAlbumId(albumId);
    item.setCaption(caption);
    item.setDescription(description);
    item.setMimeType(mimeType);
    item.setThumbnailUrl(thumbnailUrl);
    item.setType(type);
    item.setUrl(url);
    File content = new File("");

    request = MediaItemsService.uploadImage(item, content);
    assertRequestValid(MediaItem.class, "mediaItems.create", "POST");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent(albumId, Request.ALBUM_ID);

    Assert.assertEquals(itemId, request.getRestPayloadParameters().get(
        MediaItem.ID));
    Assert.assertEquals(albumId, request.getRestPayloadParameters().get(
        MediaItem.ALBUM_ID));
    Assert.assertEquals(caption, request.getRestPayloadParameters().get(
        MediaItem.CAPTION));
    Assert.assertEquals(thumbnailUrl, request.getRestPayloadParameters().get(
        MediaItem.THUMBNAIL_URL));
    Assert.assertEquals(description, request.getRestPayloadParameters().get(
        MediaItem.DESCRIPTION));
    Assert.assertEquals(mimeType, request.getRestPayloadParameters().get(
        MediaItem.MIME_TYPE));
    Assert.assertEquals(type, request.getRestPayloadParameters().get(
        MediaItem.TYPE));
    Assert.assertEquals(url, request.getRestPayloadParameters().get(
        MediaItem.URL));

    Map<String, Object> parameters = request.getRpcPayloadParameters();
    Assert.assertEquals(itemId, parameters.get(MediaItem.ID));
    Assert.assertEquals(albumId, parameters.get(MediaItem.ALBUM_ID));
    Assert.assertEquals(caption, parameters.get(MediaItem.CAPTION));
    Assert.assertEquals(thumbnailUrl, parameters.get(MediaItem.THUMBNAIL_URL));
    Assert.assertEquals(description, parameters.get(MediaItem.DESCRIPTION));
    Assert.assertEquals(mimeType, parameters.get(MediaItem.MIME_TYPE));
    Assert.assertEquals(type, parameters.get(MediaItem.TYPE));
    Assert.assertEquals(url, parameters.get(MediaItem.URL));

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"mediaItems.create\"",
          "\"userId\":\"@me\"", "\"albumId\":\"" + albumId + "\"",
          "\"itemId\":\"" + itemId + "\"", "\"caption\":\"" + caption + "\"",
          "\"description\":\"" + description + "\"", "\"mime_type\":\"" +
          mimeType + "\"", "\"thumbnailUrl\":\"" + thumbnailUrl + "\"",
          "\"type\":\"" + type + "\"", "\"url\":\"" + url + "\"");
    } else {
      assertRestUrlValid("POST", "/mediaitems/@me/@self/" + albumId + "/"
          + itemId);
    }
  }

  @Ignore("need set the content file to a valid video file")
  @Test
  public void testMediaItemsUploadVideo() throws RequestException,
      IOException {
    String albumId = "myNewAlbum001";
    String itemId = "first";
    String caption = "item caption";
    String description = "item description";
    String mimeType = "image/jpeg";
    String thumbnailUrl = "http://thumbnail_url";
    String type = "image";
    String url = "http://url";
    MediaItem item = new MediaItem();
    item.setId(itemId);
    item.setAlbumId(albumId);
    item.setCaption(caption);
    item.setDescription(description);
    item.setMimeType(mimeType);
    item.setThumbnailUrl(thumbnailUrl);
    item.setType(type);
    item.setUrl(url);
    File content = new File("");

    request = MediaItemsService.uploadVideo(item, content);
    assertRequestValid(MediaItem.class, "mediaItems.create", "POST");
    assertRequestComponent("@me", Request.GUID);
    assertRequestComponent(albumId, Request.ALBUM_ID);

    Assert.assertEquals(itemId, request.getRestPayloadParameters().get(
        MediaItem.ID));
    Assert.assertEquals(albumId, request.getRestPayloadParameters().get(
        MediaItem.ALBUM_ID));
    Assert.assertEquals(caption, request.getRestPayloadParameters().get(
        MediaItem.CAPTION));
    Assert.assertEquals(thumbnailUrl, request.getRestPayloadParameters().get(
        MediaItem.THUMBNAIL_URL));
    Assert.assertEquals(description, request.getRestPayloadParameters().get(
        MediaItem.DESCRIPTION));
    Assert.assertEquals(mimeType, request.getRestPayloadParameters().get(
        MediaItem.MIME_TYPE));
    Assert.assertEquals(type, request.getRestPayloadParameters().get(
        MediaItem.TYPE));
    Assert.assertEquals(url, request.getRestPayloadParameters().get(
        MediaItem.URL));

    Map<String, Object> parameters = request.getRpcPayloadParameters();
    Assert.assertEquals(itemId, parameters.get(MediaItem.ID));
    Assert.assertEquals(albumId, parameters.get(MediaItem.ALBUM_ID));
    Assert.assertEquals(caption, parameters.get(MediaItem.CAPTION));
    Assert.assertEquals(thumbnailUrl, parameters.get(MediaItem.THUMBNAIL_URL));
    Assert.assertEquals(description, parameters.get(MediaItem.DESCRIPTION));
    Assert.assertEquals(mimeType, parameters.get(MediaItem.MIME_TYPE));
    Assert.assertEquals(type, parameters.get(MediaItem.TYPE));
    Assert.assertEquals(url, parameters.get(MediaItem.URL));

    super.toHttpMessage();
    super.assertMessageValid();
    if (isRpc) {
      assertMsgBodyHas("\"method\":\"mediaItems.create\"",
          "\"userId\":\"@me\"", "\"albumId\":\"" + albumId + "\"",
          "\"itemId\":\"" + itemId + "\"", "\"caption\":\"" + caption + "\"",
          "\"description\":\"" + description + "\"", "\"mime_type\":\"" +
          mimeType + "\"", "\"thumbnailUrl\":\"" + thumbnailUrl + "\"",
          "\"type\":\"" + type + "\"", "\"url\":\"" + url + "\"");
    } else {
      assertRestUrlValid("POST", "/mediaitems/@me/@self/" + albumId + "/"
          + itemId);
    }
  }
}

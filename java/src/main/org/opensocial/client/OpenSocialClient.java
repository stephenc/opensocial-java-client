/* Copyright (c) 2008 Google Inc.
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


package org.opensocial.client;

import net.oauth.OAuthException;

import org.json.JSONException;
import org.opensocial.data.OpenSocialAppData;
import org.opensocial.data.OpenSocialPerson;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * An object which provides methods for indirectly interacting with the
 * OpenSocial RESTful and/or JSON-RPC APIs exposed by a container. Clients
 * can create a new instance of this class to fetch profile information for a
 * particular user, the profile information for friends of a particular user,
 * and app data from an application's persistent data store. In the future,
 * this class will expose methods to update app data, fetch group and
 * activity information, and so forth.
 *
 * @author Jason Cooper
 */
public class OpenSocialClient {

  public static class Properties {
    public static final String CONSUMER_SECRET = "consumer_secret";
    public static final String REST_BASE_URI = "rest_base_uri";
    public static final String RPC_ENDPOINT = "rpc_endpoint";
    public static final String CONSUMER_KEY = "consumer_key";
    public static final String VIEWER_ID = "viewer_id";
    public static final String DOMAIN = "domain";
    public static final String TOKEN = "token";
  }

  private final Map<String, String> properties;

  public OpenSocialClient() {
    this(null);
  }

  public OpenSocialClient(String domain) {
    properties = new HashMap<String, String>();
    this.setProperty(Properties.DOMAIN, domain);
  }

  public String getProperty(String name) {
    return properties.get(name);
  }
  
  public void setProperty(String name, String value) {
    properties.put(name, value);
  }

  public OpenSocialPerson fetchPerson()
      throws OpenSocialRequestException, URISyntaxException, IOException,
          JSONException, OAuthException, InstantiationException, IllegalAccessException {
    
    return this.fetchPerson("@me");
  }
  
  /**
   * Requests a user's profile details and returns an OpenSocialPerson
   * instance with all of the relevant information.
   * 
   * @param  userId OpenSocial ID of user whose profile details are to be
   *         fetched
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         JSON output that gets returned
   * @throws URISyntaxException
   * @throws JSONException 
   * @throws OAuthException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public OpenSocialPerson fetchPerson(String userId)
      throws OpenSocialRequestException, URISyntaxException, IOException,
          JSONException, OAuthException, InstantiationException, IllegalAccessException {

    OpenSocialResponse response = fetchPeople(userId, "@self");
    return response.getItemAsPerson("people");
  }

  /**
   * Requests the friend list and profile information for a user and returns a
   * Java Collection of OpenSocialPerson instances representing the friends.
   * 
   * @param  userId OpenSocial ID of user whose friend list is to be fetched
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         JSON output that gets returned
   * @throws URISyntaxException
   * @throws JSONException 
   * @throws OAuthException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public Collection<OpenSocialPerson> fetchFriends(String userId)
      throws OpenSocialRequestException, URISyntaxException, IOException,
          JSONException, OAuthException, InstantiationException, IllegalAccessException {

    OpenSocialResponse response = fetchPeople(userId, "@friends");
    return response.getItemAsPersonCollection("people");
  }

  public OpenSocialAppData fetchPersonAppData(String userId)
      throws OpenSocialRequestException, URISyntaxException, IOException,
          JSONException, OAuthException, InstantiationException, IllegalAccessException {

    return fetchPersonAppData(userId, "@app");
  }
  
  /**
   * Requests all persistent data of a given user for a given application and
   * returns an OpenSocialObject containing a field for each key saved in the
   * client application.
   * 
   * @param  userId OpenSocial ID of user whose app data is to be fetched
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         JSON output that gets returned
   * @throws URISyntaxException
   * @throws JSONException 
   * @throws OAuthException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public OpenSocialAppData fetchPersonAppData(String userId, String appId)
      throws OpenSocialRequestException, URISyntaxException, IOException,
          JSONException, OAuthException, InstantiationException, IllegalAccessException {

    OpenSocialResponse response = fetchAppData(userId, "@self", appId);
    return response.getItemAsAppData("appdata");
  }

  private OpenSocialResponse fetchPeople(String userId, String groupId)
      throws OpenSocialRequestException, URISyntaxException, IOException,
          JSONException, OAuthException {

    if (userId.equals("") || groupId.equals("")) {
      throw new OpenSocialRequestException("Invalid request parameters");
    }
    
    OpenSocialRequest r =
      OpenSocialClient.newFetchPeopleRequest(userId, groupId);

    OpenSocialBatch batch = new OpenSocialBatch();
    batch.addRequest(r, "people");
    
    return batch.submit(this);
  }

  private OpenSocialResponse fetchAppData(String userId, String groupId, String appId)
      throws OpenSocialRequestException, URISyntaxException, IOException,
          JSONException, OAuthException {
    
    if (userId.equals("") || groupId.equals("") || appId.equals("")) {
      throw new OpenSocialRequestException("Invalid request parameters");
    }
    
    OpenSocialRequest r =
      OpenSocialClient.newFetchPersonAppDataRequest(userId, groupId, appId);
    
    OpenSocialBatch batch = new OpenSocialBatch();
    batch.addRequest(r, "appdata");
    
    return batch.submit(this);
  }
  
  public static OpenSocialRequest newFetchPeopleRequest(String userId, String groupId) {
    OpenSocialRequest r = new OpenSocialRequest("people/", "people.get");
    r.addParameter("groupId", groupId);
    r.addParameter("userId", userId);
    
    return r;
  }
  
  public static OpenSocialRequest newFetchPersonAppDataRequest(String userId, String groupId, String appId) {
    OpenSocialRequest r = new OpenSocialRequest("appdata/", "appdata.get");
    r.addParameter("groupId", groupId);
    r.addParameter("userId", userId);
    r.addParameter("appId", appId);
    
    return r;
  }

  public static OpenSocialRequest newFetchPersonAppDataRequest(String userId, String groupId) {
    return newFetchPersonAppDataRequest(userId, groupId, "@app");
  }
}

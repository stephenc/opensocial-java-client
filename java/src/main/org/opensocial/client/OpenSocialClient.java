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
 * and App Data from an application's persistent data store. This class also
 * exposes static methods for creating OpenSocialRequest instances, which
 * can be added to OpenSocialBatchRequest instances, which is ideal for
 * requesting bulk data from containers exposing an RPC endpoint.
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

  /**
   * Returns the value of the property with the passed name.
   * 
   * @param  name Name of desired property
   */
  public String getProperty(String name) {
    return properties.get(name);
  }

  /**
   * Adds a new property with the passed name and value.
   * 
   * @param  name Property name
   * @param  value Property value
   */
  public void setProperty(String name, String value) {
    properties.put(name, value);
  }

  /**
   * Requests the profile details of the current user (i.e. the viewer as
   * identified by the VIEWER_ID property) and returns an OpenSocialPerson
   * instance with all of the corresponding information.
   * 
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         response that the container returns
   * @throws JSONException
   * @throws OAuthException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   * @throws URISyntaxException
   */
  public OpenSocialPerson fetchPerson()
      throws OpenSocialRequestException, JSONException, OAuthException,
             IllegalAccessException, InstantiationException, IOException,
             URISyntaxException {

    return this.fetchPerson("@me");
  }

  /**
   * Requests a user's profile details and returns an OpenSocialPerson
   * instance with all of the corresponding information.
   * 
   * @param  userId OpenSocial ID of user whose profile details are to be
   *         fetched
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         response that the container returns
   * @throws JSONException
   * @throws OAuthException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   * @throws URISyntaxException
   */
  public OpenSocialPerson fetchPerson(String userId)
      throws OpenSocialRequestException, JSONException, OAuthException,
             IllegalAccessException, InstantiationException, IOException,
             URISyntaxException {

    OpenSocialResponse response = fetchPeople(userId, "@self");
    return response.getItemAsPerson("people");
  }

  /**
   * Requests profile details for the friends of a given user and returns a
   * Java Collection of OpenSocialPerson instances representing the friends
   * with all of the corresponding information.
   * 
   * @param  userId OpenSocial ID of user whose friend list is to be fetched
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         response that the container returns
   * @throws JSONException
   * @throws OAuthException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   * @throws URISyntaxException
   */
  public Collection<OpenSocialPerson> fetchFriends(String userId)
      throws OpenSocialRequestException, JSONException, OAuthException,
             IllegalAccessException, InstantiationException, IOException,
             URISyntaxException {

    OpenSocialResponse response = fetchPeople(userId, "@friends");
    return response.getItemAsPersonCollection("people");
  }

  /**
   * Requests the persistent key-value pairs comprising a given user's "App
   * Data for the current application and returns a specialized
   * OpenSocialObject instance mapping each pair to a field.
   * 
   * @param  userId OpenSocial ID of user whose App Data is to be fetched
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         response that the container returns
   * @throws JSONException
   * @throws OAuthException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   * @throws URISyntaxException
   */
  public OpenSocialAppData fetchPersonAppData(String userId)
      throws OpenSocialRequestException, JSONException, OAuthException,
             IllegalAccessException, InstantiationException, IOException,
             URISyntaxException {

    return fetchPersonAppData(userId, "@app");
  }

  /**
   * Requests the persistent key-value pairs comprising a given user's "App
   * Data for the application with the passed ID and returns a specialized
   * OpenSocialObject instance mapping each pair to a field.
   * 
   * @param  userId OpenSocial ID of user whose App Data is to be fetched
   * @param  appId The ID of the application to fetch user App Data for
   *         or "@app" for the current application
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         response that the container returns
   * @throws JSONException
   * @throws OAuthException
   * @throws IllegalAccessException
   * @throws InstantiationException
   * @throws IOException
   * @throws URISyntaxException
   */
  public OpenSocialAppData fetchPersonAppData(String userId, String appId)
      throws OpenSocialRequestException, JSONException, OAuthException,
             IllegalAccessException, InstantiationException, IOException,
             URISyntaxException {

    OpenSocialResponse response = fetchAppData(userId, "@self", appId);
    return response.getItemAsAppData("appdata");
  }

  /**
   * Creates and submits a new request to retrieve the person or group of
   * people selected by the arguments and returns the response from the
   * container as an OpenSocialResponse object.
   * 
   * @param  userId OpenSocial ID of the request's target
   * @param  groupId "@self" to fetch the user's profile details or "@friends"
   *         to fetch the user's friend list
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         response that the container returns
   * @throws JSONException
   * @throws OAuthException
   * @throws IOException
   * @throws URISyntaxException
   */
  private OpenSocialResponse fetchPeople(String userId, String groupId)
      throws OpenSocialRequestException, JSONException, OAuthException,
             IOException, URISyntaxException {

    if (userId.equals("") || groupId.equals("")) {
      throw new OpenSocialRequestException("Invalid request parameters");
    }

    OpenSocialRequest r =
      OpenSocialClient.newFetchPeopleRequest(userId, groupId);

    OpenSocialBatch batch = new OpenSocialBatch();
    batch.addRequest(r, "people");

    return batch.send(this);
  }

  /**
   * Creates and submits a new request to retrieve the persistent App Data for
   * the person or group of people selected by the arguments for the specified
   * application and returns the response from the container as an
   * OpenSocialResponse object.
   * 
   * @param  userId OpenSocial ID of the request's target
   * @param  groupId "@self" to fetch the user's App Data or "@friends" to
   *         fetch App Data for the user's friends
   * @param  appId The ID of the application to fetch user App Data for
   *         or "@app" for the current application
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         response that the container returns
   * @throws JSONException
   * @throws OAuthException
   * @throws IOException
   * @throws URISyntaxException
   */
  private OpenSocialResponse fetchAppData(
      String userId, String groupId, String appId)
      throws OpenSocialRequestException, JSONException, OAuthException,
             IOException, URISyntaxException {

    if (userId.equals("") || groupId.equals("") || appId.equals("")) {
      throw new OpenSocialRequestException("Invalid request parameters");
    }

    OpenSocialRequest r =
        OpenSocialClient.newFetchPersonAppDataRequest(userId, groupId, appId);

    OpenSocialBatch batch = new OpenSocialBatch();
    batch.addRequest(r, "appdata");

    return batch.send(this);
  }

  /**
   * Creates and returns a new OpenSocialRequest object for retrieving the
   * person or group of people selected by the arguments.
   * 
   * @param  userId OpenSocial ID of the request's target
   * @param  groupId "@self" to fetch the user's profile details or "@friends"
   *         to fetch the user's friend list
   */
  public static OpenSocialRequest newFetchPeopleRequest(
      String userId, String groupId) {
    
    OpenSocialRequest r = new OpenSocialRequest("people", "people.get");
    r.addParameter("groupId", groupId);
    r.addParameter("userId", userId);
    
    return r;
  }

  /**
   * Creates and returns a new OpenSocialRequest object for retrieving the
   * persistent App Data for the person or group of people selected by the
   * arguments for the specified application.
   * 
   * @param  userId OpenSocial ID of the request's target
   * @param  groupId "@self" to fetch the user's App Data or "@friends" to
   *         fetch App Data for the user's friends
   * @param  appId The ID of the application to fetch user App Data for
   *         or "@app" for the current application
   */
  public static OpenSocialRequest newFetchPersonAppDataRequest(
      String userId, String groupId, String appId) {
    
    OpenSocialRequest r = new OpenSocialRequest("appdata", "appdata.get");
    r.addParameter("groupId", groupId);
    r.addParameter("userId", userId);
    r.addParameter("appId", appId);

    return r;
  }

  /**
   * Creates and returns a new OpenSocialRequest object for retrieving the
   * persistent App Data for the person or group of people selected by the
   * arguments for the current application.
   * 
   * @param  userId OpenSocial ID of the request's target
   * @param  groupId "@self" to fetch the user's App Data or "@friends" to
   *         fetch App Data for the user's friends
   */
  public static OpenSocialRequest newFetchPersonAppDataRequest(
      String userId, String groupId) {

    return newFetchPersonAppDataRequest(userId, groupId, "@app");
  }
}

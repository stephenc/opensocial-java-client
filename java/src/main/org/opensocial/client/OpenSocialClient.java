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

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.httpclient4.HttpClientPool;
import net.oauth.client.httpclient4.OAuthHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.opensocial.data.OpenSocialAppData;
import org.opensocial.data.OpenSocialPerson;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String ACCESS_TOKEN_SECRET = "accessTokenSecret";
  }

  private final Map<String, String> properties;
  private OAuthHttpClient oAuthHttpClient;

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

  private OAuthConsumer getOAuthConsumer(OpenSocialProvider provider) {
    OAuthServiceProvider serviceProvider = new OAuthServiceProvider(provider.requestTokenUrl,
        provider.authorizeUrl, provider.accessTokenUrl);
    return new OAuthConsumer(null, getProperty(Properties.CONSUMER_KEY),
        getProperty(Properties.CONSUMER_SECRET), serviceProvider);
  }

  private OAuthHttpClient getOAuthHttpClient() {
    if (oAuthHttpClient == null) {
      final HttpClient httpClient = new DefaultHttpClient();

      HttpClientPool clientPool = new HttpClientPool() {
        public HttpClient getHttpClient(URL server) {
          return httpClient;
        }
      };

      oAuthHttpClient = new OAuthHttpClient(clientPool);
    }

    return oAuthHttpClient;
  }

  public Token getRequestToken(OpenSocialProvider provider)
      throws IOException, URISyntaxException, OAuthException {

    if (provider.requestTokenUrl == null) {
      // Used for unregistered oauth
      return new Token("", "");
    }

    OAuthHttpClient httpClient = getOAuthHttpClient();
    OAuthAccessor accessor = new OAuthAccessor(getOAuthConsumer(provider));

    Set<Map.Entry<String,String>> extraParams = null;
    if (provider.requestTokenParams != null) {
      extraParams = provider.requestTokenParams.entrySet();
    }
    httpClient.getRequestToken(accessor, "GET", extraParams);

    return new Token(accessor.requestToken, accessor.tokenSecret);
  }

  public String getAuthorizationUrl(OpenSocialProvider provider, Token requestToken,
      String callbackUrl) {
    if (requestToken.token == null || requestToken.token.equals("")) {
      // This is an unregistered oauth request
      return provider.authorizeUrl + "?oauth_callback=" + callbackUrl;
    }
    return provider.authorizeUrl + "?oauth_token=" + requestToken.token
        + "&oauth_callback=" + callbackUrl;
  }

  public Token getAccessToken(OpenSocialProvider provider, Token requestToken)
      throws IOException, URISyntaxException, OAuthException {
    OAuthHttpClient httpClient = getOAuthHttpClient();
    OAuthAccessor accessor = new OAuthAccessor(getOAuthConsumer(provider));
    accessor.accessToken = requestToken.token;
    accessor.tokenSecret = requestToken.secret;

    OAuthMessage message = httpClient.invoke(accessor, "GET", provider.accessTokenUrl, null);
    return new Token(message.getToken(), message.getParameter("oauth_token_secret"));
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
   * @throws IOException
   * @throws URISyntaxException
   */
  public OpenSocialPerson fetchPerson()
      throws OpenSocialRequestException, JSONException, OAuthException,
      IOException, URISyntaxException {

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
   * @throws IOException
   * @throws URISyntaxException
   */
  public OpenSocialPerson fetchPerson(String userId)
      throws OpenSocialRequestException, JSONException, OAuthException,
      IOException, URISyntaxException {

    OpenSocialResponse response = fetchPeople(userId, "@self");
    if (response == null) {
      System.out.println("response is null");
    }
    return response.getItemAsPerson("people");
  }

  /**
   * Requests profile details for the friends of the current user and returns a
   * Java List of OpenSocialPerson instances representing the friends
   * with all of the corresponding information.
   *
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         response that the container returns
   * @throws JSONException
   * @throws OAuthException
   * @throws IOException
   * @throws URISyntaxException
   */
  public List<OpenSocialPerson> fetchFriends()
      throws OpenSocialRequestException, JSONException, OAuthException,
      IOException, URISyntaxException {

    return fetchFriends("@me");
  }

  /**
   * Requests profile details for the friends of a given user and returns a
   * Java List of OpenSocialPerson instances representing the friends
   * with all of the corresponding information.
   *
   * @param  userId OpenSocial ID of user whose friend list is to be fetched
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         response that the container returns
   * @throws JSONException
   * @throws OAuthException
   * @throws IOException
   * @throws URISyntaxException
   */
  public List<OpenSocialPerson> fetchFriends(String userId)
      throws OpenSocialRequestException, JSONException, OAuthException,
      IOException, URISyntaxException {
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
   * @throws IOException
   * @throws URISyntaxException
   */
  public OpenSocialAppData fetchPersonAppData(String userId)
      throws OpenSocialRequestException, JSONException, OAuthException,
      IOException, URISyntaxException {

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
   * @throws IOException
   * @throws URISyntaxException
   */
  public OpenSocialAppData fetchPersonAppData(String userId, String appId)
      throws OpenSocialRequestException, JSONException, OAuthException,
      IOException, URISyntaxException {

    OpenSocialResponse response = fetchAppData(userId, "@self", appId);
    return response.getItemAsAppData("appdata");
  }
  
  public void updatePersonAppData(String key, String value)
      throws OpenSocialRequestException, JSONException, OAuthException,
      IOException, URISyntaxException {

    Map<String, String> data = new HashMap<String, String>();
    data.put(key, value);
    
    updatePersonAppData("@viewer", data);
  }

  public void updatePersonAppData(Map<String, String> data)
      throws OpenSocialRequestException, JSONException, OAuthException,
      IOException, URISyntaxException {

    updatePersonAppData("@viewer", data);
  }

  public void updatePersonAppData(String userId, String key, String value)
      throws OpenSocialRequestException, JSONException, OAuthException,
      IOException, URISyntaxException {

    Map<String, String> data = new HashMap<String, String>();
    data.put(key, value);

    updatePersonAppData(userId, data);
  }

  public void updatePersonAppData(String userId, Map<String, String> data)
      throws OpenSocialRequestException, JSONException, OAuthException,
      IOException, URISyntaxException {

    updateAppData(userId, data);
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

  private OpenSocialResponse updateAppData(String userId, Map<String, String> data)
      throws OpenSocialRequestException, JSONException, OAuthException,
      IOException, URISyntaxException {

    OpenSocialRequest r = OpenSocialClient.newUpdatePersonAppDataRequest(userId, data);

    OpenSocialBatch batch = new OpenSocialBatch();
    batch.addRequest(r, "appdata");

    return batch.send(this);
  }

  /**
   * Creates and returns a new OpenSocialRequest object for retrieving the
   * profile information for a single person.
   *
   * @param  userId OpenSocial ID of the request's target
   */
  public static OpenSocialRequest newFetchPersonRequest(String userId) {
    return newFetchPeopleRequest(userId, "@self");
  }

  /**
   * Creates and returns a new OpenSocialRequest object for retrieving the
   * friends of the given user.
   *
   * @param  userId OpenSocial ID of the request's target
   */
  public static OpenSocialRequest newFetchFriendsRequest(String userId) {
    return newFetchPeopleRequest(userId, "@friends");
  }

  /**
   * Creates and returns a new OpenSocialRequest object for retrieving the
   * person or group of people selected by the arguments.
   *
   * @param  userId OpenSocial ID of the request's target
   * @param  groupId "@self" to fetch the user's profile details or "@friends"
   *         to fetch the user's friend list
   */
  private static OpenSocialRequest newFetchPeopleRequest(
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

  public static OpenSocialRequest newUpdatePersonAppDataRequest(
      String userId, Map<String, String> data) {

    OpenSocialRequest r = new OpenSocialRequest("appdata", "PUT", "appdata.update");
    r.addParameter("groupId", "@self");
    r.addParameter("userId", userId);
    r.addParameter("appId", "@app");
    r.addParameter("data", data);
    
    String[] fields = new String[data.size()];
    fields = data.keySet().toArray(fields);    
    r.addParameter("fields", fields);

    return r;
  }
}

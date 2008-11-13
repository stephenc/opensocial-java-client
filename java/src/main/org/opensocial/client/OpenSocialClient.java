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

import org.json.JSONException;
import org.opensocial.data.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
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

  private static enum Operation {
    GET_PEOPLE,
    GET_DATA
  }

  private static enum Selector {
    FRIENDS,
    SELF
  }

  public static enum Property {
    RESTFUL_BASE_URI,
    SHARED_SECRET,
    RPC_ENDPOINT,
    DOMAIN,
    TOKEN
  }

  private final Map<Property, String> properties;

  public OpenSocialClient() {
    this(null);
  }

  public OpenSocialClient(String domain) {
    properties = new HashMap<Property, String>();
    this.setProperty(Property.DOMAIN, domain);
  }

  private String getRestEndpoint(Operation op) {
    String uri = this.properties.get(Property.RESTFUL_BASE_URI);

    if (uri != null) {
      if (op.equals(Operation.GET_PEOPLE)) {
        return uri + "people/";
      } else if (op.equals(Operation.GET_DATA)) {
        return uri + "appdata/";
      }
    }

    return null;
  }

  private String getRpcMethod(Operation op) {
    if (op.equals(Operation.GET_PEOPLE)) {
      return "people.get";
    }
    if (op.equals(Operation.GET_DATA)) {
      return "appdata.get";
    }

    return null;
  }

  private String getSelectorString(Selector selector) {
    if (selector.equals(Selector.FRIENDS)) {
      return "@friends";
    } else if (selector.equals(Selector.SELF)) {
      return "@self";
    }

    return null;
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
   */
  public OpenSocialPerson fetchPerson(String userId)
      throws OpenSocialRequestException, URISyntaxException, IOException,
          JSONException {

    String responseString = fetchPeople(userId, Selector.SELF);
    return OpenSocialJsonParser.parsePersonEntry(responseString);
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
   */
  public Collection<OpenSocialPerson> fetchFriends(String userId)
      throws OpenSocialRequestException, URISyntaxException, IOException,
          JSONException {

    String responseString = fetchPeople(userId, Selector.FRIENDS);
    return OpenSocialJsonParser.parsePersonCollection(responseString);
  }

  private String fetchPeople(String userId, Selector selector)
      throws OpenSocialRequestException, URISyntaxException, IOException,
          JSONException {

    String groupId = this.getSelectorString(selector);

    if (userId.equals("")) {
      throw new OpenSocialRequestException("Invalid user ID");
    }
    if (groupId == null) {
      throw new OpenSocialRequestException("Invalid group ID");
    }

    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("groupId", groupId);
    parameters.put("userId", userId);

    OpenSocialRequest request =
        this.getRequest(Operation.GET_PEOPLE, parameters);
    return request.getResponseString();
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
   */
  public OpenSocialObject fetchPersonAppData(String userId, String appId)
      throws OpenSocialRequestException, URISyntaxException, IOException,
          JSONException {

    String responseString = fetchAppData(userId, Selector.SELF, appId);
    return OpenSocialJsonParser.parseDataEntry(responseString);
  }

  private String fetchAppData(String userId, Selector selector, String appId)
      throws OpenSocialRequestException, URISyntaxException, IOException,
          JSONException {

    String groupId = this.getSelectorString(selector);

    if (userId.equals("")) {
      throw new OpenSocialRequestException("Invalid user ID");
    }
    if (appId.equals("")) {
      throw new OpenSocialRequestException("Invalid application ID");
    }
    if (groupId == null) {
      throw new OpenSocialRequestException("Invalid group ID");
    }

    Map<String, String> parameters = new HashMap<String, String>();
    parameters.put("groupId", groupId);
    parameters.put("userId", userId);
    parameters.put("appId", appId);

    OpenSocialRequest request = this.getRequest(Operation.GET_DATA,
                                                parameters);
    return request.getResponseString();
  }

  private OpenSocialRequest getRequest(
      Operation op, Map<String, String> components)
      throws OpenSocialRequestException, URISyntaxException, IOException,
          JSONException {

    StringBuilder path = new StringBuilder();
    
    String rpcEndpoint = this.properties.get(Property.RPC_ENDPOINT);
    String restfulBaseUri = this.properties.get(Property.RESTFUL_BASE_URI);

    if (rpcEndpoint != null) {
      String methodName = this.getRpcMethod(op);
      path.append(rpcEndpoint);

      path.append("?format=json");
      if (this.properties.get(Property.TOKEN) != null) {
        path.append("&st=");
        path.append(this.properties.get(Property.TOKEN));
      }

      return new OpenSocialRequest(
          new URL(path.toString()), methodName, components);
    } else if (restfulBaseUri != null) {
      path.append(this.getRestEndpoint(op));
      if (components.get("userId") != null) {
        path.append(components.get("userId"));
        path.append("/");
      }
      if (components.get("groupId") != null) {
        path.append(components.get("groupId"));
        path.append("/");
      }
      if (components.get("appId") != null) {
        path.append(components.get("appId"));
        path.append("/");
      }

      path.append("?format=json");
      if (this.properties.get(Property.TOKEN) != null) {
        path.append("&st=");
        path.append(this.properties.get(Property.TOKEN));
      }

      return new OpenSocialRequest(new URL(path.toString()));
    } else {
      throw new OpenSocialRequestException(
          "Base URI or RPC endpoint required");
    }    
  }

  public void setProperty(Property name, String value) {
    properties.put(name, value);
  }

  public String getDomain() {
    return this.properties.get(Property.DOMAIN);
  }
}

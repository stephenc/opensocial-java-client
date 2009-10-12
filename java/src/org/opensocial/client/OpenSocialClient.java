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

package org.opensocial.client;

import org.json.JSONObject;

// Models
import org.opensocial.data.OpenSocialActivity;
import org.opensocial.data.OpenSocialAppData;
import org.opensocial.data.OpenSocialPerson;
import org.opensocial.providers.OpenSocialProvider;

// Services - will update this once we finalize where this include should
// live and how it should be handled.
import org.opensocial.services.*;
import org.opensocial.services.myspace.NotificationsService;
import org.opensocial.services.myspace.StatusMoodService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * @author apijason@google.com (Jason Cooper)
 * @author jle.edwards@gmail.com (Jesse Edwards)
 */
public class OpenSocialClient {

  /** Enumeration of OpenSocialClient properties that can be set by app */
  public static enum Property {
    CONSUMER_KEY, CONSUMER_SECRET, REST_BASE_URI, RPC_ENDPOINT, VIEWER_ID,
    DOMAIN, ACCESS_TOKEN_SECRET, ACCESS_TOKEN, TOKEN_NAME, TOKEN, CONTENT_TYPE,
    SIGN_BODY_HASH, DEBUG
  }

  /** Constant used to set the request's user target to the current viewer. */
  public static final String ME = "@me";

  /** Constant used to set the request's group target to the individual. */
  public static final String SELF = "@self";

  /** Constant used to set the request's group target to the user's friends.*/
  public static final String FRIENDS = "@friends";

  /** Constant used to set the request's application target to current app.*/
  public static final String APP = "@app";

  private OpenSocialProvider provider;
  
  private final Map<Property, String> properties;
  
  private MediaItemsService mediaItems = null;
  private AlbumsService albums = null;
  private NotificationsService notifications = null;
  private GroupsService groups = null;
  private StatusMoodService statusmood = null;
  private ActivitiesService activities = null;
  private AppDataService appData = null;
  private PeopleService people = null;
  private MessagesService messages = null;
  
  /**
   * Lazy load getter for mediaItems
   * 
   * @return MediaItemsService
   */
  public MediaItemsService getMediaItemsService() {
    if(mediaItems == null) {
      mediaItems = new MediaItemsService();
    }
    return mediaItems;
  }
  
  /**
   * Lazy load getter for albums
   * 
   * @return AlbumsService
   */
  public AlbumsService getAlbumsService() {
    if(albums == null) {
      albums = new AlbumsService();
    }
    return albums;
  }
  
  /**
   * Lazy load getter for notifications
   * 
   * @return NotificationsService
   */
  public NotificationsService getNotificationsService() {
    if(notifications == null) {
      notifications = new NotificationsService();
    }
    return notifications;
  }
  
  /**
   * Lazy load getter for groups
   * 
   * @return GroupsService
   */
  public GroupsService getGroupsService() {
    if(groups == null) {
      groups = new GroupsService();
    }
    return groups;
  }
  
  /**
   * Lazy load getter for statusmood
   * 
   * @return StatusMoodService
   */
  public StatusMoodService getStatusMoodService() {
    if(statusmood == null) {
      statusmood = new StatusMoodService();
    }
    return statusmood;
  }
  
  /**
   * Lazy load getter for activities
   * 
   * @return ActivitiesService
   */
  public ActivitiesService getActivitiesService() {
    if(activities == null) {
      activities = new ActivitiesService();
    }
    return activities;
  }
  
  /**
   * Lazy load getter for appData
   * 
   * @return AppDataService
   */
  public AppDataService getAppDataService() {
    if(appData == null) {
      appData = new AppDataService();
    }
    return appData;
  }
  
  /**
   * Lazy load getter for people
   * 
   * @return PeopleService
   */
  public PeopleService getPeopleService() {
    if(people == null) {
      people = new PeopleService();
    }
    return people;
  }
  
  /**
   * Lazy load getter for messages
   * 
   * @return MessageService
   */
  public MessagesService getMessagesService() {
    if(messages == null) {
      messages = new MessagesService();
    }
    return messages;
  }
  
  public OpenSocialClient() {
      this("");
  }

  public OpenSocialClient(String domain) {
    properties = new HashMap<Property, String>();

    setProperty(Property.DOMAIN, domain);
    setProperty(Property.SIGN_BODY_HASH, "true");
    setProperty(Property.CONTENT_TYPE, "application/json");
  }

  public OpenSocialClient(OpenSocialProvider provider) {
    this.provider = provider;
    properties = new HashMap<Property, String>();
    setProperty(Property.DOMAIN, provider.providerName);
    setProperty(Property.CONTENT_TYPE, provider.contentType);
    setProperty(Property.SIGN_BODY_HASH, String.valueOf(provider.signBodyHash));
    setProperty(Property.REST_BASE_URI, provider.restEndpoint);
    setProperty(Property.RPC_ENDPOINT, provider.rpcEndpoint);
  }

  /**
   * Gets the curret provider
   * 
   * @return OpenSocialProvider
   */
  public OpenSocialProvider getProvider() {
    return this.provider;
  }

  /**
   * Returns the value of the property with the passed name.
   *
   * @param  name Name of desired property
   */
  public String getProperty(Property name) {
    return properties.get(name);
  }

  /**
   * Adds a new property with the passed name and value.
   *
   * @param  name Property name
   * @param  value Property value
   */
  public void setProperty(Property name, String value) {
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
   * @throws IOException
   */
  public OpenSocialPerson fetchPerson()
      throws OpenSocialRequestException, IOException {
    return fetchPerson(ME);
  }

  public OpenSocialPerson fetchPerson(
      Map<String, String> parameters) throws
      OpenSocialRequestException, IOException {
    return fetchPerson(ME, parameters);
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
   * @throws IOException
   */
  public OpenSocialPerson fetchPerson(String userId) throws
      OpenSocialRequestException, IOException {
    Map<String, String> params = new HashMap<String, String>();
    params.put("groupId", SELF);
    
    return fetchPerson(userId, params);
  }

  public OpenSocialPerson fetchPerson(String userId,
      Map<String, String> parameters) throws
      OpenSocialRequestException, IOException {
    ArrayList<OpenSocialPerson> data = fetchPeople(userId, SELF, parameters);
    return data.get(0);
  }

  /**
   * Requests profile details for the friends of the current user and returns a
   * Java List of OpenSocialPerson instances representing the friends
   * with all of the corresponding information.
   *
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         response that the container returns
   * @throws IOException
   */
  public List<OpenSocialPerson> fetchFriends()
      throws OpenSocialRequestException, IOException {
    return fetchFriends(ME);
  }

  public List<OpenSocialPerson> fetchFriends(
      Map<String, String> parameters) throws
      OpenSocialRequestException, IOException {
    return fetchFriends(ME, parameters);
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
   * @throws IOException
   */
  public List<OpenSocialPerson> fetchFriends(String userId) throws
      OpenSocialRequestException, IOException {
    return fetchFriends(userId, new HashMap<String, String>());
  }

  public ArrayList<OpenSocialPerson> fetchFriends(String userId,
      Map<String, String> parameters) throws
      OpenSocialRequestException, IOException {
    ArrayList<OpenSocialPerson> data = fetchPeople(userId, FRIENDS, parameters);
    
    return data;
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
   * @throws IOException
   */
  public OpenSocialAppData fetchPersonAppData(String userId) throws
      OpenSocialRequestException, IOException {
    return fetchPersonAppData(userId, APP);
  }

  /**
   * Requests the persistent key-value pairs comprising a given user's App
   * Data for the application with the passed ID and returns a specialized
   * OpenSocialObject instance mapping each pair to a field.
   *
   * @param  userId OpenSocial ID of user whose App Data is to be fetched
   * @param  appId The ID of the application to fetch user App Data for
   *         or "@app" for the current application
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         response that the container returns
   * @throws IOException
   */
  public OpenSocialAppData fetchPersonAppData(String userId, String appId)
      throws OpenSocialRequestException, IOException {
    
    HashMap<String, String>params = new HashMap<String, String>();
    params.put("userId", userId);
    params.put("groupId", SELF);
    params.put("appId", appId);
    
    OpenSocialBatch batch = new OpenSocialBatch();
    batch.addRequest(this.getAppDataService().get(params), "fetchAppData");
    batch.send(this);
    
    OpenSocialHttpResponseMessage response = batch.getResponse("fetchAppData");
    ArrayList<OpenSocialAppData>appData = response.getAppDataCollection();
    
    return appData.get(0);
  }

  public void updatePersonAppData(String key, String value) throws
      OpenSocialRequestException, IOException {

    OpenSocialAppData data = new OpenSocialAppData();
    data.setField(key, value);

    updatePersonAppData("@viewer", data);
  }

  public void updatePersonAppData(OpenSocialAppData data) throws
      OpenSocialRequestException, IOException {
    updatePersonAppData("@viewer", data);
  }

  public void updatePersonAppData(String userId, String key, String value)
      throws OpenSocialRequestException, IOException {
    
    OpenSocialAppData data = new OpenSocialAppData();
    data.setField(key, value);

    updatePersonAppData(userId, data);
  }

  public void updatePersonAppData(String userId, OpenSocialAppData data)
      throws OpenSocialRequestException, IOException {
    
    OpenSocialBatch batch = new OpenSocialBatch();
    Map<String, String> params = null;
    
    // Set Request Parameters
    params = new HashMap<String, String>();
    params.put("userId", userId);
    params.put("groupId", OpenSocialClient.SELF);
    params.put("appId", "@app");
    params.put("appdata", data.toString());
    batch.addRequest(getAppDataService().update(params), "updateAppData");
    batch.send(this);
  }

  /**
   * Sends a request to delete the data associated with the passed 
   * App Data key for the current user for the current application.
   *
   * @param key
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  public void removePersonAppData(String key) 
      throws OpenSocialRequestException, IOException {
    removePersonAppData(ME, key);
  }

  /**
   * Sends a request to delete the data associated with the passed 
   * App Data keys for the current user for the current application.
   *
   * @param  keys
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  public void removePersonAppData(List<String> keys) throws
      OpenSocialRequestException, IOException {
    removePersonAppData(ME, keys);
  }

  public void removePersonAppData(String userId, String key) throws
      OpenSocialRequestException, IOException {
    List<String> keys = new ArrayList<String>();
    keys.add(key);

    removePersonAppData(userId, keys);
  }

  public void removePersonAppData(String userId, List<String> keys) throws
      OpenSocialRequestException, IOException {
    removeAppData(userId, keys);
  }

  /**
   * Returns all activities associated with the current user across all
   * applications.
   *
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  public List<OpenSocialActivity> fetchActivities() throws
      OpenSocialRequestException, IOException {
    return fetchActivities(ME, SELF, null);
  }

  /**
   * Returns all activities associated with the current user for the current
   * app.
   *
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  public List<OpenSocialActivity> fetchActivitiesForApp() throws
      OpenSocialRequestException, IOException {
    return fetchActivities(ME, SELF, APP);
  }

  /**
   * Returns all activities associated with the user with the passed ID.
   *
   * @param  userId
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  public List<OpenSocialActivity> fetchActivitiesForPerson(String userId)
      throws OpenSocialRequestException, IOException {
    return fetchActivities(userId, SELF, null);
  }

  /**
   * Returns all activities associated with the friends of the user with the
   * passed ID.
   *
   * @param  userId
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  public List<OpenSocialActivity> fetchActivitiesForFriends(String userId)
      throws OpenSocialRequestException, IOException {
    return fetchActivities(userId, FRIENDS, null);
  }

  /**
   * Method to fetch the activities for the specified user or group 
   * of users for the specified application.
   *
   * @param userId
   * @param groupId
   * @param appId
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  public List<OpenSocialActivity> fetchActivities(String userId, String
      groupId, String appId) throws OpenSocialRequestException, IOException {
    if (userId == null || userId.equals("") || groupId == null ||
        groupId.equals("")) {
      throw new OpenSocialRequestException("Invalid request parameters");
    }

    OpenSocialRequest r = OpenSocialClient.newFetchActivitiesRequest(userId,
        groupId, appId);
    OpenSocialBatch batch = new OpenSocialBatch();
    batch.addRequest(r, "activities");

    OpenSocialResponse response = batch.send(this);
    return response.getItemAsActivityCollection("activities");
  }

  /**
   * Creates an activity for the current user for the current 
   * application with the passed title and body.
   *
   * @param  title
   * @param  body
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  public void createActivity(String title, String body) throws
      OpenSocialRequestException, IOException {
    createActivity(ME, APP, title, body);
  }

  /**
   * Creates an activity for the specified user and application 
   * with the passed title and body.
   *
   * @param userId
   * @param appId
   * @param title
   * @param body
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  public void createActivity(String userId, String appId, String title,
      String body) throws OpenSocialRequestException, IOException {
    if (userId == null || userId.equals("")) {
      throw new OpenSocialRequestException("Invalid request parameters");
    }

    // Group the required activity parameters, namely title and body
    Map<String, String> activityData = new HashMap<String, String>();
    activityData.put("title", title);
    activityData.put("body", body);

    OpenSocialRequest r = OpenSocialClient.newCreateActivityRequest(userId,
        appId, activityData);
    OpenSocialBatch batch = new OpenSocialBatch();
    batch.addRequest(r, "activities");
    batch.send(this);
  }

  /**
   * Creates and submits a new request to retrieve the person or group of
   * people selected by the arguments and returns the response from the
   * container as an OpenSocialResponse object.
   *
   * @param  userId OpenSocial ID of the request's target
   * @param  groupId "@self" to fetch the user's profile details or "@friends"
   *         to fetch the user's friend list
   * @param  parameters Object containing any parameters to be passed along
             with the request
   * @throws OpenSocialRequestException if there are any runtime issues with
   *         establishing a RESTful or JSON-RPC connection or parsing the
   *         response that the container returns
   * @throws IOException
   */
  private ArrayList<OpenSocialPerson> fetchPeople(String userId, String groupId,
      Map<String, String> params) throws
      OpenSocialRequestException, IOException {
    
    //TODO: prameter defaults
    params.put("userId", userId);
    params.put("groupId", groupId);
    
    OpenSocialBatch batch = new OpenSocialBatch();
    
    batch.addRequest(getPeopleService().get(params), "fetchPeople");
    
    batch.send(this);
    OpenSocialHttpResponseMessage response = batch.getResponse("fetchPeople");
    
    return response.getPersonCollection();
  }

  /**
   * Creates and submits a new request to delete the specified App Data keys.
   *
   * @param  userId user whose app data must be deleted
   * @param  fields list of app data keys for which the key-value pairs must be
   *         deleted
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  private OpenSocialResponse removeAppData(String userId, List<String> keys)
      throws OpenSocialRequestException, IOException {
    OpenSocialRequest r =
      OpenSocialClient.newRemovePersonAppDataRequest(userId, keys);
    OpenSocialBatch batch = new OpenSocialBatch();
    batch.addRequest(r, "appdata");
    return batch.send(this);
  }

  /**
   * Creates and returns a new OpenSocialRequest object for retrieving the
   * profile information for a single person.
   *
   * @param userId OpenSocial ID of the request's target
   */
  public static OpenSocialRequest newFetchPersonRequest(String userId) {
    return newFetchPeopleRequest(userId, SELF, null);
  }

  /**
   * Creates and returns a new OpenSocialRequest object for retrieving the
   * friends of the given user.
   *
   * @param userId OpenSocial ID of the request's target
   */
  public static OpenSocialRequest newFetchFriendsRequest(String userId) {
    return newFetchPeopleRequest(userId, FRIENDS, null);
  }

  /**
   * Creates and returns a new OpenSocialRequest object for retrieving the
   * person or group of people selected by the arguments.
   *
   * @param  userId OpenSocial ID of the request's target
   * @param  groupId "@self" to fetch the user's profile details or "@friends"
   *         to fetch the user's friend list
   */
  private static OpenSocialRequest newFetchPeopleRequest(String userId, String
      groupId, Map<String, OpenSocialRequestParameter> parameters) {
    OpenSocialRequest r = new OpenSocialRequest("people", "people.get");
    if (parameters != null) {
      r.setParameters(parameters);
    }
    r.addParameter(OpenSocialRequest.GROUP_PARAMETER, groupId);
    r.addParameter(OpenSocialRequest.USER_PARAMETER, userId);

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
  public static OpenSocialRequest newFetchPersonAppDataRequest(String userId,
      String groupId, String appId) {
    OpenSocialRequest r = new OpenSocialRequest("appdata", "appdata.get");
    r.addParameter(OpenSocialRequest.GROUP_PARAMETER, groupId);
    r.addParameter(OpenSocialRequest.USER_PARAMETER, userId);
    r.addParameter(OpenSocialRequest.APP_PARAMETER, appId);

    return r;
  }

  /**
   * Creates and returns a new OpenSocialRequest object for retrieving the
   * persistent App Data for the person or group of people selected by the
   * arguments for the current application.
   *
   * @param userId OpenSocial ID of the request's target
   * @param groupId "@self" to fetch the user's App Data or "@friends" to
   *        fetch App Data for the user's friends
   */
  public static OpenSocialRequest newFetchPersonAppDataRequest(String userId,
      String groupId) {
    return newFetchPersonAppDataRequest(userId, groupId, APP);
  }

  public static OpenSocialRequest newUpdatePersonAppDataRequest(String userId,
      Map<String, String> data) {
    OpenSocialRequest r = new OpenSocialRequest("appdata", "PUT",
        "appdata.update");
    r.addParameter(OpenSocialRequest.GROUP_PARAMETER, SELF);
    r.addParameter(OpenSocialRequest.USER_PARAMETER, userId);
    r.addParameter(OpenSocialRequest.APP_PARAMETER, APP);
    r.addParameter("data", data);

    String[] fields = new String[data.size()];
    fields = data.keySet().toArray(fields);
    r.addParameter("fields", fields);

    return r;
  }

  /**
   * Creates and returns an OpenSocialRequest to remove App Data 
   * associated with the passed list of keys for the current application.
   *
   * @param userId
   * @param fieldsList
   */
  public static OpenSocialRequest newRemovePersonAppDataRequest(
	  String userId, List<String> fieldsList) {

    OpenSocialRequest r = new OpenSocialRequest("appdata", "DELETE",
        "appdata.delete");
    r.addParameter(OpenSocialRequest.GROUP_PARAMETER, SELF);
    r.addParameter(OpenSocialRequest.USER_PARAMETER, userId);
    r.addParameter(OpenSocialRequest.APP_PARAMETER, APP);

    String[] fields = new String[fieldsList.size()];
    fields = fieldsList.toArray(fields);
    r.addParameter("fields", fields);

    return r;
  }

  /**
   * Creates and returns a new OpenSocialRequest object for retrieving the
   * activities for the specified user, group and application.
   *
   * @param userId
   * @param groupId
   * @param appId
   */
  public static OpenSocialRequest newFetchActivitiesRequest(String userId,
      String groupId, String appId) {
    OpenSocialRequest r = new OpenSocialRequest("activities", 
        "activities.get");
    r.addParameter(OpenSocialRequest.USER_PARAMETER, userId);
    r.addParameter(OpenSocialRequest.GROUP_PARAMETER, groupId);

    // If appId is null, retrieve activities across all of the user's apps
    if (appId != null) {
      r.addParameter(OpenSocialRequest.APP_PARAMETER, appId);
    }

    return r;
  }

  /**
   * Creates and returns a new OpenSocialRequest object for creating an
   * activity for the specified user, group and app
   *
   * @param userId
   * @param appId
   * @param data
   */
  public static OpenSocialRequest newCreateActivityRequest(String userId,
      String appId, Map<String, String> data) {
    OpenSocialRequest r = new OpenSocialRequest("activities", "POST",
        "activities.create");
    r.addParameter(OpenSocialRequest.USER_PARAMETER, userId);
    r.addParameter(OpenSocialRequest.GROUP_PARAMETER, SELF);

    // If appId is null, retrieve activities across all of the user's apps
    if (appId != null) {
      r.addParameter(OpenSocialRequest.APP_PARAMETER, appId);
    }

    r.addParameter("activity", new JSONObject(data).toString());
    return r;
  }
}

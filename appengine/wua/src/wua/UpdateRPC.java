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

package wua;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashMap;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONObject;
import org.opensocial.data.OpenSocialPerson;

import wua.controllers.LoginController;
import wua.data.PMF;
import wua.data.Update;
import wua.data.User;

/**
 * Provides a REST-like, public RPC API for use within the application UI. Two
 * methods are exposed: post (for saving an update) and get (for
 * retrieving Updates that are recent, nearby, or friendly).
 * 
 * @author api.dwh@google (Dan Holevoet)
 */
@SuppressWarnings("serial")
public class UpdateRPC extends HttpServlet {
  
  /**
   * Handles an incoming POST request and delegates to the appropriate method
   * call.
   * 
   * @param req The incoming request (with access to the session)
   * @param resp The outgoing response
   */
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String method = req.getParameter("method");
    
    if (method.equals("post")) {
      postUpdate(req, resp);
    } else {
      loadUpdates(req, resp);
    }
  }
  
  /**
   * Handles an incoming GET request and delegates to the appropriate method
   * call.
   * 
   * @param req The incoming request (with access to the session)
   * @param resp The outgoing response
   */
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String method = req.getParameter("method");
    
    if (method.equals("post")) {
      postUpdate(req, resp);
    } else {
      loadUpdates(req, resp);
    }
  }
  
  /**
   * Saves a newly posted update to the datastore, with latitude, longitude,
   * and the specified private settings (public/private, location/non-location).
   * Writes JSON to resp.
   * 
   * @param req The incoming request (with access to the session)
   * @param resp The outgoing response
   */
  private void postUpdate(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    User user = User.getCurrentUser(req);
    if (user != null) {
      String content = req.getParameter("content");
      String latitude = req.getParameter("latitude");
      String longitude = req.getParameter("longitude");
      Boolean includePosition = (req.getParameter("position") != null);
      Boolean makePublic = (req.getParameter("public") != null);

      if (content != null && !content.equals("")) {
        if (includePosition) {
          Update.addUpdate(user.getContainerId(), user.getContainer(), user.getDisplayName(), content, 
              latitude, longitude, makePublic);
          resp.getWriter().print("{'status':'success'}");
        } else {
          Update.addUpdate(user.getContainerId(), user.getContainer(), user.getDisplayName(), content,
              null, null, makePublic);
          resp.getWriter().print("{'status':'success'}");
        }
      }
    } else {
      resp.getWriter().print("{'status':'not logged in'}");
    }    
  }
  
  /**
   * Loads updates as denoted by the incoming request parameters. Writes JSON
   * to resp.
   * 
   * @param req The incoming request (with access to the session)
   * @param resp The outgoing response
   */
  private void loadUpdates(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    LoginController controller = null;
    
    String container = "";
    PersistenceManager pm = PMF.get().getPersistenceManager();
        
    JSONObject json = null;
    
    String llBoundLat = req.getParameter("llboundlat");
    String llBoundLong = req.getParameter("llboundlong");
    String trBoundLat = req.getParameter("trboundlat");
    String trBoundLong = req.getParameter("trboundlong");
    Boolean showFriends = new Boolean(req.getParameter("showFriends"));
    Boolean showNearby = new Boolean(req.getParameter("showNearby"));
    Integer limit = 0;
    if (req.getParameter("limit") != null) {
      limit = new Integer(req.getParameter("limit"));
    }
    
    try {
      controller = new LoginController(req, resp);
      if (showFriends) {
        if (controller.client != null) {
          container = (String) req.getSession().getAttribute("container");
        
          Collection<OpenSocialPerson> friends =
            controller.client.fetchFriends();
          
          // Load updates (friends)
          json = loadUpdates(friends, container);
        } else {
          json = new JSONObject();
        }
      } else if (showNearby) {
        // Load updates (nearby)
        json = loadUpdates(llBoundLat, llBoundLong, trBoundLat, trBoundLong, limit);
      } else {
        // Load updates (recent public)
        json = loadUpdates(limit, true);
      }
    } catch (Exception e) {
      pm.close();
      HashMap<String, String> response = new HashMap<String, String>();
      response.put("status", "error");
      response.put("message", e.getMessage());
      json = new JSONObject(response);
    }
    
    resp.getWriter().print(json.toString());
  }
  
  /**
   * Loads friendly updates into JSON.
   * 
   * @param friends A Collection of the user's friends
   * @param container The container the user has logged into
   */
  @SuppressWarnings("unchecked")
  private JSONObject loadUpdates(Collection<OpenSocialPerson> friends,
      String container) {
    ArrayList<String> friendIds = new ArrayList<String>();
    int i = 0;
    for (OpenSocialPerson friend : friends) {
      if (i < 30 && !friend.getId().equals("")) {
        friendIds.add(friend.getId());
        i++;
      }
    }
    
    PersistenceManager pm = PMF.get().getPersistenceManager();
    HashMap<String, List<Update>> updates = Update.getFriendUpdates(pm, friendIds, container);
    HashMap<String, ArrayList<HashMap<String, String>>> responseMap = new HashMap<String, ArrayList<HashMap<String, String>>>();
    for (String user : updates.keySet()) {
      ArrayList<HashMap<String, String>> userUpdates = new ArrayList<HashMap<String, String>>();
      for (Update u : updates.get(user)) {
        HashMap<String, String> attributes = new HashMap();
        attributes.put("author", u.getAuthor());
        attributes.put("authorName", u.getAuthorName());
        attributes.put("container", u.getContainer());
        attributes.put("content", StringEscapeUtils.escapeHtml(u.getContent()));
        attributes.put("posted", u.getDate().toString());
        attributes.put("geohash", u.getGeohash());
        Double[] p = u.getPosition();
        if (p != null) {
          attributes.put("lat", u.getPosition()[0].toString());
          attributes.put("long", u.getPosition()[1].toString());
        }
        userUpdates.add(attributes);
      }
      responseMap.put(user, userUpdates);
    }
    
    HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>> responseObject =
      new HashMap<String, HashMap<String, ArrayList<HashMap<String, String>>>>();
    responseObject.put("data", responseMap);
    return new JSONObject(responseObject);
  }
  
  /**
   * Loads nearby updates into JSON.
   * 
   * @param llBoundLat The lower left bound latitude of the map
   * @param llBoundLong The lower left bound longitude of the map
   * @param trBoundLat The top right bound latitude of the map
   * @param trBoundLong The top right bound longitude of the map
   * @param limit The maximum number of updates to retrieve
   */
  @SuppressWarnings("unchecked")
  private JSONObject loadUpdates(String llBoundLat, String llBoundLong,
      String trBoundLat, String trBoundLong, Integer limit) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    List<Update> updates = Update.getNearbyUpdates(pm, llBoundLat, llBoundLong,
        trBoundLat, trBoundLong);
    ArrayList<HashMap<String, String>> responses = new ArrayList<HashMap<String, String>>();
    for (Update u : updates) {
      HashMap<String, String> attributes = new HashMap();
      attributes.put("author", u.getAuthor());
      attributes.put("authorName", u.getAuthorName());
      attributes.put("content", StringEscapeUtils.escapeHtml(u.getContent()));
      attributes.put("posted", u.getDate().toString());
      attributes.put("geohash", u.getGeohash());
      Double[] p = u.getPosition();
      if (p != null) {
        attributes.put("lat", u.getPosition()[0].toString());
        attributes.put("long", u.getPosition()[1].toString());
      }
      responses.add(attributes);
    }
    
    HashMap<String, ArrayList<HashMap<String, String>>> responseObject =
      new HashMap<String, ArrayList<HashMap<String, String>>>();
    responseObject.put("data", responses);
    return new JSONObject(responseObject);
  }
  
  /**
   * Loads recent updates into JSON.
   * 
   * @param limit The maximum number of updates to retrieve
   * @param isPublic Whether to load public or private updates
   */
  @SuppressWarnings("unchecked")
  private JSONObject loadUpdates(Integer limit, Boolean isPublic) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    List<Update> updates = Update.getRecentUpdates(pm, isPublic);
    ArrayList<HashMap<String, String>> responses = new ArrayList<HashMap<String, String>>();
    for (Update u : updates) {
      HashMap<String, String> attributes = new HashMap();
      attributes.put("author", u.getAuthor());
      attributes.put("authorName", u.getAuthorName());
      attributes.put("content", StringEscapeUtils.escapeHtml(u.getContent()));
      attributes.put("posted", u.getDate().toString());
      attributes.put("geohash", u.getGeohash());
      Double[] p = u.getPosition();
      if (p != null) {
        attributes.put("lat", u.getPosition()[0].toString());
        attributes.put("long", u.getPosition()[1].toString());
      }
      responses.add(attributes);
    }
    
    HashMap<String, ArrayList<HashMap<String, String>>> responseObject =
      new HashMap<String, ArrayList<HashMap<String, String>>>();
    responseObject.put("data", responses);
    return new JSONObject(responseObject);
  }
}

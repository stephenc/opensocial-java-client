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

package wua.controllers;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.opensocial.client.OpenSocialClient;
import org.opensocial.client.OpenSocialOAuthClient;
import org.opensocial.client.OpenSocialProvider;
import org.opensocial.client.OpenSocialRequestException;
import org.opensocial.client.Token;
import org.opensocial.data.OpenSocialPerson;

import wua.data.PMF;
import wua.data.User;

/**
 * Controls the authentication of a user (through 3-legged OAuth) with an
 * OpenSocial container, and the creation of a local session storing and
 * accessing these credentials.
 * 
 * @author api.dwh@google.com (Dan Holevoet)
 */
public class LoginController {
  @SuppressWarnings("serial")
  public class LoginException extends Exception {
    public String message;
    
    public LoginException(String message) {
      this.message = message;
    }
    
    public String getMessage() {
      return this.message;
    }
  }
  
  public static final String AUTH_CALLBACK = "http://os-whereyouat.appspot.com/login";
  
  public static final String PARTUZA_KEY = "e2c2d2dd-e6c4-c4df-b2c4-d6efd2dcffd1";
  public static final String PARTUZA_SECRET = "xxxxxxxxxxxxxxx";
  public static final String GOOGLE_KEY = "os-whereyouat.appspot.com";
  public static final String GOOGLE_SECRET = "xxxxxxxxxxxxxxx";
  public static final String MYSPACE_KEY = "http://os-whereyouat.appspot.com";
  public static final String MYSPACE_SECRET = "xxxxxxxxxxxxxxx";
  
  public OpenSocialClient client;
  
  /**
   * Performs 3-legged OAuth upon instantiation, or, if it has already
   * occurred, will initialize a new OpenSocialClient with the existing
   * access token and secret.
   * 
   * @param req The incoming request (with access to the session)
   * @param resp The outgoing response
   * @throws IOException
   * @throws LoginException
   */
  public LoginController(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, LoginException {
    HttpSession session = req.getSession();
        
    Boolean authenticated = (Boolean) session.getAttribute("authenticated");
    
    OpenSocialProvider provider;
    String consumerKey;
    String consumerSecret;
    
    String container = getContainer(req, session);
    if (container == null) {
      // Can't do anything without specifying a container.
      return;
    } else {
      if (container.equals("partuza")) {
        provider = OpenSocialProvider.PARTUZA;
        consumerKey = PARTUZA_KEY;
        consumerSecret = PARTUZA_SECRET;
      } else if (container.equals("myspace")) {
        provider = OpenSocialProvider.MYSPACE;
        consumerKey = MYSPACE_KEY;
        consumerSecret = MYSPACE_SECRET;
      } else if (container.equals("google")) {
        provider = OpenSocialProvider.GOOGLE;
        consumerKey = GOOGLE_KEY;
        consumerSecret = GOOGLE_SECRET;
      } else {
        throw new LoginException("An unknown container has been chosen for login.");
      }
    }
    
    client = new OpenSocialClient(provider);
    client.setProperty(OpenSocialClient.Property.CONSUMER_KEY, consumerKey);
    client.setProperty(OpenSocialClient.Property.CONSUMER_SECRET, consumerSecret);
    
    String accessToken = (String) session.getAttribute("access_token");
    String accessTokenSecret = (String) session.getAttribute("access_token_secret");
    
    if (accessToken != null && accessTokenSecret != null) {
      // Login has already occurred, update current client with credentials.
      client.setProperty(OpenSocialClient.Property.ACCESS_TOKEN, accessToken);
      client.setProperty(OpenSocialClient.Property.ACCESS_TOKEN_SECRET, accessTokenSecret);
      
      createOrLoadUser(session, container);
    } else if (authenticated == null || !authenticated.booleanValue()) {
      // Login is being initiated, request a token and redirect to container.
      try {
        Token token = OpenSocialOAuthClient.getRequestToken(client, provider);
        session.setAttribute("container", container);
        session.setAttribute("token_secret", token.secret);
        session.setAttribute("authenticated", true);
        
        String url = OpenSocialOAuthClient.getAuthorizationUrl(provider, token, AUTH_CALLBACK);
        resp.sendRedirect(url);
      } catch (Exception e) {
        throw new LoginException("An error occurred while attempting to redirect to the container.");
      }
    } else {
      // OAuth token has been returned, exchange it for an access token.
      String requestToken = null;
      try {
        requestToken = (String) req.getParameter("oauth_token");
        String requestSecret = (String) session.getAttribute("token_secret");
        Token rt = new Token(requestToken, requestSecret);
        Token at = OpenSocialOAuthClient.getAccessToken(client, provider, rt);

        accessToken = at.token;
        accessTokenSecret = at.secret;
        
        client.setProperty(OpenSocialClient.Property.ACCESS_TOKEN, accessToken);
        client.setProperty(OpenSocialClient.Property.ACCESS_TOKEN_SECRET, accessTokenSecret);
        
        session.setAttribute("access_token", accessToken);
        session.setAttribute("access_token_secret", accessTokenSecret);
        
        createOrLoadUser(session, container);
      } catch (Exception e) {
        resetLogin(req, resp);
        throw new LoginException("An error occurred while attempting to fetch the access token.");
      }
    }
  }

  /**
   * Creates a local user if none exists, or loads an existing user.
   * 
   * @param session The user's session
   * @param container The user's originating container
   * @throws IOException
   * @throws LoginException
   */
  @SuppressWarnings("unchecked")
  private void createOrLoadUser(HttpSession session, String container)
      throws IOException, LoginException {
    Long id = (Long) session.getAttribute("user_id");
    if (id == null) {
      OpenSocialPerson person;
      String personId;
      String displayName;
      User user = null;
      
      PersistenceManager pm = PMF.get().getPersistenceManager();
      try {
        person = client.fetchPerson();
        personId = person.getId();
        displayName = person.getDisplayName();
        String query = "SELECT FROM " + User.class.getName() + " WHERE " +
        "container == '" + container + "' && containerId == '" + personId + "' ";
        List<User> users = (List<User>) pm.newQuery(query).execute();
        
        if (users.isEmpty()) {
          user = new User(container, personId, displayName, "");
          pm.makePersistent(user);
        } else {
          user = users.get(0);
        }
      } catch (OpenSocialRequestException e) {
        throw new LoginException("Failed to create a local user.");
      } finally {
        pm.close();
      }
      
      session.setAttribute("user_id", user.getId());
    }
  }

  /**
   * Retrieves the currently selected container if specified.
   * 
   * @param req The incoming request (with access to the session)
   * @param session The user's session
   * @return the user's originating container
   */
  private String getContainer(HttpServletRequest req, HttpSession session) {
    String container = req.getParameter("container");
    if (container == null) {
      container = (String) session.getAttribute("container");
    }
    return container;
  }
  
  /**
   * Resets authenticated-related session variables.
   * 
   * @param req The incoming request (with access to the session)
   * @param resp The outgoing response
   * @throws IOException
   */
  public static void resetLogin(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    HttpSession session = req.getSession();
    session.removeAttribute("container");
    session.removeAttribute("authenticated");
    session.removeAttribute("consumer_key");
    session.removeAttribute("consumer_secret");
    session.removeAttribute("token_secret");
    session.removeAttribute("access_token");
    session.removeAttribute("access_token_secret");
    session.removeAttribute("user_id");
    resp.sendRedirect("/");
  }
}
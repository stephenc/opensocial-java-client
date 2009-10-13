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

package swt.client;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import javax.jdo.PersistenceManager;

import net.oauth.OAuthException;

import org.opensocial.client.OpenSocialClient;
import org.opensocial.client.OpenSocialOAuthClient;
import org.opensocial.client.OpenSocialProvider;
import org.opensocial.client.OpenSocialRequestException;
import org.opensocial.client.Token;
import org.opensocial.data.OpenSocialPerson;

import swt.model.GiftTransaction;
import swt.model.PMF;

import javax.jdo.Query;
import javax.mail.Session;
import javax.servlet.http.HttpSession;

/**
 * Utility bean that provides simple operations to fetch social data and is usable from a JSP.
 * 
 * @author cschalk@gmail.com (Chris Schalk)
 */

public class OSClientBean implements Serializable {  
  
  public OSClientBean()  { 
  }

  /**
   * Returns an OpenSocialClient object populated with person 
   * information for the current person who id authenticated and viewing 
   * the application.
   * This data is needed when querying the list of gifts this person has 
   * given.
   * 
   * @param session
   * @param requestToken
   * @return OpenSocialPerson
   */
  public OpenSocialPerson fetchPerson(HttpSession session, String requestToken){
    
    OpenSocialClient client = setUpOSClient(session, requestToken);
    OpenSocialPerson person = null;
    
    try {
      person = client.fetchPerson();
    } catch (OpenSocialRequestException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  
    return person;
  }

  /**
   * Fetches a Collection of friends of the current person who is authenticated and 
   * viewing the application.
   * 
   * @param session
   * @param requestToken
   * @return Collection<OpenSocialPerson>
   */
  public Collection<OpenSocialPerson> fetchFriends(HttpSession session, String requestToken){
    
    OpenSocialClient client = setUpOSClient(session, requestToken);
    OpenSocialPerson person = null;
    
    Collection<OpenSocialPerson> friends =  null;

    try {
      friends =  client.fetchFriends();
    } catch (OpenSocialRequestException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return friends;
  }

  
  
  /**
   * Sets up an OpenSocial client for use by extracting OAuth values extracted from the request/session.
   * @param session
   * @param requestToken
   * @return OpenSocialClient
   */
  public OpenSocialClient setUpOSClient(HttpSession session, String requestToken){
    
    if (requestToken != null) {
      //Store requestToken on session for future use
      session.setAttribute("oauth_token", requestToken);
    } else {
      //oauth_token not passed as parameter, so get from session if available
      requestToken = (String) session.getAttribute("oauth_token");
    }
    
    String requestSecret =  (String) session.getAttribute("token_secret");
    String provider_name = (String) session.getAttribute("provider_name");
    OpenSocialProvider provider = OpenSocialProvider.valueOf(provider_name); 


    String consumerKey = (String) session.getAttribute("consumerKey");
    String secretKey = (String) session.getAttribute("secretKey");

    
    // Before creating OS client, make sure all OAuth values are present
    if (requestToken==null || requestSecret==null || provider==null || 
        consumerKey==null || secretKey==null  ) {
      System.out.println("Error: OAuth values not set or available from session. ");
     return null;
    }
    
    OpenSocialClient client = new OpenSocialClient(provider);
    
    client.setProperty(OpenSocialClient.Property.CONSUMER_KEY, consumerKey);
    client.setProperty(OpenSocialClient.Property.CONSUMER_SECRET, secretKey);

    String accessToken = (String) session.getAttribute("access_token");
    String accessTokenSecret = (String) session.getAttribute("access_token_secret");
    
    if (accessToken==null || accessTokenSecret==null){
    // first time, so get request token, access token
      Token rt = new Token(requestToken, requestSecret);
      Token at = null;
      try {
        at = OpenSocialOAuthClient.getAccessToken(client, provider, rt);
      } catch (OAuthException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (URISyntaxException e) {
        e.printStackTrace();
      }

      accessToken = at.token;
      accessTokenSecret = at.secret;
    
      session.setAttribute("request_token", requestToken);
      session.setAttribute("access_token", accessToken);
      session.setAttribute("access_token_secret", accessTokenSecret);
    }
    
    client.setProperty(OpenSocialClient.Property.ACCESS_TOKEN, accessToken);
    client.setProperty(OpenSocialClient.Property.ACCESS_TOKEN_SECRET, accessTokenSecret);
    
    return client;
  }
  
  /**
   * getGiftsTransactions returns the set of gift transactions for the person
   * who is authenticated and viewing the application.
   * @param session
   * @param personDisplayName
   * @return List<GiftTransaction>
   */
  public List<GiftTransaction> getGiftsTransactions(HttpSession session, String personDisplayName){
    
    // Get gifts for current user using local AppEngine datastore
    PersistenceManager pm = PMF.get().getPersistenceManager();
    
    String uniqueFromPersonID = personDisplayName + "-" + session.getAttribute("provider_name");
    Query query = pm.newQuery(GiftTransaction.class);      
     query.setFilter("fromPersonId == '" + uniqueFromPersonID + "'");
    List<GiftTransaction> giftTrans = (List<GiftTransaction>) query.execute();
    return giftTrans;
  }
    
}

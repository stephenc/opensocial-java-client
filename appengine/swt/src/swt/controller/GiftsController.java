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

package swt.controller;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.opensocial.client.OpenSocialClient;
import org.opensocial.client.OpenSocialProvider;
import org.opensocial.client.OpenSocialRequestException;
import org.opensocial.data.OpenSocialPerson;

import swt.model.GiftTransaction;
import swt.model.PMF;

/**
 * Handles the operations for managing the gifts within the App Engine datastore. These are
 * 'processgift' and 'resetgifts'.
 * 
 * @author cschalk@gmail.com (Chris Schalk)
 */

@SuppressWarnings("serial")
public class GiftsController extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");

    // The only operation supported for GET method is resetting gifts
    String operation = req.getParameter("op");
    if (operation != null) {
      if (operation.equals("resetgifts")) {
        // Reset gifts data
        cleanDatabase();
        resp.sendRedirect("/main.jsp");
      }
    }
  }

  @SuppressWarnings("unused")
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
      IOException {
    resp.setContentType("text/plain");
    resp.getWriter().println("in post method");

    String operation = req.getParameter("op");

    if (operation != null) {
      if (operation.equals("processgift")) {
        // Process the act of giving a gift

        // Store gift transaction in App Engine DataStore
        resp.getWriter().println("processing a gift");

        // Get gift parameters
        String fromperson = req.getParameter("fromperson");
        String toperson = req.getParameter("person");
        String gift = req.getParameter("gift");

        GiftTransaction newGift = new GiftTransaction(fromperson, toperson, gift);

        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
          pm.makePersistent(newGift);
        } finally {
          pm.close();
        }

        // Post activity to container
        OpenSocialClient client = getOpenSocialContainerClient(req.getSession());
        OpenSocialPerson person;
        try {
          person = client.fetchPerson();
          client.createActivity("Just used the Gift Giving OpenSocial Tutorial App.", 
              person.getDisplayName() + " just gave a " + gift + " to " + toperson);
        } catch (OpenSocialRequestException e) {
          e.printStackTrace();
        }
      }
    }
    resp.sendRedirect("main.jsp");
  }

  
  /**
   * Returns an OpenSocialClient object based on the provider, and OAuth keys extracted 
   * from the session.
   * 
   * @param session
   * @return OpenSocialClient
   */
  OpenSocialClient getOpenSocialContainerClient(HttpSession session) {
    // get stored values on session and create OS client object.

    String provider_name = (String) session.getAttribute("provider_name");
    OpenSocialProvider provider = OpenSocialProvider.valueOf(provider_name);

    String consumerKey = (String) session.getAttribute("consumerKey");
    String secretKey = (String) session.getAttribute("secretKey");

    OpenSocialClient client = new OpenSocialClient(provider);

    client.setProperty(OpenSocialClient.Property.CONSUMER_KEY, consumerKey);
    client.setProperty(OpenSocialClient.Property.CONSUMER_SECRET, secretKey);

    String requestToken = (String) session.getAttribute("request_token");
    String accessToken = (String) session.getAttribute("access_token");
    String accessTokenSecret = (String) session.getAttribute("access_token_secret");

    client.setProperty(OpenSocialClient.Property.ACCESS_TOKEN, accessToken);
    client.setProperty(OpenSocialClient.Property.ACCESS_TOKEN_SECRET, accessTokenSecret);

    return client;
  }

  /**
   * Cleans the AppEngine DataStore objects: GiftTransaction, GiftItem.
   * 
   */
  @SuppressWarnings("unchecked")
  public void cleanDatabase() {

    PersistenceManager pm = PMF.get().getPersistenceManager();

    String query = "select from " + GiftTransaction.class.getName();
    List<GiftTransaction> giftTrans = (List<GiftTransaction>) pm.newQuery(query).execute();

    try {
      pm.deletePersistentAll(giftTrans);
    }

    finally {
      pm.close();
    }
    System.out.println("Cleaned database");
  }

}

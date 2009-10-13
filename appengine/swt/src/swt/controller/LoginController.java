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
import javax.servlet.http.*;

import org.opensocial.client.OpenSocialClient;
import org.opensocial.client.OpenSocialOAuthClient;
import org.opensocial.client.OpenSocialProvider;
import org.opensocial.client.Token;


/**
 * Handles the login operation for a specific set of OpenSocial containers and then redirects
 * to the user specified container for authentication.
 * 
 * @author cschalk@gmail.com (Chris Schalk)
 */

public class LoginController extends HttpServlet {
  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {
    resp.setContentType("text/plain");	

    HttpSession session = req.getSession();

    String consumerKey = "";
    String secretKey = "";
    OpenSocialProvider provider = null;

    String container = req.getParameter("container");

    if (container != null ){
      if (container.equals("myspace")){
        // proceed with myspace credentials			  
        consumerKey = "a86d9aa6aa5c4670a953aa0f48379613"; 
        secretKey = "6a9f1aa8df9149a9a8b1aecf8c385613fec5e5dd592f4f68b7250d1f6ce7d2c2"; 
        provider = OpenSocialProvider.valueOf("MYSPACE");
        session.setAttribute("provider_name", "MYSPACE");
      } else {
        if (container.equals("google")) {
          // proceed with google credentials
          consumerKey = "ostutorial.appspot.com"; 
          secretKey = "h1lPKHHzPvGPUW5HEFrbuh9t"; 
          provider = OpenSocialProvider.valueOf("GOOGLE");
          session.setAttribute("provider_name", "GOOGLE");
        } else {
          if (container.equals("partuza")){
            // proceed with partuza credentials
            consumerKey = "fac4ebd3-e7dc-c6f4-95ed-dbf6e7e1c8cb"; 
            secretKey = "92cacc4af4f7d9a299297464f34a1c76"; 
            provider = OpenSocialProvider.valueOf("PARTUZA");
            session.setAttribute("provider_name", "PARTUZA");
          } else {
            // no valid container specified
            resp.getWriter().println("Error: container: \"" + container + "\" not supported." );
            return;
          }
        }
      }							
    }

    session.setAttribute("consumerKey", consumerKey);
    session.setAttribute("secretKey", secretKey);

    final OpenSocialClient client = new OpenSocialClient(provider);

    client.setProperty(OpenSocialClient.Property.CONSUMER_SECRET, secretKey);
    client.setProperty(OpenSocialClient.Property.CONSUMER_KEY, consumerKey);

    Token requestToken = null;

    try {
      requestToken = OpenSocialOAuthClient.getRequestToken(client, provider);
      session.setAttribute("token_secret", requestToken.secret);
    } catch (Exception e) {
      resp.getWriter().println("<b>ERROR:</b> Failed to fetch request token: " + e.getClass());
      resp.getWriter().println("<pre>");
      e.printStackTrace(new java.io.PrintWriter(resp.getWriter()));
      resp.getWriter().println("</pre>");
      return;
    }

    String url = OpenSocialOAuthClient.getAuthorizationUrl(provider,
        requestToken, "http://ostutorial.appspot.com/main.jsp");

//For local debugging use the following...	  
//  String url = OpenSocialOAuthClient.getAuthorizationUrl(provider,
//    	   requestToken, "http://localhost:8080/main.jsp");

    resp.sendRedirect(url);		

  }
}

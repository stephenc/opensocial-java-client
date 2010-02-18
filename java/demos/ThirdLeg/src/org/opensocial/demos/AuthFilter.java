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

package org.opensocial.demos;

import org.opensocial.auth.OAuth3LeggedScheme;
import org.opensocial.providers.GoogleProvider;
import org.opensocial.providers.YahooProvider;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthFilter implements Filter {

  private static final String SCHEME_KEY = "scheme";

  private static final String GOOGLE_KEY = "anonymous";
  private static final String GOOGLE_SECRET = "anonymous";

  private static final String YAHOO_KEY =
    "dj0yJmk9bWpERnlGZVkyVkxMJmQ9WVdrOVNVMUJTelpoTlRBbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD0xMQ--";
  private static final String YAHOO_SECRET =
    "2f23aee8443ae302efcdae489fd7141023bb116c";

  public void init(FilterConfig config) throws ServletException {
  }

  public void doFilter(ServletRequest req, ServletResponse resp,
      FilterChain chain) throws IOException, ServletException {
    HttpServletResponse response = (HttpServletResponse) resp;
    HttpServletRequest request = (HttpServletRequest) req;
    HttpSession session = request.getSession();

    String verifierParam = request.getParameter("oauth_verifier");
    String tokenParam = request.getParameter("oauth_token");
    boolean authFlag = false;

    OAuth3LeggedScheme scheme = null;
    if (session != null) {
      scheme = (OAuth3LeggedScheme) session.getAttribute(SCHEME_KEY);
    }

    if (scheme == null) {
      authFlag = true;
    } else {
      if (scheme.getAccessToken() != null) {
        chain.doFilter(req, resp);
      } else if (scheme.getRequestToken() != null && tokenParam != null) {
        try {
          scheme.requestAccessToken(tokenParam, verifierParam);
          session.setAttribute(SCHEME_KEY, scheme);
          response.sendRedirect(request.getRequestURL().toString());
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        authFlag = true;
      }
    }

    if (authFlag) {
      scheme = new OAuth3LeggedScheme(new GoogleProvider(), GOOGLE_KEY,
          GOOGLE_SECRET);
      //scheme = new OAuth3LeggedScheme(new YahooProvider(
          //request.getRequestURL().toString()), YAHOO_KEY, YAHOO_SECRET);

      try {
        String authUrl =
          scheme.getAuthorizationUrl(request.getRequestURL().toString());
        session.setAttribute(SCHEME_KEY, scheme);
        response.sendRedirect(authUrl);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void destroy() {
  }
}

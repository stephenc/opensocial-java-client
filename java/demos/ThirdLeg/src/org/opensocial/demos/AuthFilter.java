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

  public void init(FilterConfig config) throws ServletException {
  }

  public void doFilter(ServletRequest req, ServletResponse resp,
      FilterChain chain) throws IOException, ServletException {
    HttpServletResponse response = (HttpServletResponse) resp;
    HttpServletRequest request = (HttpServletRequest) req;
    HttpSession session = request.getSession();

    OAuth3LeggedScheme authScheme = new OAuth3LeggedScheme(
        new GoogleProvider(), "anonymous", "anonymous");
    OAuth3LeggedScheme.Token requestToken =
      (OAuth3LeggedScheme.Token) session.getAttribute("rt");
    OAuth3LeggedScheme.Token accessToken =
      (OAuth3LeggedScheme.Token) session.getAttribute("at");

    String tokenParam = request.getParameter("oauth_token");

    if (accessToken != null) {
      chain.doFilter(req, resp);
    } else if (tokenParam != null && requestToken != null) {
      try {
        authScheme.setRequestToken(requestToken);
        authScheme.requestAccessToken(tokenParam);
        session.setAttribute("at", authScheme.getAccessToken());
        response.sendRedirect(request.getRequestURL().toString());
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      try {
        String authUrl =
          authScheme.getAuthorizationUrl(request.getRequestURL().toString());
        session.setAttribute("rt", authScheme.getRequestToken());
        response.sendRedirect(authUrl);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public void destroy() {
  }
}

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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.SimpleOAuthValidator;

public class OpenSocialRequestValidator {

  public static boolean verifyHmacSignature(HttpServletRequest request, String consumerSecret) throws IOException, URISyntaxException {
    String method = request.getMethod();
    String requestUrl = getRequestUrl(request);
    List<OAuth.Parameter> requestParameters = getRequestParameters(request);
    
    OAuthMessage message = new OAuthMessage(method, requestUrl, requestParameters);
    
    OAuthConsumer consumer = new OAuthConsumer(null, null, consumerSecret, null);
    OAuthAccessor accessor = new OAuthAccessor(consumer);

    try {
      message.validateMessage(accessor, new SimpleOAuthValidator());
    } catch (OAuthException e) {
      return false;
    }
    
    return true;
  }

  public static String getRequestUrl(HttpServletRequest request) {
    StringBuilder requestUrl = new StringBuilder();
    String scheme = request.getScheme();
    int port = request.getLocalPort();

    requestUrl.append(scheme);
    requestUrl.append("://");
    requestUrl.append(request.getServerName());
    if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
      requestUrl.append(":");
      requestUrl.append(port);
    }
    requestUrl.append(request.getContextPath());
    requestUrl.append(request.getServletPath());

    return requestUrl.toString();
  }

  public static List<OAuth.Parameter> getRequestParameters(HttpServletRequest request) {
    List<OAuth.Parameter> parameters = new ArrayList<OAuth.Parameter>();

    for (Object e : request.getParameterMap().entrySet()) {
      Map.Entry<String, String[]> entry = (Map.Entry<String, String[]>) e;
      
      for (String value : entry.getValue()) {
        parameters.add(new OAuth.Parameter(entry.getKey(), value));
      }
    }

    return parameters;
  }
}

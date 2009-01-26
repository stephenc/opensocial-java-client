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

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.SimpleOAuthValidator;
import net.oauth.signature.RSA_SHA1;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * A utility object containing static methods for verifying the signature of
 * requests signed by OpenSocial containers. All incoming requests should be
 * verified in case a malicious third party attempts to submit fraudulent
 * requests for user information.
 * 
 * @author Jason Cooper
 */
public class OpenSocialRequestValidator {

  /**
   * Validates the passed request by reconstructing the original URL and
   * parameters and generating a signature following the OAuth HMAC-SHA1
   * specification and using the passed secret key.
   * 
   * @param  request Servlet request containing required information for
   *         reconstructing the signature such as the request's URL
   *         components and parameters
   * @param  consumerSecret Secret key shared between application owner and
   *         container. Used by containers when issuing signed makeRequests
   *         and by client applications to verify the source of these
   *         requests and the authenticity of its parameters.
   * @return {@code true} if the signature generated in this function matches
   *         the signature in the passed request, {@code false} otherwise
   * @throws IOException
   * @throws URISyntaxException
   */
  public static boolean verifyHmacSignature(HttpServletRequest request, String consumerSecret)
      throws IOException, URISyntaxException {

    String method = request.getMethod();
    String requestUrl = getRequestUrl(request);
    List<OAuth.Parameter> requestParameters = getRequestParameters(request);

    OAuthMessage message = new OAuthMessage(method, requestUrl, requestParameters);

    OAuthConsumer consumer = new OAuthConsumer(null, null, consumerSecret, null);
    consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);

    OAuthAccessor accessor = new OAuthAccessor(consumer);

    try {
      message.validateMessage(accessor, new SimpleOAuthValidator());
    } catch (OAuthException e) {
      return false;
    }

    return true;
  }

  public static boolean verifyRsaSignature(HttpServletRequest request, String certificate)
      throws IOException, URISyntaxException {

    String method = request.getMethod();
    String requestUrl = getRequestUrl(request);
    List<OAuth.Parameter> requestParameters = getRequestParameters(request);

    OAuthMessage message = new OAuthMessage(method, requestUrl, requestParameters);

    OAuthConsumer consumer = new OAuthConsumer(null, null, null, null);
    consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.RSA_SHA1);
    consumer.setProperty(RSA_SHA1.X509_CERTIFICATE, certificate);

    OAuthAccessor accessor = new OAuthAccessor(consumer);

    try {
      message.validateMessage(accessor, new SimpleOAuthValidator());
    } catch (OAuthException e) {
      return false;
    }

    return true;
  }

  /**
   * Constructs and returns the full URL associated with the passed request
   * object.
   * 
   * @param  request Servlet request object with methods for retrieving the
   *         various components of the request URL
   */
  public static String getRequestUrl(HttpServletRequest request) {
    StringBuilder requestUrl = new StringBuilder();
    String scheme = request.getScheme();
    int port = request.getLocalPort();

    requestUrl.append(scheme);
    requestUrl.append("://");
    requestUrl.append(request.getServerName());

    if ((scheme.equals("http") && port != 80)
            || (scheme.equals("https") && port != 443)) {
      requestUrl.append(":");
      requestUrl.append(port);
    }

    requestUrl.append(request.getContextPath());
    requestUrl.append(request.getServletPath());

    return requestUrl.toString();
  }

  /**
   * Constructs and returns a List of OAuth.Parameter objects, one per
   * parameter in the passed request.
   * 
   * @param  request Servlet request object with methods for retrieving the
   *         full set of parameters passed with the request
   */
  public static List<OAuth.Parameter> getRequestParameters(
      HttpServletRequest request) {

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

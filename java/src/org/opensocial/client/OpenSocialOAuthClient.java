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
import net.oauth.OAuthServiceProvider;
import net.oauth.SimpleOAuthValidator;
import net.oauth.client.OAuthClient;
import net.oauth.http.HttpClient;
import net.oauth.signature.RSA_SHA1;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * A utility object containing various static methods for dealing with OAuth in
 * the context of OpenSocial. Included in these are static methods for
 * digitally signing, verifying the signature of requests signed by OpenSocial
 * containers, and retrieving access tokens from these containers.
 *
 * @author Jason Cooper, Cassandra Doll <doll@google.com>
 */
public class OpenSocialOAuthClient {

  private static OAuthClient oAuthClient;

  /**
   * Extracts pertinent properties from passed OpenSocialClient object and
   * passes these along with OpenSocialHttpRequest object to a separate
   * method which does the actual signing.
   *
   * @param  request OpenSocialHttpRequest object which contains both the URL
   *         to sign as well as the POST body which must be included as a
   *         parameter when signing POST requests
   * @param  client OpenSocialClient object with various properties, both
   *         optional and required, used during the signing process
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  public static void signRequest(
      OpenSocialHttpRequest request, OpenSocialClient client)
      throws OpenSocialRequestException, IOException {

    String token =
      client.getProperty(OpenSocialClient.Properties.TOKEN);
    String debug =
      client.getProperty(OpenSocialClient.Properties.DEBUG);
    String viewerId =
      client.getProperty(OpenSocialClient.Properties.VIEWER_ID);
    String consumerKey =
      client.getProperty(OpenSocialClient.Properties.CONSUMER_KEY);
    String consumerSecret =
      client.getProperty(OpenSocialClient.Properties.CONSUMER_SECRET);

    String accessToken =
      client.getProperty(OpenSocialClient.Properties.ACCESS_TOKEN);
    String accessTokenSecret =
      client.getProperty(OpenSocialClient.Properties.ACCESS_TOKEN_SECRET);

    String signBody = 
      client.getProperty(OpenSocialClient.Properties.SIGN_BODY);
    String appendBodyHash =
      client.getProperty(OpenSocialClient.Properties.APPEND_BODY_HASH);

    OpenSocialUrl requestUrl = request.getUrl();

    if (viewerId != null) {
      requestUrl.addQueryStringParameter("xoauth_requestor_id", viewerId);
    }
    if (token != null) {
      requestUrl.addQueryStringParameter("st", token);
    }

    String requestBody = request.getBody();
    String requestMethod = request.getMethod();

    if (consumerKey != null && consumerSecret != null) {
      OAuthMessage message =
          new OAuthMessage(requestMethod, requestUrl.toString(), null);
      
      OAuthConsumer consumer =
          new OAuthConsumer(null, consumerKey, consumerSecret, null);
      consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);

      OAuthAccessor accessor = new OAuthAccessor(consumer);
      if (accessToken != null) {
        accessor.accessToken = accessToken;
        accessor.tokenSecret = accessTokenSecret;
      } else {
        accessor.accessToken = "";
      }

      if (requestBody != null && signBody.equals("true")) {
        message.addParameter(requestBody, "");
      }

      if (requestBody != null && appendBodyHash.equals("true")) {
        try {
          MessageDigest md = MessageDigest.getInstance("SHA-1");

          byte[] hash = md.digest(requestBody.getBytes("UTF-8"));
          byte[] encodedHash = new Base64().encode(hash);

          message.addParameter("oauth_body_hash",
              new String(encodedHash, "UTF-8"));
        } catch (java.security.NoSuchAlgorithmException e) {
          // Ignore exception
        } catch (java.io.UnsupportedEncodingException e) {
          // Ignore exception
        }
      }

      try {
        message.addRequiredParameters(accessor);        
      } catch (OAuthException e) {
        throw new OpenSocialRequestException(
            "OAuth error thrown while signing request " + e.getMessage());
      } catch (java.net.URISyntaxException e) {
        throw new OpenSocialRequestException(
            "Malformed request URL " + message.URL + " could not be signed");
      }

      if (debug != null) {
        try {
          System.out.println("Signature base string:\n" +
              net.oauth.signature.OAuthSignatureMethod.getBaseString(message));
        } catch (URISyntaxException e) {
          // Ignore exception thrown
        }
      }

      for (Map.Entry<String, String> p : message.getParameters()) {
        if (!p.getKey().equals(requestBody)) {
          requestUrl.addQueryStringParameter(OAuth.percentEncode(p.getKey()),
              OAuth.percentEncode(p.getValue()));
        }
      }
    }
  }

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

  public static Token getRequestToken(OpenSocialClient client, OpenSocialProvider provider)
      throws IOException, URISyntaxException, OAuthException {

    if (provider.requestTokenUrl == null) {
      // Used for unregistered oauth
      return new Token("", "");
    }

    OAuthClient httpClient = getOAuthClient();
    OAuthAccessor accessor = new OAuthAccessor(getOAuthConsumer(client, provider));

    Set<Map.Entry<String,String>> extraParams = null;
    if (provider.requestTokenParams != null) {
      extraParams = provider.requestTokenParams.entrySet();
    }
    httpClient.getRequestToken(accessor, "GET", extraParams);

    return new Token(accessor.requestToken, accessor.tokenSecret);
  }

  public static String getAuthorizationUrl(OpenSocialProvider provider, Token requestToken,
      String callbackUrl) {
    if (requestToken.token == null || requestToken.token.equals("")) {
      // This is an unregistered oauth request
      return provider.authorizeUrl + "?oauth_callback=" + callbackUrl;
    }
    return provider.authorizeUrl + "?oauth_token=" + requestToken.token
        + "&oauth_callback=" + callbackUrl;
  }

  public static Token getAccessToken(OpenSocialClient client, OpenSocialProvider provider, Token requestToken)
      throws IOException, URISyntaxException, OAuthException {
    OAuthClient httpClient = getOAuthClient();
    OAuthAccessor accessor = new OAuthAccessor(getOAuthConsumer(client, provider));
    accessor.accessToken = requestToken.token;
    accessor.tokenSecret = requestToken.secret;

    OAuthMessage message = httpClient.invoke(accessor, "GET", provider.accessTokenUrl, null);
    return new Token(message.getToken(), message.getParameter("oauth_token_secret"));
  }

  private static OAuthConsumer getOAuthConsumer(OpenSocialClient client, OpenSocialProvider provider) {
    OAuthServiceProvider serviceProvider = new OAuthServiceProvider(provider.requestTokenUrl,
        provider.authorizeUrl, provider.accessTokenUrl);
    return new OAuthConsumer(null, client.getProperty(OpenSocialClient.Properties.CONSUMER_KEY),
        client.getProperty(OpenSocialClient.Properties.CONSUMER_SECRET), serviceProvider);
  }

  private static OAuthClient getOAuthClient() {
    if (oAuthClient == null) {
      final HttpClient httpClient = new OpenSocialHttpClient();

      oAuthClient = new OAuthClient(httpClient);
    }

    return oAuthClient;
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

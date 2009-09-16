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

package org.opensocial.client;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.http.HttpClient;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;

/**
 * A utility object containing various static methods for dealing with OAuth in
 * the context of OpenSocial. Included are static methods for digitally signing
 * requests and fetching/parsing tokens from OpenSocial containers.
 *
 * @author apijason@google.com (Jason Cooper)
 * @author doll@google.com (Cassandra Doll)
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
  public static void signRequest(OpenSocialHttpMessage request, 
      OpenSocialClient client)
      throws OpenSocialRequestException, IOException {
    String debug = client.getProperty(
        OpenSocialClient.Property.DEBUG);
    String viewerId = client.getProperty(
        OpenSocialClient.Property.VIEWER_ID);
    String consumerKey = client.getProperty(
        OpenSocialClient.Property.CONSUMER_KEY);
    String consumerSecret = client.getProperty(
        OpenSocialClient.Property.CONSUMER_SECRET);

    String token = client.getProperty(
        OpenSocialClient.Property.TOKEN);
    String tokenName = client.getProperty(
        OpenSocialClient.Property.TOKEN_NAME);

    String accessToken = client.getProperty(
        OpenSocialClient.Property.ACCESS_TOKEN);
    String accessTokenSecret = client.getProperty(
        OpenSocialClient.Property.ACCESS_TOKEN_SECRET);

    String signBodyHash = client.getProperty(
        OpenSocialClient.Property.SIGN_BODY_HASH);

    OpenSocialUrl requestUrl = request.getUrl();

    if (viewerId != null) {
      requestUrl.addQueryStringParameter("xoauth_requestor_id", viewerId);
    }

    if (tokenName == null) {
      tokenName = "st";
    }

    if (token != null) {
      requestUrl.addQueryStringParameter(tokenName, token);
    }

    String requestBody = request.getBodyString();
    String requestMethod = request.method;

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
      }
      
      if (requestBody != null) {
        if (signBodyHash.equals("true")) {
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
        } else if(request.getHeader(OpenSocialHttpMessage.CONTENT_TYPE)
            .equals("application/x-www-form-urlencoded")){
            message.addParameter(requestBody, "");
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

      if (debug.equals("true")) {
        try {
          System.out.println("Signature base string:\n" +
              net.oauth.signature.OAuthSignatureMethod.getBaseString(message));
        } catch (URISyntaxException e) {
          // Ignore exception
        }
      }

      for (Map.Entry<String, String> p : message.getParameters()) {
        if (!p.getKey().equals(requestBody)) {
          requestUrl.addQueryStringParameter(p.getKey(), p.getValue());
        }
      }
    }
  }

  public static Token getRequestToken(OpenSocialClient client,
      OpenSocialProvider provider) throws OAuthException, IOException,
      URISyntaxException {
    if (provider.requestTokenUrl == null) {
      // Used for unregistered oauth
      return new Token("", "");
    }

    OAuthClient httpClient = getOAuthClient();
    OAuthAccessor accessor =
      new OAuthAccessor(getOAuthConsumer(client, provider));

    Set<Map.Entry<String,String>> extraParams = null;
    if (provider.requestTokenParams != null) {
      extraParams = provider.requestTokenParams.entrySet();
    }
    httpClient.getRequestToken(accessor, "GET", extraParams);

    return new Token(accessor.requestToken, accessor.tokenSecret);
  }

  public static String getAuthorizationUrl(OpenSocialProvider provider, Token
      requestToken, String callbackUrl) {
    if (requestToken.token == null || requestToken.token.equals("")) {
      // This is an unregistered oauth request
      return provider.authorizeUrl + "?oauth_callback=" + callbackUrl;
    }
    return provider.authorizeUrl + "?oauth_token=" + requestToken.token
        + "&oauth_callback=" + callbackUrl;
  }

  public static Token getAccessToken(OpenSocialClient client,
      OpenSocialProvider provider, Token requestToken) throws OAuthException,
      IOException, URISyntaxException {
    OAuthAccessor accessor =
      new OAuthAccessor(getOAuthConsumer(client, provider));
    accessor.accessToken = requestToken.token;
    accessor.tokenSecret = requestToken.secret;

    OAuthClient httpClient = getOAuthClient();
    OAuthMessage message = httpClient.invoke(accessor, "GET",
        provider.accessTokenUrl, null);

    return new Token(message.getToken(),
        message.getParameter("oauth_token_secret"));
  }

  private static OAuthConsumer getOAuthConsumer(OpenSocialClient client,
      OpenSocialProvider provider) {
    OAuthServiceProvider serviceProvider =
      new OAuthServiceProvider(provider.requestTokenUrl, provider.authorizeUrl,
          provider.accessTokenUrl);

    return new OAuthConsumer(null,
        client.getProperty(OpenSocialClient.Property.CONSUMER_KEY),
        client.getProperty(OpenSocialClient.Property.CONSUMER_SECRET),
        serviceProvider);
  }

  private static OAuthClient getOAuthClient() {
    if (oAuthClient == null) {
      final HttpClient httpClient = new OpenSocialHttpClient();
      oAuthClient = new OAuthClient(httpClient);
    }

    return oAuthClient;
  }
}

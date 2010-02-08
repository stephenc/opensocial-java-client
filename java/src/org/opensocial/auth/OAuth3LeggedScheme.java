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

package org.opensocial.auth;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.http.HttpMessage;

import org.opensocial.RequestException;
import org.opensocial.http.HttpClient;
import org.opensocial.providers.Provider;

import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class OAuth3LeggedScheme extends OAuthScheme implements AuthScheme {

  public static class Token implements Serializable {
    public String token;
    public String secret;
    
    public Token(String token, String secret) {
      this.token = token;
      this.secret = secret;
    }
    
    public Token() {
    }
  }

  private Provider provider;
  private OAuthClient oAuthClient;
  private Token requestToken;
  private Token accessToken;

  public OAuth3LeggedScheme(Provider provider, String consumerKey,
      String consumerSecret) {
    this(provider, consumerKey, consumerSecret, new HttpClient());
  }

  public OAuth3LeggedScheme(Provider provider, String consumerKey,
      String consumerSecret, HttpClient httpClient) {
    super(consumerKey, consumerSecret);

    this.provider = provider;
    this.oAuthClient = new OAuthClient(httpClient);
  }

  public HttpMessage getHttpMessage(Provider provider, String method,
      String url, Map<String, String> headers, byte[] body) throws
      RequestException, IOException {
    return getHttpMessage(provider, method, url, headers, body, null);
  }

  public HttpMessage getHttpMessage(Provider provider, String method,
      String url, Map<String, String> headers, byte[] body,
      Collection<? extends Entry> parameters) throws
      RequestException, IOException {
    OAuthAccessor accessor = getOAuthAccessor(accessToken.token,
        accessToken.secret);
    OAuthMessage message = new OAuthMessage(method, url, parameters,
        byteArrayToStream(body));

    for (Map.Entry<String, String> header : headers.entrySet()) {
      message.getHeaders().add(header);
    }

    return getHttpMessage(message, accessor, body, provider.getSignBodyHash());
  }

  public String getAuthorizationUrl(String callbackUrl) throws OAuthException,
      URISyntaxException, IOException {
    requestToken = requestRequestToken();
    if (requestToken.token == null) {
      // This is an unregistered OAuth request
      return provider.getAuthorizeUrl() + "?oauth_callback=" + callbackUrl;
    }

    return provider.getAuthorizeUrl() + "?oauth_token=" + requestToken.token +
        "&oauth_callback=" + callbackUrl;
  }

  public void requestAccessToken(String oAuthToken) throws OAuthException,
      URISyntaxException, IOException {
    OAuthAccessor accessor = getOAuthAccessor(oAuthToken,
        this.requestToken.secret);
    OAuthMessage message = oAuthClient.invoke(accessor, "GET",
        provider.getAccessTokenUrl(), null);

    accessToken = new Token(message.getToken(),
        message.getParameter(OAuth.OAUTH_TOKEN_SECRET));
  }

  public Token getRequestToken() {
    return requestToken;
  }

  public Token getAccessToken() {
    return accessToken;
  }

  public void setRequestToken(Token token) {
    requestToken = token;
  }

  public void setAccessToken(Token token) {
    accessToken = token;
  }

  private Token requestRequestToken() throws OAuthException,
      URISyntaxException, IOException {
    if (provider.getRequestTokenUrl() == null) {
      return new Token();
    }

    Set<Map.Entry<String,String>> extraParams = null;
    if (provider.getRequestTokenParameters() != null) {
      extraParams = provider.getRequestTokenParameters().entrySet();
    }

    OAuthAccessor accessor = getOAuthAccessor();
    oAuthClient.getRequestToken(accessor, "GET", extraParams);

    return new Token(accessor.requestToken, accessor.tokenSecret);
  }

  private OAuthAccessor getOAuthAccessor() {
    OAuthServiceProvider serviceProvider = new OAuthServiceProvider(
        provider.getRequestTokenUrl(), provider.getAuthorizeUrl(),
        provider.getAccessTokenUrl());

    OAuthConsumer consumer = new OAuthConsumer(null, consumerKey,
        consumerSecret, serviceProvider);
    consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);

    return new OAuthAccessor(consumer);
  }

  private OAuthAccessor getOAuthAccessor(String token, String secret) {
    OAuthAccessor accessor = getOAuthAccessor();
    accessor.accessToken = token;
    accessor.tokenSecret = secret;

    return accessor;
  }
}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import net.oauth.OAuth;
import net.oauth.OAuthException;
import net.oauth.http.HttpMessage;

import org.junit.Test;
import org.opensocial.RequestException;
import org.opensocial.http.HttpClient;
import org.opensocial.http.HttpResponseMessage;
import org.opensocial.providers.MySpaceProvider;
import org.opensocial.providers.Provider;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OAuth3LeggedSchemeTest {

  private static class HttpClientStub extends HttpClient {

    private LinkedList<HttpResponseMessage> responses =
      new LinkedList<HttpResponseMessage>();
    private LinkedList<HttpMessage> requests =
      new LinkedList<HttpMessage>();

    @Override
    public HttpResponseMessage execute(HttpMessage message,
        Map<String, Object> parameters) {
      requests.addLast(message);

      return responses.removeFirst();
    }

    public void addResponse(HttpResponseMessage response) {
      responses.addLast(response);
    }

    public void addResponse(int statusCode, String response) throws
        IOException {
      addResponse(new HttpResponseMessage("GET", new URL("http://example.org"),
          statusCode, new ByteArrayInputStream(response.getBytes())));
    }

    public int getNumRequests() {
      return requests.size();
    }

    public HttpMessage getRequest() {
      return requests.removeFirst();
    }
  }

  private String consumerKey = "consumerKey";
  private String consumerSecret = "consumerSecret";
 
  @Test
  public void testGetAuthorizationUrl() throws OAuthException,
      URISyntaxException, IOException {
    HttpClientStub httpClient = new HttpClientStub();
    Provider provider = new MySpaceProvider();

    OAuth3LeggedScheme authScheme = new OAuth3LeggedScheme(provider,
        consumerKey, consumerSecret, httpClient);

    httpClient.addResponse(200, OAuth.OAUTH_TOKEN + "=OAUTH_TOKEN&" +
        OAuth.OAUTH_TOKEN_SECRET + "=OAUTH_TOKEN_SECRET");

    String authorizationUrl =
      authScheme.getAuthorizationUrl("http://www.example.org?token=test");

    assertEquals("http://api.myspace.com/authorize?oauth_token=OAUTH_TOKEN" +
        "&oauth_callback=http://www.example.org?token=test", authorizationUrl);
    assertEquals(1, httpClient.getNumRequests());

    HttpMessage request = httpClient.getRequest();

    assertEquals(null, request.getBody());
    assertTrue(request.url.toString().startsWith(
        "http://api.myspace.com/request_token"));
    assertTrue(request.url.toString().contains(
        "oauth_consumer_key=consumerKey"));
  }

  @Test
  public void testGetAuthorizationUrlUnregistered() throws OAuthException,
      URISyntaxException, IOException {
    HttpClientStub httpClient = new HttpClientStub();
    Provider provider = new MySpaceProvider() {
      @Override
      public String getRequestTokenUrl() {
        return null;
      }
    };

    OAuth3LeggedScheme authScheme = new OAuth3LeggedScheme(provider,
        consumerKey, consumerSecret, httpClient);
    String authorizationUrl =
      authScheme.getAuthorizationUrl("http://www.example.org?token=test");

    assertEquals("http://api.myspace.com/authorize" +
        "?oauth_callback=http://www.example.org?token=test", authorizationUrl);
  }

  @Test
  public void testGetAuthorizationUrlRequestTokenParams() throws
      OAuthException, URISyntaxException, IOException {
    HttpClientStub httpClient = new HttpClientStub();
    Provider provider = new MySpaceProvider();

    Map<String, String> requestTokenParams = new HashMap<String, String>();
    requestTokenParams.put("testKey", "testValue");
    provider.setRequestTokenParameters(requestTokenParams);

    OAuth3LeggedScheme authScheme = new OAuth3LeggedScheme(provider,
        consumerKey, consumerSecret, httpClient);

    httpClient.addResponse(200, OAuth.OAUTH_TOKEN + "=OAUTH_TOKEN&" +
        OAuth.OAUTH_TOKEN_SECRET + "=OAUTH_TOKEN_SECRET");

    String authorizationUrl = authScheme.getAuthorizationUrl(
        "http://www.example.org?token=test");

    assertEquals("http://api.myspace.com/authorize?oauth_token=OAUTH_TOKEN" +
        "&oauth_callback=http://www.example.org?token=test", authorizationUrl);
    assertEquals(1, httpClient.getNumRequests());

    HttpMessage request = httpClient.getRequest();

    assertEquals(null, request.getBody());
    assertTrue(request.url.toString().startsWith(
        "http://api.myspace.com/request_token"));
    assertTrue(request.url.toString().contains(
        "oauth_consumer_key=consumerKey"));
    assertTrue(request.url.toString().contains("testKey=testValue"));
  }

  @Test
  public void testRequestAccessToken() throws OAuthException,
      URISyntaxException, IOException {
    HttpClientStub httpClient = new HttpClientStub();
    Provider provider = new MySpaceProvider();

    OAuth3LeggedScheme authScheme = new OAuth3LeggedScheme(provider,
        consumerKey, consumerSecret, httpClient);

    httpClient.addResponse(200, OAuth.OAUTH_TOKEN + "=OAUTH_TOKEN&" +
        OAuth.OAUTH_TOKEN_SECRET + "=OAUTH_TOKEN_SECRET");

    httpClient.addResponse(200, OAuth.OAUTH_TOKEN_SECRET +
        "=ACCESS_TOKEN_SECRET&" + OAuth.OAUTH_TOKEN + "=ACCESS_TOKEN");

    authScheme.getAuthorizationUrl("http://www.example.org?token=test");
    authScheme.requestAccessToken("oAuthRequestToken");
    OAuth3LeggedScheme.Token token = authScheme.getAccessToken();

    assertEquals("ACCESS_TOKEN_SECRET", token.secret);
    assertEquals("ACCESS_TOKEN", token.token);
  }

  @Test
  public void testGetHttpMessage() throws RequestException, IOException {
    Provider provider = new MySpaceProvider();

    OAuth3LeggedScheme authScheme = new OAuth3LeggedScheme(provider,
        consumerKey, consumerSecret);
    authScheme.setAccessToken(new OAuth3LeggedScheme.Token("ACCESS_TOKEN",
        "ACCESS_TOKEN_SECRET"));

    String url = "http://example.org/test?arg=value";
    Map<String, String> headers = new HashMap<String, String>();

    List<Map.Entry<String, String>> parameters =
      new ArrayList<Map.Entry<String, String>>();
    parameters.add(new OAuth.Parameter(OAuth.OAUTH_TIMESTAMP, "123456789"));
    parameters.add(new OAuth.Parameter(OAuth.OAUTH_NONCE, "987654321"));

    HttpMessage message = authScheme.getHttpMessage(provider, "GET", url,
        headers, null, parameters);

    assertEquals("http://example.org/test?arg=value" +
        "&oauth_timestamp=123456789&oauth_nonce=987654321" +
        "&oauth_token=ACCESS_TOKEN&oauth_consumer_key=consumerKey" +
        "&oauth_signature_method=HMAC-SHA1&oauth_version=1.0" +
        "&oauth_signature=uiz7qEAnFsKWTSgfBXppS%2Br%2BmQg%3D",
        message.url.toString());
  }

  @Test
  public void testGetHttpMessageSignBodyHash() throws RequestException,
      IOException {
    Provider provider = new MySpaceProvider() {
      @Override
      public boolean getSignBodyHash() {
        return true;
      }
    };

    OAuth3LeggedScheme authScheme = new OAuth3LeggedScheme(provider,
        consumerKey, consumerSecret);
    authScheme.setAccessToken(new OAuth3LeggedScheme.Token("ACCESS_TOKEN",
        "ACCESS_TOKEN_SECRET"));

    String url = "http://example.org/test?arg=value";

    Map<String, String> headers = new HashMap<String, String>();
    headers.put(HttpMessage.CONTENT_TYPE, "application/json");

    List<Map.Entry<String, String>> parameters =
      new ArrayList<Map.Entry<String, String>>();
    parameters.add(new OAuth.Parameter(OAuth.OAUTH_TIMESTAMP, "123456789"));
    parameters.add(new OAuth.Parameter(OAuth.OAUTH_NONCE, "987654321"));
    
    HttpMessage message = authScheme.getHttpMessage(provider, "POST", url,
        headers, "{a:\"test\"}", parameters);

    assertEquals("http://example.org/test?arg=value" +
        "&oauth_timestamp=123456789&oauth_nonce=987654321" +
        "&oauth_body_hash=PkyA2Pf44ldLEq%2BZYMo7g6uH3UE%3D" +
        "&oauth_token=ACCESS_TOKEN&oauth_consumer_key=consumerKey" +
        "&oauth_signature_method=HMAC-SHA1&oauth_version=1.0&" +
        "oauth_signature=zdRixuj4wcLHvY6%2BT8u6%2FWGNuZI%3D",
        message.url.toString());

    assertEquals("{a:\"test\"}", new BufferedReader(
        new InputStreamReader(message.getBody())).readLine());
  }
}

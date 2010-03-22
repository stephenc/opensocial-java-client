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

package org.opensocial;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.or;
import static org.junit.Assert.*;

import net.oauth.http.HttpMessage;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.junit.Test;
import org.opensocial.auth.AuthScheme;
import org.opensocial.auth.OAuth2LeggedScheme;
import org.opensocial.http.HttpClient;
import org.opensocial.http.HttpResponseMessage;
import org.opensocial.providers.OrkutProvider;
import org.opensocial.providers.Provider;
import org.opensocial.services.PeopleService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ClientTest {

  private static final String VIEWER_ID = "03067092798963641994";
  private static final String CONSUMER_KEY = "orkut.com:623061448914";
  private static final String CONSUMER_SECRET = "uynAeXiWTisflWX99KU1D2q5";

  @Test(expected = RequestException.class)
  public void testEmptyRequestQueue() throws RequestException, IOException {
    Client client = new Client(new OrkutProvider(), new OAuth2LeggedScheme(
        CONSUMER_KEY, CONSUMER_SECRET, VIEWER_ID));

    Map<String, Request> requests = new HashMap<String, Request>();
    client.send(requests);
  }

  @Test(expected = RequestException.class)
  public void testNoEndpointsSet() throws RequestException, IOException {
    Client client = new Client(new Provider(), new OAuth2LeggedScheme(
        CONSUMER_KEY, CONSUMER_SECRET, VIEWER_ID));

    Request request = PeopleService.getViewer();
    client.send(request);
  }

  private InputStream stringToInputStream(String str) {
    return new ByteArrayInputStream(str.getBytes());
  }

  @Test
  public void testSubmitRpcContentType() throws RequestException, IOException {
    IMocksControl mockControl = EasyMock.createControl();

    HttpClient httpClient = mockControl.createMock(HttpClient.class);
    AuthScheme authScheme = mockControl.createMock(AuthScheme.class);

    OrkutProvider provider = new OrkutProvider();

    Client client = new Client(provider, authScheme, httpClient);

    Request request = new Request(null, "service.method", null);
    request.setContentType("test/content-type");

    String rpcEndPoint = provider.getRpcEndpoint();
    rpcEndPoint = rpcEndPoint.substring(0, rpcEndPoint.length() - 1);

    HttpResponseMessage httpResponseMessage = new HttpResponseMessage("GET",
        new URL(provider.getRpcEndpoint()), 200, stringToInputStream("[]"));

    EasyMock.expect(authScheme.getHttpMessage(eq(provider), eq("POST"),
        eq(rpcEndPoint), isA(Map.class), isA(byte[].class)))
        .andAnswer(new IAnswer<HttpMessage>() {
          public HttpMessage answer() throws Throwable {
            Map<String, String> requestHeaders =
              (Map<String, String>) EasyMock.getCurrentArguments()[3];
            assertEquals("test/content-type",
                requestHeaders.get(HttpMessage.CONTENT_TYPE));
            return null;
          }
        });

    EasyMock.expect(httpClient.execute(
        (HttpMessage) eq(null))).andReturn(httpResponseMessage);

    mockControl.replay();

    client.send(request);

    mockControl.verify();
  }

  @Test
  public void testBuildRpcPayloadCustomPayload() throws RequestException,
  IOException {
    IMocksControl mockControl = EasyMock.createControl();

    HttpClient httpClient = mockControl.createMock(HttpClient.class);
    AuthScheme authScheme = mockControl.createMock(AuthScheme.class);

    OrkutProvider provider = new OrkutProvider();

    Client client = new Client(provider, authScheme, httpClient);

    Request request = new Request(null, "service.method", null);
    byte[] payload = "testpayload".getBytes();
    request.setCustomPayload(payload);

    String rpcEndPoint = provider.getRpcEndpoint();
    rpcEndPoint = rpcEndPoint.substring(0, rpcEndPoint.length() - 1);

    HttpResponseMessage httpResponseMessage = new HttpResponseMessage("GET",
        new URL(provider.getRpcEndpoint()), 200, stringToInputStream("[]"));

    EasyMock.expect(authScheme.getHttpMessage(eq(provider), eq("POST"),
        eq(rpcEndPoint), isA(Map.class), eq(payload))).andReturn(null);

    EasyMock.expect(httpClient.execute((HttpMessage) eq(null))).andReturn(
        httpResponseMessage);

    mockControl.replay();

    client.send(request);

    mockControl.verify();
  }

  @Test
  public void testBuildRpcUrl() throws RequestException, IOException {
    IMocksControl mockControl = EasyMock.createControl();

    HttpClient httpClient = mockControl.createMock(HttpClient.class);
    AuthScheme authScheme = mockControl.createMock(AuthScheme.class);

    OrkutProvider provider = new OrkutProvider();

    Client client = new Client(provider, authScheme, httpClient);

    Request request = new Request(null, null, null);
    request.addRpcQueryStringParameter("key1", "value1");
    request.addRpcQueryStringParameter("key2", "value2");

    String rpcUrl = provider.getRpcEndpoint();
    rpcUrl = rpcUrl.substring(0, rpcUrl.length() - 1);
    String rpcUrl1 = rpcUrl + "?key1=value1&key2=value2";
    String rpcUrl2 = rpcUrl + "?key2=value2&key1=value1";

    EasyMock.expect(authScheme.getHttpMessage(eq(provider), eq("POST"), 
        or(eq(rpcUrl1), eq(rpcUrl2)), isA(Map.class), isA(byte[].class)))
        .andReturn(null);

    HttpResponseMessage httpResponseMessage = new HttpResponseMessage("GET",
        new URL(provider.getRpcEndpoint()), 200, stringToInputStream("[]"));

    EasyMock.expect(httpClient.execute((HttpMessage) eq(null))).andReturn(
        httpResponseMessage);

    mockControl.replay();

    client.send(request);

    mockControl.verify();
  }
}

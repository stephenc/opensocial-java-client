/* Copyright (c) 2010 Google Inc.
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

import net.oauth.http.HttpMessage;

import org.opensocial.auth.SecurityTokenScheme;
import org.opensocial.models.Model;
import org.opensocial.providers.Provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

/**
 * Abstract parent class for BaseRequestTest.
 *
 * @author Guibin Kong
 *
 */
public abstract class AbstractRequestTest {

  protected static Client client;
  protected Request request;

  protected HttpMessage message;
  protected boolean isRpc;
  protected String messageBody;

  protected void clear() {
    request = null;
    message = null;
    messageBody = null;
    isRpc = false;
  }

  protected void assertRequestValid(Class<? extends Model> modelClass,
      String rpcMethod, String restMethod) {
    StringBuilder msg = new StringBuilder();

    if (modelClass != null) {
      msg.append("Request check failed. Expected model class <");
      msg.append(modelClass).append(">, actual <");
      msg.append(request.getModelClass()).append(">.");
      Assert.assertEquals(msg.toString(), modelClass, request.getModelClass());
    }

    msg.setLength(0);
    msg.append("Request check failed. Expected rpc method <");
    msg.append(request.getRpcMethod()).append(">, actual <");
    msg.append(request.getRpcMethod()).append(">.");
    Assert.assertEquals(msg.toString(), rpcMethod, request.getRpcMethod());

    msg.setLength(0);
    msg.append("Request check failed. Expected rest method <");
    msg.append(request.getRestMethod()).append(">, actual <");
    msg.append(request.getRestMethod()).append(">.");
    Assert.assertEquals(msg.toString(), restMethod, request.getRestMethod());
  }

  protected void assertRequestComponent(String expected, String key) {
    String actual = request.getComponent(key);

    StringBuilder msg = new StringBuilder();
    msg.append("Request component check failed. Expected value for <");
    msg.append(key).append("> is <").append(expected);
    msg.append(">, actual <").append(actual).append(">.");
    Assert.assertEquals(msg.toString(), expected, actual);
  }

  protected void assertMessageNotNull() {
    Assert.assertNotNull(message);
  }

  protected void assertMsgBodyHas(String... parameters) {
    Assert.assertNotNull("RPC body is null.", messageBody);

    String msg = "RPC body check failed. Missing expected parameter: ";
    for (String param : parameters) {
      Assert.assertTrue(msg + param, messageBody.contains(param));
    }
  }

  protected void assertMsgBodyHasEither(String data1, String data2) {
    Assert.assertTrue(messageBody.contains(data1)
        || messageBody.contains(data2));
  }

  protected void assertRestUrlHas(String url, String... parameters) {
    String msg = "Rest URL check failed. Missing expected parameter: ";
    for (String param : parameters) {
      Assert.assertTrue(msg + param, url.contains(param));
    }
  }

  protected void assertRestUrlValid(String httpMethod, String restUrl,
      String... parameters) {
    Assert.assertEquals(httpMethod, message.method);
    String expect = getEndPointUrl() + restUrl;
    expect = expect.endsWith("/") ? expect.substring(0, expect.length() - 1)
        : expect;

    String url = message.url.toExternalForm();
    int pos = url.indexOf('?');
    String actual = (pos < 0) ? url: url.substring(0, pos);
    actual = actual.endsWith("/") ? actual.substring(0, actual.length() - 1)
        : actual;

    StringBuilder msg = new StringBuilder();
    msg.append("Restful URL check failed. Expected <").append(expect);
    msg.append(">, actually <").append(actual).append(">");
    Assert.assertTrue(msg.toString(), actual.equals(expect));

    assertRestUrlHas(url, parameters);
  }

  protected String getEndPointUrl() {
    String endpointUrl;
    if (isRpc) {
      endpointUrl = client.getProvider().getRpcEndpoint();
    } else {
      endpointUrl = client.getProvider().getRestEndpoint();
    }
    if (endpointUrl.endsWith("/")) {
      endpointUrl = endpointUrl.substring(0, endpointUrl.length() - 1);
    }
    return endpointUrl;
  }

  protected void assertHasSecurityToken() {
    if (client.getAuthScheme() instanceof SecurityTokenScheme) {
      assertRestUrlHas(message.url.toExternalForm(), "st=securityToken");
    }
  }

  protected void assertMessageValid() {
    assertMessageNotNull();
    Assert.assertNotNull(message.url);

    String endpointUrl = getEndPointUrl();
    String url = message.url.toExternalForm();
    Assert.assertTrue(url + " not start with " + endpointUrl,
        url.startsWith(endpointUrl));

    assertHasSecurityToken();
  }

  // TODO: call corresponding functions in Client class directly.
  protected void toHttpMessage() throws RequestException, IOException {
    Provider provider = client.getProvider();
    if (provider.getRpcEndpoint() != null) {
      isRpc = true;

      String method = "POST";

      String url = client.buildRpcUrl();

      Map<String, String> headers = new HashMap<String, String>();
      headers.put(HttpMessage.CONTENT_TYPE, provider.getContentType());

      final String KEY = "key";
      Map<String, Request> requests = new HashMap<String, Request>();
      requests.put(KEY, request);

      byte[] body = client.buildRpcPayload(requests);

      message = client.getAuthScheme().getHttpMessage(provider, method, url,
          headers, body);
    } else if (provider.getRestEndpoint() != null) {
      isRpc = false;

      Map<String, String> headers = new HashMap<String, String>();
      if (request.getContentType() != null) {
        headers.put(HttpMessage.CONTENT_TYPE, request.getContentType());
      } else {
        headers.put(HttpMessage.CONTENT_TYPE, provider.getContentType());
      }

      String method = request.getRestMethod();

      String url = client.buildRestUrl(request);

      byte[] body = client.buildRestPayload(request);

      message = client.getAuthScheme().getHttpMessage(provider, method, url,
          headers, body);
    } else {
      throw new RequestException("Provider has no REST or RPC endpoint set");
    }

    parseBody();
  }

  protected void parseBody() {
    if (message != null) {
      try {
        InputStream in = message.getBody();
        if (in != null) {
          String line = null;
          StringBuilder builder = new StringBuilder();
          BufferedReader reader = new BufferedReader(new InputStreamReader(in));

          while ((line = reader.readLine()) != null) {
            builder.append(line);
          }

          messageBody = builder.toString();
          in.close();
        }
      } catch (IOException e) {
      }
    }
  }
}

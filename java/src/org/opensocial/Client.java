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

import net.oauth.http.HttpMessage;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opensocial.auth.AuthScheme;
import org.opensocial.http.HttpClient;
import org.opensocial.http.HttpResponseMessage;
import org.opensocial.providers.Provider;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class Client {

  private Provider provider;
  private AuthScheme authScheme;
  private HttpClient httpClient;

  public Client(Provider provider, AuthScheme authScheme) {
    this.provider = provider;
    this.authScheme = authScheme;
    this.httpClient = new HttpClient();
  }

  public Provider getProvider() {
    return provider;
  }

  public AuthScheme getAuthScheme() {
    return authScheme;
  }

  public Response send(Request request) throws RequestException, IOException {
    final String KEY = "key";

    Map<String, Request> requests = new HashMap<String, Request>();
    requests.put(KEY, request);

    Map<String, Response> responses = send(requests);

    return responses.get(KEY);
  }

  public Map<String, Response> send(Map<String, Request> requests) throws
      RequestException, IOException {
    if (requests.size() == 0) {
      throw new RequestException("Request queue is empty");
    }

    Map<String, Response> responses = new HashMap<String, Response>();

    if (provider.getRpcEndpoint() != null) {
      responses = submitRpc(requests);
    } else if (provider.getRestEndpoint() != null) {
      for (Map.Entry<String, Request> entry : requests.entrySet()) {
        Request request = entry.getValue();

        provider.preRequest(request);

        Response response = submitRestRequest(request);
        responses.put(entry.getKey(), response);

        provider.postRequest(request, response);
      }
    } else {
      throw new RequestException("Provider has no REST or RPC endpoint set");
    }

    return responses;
  }

  private Map<String, Response> submitRpc(Map<String, Request> requests) throws
      RequestException, IOException {
    Map<String, String> requestHeaders = new HashMap<String, String>();
    requestHeaders.put(HttpMessage.CONTENT_TYPE, provider.getContentType());

    HttpMessage message = authScheme.getHttpMessage(provider, "POST",
        buildRpcUrl(), requestHeaders, buildRpcPayload(requests));

    HttpResponseMessage responseMessage = httpClient.execute(message);

    System.out.println("Request URL: " + responseMessage.getUrl().toString());
    System.out.println("Request body: " + buildRpcPayload(requests));
    System.out.println("Status code: " + responseMessage.getStatusCode());
    System.out.println("Response: " + responseMessage.getResponse());

    Map<String, Response> responses = Response.parseRpcResponse(requests,
        responseMessage, provider.getVersion());

    return responses;
  }

  private Response submitRestRequest(Request request) throws RequestException,
      IOException{
    Map<String, String> requestHeaders = new HashMap<String, String>();
    requestHeaders.put(HttpMessage.CONTENT_TYPE, provider.getContentType());

    HttpMessage message = authScheme.getHttpMessage(provider,
        request.getRestMethod(), buildRestUrl(request), requestHeaders,
        buildRestPayload(request));

    HttpResponseMessage responseMessage = httpClient.execute(message);

    System.out.println("Request method: " + responseMessage.getMethod());
    System.out.println("Request URL: " + responseMessage.getUrl().toString());
    System.out.println("Request body: " + buildRestPayload(request));
    System.out.println("Status code: " + responseMessage.getStatusCode());
    System.out.println("Response: " + responseMessage.getResponse());

    Response response = Response.parseRestResponse(request, responseMessage,
        provider.getVersion());

    return response;
  }

  private String buildRpcUrl() {
    StringBuilder builder = new StringBuilder(provider.getRpcEndpoint());

    // Remove trailing forward slash
    if (builder.charAt(builder.length() - 1) == '/') {
      builder.deleteCharAt(builder.length() - 1);
    }

    return builder.toString();
  }

  private String buildRpcPayload(Map<String, Request> requests) {
    JSONArray requestArray = new JSONArray();
    for (Map.Entry<String, Request> requestEntry : requests.entrySet()) {
      JSONObject request = new JSONObject();
      request.put("id", requestEntry.getKey());
      request.put("method", requestEntry.getValue().getRpcMethod());

      JSONObject requestParams = new JSONObject();
      if (requestEntry.getValue().getGuid() != null) {
        requestParams.put("userId", requestEntry.getValue().getGuid());
      }
      if (requestEntry.getValue().getSelector() != null) {
        requestParams.put("groupId", requestEntry.getValue().getSelector());
      }
      if (requestEntry.getValue().getAppId() != null) {
        requestParams.put("appId", requestEntry.getValue().getAppId());
      }

      for (Map.Entry<String, Object> parameter :
          requestEntry.getValue().getRpcPayloadParameters().entrySet()) {
        requestParams.put(parameter.getKey(), parameter.getValue());
      }

      request.put("params", requestParams);
      requestArray.add(request);
    }

    return requestArray.toJSONString();
  }

  private String buildRestUrl(Request request) {
    StringBuilder builder = new StringBuilder(provider.getRestEndpoint());
    String[] components = request.getTemplate().split("/");

    for (String component : components) {
      if (component.startsWith("{") && component.endsWith("}")) {
        String tag = component.substring(1, component.length()-1);

        if (tag.equals("guid") && request.getGuid() != null) {
          builder.append(request.getGuid());
          builder.append("/");
        } else if (tag.equals("selector") && request.getSelector() != null) {
          builder.append(request.getSelector());
          builder.append("/");
        } else if (tag.equals("appid") && request.getAppId() != null) {
          builder.append(request.getAppId());
          builder.append("/");
        }
      } else {
        builder.append(component);
        builder.append("/");
      }
    }

    // Remove trailing forward slash
    builder.deleteCharAt(builder.length() - 1);

    // Append query string parameters
    Map<String, String> parameters = request.getRestQueryStringParameters();
    if (parameters != null && parameters.size() > 0) {
      boolean runOnce = false;

      for (Map.Entry<String, String> parameter: parameters.entrySet()) {
        if (!runOnce) {
          builder.append("?");
          runOnce = true;
        } else {
          builder.append("&");
        }

        try {
          builder.append(URLEncoder.encode(parameter.getKey(), "UTF-8"));
          builder.append("=");
          builder.append(URLEncoder.encode(parameter.getValue(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
          // Ignore
        }
      }
    }

    return builder.toString();
  }

  private String buildRestPayload(Request request) {
    Map<String, Object> parameters = request.getRestPayloadParameters();

    if (parameters == null || parameters.size() == 0) {
      return null;
    }

    JSONObject payload = new JSONObject();
    for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
      payload.put(parameter.getKey(), parameter.getValue());
    }

    return payload.toJSONString();
  }
}

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
import org.opensocial.auth.OAuth2LeggedScheme;
import org.opensocial.http.HttpClient;
import org.opensocial.http.HttpResponseMessage;
import org.opensocial.providers.OrkutProvider;
import org.opensocial.providers.Provider;
import org.opensocial.services.PeopleService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * OpenSocial RESTful client supporting both the RPC and REST protocols defined
 * in the OpenSocial specification as well as two- and three-legged OAuth for
 * authentication. This class handles the transmission of requests to
 * OpenSocial containers such as orkut and MySpace. Typical usage:
 * <pre>
 *   Client client = new Client(new OrkutProvider(),
         new OAuth2LeggedScheme(ORKUT_KEY, ORKUT_SECRET, ORKUT_ID));
     Response response = client.send(PeopleService.getViewer());
 * </pre>
 * The send method either returns a single {@link Response} or a {@link Map} of
 * Response objects mapped to ID strings. The data returned from the container
 * can be extracted from these objects.
 *
 * @author Jason Cooper
 */
public class Client {

  private Provider provider;
  private AuthScheme authScheme;
  private HttpClient httpClient;

  /**
   * Creates and returns a new {@link Client} associated with the passed
   * {@link Provider} and {@link AuthScheme}.
   *
   * @param provider   {@link Provider} to associate with new {@link Client}
   * @param authScheme {@link AuthScheme} to associate with new {@link Client}
   */
  public Client(Provider provider, AuthScheme authScheme) {
    this.provider = provider;
    this.authScheme = authScheme;
    this.httpClient = new HttpClient();
  }

  /**
   * Returns the associated {@link Provider}.
   */
  public Provider getProvider() {
    return provider;
  }

  /**
   * Returns the associated {@link AuthScheme}.
   */
  public AuthScheme getAuthScheme() {
    return authScheme;
  }

  /**
   * Submits the passed {@link Request} to the associated {@link Provider} and
   * returns the container's response data as a {@link Response} object.
   *
   * @param  request Request object (typically returned from static methods in
   *                 service classes) encapsulating all request data including
   *                 endpoint, HTTP method, and any required parameters
   * @return         Response object encapsulating the response data returned
   *                 by the container
   *
   * @throws RequestException if the passed request cannot be serialized, the
   *                          container returns an error code, or the response
   *                          cannot be parsed
   * @throws IOException      if an I/O error prevents a connection from being
   *                          opened or otherwise causes request transmission
   *                          to fail
   */
  public Response send(Request request) throws RequestException, IOException {
    final String KEY = "key";

    Map<String, Request> requests = new HashMap<String, Request>();
    requests.put(KEY, request);

    Map<String, Response> responses = send(requests);

    return responses.get(KEY);
  }

  /**
   * Submits the passed {@link Map} of {@link Request}s to the associated
   * {@link Provider} and returns the container's response data as a Map of
   * {@link Response} objects mapped to the same IDs as the passed requests. If
   * the associated provider supports the OpenSocial RPC protocol, only one
   * HTTP request is sent; otherwise, one HTTP request is executed per
   * container request.
   *
   * @param  requests Map of Request objects (typically returned from static
   *                  methods in service classes) to ID strings; each object
   *                  encapsulates the data for a single container request
   *                  such as fetching the viewer or creating an activity.
   * @return          Map of Response objects, each encapsulating the response
   *                  data returned by the container for a single request, to
   *                  the associated ID strings in the passed Map of Request
   *                  objects
   *
   * @throws RequestException if the passed request cannot be serialized, the
   *                          container returns an error code, or the response
   *                          cannot be parsed
   * @throws IOException      if an I/O error prevents a connection from being
   *                          opened or otherwise causes request transmission
   *                          to fail
   */
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
    if (request.getCustomContentType() != null) {
      requestHeaders.put(HttpMessage.CONTENT_TYPE,
          request.getCustomContentType());
    } else {
      requestHeaders.put(HttpMessage.CONTENT_TYPE, provider.getContentType());
    }

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
      if (requestEntry.getValue().getAppId() != null) {
        requestParams.put("appId", requestEntry.getValue().getAppId());
      }
      if (requestEntry.getValue().getGuid() != null) {
        requestParams.put("userId", requestEntry.getValue().getGuid());
      }
      if (requestEntry.getValue().getGroupId() != null) {
        requestParams.put("groupId", requestEntry.getValue().getGroupId());
      }
      if (requestEntry.getValue().getSelector() != null) {
        requestParams.put("groupId", requestEntry.getValue().getSelector());
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

        if (tag.equals("appid") && request.getAppId() != null) {
          builder.append(request.getAppId());
          builder.append("/");
        } else if (tag.equals("guid") && request.getGuid() != null) {
          builder.append(request.getGuid());
          builder.append("/");
        } else if (tag.equals("moodId") && request.getMoodId() != null) {
          builder.append(request.getMoodId());
          builder.append("/");
        } else if (tag.equals("itemId") && request.getItemId() != null) {
          builder.append(request.getItemId());
          builder.append("/");
        } else if (tag.equals("albumId") && request.getAlbumId() != null) {
          builder.append(request.getAlbumId());
          builder.append("/");
        } else if (tag.equals("groupId") && request.getGroupId() != null) {
          builder.append(request.getGroupId());
          builder.append("/");
        } else if (tag.equals("selector") && request.getSelector() != null) {
          builder.append(request.getSelector());
          builder.append("/");
        } else if (tag.equals("friendId") && request.getFriendId() != null) {
          builder.append(request.getFriendId());
          builder.append("/");
        } else if (tag.equals("history") && request.getHistory()) {
          builder.append("history/");
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
    if (request.getRawPayload() != null) {
      return request.getRawPayload();
    }

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

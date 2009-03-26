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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An object representing a collection of OpenSocial requests. Instances may be
 * instantiated at the top level; individual requests are created via static
 * methods of OpenSocialClient and added to the batch request with the add
 * method. The requests are then submitted to the container with the send
 * method.
 *
 * @author apijason@google.com (Jason Cooper)
 */
public class OpenSocialBatch {

  private List<OpenSocialRequest> requests;
  private OpenSocialHttpClient httpClient;

  public OpenSocialBatch() {
    this.requests = new ArrayList<OpenSocialRequest>();
    this.httpClient = new OpenSocialHttpClient();
  }

  /**
   * Sets the id property of the passed request and adds it to the internal
   * collection.
   *
   * @param request OpenSocialRequest object to add to internal collection
   * @param id Request label, used to extract data from OpenSocialResponse
   *        object returned
   */
  public void addRequest(OpenSocialRequest request, String id) {
    request.setId(id);
    this.requests.add(request);
  }

  /**
   * Calls one of two private methods which submit the queued requests to the
   * container given the properties of the OpenSocialClient object passed in.
   *
   * @param  client OpenSocialClient object with REST_BASE_URI or RPC_ENDPOINT
   *                properties set
   * @return Object encapsulating the data requested from the container
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  public OpenSocialResponse send(OpenSocialClient client) throws
      OpenSocialRequestException, IOException {

    if (this.requests.size() == 0) {
      throw new OpenSocialRequestException(
          "One or more requests must be added before sending batch");
    }

    String rpcEndpoint =
      client.getProperty(OpenSocialClient.Property.RPC_ENDPOINT);
    String restBaseUri =
      client.getProperty(OpenSocialClient.Property.REST_BASE_URI);

    String urlRegEx = "([A-Za-z][A-Za-z0-9+.-]{1,120}:[A-Za-z0-9/]" +
        "(([A-Za-z0-9$_.+!*,;/?:@&~=-])|%[A-Fa-f0-9]{2}){1,333}" + 
        "(#([a-zA-Z0-9][a-zA-Z0-9$_.+!*,;/?:@&~=%-]{0,1000}))?)";

    if (rpcEndpoint == null && restBaseUri == null) {
      throw new OpenSocialRequestException(
          "REST base URI or RPC endpoint required");
    } else if (rpcEndpoint == null) {
      if (!restBaseUri.matches(urlRegEx)) {
        throw new OpenSocialRequestException(
            "REST base URI must be a valid URL");
      }

      return this.submitRest(client);
    } else {
      if (!rpcEndpoint.matches(urlRegEx)) {
        throw new OpenSocialRequestException(
            "RPC endpoint must be a valid URL");
      }

      return this.submitRpc(client);
    }
  }

  /**
   * Collects all of the queued requests and encodes them into a single JSON
   * string before creating a new HTTP request, attaching this string to the
   * request body, signing it, and sending it to the container.
   *
   * @param  client OpenSocialClient object with RPC_ENDPOINT property set
   * @return Object encapsulating the data requested from the container
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  private OpenSocialResponse submitRpc(OpenSocialClient client) throws
      OpenSocialRequestException, IOException {

    String rpcEndpoint =
      client.getProperty(OpenSocialClient.Property.RPC_ENDPOINT);
    String contentType =
      client.getProperty(OpenSocialClient.Property.CONTENT_TYPE);

    JSONArray requestArray = new JSONArray();
    for (OpenSocialRequest r : this.requests) {
      try {
        requestArray.put(new JSONObject(r.toJson()));
      } catch (org.json.JSONException e) {
        throw new OpenSocialRequestException(
            "Invalid JSON object string " + r.toJson());
      }
    }

    OpenSocialUrl requestUrl = new OpenSocialUrl(rpcEndpoint);

    OpenSocialHttpMessage request = new OpenSocialHttpMessage("POST",
        requestUrl, requestArray.toString());
    request.addHeader(OpenSocialHttpMessage.CONTENT_TYPE, contentType);

    OpenSocialOAuthClient.signRequest(request, client);

    OpenSocialHttpResponseMessage response = httpClient.execute(request);
    String responseString = response.getBodyString();

    String debug = client.getProperty(OpenSocialClient.Property.DEBUG);
    if (debug != null && !debug.equals("")) {
      System.out.println("Request URL:\n" + request.getUrl().toString());
      System.out.println("Request body:\n" + request.getBodyString());
      System.out.println("Container response:\n" + responseString);
    }

    return OpenSocialJsonParser.getResponse(responseString);
  }

  /**
   * Retrieves the first request in the queue and builds the full REST URI
   * given the properties of this request before creating a new HTTP request,
   * signing it, and sending it to the container.
   *
   * @param  client OpenSocialClient object with REST_BASE_URI property set
   * @return Object encapsulating the data requested from the container
   * @throws OpenSocialRequestException
   * @throws IOException
   */
  private OpenSocialResponse submitRest(OpenSocialClient client) throws
      OpenSocialRequestException, IOException {

    String restBaseUri =
      client.getProperty(OpenSocialClient.Property.REST_BASE_URI);
    String contentType =
      client.getProperty(OpenSocialClient.Property.CONTENT_TYPE);

    OpenSocialRequest r = this.requests.get(0);

    OpenSocialUrl requestUrl = new OpenSocialUrl(restBaseUri);
    requestUrl.addPathComponent(r.getRestPathComponent());

    if (r.hasParameter("userId")) {
      requestUrl.addPathComponent((String) r.popParameter("userId"));
    }
    if (r.hasParameter("groupId")) {
      requestUrl.addPathComponent((String) r.popParameter("groupId"));
    }
    if (r.hasParameter("appId")) {
      requestUrl.addPathComponent((String) r.popParameter("appId"));
    }

    String requestBody = null;
    if (r.hasParameter("data")) {
      requestBody = (String) r.popParameter("data");
    }

    Set<Map.Entry<String, OpenSocialRequestParameter>> parameters =
      r.getParameters();
    for (Map.Entry<String, OpenSocialRequestParameter> entry : parameters) {
      requestUrl.addQueryStringParameter(entry.getKey(),
          entry.getValue().getValuesString());
    }

    OpenSocialHttpMessage request = new OpenSocialHttpMessage(
        r.getRestMethod(), requestUrl, requestBody);
    request.addHeader(OpenSocialHttpMessage.CONTENT_TYPE, contentType);

    OpenSocialOAuthClient.signRequest(request, client);

    OpenSocialHttpResponseMessage response = httpClient.execute(request);
    String responseString = response.getBodyString();

    String debug = client.getProperty(OpenSocialClient.Property.DEBUG);
    if (debug != null && !debug.equals("")) {
      System.out.println("Request URL:\n" + request.getUrl().toString());
      System.out.println("Request body:\n" + request.getBodyString());
      System.out.println("Container response:\n" + responseString);
    }

    return OpenSocialJsonParser.getResponse(responseString, r.getId());
  }
}

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

import net.oauth.OAuthException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

/**
 * An object representing a collection of OpenSocial requests. Instances may be
 * instantiated at the top level; individual requests are created via static
 * methods of OpenSocialClient and added to the batch request with the add
 * method. The requests are then submitted to the container with the send
 * method.
 *
 * @author Jason Cooper
 */
public class OpenSocialBatch {

  private List<OpenSocialRequest> requests;

  public OpenSocialBatch() {
    this.requests = new Vector<OpenSocialRequest>();
  }

  /**
   * Sets the id property of the passed request and adds it to the internal
   * collection.
   *
   * @param request OpenSocialRequest object to add to internal collection
   * @param id Request label, used to extract data from OpenSocialResponse
   *           object returned
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
   * @throws JSONException
   * @throws OAuthException
   * @throws IOException
   * @throws URISyntaxException
   */
  public OpenSocialResponse send(OpenSocialClient client)
      throws OpenSocialRequestException, JSONException, OAuthException,
             IOException, URISyntaxException {

    if (this.requests.size() == 0) {
      throw new OpenSocialRequestException(
          "One or more requests must be added before sending batch");
    }

    String rpcEndpoint =
      client.getProperty(OpenSocialClient.Properties.RPC_ENDPOINT);
    String restBaseUri =
      client.getProperty(OpenSocialClient.Properties.REST_BASE_URI);

    if (rpcEndpoint == null && restBaseUri == null) {
      throw new OpenSocialRequestException(
          "REST base URI or RPC endpoint required");
    }

    if (rpcEndpoint != null) {
      return this.submitRpc(client);
    } else {
      return this.submitRest(client);
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
   * @throws JSONException
   * @throws OAuthException
   * @throws IOException
   * @throws URISyntaxException
   */
  private OpenSocialResponse submitRpc(OpenSocialClient client)
      throws OpenSocialRequestException, JSONException, OAuthException,
             IOException, URISyntaxException {

    String rpcEndpoint =
      client.getProperty(OpenSocialClient.Properties.RPC_ENDPOINT);

    JSONArray requestArray = new JSONArray();
    for (OpenSocialRequest r : this.requests) {
      requestArray.put(new JSONObject(r.getJsonEncoding()));
    }

    OpenSocialUrl requestUrl = new OpenSocialUrl(rpcEndpoint);

    OpenSocialHttpRequest request = new OpenSocialHttpRequest(requestUrl);
    request.setPostBody(requestArray.toString());

    OpenSocialRequestSigner.signRequest(request, client);

    String responseString = getHttpResponse(request);
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
   * @throws JSONException
   * @throws OAuthException
   * @throws IOException
   * @throws URISyntaxException
   */
  private OpenSocialResponse submitRest(OpenSocialClient client)
      throws OpenSocialRequestException, JSONException, OAuthException,
             IOException, URISyntaxException {

    String restBaseUri =
      client.getProperty(OpenSocialClient.Properties.REST_BASE_URI);

    OpenSocialRequest r = this.requests.get(0);

    OpenSocialUrl requestUrl = new OpenSocialUrl(restBaseUri);

    requestUrl.addPathComponent(r.getRestPathComponent());
    if (r.getParameter("userId") != null) {
      requestUrl.addPathComponent(r.getParameter("userId"));
    }
    if (r.getParameter("groupId") != null) {
      requestUrl.addPathComponent(r.getParameter("groupId"));
    }
    if (r.getParameter("appId") != null) {
      requestUrl.addPathComponent(r.getParameter("appId"));
    }

    OpenSocialHttpRequest request = new OpenSocialHttpRequest(requestUrl);

    OpenSocialRequestSigner.signRequest(request, client);

    String responseString = getHttpResponse(request);
    return OpenSocialJsonParser.getResponse(responseString, r.getId());
  }

  /**
   * Returns the text returned by the container after executing the passed
   * OpenSocialHttpRequest object.
   *
   * @param  r OpenSocialHttpRequest object to execute
   * @return
   * @throws OpenSocialRequestException if the status code returned by the
   *         container is not 200 OK.
   * @throws IOException
   */
  private String getHttpResponse(OpenSocialHttpRequest r)
      throws OpenSocialRequestException, IOException {

    if (r != null) {
      int requestStatus = r.execute();

      if (requestStatus == 200) {
        return r.getResponseString();
      } else {
        throw new OpenSocialRequestException(
            "Request returned error code: " + requestStatus + " for url: " + r.getUrl());
      }
    }

    return null;
  }
}

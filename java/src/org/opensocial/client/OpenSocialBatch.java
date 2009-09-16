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
import java.util.HashMap;
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
 * @author jle.edwards@gmail.com (Jesse Edwards)
 */
public class OpenSocialBatch {

  private List<OpenSocialRequest> requests;
  private Map<String, OpenSocialHttpResponseMessage> responses;
  private OpenSocialHttpClient httpClient;
  private OpenSocialClient client;

  public OpenSocialBatch() {
    this.requests = new ArrayList<OpenSocialRequest>();
    this.responses = new HashMap<String, OpenSocialHttpResponseMessage>();
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

  public OpenSocialHttpResponseMessage getResponse(String id) {
    if(this.responses.containsKey(id)){
      return responses.get(id);
    }
    return null;
  }

  public Set<String> getResponseQueue() {
     return responses.keySet();
  }

  private void addResponse(OpenSocialHttpResponseMessage value, String id) {
      this.responses.put(id, value);
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
  public OpenSocialResponse send(OpenSocialClient client) 
      throws OpenSocialRequestException, IOException {
    // Prevent the need to pass client to submitREST & submitRPC
    this.client = client;
    OpenSocialResponse response = new OpenSocialResponse();
    
    if (this.requests.size() == 0) {
        throw new OpenSocialRequestException("One or more requests must be " +
          "added before sending batch");
    }

    if(client.getProvider().rpcEndpoint != null) {
      response = submitRpc();
    }else if(client.getProvider().restEndpoint != null) {
      for (OpenSocialRequest r : this.requests) {
        // Pre Request
        client.getProvider().preRequest(r);
        // Make Request
        String restResponse = submitRest(r);
        // Post Request
        client.getProvider().postRequest(r, restResponse);
        // Add to response queue
        response.addItem(r.getId(), restResponse);
      }
    }else {
      throw new OpenSocialRequestException(
          "REST base URI or RPC endpoint required");
    }
    return response;
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
  private OpenSocialResponse submitRpc() 
    throws OpenSocialRequestException, IOException {
    
    String rpcEndpoint = client.getProvider().rpcEndpoint;
    String contentType = client.getProvider().contentType;

    JSONArray requestArray = new JSONArray();
    for (OpenSocialRequest r : this.requests) {
      try {
        requestArray.put(new JSONObject(r.toJson()));
      } catch (org.json.JSONException e) {
        throw new OpenSocialRequestException("Invalid JSON object string " 
            + r.toJson());
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
  private String submitRest(OpenSocialRequest r) 
    throws OpenSocialRequestException, IOException {
    //TODO: content-type should be determined by what we are dealing with 
    // in the request. Not the client
    String contentType = client.getProperty(
        OpenSocialClient.Property.CONTENT_TYPE);
    String requestBody = null;
    OpenSocialUrl requestUrl = _createUrlForRequest(r);
    
    // Get parameters that remain ater we applied the template.
    Set<Map.Entry<String, OpenSocialRequestParameter>> parameters = 
      r.getParameters();
    
    String exemptKey;
    
    if(requestUrl.getPostAliases().containsKey(r.getRestPathComponent())) {
      exemptKey = requestUrl.getPostAliases().get(r.getRestPathComponent());
    }else{
      exemptKey = "";
    }
    
    // If we are uploading. We need to get the contentType of what we are 
    //uploading. As with the PHP library. Pull this from the parameters.
    if(r.hasParameter("contentType")){
      contentType = r.popParameter("contentType");
    }
    
    // Loop through each parameter 
    for (Map.Entry<String, OpenSocialRequestParameter> entry : parameters) {
    	//If it is a postBody variable add it to postBody, 
      // else add to querystring
    	if(exemptKey.equals(entry.getKey())) {
    		requestBody = (String) entry.getValue().getValuesString();
    	}else {
    		requestUrl.addQueryStringParameter(entry.getKey(), 
    		    entry.getValue().getValuesString());
    	}
    }
    
    // This is done after looping through hasmap due to issues with
    // removing items form a hasmap while looping through it.
    if(r.hasParameter(exemptKey))
      r.popParameter(exemptKey);

    OpenSocialHttpMessage request = new OpenSocialHttpMessage(
        r.getRestMethod(), requestUrl, requestBody);
    request.addHeader(OpenSocialHttpMessage.CONTENT_TYPE, contentType);

    OpenSocialOAuthClient.signRequest(request, client);

    OpenSocialHttpResponseMessage response = httpClient.execute(request);
    
    addResponse(response, r.getId());
    
    String responseString = response.getBodyString();

    String debug = client.getProperty(OpenSocialClient.Property.DEBUG);
    if (debug != null && !debug.equals("")) {
      System.out.println("Request URL:\n" + request.getUrl().toString());
      System.out.println("Request body:\n" + request.getBodyString());
      System.out.println("Container response:\n" + responseString);
    }

    return responseString;
  }
  
  private OpenSocialUrl _createUrlForRequest(OpenSocialRequest r) {
    String service = r.getRestPathComponent();
    OpenSocialUrl requestUrl = new OpenSocialUrl(
        client.getProvider().restEndpoint, service);
    String[] urlParts = requestUrl.getUrlTemplate(service).split("/");
  
    // Construct URL based on template
    for(int i=0; i<urlParts.length; i++) {
      String p = urlParts[i];

      if(p.startsWith("{") &&  p.endsWith("}")) {
        String tag = p.substring(1, p.length()-1);
        if(r.hasParameter(tag)) {
          requestUrl.addPathComponent(r.popParameter(tag));
        }
      }
    }
  
    return requestUrl;
  }
}

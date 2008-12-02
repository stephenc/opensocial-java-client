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

import java.io.IOException;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.oauth.OAuthException;

public class OpenSocialBatch {

  private List<OpenSocialRequest> requests;

  public OpenSocialBatch() {    
    this.requests = new Vector<OpenSocialRequest>();
  }

  public void addRequest(OpenSocialRequest request, String id) {
    request.setId(id);  
    this.requests.add(request);
  }

  public OpenSocialResponse submit(OpenSocialClient client) throws IOException, JSONException, OAuthException, OpenSocialRequestException, URISyntaxException  {

    if (this.requests.size() == 0) {
      throw new OpenSocialRequestException("");
    }

    String rpcEndpoint =
      client.getProperty(OpenSocialClient.Properties.RPC_ENDPOINT);
    String restBaseUri =
      client.getProperty(OpenSocialClient.Properties.REST_BASE_URI);

    if (rpcEndpoint == null && restBaseUri == null) {
      throw new OpenSocialRequestException("REST base URI or RPC endpoint required");
    }

    if (rpcEndpoint != null) {
      return this.submitRpc(client);
    } else {
      return this.submitRest(client);
    }
  }

  private OpenSocialResponse submitRpc(OpenSocialClient client) throws JSONException, OAuthException, IOException, URISyntaxException, OpenSocialRequestException {

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

  private OpenSocialResponse submitRest(OpenSocialClient client) throws OAuthException, IOException, URISyntaxException, OpenSocialRequestException, JSONException {

    String restBaseUri =
      client.getProperty(OpenSocialClient.Properties.REST_BASE_URI);

    OpenSocialRequest r = this.requests.get(0);

    OpenSocialUrl requestUrl = new OpenSocialUrl(restBaseUri);
    requestUrl.addStandardRestPathComponents(r);

    OpenSocialHttpRequest request = new OpenSocialHttpRequest(requestUrl);

    OpenSocialRequestSigner.signRequest(request, client);
    System.out.println(request.getUrl().toString());
    
    String responseString = getHttpResponse(request);

    return OpenSocialJsonParser.getResponse(responseString, r.getId());
  }

  private String getHttpResponse(OpenSocialHttpRequest c) throws IOException, OpenSocialRequestException {
    if (c != null) {
      int requestStatus = c.execute();

      if (requestStatus == 200) {
        return c.getResponseString();
      } else {
        throw new OpenSocialRequestException("Request returned error code: " + requestStatus);
      }
    }

    return null;
  }
}

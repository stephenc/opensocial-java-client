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

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * An internal object which represents a single HTTP request and contains a
 * minimal set of methods for setting request properties, submitting the
 * request, and retrieving the response which can later be parsed into a
 * more meaningful object.
 *
 * @author Jason Cooper
 */
public class OpenSocialHttpRequest {

  private OpenSocialUrl url;
  private HttpRequestBase request;
  
  public OpenSocialHttpRequest(String method, OpenSocialUrl url) {
    this.url = url;

    if (method.equals("GET")) {
      this.request = new HttpGet();
    } else if (method.equals("PUT")) {
      this.request = new HttpPut();
    } else if (method.equals("POST")) {
      this.request = new HttpPost();
    } else if (method.equals("DELETE")) {
      this.request = new HttpDelete();
    }
  }

  public String execute() throws OpenSocialRequestException, IOException {
    HttpClient httpClient = new DefaultHttpClient();
    httpClient.getParams().setBooleanParameter( "http.protocol.expect-continue", false );
    ResponseHandler<String> responseHandler = new BasicResponseHandler();

    try {
      this.request.setURI(this.url.toUri());
      return httpClient.execute(this.request, responseHandler);
    } catch (java.net.URISyntaxException e) {
      throw new OpenSocialRequestException("Malformed URL: " + this.url.toString());
    }    
  }

  public void setBody(String body) {
    String requestMethod = this.request.getMethod();
    
    if (requestMethod.equals("PUT") || requestMethod.equals("POST")) {
      HttpEntityEnclosingRequestBase entityRequest =
        (HttpEntityEnclosingRequestBase) this.request;

      try {
        StringEntity requestEntity = new StringEntity(body, "UTF-8");
        requestEntity.setContentType("application/x-www-form-urlencoded");
        entityRequest.setEntity(requestEntity);        
      } catch (java.io.UnsupportedEncodingException e) {
      }
    }
  }

  public String getBody() throws IOException {
    String requestMethod = this.request.getMethod();

    if (requestMethod.equals("PUT") || requestMethod.equals("POST")) {
      HttpEntityEnclosingRequestBase entityRequest =
        (HttpEntityEnclosingRequestBase) this.request;
      HttpEntity bodyEntity = entityRequest.getEntity();
    
      StringBuilder sb = new StringBuilder();
      BufferedReader reader = new BufferedReader(
        new InputStreamReader(bodyEntity.getContent()));

      String line = null;
      while ((line = reader.readLine()) != null) {
        sb.append(line);
      }

      return sb.toString();
    } else {
      return null;
    }
  }

  public OpenSocialUrl getUrl() {
    return this.url;
  }

  public String getMethod() {
    return this.request.getMethod();
  }
}

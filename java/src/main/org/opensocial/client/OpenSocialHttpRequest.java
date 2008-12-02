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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenSocialHttpRequest {

  private String method;
  private String postBody;
  private OpenSocialUrl url;
  private HttpURLConnection connection;

  public OpenSocialHttpRequest(OpenSocialUrl url) throws IOException {
    this.method = "GET";
    this.postBody = null;
    this.connection = null;    
    this.setUrl(url);
  }

  public int execute() throws IOException, OpenSocialRequestException {
    if (this.connection == null) {
      this.connection = this.getConnection();
    }
    
    this.connection.setRequestMethod(this.method);
    
    if (this.postBody == null) {
      this.connection.connect();
    } else {
      this.connection.setDoOutput(true);

      OutputStreamWriter wr = new OutputStreamWriter(
          this.connection.getOutputStream());
      wr.write(this.postBody);
      wr.flush();
      wr.close();
    }
    
    return this.connection.getResponseCode();
  }
  
  public InputStream getResponseStream() throws IOException {
    return this.connection.getInputStream();
  }
  
  /**
   * Transforms response output contained in the connection's InputStream
   * object into a string representation which can later be parsed into a
   * more meaningful object (e.g. OpenSocialObject, OpenSocialPerson). 
   *
   * @throws IOException if the open connection's input stream is not
   *         retrievable or accessible
   */
  public String getResponseString() throws IOException {
    InputStream responseStream = this.getResponseStream();

    StringBuilder sb = new StringBuilder();
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(responseStream));

    String line = null;
    while ((line = reader.readLine()) != null) {
      sb.append(line);
    }
    
    responseStream.close();

    return sb.toString();
  }

  public String getMethod() {
    return this.method;
  }
  
  public String getPostBody() {
    return this.postBody;
  }
  
  public OpenSocialUrl getUrl() {
    return this.url;
  }
  
  public void setMethod(String method) {
    this.method = method;
  }
  
  public void setPostBody(String postBody) {
    this.postBody = postBody;
    this.setMethod("POST");
  }
  
  private void setUrl(OpenSocialUrl requestUrl) throws IOException {
    this.url = requestUrl;
  }

  private HttpURLConnection getConnection() throws IOException {
    URL url = new URL(this.url.toString());

    if (!url.getProtocol().startsWith("http")) {
      throw new IOException("Unsupported scheme:" + url.getProtocol());
    }

    return ((HttpURLConnection) url.openConnection());
  }
}

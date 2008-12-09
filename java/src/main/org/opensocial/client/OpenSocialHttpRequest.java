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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An internal object which represents a single HTTP request and contains a
 * minimal set of methods for setting request properties, submitting the
 * request, and retrieving the response which can later be parsed into a
 * more meaningful object.
 *
 * @author Jason Cooper
 */
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

  /**
   * Initializes connection if necessary before establishing the connection,
   * including writing POST data to the connection's output stream if
   * applicable.
   * 
   * @return HTTP status code of request, e.g. 200, 401, 500, etc.
   * @throws IOException
   */
  public int execute() throws IOException {
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

  /**
   * After executing the request, returns the server response as an InputStream
   * object.
   * 
   * @throws IOException
   */
  public InputStream getResponseStream() throws IOException {
    return this.connection.getInputStream();
  }

  /**
   * After executing the request, transforms response output contained in the
   * connection's InputStream object into a string representation which can
   * later be parsed into a more meaningful object, e.g. OpenSocialObject. 
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

  /**
   * Returns instance variable: method.
   */
  public String getMethod() {
    return this.method;
  }

  /**
   * Returns instance variable: postBody.
   */
  public String getPostBody() {
    return this.postBody;
  }

  /**
   * Returns instance variable: url.
   */
  public OpenSocialUrl getUrl() {
    return this.url;
  }

  /**
   * Sets instance variable method to passed String.
   */
  public void setMethod(String method) {
    this.method = method;
  }

  /**
   * Sets instance variable postBody to passed String and automatically sets
   * request method to POST.
   */
  public void setPostBody(String postBody) {
    this.postBody = postBody;
    this.setMethod("POST");
  }

  /**
   * Sets instance variable url to passed OpenSocialUrl object.
   * 
   * @param requestUrl
   * @throws IOException
   */
  private void setUrl(OpenSocialUrl requestUrl) throws IOException {
    this.url = requestUrl;
  }

  /**
   * Opens a new HTTP connection for the URL associated with this object.
   * 
   * @return Opened connection
   * @throws IOException if URL protocol is not http
   */
  private HttpURLConnection getConnection() throws IOException {
    URL url = new URL(this.url.toString());

    if (!url.getProtocol().startsWith("http")) {
      throw new IOException("Unsupported scheme:" + url.getProtocol());
    }

    return ((HttpURLConnection) url.openConnection());
  }
}

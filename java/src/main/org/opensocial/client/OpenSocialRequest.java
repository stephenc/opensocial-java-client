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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * An internal object responsible for constructing HTTP requests, opening
 * connections to specified endpoints, and returning the raw response
 * strings which can later be parsed into more meaningful objects.
 *
 * @author Jason Cooper
 */
public class OpenSocialRequest {
  private JSONObject postData;
  private HttpURLConnection connection;

  public OpenSocialRequest(URL url) throws IOException {
    this.postData = null;
    this.connection = getRequestConnection(url);
  }

  public OpenSocialRequest(
      URL url, String methodName, Map<String,String> parameters)
      throws IOException, JSONException {

    this.postData = new JSONObject();
    this.postData.put("method", methodName);
    this.postData.put("params", new JSONObject(parameters));

    this.connection = getRequestConnection(url);
  }

  /**
   * Opens connection to RESTful or JSON-RPC endpoint and writes POST data to
   * connection's output stream if necessary.
   * 
   * @throws IOException if the connection to the specified URL can't be
   *         opened or if there are errors accessing or writing to the
   *         that connection's output stream
   * @throws OpenSocialRequestException 
   */
  private void execute() throws IOException, OpenSocialRequestException {
    if (this.postData == null) {
      this.connection.connect();
    } else {
      this.connection.setRequestMethod("POST");
      this.connection.setDoOutput(true);

      OutputStreamWriter wr = new OutputStreamWriter(
          this.connection.getOutputStream());
      wr.write(this.postData.toString());
      wr.flush();
      wr.close();
    }

    if (this.connection.getResponseCode() >= 300) {
      StringBuilder sb = new StringBuilder();
      
      sb.append("Request returned ");
      sb.append(this.connection.getResponseCode());
      sb.append(" status: ");
      sb.append(this.connection.getResponseMessage());
      
      throw new OpenSocialRequestException(sb.toString());
    }
  }

  public InputStream getResponseStream() throws IOException, OpenSocialRequestException {
    this.execute();

    return this.connection.getInputStream();
  }

  /**
   * Transforms response output contained in the connection's InputStream
   * object into a string representation which can later be parsed into a
   * more meaningful object (e.g. OpenSocialObject, OpenSocialPerson). 
   * @return
   * @throws IOException if the open connection's input stream is not
   *         retrievable or accessible
   * @throws OpenSocialRequestException 
   */
  public String getResponseString() throws IOException, OpenSocialRequestException {
    InputStream responseStream = getResponseStream();

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

  private HttpURLConnection getRequestConnection(URL url) throws IOException {
    if (!url.getProtocol().startsWith("http")) {
      throw new UnsupportedOperationException(
          "Unsupported scheme:" + url.getProtocol());
    }

    return (HttpURLConnection) url.openConnection();
  }
}

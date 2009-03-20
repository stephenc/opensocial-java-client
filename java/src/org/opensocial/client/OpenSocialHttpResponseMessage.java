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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.oauth.http.HttpResponseMessage;

/**
 * A small implementation of an HttpResponseMessage that does not require
 * org.apache.http.client as a dependency.
 *
 * @author Dan Holevoet, Jason Cooper
 */
public class OpenSocialHttpResponseMessage extends HttpResponseMessage {
  
  protected int status;
  protected String body;
  protected OpenSocialUrl url;

  protected OpenSocialHttpResponseMessage(String method, OpenSocialUrl url,
      InputStream responseStream, int status) throws IOException {
    super(method, null);

    this.url = url;
    this.body = getResponseString(responseStream);
    this.status = status;
  }
  
  /**
   * Returns the status code for the response.
   * 
   * @return Status code
   * @throws IOException if the status code is 0 (not set)
   */
  public int getStatusCode() throws IOException {
    if (this.status == 0) {
      throw new IOException("Response returned status code" + this.status);
    }

    return this.status;
  }

  /**
   * Returns the buffered response from the server as a String.
   *
   * @return Server response
   */
  public String getBodyString() {
    return this.body;
  }

  /**
   * After executing the request, transforms response output contained in the
   * connection's InputStream object into a string representation which can
   * later be parsed into a more meaningful object, e.g. OpenSocialPerson. 
   *
   * @throws IOException if the open connection's input stream is not
   *         retrievable or accessible
   */
  private String getResponseString(InputStream responseStream) throws
      IOException {
    if (responseStream != null) {
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

    return null;
  }
}

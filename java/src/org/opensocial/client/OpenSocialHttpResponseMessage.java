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

import net.oauth.http.HttpResponseMessage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A small implementation of an HttpResponseMessage that does not require
 * org.apache.http.client as a dependency.
 *
 * @author api.dwh@google.com (Dan Holevoet)
 * @author apijason@google.com (Jason Cooper)
 * @author jle.edwards@gmail.com (Jesse Edwards)
 */
public class OpenSocialHttpResponseMessage extends HttpResponseMessage {

  protected String responseBody = null;
  protected int status;
  protected JSONObject data;

  public OpenSocialHttpResponseMessage(String method, OpenSocialUrl url,
      InputStream responseStream, int status) throws IOException {
    super(method, url.toURL());

    _setResponseBody(responseStream);

    try {
      data = new JSONObject(responseBody);
    } catch(JSONException e) {
      e.printStackTrace();
    }

    this.status = status;
  }

  public OpenSocialHttpResponseMessage(String method, OpenSocialUrl url, 
      int status) throws IOException {
    super(method, url.toURL());
    this.status = status;
    data = new JSONObject();
  }

  /**
   * Returns the status code for the response.
   *
   * @return Status code
   * @throws IOException if the status code is 0 (not set)
   */
  public int getStatusCode() {
    return this.status;
  }

  /**
   * Transforms response output contained in the InputStream object returned by
   * the connection into a string representation which can later be parsed into
   * a more meaningful object, e.g. OpenSocialPerson.
   *
   */
  private void _setResponseBody(InputStream in) {
    try{
      if (in != null) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
        in.close();
        this.responseBody = sb.toString();
      }
    }catch(IOException e){
      this.responseBody = null;
    }
  }

  public JSONObject getResponseData() {
    return data;
  }

  public String getBodyString() {
      return responseBody;
  }
}

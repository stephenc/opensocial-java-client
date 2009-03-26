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

import net.oauth.http.HttpMessage;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A small implementation of HttpClient to serve the needs of the OAuth library
 * rather than requiring org.apache.http.client as a dependency.
 *
 * @author api.dwh@google.com (Dan Holevoet)
 */
class OpenSocialHttpClient implements net.oauth.http.HttpClient {

  /**
   * Executes the request, sending the request body if applicable.
   * 
   * @param request
   * @return Response message
   * @throws IOException
   */
  public OpenSocialHttpResponseMessage execute(HttpMessage request) throws
      IOException {
    OpenSocialHttpMessage openSocialRequest = new OpenSocialHttpMessage(
        request.method, new OpenSocialUrl(request.url.toExternalForm()), null);

    return execute(openSocialRequest);
  }

  /**
   * Executes the request, sending the request body if applicable.
   * 
   * @param request
   * @return Response message
   * @throws IOException
   */
  public OpenSocialHttpResponseMessage execute(OpenSocialHttpMessage request)
      throws IOException {
    final String method = request.method;
    final boolean isPut = PUT.equalsIgnoreCase(method);
    final boolean isPost = POST.equalsIgnoreCase(method);
    final boolean isDelete = DELETE.equalsIgnoreCase(method);

    final String bodyString = request.getBodyString();
    final String contentType = request.getHeader(HttpMessage.CONTENT_TYPE);
    final OpenSocialUrl url = request.getUrl();

    OpenSocialHttpResponseMessage response = null;
    if (isPut) {
      response = send("PUT", url, contentType, bodyString);
    } else if (isPost) {
      response = send("POST", url, contentType, bodyString);
    } else if (isDelete) {
      response = send("DELETE", url, contentType);
    } else {
      response = send("GET", url, contentType);
    }

    return response;
  }

  /**
   * Executes a request without writing any data in the request's body.
   *
   * @param method
   * @param url
   * @return Response message
   */
  private OpenSocialHttpResponseMessage send(String method, OpenSocialUrl url,
      String contentType) throws IOException {
    return send(method, url, contentType, null);
  }

  /**
   * Executes a request and writes all data in the request's body to the
   * output stream.
   *
   * @param method
   * @param url
   * @param body
   * @return Response message
   */
  private OpenSocialHttpResponseMessage send(String method, OpenSocialUrl url,
      String contentType, String body) throws IOException {
    int responseCode = 0;
    try {
      HttpURLConnection connection = getConnection(method, url, contentType);

      if (body != null) {
        OutputStreamWriter out =
          new OutputStreamWriter(connection.getOutputStream());
        out.write(body);
        out.flush();
        out.close();
      }

      responseCode = connection.getResponseCode();

      return new OpenSocialHttpResponseMessage(method, url,
          connection.getInputStream(), responseCode);
    } catch (IOException e) {
      return new OpenSocialHttpResponseMessage(method, url, null,
          responseCode);
    }
  }

  /**
   * Opens a new HTTP connection for the URL associated with this object.
   *
   * @param method
   * @param url
   * @return Opened connection
   * @throws IOException if URL is invalid, or unsupported
   */
  private HttpURLConnection getConnection(String method, OpenSocialUrl url,
      String contentType) throws IOException {
    HttpURLConnection connection =
      (HttpURLConnection) new URL(url.toString()).openConnection();
    if (contentType != null && !contentType.equals("")) {
      connection.setRequestProperty(HttpMessage.CONTENT_TYPE, contentType);
    }
    connection.setRequestMethod(method);
    connection.setDoOutput(true);
    connection.connect();

    return connection;
  }
}

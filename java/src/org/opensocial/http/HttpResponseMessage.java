package org.opensocial.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class HttpResponseMessage extends net.oauth.http.HttpResponseMessage {

  private int statusCode;
  private String response;

  public HttpResponseMessage(String method, URL url, int statusCode) throws
      IOException {
    this(method, url, statusCode, null);
  }

  public HttpResponseMessage(String method, URL url, int statusCode,
      InputStream responseStream) throws IOException {
    super(method, url);
    this.statusCode = statusCode;
    setResponse(responseStream);
  }

  @Override
  public int getStatusCode() {
    return statusCode;
  }

  public String getResponse() {
    return response;
  }

  public String getMethod() {
    return method;
  }

  public URL getUrl() {
    return url;
  }

  private void setResponse(InputStream in) {
    if (in != null) {
      try {
        String line = null;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        while ((line = reader.readLine()) != null) {
          builder.append(line);
        }

        response = builder.toString();
        in.close();
      } catch(IOException e) {
        response = null;
      }
    }
  }
}

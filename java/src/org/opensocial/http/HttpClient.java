package org.opensocial.http;

import net.oauth.http.HttpMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.Map;

public class HttpClient implements net.oauth.http.HttpClient {

  public HttpResponseMessage execute(HttpMessage message) throws IOException {
    return execute(message, null);
  }

  public HttpResponseMessage execute(HttpMessage message,
      Map<String, Object> parameters) throws IOException {
    HttpURLConnection connection = null;

    try {
      connection = getConnection(message);

      if (message.getBody() != null) {
        OutputStreamWriter out =
          new OutputStreamWriter(connection.getOutputStream());
        out.write(StreamToString(message.getBody()));
        out.flush();
        out.close();
      }

      return new HttpResponseMessage(message.method, message.url,
          connection.getResponseCode(), connection.getInputStream());
    } catch (IOException e) {
      return new HttpResponseMessage(message.method, message.url,
          connection.getResponseCode());
    }
  }

  private HttpURLConnection getConnection(HttpMessage message) throws
  IOException {
    HttpURLConnection connection =
      (HttpURLConnection) message.url.openConnection();

    for (Map.Entry<String, String> header : message.headers) {
      connection.setRequestProperty(header.getKey(), header.getValue());
    }

    connection.setRequestMethod(message.method);
    connection.setDoOutput(true);
    connection.connect();

    return connection;
  }

  /**
   * From http://www.kodejava.org/examples/266.html
   * 
   * @param is
   * @return
   */
  private String StreamToString(InputStream stream) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    StringBuilder builder = new StringBuilder();

    String line = null;
    try {
      while ((line = reader.readLine()) != null) {
        builder.append(line);
      }
    } catch (IOException e) {
    } finally {
      try {
        stream.close();
      } catch (IOException e) {
      }
    }

    return builder.toString();
  }
}

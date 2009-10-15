package org.opensocial;

import net.oauth.http.HttpMessage;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.opensocial.auth.AuthScheme;
import org.opensocial.http.HttpClient;
import org.opensocial.http.HttpResponseMessage;
import org.opensocial.providers.Provider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Client {

  private Provider provider;
  private AuthScheme authScheme;
  private HttpClient httpClient;

  private static final Logger logger = Logger.getLogger("org.opensocial");

  public Client(Provider provider, AuthScheme authScheme) {
    this.provider = provider;
    this.authScheme = authScheme;
    this.httpClient = new HttpClient();
  }

  public Provider getProvider() {
    return provider;
  }

  public AuthScheme getAuthScheme() {
    return authScheme;
  }

  public Response send(Request request) throws RequestException, IOException {
    final String KEY = "key";

    Map<String, Request> requests = new HashMap<String, Request>();
    requests.put(KEY, request);

    Map<String, Response> responses = send(requests);

    return responses.get(KEY);
  }

  public Map<String, Response> send(Map<String, Request> requests) throws
      RequestException, IOException {
    if (requests.size() == 0) {
      throw new RequestException("Request queue is empty");
    }

    Map<String, Response> responses = new HashMap<String, Response>();

    if (provider.getRpcEndpoint() != null) {
      responses = submitRpc(requests);
    } else if (provider.getRestEndpoint() != null) {
      for (Map.Entry<String, Request> entry : requests.entrySet()) {
        Request request = entry.getValue();

        provider.preRequest(request);

        Response response = submitRestRequest(request);
        responses.put(entry.getKey(), response);

        provider.postRequest(request, response);
      }
    } else {
      throw new RequestException("Provider has no REST or RPC endpoint set");
    }

    return responses;
  }

  private Map<String, Response> submitRpc(Map<String, Request> requests) throws
      RequestException, IOException {
    Map<String, String> requestHeaders = new HashMap<String, String>();
    requestHeaders.put(HttpMessage.CONTENT_TYPE, provider.getContentType());

    JSONArray requestArray = new JSONArray();
    for (Map.Entry<String, Request> requestEntry : requests.entrySet()) {
      JSONObject request = new JSONObject();
      request.put("id", requestEntry.getKey());
      request.put("method", requestEntry.getValue().getRpcMethod());

      JSONObject requestParams = new JSONObject();
      if (requestEntry.getValue().getGuid() != null) {
        requestParams.put("userId", requestEntry.getValue().getGuid());
      }
      if (requestEntry.getValue().getSelector() != null) {
        requestParams.put("groupId", requestEntry.getValue().getSelector());
      }

      request.put("params", requestParams);
      requestArray.add(request);
    }

    HttpMessage message = authScheme.getHttpMessage(provider, "POST",
        buildRpcUrl(), requestHeaders, requestArray.toJSONString());

    HttpResponseMessage responseMessage = httpClient.execute(message);

    logger.debug("Request URL: " + responseMessage.getUrl().toExternalForm());
    logger.debug("Request body: " + requestArray.toJSONString());
    logger.debug("Status code: " + responseMessage.getStatusCode());
    logger.debug("Response: " + responseMessage.getResponse());

    Map<String, Response> responses = Response.parseRpcResponse(requests,
        responseMessage, provider.getVersion());

    return responses;
  }

  private Response submitRestRequest(Request request) throws RequestException,
      IOException{
    Map<String, String> requestHeaders = new HashMap<String, String>();
    requestHeaders.put(HttpMessage.CONTENT_TYPE, provider.getContentType());

    HttpMessage message = authScheme.getHttpMessage(provider,
        request.getRestMethod(), buildRestUrl(request), requestHeaders, null);

    HttpResponseMessage responseMessage = httpClient.execute(message);

    logger.debug("Request URL: " + responseMessage.getUrl().toString());
    logger.debug("Status code: " + responseMessage.getStatusCode());
    logger.debug("Response: " + responseMessage.getResponse());

    Response response = Response.parseRestResponse(request, responseMessage,
        provider.getVersion());

    return response;
  }

  private String buildRestUrl(Request request) {
    StringBuilder builder = new StringBuilder(provider.getRestEndpoint());
    String[] components = request.getTemplate().split("/");

    for (String component : components) {
      if (component.startsWith("{") && component.endsWith("}")) {
        String tag = component.substring(1, component.length()-1);

        if (tag.equals("guid") && request.getGuid() != null) {
          builder.append(request.getGuid());
          builder.append("/");
        } else if (tag.equals("selector") && request.getSelector() != null) {
          builder.append(request.getSelector());
          builder.append("/");
        }
      } else {
        builder.append(component);
        builder.append("/");
      }
    }

    // Remove trailing forward slash
    builder.deleteCharAt(builder.length() - 1);

    return builder.toString();
  }

  private String buildRpcUrl() {
    StringBuilder builder = new StringBuilder(provider.getRpcEndpoint());

    // Remove trailing forward slash
    if (builder.charAt(builder.length() - 1) == '/') {
      builder.deleteCharAt(builder.length() - 1);
    }

    return builder.toString();
  }
}

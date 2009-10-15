package org.opensocial.auth;

import java.io.IOException;
import java.util.Map;

import net.oauth.http.HttpMessage;

import org.opensocial.RequestException;
import org.opensocial.providers.Provider;

public interface AuthScheme {

  public HttpMessage getHttpMessage(Provider provider, String method,
      String url, Map<String, String> headers, String body) throws
      RequestException, IOException;
}

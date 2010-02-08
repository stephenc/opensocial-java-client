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

package org.opensocial.auth;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.http.HttpMessage;

import org.opensocial.RequestException;
import org.opensocial.providers.Provider;

import java.io.IOException;
import java.util.Map;

public class OAuth2LeggedScheme extends OAuthScheme implements AuthScheme {

  private String requestorId;

  public OAuth2LeggedScheme(String consumerKey, String consumerSecret) {
    this(consumerKey, consumerSecret, null);
  }

  public OAuth2LeggedScheme(String consumerKey, String consumerSecret,
      String requestorId) {
    super(consumerKey, consumerSecret);

    this.requestorId = requestorId;
  }

  public HttpMessage getHttpMessage(Provider provider, String method,
      String url, Map<String, String> headers, byte[] body) throws
      RequestException, IOException {
    if (consumerKey == null || consumerSecret == null) {
      return null;
    }

    url = appendRequestorIdToQueryString(url);
    OAuthMessage message = new OAuthMessage(method, url, null,
        byteArrayToStream(body));

    for (Map.Entry<String, String> header : headers.entrySet()) {
      message.getHeaders().add(header);
    }

    OAuthConsumer consumer =
      new OAuthConsumer(null, consumerKey, consumerSecret, null);
    consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);

    OAuthAccessor accessor = new OAuthAccessor(consumer);

    return getHttpMessage(message, accessor, body, provider.getSignBodyHash());
  }

  public String getRequestorId() {
    return requestorId;
  }

  private String appendRequestorIdToQueryString(String url) {
    if (requestorId == null) {
      return url;
    }

    StringBuilder builder = new StringBuilder(url);

    if (url.indexOf('?') == -1) {
      builder.append("?xoauth_requestor_id=");
    } else {
      builder.append("&xoauth_requestor_id=");
    }

    builder.append(requestorId);

    return builder.toString();
  }
}

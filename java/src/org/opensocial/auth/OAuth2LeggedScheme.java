package org.opensocial.auth;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.ParameterStyle;
import net.oauth.http.HttpMessage;

import org.apache.commons.codec.binary.Base64;
import org.opensocial.RequestException;
import org.opensocial.providers.Provider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Map;

public class OAuth2LeggedScheme implements AuthScheme {

  private String consumerKey;
  private String consumerSecret;
  private String requestorId;

  public OAuth2LeggedScheme(String consumerKey, String consumerSecret) {
    this(consumerKey, consumerSecret, null);
  }

  public OAuth2LeggedScheme(String consumerKey, String consumerSecret,
      String requestorId) {
    this.consumerKey = consumerKey;
    this.consumerSecret = consumerSecret;
    this.requestorId = requestorId;
  }

  public HttpMessage getHttpMessage(Provider provider, String method,
      String url, Map<String, String> headers, String body) throws
      RequestException, IOException {
    if (consumerKey == null || consumerSecret == null) {
      return null;
    }

    url = appendRequestorIdToQueryString(url);
    OAuthMessage message = new OAuthMessage(method, url, null,
        StringToStream(body));

    for (Map.Entry<String, String> header : headers.entrySet()) {
      message.getHeaders().add(header);
    }

    OAuthConsumer consumer =
      new OAuthConsumer(null, consumerKey, consumerSecret, null);
    consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);

    OAuthAccessor accessor = new OAuthAccessor(consumer);

    if (body != null) {
      if (provider.getSignBodyHash()) {
        try {
          MessageDigest md = MessageDigest.getInstance("SHA-1");

          byte[] hash = md.digest(body.getBytes("UTF-8"));
          byte[] encodedHash = new Base64().encode(hash);

          message.addParameter("oauth_body_hash",
              new String(encodedHash, "UTF-8"));
        } catch (java.security.NoSuchAlgorithmException e) {
          // Ignore exception
        } catch (java.io.UnsupportedEncodingException e) {
          // Ignore exception
        }
      } else if (message.getHeader(HttpMessage.CONTENT_TYPE)
          .equals("application/x-www-form-urlencoded")){
        message.addParameter(body, "");
      }
    }

    try {
      message.addRequiredParameters(accessor);
    } catch (OAuthException e) {
      throw new RequestException(
          "OAuth error thrown while signing request " + e.getMessage());
    } catch (java.net.URISyntaxException e) {
      throw new RequestException(
          "Malformed request URL " + message.URL + " could not be signed");
    }

    return HttpMessage.newRequest(message, ParameterStyle.QUERY_STRING);
  }

  public String getConsumerKey() {
    return consumerKey;
  }

  public String getConsumerSecret() {
    return consumerSecret;
  }

  public String getRequestorId() {
    return requestorId;
  }

  private InputStream StringToStream(String text) {
    InputStream stream = null;

    if (text != null) {
      try {
        stream = new ByteArrayInputStream(text.getBytes("UTF-8"));
      } catch (UnsupportedEncodingException e) {
        // Ignore
      }
    }

    return stream;
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

package org.opensocial.auth;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;
import net.oauth.ParameterStyle;
import net.oauth.http.HttpMessage;

import org.apache.commons.codec.binary.Base64;
import org.opensocial.RequestException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

public abstract class OAuthScheme {

  protected String consumerKey;
  protected String consumerSecret;

  public OAuthScheme(String consumerKey, String consumerSecret) {
    this.consumerKey = consumerKey;
    this.consumerSecret = consumerSecret;
  }

  public String getConsumerKey() {
    return consumerKey;
  }

  public String getConsumerSecret() {
    return consumerSecret;
  }

  protected InputStream stringToStream(String text) {
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

  protected HttpMessage getHttpMessage(OAuthMessage message,
      OAuthAccessor accessor, String body, boolean signBodyHash) throws
      IOException, RequestException {
    if (body != null) {
      if (signBodyHash) {
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
}

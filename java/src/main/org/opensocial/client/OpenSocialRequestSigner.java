/* Copyright (c) 2008 Google Inc.
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthException;
import net.oauth.OAuthMessage;

public class OpenSocialRequestSigner {
  
  public static void signRequest(OpenSocialHttpRequest request, OpenSocialClient client) throws OAuthException, IOException, URISyntaxException {
    String token =
      client.getProperty(OpenSocialClient.Properties.TOKEN);
    String viewerId =
      client.getProperty(OpenSocialClient.Properties.VIEWER_ID);
    String consumerKey =
      client.getProperty(OpenSocialClient.Properties.CONSUMER_KEY);
    String consumerSecret =
      client.getProperty(OpenSocialClient.Properties.CONSUMER_SECRET);
    
    signRequest(request, token, viewerId, consumerKey, consumerSecret);
  }
  
  public static void signRequest(OpenSocialHttpRequest request, String token, String viewerId, String consumerKey, String consumerSecret) throws OAuthException, IOException, URISyntaxException {
    OpenSocialUrl requestUrl = request.getUrl();

    if (viewerId != null) {
      requestUrl.addQueryStringParameter("xoauth_requestor_id", viewerId);
    }
    if (token != null) {
      requestUrl.addQueryStringParameter("st", token);
    }
    
    signRequest(request, consumerKey, consumerSecret);
  }
  
  public static void signRequest(OpenSocialHttpRequest request, String consumerKey, String consumerSecret) throws OAuthException, IOException, URISyntaxException {
    String postBody = request.getPostBody();
    String requestMethod = request.getMethod();
    OpenSocialUrl requestUrl = request.getUrl();
    
    if (consumerKey != null && consumerSecret != null) {
      OAuthMessage message = new OAuthMessage(requestMethod, requestUrl.toString(), null);
      
      if (postBody != null) {
        message.addParameter(postBody, "");        
      }

      OAuthConsumer consumer = new OAuthConsumer(null, consumerKey, consumerSecret, null);
      consumer.setProperty(OAuth.OAUTH_SIGNATURE_METHOD, OAuth.HMAC_SHA1);

      OAuthAccessor accessor = new OAuthAccessor(consumer);
      accessor.accessToken = "";      
      
      message.addRequiredParameters(accessor);

      for (Map.Entry<String, String> p : message.getParameters()) {
        if (!p.getKey().equals(postBody)) {
          requestUrl.addQueryStringParameter(OAuth.percentEncode(p.getKey()), OAuth.percentEncode(p.getValue()));          
        }
      }
    }
  }
}

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

import java.util.Map;
import java.util.HashMap;

/**
 * Lists all known OpenSocial providers and their OAuth endpoints.
 *
 * @author doll@google.com (Cassandra Doll)
 * @author apijason@google.com (Jason Cooper)
 */
public enum OpenSocialProvider {
  FRIENDCONNECT(null, null, null, "http://www.google.com/friendconnect/api",
      "http://www.google.com/friendconnect/api/rpc", "friendconnect",
      Feature.OPENSOCIAL, Feature.NO_SIGN_BODY),

  GOOGLE("https://www.google.com/accounts/OAuthGetRequestToken",
      "https://www.google.com/accounts/OAuthAuthorizeToken",
      "https://www.google.com/accounts/OAuthGetAccessToken",
      "http://www-opensocial-sandbox.googleusercontent.com/api",
      "http://www-opensocial-sandbox.googleusercontent.com/api/rpc",
      "Google", Feature.OPENSOCIAL, Feature.NO_SIGN_BODY),

  MYSPACE("http://api.myspace.com/request_token",
      "http://api.myspace.com/authorize",
      "http://api.myspace.com/access_token",
      "http://api.myspace.com/v2", null, "MySpace", Feature.OPENSOCIAL,
      Feature.NO_SIGN_BODY),

  ORKUT(null, null, null, "http://www.orkut.com/social/rest/",
      "http://www.orkut.com/social/rpc/", "orkut.com", Feature.OPENSOCIAL,
      Feature.NO_SIGN_BODY),

  ORKUT_SANDBOX(null, null, null, "http://sandbox.orkut.com/social/rest/",
      "http://sandbox.orkut.com/social/rpc/", "orkut.com", Feature.OPENSOCIAL,
      Feature.NO_SIGN_BODY),

  PARTUZA("http://www.partuza.nl/oauth/request_token",
      "http://www.partuza.nl/oauth/authorize",
      "http://www.partuza.nl/oauth/access_token",
      "http://modules.partuza.nl/social/rest", null, "Partuza",
      Feature.OPENSOCIAL, Feature.SIGN_BODY),

  PLAXO("http://www.plaxo.com/oauth/request",
      "http://www.plaxo.com/oauth/authorize",
      "http://www.plaxo.com/oauth/activate",
      "http://www.plaxo.com/pdata/contacts", null, "Plaxo",
      Feature.NO_OPENSOCIAL, Feature.NO_SIGN_BODY);

  static {
    GOOGLE.requestTokenParams = new HashMap<String, String>();
    GOOGLE.requestTokenParams.put("scope",
        "http://sandbox.gmodules.com/api/people");
  }

  private enum Feature {
    OPENSOCIAL, NO_OPENSOCIAL, SIGN_BODY, NO_SIGN_BODY
  }

  public String requestTokenUrl;
  public Map<String, String> requestTokenParams;
  public String authorizeUrl;
  public String accessTokenUrl;
  public String restEndpoint;
  public String rpcEndpoint;
  public String providerName;
  public boolean isOpenSocial;
  public boolean signBody;

  public String contentType;

  OpenSocialProvider(String requestTokenUrl, String authorizeUrl,
      String accessTokenUrl, String restEndpoint, String rpcEndpoint,
      String providerName, Feature isOpenSocial, Feature signBody) {
    this.requestTokenUrl = requestTokenUrl;
    this.authorizeUrl = authorizeUrl;
    this.accessTokenUrl = accessTokenUrl;
    this.restEndpoint = restEndpoint;
    this.rpcEndpoint = rpcEndpoint;
    this.providerName = providerName;
    this.contentType = "application/json";

    this.isOpenSocial = true;
    if (isOpenSocial.equals(Feature.NO_OPENSOCIAL)) {
      this.isOpenSocial = false;
    }
    this.signBody = false;
    if (signBody.equals(Feature.SIGN_BODY)) {
      this.signBody = true;
    }
  }
}

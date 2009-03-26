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
  MYSPACE("http://api.myspace.com/request_token",
      "http://api.myspace.com/authorize",
      "http://api.myspace.com/access_token",
      "http://api.myspace.com/v2", null, "MySpace", Feature.OPENSOCIAL,
      ContentType.URLENCODED, Feature.NO_APPEND_BODY_HASH, Feature.SIGN_BODY),

  PLAXO("http://www.plaxo.com/oauth/request",
      "http://www.plaxo.com/oauth/authorize",
      "http://www.plaxo.com/oauth/activate",
      "http://www.plaxo.com/pdata/contacts", null, "Plaxo",
      Feature.NO_OPENSOCIAL, ContentType.URLENCODED,
      Feature.NO_APPEND_BODY_HASH, Feature.SIGN_BODY),

  GOOGLE("https://www.google.com/accounts/OAuthGetRequestToken",
      "https://www.google.com/accounts/OAuthAuthorizeToken",
      "https://www.google.com/accounts/OAuthGetAccessToken",
      "http://www-opensocial-sandbox.googleusercontent.com/api",
      "http://www-opensocial-sandbox.googleusercontent.com/api/rpc",
      "Google", Feature.OPENSOCIAL, ContentType.URLENCODED,
      Feature.NO_APPEND_BODY_HASH, Feature.SIGN_BODY),

  ORKUT_SANDBOX(null, null, null, "http://sandbox.orkut.com/social/rest/",
      "http://sandbox.orkut.com/social/rpc/", "orkut.com", Feature.OPENSOCIAL,
      ContentType.JSON, Feature.APPEND_BODY_HASH, Feature.NO_SIGN_BODY),

  ORKUT(null, null, null, "http://www.orkut.com/social/rest/",
      "http://www.orkut.com/social/rpc/", "orkut.com", Feature.OPENSOCIAL,
      ContentType.URLENCODED, Feature.NO_APPEND_BODY_HASH,
      Feature.SIGN_BODY),

  PARTUZA("http://www.partuza.nl/oauth/request_token",
      "http://www.partuza.nl/oauth/authorize",
      "http://www.partuza.nl/oauth/access_token",
      "http://modules.partuza.nl/social/rest", null, "Partuza",
      Feature.OPENSOCIAL, ContentType.URLENCODED,
      Feature.NO_APPEND_BODY_HASH, Feature.SIGN_BODY);

  static {
    GOOGLE.requestTokenParams = new HashMap<String, String>();
    GOOGLE.requestTokenParams.put("scope",
        "http://sandbox.gmodules.com/api/people");
  }

  private enum Feature {
    OPENSOCIAL, NO_OPENSOCIAL, SIGN_BODY, NO_SIGN_BODY, APPEND_BODY_HASH,
    NO_APPEND_BODY_HASH
  }

  private enum ContentType {
    URLENCODED, JSON
  }

  public String requestTokenUrl;
  public Map<String, String> requestTokenParams;
  public String authorizeUrl;
  public String accessTokenUrl;
  public String restEndpoint;
  public String rpcEndpoint;
  public String providerName;
  public boolean isOpenSocial;
  public String contentType;
  public boolean appendBodyHash;
  public boolean signBody;

  OpenSocialProvider(String requestTokenUrl, String authorizeUrl,
      String accessTokenUrl, String restEndpoint, String rpcEndpoint,
      String providerName, Feature isOpenSocial, ContentType contentType,
      Feature appendBodyHash, Feature signBody) {
    this.requestTokenUrl = requestTokenUrl;
    this.authorizeUrl = authorizeUrl;
    this.accessTokenUrl = accessTokenUrl;
    this.restEndpoint = restEndpoint;
    this.rpcEndpoint = rpcEndpoint;
    this.providerName = providerName;

    this.isOpenSocial = false;
    if (Feature.OPENSOCIAL.equals(isOpenSocial)) {
      this.isOpenSocial = true;
    }
    this.contentType = "application/json";
    if (ContentType.URLENCODED.equals(contentType)) {
      this.contentType = "application/x-www-form-urlencoded";
    }
    this.appendBodyHash = false;
    if (Feature.APPEND_BODY_HASH.equals(appendBodyHash)) {
      this.appendBodyHash = true;
    }
    this.signBody = false;
    if (Feature.SIGN_BODY.equals(signBody)) {
      this.signBody = true;
    }
  }
}

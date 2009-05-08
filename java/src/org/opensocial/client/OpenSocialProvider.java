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
      BodySigningMethod.SIGN_BODY_HASH, true),

  GOOGLE("https://www.google.com/accounts/OAuthGetRequestToken",
      "https://www.google.com/accounts/OAuthAuthorizeToken",
      "https://www.google.com/accounts/OAuthGetAccessToken",
      "http://www-opensocial-sandbox.googleusercontent.com/api",
      "http://www-opensocial-sandbox.googleusercontent.com/api/rpc",
      "Google", BodySigningMethod.SIGN_BODY_HASH, true),

  HI5("https://api.hi5.com/oauth/requestToken",
      "https://login.hi5.com/oauth/authorize",
      "https://api.hi5.com/oauth/accessToken",
      "http://api.hi5.com/social/rest", "http://api.hi5.com/social/rpc", "hi5",
      BodySigningMethod.SIGN_BODY_HASH, true),

  MYSPACE("http://api.myspace.com/request_token",
      "http://api.myspace.com/authorize",
      "http://api.myspace.com/access_token",
      "http://api.myspace.com/v2", null, "MySpace",
      BodySigningMethod.SIGN_BODY_HASH, true),

  ORKUT(null, null, null, "http://www.orkut.com/social/rest/",
      "http://www.orkut.com/social/rpc/", "orkut.com",
      BodySigningMethod.SIGN_BODY_HASH, true),

  ORKUT_SANDBOX(null, null, null, "http://sandbox.orkut.com/social/rest/",
      "http://sandbox.orkut.com/social/rpc", "orkut.com",
      BodySigningMethod.SIGN_BODY_HASH, true),

  PARTUZA("http://www.partuza.nl/oauth/request_token",
      "http://www.partuza.nl/oauth/authorize",
      "http://www.partuza.nl/oauth/access_token",
      "http://modules.partuza.nl/social/rest", null, "Partuza",
      BodySigningMethod.SIGN_RAW_BODY, true),

  PLAXO("http://www.plaxo.com/oauth/request",
      "http://www.plaxo.com/oauth/authorize",
      "http://www.plaxo.com/oauth/activate",
      "http://www.plaxo.com/pdata/contacts", null, "Plaxo",
      BodySigningMethod.SIGN_BODY_HASH, false),

  /**
   * Note: The SHINDIG provider uses a local OAuth provider. To get this local
   * OAuth provider running on port 9090, do the following:
   * 1) Download and launch example service provider from oauth.net:
   *    a) svn co http://oauth.googlecode.com/svn/code/java java
   *    b) cd java/example/oauth-provider
   *    c) mvn jetty:run-war
   * 2) If your OAuth provider is not running on port 9090 or your Shindig
   *    instance is not running on port 8080, edit the port numbers below.
   */
  SHINDIG("http://localhost:9090/oauth-provider/request_token",
      "http://localhost:9090/oauth-provider/authorize",
      "http://localhost:9090/oauth-provider/access_token",
      "http://localhost:8080/social/rest/",
      "http://localhost:8080/social/rpc/", "localhost",
      BodySigningMethod.SIGN_BODY_HASH, true);

  static {
    GOOGLE.requestTokenParams = new HashMap<String, String>();
    GOOGLE.requestTokenParams.put("scope",
        "http://sandbox.gmodules.com/api/people");
  }

  /** For an explanation of these different signing methods, see
   *  http://tr.im/osbodysigningmethod; SIGN_RAW_BODY is deprecated and should
   *  only be used for the purposes of maintaing backwards-compatibility with
   *  containers which do not accept the signed body hash. */
  private enum BodySigningMethod {
    SIGN_BODY_HASH, @Deprecated SIGN_RAW_BODY
  }

  public String requestTokenUrl;
  public Map<String, String> requestTokenParams;
  public String authorizeUrl;
  public String accessTokenUrl;
  public String restEndpoint;
  public String rpcEndpoint;
  public String providerName;
  public boolean signBodyHash;
  public boolean isOpenSocial;

  public String contentType;

  OpenSocialProvider(String requestTokenUrl, String authorizeUrl,
      String accessTokenUrl, String restEndpoint, String rpcEndpoint,
      String providerName, BodySigningMethod bodySigningMethod,
      boolean isOpenSocial) {
    this.requestTokenUrl = requestTokenUrl;
    this.authorizeUrl = authorizeUrl;
    this.accessTokenUrl = accessTokenUrl;
    this.restEndpoint = restEndpoint;
    this.rpcEndpoint = rpcEndpoint;
    this.providerName = providerName;
    this.contentType = "application/json";
    this.isOpenSocial = isOpenSocial;

    signBodyHash = true;
    if (bodySigningMethod.equals(BodySigningMethod.SIGN_RAW_BODY)) {
      signBodyHash = false;
    }
  }
}

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

import java.util.Map;
import java.util.HashMap;

/**
 * Lists all known OpenSocial providers and their OAuth endpoints.
 *
 * @author Cassandra Doll
 */
public enum OpenSocialProvider {
  MYSPACE("http://api.myspace.com/request_token",
      "http://api.myspace.com/authorize",
      "http://api.myspace.com/access_token",
      "http://api.myspace.com/v2",
      null,
      "MySpace", true),

  PLAXO("http://www.plaxo.com/oauth/request",
      "http://www.plaxo.com/oauth/authorize",
      "http://www.plaxo.com/oauth/activate",
      "http://www.plaxo.com/pdata/contacts",
      null,
      "Plaxo", false),

  GOOGLE("https://www.google.com/accounts/OAuthGetRequestToken",
      "https://www.google.com/accounts/OAuthAuthorizeToken",
      "https://www.google.com/accounts/OAuthGetAccessToken",
      "http://sandbox.gmodules.com/api",
      null,
      "Google", true),

  PARTUZA("http://www.partuza.nl/oauth/request_token",
      "http://www.partuza.nl/oauth/authorize",
      "http://www.partuza.nl/oauth/access_token",
      "http://modules.partuza.nl/social/rest",
      null,
      "Partuza", true);

  static {
    GOOGLE.requestTokenParams = new HashMap<String, String>();
    GOOGLE.requestTokenParams.put("scope", "http://sandbox.gmodules.com/api/people");
  }

  public String requestTokenUrl;
  public Map<String, String> requestTokenParams;
  public String authorizeUrl;
  public String accessTokenUrl;
  public String restEndpoint;
  public String rpcEndpoint;
  public String providerName;
  public boolean isOpenSocial;

  OpenSocialProvider(String requestTokenUrl, String authorizeUrl, String accessTokenUrl,
      String restEndpoint, String rpcEndpoint, String providerName, boolean isOpenSocial) {
    this.requestTokenUrl = requestTokenUrl;
    this.authorizeUrl = authorizeUrl;
    this.accessTokenUrl = accessTokenUrl;
    this.restEndpoint = restEndpoint;
    this.rpcEndpoint = rpcEndpoint;
    this.providerName = providerName;
    this.isOpenSocial = isOpenSocial;
  }
}

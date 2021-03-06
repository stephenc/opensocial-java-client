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

package org.opensocial.providers;

public class GoogleProvider extends Provider {

  public GoogleProvider() {
    this(false);
  }

  public GoogleProvider(boolean useRest) {
    super();

    setName("Google");
    setVersion("0.8");
    setRestEndpoint("http://www-opensocial.googleusercontent.com/api/");
    if (!useRest) {
      setRpcEndpoint("http://www-opensocial.googleusercontent.com/api/rpc/");
    }
    setAuthorizeUrl("https://www.google.com/accounts/OAuthAuthorizeToken");
    setAccessTokenUrl("https://www.google.com/accounts/OAuthGetAccessToken");
    setRequestTokenUrl("https://www.google.com/accounts/OAuthGetRequestToken");
    addRequestTokenParameter("scope", getRestEndpoint() + " " +
        getRpcEndpoint());
  }
}

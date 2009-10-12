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
public class ShindigProvider extends OpenSocialProvider {

  public ShindigProvider() {
    super();
    
    requestTokenUrl = "http://localhost:9090/oauth-provider/request_token";
    authorizeUrl = "http://localhost:9090/oauth-provider/authorize";
    accessTokenUrl = "http://localhost:9090/oauth-provider/access_token";
    restEndpoint = "http://localhost:8080/social/rest/";
    rpcEndpoint = "http://localhost:8080/social/rpc/";
    providerName = "localhost";
    signBodyHash = true;
    isOpenSocial = true;
  }
}

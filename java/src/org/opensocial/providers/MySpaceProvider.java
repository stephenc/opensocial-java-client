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

import org.opensocial.client.OpenSocialRequest;

public class MySpaceProvider extends OpenSocialProvider {
  
  public MySpaceProvider() {
    super();
    
    requestTokenUrl = "http://api.myspace.com/request_token";
    authorizeUrl = "http://api.myspace.com/authorize";
    accessTokenUrl = "http://api.myspace.com/access_token";
    restEndpoint = "http://opensocial.myspace.com/roa/09";
    providerName = "MySpace";
    signBodyHash = false;
    isOpenSocial = true;
  }
  
  public void preRequest(OpenSocialRequest request) {
      // Modify request as necessary
  }
  
  public void postRequest(OpenSocialRequest request, String response) {
      
  }
}

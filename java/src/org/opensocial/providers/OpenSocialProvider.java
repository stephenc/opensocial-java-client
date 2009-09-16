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

import java.util.Map;

import org.opensocial.client.OpenSocialRequest;

public class OpenSocialProvider 
{
  public String requestTokenUrl;
  public Map<String, String> requestTokenParams;
  public String authorizeUrl;
  public String accessTokenUrl;
  public String restEndpoint;
  public String rpcEndpoint;
  public String providerName;
  public boolean signBodyHash;
  public boolean isOpenSocial;
  public String contentType = "application/json";
  
  public OpenSocialProvider( String requestTokenUrl,String authorizeUrl, 
      String accessTokenUrl, String restEndpoint, String rpcEndpoint, 
      String providerName, boolean signBodyHash, boolean isOpenSocial ) {
      
    //TODO: check for vaild urls when setting. 
    this.requestTokenUrl = requestTokenUrl;
    this.authorizeUrl = authorizeUrl;
    this.accessTokenUrl = accessTokenUrl;
    this.restEndpoint = restEndpoint;
    this.rpcEndpoint = rpcEndpoint;
    this.providerName = providerName;
    this.isOpenSocial = isOpenSocial;
    this.signBodyHash = signBodyHash;
  }
  
  OpenSocialProvider(){}
  
  public void preRequest(OpenSocialRequest request) {
      
  }
  
  public void postRequest(OpenSocialRequest request, String response) {
      
  }
}

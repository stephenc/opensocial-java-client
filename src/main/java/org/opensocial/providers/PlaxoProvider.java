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

import org.opensocial.Request;

public class PlaxoProvider extends Provider {

  private static final String REST_URL_TEMPLATE = "{guid}/{selector}/{pid}";

  public PlaxoProvider() {
    super();

    setName("Plaxo");
    setVersion("0.8");
    setRestEndpoint("http://www.plaxo.com/pdata/contacts/");
    setAuthorizeUrl("http://www.plaxo.com/oauth/authorize");
    setAccessTokenUrl("http://www.plaxo.com/oauth/activate");
    setRequestTokenUrl("http://www.plaxo.com/oauth/request");
  }

  @Override
  public void preRequest(Request request) {
    request.setRestUrlTemplate(REST_URL_TEMPLATE);

    String guid = request.getComponent(Request.GUID);
    if (!guid.equals("@me")) {
      request.setPId(guid);
      request.setGuid("@me");
      request.setSelector("@all");
    }

    String selector = request.getComponent(Request.SELECTOR);
    if (selector.equals("@friends")) {
      request.setSelector("@all");
    }
  }
}

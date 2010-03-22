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

package org.opensocial;

import org.junit.Test;
import org.opensocial.auth.OAuth2LeggedScheme;
import org.opensocial.providers.OrkutProvider;
import org.opensocial.providers.Provider;
import org.opensocial.services.PeopleService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ClientTest {

  private static final String VIEWER_ID = "03067092798963641994";
  private static final String CONSUMER_KEY = "orkut.com:623061448914";
  private static final String CONSUMER_SECRET = "uynAeXiWTisflWX99KU1D2q5";

  @Test(expected = RequestException.class)
  public void testEmptyRequestQueue() throws RequestException, IOException {
    Client client = new Client(new OrkutProvider(), new OAuth2LeggedScheme(
        CONSUMER_KEY, CONSUMER_SECRET, VIEWER_ID));

    Map<String, Request> requests = new HashMap<String, Request>();
    client.send(requests);
  }

  @Test(expected = RequestException.class)
  public void testNoEndpointsSet() throws RequestException, IOException {
    Client client = new Client(new Provider(), new OAuth2LeggedScheme(
        CONSUMER_KEY, CONSUMER_SECRET, VIEWER_ID));

    Request request = PeopleService.getViewer();
    client.send(request);
  }
}

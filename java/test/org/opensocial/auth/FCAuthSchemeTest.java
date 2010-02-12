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

package org.opensocial.auth;

import static org.junit.Assert.assertEquals;

import net.oauth.http.HttpMessage;

import org.junit.Test;
import org.opensocial.RequestException;
import org.opensocial.providers.OrkutProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FCAuthSchemeTest {

  private static final String TOKEN = "TOKEN";

  @Test
  public void getHttpMessage() throws RequestException,
      IOException {
    final String url = "http://example.org/test";
    final Map<String, String> headers = new HashMap<String, String>();

    FCAuthScheme authScheme = new FCAuthScheme(TOKEN);

    HttpMessage message = authScheme.getHttpMessage(new OrkutProvider(), "GET",
        url, headers, null);
    assertEquals(url + "?fcauth=" + TOKEN, message.url.toString());
  }
}

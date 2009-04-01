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

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

/**
 * Unit test suite for OpenSocialUrl.
 *
 * @author apijason@google.com (Jason Cooper)
 */
public class OpenSocialUrlTest {

  /**
   * Tests routine for serializing OpenSocialUrl instance as a URL string,
   * ensuring that query string parameters are appended to the final string
   * correctly.
   *
   * @throws UnsupportedEncodingException if the specified character encoding
   *         used for encoding the URL parameters is not supported
   */
  @Test
  public void testToString() throws UnsupportedEncodingException {
    String base = "http://www.opensocial.org";
    OpenSocialUrl u = new OpenSocialUrl(base);
    assertTrue(u.toString().equals(base));

    String parameter1 = "p1";
    String parameter2 = "p?";
    String value1 = "v1";
    String value2 = "v&";
    u.addQueryStringParameter(parameter1, value1);
    u.addQueryStringParameter(parameter2, value2);

    Pattern pattern1 = Pattern.compile("(\\?|&)" + URLEncoder.encode(parameter1,
        "UTF-8") + "=" + URLEncoder.encode(value1, "UTF-8"));
    Pattern pattern2 = Pattern.compile("(\\?|&)" + URLEncoder.encode(parameter2,
        "UTF-8") + "=" + URLEncoder.encode(value2, "UTF-8"));
    assertTrue(pattern1.matcher(u.toString()).find());
    assertTrue(pattern2.matcher(u.toString()).find());
  }
}

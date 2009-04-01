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

import java.io.IOException;

/**
 * Unit test suite for OpenSocialClient.
 *
 * @author apijason@google.com (Jason Cooper)
 */
public class OpenSocialClientTest {

  /** Sample domain string used throughout the test suite. */
  private static final String DOMAIN = "orkut.com";

  /**
   * Tests whether DOMAIN property is set correctly on initialization by
   * retrieving the DOMAIN property of a new OpenSocialClient instance and
   * comparing its value to the string passed into the constructor.
   */
  @Test
  public void testCreate() {
    OpenSocialClient client = new OpenSocialClient(DOMAIN);

    assertTrue(client.getProperty(OpenSocialClient.Property.DOMAIN).equals(
        DOMAIN));
  }

  /**
   * Tests whether property is set correctly when passed into setProperty method
   * by setting then retrieving a property and comparing its value to the
   * original string.
   */
  @Test
  public void testGetProperty() {
    OpenSocialClient client = new OpenSocialClient();
    client.setProperty(OpenSocialClient.Property.DOMAIN, DOMAIN);

    assertTrue(client.getProperty(OpenSocialClient.Property.DOMAIN).equals(
        DOMAIN));
  }

  /**
   * Tests whether "fetch person" request parameters are set correctly by
   * comparing the parameter values to the values specified by the OpenSocial
   * JSON-RPC protocol specification.
   */
  @Test
  public void testNewFetchPersonRequest() {
    OpenSocialRequest request = OpenSocialClient.newFetchPersonRequest(
        OpenSocialClient.ME);

    assertTrue(request.getParameter(OpenSocialRequest.GROUP_PARAMETER).equals(
        OpenSocialClient.SELF));
    assertTrue(request.getParameter(OpenSocialRequest.USER_PARAMETER).equals(
        OpenSocialClient.ME));
  }

  /**
   * Tests whether an OpenSocialRequestException is thrown when the user ID
   * passed is an empty string.
   *
   * @throws OpenSocialRequestException if there is an error creating the
   *         request, the container returns an error code or the container's
   *         response cannot be parsed
   * @throws IOException if an I/O error prevents a connection from being opened
   *         or otherwise causes the RPC request to fail
   */
  @Test(expected = OpenSocialRequestException.class)
  public void testEmptyUserId() throws OpenSocialRequestException, IOException {
    OpenSocialClient client = new OpenSocialClient();
    client.fetchPerson("");
  }
}

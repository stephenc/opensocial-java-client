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

import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit test suite for OpenSocialRequest.
 *
 * @author apijason@google.com (Jason Cooper)
 */
public class OpenSocialRequestTest {

  /** String identifier of request instance. */
  private static final String ID = "id";

  /** RPC method name indicating which action to perform. */
  private static final String METHOD = "people.get";

  /** REST path component associated with requests for this resource. */
  private static final String REST_COMPONENT = "people";

  /** Arbitrary property name to test whether properties are set correctly. */
  private static final String PROPERTY1 = "p1";

  /** Arbitrary property name to test whether properties are set correctly. */
  private static final String PROPERTY2 = "p2";

  /** Arbitrary property value to test whether properties are set correctly. */
  private static final String VALUE1 = "v1";

  /** Arbitrary property value to test whether properties are set correctly. */
  private static final String VALUE2 = "v2";

  /**
   * Tests whether id property is set correctly when passed into setId method
   * by setting then retrieving the id property and comparing its value to the
   * original id string.
   */
  @Test
  public void testGetId() {
    OpenSocialRequest r = new OpenSocialRequest(REST_COMPONENT, METHOD);
    r.setId(ID);

    assertTrue(r.getId().equals(ID));
  }

  /**
   * Tests whether parameter values are set correctly when passed into
   * addParameter method by adding a couple of arbitrary parameters then
   * retrieving their values and comparing these to the original strings.
   */
  @Test
  public void testGetParameter() {
    OpenSocialRequest r = new OpenSocialRequest(REST_COMPONENT, METHOD);
    r.addParameter(PROPERTY1, VALUE1);
    r.addParameter(PROPERTY2, VALUE2);

    assertTrue(r.getParameter(PROPERTY1).equals(VALUE1));
    assertTrue(r.getParameter(PROPERTY2).equals(VALUE2));
  }

  /**
   * Verifies the integrity of the JSON serialization routine by creating a
   * JSONObject having the same parameters as a new OpenSocialRequest instance
   * and comparing their serialized forms.
   *
   * @throws OpenSocialRequestException if an error or malformed parameter (e.g.
   *         null key) prevents the request from being serialized
   * @throws JSONException if any property value is a non-finite number or if
   *         any property key is null
   */
  @Test
  public void testToJson() throws OpenSocialRequestException, JSONException {
    String idKey = "id";
    String methodKey = "method";
    String paramsKey = "params";

    JSONObject jsonControlParams = new JSONObject();
    jsonControlParams.put(PROPERTY1, VALUE1);
    jsonControlParams.put(PROPERTY2, VALUE2);

    JSONObject jsonControl = new JSONObject();
    jsonControl.put(paramsKey, jsonControlParams);
    jsonControl.put(methodKey, METHOD);
    jsonControl.put(idKey, ID);

    OpenSocialRequest request = new OpenSocialRequest(REST_COMPONENT, METHOD);
    request.addParameter(PROPERTY1, VALUE1);
    request.addParameter(PROPERTY2, VALUE2);
    request.setId(ID);

    JSONObject jsonTest = new JSONObject(request.toJson());
    JSONObject jsonTestParams = jsonTest.getJSONObject(paramsKey);
    assertTrue(jsonTest.get(idKey).equals(jsonControl.get(idKey)));
    assertTrue(jsonTest.get(methodKey).equals(jsonControl.get(methodKey)));
    assertTrue(jsonTestParams.get(PROPERTY1).equals(jsonControlParams
        .get(PROPERTY1)));
    assertTrue(jsonTestParams.get(PROPERTY2).equals(jsonControlParams
        .get(PROPERTY2)));
  }

  /**
   * Tests whether an OpenSocialRequestException is thrown when a null parameter
   * key is passed; parameters will null keys cannot be serialized as JSON.
   *
   * @throws OpenSocialRequestException if an error or malformed parameter (e.g.
   *         null key) prevents the request from being serialized
   */
  @Test(expected = OpenSocialRequestException.class)
  public void testNullParameterKey() throws OpenSocialRequestException {
    OpenSocialRequest request = new OpenSocialRequest(REST_COMPONENT, METHOD);
    request.addParameter(null, VALUE1);
    request.toJson();
  }
}

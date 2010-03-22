/* Copyright (c) 2010 Google Inc.
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

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.opensocial.auth.OAuth2LeggedScheme;
import org.opensocial.auth.OAuth3LeggedScheme;
import org.opensocial.auth.SecurityTokenScheme;
import org.opensocial.providers.OrkutProvider;
import org.opensocial.providers.Provider;

/**
 * The test suite defines the Provider, AuthScheme, Protocol (RPC/REST)
 * combination to run the test cases in BaseRequestTest.
 *
 * @author Guibin Kong
 *
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
  RequestTestSuite.Orkut2LeggedRestTest.class,
  RequestTestSuite.Orkut2LeggedRpcTest.class,

  RequestTestSuite.Orkut3LeggedRpcTest.class,
  RequestTestSuite.Orkut3LeggedRestTest.class,

  RequestTestSuite.OrkutSecurityTokenRpcTest.class,
  RequestTestSuite.OrkutSecurityTokenRestTest.class
})
public class RequestTestSuite {

  public static class Orkut2LeggedRpcTest extends BaseRequestTest {
    @BeforeClass
    public static void init() {
      client = new Client(new OrkutProvider(false), new OAuth2LeggedScheme(
          "consumerKey", "consumerSecret"));
    }
  }

  public static class Orkut2LeggedRestTest extends BaseRequestTest {
    @BeforeClass
    public static void init() {
      client = new Client(new OrkutProvider(true), new OAuth2LeggedScheme(
          "consumerKey", "consumerSecret"));
    }
  }

  public static class Orkut3LeggedRpcTest extends BaseRequestTest {
    @BeforeClass
    public static void init() {
      Provider p = new OrkutProvider(false);
      client = new Client(p, new OAuth3LeggedScheme(p, "consumerKey",
          "consumerSecret"));
    }
  }

  public static class Orkut3LeggedRestTest extends BaseRequestTest {
    @BeforeClass
    public static void init() {
      Provider p = new OrkutProvider(true);
      client = new Client(p, new OAuth3LeggedScheme(p, "consumerKey",
          "consumerSecret"));
    }
  }

  public static class OrkutSecurityTokenRpcTest extends BaseRequestTest {
    @BeforeClass
    public static void init() {
      client = new Client(new OrkutProvider(false), new SecurityTokenScheme(
          "securityToken"));
    }
  }

  public static class OrkutSecurityTokenRestTest extends BaseRequestTest {
    @BeforeClass
    public static void init() {
      client = new Client(new OrkutProvider(true), new SecurityTokenScheme(
          "securityToken"));
    }
  }
}

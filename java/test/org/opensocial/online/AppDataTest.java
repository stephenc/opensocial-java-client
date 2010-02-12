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

package org.opensocial.online;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opensocial.Client;
import org.opensocial.Request;
import org.opensocial.Response;
import org.opensocial.auth.OAuth2LeggedScheme;
import org.opensocial.models.AppData;
import org.opensocial.providers.OrkutSandboxProvider;
import org.opensocial.services.AppDataService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AppDataTest {

  private static final String ORKUT_KEY = "orkut.com:623061448914";
  private static final String ORKUT_SECRET = "uynAeXiWTisflWX99KU1D2q5";
  private static final String ORKUT_ID = "03067092798963641994";

  @BeforeClass
  public static void setup() {
    Client client = new Client(new OrkutSandboxProvider(),
        new OAuth2LeggedScheme(ORKUT_KEY, ORKUT_SECRET, ORKUT_ID));
    try {
      Request request = AppDataService.updateAppData("java", "rocks");
      client.send(request);
    } catch (Exception e) {
    }
  }

  @Test
  public void retrieveFromOrkutUsingRpc() {
    retrieveFromOrkut(false);
  }

  @Test
  public void retrieveFromOrkutUsingRest() {
    retrieveFromOrkut(true);
  }

  private void retrieveFromOrkut(boolean useRest) {
    try {
      Client client = new Client(new OrkutSandboxProvider(useRest),
          new OAuth2LeggedScheme(ORKUT_KEY, ORKUT_SECRET, ORKUT_ID));
      Request request = AppDataService.getAppData();
      Response response = client.send(request);

      AppData data = response.getEntry();
      assertTrue(data.hasField(ORKUT_ID));
    } catch (Exception e) {
      fail("Exception occurred while processing request");
    }
  }

  @Test
  public void singleUpdateAndRetrieveFromOrkutUsingRpc() {
    singleUpdateAndRetrieveFromOrkut(false);
  }

  @Test
  public void singleUpdateAndRetrieveFromOrkutUsingRest() {
    singleUpdateAndRetrieveFromOrkut(true);
  }

  private void singleUpdateAndRetrieveFromOrkut(boolean useRest) {
    Random generator = new Random();
    String randomValue = String.valueOf(generator.nextInt());

    Client client = new Client(new OrkutSandboxProvider(useRest),
        new OAuth2LeggedScheme(ORKUT_KEY, ORKUT_SECRET, ORKUT_ID));

    try {
      Request request = AppDataService.updateAppData("key", randomValue);
      client.send(request);
    } catch (Exception e) {
      fail("Exception occurred while processing update request");
    }

    try {
      Request request = AppDataService.getAppData();
      Response response = client.send(request);

      AppData data = response.getEntry();
      assertTrue(data.getDataForUser(ORKUT_ID, "key").equals(randomValue));
    } catch (Exception e) {
      fail("Exception occurred while processing retrieve request");
    }

    try {
      Request request = AppDataService.deleteAppData("key");
      client.send(request);
    } catch (Exception e) {
      fail("Exception occurred while processing delete request");
    }

    try {
      Request request = AppDataService.getAppData();
      Response response = client.send(request);

      AppData data = response.getEntry();
      assertTrue(data.hasField(ORKUT_ID));
      assertTrue(data.getDataForUser(ORKUT_ID, "key") == null);
      assertTrue(data.getDataForUser(ORKUT_ID, "java") != null);
    } catch (Exception e) {
      fail("Exception occurred while processing retrieve request");
    }
  }

  @Test
  public void multiUpdateRetrieveAndDeleteFromOrkutUsingRpc() {
    multiUpdateRetrieveAndDeleteFromOrkut(false);
  }

  @Test
  public void multiUpdateRetrieveAndDeleteFromOrkutUsingRest() {
    multiUpdateRetrieveAndDeleteFromOrkut(true);
  }

  private void multiUpdateRetrieveAndDeleteFromOrkut(boolean useRest) {
    Random generator = new Random();
    String randomValue1 = String.valueOf(generator.nextInt());
    String randomValue2 = String.valueOf(generator.nextInt());

    Client client = new Client(new OrkutSandboxProvider(useRest),
        new OAuth2LeggedScheme(ORKUT_KEY, ORKUT_SECRET, ORKUT_ID));

    try {
      Map<String, String> data = new HashMap<String, String>();
      data.put("key1", randomValue1);
      data.put("key2", randomValue2);

      Request request = AppDataService.updateAppData(data);
      client.send(request);
    } catch (Exception e) {
      fail("Exception occurred while processing update request");
    }

    try {
      Request request = AppDataService.getAppData();
      Response response = client.send(request);

      AppData data = response.getEntry();
      assertTrue(data.getDataForUser(ORKUT_ID, "key1").equals(randomValue1));
      assertTrue(data.getDataForUser(ORKUT_ID, "key2").equals(randomValue2));
    } catch (Exception e) {
      fail("Exception occurred while processing retrieve request");
    }

    try {
      Request request = AppDataService.deleteAppData(
          new String[] {"key1", "key2"});
      client.send(request);
    } catch (Exception e) {
      fail("Exception occurred while processing delete request");
    }

    try {
      Request request = AppDataService.getAppData();
      Response response = client.send(request);

      AppData data = response.getEntry();
      assertTrue(data.hasField(ORKUT_ID));
      assertTrue(data.getDataForUser(ORKUT_ID, "key1") == null);
      assertTrue(data.getDataForUser(ORKUT_ID, "key2") == null);
      assertTrue(data.getDataForUser(ORKUT_ID, "java") != null);
    } catch (Exception e) {
      fail("Exception occurred while processing retrieve request");
    }
  }
}

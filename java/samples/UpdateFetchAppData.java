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


import org.opensocial.client.OpenSocialClient;
import org.opensocial.data.OpenSocialAppData;
import org.opensocial.providers.OrkutSandboxProvider;

import java.util.Random;

public class UpdateFetchAppData {

  public static void main(String[] args) {
    Random generator = new Random();

    // Create a new OpenSocialClient instance configured to hit orkut endpoints;
    // other pre-configured providers include MYSPACE, GOOGLE, and PLAXO
    OpenSocialClient c =
      new OpenSocialClient(new OrkutSandboxProvider());
    c.setProperty(OpenSocialClient.Property.DEBUG, "true");

    if (args.length > 0 && args[0].equalsIgnoreCase("REST")) {
      c.setProperty(OpenSocialClient.Property.RPC_ENDPOINT, null);
    }

    // Credentials provided here are associated with the gadget located at
    // http://opensocial-resources.googlecode.com/svn/samples/rest_rpc/sample.xml;
    // If you install this gadget, you can substitute your own OpenSocial ID
    // for the one used below and update and fetch your own app data keys
    c.setProperty(OpenSocialClient.Property.CONSUMER_SECRET,
        "uynAeXiWTisflWX99KU1D2q5");
    c.setProperty(OpenSocialClient.Property.CONSUMER_KEY,
        "orkut.com:623061448914");
    c.setProperty(OpenSocialClient.Property.VIEWER_ID,
        "03067092798963641994");
    try {
      // Update app data for key "key1" -- store a random number under the
      // given "key1" for the current user
      c.updatePersonAppData("key1", String.valueOf(generator.nextInt()));

      // Retrieve all key-value pairs stored as app data for the specified user
      // including the pair added above
      OpenSocialAppData appData = c.fetchPersonAppData("03067092798963641994");

      System.out.println("----------");

      // Print all app data values associated with the user
      for (String key : appData.getFieldNamesForUser("03067092798963641994")) {
        System.out.println(key + ": " +
            appData.getStringForUser("03067092798963641994", key));
      }

      System.out.println("----------");

    } catch (org.opensocial.client.OpenSocialRequestException e) {
      System.out.println("OpenSocialRequestException thrown: " + e.getMessage());
      e.printStackTrace();
    } catch (org.opensocial.data.OpenSocialException e) {
      System.out.println("OpenSocialException thrown: " + e.getMessage());
      e.printStackTrace();
    } catch (java.io.IOException e) {
      System.out.println("IOException thrown: " + e.getMessage());
      e.printStackTrace();
    }
  }

}
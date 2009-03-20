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
import org.opensocial.client.OpenSocialProvider;
import org.opensocial.data.OpenSocialAppData;

import java.util.Collection;

public class UpdateFetchAppData {

  public static void main(String[] args) {
    // Create a new OpenSocialClient instance configured to hit orkut endpoints;
    // other pre-configured providers include MYSPACE, GOOGLE, and PLAXO
    OpenSocialClient c = new OpenSocialClient(OpenSocialProvider.valueOf("ORKUT"));
    c.setProperty(OpenSocialClient.Properties.DEBUG, "true");

    if (args.length > 0 && args[0].equalsIgnoreCase("REST")) {
      c.setProperty(OpenSocialClient.Properties.RPC_ENDPOINT, null);
      c.setProperty(OpenSocialClient.Properties.REST_BASE_URI,
          "http://sandbox.orkut.com/social/rest/");
    }

    // Credentials provided here are associated with the gadget located at
    // http://opensocial-resources.googlecode.com/svn/samples/rest_rpc/sample.xml;
    // If you install this gadget, you can substitute your own OpenSocial ID
    // for the one used below and fetch your profile data and friends
    c.setProperty(OpenSocialClient.Properties.CONSUMER_SECRET,
        "uynAeXiWTisflWX99KU1D2q5");
    c.setProperty(OpenSocialClient.Properties.CONSUMER_KEY,
        "orkut.com:623061448914");
    c.setProperty(OpenSocialClient.Properties.VIEWER_ID,
        "03067092798963641994");

    try {
      // Update app data for key "key1" -- store a random number under the
      // given "key1" for the current user
      c.updatePersonAppData("key1", "fake");

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
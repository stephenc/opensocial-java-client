/* Copyright (c) 2008 Google Inc.
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
import org.opensocial.data.OpenSocialPerson;
import org.opensocial.providers.MySpaceProvider;

import java.util.Collection;

public class DisplayFriends {

  public static void main(String[] args) {
    // Create a new OpenSocialClient
    // other pre-configured providers include MYSPACE, GOOGLE, and PLAXO and ORKUT"
    OpenSocialClient c =
      new OpenSocialClient(new MySpaceProvider());
    c.setProperty(OpenSocialClient.Property.DEBUG, "true");
    c.setProperty(OpenSocialClient.Property.RPC_ENDPOINT, null);

    // Credentials provided here are associated with the gadget located at
    // http://opensocial-resources.googlecode.com/svn/samples/rest_rpc/sample.xml;
    // If you install this gadget, you can substitute your own OpenSocial ID
    // for the one used below and fetch your profile data and friends
    c.setProperty(OpenSocialClient.Property.CONSUMER_SECRET,
        "75c76022d8db4d5a817237562e163089");
    c.setProperty(OpenSocialClient.Property.CONSUMER_KEY,
        "http://www.myspace.com/435305524");
    c.setProperty(OpenSocialClient.Property.VIEWER_ID,
        "495184236");

    try {
      // Retrieve the friends of the specified user using the OpenSocialClient
      // method fetchFriends
      Collection<OpenSocialPerson> friends =
        c.fetchFriends();
      
      System.out.println("----------");
      
      // The fetchFriends method returns a typical Java Collection object with
      // all of the methods you're already accustomed to like size()
      System.out.println(friends.size() + " friends:");

      // Iterate through the Collection
      for (OpenSocialPerson friend : friends) {
        // Output the name of the current friend
        System.out.println("- " + friend.getDisplayName());
      }

      System.out.println("----------");

    } catch (org.opensocial.client.OpenSocialRequestException e) {
      System.out.println("OpenSocialRequestException thrown: " + e.getMessage());
      e.printStackTrace();
    } catch (java.io.IOException e) {
      System.out.println("IOException thrown: " + e.getMessage());
      e.printStackTrace();
    }
  }

}
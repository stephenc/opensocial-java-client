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


import org.opensocial.client.OpenSocialBatch;
import org.opensocial.client.OpenSocialClient;
import org.opensocial.client.OpenSocialResponse;
import org.opensocial.data.OpenSocialField;
import org.opensocial.data.OpenSocialPerson;
import org.opensocial.providers.OrkutSandboxProvider;

import java.util.Collection;

public class BatchRequests {

  public static void main(String[] args) {
    // Create a new OpenSocialClient instance configured to hit orkut endpoints;
    // other pre-configured providers include MYSPACE, GOOGLE, and PLAXO
    OpenSocialClient c =
      new OpenSocialClient(new OrkutSandboxProvider());
    c.setProperty(OpenSocialClient.Property.DEBUG, "true");

    // Credentials provided here are associated with the gadget located at
    // http://opensocial-resources.googlecode.com/svn/samples/rest_rpc/sample.xml;
    // If you install this gadget, you can substitute your own OpenSocial ID
    // for the one used below and fetch your profile data and friends
    c.setProperty(OpenSocialClient.Property.CONSUMER_SECRET,
        "uynAeXiWTisflWX99KU1D2q5");
    c.setProperty(OpenSocialClient.Property.CONSUMER_KEY,
        "orkut.com:623061448914");
    c.setProperty(OpenSocialClient.Property.VIEWER_ID,
        "03067092798963641994");

    try {
      // OpenSocialBatch objects are used to bulk multiple OpenSocial data
      // requests so they can be submitted to the container (orkut in this
      // case) in a single HTTP request
      OpenSocialBatch batch = new OpenSocialBatch();
      
      // Add two data requests to the batch object, one for the profile details
      // and the other for the friends of the specified user; each request is
      // identified by a unique String ID so the results can be accessed in
      // the response returned
      batch.addRequest(
          OpenSocialClient.newFetchPersonRequest("03067092798963641994"),
          "person");
      batch.addRequest(
          OpenSocialClient.newFetchFriendsRequest("03067092798963641994"),
          "friends");
      
      // Submit the batch request -- the JSON returned by the container is
      // parsed into an OpenSocialResponse object
      OpenSocialResponse resp = batch.send(c);
      
      // Retrieve the individual components of the submitted batch -- an
      // OpenSocialPerson object representing the requested person and a
      // Collection of these objects representing the requested friends;
      // provide the request ID that you specified when you added the
      // request to the batch
      OpenSocialPerson person = resp.getItemAsPerson("person");
      Collection<OpenSocialPerson> friends =
        resp.getItemAsPersonCollection("friends");
      
      System.out.println("----------");
      
      // Output the name and ID of the requested person;
      System.out.println("Info. for " + person.getDisplayName());
      System.out.println("ID: " + person.getId());      
      
      // Retrieve individual fields using the getField method; fields may be
      // complex (objects or arrays) or simple (Strings), which you can
      // determine by querying the object using the isComplex method.
      // The thumbnail URL should be a simple field, so we'll output
      // the value as a String
      OpenSocialField thumbnailUrlField = person.getField("thumbnailUrl");
      if (!thumbnailUrlField.isComplex()) {
        System.out.println("Thumbnail URL: " +
            thumbnailUrlField.getStringValue());        
      }
      
      System.out.println("Friends:");
      
      // Iterate through the returned Collection
      for (OpenSocialPerson friend : friends) {
        // Output the name of the current person
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
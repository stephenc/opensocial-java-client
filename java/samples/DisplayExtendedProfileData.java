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
import org.opensocial.providers.OrkutSandboxProvider;
import org.opensocial.data.OpenSocialPerson;

import java.util.HashMap;

public class DisplayExtendedProfileData {

  public static void main(String[] args) {
    
    // Setup Client
    OrkutSandboxProvider provider = new OrkutSandboxProvider();
    //provider.rpcEndpoint = null;
    
    OpenSocialClient c = new OpenSocialClient(provider);
    c.setProperty(OpenSocialClient.Property.DEBUG, "false");
    
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
      // Create a new OpenSocialRequestParameterSet object to indicate which request
      // parameters you want to pass with your request; in this case, we are asking
      // orkut specifically to return the name and gender fields for the person
      // fetched below.
      HashMap<String, String> params = new HashMap<String, String>();
      params.put("fields", "gender,name");

      // Retrieve the profile data of the specified user using the OpenSocialClient
      // method fetchPerson; pass in the parameter set object created above
      OpenSocialPerson person = c.fetchPerson("03067092798963641994", params);

      System.out.println("----------");

      // Output the name, ID, and gender of the requested person
      System.out.println("ID: " + person.getField("id"));
      System.out.println("Name: " + person.getField("name"));
      System.out.println("Display Name: " + person.getDisplayName());
      System.out.println("Gender: " + person.getField("gender"));

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
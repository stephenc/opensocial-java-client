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
import org.opensocial.client.OpenSocialProvider;
import org.opensocial.data.OpenSocialField;
import org.opensocial.data.OpenSocialPerson;

import java.util.Collection;

public class DisplayProfileData {

  public static void main(String[] args) {
    // Create a new OpenSocialClient instance configured to hit orkut endpoints;
    // other pre-configured providers include MYSPACE, GOOGLE, and PLAXO
    OpenSocialClient c = new OpenSocialClient(OpenSocialProvider.valueOf("ORKUT"));
    
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
      // Retrieve the profile data of the specified user using the
      // OpenSocialClient method fetchPerson
      OpenSocialPerson person = c.fetchPerson("03067092798963641994");
      
      System.out.println("----------");
      
      // Output the name and ID of the requested person
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

      System.out.println("----------");

    } catch (Exception e) {
      System.out.println("Request failed:" );
      e.printStackTrace();
    }
  }

}
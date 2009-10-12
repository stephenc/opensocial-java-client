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
import org.opensocial.client.OpenSocialHttpResponseMessage;
import org.opensocial.data.OpenSocialPerson;
import org.opensocial.providers.MySpaceProvider;

import java.util.List;
import java.util.HashMap;
import java.util.Set;

public class MySpacePeopleExample {

  public static void main(String[] args) {
    
    // Setup Client
    OpenSocialClient c = new OpenSocialClient(new MySpaceProvider());
    c.setProperty(OpenSocialClient.Property.DEBUG, "false");
    c.setProperty(OpenSocialClient.Property.RPC_ENDPOINT, null);
    c.setProperty(OpenSocialClient.Property.CONSUMER_SECRET, "20ab52223e684594a8050a8bfd4b06693ba9c9183ee24e1987be87746b1b03f8");
    c.setProperty(OpenSocialClient.Property.CONSUMER_KEY, "http://www.myspace.com/495182150");
    c.setProperty(OpenSocialClient.Property.VIEWER_ID, "495184236");
    
    // Create Batch handler
    OpenSocialBatch batch = new OpenSocialBatch();
    HashMap<String, String> params = null;
    
    try {
      // Request people self
      params = new HashMap<String, String>();
      params.put("fields", "@all");
      params.put("groupId", OpenSocialClient.SELF);
      batch.addRequest(c.getPeopleService().get(params), "self");
      
      // Request people friends
      params = new HashMap<String, String>();
      params.put("fields", "@all");
      params.put("groupId", OpenSocialClient.FRIENDS);
      batch.addRequest(c.getPeopleService().get(params), "friends");
      
      //supportedFields
      batch.addRequest(c.getPeopleService().getSupportedFields(), "supportedFields");
      
      batch.send(c);
          
      // Get a list of all response in request queue
      Set<String> responses = batch.getResponseQueue();
      OpenSocialPerson person = new OpenSocialPerson();
      
      // Interate through each response
      for(Object id : responses) {
          OpenSocialHttpResponseMessage resp = batch.getResponse(id.toString());
          System.out.println("\n"+id+" responded with status: "+resp.getStatusCode()+" with "+resp.getTotalResults()+" results");
          System.out.println("==============================================");
          
          if(id.toString().equals("supportedFields")) {
            List<String> supportedFields = resp.getSupportedFields();
            
            for(int i=0; i < supportedFields.size(); i++) {
              System.out.println(supportedFields.get(i));
            }
          }else{
            List<OpenSocialPerson> people = resp.getPersonCollection();

            for(int i=0; i < people.size(); i++) {
              person = people.get(i);
              System.out.println("Is Location Complex ?: "+person.isFieldComplex("currentLocation") + " value: "+person.getField("currentLocation"));
              System.out.println(person.getField("id"));
              System.out.println(person.getField("displayName"));
              System.out.println("App Installed: "+person.getField("hasApp"));
              System.out.println("--------------------------------------");
            }
          }
      }
    } catch (org.opensocial.client.OpenSocialRequestException e) {
      System.out.println("OpenSocialRequestException thrown: " + e.getMessage());
      e.printStackTrace();
    } catch (java.io.IOException e) {
      System.out.println("IOException thrown: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
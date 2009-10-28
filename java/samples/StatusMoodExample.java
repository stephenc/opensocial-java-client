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
import org.opensocial.data.MySpaceStatusMood;
import org.opensocial.providers.MySpaceProvider;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class StatusMoodExample {

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
    Map<String, String> params = null;
    
    try {
      // Fetch statusMood
      params = new HashMap<String, String>();
      params.put("userId", "495184236");
      params.put("groupId", "@self");
      batch.addRequest(c.getStatusMoodService().get(params), "fetchStatusMood");
      // End fetchStatusMood
      
      // Update statusMood
      MySpaceStatusMood sm = new MySpaceStatusMood();
      sm.setField("status", "Working on the Java SDK");
      sm.setField("moodId", 90);
      
      params = new HashMap<String, String>();
      params.put("userId", "495184236");
      params.put("groupId", "@self");
      params.put("statusMood", sm.toString());
      batch.addRequest(c.getStatusMoodService().update(params), "updateStatusMood");
      // End update StatusMood
      
      // Fetch supportedMood 
      params = new HashMap<String, String>();
      params.put("moodId", "90");
      batch.addRequest(c.getStatusMoodService().getSupportedMoods(params), "fetchStatusMoodsSingle");
      // End Fetch StatusMood
      
      // Fetch supportedMoods 
      batch.addRequest(c.getStatusMoodService().getSupportedMoods(), "fetchStatusMoods");
      // End Fetch StatusMoods
      
      // Fetch statusMood history self
      params = new HashMap<String, String>();
      params.put("userId", "495184236");
      params.put("groupId", "@self");
      batch.addRequest(c.getStatusMoodService().getHistory(params), "fetchHistorySelf");
      // End fetchStatusMood history self
      
      // Fetch statusMood history friends
      params = new HashMap<String, String>();
      params.put("userId", "495184236");
      params.put("groupId", "@friends");
      batch.addRequest(c.getStatusMoodService().getHistory(params), "fetchHistoryFriends");
      // End fetchStatusMood friends
      
      // Fetch statusMood history specific friend
      params = new HashMap<String, String>();
      params.put("userId", "495184236");
      params.put("groupId", "@friends");
      params.put("friendId", "myspace.com.person.63129100");
      batch.addRequest(c.getStatusMoodService().getHistory(params), "fetchHistoryFriend");
      // End fetchStatusMood history specific friend
      
      batch.send(c);
      // Get a list of all response in request queue
      Set<String> responses = batch.getResponseQueue();
      
      // Interate through each response
      for(Object id : responses) {
        OpenSocialHttpResponseMessage resp = batch.getResponse(id.toString());
        System.out.println("\n"+id+" responded with status: "+resp.getStatusCode()+" with "+resp.getTotalResults()+" results");
        System.out.println("==============================================");
        
        if(resp.getStatusCode() > 201) {
          System.out.println(resp.getBodyString());
        }

        List<MySpaceStatusMood> statusMoods = resp.getStatusMoodCollection();
        MySpaceStatusMood statusMood = new MySpaceStatusMood();
        
        for(int i=0; i < statusMoods.size(); i++) {
          statusMood = statusMoods.get(i);
          System.out.println(statusMood.getField("status"));
          System.out.println(statusMood.getField("moodId"));
          System.out.println(statusMood.getField("moodName"));
          System.out.println(statusMood.getField("moodPictureUrl"));
          System.out.println("-----------------------------");
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
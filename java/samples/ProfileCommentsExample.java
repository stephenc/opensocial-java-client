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
import org.opensocial.data.MySpaceComment;
import org.opensocial.providers.MySpaceProvider;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class ProfileCommentsExample {

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
      params = new HashMap<String, String>();
      params.put("userId", "@me");
      params.put("groupId", "@self");
      
      batch.addRequest(c.getProfileCommentsService().get(params), "fetchProfileComment");
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
        }else {
          List<MySpaceComment> comments = resp.getCommentCollection();
          MySpaceComment comment = null;
          System.out.println(resp.getBodyString());
          for(int i=0; i < comments.size(); i++) {
            comment = comments.get(i);
            System.out.println(comment.getField("postedDate"));
            System.out.println(comment.getField("commentId"));
            System.out.println(comment.getField("body"));
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
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

import org.opensocial.client.OpenSocialBatch;
import org.opensocial.client.OpenSocialClient;
import org.opensocial.client.OpenSocialHttpResponseMessage;
import org.opensocial.data.OpenSocialAppData;
import org.opensocial.providers.OrkutSandboxProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class OrkutAppDataExample {

  public static void main(String[] args) {

    // Setup Client
    OrkutSandboxProvider provider = new OrkutSandboxProvider();
    provider.rpcEndpoint = null;
    
    OpenSocialClient c = new OpenSocialClient(provider);
    c.setProperty(OpenSocialClient.Property.DEBUG, "false");
    c.setProperty(OpenSocialClient.Property.RPC_ENDPOINT, null);
    c.setProperty(OpenSocialClient.Property.CONSUMER_SECRET, "uynAeXiWTisflWX99KU1D2q5");
    c.setProperty(OpenSocialClient.Property.CONSUMER_KEY, "orkut.com:623061448914");
    c.setProperty(OpenSocialClient.Property.VIEWER_ID, "03067092798963641994");
    
    // Create Batch handler
    OpenSocialBatch batch = new OpenSocialBatch();
    Map<String, String> params = null;
    OpenSocialAppData data = new OpenSocialAppData();
    
    Random generator = new Random();
    
    try {
      // Fetch AppData Self
      params = new HashMap<String, String>();
      params.put("userId", "@viewer");
      params.put("groupId", OpenSocialClient.SELF);
      params.put("appId", "@app");
      params.put("fields", "baz,foo");
      batch.addRequest(c.getAppDataService().get(params), "1 fetchAppDataSelf");
      // End AppData Self
      
      // Fetch AppData Friends
      params = new HashMap<String, String>();
      params.put("userId", "@viewer");
      params.put("groupId", OpenSocialClient.FRIENDS);
      params.put("appId", "@app");
      batch.addRequest(c.getAppDataService().get(params), "2 fetchAppDataFriends");
      // End AppData Friends
      
      // Create AppData Self
      data = new OpenSocialAppData();
      data.setField("foo", "bar");
      data.setField("baz", "boz");
      params = new HashMap<String, String>();
      params.put("userId", "@viewer");
      params.put("groupId", OpenSocialClient.SELF);
      params.put("appId", "@app");
      params.put("appdata", data.toString());
      batch.addRequest(c.getAppDataService().create(params), "3 createAppData");
      // End CreateAppData
      
      // Fetch AppData Self
      params = new HashMap<String, String>();
      params.put("userId", "@viewer");
      params.put("groupId", OpenSocialClient.SELF);
      params.put("appId", "@app");
      params.put("fields", "baz,foo");
      batch.addRequest(c.getAppDataService().get(params), "4 fetchAppDataAfterCreate");
      // End AppData Self
      
      // Update AppData Self
      data = new OpenSocialAppData();
      data.setField("foo", generator.nextInt());
      
      params = new HashMap<String, String>();
      params.put("userId", "@viewer");
      params.put("groupId", OpenSocialClient.SELF);
      params.put("appId", "@app");
      params.put("appdata", data.toString());
      batch.addRequest(c.getAppDataService().update(params), "5 updateAppData");
      // End UpdateAppData
      
      // Fetch AppData Self
      params = new HashMap<String, String>();
      params.put("userId", "@viewer");
      params.put("groupId", OpenSocialClient.SELF);
      params.put("appId", "@app");
      params.put("fields", "baz,foo");
      batch.addRequest(c.getAppDataService().get(params), "6 fetchAppDataSelfAfterUpdate");
      // End AppData Self
      
      // Delete AppData Self
      params = new HashMap<String, String>();
      params.put("userId", "@viewer");
      params.put("groupId", OpenSocialClient.SELF);
      params.put("appId", "@app");
      params.put("fields", "baz,foo");
      batch.addRequest(c.getAppDataService().delete(params), "7 deleteAppData");
      // End DeleteAppData
      
      // Fetch AppData Self
      params = new HashMap<String, String>();
      params.put("userId", "@viewer");
      params.put("groupId", OpenSocialClient.SELF);
      params.put("appId", "@app");
      params.put("fields", "baz,foo");
      batch.addRequest(c.getAppDataService().get(params), "8 fetchAppDataSelfAfterDelete");
      // End AppData Self
      
      batch.send(c);
      
      // Get a list of all response in request queue
      Set<String> responses = batch.getResponseQueue();
      
      // Interate through each response
      for(Object id : responses) {
        OpenSocialHttpResponseMessage resp = batch.getResponse(id.toString());
        System.out.println("\n"+id+" responded with status: "+
            resp.getStatusCode()+" with "+resp.getTotalResults()+" results");
        System.out.println("==============================================");
        
        if(resp.getStatusCode() > 201) {
          System.out.println(resp.getBodyString());
        }
        
        if(!id.toString().equals("deleteAppData")) {
          List<OpenSocialAppData> appData = resp.getAppDataCollection();
          for(int i=0; i < appData.size(); i++) {
            data = appData.get(i);
            ArrayList<String> keys = data.getAppDataKeys();
            System.out.println("PersonId: "+data.getPersonId());
            for(int j=0; j< keys.size(); j++) {
              System.out.println(keys.get(j)+ " = "+data.getField(keys.get(j)));
            }
          }
        }
      }
    } catch (org.opensocial.client.OpenSocialRequestException e) {
      System.out.println("OpenSocialRequestException thrown: " + e.getMessage());
      e.printStackTrace();
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

}
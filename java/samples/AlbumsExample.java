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
import org.opensocial.data.OpenSocialAlbum;
import org.opensocial.providers.MySpaceProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class AlbumsExample {

  public static void main(String[] args) {
	  
	  // Setup Client
	  OpenSocialClient c = new OpenSocialClient(new MySpaceProvider());
	  c.setProperty(OpenSocialClient.Property.DEBUG, "true");
	  c.setProperty(OpenSocialClient.Property.RPC_ENDPOINT, null);
	  c.setProperty(OpenSocialClient.Property.CONSUMER_SECRET, "20ab52223e684594a8050a8bfd4b06693ba9c9183ee24e1987be87746b1b03f8");
	  c.setProperty(OpenSocialClient.Property.CONSUMER_KEY, "http://www.myspace.com/495182150");
	  
	  // Create Batch handler
	  OpenSocialBatch batch = new OpenSocialBatch();
	  Map<String, String> params = null;
	  
	  try {
    	
		  // Fetch Albums
		  params = new HashMap<String, String>();
		  params.put("userId", "495184236");
		  params.put("groupId", OpenSocialClient.SELF);
		  params.put("startIndex", "1");
		  params.put("count", "2");
		  params.put("fields", "@all");
		  batch.addRequest(c.getAlbumsService().get(params), "fetchAlbums");
		  // End Fetch Albums
		  
		  // Fetch Album
		  params = new HashMap<String, String>();
		  params.put("userId", "495184236");
		  params.put("groupId", OpenSocialClient.SELF);
		  params.put("albumId", "myspace.com.album.81886");
		  params.put("fields", "@all");
		  batch.addRequest(c.getAlbumsService().get(params), "fetchAlbum");
		  // End Fetch Album
		  
		  // Create Album
		  OpenSocialAlbum album = new OpenSocialAlbum();
          album.setField("caption", "value");
          album.setField("description", "my description goes here");
          
          params = new HashMap<String, String>();
          params.put("userId", "495184236");
          params.put("groupId", OpenSocialClient.SELF);
          params.put("album", album.toString());
          
          // Commented out so that each run doesn't create an album.
          //batch.addRequest(c.getAlbumsService().create(params), "createAlbum");
          // End Create Album
          
          // Update Album
          album = new OpenSocialAlbum();
          album.setField("caption", "This is my updated caption");
          album.setField("description", "my description goes here");
          
          params = new HashMap<String, String>();
          params.put("userId", "495184236");
          params.put("groupId", OpenSocialClient.SELF);
          params.put("album", album.toString());
          params.put("albumId", "myspace.com.album.81886");
          //batch.addRequest(c.getAlbumsService().update(params), "updateAlbum");
          // End Update Album
          
          //supportedFields
          //batch.addRequest(c.getAlbumsService().getSupportedFields(), "supportedFields");
          
          batch.send(c);
          
          // Get a list of all response in request queue
          Set<String> responses = batch.getResponseQueue();
          
          // Interate through each response
          for(Object id : responses) {
              OpenSocialHttpResponseMessage msg = batch.getResponse(id.toString());
              System.out.println("\n"+id.toString()+" with response code ("+msg.getStatusCode()+")");
              //System.out.println(msg.getBodyString());
              System.out.println("==================================================");
              
              //TODO: move this logic into OpenSocialHttpResonseMessage so we can use something like
              // response.getAlbum() or response.getAlbumCollection() or even more generic response.getCollection()
              JSONObject obj = new JSONObject(msg.getBodyString());
              if(obj.has("entry") ){
                  JSONArray entry = obj.getJSONArray("entry");
                  for(int i=0; i<entry.length(); i++) {
                      album = new OpenSocialAlbum(entry.getJSONObject(i).getJSONObject("album").toString());
                      System.out.println("album id: "+album.getField("id"));
                  }
              } else if(obj.has("album")) {
                  album = new OpenSocialAlbum(obj.getJSONObject("album").toString());
                  System.out.println("album id: "+album.getField("id"));
                  System.out.println("album mediaItemCount: "+album.getField("mediaItemCount"));
              } else {
                  System.out.println(msg.getBodyString());
              }
          }
    } catch (org.opensocial.client.OpenSocialRequestException e) {
      System.out.println("OpenSocialRequestException thrown: " + e.getMessage());
      e.printStackTrace();
    } catch (java.io.IOException e) {
      System.out.println("IOException thrown: " + e.getMessage());
      e.printStackTrace();
    }catch (JSONException e){
        System.out.println("IOException thrown: " + e.getMessage());
        e.printStackTrace();
      }
  }

}
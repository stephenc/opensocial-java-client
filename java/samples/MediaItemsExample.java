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
import org.opensocial.client.OpenSocialRequestException;
import org.opensocial.data.OpenSocialMediaItem;
import org.opensocial.providers.MySpaceProvider;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class MediaItemsExample {

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
	  OpenSocialMediaItem mediaItem = new OpenSocialMediaItem();
	  
	  try {
      updateMediaItem(batch, c);
      createMediaItem(batch, c);
      //uploadMediaItem(batch, c);
      batch.addRequest(c.getMediaItemsService().getSupportedFields(), "supportedFields");
	      
		  // Fetch MediaItems
		  params = new HashMap<String, String>();
		  params.put("userId", "495184236");
		  params.put("groupId", OpenSocialClient.SELF);
		  params.put("startIndex", "1");
		  params.put("count", "2");
		  params.put("fields", "@all");
		  params.put("albumId", "myspace.com.album.81886");
		  batch.addRequest(c.getMediaItemsService().get(params), "fetchMediaItems");
		  // End Fetch MediaItems
		  
		  // Fetch MediaItem
		  params = new HashMap<String, String>();
		  params.put("userId", "495184236");
		  params.put("groupId", OpenSocialClient.SELF);
		  params.put("fields", "@all");
		  params.put("albumId", "myspace.com.album.81886");
		  params.put("mediaItemId", "myspace.com.mediaItem.image.646364");
		  batch.addRequest(c.getMediaItemsService().get(params), "fetchMediaItem");
		  // End Fetch MediaItem
		  
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
          if(id.toString().equals("supportedFields")) {
            List<String> supportedFields = resp.getSupportedFields();
            
            for(int i=0; i < supportedFields.size(); i++) {
              System.out.println(supportedFields.get(i));
            }
          }else{
            List<OpenSocialMediaItem> mediaItems = resp.getMediaItemCollection();
            
            for(int i=0; i < mediaItems.size(); i++) {
              mediaItem = mediaItems.get(i);
              System.out.println(mediaItem.getField("id"));
            }
          }
        }
      }
    } catch (OpenSocialRequestException e) {
      System.out.println("OpenSocialRequestException thrown: " + e.getMessage());
      e.printStackTrace();
    } catch (java.io.IOException e) {
      System.out.println("IOException thrown: " + e.getMessage());
      e.printStackTrace();
    }
  }
  
  static void updateMediaItem(OpenSocialBatch batch, OpenSocialClient c)
  throws IOException, OpenSocialRequestException {
      OpenSocialMediaItem mediaItem = new OpenSocialMediaItem();
      Map<String, String> params = null;
      
      mediaItem = new OpenSocialMediaItem();
      mediaItem.setField("title", "This is my updated caption");
      mediaItem.setField("description", "my description goes here");
      
      params = new HashMap<String, String>();
      params.put("userId", "495184236");
      params.put("groupId", OpenSocialClient.SELF);
      params.put("mediaItem", mediaItem.toString());
      params.put("albumId", "myspace.com.album.81886");
      params.put("mediaItemId", "myspace.com.mediaItem.image.646364");
      batch.addRequest(c.getMediaItemsService().update(params), "updateMediaItem");
  }
  
  static void createMediaItem(OpenSocialBatch batch, OpenSocialClient c)
  throws IOException, OpenSocialRequestException {
      OpenSocialMediaItem mediaItem = new OpenSocialMediaItem();
      Map<String, String> params = null;
      
      mediaItem.setField("caption", "value");
      mediaItem.setField("description", "my description goes here");
      
      params = new HashMap<String, String>();
      params.put("userId", "495184236");
      params.put("groupId", OpenSocialClient.SELF);
      params.put("albumId", "myspace.com.album.81886");
      params.put("mediaItem", mediaItem.toString());
      batch.addRequest(c.getMediaItemsService().create(params), "createMediaItem");
  }
  
  static void uploadMediaItem(OpenSocialBatch batch, OpenSocialClient c) 
      throws IOException, OpenSocialRequestException {
      Map<String, String> params = null;
      File f = new File("C:\\workspaces\\ms-dev\\Services\\MySpace.OpenSocial.v09\\SDKs\\Java\\java\\samples\\images.jpg");
      InputStream in = new FileInputStream(f);
      String fileContents = null;
      ByteArrayOutputStream buffer = null;
      buffer = new ByteArrayOutputStream();
      byte[] barr = new byte[1024];
      
      while(true) {
          int r = in.read(barr);
          if(r <= 0) {
              break;
          }
          buffer.write(barr, 0, r);
      }
      fileContents = new String(buffer.toString());
      in.close();
      
      params = new HashMap<String, String>(); 
      params.put("userId", "495184236");
      params.put("groupId", OpenSocialClient.SELF);
      params.put("albumId", "myspace.com.album.706610");
      params.put("mediaItem", fileContents);
      params.put("type", "image");
      params.put("contentType", "image/jpg");
      
      batch.addRequest(c.getMediaItemsService().upload(params), "uploadContent");
  }
}

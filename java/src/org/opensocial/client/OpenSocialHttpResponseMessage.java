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

package org.opensocial.client;

import net.oauth.http.HttpResponseMessage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.opensocial.data.MySpaceNotification;
import org.opensocial.data.MySpaceStatusMood;
import org.opensocial.data.OpenSocialActivity;
import org.opensocial.data.OpenSocialAlbum;
import org.opensocial.data.OpenSocialAppData;
import org.opensocial.data.OpenSocialGroup;
import org.opensocial.data.OpenSocialMediaItem;
import org.opensocial.data.OpenSocialPerson;

/**
 * A small implementation of an HttpResponseMessage that does not require
 * org.apache.http.client as a dependency.
 *
 * @author api.dwh@google.com (Dan Holevoet)
 * @author apijason@google.com (Jason Cooper)
 * @author jle.edwards@gmail.com (Jesse Edwards)
 */
public class OpenSocialHttpResponseMessage extends HttpResponseMessage {

  protected String responseBody = null;
  protected int httpStatus;
  protected int itemsPerPage;
  protected int startIndex;
  protected int totalResults;
  protected String filtered;
  protected String sorted;
  protected String updatedSince;
  
  protected String data;
  
  
  @SuppressWarnings("unchecked")
  private ArrayList osCollection;
  
  public OpenSocialHttpResponseMessage(String method, OpenSocialUrl url,
      InputStream responseStream, int status) throws IOException {
    super(method, url.toURL());
    
    httpStatus  = status;
    setBodyString(responseStream);
    formatOpenSocialResponse();
  }
  
  public OpenSocialHttpResponseMessage(String method, OpenSocialUrl url, 
      String body, int status) throws IOException {
    
    super(method, url.toURL());
    
    httpStatus = status;
    responseBody = body;
    formatOpenSocialResponse();
  }
  
  private void formatOpenSocialResponse() {
    if(responseBody.startsWith("{") && responseBody.endsWith("}")) {
      try{
        JSONObject obj = new JSONObject(responseBody);
        
        // If we are dealing with RPC
        if(obj.has("entry") && obj.getString("entry").startsWith("{")) {
          JSONObject entry = obj.getJSONObject("entry");
          
          if(entry.has("list")) {
            
            if(entry.getString("list").startsWith("[")) {
              entry.put("entry", entry.getJSONArray("list"));
            }else {
              entry.put("entry", entry.getJSONObject("list"));
            }
            
            entry.remove("list");
            obj = entry;
          }
        }
        
        itemsPerPage = obj.has("itemsPerPage") ? obj.getInt("itemsPerPage") : 0;
        obj.remove("itemsPerPage");
        
        startIndex = obj.has("startIndex") ? obj.getInt("startIndex") : 0;
        obj.remove("startIndex");
        
        totalResults = obj.has("totalResults") ? obj.getInt("totalResults") : 0;
        obj.remove("totalResults");
        
        filtered = obj.has("filtered") ? obj.getString("filtered") : "";
        obj.remove("filtered");
        
        sorted = obj.has("sorted") ? obj.getString("sorted") : "";
        obj.remove("sorted");
        
        updatedSince = obj.has("updatedSince") ? obj.getString("updatedSince") : "";
        obj.remove("updatedSince");
        
        data = obj.toString();
        
      }catch(JSONException e) {
        e.printStackTrace();
      }
    }else {
      data = responseBody;
    }
  }
  
  public void setOpenSocialDataString(String value) {
    data = value;
  }
  public String getOpenSocialDataString() {
    return data;
  }
  
  @SuppressWarnings("unchecked")
  public void setCollection(ArrayList value) {
    osCollection = value;
  }
  
  public void setItemsPerPage(int value) {
    itemsPerPage = value;
  }
  
  public void setStartIndex(int value) {
    startIndex = value;
  }
  
  public void setTotalResults(int value) {
    totalResults = value;
  }
  
  public int getItemsPerPage() {
    return itemsPerPage;
  }
  
  public int getStartIndex() {
    return startIndex;
  }
  
  public int getTotalResults() {
    return totalResults;
  }

  /**
   * Returns the status code for the response.
   *
   * @return Status code
   * @throws IOException if the status code is 0 (not set)
   */
  public int getStatusCode() {
    return httpStatus ;
  }

  /**
   * Transforms response output contained in the InputStream object returned by
   * the connection into a string representation which can later be parsed into
   * a more meaningful object, e.g. OpenSocialPerson.
   *
   */
  private void setBodyString(InputStream in) {
    try{
      if (in != null) {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while ((line = reader.readLine()) != null) {
          sb.append(line);
        }
        in.close();
        responseBody = sb.toString();
      }
    }catch(IOException e){
      e.printStackTrace();
      responseBody = "";
    }
  }
  
  public String getBodyString() {
      return responseBody;
  }
  
  public void setBodyString(String value) {
    this.responseBody = value;
  }
  
  @SuppressWarnings("unchecked")
  public ArrayList<String> getSupportedMoods() {
    return osCollection;
  }
  
  @SuppressWarnings("unchecked")
  public ArrayList<String> getSupportedFields() {
    return osCollection;
  }
  
  @SuppressWarnings("unchecked")
  public ArrayList<OpenSocialAlbum> getAlbumCollection() {
    return osCollection;
  }
  
  @SuppressWarnings("unchecked")
  public ArrayList<OpenSocialAppData> getAppDataCollection() {
    return osCollection;
  }
  
  @SuppressWarnings("unchecked")
  public ArrayList<OpenSocialActivity> getActivityCollection() {
    return osCollection;
  }
  
  @SuppressWarnings("unchecked")
  public ArrayList<OpenSocialMediaItem> getMediaItemCollection() {
    return osCollection;
  }
  
  @SuppressWarnings("unchecked")
  public ArrayList<OpenSocialPerson> getPersonCollection() {
    return osCollection;
  }
  
  @SuppressWarnings("unchecked")
  public ArrayList<MySpaceNotification> getNotificationCollection() {
    return osCollection;
  }
  
  @SuppressWarnings("unchecked")
  public ArrayList<MySpaceStatusMood> getStatusMoodCollection() {
    return osCollection;
  }
  
  @SuppressWarnings("unchecked")
  public ArrayList<OpenSocialGroup> getGroupCollection() {
    return osCollection;
  }
}

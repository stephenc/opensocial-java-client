package org.opensocial.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model object for AppData
 * @author Jesse Edwards
 */
public class OpenSocialAppData extends OpenSocialModel
{
  public OpenSocialAppData(){}

  public OpenSocialAppData(String json) throws JSONException {
    super(json);
  }

  /**
   * setField - set a field into appData.
   * @param String key
   * @param String value
   */
  public void setField(String key, String value) {
    try {
      if(!this.has("appData")) {
        this.put("appData", new JSONArray());
      }
      JSONArray appData = this.getJSONArray("appData");
      JSONObject kvp = new JSONObject();
      kvp.put("key", key);
      kvp.put("value", value);
      appData.put(kvp);
    }catch(JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * setField - set field into appData
   * @override
   * @param String key
   * @param int value
   */
  public void setField(String key, int value) {
    String str = ""+value;
    setField(key, str);
  }

  /**
   * getField - gets requested value 'key' from appData.
   * 
   * @param String key
   * @return String
   */
  public String getField(String key) {
    try {
      if(this.has("appData")) {
        JSONArray appData = this.getJSONArray("appData");
        JSONObject kvp = new JSONObject();
        
        for(int i=0; i < appData.length(); i++) {
          kvp = appData.getJSONObject(i);
          if(kvp.getString("key").equals(key)) {
            return kvp.getString("value");
          }
        }
      }
    }catch(JSONException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * setUserId - sets the userId the appData belongs to.
   * @param String value
   */
  public void setPersonId(String value) {
    try{
      this.put("personId", value);
    }catch(JSONException e) {
      e.printStackTrace();
    }
  }

  /**
   * getUserId - gets the userId the appData belongs to.
   * @return String
   */
  public String getPersonId() {
    try{
      if(this.has("personId")){
        return this.getString("personId");
      }
    }catch(JSONException e) {
      e.printStackTrace();
    }
    
    return null;
  }

  public ArrayList<String> getAppDataKeys() {
    if(this.has("appData")) {
      try {
        JSONArray data = this.getJSONArray("appData");
        ArrayList<String> keys = new ArrayList<String>();
        for(int i=0; i< data.length(); i++) {
          keys.add(data.getJSONObject(i).getString("key"));
        }
        return keys;
      } catch(JSONException e) {
        e.printStackTrace();
      }
    }
    
    return new ArrayList<String>();
  }
  /**
   * getJSONObject - returns the appData in a JSONObject format 
   * IE({"foo":"bar"}).
   * @return JSONObject
   */
  public JSONObject getJSONObject() {
    
    if(this.has("appData")) {
      try{
        JSONArray appData = this.getJSONArray("appData");
        JSONObject data = new JSONObject();
        JSONObject entry;
        
        for(int i=0; i < appData.length(); i++) {
          entry = appData.getJSONObject(i);
          data.put(entry.getString("key"), entry.getString("value"));
        }
        
        return data;
      }catch(JSONException e) {
        e.printStackTrace();
      }
    }
    return new JSONObject();
  }
}
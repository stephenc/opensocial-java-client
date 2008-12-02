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


package org.opensocial.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensocial.data.OpenSocialAppData;
import org.opensocial.data.OpenSocialField;
import org.opensocial.data.OpenSocialObject;
import org.opensocial.data.OpenSocialPerson;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * An object which exposes a number of static methods for parsing JSON strings
 * returned from RESTful or JSON-RPC requests into appropriate objects.
 *
 * @author Jason Cooper
 */
public class OpenSocialJsonParser {

  public static OpenSocialResponse getResponse(String in) throws JSONException {
    OpenSocialResponse r = null;
    
    if (in.charAt(0) == '[') {
      JSONArray responseArray = new JSONArray(in);
      r = new OpenSocialResponse();
      
      for (int i=0; i<responseArray.length(); i++) {
        JSONObject o = responseArray.getJSONObject(i);
        
        if (o.has("id")) {
          String id = o.getString("id");
          r.addItem(id, escape(o.toString()));
        }
      }
    }
    
    return r;
  }
  
  public static OpenSocialResponse getResponse(String in, String id) throws JSONException {
    OpenSocialResponse r = null;
    
    if (in.charAt(0) == '{') {
      r = new OpenSocialResponse();
      r.addItem(id, escape(in));
    } else if (in.charAt(0) == '[') {
      return getResponse(in);
    }
    
    return r;
  }
  
  /**
   * Transforms a raw JSON string containing profile information for a single
   * user into an OpenSocialPerson instance with all profile details
   * abstracted as OpenSocialField objects associated with the
   * instance.
   * 
   * @throws JSONException 
   * @throws OpenSocialRequestException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public static OpenSocialPerson parseAsPerson(String in) throws OpenSocialRequestException, JSONException, InstantiationException, IllegalAccessException {
    if (in == null) {
      throw new OpenSocialRequestException("Response item with given key not found");
    }
    
    JSONObject root = new JSONObject(in);
    JSONObject entry = getEntryObject(root);
    OpenSocialPerson p = (OpenSocialPerson) parseAsObject(entry, OpenSocialPerson.class);
    
    return p;
  }
  
  /**
   * Transforms a raw JSON string containing profile information for a group
   * of users into a group of OpenSocialPerson instances with all profile
   * details abstracted as OpenSocialField objects associated with the
   * instance. These instances are then added to a Java Collection
   * which gets returned.
   * 
   * @throws JSONException 
   * @throws OpenSocialRequestException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public static List<OpenSocialPerson> parseAsPersonCollection(String in) throws OpenSocialRequestException, JSONException, InstantiationException, IllegalAccessException {
    if (in == null) {
      throw new OpenSocialRequestException("Response item with given key not found");
    }
    
    JSONObject root = new JSONObject(in);
    JSONArray entries = getEntryArray(root);
    List<OpenSocialPerson> l = new Vector<OpenSocialPerson>(entries.length());
    
    for (int i=0; i<entries.length(); i++) {
      JSONObject entry = entries.getJSONObject(i);
      OpenSocialPerson p = (OpenSocialPerson) parseAsObject(entry, OpenSocialPerson.class);

      l.add(p);
    }
    
    return l;
  }
  
  /**
   * Transforms a raw JSON string containing app data key-value pairs for a 
   * single user into an OpenSocialObject instance with each key-value pair
   * abstracted as OpenSocialField objects associated with the instance.
   * 
   * @throws JSONException 
   * @throws OpenSocialRequestException 
   */
  public static OpenSocialAppData parseAsAppData(String in) throws OpenSocialRequestException, JSONException, InstantiationException, IllegalAccessException {
    if (in == null) {
      throw new OpenSocialRequestException("Response item with given key not found");
    }
    
    JSONObject root = new JSONObject(in);
    JSONObject entry = getEntryObject(root);
    OpenSocialAppData d = (OpenSocialAppData) parseAsObject(entry, OpenSocialAppData.class);
    
    return d;
  }
  
  private static JSONArray getEntryArray(JSONObject root) throws OpenSocialRequestException, JSONException {
    JSONArray entry = new JSONArray();
    
    if (root.has("entry")) {
      entry = root.getJSONArray("entry");
    } else if (root.has("data")) {
      entry = root.getJSONObject("data").getJSONArray("list");
    } else {
      throw new OpenSocialRequestException("Entry not found");
    }
    
    return entry;
  }
  
  private static JSONObject getEntryObject(JSONObject root) throws OpenSocialRequestException, JSONException {
    JSONObject entry = new JSONObject();
    
    if (root.has("data")) {
      entry = root.getJSONObject("data");
    } else if (root.has("entry")) {
      entry = root.getJSONObject("entry");
    } else {
      throw new OpenSocialRequestException("Entry not found");
    }
    
    return entry;
  }
  
  private static OpenSocialObject parseAsObject(JSONObject entryObject, Class<? extends OpenSocialObject> clientClass) throws JSONException, InstantiationException, IllegalAccessException {
    OpenSocialObject o = clientClass.newInstance();

    Map<String, OpenSocialField> entryRepresentation = 
        createObjectRepresentation(entryObject);
    
    for (Map.Entry<String, OpenSocialField> e : entryRepresentation.entrySet()) {
      o.setField(e.getKey(), e.getValue());
    }
    
    return o;
  }

  private static Map<String, OpenSocialField> createObjectRepresentation(
      JSONObject o) throws JSONException {

    HashMap<String,OpenSocialField> r = new HashMap<String,OpenSocialField>();

    Iterator<?> keys = o.keys();

    while (keys.hasNext()) {
      String key = (String) keys.next();
      String property = o.getString(key);

      if (property.length() > 0 && property.charAt(0) == '{') {
        JSONObject p = o.getJSONObject(key);
        OpenSocialField field = new OpenSocialField(true);

        field.addValue(new OpenSocialObject(createObjectRepresentation(p)));
        r.put(key, field);
      } else if (property.length() > 0 && property.charAt(0) == '[') {
        JSONArray p = o.getJSONArray(key);
        Collection<Object> values = createArrayRepresentation(p);
        OpenSocialField field = new OpenSocialField(true);

        for (Object v : values) {
          field.addValue(v);
        }

        r.put(key, field);
      } else if (property.length() > 0) {
        OpenSocialField field = new OpenSocialField(false);
        field.addValue(unescape(property));
        r.put(key, field);
      }      
    }

    return r;
  }

  private static List<Object> createArrayRepresentation(
      JSONArray a) throws JSONException {

    Vector<Object> r = new Vector<Object>(a.length());

    for (int i=0; i<a.length(); i++) {
      String member = a.getString(i);

      if (member.length() > 0 && member.charAt(0) == '{') {
        JSONObject p = a.getJSONObject(i);
        r.add(new OpenSocialObject(createObjectRepresentation(p)));
      } else if (member.length() > 0 && member.charAt(0) == '[') {
        JSONArray p = a.getJSONArray(i);
        List<Object> values = createArrayRepresentation(p);

        for (Object v : values) {
          r.add(v);
        }
      } else if (member.length() > 0) {
        r.add(member);
      }
    }

    return r;
  }
  
  private static String escape(String in) {
    String out = in;
    
    out = out.replaceAll("\"\\{", "\"%7B");
    out = out.replaceAll("\\}\"", "%7D\"");
    
    return out;
  }
  
  private static String unescape(String in) {
    String out = in;
    
    out = out.replaceAll("%7B", "{");
    out = out.replaceAll("%7D", "}");
    
    return out;
  }
}

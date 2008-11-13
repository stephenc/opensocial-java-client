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
import org.opensocial.data.OpenSocialField;
import org.opensocial.data.OpenSocialObject;
import org.opensocial.data.OpenSocialPerson;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * An object which exposes a number of static methods for parsing JSON strings
 * returned from RESTful or JSON-RPC requests into appropriate objects.
 *
 * @author Jason Cooper
 */
public class OpenSocialJsonParser {

  /**
   * Transforms a raw JSON string containing profile information for a single
   * user into an OpenSocialPerson instance with all profile details
   * abstracted as OpenSocialField objects associated with the
   * instance.
   * 
   * @throws JSONException 
   * @throws OpenSocialRequestException 
   */
  public static OpenSocialPerson parsePersonEntry(String in)
      throws JSONException, OpenSocialRequestException {
    
    JSONObject root = new JSONObject(in);
    JSONObject entryObject = null;

    if (root.has("entry")) {
      entryObject = root.getJSONObject("entry");
    } else if (root.has("data")) {
      entryObject = root.getJSONObject("data");
    } else {
      throw new OpenSocialRequestException(
          "Enclosing object not found in JSON response");
    }

    OpenSocialPerson p = new OpenSocialPerson();

    Map<String,OpenSocialField> entryRepresentation = 
        createObjectRepresentation(entryObject);
    Object[] keys = entryRepresentation.keySet().toArray();

    for (int i=0; i<entryRepresentation.size(); i++) {
      p.setField((String)keys[i], entryRepresentation.get(keys[i]));
    }

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
   */
  public static Collection<OpenSocialPerson> parsePersonCollection(String in)
      throws JSONException, OpenSocialRequestException {
    
    Vector<OpenSocialPerson> collection;

    JSONObject root = new JSONObject(in);
    JSONArray entryArray = null;

    if (root.has("entry")) {
      entryArray = root.getJSONArray("entry");
    } else if (root.has("data")) {
      entryArray = root.getJSONObject("data").getJSONArray("list");
    } else {
      throw new OpenSocialRequestException(
          "Enclosing object not found in JSON response");
    }

    collection = new Vector<OpenSocialPerson>(entryArray.length());

    for (int i=0; i<entryArray.length(); i++) {
      JSONObject entryObject = entryArray.getJSONObject(i);
      OpenSocialPerson p = new OpenSocialPerson();

      Map<String,OpenSocialField> entryRepresentation =
          createObjectRepresentation(entryObject);
      Object[] keys = entryRepresentation.keySet().toArray();

      for (int j=0; j<entryRepresentation.size(); j++) {
        p.setField((String)keys[j], entryRepresentation.get(keys[j]));
      }

      collection.add(p);
    }

    return collection;
  }

  /**
   * Transforms a raw JSON string containing app data key-value pairs for a 
   * single user into an OpenSocialObject instance with each key-value pair
   * abstracted as OpenSocialField objects associated with the instance.
   * 
   * @throws JSONException 
   * @throws OpenSocialRequestException 
   */
  public static OpenSocialObject parseDataEntry(String in)
      throws JSONException, OpenSocialRequestException {
    
    JSONObject root = new JSONObject(in);
    JSONObject entryObject = null;

    if (root.has("entry")) {
      entryObject = root.getJSONObject("entry");
    } else if (root.has("data")) {
      entryObject = root.getJSONObject("data");
    } else {
      throw new OpenSocialRequestException(
          "Enclosing object not found in JSON response");
    }

    OpenSocialObject o = new OpenSocialObject();

    String[] fieldNames = JSONObject.getNames(entryObject);
    if (fieldNames == null) {
      throw new OpenSocialRequestException(
          "No ID mappings in JSON data response");
    }

    JSONObject idObject = entryObject.getJSONObject(fieldNames[0]);

    Map<String,OpenSocialField> entryRepresentation =
        createObjectRepresentation(idObject);
    Object[] keys = entryRepresentation.keySet().toArray();

    for (int i=0; i<entryRepresentation.size(); i++) {
      o.setField((String)keys[i], entryRepresentation.get(keys[i]));
    }

    return o;
  }

  private static Map<String,OpenSocialField> createObjectRepresentation(
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
        field.addValue(property);
        r.put(key, field);
      }
    }

    return r;
  }

  private static Collection<Object> createArrayRepresentation(
      JSONArray a) throws JSONException {

    Vector<Object> r = new Vector<Object>(a.length());

    for (int i=0; i<a.length(); i++) {
      String member = a.getString(i);

      if (member.length() > 0 && member.charAt(0) == '{') {
        JSONObject p = a.getJSONObject(i);
        r.add(new OpenSocialObject(createObjectRepresentation(p)));
      } else if (member.length() > 0 && member.charAt(0) == '[') {
        JSONArray p = a.getJSONArray(i);
        Collection<Object> values = createArrayRepresentation(p);

        for (Object v : values) {
          r.add(v);
        }
      } else if (member.length() > 0) {
        r.add(member);
      }
    }

    return r;
  }
}

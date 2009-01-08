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

  /**
   * Parses the passed JSON string into an OpenSocialResponse object -- if the
   * passed string represents a JSON array, each object in the array is added
   * to the returned object keyed on its "id" property.
   *
   * @param  in The complete JSON string returned from an OpenSocial container
   *            in response to a request for data
   * @throws JSONException
   */
  public static OpenSocialResponse getResponse(String in)
      throws JSONException {

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

  /**
   * Parses the passed JSON string into an OpenSocialResponse object -- if the
   * passed string represents a JSON object, it is added to the returned
   * object keyed on the passed ID.
   *
   * @param  in The complete JSON string returned from an OpenSocial container
   *            in response to a request for data
   * @param  id The string ID to tag the JSON object string with as it is added
   *            to the OpenSocialResponse object
   * @throws JSONException
   */
  public static OpenSocialResponse getResponse(String in, String id)
      throws JSONException {

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
   * Transforms a raw JSON object string containing profile information for a
   * single user into an OpenSocialPerson instance with all profile details
   * abstracted as OpenSocialField objects associated with the instance.
   *
   * @param  in The JSON object string to parse as an OpenSocialPerson object
   * @throws OpenSocialRequestException
   * @throws JSONException
   */
  public static OpenSocialPerson parseAsPerson(String in)
      throws OpenSocialRequestException, JSONException {

    if (in == null) {
      throw new OpenSocialRequestException(
          "Response item with given key not found");
    }

    JSONObject root = new JSONObject(in);
    JSONObject entry = getEntryObject(root);

    OpenSocialPerson p =
        (OpenSocialPerson) parseAsObject(entry, OpenSocialPerson.class);

    return p;
  }

  /**
   * Transforms a raw JSON object string containing profile information for a
   * group of users into a list of OpenSocialPerson instances with all profile
   * details abstracted as OpenSocialField objects associated with the
   * instances. These instances are then added to a Java List which
   * gets returned.
   *
   * @param  in The JSON object string to parse as a List of OpenSocialPerson
   *         objects
   * @throws OpenSocialRequestException
   * @throws JSONException
   */
  public static List<OpenSocialPerson> parseAsPersonCollection(String in)
      throws OpenSocialRequestException, JSONException {

    if (in == null) {
      throw new OpenSocialRequestException(
          "Response item with given key not found");
    }

    JSONObject root = new JSONObject(in);
    JSONArray entries = getEntryArray(root);
    List<OpenSocialPerson> l = new Vector<OpenSocialPerson>(entries.length());

    for (int i=0; i<entries.length(); i++) {
      JSONObject entry = entries.getJSONObject(i);

      OpenSocialPerson p =
          (OpenSocialPerson) parseAsObject(entry, OpenSocialPerson.class);

      l.add(p);
    }

    return l;
  }

  /**
   * Transforms a raw JSON object string containing key-value pairs (i.e. App
   * Data) for one or more users into a specialized OpenSocialObject instance
   * with each key-value pair abstracted as OpenSocialField objects associated
   * with the instance.
   *
   * @param  in The JSON object string to parse as an OpenSocialAppData object
   * @throws JSONException
   * @throws OpenSocialRequestException
   */
  public static OpenSocialAppData parseAsAppData(String in)
      throws OpenSocialRequestException, JSONException {

    if (in == null) {
      throw new OpenSocialRequestException(
          "Response item with given key not found");
    }

    JSONObject root = new JSONObject(in);
    JSONObject entry = getEntryObject(root);

    OpenSocialAppData d =
        (OpenSocialAppData) parseAsObject(entry, OpenSocialAppData.class);

    return d;
  }

  /**
   * Inspects the passed object for one of several specific properties and, if
   * found, returns that property as a JSONArray object. All valid response
   * objects which contain a data collection (e.g. a collection of people)
   * must have this property.
   *
   * @param  root JSONObject to query for the presence of the specific property
   * @throws OpenSocialRequestException if property is not found in the passed
   *         object
   * @throws JSONException
   */
  private static JSONArray getEntryArray(JSONObject root)
      throws OpenSocialRequestException, JSONException {

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

  /**
   * Inspects the passed object for one of several specific properties and, if
   * found, returns that property as a JSONObject object. All valid response
   * objects which encapsulate a single data item (e.g. a person) must have
   * this property.
   *
   * @param  root JSONObject to query for the presence of the specific property
   * @throws OpenSocialRequestException if property is not found in the passed
   *         object
   * @throws JSONException
   */
  private static JSONObject getEntryObject(JSONObject root)
      throws OpenSocialRequestException, JSONException {

    JSONObject entry;

    if (root.has("data")) {
      entry = root.getJSONObject("data");
    } else if (root.has("entry")) {
      entry = root.getJSONObject("entry");
    } else {
      throw new OpenSocialRequestException("Entry not found");
    }

    return entry;
  }

  /**
   * Calls a function to recursively iterates through the the properties of the
   * passed JSONObject object and returns an equivalent OpenSocialObject with
   * each property of the original object mapped to fields in the returned
   * object.
   *
   * @param  entryObject Object-oriented representation of JSON response
   *         string which is transformed into and returned as an
   *         OpenSocialObject
   * @param  clientClass Class of object to return, either OpenSocialObject
   *         or a subclass
   * @throws JSONException
   */
  private static OpenSocialObject parseAsObject(
      JSONObject entryObject, Class<? extends OpenSocialObject> clientClass)
      throws JSONException {

    OpenSocialObject o = null;
    try {
      o = clientClass.newInstance();
    } catch (InstantiationException e) {
      throw new RuntimeException("Library error - Json class not found: " + clientClass);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Library error - Json class not accessible: " + clientClass);
    }

    Map<String,OpenSocialField> entryRepresentation =
        createObjectRepresentation(entryObject);

    for (Map.Entry<String,OpenSocialField> e : entryRepresentation.entrySet()) {
      o.setField(e.getKey(), e.getValue());
    }

    return o;
  }

  /**
   * Recursively iterates through the properties of the passed JSONObject
   * object and returns a Map of OpenSocialField objects keyed on Strings
   * representing the property values and names respectively.
   *
   * @param  o Object-oriented representation of a JSON object which is
   *         transformed into and returned as a Map of OpenSocialField
   *         objects keyed on Strings
   * @throws JSONException
   */
  private static Map<String, OpenSocialField> createObjectRepresentation(
      JSONObject o)
      throws JSONException {

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

  /**
   * Iterates through the objects in the passed JSONArray object, recursively
   * transforms each as needed, and returns a List of Java objects.
   *
   * @param  a Object-oriented representation of a JSON array which is iterated
   *         through and returned as a List of Java objects
   * @throws JSONException
   */
  private static List<Object> createArrayRepresentation(JSONArray a)
      throws JSONException {

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

  /**
   * Escapes "{ and }" as "%7B and "%7D respectively to prevent parsing errors
   * when property values begin with { or } tokens.
   *
   * @param  in String to escape
   * @return escaped String
   */
  private static String escape(String in) {
    String out = in;

    out = out.replaceAll("\"\\{", "\"%7B");
    out = out.replaceAll("\\}\"", "%7D\"");

    return out;
  }

  /**
   * Unescapes String objects previously returned from the escape method by
   * substituting { and } for %7B and %7D respectively. Called after
   * parsing to restore property values.
   *
   * @param  in String to unescape
   * @return unescaped String
   */
  private static String unescape(String in) {
    String out = in;

    out = out.replaceAll("%7B", "{");
    out = out.replaceAll("%7D", "}");

    return out;
  }
}

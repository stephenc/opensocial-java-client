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


package org.opensocial.data;

import java.util.HashMap;
import java.util.Map;

/**
 * An object representing a generic, extensible OpenSocial entity. Virtually
 * every object, both concrete (person) or abstract (data), having arbitrary
 * properties is modeled as an OpenSocialObject instance. Instance methods
 * provide an interface for associating strings with objects representing
 * properties or attributes of that object. These fields can in turn
 * reference other OpenSocialObject instances.
 *
 * @author Jason Cooper
 */
public class OpenSocialObject {

  protected Map<String,OpenSocialField> fields;

  public OpenSocialObject() {
    this.fields = new HashMap<String,OpenSocialField>();
  }

  /**
   * Instantiates a new OpenSocialObject object with the passed Map of
   * OpenSocialField objects keyed on strings, replicating these
   * correspondences in its own fields Map.
   *
   * @param  properties Map of OpenSocialField objects keyed on field name
   *         which should be "imported" upon instantiation
   */
  public OpenSocialObject(Map<String,OpenSocialField> properties) {
    this();

    for (Map.Entry<String,OpenSocialField> e : properties.entrySet()) {
      this.setField(e.getKey(), e.getValue());
    }
  }

  /**
   * Returns {@code true} if a field with the passed key is associated with
   * the current instance, {@code false} otherwise.
   */
  public boolean hasField(String key) {
    return this.fields.containsKey(key);
  }

  /**
   * Returns field mapped to the passed key.
   *
   * @param  key Key associated with desired field
   */
  public OpenSocialField getField(String key) {
    return this.fields.get(key);
  }

  /**
   * Creates a new entry in fields Map, associating the passed OpenSocialField
   * object with the passed key.
   *
   * @param  key Field name
   * @param  value OpenSocialField object to associate with key
   */
  public void setField(String key, OpenSocialField value) {
    this.fields.put(key, value);
  }

  /**
   * Returns the names of all properties associated with the instance as an
   * array of Java String objects.
   */
  public String[] fieldNames() {
    Object[] keys = this.fields.keySet().toArray();
    String[] fieldNames = new String[this.fields.size()];

    for (int i = 0; i < this.fields.size(); i++) {
      fieldNames[i] = (String) keys[i];
    }

    return fieldNames;
  }

  @Override
  public String toString() {
    String allFields = "";
    for (Map.Entry<String, OpenSocialField> entry : fields.entrySet()) {
      OpenSocialField value = entry.getValue();
      String valueString = value.getStringValue();
      if (value.isComplex()) {
        valueString = value.getStringValues().toString();
      }
      allFields += entry.getKey() + ": " + valueString + "\n";
    }
    return allFields;
  }
}

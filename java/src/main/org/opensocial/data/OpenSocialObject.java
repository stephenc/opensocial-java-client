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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * An object representing a generic, extensible OpenSocial entity. Virtually
 * every object, both concrete (person) or abstract (name), having arbitrary
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

  public OpenSocialObject(Map<String,OpenSocialField> properties) {
    this();

    Object[] keys = properties.keySet().toArray();
    Collection<OpenSocialField> values = properties.values();

    int i = 0;
    for (OpenSocialField o : values) {
      this.setField((String) keys[i], o);
      i++;
    }
  }

  public boolean hasField(String key) {
    return this.fields.containsKey(key);
  }

  public OpenSocialField getField(String key) {
    return this.fields.get(key);
  }

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

    for (int i=0; i<this.fields.size(); i++) {
      fieldNames[i] = (String) keys[i];
    }

    return fieldNames;
  }
}

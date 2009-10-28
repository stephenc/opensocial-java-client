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


package org.opensocial.data;

import java.util.Collection;
import java.util.Vector;

/**
 * An object representing a single property of an OpenSocial entity. These
 * properties may have a single value or multiple values and these values
 * can either consist of simple character strings or complex entities;
 * for example, the "name" field of a Person instance can be modeled
 * as an OpenSocialObject with "givenName" and "familyName" fields.
 *
 * @author Jason Cooper
 */
public class OpenSocialField {

  private Collection<Object> values;
  private boolean complex;

  public OpenSocialField(boolean complex) {
    this.complex = complex;

    this.values = new Vector<Object>();
  }

  /**
   * Stores passed object as a value.
   */
  public void addValue(Object o) {
    this.values.add(o);
  }

  /**
   * Returns the first stored value as a String. Returns null if no values
   * have been associated with the instance.
   */
  public String getStringValue() {
    if (this.values.size() == 0) {
      return null;
    }

    Object[] objectValues = this.values.toArray();

    if (this.complex == true) {
      return objectValues[0].toString();
    }

    return (String) objectValues[0];
  }

  /**
   * Returns the first stored value as an OpenSocialObject. Returns null if
   * no values have been associated with the instance.
   * 
   * @throws OpenSocialException if the complex instance variable is false,
   *         indicating that the values are stored as simple String objects.
   */
  public OpenSocialObject getValue() throws OpenSocialException {
    if (this.values.size() == 0) {
      return null;
    }
    if (this.complex == false) {
      throw new OpenSocialException(
          "String-based field cannot be returned as an OpenSocialObject");
    }

    Object[] objectValues = this.values.toArray();
    return (OpenSocialObject) objectValues[0];
  }

  /**
   * Returns all stored values as a Java Collection of String objects.
   */
  public Collection<String> getStringValues() {
    Collection<String> stringCollection =
        new Vector<String>(this.values.size());

    for (Object o : values) {
      if (this.complex == true) {
        stringCollection.add(o.toString());
      } else {
        stringCollection.add((String) o);
      }
    }

    return stringCollection;
  }

  /**
   * Returns all stored values as a Java Collection of OpenSocialObject
   * instances.
   *
   * @throws OpenSocialException if the complex instance variable is false,
   *         indicating that the values are stored as simple String objects.
   */
  public Collection<OpenSocialObject> getValues() throws OpenSocialException {
    if (this.complex == false) {
      throw new OpenSocialException(
          "String-based fields cannot be returned as an OpenSocialObject " +
          "collection");
    }

    Collection<OpenSocialObject> objectCollection =
        new Vector<OpenSocialObject>(this.values.size());

    for (Object o: this.values) {
      objectCollection.add((OpenSocialObject) o);
    }

    return objectCollection;
  }

  /**
   * Returns true if the number of stored values is greater than one, false
   * otherwise.
   */
  public boolean isMultivalued() {
    return (values.size() > 1);
  }

  /**
   * Returns false if values are stored as simple String objects, true if
   * OpenSocialObject instances are stored instead.
   */
  public boolean isComplex() {
    return this.complex;
  }
}
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

import java.util.List;
import java.util.Vector;

public class OpenSocialRequestParameter {

  private List<String> values;

  public OpenSocialRequestParameter() {
    this.values = new Vector<String>(1);
  }

  public OpenSocialRequestParameter(String[] values) {
    this();
    this.addValues(values);
  }

  public OpenSocialRequestParameter(String value) {
    this();    
    this.addValue(value);
  }

  public void addValues(String[] values) {
    for (int i = 0; i < values.length; i++) {
      this.values.add(values[i]);
    }
  }

  public void addValue(String value) {
    this.values.add(value);
  }

  public String[] getValuesArray() {
    String[] valuesArray = new String[values.size()];
    valuesArray = values.toArray(valuesArray);

    return valuesArray;
  }

  public String getValuesString() {
    StringBuilder list = new StringBuilder();

    for (int i = 0; i < values.size(); i++) {
      if (i != 0) {
        list.append(",");
      }

      list.append(values.get(i));
    }

    return list.toString();
  }

  /**
   * Returns true if the number of stored values is greater than one, false
   * otherwise.
   */
  public boolean isMultivalued() {
    return (values.size() > 1);
  }
}

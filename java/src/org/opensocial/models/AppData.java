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

package org.opensocial.models;

import java.util.Map;
import java.util.Set;

public class AppData extends Model {

  public String getDataForUser(String userId, String key) {
    Map userData = getFieldAsMap(userId);
    if (userData != null) {
      return (String) userData.get(key);
    }

    return null;
  }

  public Set<String> getFieldNamesForUser(String userId) {
    Map userData = getFieldAsMap(userId);
    if (userData != null) {
      return userData.keySet();
    }

    return null;
  }
}

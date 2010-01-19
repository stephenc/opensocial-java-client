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

package org.opensocial.models.myspace;

import org.opensocial.models.Model;

public class StatusMood extends Model {

  public String getStatus() {
    return getFieldAsString("status");
  }

  public String getMoodId() {
    return getFieldAsString("moodId");
  }

  public String getMoodName() {
    return getFieldAsString("moodName");
  }

  public void setStatus(String status) {
    put("status", status);
  }

  public void setMoodId(long moodId) {
    put("moodId", moodId);
  }
}

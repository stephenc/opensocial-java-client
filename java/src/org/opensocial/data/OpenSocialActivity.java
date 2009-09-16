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

/**
 * Class representing the OpenSocialActivity object which is used in the REST calls
 * for fetching and creating activities
 *
 * @author vijayam
 *
 */
public class OpenSocialActivity extends OpenSocialObject {

  public String getUserId() {
    return getStringField("userId");
  }

  public String getTitle() {
    return getStringField("title");
  }

  public String getId() {
    return getStringField("id");
  }

  public String getBody() {
    return getStringField("body");
  }

  public String getBodyId() {
    return getStringField("bodyId");
  }

  public String getUrl() {
    return getStringField("url");
  }

  /*public Date getLastUpdatedDate() {
      OpenSocialField field = this.getField("lastUpdated");

      if (field != null ) {
        return new Date(field.getStringValue());
      }

      return "";
  }
  */


  /**
   * Generic method which takes the name of the string field and returns its value
   *
   * @param fieldName
   */
  public String getStringField(String fieldName) {
    OpenSocialField field = this.getField(fieldName);

    if (field != null ) {
      return field.getStringValue();
    }

    return "";
  }

}

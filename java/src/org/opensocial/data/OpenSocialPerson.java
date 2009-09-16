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
 * An object representing a person in an OpenSocial environment; extends
 * OpenSocialObject class by adding convenience methods for easily
 * accessing special fields unique to instances of this class.
 *
 * @author Jason Cooper
 */
public class OpenSocialPerson extends OpenSocialObject {

  /**
   * Retrieves the OpenSocial ID associated with the instance. Returns an
   * empty string if no ID has been set.
   */
  public String getId() {
    OpenSocialField idField = this.getField("id");

    if (idField != null && !idField.isComplex()) {
      return idField.getStringValue();
    }

    return "";
  }

  /**
   * Retrieves the display name (typically given name followed by family name)
   * associated with the instance. Returns an empty string if no name has been
   * set.
   */
  public String getDisplayName() {
    OpenSocialField nicknameField = this.getField("nickname");
    OpenSocialField nameField = this.getField("name");
    OpenSocialField displayNameField = this.getField("displayName");
    StringBuilder name = new StringBuilder();

    if (displayNameField != null) {
      name.append(displayNameField.getStringValue());
    } else if (nicknameField != null) {
      name.append(nicknameField.getStringValue());
    } else if (nameField != null) {
      if (nameField.isComplex()) {
        OpenSocialObject nameObject;
        try {
          nameObject = nameField.getValue();
        } catch (OpenSocialException e) {
          // This should never happen as the nameField is guaranteed to be complex
          // by the if statement
          throw new IllegalStateException(e);
        }

        if (nameObject.hasField("givenName")) {
          name.append(nameObject.getField("givenName").getStringValue());
        }

        if (nameObject.hasField("givenName") && nameObject.hasField("familyName")) {
          name.append(" ");
        }

        if (nameObject.hasField("familyName")) {
          name.append(nameObject.getField("familyName").getStringValue());
        }
      } else {
        name.append(nameField.getStringValue());
      }
    } else {
      name.append("Unknown Person");
    }

    return name.toString();
  }

  /**
   * Retrieves the thumbnailUrl associated with the instance. 
   * Returns an empty string if no thumbnailUrl has been set.
   */
  public String getThumbnailUrl() {
    OpenSocialField thumbnailUrlField = this.getField("thumbnailUrl");

    if (thumbnailUrlField != null && !thumbnailUrlField.isComplex()) {
      return thumbnailUrlField.getStringValue();
    }

    return "";  
  } 
}

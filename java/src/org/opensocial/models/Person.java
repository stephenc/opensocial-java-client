package org.opensocial.models;

import java.util.Map;

public class Person extends Model {

  public String getId() {
    return (String) getField("id");
  }

  public String getDisplayName() {
    StringBuilder name = new StringBuilder();

    if (getField("displayName") != null) {
      name.append(getField("displayName"));
    } else if (getField("nickname") != null) {
      name.append(getField("nickname"));
    } else if (getField("name") != null) {
      if (isFieldMultikeyed("name")) {
        Map nameMap = getFieldAsMap("name");

        if (nameMap.containsKey("givenName")) {
          name.append(nameMap.get("givenName"));
        }
        if (nameMap.containsKey("givenName") &&
            nameMap.containsKey("familyName")) {
          name.append(" ");
        }
        if (nameMap.containsKey("familyName")) {
          name.append(nameMap.get("familyName"));
        }
      } else {
        name.append((String) getField("name"));
      }
    }

    return name.toString();
  }

  public String getThumbnailUrl() {
    return (String) getField("thumbnailUrl");
  }
}

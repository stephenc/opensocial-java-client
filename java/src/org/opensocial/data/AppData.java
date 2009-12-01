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

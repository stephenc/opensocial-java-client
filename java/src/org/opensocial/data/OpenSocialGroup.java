package org.opensocial.data;

import org.json.JSONException;

public class OpenSocialGroup extends OpenSocialModel {
  public OpenSocialGroup(){}
  
  public OpenSocialGroup(String json) throws JSONException {
    super(json);
  }
}

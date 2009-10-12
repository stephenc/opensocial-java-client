package org.opensocial.data;

import org.json.JSONException;

/**
 * Model object for Messages
 * @author jle.edwards@gmail.com (Jesse Edwards)
 */
public class OpenSocialMessage extends OpenSocialModel {
  public OpenSocialMessage(){}
  
  public OpenSocialMessage(String json) throws JSONException {
    super(json);
  }
}
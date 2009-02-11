package org.opensocial.client;

import org.json.JSONException;
import org.json.JSONObject;

import junit.framework.TestCase;

public class OpenSocialRequestTest extends TestCase {
  public OpenSocialRequestTest(String name) {
    super(name);
  }

  public void testCreate() {
    String restPathComponent = "people/";
    String rpcMethod = "people.get";

    OpenSocialRequest r = new OpenSocialRequest(restPathComponent, rpcMethod);
    assertEquals(true, r.getRestPathComponent().equals(restPathComponent));
  }

  public void testGetId() {
    String id = "myId";

    OpenSocialRequest r = new OpenSocialRequest("people/", "people.get");

    r.setId(id);
    assertEquals(true, r.getId().equals(id));
  }

  public void testGetParameter() {
    String name = "myParameterName";
    String value = "myParameterValue";

    OpenSocialRequest r = new OpenSocialRequest("people/", "people.get");
    r.addParameter(name, value);

    assertEquals(true, r.getParameter(name).equals(value));
  }

  public void testToJson() throws JSONException {
    String restPathComponent = "people/";
    String rpcMethod = "people.get";
    String n1 = "parameterName1";
    String v1 = "parameterValue1";
    String n2 = "parameterName2";
    String v2 = "parameterValue2";
    String id = "myId";

    JSONObject params = new JSONObject();
    JSONObject request = new JSONObject();

    params.put(n1, v1);
    params.put(n2, v2);    
    request.put("id", id);
    request.put("method", rpcMethod);
    request.put("params", params);

    OpenSocialRequest r = new OpenSocialRequest(restPathComponent, rpcMethod);
    r.addParameter(n1, v1);
    r.addParameter(n2, v2);
    r.setId(id);

    assertEquals(true, request.toString().equals(r.toJson()));
  }
}

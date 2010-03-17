package org.opensocial;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.easymock.LogicalOperator;
import org.junit.Before;
import org.junit.Test;
import org.opensocial.auth.AuthScheme;
import org.opensocial.http.HttpClient;
import org.opensocial.http.HttpResponseMessage;
import org.opensocial.providers.Provider;
import org.opensocial.providers.ShindigProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class RequestTest {

  class BodyContainsComparator implements Comparator<byte[]> {
    public int compare(byte[] actual, byte[] part) {
      if (new String(actual).indexOf(new String(part)) == -1) {
        return 1;
      } else {
        return 0;
      }
    }
  }

  HttpClient httpClient;
  AuthScheme authScheme;
  IMocksControl mockControl;
  Map<String, String> headers;

  @Before
  public void initialize() throws Exception {
    mockControl = EasyMock.createControl();
    httpClient = mockControl.createMock(HttpClient.class);
    authScheme = mockControl.createMock(AuthScheme.class);
    
    headers = new HashMap<String, String>();
    headers.put("Content-Type", "application/json");
  }

  @Test
  public void testSetFieldsParameterRpc() throws RequestException, IOException {
    Provider provider = new ShindigProvider();
    Client client = new Client(provider, authScheme, httpClient);
    Request request = new Request("/service/", "service.test", "GET");

    String fieldsString = "\"fields\":[\"field1\",\"field2\"]";
    String url = removeTrailingSlash(provider.getRpcEndpoint());

    expect(authScheme.getHttpMessage(
        eq(provider), eq("POST"), eq(url), eq(headers),
        EasyMock.cmp(fieldsString.getBytes(),
            new BodyContainsComparator(), LogicalOperator.EQUAL))).andReturn(
                null);

    String body = "[]";
    InputStream is = new ByteArrayInputStream(body.getBytes());

    expect(httpClient.execute(null)).andReturn(
        new HttpResponseMessage("POST", new URL(url), 200, is));

    mockControl.replay();

    request.setFieldsParameter(new String[] {"field1", "field2"});
    client.send(request);

    mockControl.verify();
  }

  private static String removeTrailingSlash(String s) {
    if (s.endsWith("/")) {
      return s.substring(0, s.length() - 1);
    }

    return s;
  }
}

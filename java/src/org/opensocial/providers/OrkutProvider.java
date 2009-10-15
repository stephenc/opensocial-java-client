package org.opensocial.providers;

public class OrkutProvider extends Provider {

  public OrkutProvider() {
    super();

    setName("orkut");
    setVersion("0.8");
    setRpcEndpoint("http://www.orkut.com/social/rpc/");
    setRestEndpoint("http://www.orkut.com/social/rest/");
  }
}

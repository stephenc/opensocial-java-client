package org.opensocial.providers;

public class OrkutProvider extends Provider {

  public OrkutProvider() {
    this(false);
  }

  public OrkutProvider(boolean useRest) {
    super();

    setName("orkut");
    setVersion("0.8");
    setRestEndpoint("http://www.orkut.com/social/rest/");
    if (!useRest) {
      setRpcEndpoint("http://www.orkut.com/social/rpc/");
    }
  }
}

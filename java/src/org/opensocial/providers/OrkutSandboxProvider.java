package org.opensocial.providers;

public class OrkutSandboxProvider extends Provider {

  public OrkutSandboxProvider() {
    this(false);
  }

  public OrkutSandboxProvider(boolean useRest) {
    super();

    setName("orkut");
    setVersion("0.8");
    setRestEndpoint("http://sandbox.orkut.com/social/rest/");
    if (!useRest) {
      setRpcEndpoint("http://sandbox.orkut.com/social/rpc/");
    }
  }
}

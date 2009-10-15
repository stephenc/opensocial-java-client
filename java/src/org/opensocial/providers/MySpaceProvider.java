package org.opensocial.providers;

public class MySpaceProvider extends Provider {

  public MySpaceProvider() {
    super();

    setName("MySpace");
    setVersion("0.9");
    setRestEndpoint("http://opensocial.myspace.com/roa/09/");
    setSignBodyHash(false);
  }
}

package org.opensocial.providers;

public class MySpaceProvider extends Provider {

  public MySpaceProvider() {
    super();

    setName("MySpace");
    setVersion("0.9");
    setRestEndpoint("http://opensocial.myspace.com/roa/09/");
    setAuthorizeUrl("http://api.myspace.com/authorize");
    setAccessTokenUrl("http://api.myspace.com/access_token");
    setRequestTokenUrl("http://api.myspace.com/request_token");
    setSignBodyHash(false);
  }
}

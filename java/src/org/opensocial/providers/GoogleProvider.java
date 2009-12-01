package org.opensocial.providers;

public class GoogleProvider extends Provider {

  public GoogleProvider() {
    this(false);
  }

  public GoogleProvider(boolean useRest) {
    super();

    setName("Google");
    setVersion("0.8");
    setRestEndpoint("http://www-opensocial.googleusercontent.com/api/");
    if (!useRest) {
      setRpcEndpoint("http://www-opensocial.googleusercontent.com/api/rpc/");
    }
    setAuthorizeUrl("https://www.google.com/accounts/OAuthAuthorizeToken");
    setAccessTokenUrl("https://www.google.com/accounts/OAuthGetAccessToken");
    setRequestTokenUrl("https://www.google.com/accounts/OAuthGetRequestToken");
    addRequestTokenParameter("scope", getRestEndpoint() + " " +
        getRpcEndpoint());
  }
}

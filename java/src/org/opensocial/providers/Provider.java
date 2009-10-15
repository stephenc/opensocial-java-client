package org.opensocial.providers;

import org.opensocial.Request;
import org.opensocial.Response;

public class Provider {

  private String name;
  private String version;
  private String contentType;
  private String rpcEndpoint;
  private String restEndpoint;
  private boolean signBodyHash = true;

  public String getName() {
    return name;
  }

  public String getVersion() {
    if (version == null) {
      return "0.8";
    }

    return version;
  }

  public String getContentType() {
    if (contentType == null) {
      return "application/json";
    }

    return contentType;
  }

  public String getRpcEndpoint() {
    return rpcEndpoint;
  }

  public String getRestEndpoint() {
    return restEndpoint;
  }

  public boolean getSignBodyHash() {
    return signBodyHash;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public void setRpcEndpoint(String rpcEndpoint) {
    this.rpcEndpoint = rpcEndpoint;
  }

  public void setRestEndpoint(String restEndpoint) {
    this.restEndpoint = restEndpoint;
  }

  public void setSignBodyHash(boolean signBodyHash) {
    this.signBodyHash = signBodyHash;
  }

  public void preRequest(Request request) {}

  public void postRequest(Request request, Response response) {}
}

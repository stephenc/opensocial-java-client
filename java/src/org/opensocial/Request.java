package org.opensocial;

import java.util.HashMap;
import java.util.Map;

import org.opensocial.models.Model;

public class Request {

  private String guid;
  private String appId;
  private String selector;
  private String template;
  private String rpcMethod;
  private String restMethod;
  private Map<String, Object> rpcPayloadParameters;
  private Map<String, Object> restPayloadParameters;
  private Map<String, String> restQueryStringParameters;
  private Class<? extends Model> modelClass;

  public Request(String template, String rpcMethod, String restMethod) {
    this.template = template;
    this.rpcMethod = rpcMethod;
    this.restMethod = restMethod;
  }

  public String getGuid() {
    return guid;
  }

  public String getAppId() {
    return appId;
  }

  public String getSelector() {
    return selector;
  }

  public String getTemplate() {
    return template;
  }

  public String getRpcMethod() {
    return rpcMethod;
  }

  public String getRestMethod() {
    return restMethod;
  }

  public Map<String, Object> getRpcPayloadParameters() {
    if (rpcPayloadParameters == null) {
      rpcPayloadParameters = new HashMap<String, Object>();
    }

    return rpcPayloadParameters;
  }

  public Map<String, Object> getRestPayloadParameters() {
    if (restPayloadParameters == null) {
      restPayloadParameters = new HashMap<String, Object>();
    }

    return restPayloadParameters;
  }

  public Map<String, String> getRestQueryStringParameters() {
    if (restQueryStringParameters == null) {
      restQueryStringParameters = new HashMap<String, String>();
    }

    return restQueryStringParameters;
  }

  public Class<? extends Model> getModelClass() {
    if (modelClass == null) {
      return Model.class;
    }

    return modelClass;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public void setSelector(String selector) {
    this.selector = selector;
  }

  public void addRpcPayloadParameter(String key, Object value) {
    if (rpcPayloadParameters == null) {
      rpcPayloadParameters = new HashMap<String, Object>();
    }

    rpcPayloadParameters.put(key, value);
  }

  public void setRpcPayloadParameters(Map<String, Object> parameters) {
    rpcPayloadParameters = parameters;
  }

  public void addRestPayloadParameter(String key, Object value) {
    if (restPayloadParameters == null) {
      restPayloadParameters = new HashMap<String, Object>();
    }

    restPayloadParameters.put(key, value);
  }

  public void setRestPayloadParameters(Map<String, Object> parameters) {
    restPayloadParameters = parameters;
  }

  public void addRestQueryStringParameter(String key, String value) {
    if (restQueryStringParameters == null) {
      restQueryStringParameters = new HashMap<String, String>();
    }

    restQueryStringParameters.put(key, value);
  }

  public void setRestQueryStringParameters(Map<String, String> parameters) {
    restQueryStringParameters = parameters;
  }

  public void setModelClass(Class<? extends Model> modelClass) {
    this.modelClass = modelClass;
  }
}

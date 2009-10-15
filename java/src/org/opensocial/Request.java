package org.opensocial;

import java.util.HashMap;
import java.util.Map;

import org.opensocial.data.Model;

public class Request {

  private String guid;
  private String selector;
  private String template;
  private String rpcMethod;
  private String restMethod;
  private Map<String, String> parameters;
  private Class<? extends Model> modelClass;

  public Request(String template, String rpcMethod, String restMethod) {
    this.template = template;
    this.rpcMethod = rpcMethod;
    this.restMethod = restMethod;
    this.parameters = new HashMap<String, String>();
  }

  public String getGuid() {
    return guid;
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

  public Map<String, String> getParameters() {
    return parameters;
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

  public void setSelector(String selector) {
    this.selector = selector;
  }

  public void setModelClass(Class<? extends Model> modelClass) {
    this.modelClass = modelClass;
  }
}

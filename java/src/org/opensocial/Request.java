/* Copyright (c) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensocial;

import org.opensocial.models.Model;

import java.util.HashMap;
import java.util.Map;

public class Request {

  private String guid;
  private String appId;
  private String moodId;
  private String itemId;
  private String albumId;
  private String groupId;
  private String selector;
  private String friendId;
  private boolean history;
  private String template;
  private String rpcMethod;
  private String restMethod;
  private Map<String, Object> rpcPayloadParameters;
  private Map<String, Object> restPayloadParameters;
  private Map<String, String> restQueryStringParameters;
  private Class<? extends Model> modelClass;

  private String customContentType;
  private String rawPayload;

  public Request(String template, String rpcMethod, String restMethod) {
    this.history = false;
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

  public String getMoodId() {
    return moodId;
  }

  public String getItemId() {
    return itemId;
  }

  public String getAlbumId() {
    return albumId;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getSelector() {
    return selector;
  }

  public String getFriendId() {
    return friendId;
  }

  public boolean getHistory() {
    return history;
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

  public String getCustomContentType() {
    return customContentType;
  }

  public String getRawPayload() {
    return rawPayload;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public void setMoodId(String moodId) {
    this.moodId = moodId;
  }

  public void setItemId(String itemId) {
    this.itemId = itemId;
  }

  public void setAlbumId(String albumId) {
    this.albumId = albumId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public void setSelector(String selector) {
    this.selector = selector;
  }

  public void setFriendId(String friendId) {
    this.friendId = friendId;
  }

  public void setHistory(boolean history) {
    this.history = history;
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

  public <T extends Model> void setRestPayloadParameters(T modelObject) {
    restPayloadParameters = modelObject;
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

  public void setCustomContentType(String contentType) {
    this.customContentType = contentType;
  }

  public void setRawPayload(String payload) {
    this.rawPayload = payload;
  }
}

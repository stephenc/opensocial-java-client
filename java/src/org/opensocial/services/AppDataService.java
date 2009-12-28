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

package org.opensocial.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensocial.Request;
import org.opensocial.models.AppData;

public class AppDataService extends Service {

  private static final String restTemplate =
    "appdata/{guid}/{selector}/{appid}";

  public static Request retrieve() {
    Request request = new Request(restTemplate, "appdata.get", "GET");
    request.setModelClass(AppData.class);
    request.setSelector(Service.SELF);
    request.setGuid(Service.VIEWER);
    request.setAppId(Service.APP);

    return request;
  }

  public static Request update(String key, String value) {
    Map<String, String> data = new HashMap<String, String>();
    data.put(key, value);

    return update(data);
  }

  public static Request update(Map<String, String> data) {
    Request request = new Request(restTemplate, "appdata.update", "PUT");
    request.setSelector(AppDataService.SELF);
    request.setGuid("@viewer");
    request.setAppId(AppDataService.APP);

    // Add RPC payload parameters
    List<String> fields = new ArrayList<String>(data.size());
    for (Map.Entry<String, String> field : data.entrySet()) {
      fields.add(field.getKey());
    }

    request.addRpcPayloadParameter("data", data);
    request.addRpcPayloadParameter("fields", fields);

    // Add REST query string parameters
    StringBuilder fieldsBuilder = new StringBuilder();
    for (Map.Entry<String, String> datum : data.entrySet()) {
      if (fieldsBuilder.length() != 0) {
        fieldsBuilder.append(",");
      }
      fieldsBuilder.append(datum.getKey());
    }

    request.addRestQueryStringParameter("fields", fieldsBuilder.toString());

    // Add REST payload parameters
    for (Map.Entry<String, String> datum : data.entrySet()) {
      request.addRestPayloadParameter(datum.getKey(), datum.getValue());
    }

    return request;
  }

  public static Request delete(String key) {
    return delete(new String[] {key});
  }

  public static Request delete(String[] keys) {
    List<String> keyList = new ArrayList<String>(keys.length);
    for (int i = 0; i < keys.length; i++) {
      keyList.add(keys[i]);
    }

    return delete(keyList);
  }

  public static Request delete(List<String> keys) {
    Request request = new Request(restTemplate, "appdata.delete", "DELETE");
    request.setSelector(Service.SELF);
    request.setGuid(Service.VIEWER);
    request.setAppId(Service.APP);

    // Add RPC parameters
    request.addRpcPayloadParameter("fields", keys);

    // Add REST parameters
    StringBuilder fieldsBuilder = new StringBuilder();
    for (String key : keys) {
      if (fieldsBuilder.length() != 0) {
        fieldsBuilder.append(",");
      }
      fieldsBuilder.append(key);
    }

    request.addRestQueryStringParameter("fields", fieldsBuilder.toString());

    return request;
  }
}

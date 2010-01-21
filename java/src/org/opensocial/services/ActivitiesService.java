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

import java.util.HashMap;
import java.util.Map;

import org.opensocial.Request;
import org.opensocial.models.Activity;

public class ActivitiesService extends Service {

  private static final String restTemplate =
    "activities/{guid}/{selector}/{appid}/{activityid}";

  public static Request create(Activity activity) {
    Request request = new Request(restTemplate, "activities.create", "POST");
    request.setSelector(SELF);
    request.setAppId(APP);
    request.setGuid(ME);

    // Add RPC payload parameters
    Map activityParameter = new HashMap();
    activityParameter.put("body", activity.getBody());
    activityParameter.put("title", activity.getTitle());

    request.addRpcPayloadParameter("activity", activityParameter);

    // Add REST payload parameters
    request.addRestPayloadParameter("body", activity.getBody());
    request.addRestPayloadParameter("title", activity.getTitle());
    request.addRestPayloadParameter("titleId", activity.getTitleId());

    return request;
  }

  public static Request retrieve() {
    Request request = new Request(restTemplate, "activities.get", "GET");
    request.setModelClass(Activity.class);
    request.setSelector(SELF);
    request.setGuid(ME);

    return request;
  }
}

package org.opensocial.services;

import java.util.HashMap;
import java.util.Map;

import org.opensocial.Request;
import org.opensocial.models.Activity;

public class ActivityService extends Service {

  private static final String restTemplate =
    "activities/{guid}/{selector}/{appid}/{activityid}";

  public static Request create(Activity activity) {
    Request request = new Request(restTemplate, "activities.create", "POST");
    request.setSelector(ActivityService.SELF);
    request.setGuid(ActivityService.VIEWER);
    request.setAppId(ActivityService.APP);

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
    request.setSelector(ActivityService.SELF);
    request.setGuid(ActivityService.VIEWER);

    return request;
  }
}

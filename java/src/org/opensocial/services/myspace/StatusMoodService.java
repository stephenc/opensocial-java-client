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

package org.opensocial.services.myspace;

import org.opensocial.Request;
import org.opensocial.models.myspace.StatusMood;
import org.opensocial.services.Service;

public class StatusMoodService extends Service {

  private static final String restTemplate =
    "statusmood/{guid}/{groupId}/{friendId}/{moodId}/{history}";

  public static Request retrieve() {
    return retrieve(ME);
  }

  public static Request retrieve(String guid) {
    Request request = new Request(restTemplate, null, "GET");
    request.setModelClass(StatusMood.class);
    request.setGroupId(SELF);
    request.setGuid(guid);

    return request;
  }

  public static Request retrieveMood(long moodId) {
    Request request = new Request(restTemplate, null, "GET");
    request.setModelClass(StatusMood.class);
    request.setGroupId("@supportedMood");
    request.setMoodId("" + moodId);
    request.setGuid(ME);

    return request;
  }

  public static Request retrieveStatusMoodHistoryForSelf() {
    return retrieveStatusMoodHistory(SELF, null);
  }

  public static Request retrieveStatusMoodHistoryForFriends() {
    return retrieveStatusMoodHistory(FRIENDS, null);
  }

  public static Request retrieveStatusMoodHistoryForFriend(String id) {
    return retrieveStatusMoodHistory(FRIENDS, id);
  }

  private static Request retrieveStatusMoodHistory(String groupId,
      String friendId) {
    Request request = new Request(restTemplate, null, "GET");
    request.setModelClass(StatusMood.class);
    request.setFriendId(friendId);
    request.setGroupId(groupId);
    request.setGuid(ME);

    request.setHistory(true);

    return request;
  }

  public static Request retrieveSupportedMoods() {
    Request request = new Request(restTemplate, null, "GET");
    request.setModelClass(StatusMood.class);
    request.setGroupId("@supportedMood");
    request.setGuid(ME);

    return request;
  }

  public static Request update(StatusMood statusMood) {
    return update(ME, statusMood);
  }

  public static Request update(String guid, StatusMood statusMood) {
    Request request = new Request(restTemplate, null, "PUT");
    request.setGroupId(SELF);
    request.setGuid(guid);

    // Add REST payload parameters
    try {
      long moodId = Long.parseLong(statusMood.getMoodId());
      request.addRestPayloadParameter("moodId", moodId);
    } catch (NumberFormatException e) {
      request.addRestPayloadParameter("moodId", statusMood.getMoodId());
    }
    request.addRestPayloadParameter("status", statusMood.getStatus());

    return request;
  }
}

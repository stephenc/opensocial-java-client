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

import org.opensocial.Request;
import org.opensocial.models.Album;

public class AlbumsService extends Service {

  private static final String restTemplate =
    "albums/{guid}/{groupId}/{albumId}";

  public static Request retrieve() {
    Request request = new Request(restTemplate, "albums.get", "GET");
    request.setModelClass(Album.class);
    request.setGroupId(SELF);
    request.setGuid(ME);

    return request;
  }

  public static Request retrieve(String albumId) {
    Request request = new Request(restTemplate, "albums.get", "GET");
    request.setModelClass(Album.class);
    request.setAlbumId(albumId);
    request.setGroupId(SELF);
    request.setGuid(ME);

    return request;
  }

  public static Request create(Album album) {
    Request request = new Request(restTemplate, "albums.create", "POST");
    request.setGroupId(SELF);
    request.setGuid(ME);

    // Add REST payload parameters
    request.setRestPayloadParameters(album);

    return request;
  }

  public static Request update(String albumId, Album album) {
    Request request = new Request(restTemplate, "albums.update", "PUT");
    request.setAlbumId(albumId);
    request.setGroupId(SELF);
    request.setGuid(ME);

    // Add REST payload parameters
    request.setRestPayloadParameters(album);

    return request;
  }

  public static Request delete(String albumId) {
    Request request = new Request(restTemplate, "albums.delete", "DELETE");
    request.setAlbumId(albumId);
    request.setGroupId(SELF);
    request.setGuid(ME);

    return request;
  }
}

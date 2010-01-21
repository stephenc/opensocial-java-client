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
import org.opensocial.models.MediaItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MediaItemsService extends Service {

  private static final String restTemplate =
    "mediaitems/{guid}/{groupId}/{albumId}/{itemId}";

  public static Request retrieve(String albumId) {
    Request request = new Request(restTemplate, "mediaItems.get", "GET");
    request.setModelClass(MediaItem.class);
    request.setAlbumId(albumId);
    request.setGroupId(SELF);
    request.setGuid(ME);

    return request;
  }

  public static Request retrieve(String itemId, String albumId) {
    Request request = retrieve(albumId);
    request.setItemId(itemId);

    return request;
  }

  public static Request create(MediaItem item, String albumId) {
    Request request = new Request(restTemplate, "mediaItems.create", "POST");
    request.setAlbumId(albumId);
    request.setGroupId(SELF);
    request.setGuid(ME);

    // Add REST payload parameters
    request.setRestPayloadParameters(item);

    return request;
  }

  public static Request upload(MediaItem item, File content, String albumId)
      throws IOException {
    byte[] buffer = new byte[1024];
    InputStream in = new FileInputStream(content);
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    while(true) {
      int read = in.read(buffer);
      if (read <= 0) {
        break;
      }

      out.write(buffer, 0, read);
    }

    String itemContent = new String(out.toString());
    out.close();
    in.close();

    Request request = new Request(restTemplate, "mediaItems.create", "POST");
    request.setAlbumId(albumId);
    request.setGroupId(SELF);
    request.setGuid(ME);

    if (item.getContentType() != null) {
      request.setCustomContentType(item.getContentType());
      request.setRawPayload(itemContent);
    }
    if (item.getType() != null) {
      request.addRestQueryStringParameter("type", item.getType());
    }

    return request;
  }

  public static Request update(String itemId, String albumId, MediaItem item) {
    Request request = new Request(restTemplate, "mediaItems.update", "PUT");
    request.setAlbumId(albumId);
    request.setItemId(itemId);
    request.setGroupId(SELF);
    request.setGuid(ME);

    // Add REST payload parameters
    request.setRestPayloadParameters(item);

    return request;
  }

  public static Request delete(String itemId, String albumId) {
    Request request = new Request(restTemplate, "mediaItems.delete", "DELETE");
    request.setAlbumId(albumId);
    request.setItemId(itemId);
    request.setGroupId(SELF);
    request.setGuid(ME);

    return request;
  }
}

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

package org.opensocial.models;

public class MediaItem extends Model {

  private static final String ID = "id";
  private static final String URL = "url";
  private static final String TYPE = "type";
  private static final String ALBUM_ID = "album_id";
  private static final String MIME_TYPE = "mime_type";
  private static final String THUMBNAIL_URL = "thumbnail_url";

  public String getId() {
    return getFieldAsString(ID);
  }

  public String getAlbumId() {
    return getFieldAsString(ALBUM_ID);
  }

  public String getUrl() {
    return getFieldAsString(URL);
  }

  public String getThumbnailUrl() {
    if (getFieldAsString("thumbnailUrl") != null) {
      return getFieldAsString("thumbnailUrl");
    }

    return getFieldAsString(THUMBNAIL_URL);
  }

  public String getType() {
    return getFieldAsString(TYPE);
  }

  public String getMimeType() {
    return getFieldAsString(MIME_TYPE);
  }

  public void setId(String id) {
    put(ID, id);
  }

  public void setAlbumId(String albumId) {
    put(ALBUM_ID, albumId);
  }

  public void setUrl(String url) {
    put(URL, url);
  }

  public void setThumbnailUrl(String thumbnailUrl) {
    put(THUMBNAIL_URL, thumbnailUrl);
  }

  public void setType(String type) {
    put(TYPE, type);
  }

  public void setMimeType(String mimeType) {
    put(MIME_TYPE, mimeType);
  }
}

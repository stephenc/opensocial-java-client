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

package org.opensocial.models.myspace;

import org.opensocial.models.MediaItem;
import org.opensocial.models.Model;

import java.util.List;

public class Notification extends Model {

  public String getContent() {
    return getTemplateParameter("content");
  }

  public List<String> getRecipients() {
    return getFieldAsList("recipientIds");
  }

  public List<MediaItem> getMediaItems() {
    return getFieldAsList("mediaItems");
  }

  public void setContent(String content) {
    addTemplateParameter("content", content);
  }

  public void addRecipient(String id) {
    addToListField("recipientIds", id);
  }

  public void addMediaItem(MediaItem item) {
    addToListField("mediaItems", item);
  }
}

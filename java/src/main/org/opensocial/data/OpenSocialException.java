/* Copyright (c) 2008 Google Inc.
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


package org.opensocial.data;

/**
 * A generic exception to be thrown when an unexpected or otherwise erroneous
 * event or condition forces an operation or method to terminate during
 * execution.
 *
 * @author Jason Cooper
 */
public class OpenSocialException extends Exception {

  private static final long serialVersionUID = 4702272968569125141L;

  public OpenSocialException(String message) {
    super(message);
  }
}

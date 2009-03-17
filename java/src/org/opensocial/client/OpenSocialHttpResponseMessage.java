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


package org.opensocial.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.oauth.http.HttpResponseMessage;

/**
 * A small implementation of an HttpResponseMessage that does not require
 * org.apache.http.client as a dependency.
 *
 * @author Dan Holevoet
 */
public class OpenSocialHttpResponseMessage extends HttpResponseMessage {
	
	protected int status;
	
	protected OpenSocialHttpResponseMessage(String method, URL url, InputStream body, int status) {
	  super(method, url);
	  this.body = body;
	  this.status = status;
  }
	
	/**
	 * Returns the status code for the response.
	 * 
	 * @return Status code
	 * @throws IOException if the status code is 0 (not set)
	 */
	public int getStatusCode() throws IOException {
		if (this.status == 0) {
			throw new IOException("The response did not have a valid status code.");
		}
		return this.status;
	}
}

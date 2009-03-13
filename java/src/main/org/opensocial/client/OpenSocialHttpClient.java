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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

import net.oauth.http.HttpMessage;
import net.oauth.http.HttpResponseMessage;

/**
 * A small implementation of HttpClient to serve the needs of the OAuth library
 * rather than requiring org.apache.http.client as a dependency.
 *
 * @author Dan Holevoet
 */
public class OpenSocialHttpClient implements net.oauth.http.HttpClient {
	
	/**
	 * Executes the request, sending the request body if applicable.
	 * 
	 * @param request
	 * @return Response message
	 * @throws IOException
	 */
	public HttpResponseMessage execute(HttpMessage request) throws IOException {
		final String method = request.method;
        final URL url = new URL(request.url.toExternalForm());
        final InputStream body = request.getBody();
        final boolean isDelete = DELETE.equalsIgnoreCase(method);
        final boolean isPost = POST.equalsIgnoreCase(method);
        final boolean isPut = PUT.equalsIgnoreCase(method);
        
        String bodyString = getBodyString(body);
        
        OpenSocialHttpResponseMessage response = null;
        if (isPost) {
        	response = send("POST", url, bodyString);
        } else if (isPut) {
        	response = send("PUT", url, bodyString);
        } else if (isDelete) {
            response = send("DELETE", url);
        } else {
        	response = send("GET", url);
        }
        return response;
	}
	
	/**
	 * Converts the request body from an InputStream to a String that can be
	 * consumed by the send method.
	 * 
	 * @param body
	 * @return Request body as a String
	 * @throws IOException
	 */
	private String getBodyString(InputStream body) throws IOException {
		String bodyString = null;
        if (body != null) {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(body));
        	StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
            	builder.append(line + "\n");
            }
            bodyString = builder.toString();
        }
        
        return bodyString;
	}
	
	/**
	 * Executes a request without writing any data in the request's body.
	 * 
	 * @param method
	 * @param url
	 * @return Response message
	 */
	private OpenSocialHttpResponseMessage send(String method, URL url) {
		int responseCode = 0;
		try {
			HttpURLConnection connection = getConnection(method, url);
			responseCode = connection.getResponseCode();
			
			return new OpenSocialHttpResponseMessage(method, url,
					connection.getInputStream(), responseCode);
		} catch (IOException e) {
			return new OpenSocialHttpResponseMessage(method, url, null, 
					responseCode);
		}
	}
	
	/**
	 * Executes a request and writes all data in the request's body to the
	 * output stream.
	 * 
	 * @param method
	 * @param url
	 * @param body
	 * @return Response message
	 */
	private OpenSocialHttpResponseMessage send(String method, URL url,
			String body) {
		int responseCode = 0;
		try {
			HttpURLConnection connection = getConnection(method, url);
			OutputStreamWriter out =
				new OutputStreamWriter(connection.getOutputStream());
	        out.write(body);
	        out.flush();
	        
	        responseCode = connection.getResponseCode();
	        
			return new OpenSocialHttpResponseMessage(method, url,
					connection.getInputStream(), responseCode);
		} catch (IOException e) {
			return new OpenSocialHttpResponseMessage(method, url, null,
					responseCode);
		}
	}
	
	/**
	 * Opens a new HTTP connection for the URL associated with this object.
	 * 
	 * @param method
	 * @param url
	 * @return Opened connection
	 * @throws IOException if URL is invalid, or unsupported
	 */
	private HttpURLConnection getConnection(String method, URL url)
			throws IOException {
		HttpURLConnection connection = null;
		connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod(method);
		connection.connect();
		
		return connection;
	}
}

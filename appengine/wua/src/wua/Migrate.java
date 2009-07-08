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

package wua;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wua.data.PMF;
import wua.data.Update;

/**
 * Performs migration of Update objects from previous schema versions to the
 * most recent schema.
 * 
 * @author api.dwh@google.com (Dan Holevoet)
 */
@SuppressWarnings("serial")
public class Migrate extends HttpServlet {
  
  /**
   * Performs login for the current user by constructing a LoginController
   * object with the supplied parameters. If login is unsuccessful, an
   * exception is thrown and the user is redirected to the main page (with
   * the error).
   * 
   * @param req The incoming request (with access to the session)
   * @param resp The outgoing response 
   */
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    List<Update> updates = Update.getRecentUpdates(pm);
    
    for (Update u : updates) {
      if (u.getGeohash() != null) {
        u.setHasLocation(true);
      } else {
        u.setHasLocation(false);
      }
    }
    pm.close();
  }
}

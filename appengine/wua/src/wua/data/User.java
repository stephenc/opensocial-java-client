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

package wua.data;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import wua.data.PMF;

/**
 * Represents a local user who originates from an OpenSocial container. (A
 * single user who has accounts on multiple containers has multiple User
 * entries.)
 * 
 * @author api.dwh@google.com (Dan Holevoet)
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class User {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  @Persistent
  private String container;
  
  @Persistent
  private String containerId;

  @Persistent
  private String displayName;

  @Persistent
  private String photoUrl;

  /**
   * Class constructor.
   * 
   * @param container The user's originating container
   * @param containerId The user's ID (or representative string)
   * @param displayName The user's name
   * @param photoUrl The user's thumbnail URL
   */
  public User(String container, String containerId, String displayName, String photoUrl) {
      this.container = container;
      this.containerId = containerId;
      this.displayName = displayName;
      this.photoUrl = photoUrl;
  }

  public Long getId() {
    return id;
  }

  public String getContainer() {
    return container;
  }

  public void setContainer(String container) {
    this.container = container;
  }

  public String getContainerId() {
    return containerId;
  }

  public void setContainerId(String containerId) {
    this.containerId = containerId;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getPhotoUrl() {
    return photoUrl;
  }

  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }
  
  /**
   * Returns local details for the currently authenticated User.
   * 
   * @param req The incoming request (with access to the session)
   * @return the authenticated User
   */
  public static User getCurrentUser(HttpServletRequest req) {
    User user = null;
    
    PersistenceManager pm = PMF.get().getPersistenceManager();
    user = getCurrentUser(req, pm);
    pm.close();
    
    return user;
  }
  
  /**
   * Returns local details for the currently authenticated User.
   * 
   * @param req The incoming request (with access to the session)
   * @param pm The PersistenceManager used to connect to the datastore
   * @return the authenticated User
   */
  public static User getCurrentUser(HttpServletRequest req, PersistenceManager pm) {
    Long id = (Long) req.getSession().getAttribute("user_id");
    
    Key k = KeyFactory.createKey(User.class.getSimpleName(), id);
    User user = pm.getObjectById(User.class, k);
    
    return user;
  }
}
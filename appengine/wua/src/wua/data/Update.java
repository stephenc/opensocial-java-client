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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.Query;

import wua.data.PMF;
import wua.geo.Geohash;

/**
 * Represents a single update from a user. The update includes a message and
 * author, along with privacy settings, and potentially, a location.
 * 
 * @author api.dwh@google.com (Dan Holevoet)
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Update {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  @Persistent
  private String author;
  
  @Persistent
  private String authorName;
  
  @Persistent
  private String container;

  @Persistent
  private String content;

  @Persistent
  private Date date;
  
  @Persistent
  private String geohash;
  
  @Persistent
  private Boolean hasLocation;
  
  @Persistent
  private Boolean isPublic;
  
  /**
   * Class constructor.
   * 
   * @param author The author's ID (or representative string)
   * @param authorName The author's name
   * @param container The author's originating container
   * @param content The Update's content
   * @param date The posting date
   * @param geohash The encoded latitude/longitude where the Update was posted
   * @param isPublic Whether or not the Update is public
   */
  public Update(String author, String authorName, String container,
      String content, Date date, String geohash, Boolean hasLocation,
      Boolean isPublic) {
    this.author = author;
    this.authorName = authorName;
    this.container = container;
    this.content = content;
    this.date = date;
    this.geohash = geohash;
    this.hasLocation = hasLocation;
    this.isPublic = isPublic;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }
  
  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public String getContainer() {
    return container;
  }

  public void setContainer(String container) {
    this.container = container;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
  
  public String getGeohash() {
    return geohash;
  }

  public void setGeohash(String geohash) {
    this.geohash = geohash;
  }
  
  /**
   * Returns the update's position by decoding the geohash.
   * 
   * @return the latitude/longitude pair
   */
  public Double[] getPosition() {
    if (this.geohash != null) {
      double[] latlon = new Geohash().decode(this.geohash);
      Double[] position = {new Double(latlon[0]), new Double(latlon[1])};
      
      return position;
    }
    
    return null;
  }
  
  public Boolean getHasLocation() {
    return hasLocation;
  }
  
  public void setHasLocation(Boolean hasLocation) {
    this.hasLocation = hasLocation;
  }
  
  public Boolean getIsPublic() {
    return isPublic;
  }
  
  public void setIsPublic(Boolean isPublic) {
    this.isPublic = isPublic;
  }
  
  /**
   * Returns all Updates.
   * 
   * @param pm The PersistenceManager used to connect to the datastore
   * @return the list of Updates
   */
  @SuppressWarnings("unchecked")
  public static List<Update> getRecentUpdates(PersistenceManager pm) {
    List<Update> updates = null;
    
    String query = "SELECT FROM " + Update.class.getName();
    updates = (List<Update>) pm.newQuery(query).execute();
    
    return updates;
  }
  
  /**
   * Returns all public or private recent Updates.
   * 
   * @param pm The PersistenceManager used to connect to the datastore
   * @param isPublic Whether to return public or private Updates
   * @return the list of Updates
   */
  @SuppressWarnings("unchecked")
  public static List<Update> getRecentUpdates(PersistenceManager pm, Boolean isPublic) {
    List<Update> updates = null;
    
    Query query = pm.newQuery(Update.class);
    query.setFilter("isPublic == publicParam");
    query.declareParameters("Boolean publicParam");
    query.setOrdering("date DESC");
    updates = (List<Update>) pm.newQuery(query).execute(isPublic);
    
    return updates;
  }
  
  /**
   * Returns all friendly Updates.
   * 
   * @param pm The PersistenceManager used to connect to the datastore
   * @param friendIds The IDs of the user's friends
   * @param container The user's originating container
   * @return a map of user IDs and their Updates
   */
  @SuppressWarnings("unchecked")
  public static HashMap<String, List<Update>> getFriendUpdates(PersistenceManager pm,
      ArrayList<String> friendIds, String container) {
    HashMap<String, List<Update>> updates = new HashMap<String, List<Update>>();
    
    for (String id : friendIds) {
      if (!id.equals("")) {
        Query query = pm.newQuery(Update.class);
        query.setFilter("container == '" + container + "' && " +
                        "author == '" + id + "'");
        query.setOrdering("date DESC");
        List<Update> friendUpdates = (List<Update>) pm.newQuery(query).execute();
        updates.put(id, friendUpdates);
      }
    }
    
    return updates;
  }
  
  /**
   * Returns all nearby Updates.
   * 
   * @param pm The PersistenceManager used to connect to the datastore
   * @param llBoundLat The lower left bound latitude of the searched area
   * @param llBoundLong The lower left bound longitude of the searched area
   * @param trBoundLat The top right bound latitude of the searched area
   * @param trBoundLong the top right bound longitude of the searched area
   * @return the list of Updates
   */
  @SuppressWarnings("unchecked")
  public static List<Update> getNearbyUpdates(PersistenceManager pm,
      String llBoundLat, String llBoundLong, String trBoundLat, String trBoundLong) {
    List<Update> updates = null;
    
    Geohash g = new Geohash();
    
    String llBound = g.encode(new Double(llBoundLat), new Double(llBoundLong));
    String trBound = g.encode(new Double(trBoundLat), new Double(trBoundLong));
    
    Query query = pm.newQuery(Update.class);
    query.setFilter("geohash >= '" + llBound + "' && " +
                    "geohash <= '" + trBound + "' && " +
                    "hasLocation == hasLocationParam && " +
                    "isPublic == publicParam");
    query.declareParameters("Boolean hasLocationParam, Boolean publicParam");
    query.setOrdering("geohash, date DESC");
    updates = (List<Update>) pm.newQuery(query).execute(Boolean.TRUE, Boolean.TRUE);
    
    return updates;
  }
  
  /**
   * Saves an Update in the datastore.
   * 
   * @param user The author's ID (or representative string)
   * @param container The author's originating container
   * @param username The author's name
   * @param content The Update's content
   * @param latitude The latitude where the Update was created
   * @param longitude The longitude where the Update was created
   * @param makePublic Whether or not the Update is public
   */
  public static void addUpdate(String user, String container,
      String username, String content, String latitude, String longitude,
      Boolean makePublic) {
    Date now = new Date();
    
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      Geohash g = new Geohash();
      
      String geohash = null;
      Boolean hasLocation = false;
      
      if (latitude != null && !latitude.equals("") && longitude != null &&
          !longitude.equals("")) {
        Double latD = new Double(latitude);
        Double longD = new Double(longitude);
        
        geohash = g.encode(latD, longD);
        hasLocation = true;
      }
      
      Update update = new Update(user, username, container, content, now,
          geohash, hasLocation, makePublic);
      pm.makePersistent(update);
    } finally {
      pm.close();
    }
  }
}

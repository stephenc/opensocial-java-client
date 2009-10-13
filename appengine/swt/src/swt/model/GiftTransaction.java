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

package swt.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * Represents a single gift transaction between two users of an OpenSocial container.
 * 
 * @author cschalk@gmail.com (Chris Schalk)
 */

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class GiftTransaction {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  @Persistent
  private String fromPersonId;

  @Persistent
  private String toPersonId;

  @Persistent
  private String gift;

  public GiftTransaction(String fromPersonId, String toPersonId, String gift) {

    this.fromPersonId = fromPersonId;
    this.toPersonId = toPersonId;
    this.gift = gift;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFromPersonId() {
    return fromPersonId;
  }

  public void setFromPersonId(String fromPersonId) {
    this.fromPersonId = fromPersonId;
  }

  public String getToPersonId() {
    return toPersonId;
  }

  public void setToPersonId(String toPersonId) {
    this.toPersonId = toPersonId;
  }

  public String getGift() {
    return gift;
  }

  public void setGift(String gift) {
    this.gift = gift;
  }

}

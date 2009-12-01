package org.opensocial.models;

public class Activity extends Model {

  public String getId() {
    return getFieldAsString("id");
  }

  public String getBody() {
    return getFieldAsString("body");
  }

  public String getTitle() {
    return getFieldAsString("title");
  }

  public String getTitleId() {
    return getFieldAsString("titleId");
  }

  public void setBody(String body) {
    put("body", body);
  }

  public void setTitle(String title) {
    put("title", title);
  }

  public void setTitleId(String titleId) {
    put("titleId", titleId);
  }
}

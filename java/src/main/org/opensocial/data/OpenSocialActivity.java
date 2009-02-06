package org.opensocial.data;

import java.net.URL;
import java.util.Date;

/**
 * Class representing the OpenSocialActivity object which is used in the REST calls
 * for fetching and creating activities
 * 
 * @author vijayam
 *
 */
public class OpenSocialActivity extends OpenSocialObject {
	
	public String getUserId() {
	    return getStringField("userId");
	}
	
	public String getTitle() {
	    return getStringField("title");
	}
	
	public String getId() {
	    return getStringField("id");
	}
	
	public String getBody() {
	    return getStringField("body");
	}
	
	public String getBodyId() {
	    return getStringField("bodyId");
	}
	
	public String getUrl() {
	    return getStringField("url");
	}
	
/*	public Date getLastUpdatedDate() {
	    OpenSocialField field = this.getField("lastUpdated");

	    if (field != null ) {
	      return new Date(field.getStringValue());
	    }
	    
	    return "";
	}
	*/
	
	
	/**
	 * Generic method which takes the name of the string field and returns its value
	 * 
	 * @param fieldName
	 * @return
	 */
	public String getStringField(String fieldName) {
	    OpenSocialField field = this.getField(fieldName);

	    if (field != null ) {
	      return field.getStringValue();
	    }
	    
	    return "";
	}
	
}

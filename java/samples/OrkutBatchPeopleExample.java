import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.opensocial.client.OpenSocialBatch;
import org.opensocial.client.OpenSocialClient;
import org.opensocial.client.OpenSocialHttpResponseMessage;
import org.opensocial.data.OpenSocialPerson;
import org.opensocial.providers.OrkutSandboxProvider;


public class OrkutBatchPeopleExample {
  public static void main(String[] args) {
    
    // Setup Client
    OrkutSandboxProvider provider = new OrkutSandboxProvider();
    //provider.rpcEndpoint = null;
    
    OpenSocialClient c = new OpenSocialClient(provider);
    c.setProperty(OpenSocialClient.Property.DEBUG, "false");
    c.setProperty(OpenSocialClient.Property.RPC_ENDPOINT, null);
    c.setProperty(OpenSocialClient.Property.CONSUMER_SECRET, "uynAeXiWTisflWX99KU1D2q5");
    c.setProperty(OpenSocialClient.Property.CONSUMER_KEY, "orkut.com:623061448914");
    c.setProperty(OpenSocialClient.Property.VIEWER_ID, "03067092798963641994");
    
    // Create Batch handler
    OpenSocialBatch batch = new OpenSocialBatch();
    HashMap<String, String> params = null;
    
    try {
      // Request people self
      params = new HashMap<String, String>();
      params.put("groupId", OpenSocialClient.SELF);
      batch.addRequest(c.getPeopleService().get(params), "self");
      
      // Request people friends
      params = new HashMap<String, String>();
      params.put("groupId", OpenSocialClient.FRIENDS);
      batch.addRequest(c.getPeopleService().get(params), "friends");
      
      batch.send(c);
          
      // Get a list of all response in request queue
      Set<String> responses = batch.getResponseQueue();
      OpenSocialPerson person = new OpenSocialPerson();
      
      // Interate through each response
      for(Object id : responses) {
          OpenSocialHttpResponseMessage resp = batch.getResponse(id.toString());
          System.out.println("\n"+id+" responded with status: "+resp.getStatusCode()+" with "+resp.getTotalResults()+" results");
          System.out.println("==============================================");
          
          if(id.toString().equals("supportedFields")) {
            List<String> supportedFields = resp.getSupportedFields();
            
            for(int i=0; i < supportedFields.size(); i++) {
              System.out.println(supportedFields.get(i));
            }
          }else{
            List<OpenSocialPerson> people = resp.getPersonCollection();
            
            for(int i=0; i < people.size(); i++) {
              System.out.println(person.getField("name"));
              person = people.get(i);
              System.out.println(person.getField("id"));
              System.out.println(person.getDisplayName());
              System.out.println(person.getField("thumbnailUrl"));
              System.out.println("--------------------------------------");
            }
          }
      }
    } catch (org.opensocial.client.OpenSocialRequestException e) {
      System.out.println("OpenSocialRequestException thrown: " + e.getMessage());
      e.printStackTrace();
    } catch (java.io.IOException e) {
      System.out.println("IOException thrown: " + e.getMessage());
      e.printStackTrace();
    }
  }
}

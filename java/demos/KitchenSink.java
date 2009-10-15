
import org.opensocial.Client;
import org.opensocial.Request;
import org.opensocial.RequestException;
import org.opensocial.Response;
import org.opensocial.auth.AuthScheme;
import org.opensocial.auth.OAuth2LeggedScheme;
import org.opensocial.data.Model;
import org.opensocial.data.Person;
import org.opensocial.providers.MySpaceProvider;
import org.opensocial.providers.OrkutProvider;
import org.opensocial.services.PeopleService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KitchenSink {

  public static void main(String[] args) {
    AuthScheme orkutAuth = new OAuth2LeggedScheme("orkut.com:623061448914",
        "uynAeXiWTisflWX99KU1D2q5", "03067092798963641994");
    Client orkutClient = new Client(new OrkutProvider(), orkutAuth);

    AuthScheme myspaceAuth = new OAuth2LeggedScheme(
        "http://www.myspace.com/495182150",
        "20ab52223e684594a8050a8bfd4b06693ba9c9183ee24e1987be87746b1b03f8",
        "495184236");
    Client myspaceClient = new Client(new MySpaceProvider(), myspaceAuth);

    
    /** orkut single request *************************************************/

    try {
      Request request = PeopleService.get();
      Response response = orkutClient.send(request);
      Person user = (Person) response.getEntry();

      System.out.println("\norkut profile data:");
      System.out.println(user.getId() + " | " + user.getDisplayName());
    } catch (RequestException e) {
      System.out.println("RequestException thrown: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("IOException thrown: " + e.getMessage());
      e.printStackTrace();
    }

    /** MySpace single request ***********************************************/

    try {
      Request request = PeopleService.get();
      Response response = myspaceClient.send(request);
      Person user = (Person) response.getEntry();

      System.out.println("\nMySpace profile data:");
      System.out.println(user.getId() + " | " + user.getDisplayName());
    } catch (RequestException e) {
      System.out.println("RequestException thrown: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("IOException thrown: " + e.getMessage());
      e.printStackTrace();
    }

    /** orkut batch request **************************************************/

    try {
      Request profileDataRequest = PeopleService.get("03067092798963641994");
      Request friendDataRequest = PeopleService.get("03067092798963641994",
          PeopleService.FRIENDS);

      Map<String, Request> requests = new HashMap<String, Request>();
      requests.put("viewer", profileDataRequest);
      requests.put("friends", friendDataRequest);

      Map<String, Response> responses = orkutClient.send(requests);
      Person user = (Person) responses.get("viewer").getEntry();
      List<Model> friends = responses.get("friends").getEntries();

      System.out.println("\norkut profile data:");
      System.out.println(user.getId() + " | " + user.getDisplayName());

      System.out.println("\norkut friends:");
      for (Model friendEntry : friends) {
        Person friend = (Person) friendEntry;
        System.out.println(friend.getId() + " | " + friend.getDisplayName());
      }
    } catch (RequestException e) {
      System.out.println("RequestException thrown: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("IOException thrown: " + e.getMessage());
      e.printStackTrace();
    }

    /** MySpace batch request ************************************************/

    try {
      Request profileDataRequest = PeopleService.get();
      Request friendDataRequest = PeopleService.get(PeopleService.VIEWER,
          PeopleService.FRIENDS);

      Map<String, Request> requests = new HashMap<String, Request>();
      requests.put("viewer", profileDataRequest);
      requests.put("friends", friendDataRequest);

      Map<String, Response> responses = myspaceClient.send(requests);
      Person user = (Person) responses.get("viewer").getEntry();
      List<Model> friends = responses.get("friends").getEntries();

      System.out.println("\nMySpace profile data:");
      System.out.println(user.getId() + " | " + user.getDisplayName());

      System.out.println("\nMySpace friends:");
      for (Model friendEntry : friends) {
        Person friend = (Person) friendEntry;
        System.out.println(friend.getId() + " | " + friend.getDisplayName());
      }
    } catch (RequestException e) {
      System.out.println("RequestException thrown: " + e.getMessage());
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("IOException thrown: " + e.getMessage());
      e.printStackTrace();
    }
  }
}

<%@ page import="org.opensocial.Client" %>
<%@ page import="org.opensocial.Request" %>
<%@ page import="org.opensocial.Response" %>
<%@ page import="org.opensocial.auth.OAuth3LeggedScheme" %>
<%@ page import="org.opensocial.models.Person" %>
<%@ page import="org.opensocial.services.PeopleService" %>

<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
  Person viewer = null;
  List<Person> friends = null;

  OAuth3LeggedScheme scheme =
    (OAuth3LeggedScheme) session.getAttribute("scheme");

  if (scheme != null && scheme.getAccessToken() != null) {
    Client client = new Client(scheme.getProvider(), scheme);

    try {
      Map<String, Request> requests = new HashMap<String, Request>();
      requests.put("friends", PeopleService.getFriends());
      requests.put("viewer", PeopleService.getViewer());

      Map<String, Response> responses = client.send(requests);
      friends = responses.get("friends").getEntries();
      viewer = responses.get("viewer").getEntry();
    } catch (Exception e) {
    }
  }
%>

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Hello App Engine</title>
  </head>

  <body>
    <h1>profile.jsp</h1>

    <a href="/signOut.jsp">Sign out</a>

    <div>
      Welcome, <b><%= viewer.getDisplayName() %> (ID: <%= viewer.getId() %>)</b>!
    </div>
    <div>
      <h4>Friends:</h4>
      <ul>
        <%
          for (Person friend : friends) {
            out.println("<li>" + friend.getDisplayName() + " (ID: " + friend.getId() + ")</li>");
          }
        %>
      </ul>
    </div>
  </body>
</html>

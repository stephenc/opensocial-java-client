<%@ page import="org.opensocial.Client" %>
<%@ page import="org.opensocial.Request" %>
<%@ page import="org.opensocial.Response" %>
<%@ page import="org.opensocial.auth.OAuth3LeggedScheme" %>
<%@ page import="org.opensocial.models.Person" %>
<%@ page import="org.opensocial.providers.GoogleProvider" %>
<%@ page import="org.opensocial.services.PeopleService" %>

<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%
  Person viewer = null;
  List<Person> friends = null;

  OAuth3LeggedScheme authScheme = null;
  OAuth3LeggedScheme.Token requestToken =
    (OAuth3LeggedScheme.Token) session.getAttribute("rt");
  OAuth3LeggedScheme.Token accessToken =
    (OAuth3LeggedScheme.Token) session.getAttribute("at");

  if (requestToken != null && accessToken != null) {
    authScheme =
      new OAuth3LeggedScheme(new GoogleProvider(), "anonymous", "anonymous");
    authScheme.setRequestToken(requestToken);
    authScheme.setAccessToken(accessToken);

    Client client = new Client(new GoogleProvider(), authScheme);
    try {
      Map<String, Request> requests = new HashMap<String, Request>();
      requests.put("friends", PeopleService.getViewerFriends());
      requests.put("viewer", PeopleService.getViewer());

      Map<String, Response> responses = client.send(requests);
      friends = responses.get("friends").getEntries();
      viewer = responses.get("viewer").getEntry();
    } catch (Exception e) {
    }
  }
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
    <title>Content</title>
  </head>
  <body>
    <a href="/signOut.jsp">Sign out</a>

    <div>
      Welcome, <b><%= viewer.getDisplayName() %></b>!
    </div>
    <div>
      <h4>Friends:</h4>
      <ul>
        <%
          for (Person friend : friends) {
            out.println("<li>" + friend.getDisplayName() + "</li>");
          }
        %>
      </ul>
    </div>
  </body>
</html>
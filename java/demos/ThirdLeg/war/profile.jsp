<%--
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
--%>

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

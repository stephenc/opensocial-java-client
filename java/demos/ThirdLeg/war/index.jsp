<%@ page import="org.opensocial.auth.OAuth3LeggedScheme" %>

<%
  OAuth3LeggedScheme scheme =
    (OAuth3LeggedScheme) session.getAttribute("scheme");
%>

<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Third Leg: index</title>
  </head>
  <body>
    <h1>index.jsp</h1>
    <%
      if (scheme != null && scheme.getAccessToken() != null) {
    %>
        You are signed in. <a href="/signOut.jsp">Sign out</a>
    <%
      } else {
    %>
        You are NOT signed in. You must sign in to view your profile.
    <%
      }
    %>

    <a href="/profile.jsp">Profile</a>
  </body>
</html>

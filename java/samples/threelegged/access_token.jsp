<%@ page import="org.opensocial.data.OpenSocialField" %>
<%@ page import="org.opensocial.data.OpenSocialPerson" %>
<%@ include file="common_header.jsp" %>

<%
  String requestToken = request.getParameter("oauth_token");
  String requestSecret = (String) session.getAttribute("token_secret");

  if (requestToken==null || requestSecret==null) {
    out.println("<b>Error:</b> You must authenticate with MySpace before you can see your friends. <a href=\"request_token.jsp\">Return</a>.");
    return;
  }

  String accessToken = (String) session.getAttribute("access_token");
  String accessTokenSecret = (String) session.getAttribute("access_token_secret");
  
  if (accessToken==null || accessTokenSecret==null) {
    Token rt = new Token(requestToken, requestSecret);
    Token at = OpenSocialOAuthClient.getAccessToken(client, provider, rt);

    accessToken = at.token;
    accessTokenSecret = at.secret;
    session.setAttribute("access_token", accessToken);
    session.setAttribute("access_token_secret", accessTokenSecret);
  }

  client.setProperty(OpenSocialClient.Properties.TOKEN, requestToken);
  client.setProperty(OpenSocialClient.Properties.ACCESS_TOKEN, accessToken);
  client.setProperty(OpenSocialClient.Properties.ACCESS_TOKEN_SECRET, accessTokenSecret);
%>

<html>
  <head>
    <title>accessToken</title>
  </head>
  <body>
    <%
      try {
        OpenSocialPerson person = client.fetchPerson();
        out.println("<p>Your name is <b>"+person.getDisplayName()+"</b> and your ID is <b>"+person.getId()+"</b>.</p>");

        String[] fieldNames = person.fieldNames();

        for (String field : fieldNames) {
          out.println("<p><b>"+field+"</b>:<br/>");
          
          OpenSocialField personField = person.getField(field);
          if (!personField.isComplex()) {
            out.println(personField.getStringValue());
          }
          
          out.println("</p>");
        }   
      } catch (Exception e) {
        out.println("<b>Error:</b> An error occurred while attempting to fetch data from MySpace.");
      }
    %>
    <p><a href="sign_out.jsp">Sign out</a></p>
  </body>
</html>

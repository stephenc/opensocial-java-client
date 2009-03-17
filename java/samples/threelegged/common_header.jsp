<%@ page import="org.opensocial.client.OpenSocialClient" %>
<%@ page import="org.opensocial.client.OpenSocialOAuthClient" %>
<%@ page import="org.opensocial.client.OpenSocialProvider" %>
<%@ page import="org.opensocial.client.Token" %>

<%! String consumerKey = "http://graargh.returnstrue.com/coopdog/myspace.xml"; %>
<%! String secretKey = "b2083fcdb7524b9cacc3191a3f34f84e"; %>

<%
  final OpenSocialProvider provider = OpenSocialProvider.valueOf("MYSPACE");
  final OpenSocialClient client = new OpenSocialClient(provider);

  client.setProperty(OpenSocialClient.Properties.CONSUMER_SECRET, secretKey);
  client.setProperty(OpenSocialClient.Properties.CONSUMER_KEY, consumerKey);
%>

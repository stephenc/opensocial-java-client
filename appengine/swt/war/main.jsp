<%@ page import="java.util.Collection" %>
<%@ page import="java.util.List" %>

<%@ page import="org.opensocial.data.OpenSocialPerson" %>

<%@ page import="swt.model.GiftTransaction" %>


<html>
  <head>
    <title>OpenSocial WebApp Tutorial Main</title>
    
<SCRIPT language="JavaScript">
   function giveGift() {
    document.gift_form.submit();
   }
</SCRIPT> 
    
  </head>
<body>
  
  
<h3>Social Website Tutorial </h3>

<jsp:useBean id="osbean" class="swt.client.OSClientBean" scope="session" >
</jsp:useBean>  

<%
 String requestToken = request.getParameter("oauth_token");
 
 OpenSocialPerson person =  osbean.fetchPerson(session, requestToken);
 Collection<OpenSocialPerson> friends =  osbean.fetchFriends(session, requestToken);
 List<GiftTransaction> giftTrans = osbean.getGiftsTransactions(session, person.getDisplayName());
 String uniqueFromPersonID = person.getDisplayName() + "-" + session.getAttribute("provider_name");
%>

<form name="gift_form" action="/giftscontroller" method="post">
Give
<select id="gift" name="gift">
  <option value="a cashew nut">a cashew nut</option>
  <option value="a peanut">a peanut</option>
  <option value="a hazelnut">a hazelnut</option>
  <option value="a red pistachio nut">a red pistachio nut</option>  
</select>
to
<select id="person" name="person">
  <%
      for (OpenSocialPerson friend : friends) {
  %>
  <option value=" <%= friend.getDisplayName() %>"><%= friend.getDisplayName() %></option>
  <%
     }
  %>
</select>
<a onclick="giveGift();" href="javascript: giveGift();">Give!</a>
<input type="hidden" name="fromperson" value="<%= uniqueFromPersonID %>" />
<input type="hidden" name="op" value="processgift" />
</form>

<div>
<%
  if (giftTrans.isEmpty()) {
    out.println("You haven't given any gifts yet."); 
  } else {
    out.println("Gifts you have given:"); 
  }
%>
   <ul>
<%
   for (GiftTransaction g : giftTrans) {     
%>
<li><%= g.getToPersonId() %> received <%= g.getGift() %></li>
<%
}
%></ul>
</div>

<hr/>
  <p><a href="/giftscontroller?op=resetgifts">Clear AppData</a> | <a href="sign_out.jsp">Sign out</a> </p>
  </body>
</html>

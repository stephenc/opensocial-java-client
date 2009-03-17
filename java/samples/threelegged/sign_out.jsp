<%
  session.invalidate();
  response.sendRedirect("request_token.jsp");
%>

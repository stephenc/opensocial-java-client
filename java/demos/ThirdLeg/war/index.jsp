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

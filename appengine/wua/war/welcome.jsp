<%@ page import="java.util.List" %>
<%@ page import="javax.jdo.PersistenceManager" %>
<%@ page import="wua.controllers.LoginController" %>
<%@ page import="wua.data.User" %>
<%@ page import="wua.data.Update" %>
<%@ page import="wua.data.PMF" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%
  LoginController controller = null;

  boolean loggedIn = false;
  String container = "";
  User localUser = null;
  List<Update> updates = null;
  PersistenceManager pm = PMF.get().getPersistenceManager();
  
  String error = null;
  
  try {
    controller = new LoginController(request, response);
    if (controller.client != null) {
      loggedIn = true;
      container = (String) request.getSession().getAttribute("container");
      localUser = User.getCurrentUser(request, pm);
    }
    
    updates = Update.getRecentUpdates(pm, true);
  } catch (Exception e) {
    pm.close();
    error = e.getMessage();
  }
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
  <head>
    <meta name="verify-v1" content="WH5QddXgQHBQ2k3X9/ryXvhUOPl64ABoUObFzOhCyEU=" />
  </head>
	<body>
	<% if (error != null) { %><%= error %><% } %>
	  <link href="/css/app.css" type="text/css" rel="stylesheet">
	  <script type="text/javascript" src="/js/gears_init.js"></script>
	  <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.min.js"></script>
	  <script src="/js/app.js"></script>
	  <script>
	    var ds = new wua.datastore();
	  </script>
		<h3 class="title">Where you at?</h3>
		
		<%
		  if (loggedIn && localUser != null) {
		%>
		<h5 class="login_info">
		  Logged in as <%= localUser.getDisplayName() %> @ <%= container %> <a href="/logout.jsp">[Logout]</a>
		</h5>
		    
		    <!-- <h4 class="add">Add an Update</h4>
		            <form action="/post" method="post" class="add">
		              <input type="text" size="30" id="content" name="content" /><br />
		              <label for="position">Location?</label><input type="checkbox" name="position" id="position" disabled="disabled" />
		              <label for="public">Public?</label><input type="checkbox" name="public" id="public" checked="checked" />
		              <input type="submit" value="Post" />
		              <input type="hidden" value="" name="latitude" id="latitude" />
		              <input type="hidden" value="" name="longitude" id="longitude" />
		            </form> -->
        <h4 class="add">Add an Update</h4>
        <form onsubmit="return ds.postUpdate();" class="add">
          <input type="text" size="30" id="content" name="content" /><br />
          <label for="position">Location?</label><input type="checkbox" name="position" id="position" disabled="disabled" />
          <label for="public">Public?</label><input type="checkbox" name="public" id="public" checked="checked" />
          <input type="submit" value="Post" />
          <input type="hidden" value="" name="latitude" id="latitude" />
          <input type="hidden" value="" name="longitude" id="longitude" />
        </form>
		<%
		  } else {
		%>

		<form action="/login" method="post" class="login_form">
			<select name="container">
				<option value="myspace">MySpace</option>
				<option value="google">Google</option>
				<option value="partuza">Partuza</option>
			</select>
			<input type="submit" value="Login" />
		</form>
		<%
		  }
		%>
		
		<h4 id="update_options"></h4>
		<ul id="updates"></ul>
		
		<div id="map_canvas" style="width: 300px; height:300px;"></div>
		<script type="text/javascript">
		  function onMapLoad() {
	      ds.init();
		  }
		   
      function initialize() {
        google.load("maps", "2", {"other_params":"sensor=true", "callback" : "onMapLoad"});
      }
    </script>
    <script type="text/javascript" src="http://www.google.com/jsapi?key=ABQIAAAASbzn3hpsO9bFlpTqmzcoTBQ_WwkuYBHc7WxOwbOc4DvrFtQdGBRQJx7VibxuVw1RLK5YmP3jp3xQPw&callback=initialize"></script>
  </body>
</html>
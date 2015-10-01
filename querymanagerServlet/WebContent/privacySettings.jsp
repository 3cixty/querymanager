<%@page import="eu.threecixty.Configuration" %>
<%
if (session.getAttribute("accessToken") == null) {
	response.setStatus(400);
%>
Invalid request
<%
} else {
%>
<html>
<head>
<title>Privacy settings</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<link href="<%=Configuration.get3CixtyRoot()%>/_css/table.css" rel="stylesheet" type="text/css">
<link href="<%=Configuration.get3CixtyRoot()%>/_css/privacySettings.css" rel="stylesheet" type="text/css">
</head>

<body>

<div class="table">
  <div class="row">
    <div class="left">
      <div class="button">Show</div>
      <div class="button">Manage</div>
    </div>
    <div class="right" id="mainContent">
    
    </div>
  </div>
</div>

</body>

</html>

<%}%>
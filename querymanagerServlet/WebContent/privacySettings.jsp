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
      <div class="button" id="show">Show Profile</div>
      <div class="button" id="showHeatMap">Show Heatmap</div>
      <div class="button" id="manage">Manage Profile</div>
    </div>
    <div class="spaceCell"></div>
    <div class="right" id="mainContent">
    
    </div>
  </div>
</div>

</body>

</html>

    <script type="text/javascript">
       
       $(document).ready(function () {
       // TODO: need to be updated
           $("#show").click(function() {
    	       $.ajax({url: "<%=Configuration.get3CixtyRoot()%>/getAllUserRelatedInfoByUser",
    	    	       beforeSend: function(xhr) {
    	    	    	   xhr.setRequestHeader('access_token', '<%=session.getAttribute("accessToken")%>');
    	    	       },
    	    		   success: function(result){
    	    			   $("#mainContent").html("<pre>" + result + "</pre>");
    		   			}
    	    	});
    		});
       		
       		$("#manage").click(function() {
	    		$("#mainContent").html("<iframe src='<%=Configuration.get3CixtyRoot()%>/privacySettingsManagement.html?access_token=<%=session.getAttribute("accessToken")%>'></iframe>");
	    		$("#friendUid").val("<%=session.getAttribute("accessToken")%>");
	    	});
       });
       
    </script>

<%}%>
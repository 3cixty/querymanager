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
<meta name="google-translate-customization" content="83bfcc196b36ca47-c4c32ed5fd4f4f55-g50148814a343d054-f"/>

<script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>

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
      <div class="button" id="showHeatMap">Show mobility heatmap</div>
      <div class="button" id="manage">Manage Profile</div>
    </div>
    <div class="spaceCell"></div>
    <div class="right" id="mainContent">
    
    </div>
  </div>
</div>

<div style="position: absolute; top: 0; right: 0; z-index: 10000;" id="google_translate_element"></div>
<script type="text/javascript">
    function googleTranslateElementInit() {
        new google.translate.TranslateElement({
            pageLanguage : 'en',
            layout : google.translate.TranslateElement.InlineLayout.SIMPLE,
            autoDisplay: false,
            multilanguagePage : true
            }, 'google_translate_element');
        }
</script>

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
	    		$("#mainContent").html("<iframe frameborder='0' src='<%=Configuration.get3CixtyRoot()%>/privacySettingsManagement.html?access_token=<%=session.getAttribute("accessToken")%>'></iframe>");
	    	});
       		
            $("#showHeatMap").click(function() {
     	       $.ajax({url: "<%=Configuration.get3CixtyRoot()%>/getMobidotToken",
     	    	       beforeSend: function(xhr) {
     	    	    	   xhr.setRequestHeader('access_token', '<%=session.getAttribute("accessToken")%>');
     	    	       },
     	    		   success: function(msg){
     	    			   var result = eval('(' + msg + ')');
     	    			   var mobidotToken = result.token;
     	    			   
     	    			   $("#mainContent").html("To view your Mobility Profile on Movesmarter server, please click <a href='https://www.3cixty.com/webApp/mobility.php?type=heatmap&token=" + mobidotToken + "'>here</a>");
     		   		   }
     	    	});
     		});
       });
       
    </script>

<%}%>
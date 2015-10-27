<%@page import="eu.threecixty.Configuration" %>
<%@page import="eu.threecixty.cache.AppCache" %>
<%@page import="eu.threecixty.cache.TokenCacheManager" %>
<html>
<head>
<title>Forgot password</title>
<meta name="google-translate-customization" content="83bfcc196b36ca47-c4c32ed5fd4f4f55-g50148814a343d054-f"/>
	 	
	 	
	 	<link href="login/normalize.css" rel="stylesheet" type="text/css" media="screen">
    	<link href="login/assets.css" rel="stylesheet" type="text/css" media="screen">
    	<link href="login/layout.css" rel="stylesheet" type="text/css" media="screen">
    	<link href="login/style2.css" rel="stylesheet" type="text/css" media="screen">
    	<link href="login/style.css" rel="stylesheet" type="text/css" media="screen">
    	<link href="login/fontello.css" rel="stylesheet" type="text/css" media="screen">
    	<link href="login/landing.css" rel="stylesheet" type="text/css" media="screen">
	    
	    <script src="login/jquery-1.js"></script>
	    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
		<script type="text/javascript" src="login/google_translate.js"></script>
		<script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>
		
		<style type="text/css">
    		#customBtn {
		        display: inline-block;
		        background: #dd4b39;
		        color: white;
		        width: 164px;
		        height: 39px;
		        border-radius: 4px;
		        border-color:#dd4b39;
		        white-space: nowrap;
		    }
		    #customBtn:hover {
		        cursor: pointer;
		    }
		    span.label {
		        font-weight: bold;
		    }
		    span.ico {
		        background: url('../v2/btn_red.png') no-repeat;
		        display: inline-block;
		        vertical-align: middle;
		        width: 39px;
		        height: 39px;
		    }
		    span.buttonText {
		        display: inline-block;
		        vertical-align: middle;
		        padding-left: 30px;
		        padding-right: 30px;
		        font-size: 14px;
		        font-weight: bold;
		        /* Use the Roboto font that is loaded in the <head> */
		        font-family: 'Roboto', sans-serif;
	    }
	    .loginInput {
	        width: 330px;
	    }
	    .signinButton {
          	background-color: rgba(255,252,255,1);
   			border: solid 2px rgba(0,0,0,1);
   			-webkit-border-radius: 7px;
      		-moz-border-radius: 7px;
           	border-radius: 7px;
   			padding: 9px;
   			display: inline-block;
   			color: rgba(0,0,0,1);
   			text-shadow: 0px 1px 1px rgba(227,227,227,1)
	    }
	    .link {
	        color: blue;
	    }
	</style>
</head>
<%
    String key = request.getParameter("key");
	AppCache app = TokenCacheManager.getInstance().getAppCache(key);
%>
<body class="login-body">
<div class="wrapper">
	<div class="logo">
		<table>
			<tr>
		  		<td>
    				<div class="logo-icon" style="background-image:url('<%=app.getThumbnail()%>')"/>
    			</td>
    			<td>
    				<div  align="center">
    					<h3 class="privacy-title"> Powered by </h3>
   					</div>
   				</td>
    			<td>
    				<div class="logo-icon" style="background-image:url('<%=Configuration.get3CixtyRoot()%>/3cixty.png')"/>
    			</td>
    		</tr>
    	</table>
    				
    </div>
    <div>
		<form action="./resetPassword" method="post">
		  <input type="hidden" name="key" value='<%=key%>'>
		  <div><br>
		    <input type="text" name="email" id="email" placeholder="Email" required class="loginInput">
		  </div>
		  <div style="height: 3px;"></div>
		  <input class="signinButton" type="submit" value="Reset password">
		</form>
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
	<div id="footer">
		<div class="wrapper">
			<div class="left footer-menu">
				<span>&copy 2015, 3cixty. All rights reserved. <a href="./privacy.jsp">Privacy Statement</a>, <a href="./terms.html">Terms of use</a>.</span>
			</div>
		</div>
	</div>

</body>
</html>
<%@page import="eu.threecixty.Configuration" %>
<%@page import="eu.threecixty.cache.AppCache" %>
<%@page import="eu.threecixty.cache.TokenCacheManager" %>
<%@page import="eu.threecixty.querymanager.rest.OAuthServices" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"  pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1"/>
		<meta name="google-translate-customization" content="83bfcc196b36ca47-c4c32ed5fd4f4f55-g50148814a343d054-f"/>
	 	
    	<link href="login/style2.css" rel="stylesheet" type="text/css" media="screen">
    	<link href="login/landing.css" rel="stylesheet" type="text/css" media="screen">
	    
	    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
		<script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>
		

     	<title>Sign in to 3cixty Platform</title>
		
		<style type="text/css">
    		#customBtn {
		        display: inline-block;
		        background: #dd4b39;
		        color: white;
		        width: 98px;
		        height: 27px;
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
		        width: 23px;
		        height: 23px;
		    }
		    span.buttonText {
		        display: inline-block;
		        vertical-align: middle;
		        padding-left: 0px;
		        padding-top: 2px;
		        font-size: 13px;
		        height: 24px;
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
   			text-shadow: 0px 1px 1px rgba(227,227,227,1);
   			cursor: pointer;
   			font-size: 14px;
	    }
	    .link {
	        color: blue;
	    }
	    #goog-gt-tt { display:none !important; }


		.goog-text-highlight {
		    background-color: inherit !important;
		    box-shadow: 0px 0px 0px #9999aa !important;
		    box-sizing: border-box;
		    position: relative;
		}
		
		.googleNotSupported
		{
		    font-size: 12px;
		    color: #777;
		}
	</style>
</head>

<body class="login-body">
<%
    String key = request.getParameter("key");
    AppCache app = TokenCacheManager.getInstance().getAppCache(key);
    session.setAttribute(OAuthServices.APP_KEY, app);
%>
<script>
  var fbClicked = false;

  // This is called with the results from from FB.getLoginStatus().
  function statusChangeCallback(response) {
    console.log('statusChangeCallback');
    console.log(response);
    // The response object is returned with a status field that lets the
    // app know the current login status of the person.
    // Full docs on the response object can be found in the documentation
    // for FB.getLoginStatus().
    if (response.status === 'connected') {
      // Logged into your app and Facebook.
      if (!fbClicked) {
    	  FB.logout(function(response) {
    		  // user is now logged out
    		});
      }
      else {
    	  testAPI();
      }
    }
  }

  // This function is called when someone finishes with the Login
  // Button.  See the onlogin handler attached to it in the sample
  // code below.
  function checkLoginState() {
	  fbClicked = true;
    FB.getLoginStatus(function(response) {
      statusChangeCallback(response);
    });
  }

  window.fbAsyncInit = function() {
  FB.init({
    //cookie     : true,  // enable cookies to allow the server to access
    cookie     : false,
    appId      : '<%=Configuration.getFacebookAppID()%>',
    xfbml      : true,
    version    : 'v2.3'
  });

  // Now that we've initialized the JavaScript SDK, we call 
  // FB.getLoginStatus().  This function gets the state of the
  // person visiting this page and can return one of three states to
  // the callback you provide.  They can be:
  //
  // 1. Logged into your app ('connected')
  // 2. Logged into Facebook, but not your app ('not_authorized')
  // 3. Not logged into Facebook and can't tell if they are logged into
  //    your app or not.
  //
  // These three cases are handled in the callback function.

  FB.getLoginStatus(function(response) {
    statusChangeCallback(response);
  });

  };

  // Load the SDK asynchronously
  (function(d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s); js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
  }(document, 'script', 'facebook-jssdk'));

  // Here we run a very simple test of the Graph API after login is
  // successful.  See statusChangeCallback() for when this call is made.
  function testAPI() {

    var access_token =   FB.getAuthResponse()['accessToken'];
    console.log('Successful login for: ' + access_token);
    document.write("<form id='form'>");
    document.write("<input type='hidden' name='access_token_outside' value='" + access_token + "'>");
    document.write("<input type='hidden' name='source' value='Facebook'>");
    document.write("<input type='hidden' name='width' value='50'>");
    document.write("<input type='hidden' name='height' value='50'>");
    document.write("<input type='hidden' name='key' value='<%=key%>'>");
    document.write("</form>");
    document.getElementById('form').action = "<%=Configuration.get3CixtyRoot()%>/redirect_uri";
    document.getElementById('form').submit();
  }
</script>
<div class="wrapper">
	<div class="logo" align="center" >
		<table>
			<tr>
		  		<td>
    				<div class="logo-icon" style="background-image:url('<%=app.getThumbnail()%>')">
    				</div>
    			</td>
    			<td>
    				<div  align="center">
    					<h3 class="privacy-title"> powered by </h3>
   					</div>
   				</td>
    			<td>
    				<div class="logo-icon" style="background-image:url('<%=Configuration.get3CixtyRoot()%>/3cixty.png')">
    				</div>
    			</td>
    		</tr>
    	</table>
    				
    </div>
	<div>
		
			<div>
			<form id="form" action="<%=Configuration.get3CixtyRoot()%>/auth">
				<h3 class="privacy-title"> Sign in using your account on</h3>
			    <input type=hidden name="key" value="<%=key%>">
                  <div  align="center">
                  <table>
                    <tr>
                      <td>
                      
				        <button id="customBtn" class="customGPlusSignIn">
	                        <span class="ico"></span>
	                        <span class="buttonText">Google</span>
	                    </button>
						<script>
						  $('#customBtn').click(function(){
					    		$('#form').submit();
						  });
						</script>
	                </td>
	                
				    <td>
						<fb:login-button scope="public_profile,email,user_friends" onlogin="checkLoginState();" data-size="large">
						Facebook
						</fb:login-button>
					</td>
                    </tr>
                  </table>
                  <div class="googleNotSupported">(Google accounts are not supported by the iOS mobile app.)</div>
				  </div>
			 	</form>
			</div>
			<div>
			    <form action="<%=Configuration.get3CixtyRoot()%>/signin" method="post">
				  <h3 class="privacy-title">You can also sign in using an ExplorMI 360 account:</h3>
			      <input type=hidden name="key" value="<%=key%>">
                  <div  align="center">
                    <div>
    					<input type="text" name="email" id="email" placeholder="Email" required class="loginInput">
  					</div>
  					<div>
    					<input type="password" name="password" id="password" placeholder="Password" required class="loginInput">
  					</div>
  					<div style="height: 3px;"></div>
  					<input class="signinButton" type="submit" value=" Sign In ">
			 	  </div>
			 	  <div  align="center">
			 	  	<a href="./forgotPassword.jsp?key=<%=key%>"><font class="link">Did you forget your password?</font></a>
			 	  </div>
			 	  <div  align="center">
			 	  	<a href="./signUp.jsp?key=<%=key%>"><font class="link">Create an ExplorMI 360 account</font></a>
			 	  </div>
			 	</form>
			</div>
	    	<div class="login-benefits">
                <h3 class="privacy-title">Benefits of Signing In</h3>
				
				  
<ul>
  <li>You can save items on your Wish List and access them from the mobile apps of ExplorMI 360.</li>
  <li>You can ask the application to take into account things like the ratings given by your friends.</li>
  <li>If you have been in Milan and have allowed the mobile part of ExplorMI 360 to track your movements 
					 there, you can view summaries of your movements.</li>
</ul>
					  
					  
					 <br>
					<p class="privacy-desc">
					 To see how the privacy of your data will be protected, please check our <a href="<%=Configuration.get3CixtyRoot()%>/privacy.jsp">Privacy Statement</a> and <a href="<%=Configuration.get3CixtyRoot()%>/terms.html">Terms of Use</a>; then hit the "Back" button to return to this page.
				</p>
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
<div id="footer">
	<div class="wrapper">
		<div class="left footer-menu">
			<span>&copy 2015, 3cixty. All rights reserved</span>
		</div>
	</div>
</div>
</body>
</html>

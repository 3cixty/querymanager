<%@page import="eu.threecixty.querymanager.rest.Constants" %>
<%@page import="eu.threecixty.querymanager.rest.SettingsServices" %>
<%@page import="eu.threecixty.Configuration" %>
<%@ page language="java" import="eu.threecixty.profile.ThreeCixtySettings" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="google-signin-client_id" content="<%=Configuration.getGoogleClientId()%>">
<meta name="google-translate-customization" content="83bfcc196b36ca47-c4c32ed5fd4f4f55-g50148814a343d054-f"/>
	 	
<script type="text/javascript" src="login/google_translate.js"></script>
<script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>

<link href="<%=Configuration.get3CixtyRoot()%>/3cixty.css" rel="stylesheet" type="text/css">
<title>Settings</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<style type="text/css">
  .hide { display: none;}
  .show { display: block;}
  
  .note {
  	font-size: 9 px;
  	font-style: italic;
  }
</style>
<script src="https://apis.google.com/js/plusone.js" type="text/javascript"></script>
</head>
<body>

<%
    String accessToken = (String) session.getAttribute("accessToken");
   if (accessToken == null) {
	   response.sendError(400, "Invalid request");
	   //response.sendRedirect(Constants.OFFSET_LINK_TO_ERROR_PAGE + "error.jsp");
   } else  {
        String uid = (String) session.getAttribute("uid");
        int piSum = (Integer) session.getAttribute(SettingsServices.PROFILE_IDENTITIES_KEY);
%>

<form action="<%=Configuration.get3CixtyRoot()%>/linkAccounts" method="post" onsubmit="return validation()">
<div>
    <input type="hidden" name="access_token" value="<%=accessToken%>">
</div>
<div>
    <h4>Why You May Want to Merge your Accounts</h4>
	<ul>
	  <li>It is possible for you to sign into ExplorMI 360 in three different ways:
	      <ul>
	        <li>Via your Google account</li>
	        <li>Via your Facebook account</li>
	        <li>Via a dedicated ExplorMI 360 account that you have created</li>
	      </ul>
	  </li>
	  <li>If you have already signed in in two (or all three) of these ways, you will have two (or three) separate accounts, each with its separate Wish List.</li>
	  <li>You can leave things that way if you like.</li>
	  <li>But if you would like to have just a single account from now on, you can merge the accounts that you now have.</li>
	  <li>Then, no matter which method you use to sign in, you will be visiting the same account with the same Wish List.</li>
	</ul>
	<h4>How to Merge Your Accounts</h4>
	<ol>
	    <li>If one of the accounts that you want to merge is a dedicated ExplorMI 360 account but you are currently signed in via Google or Facebook, please
	        <ul>
	            <li>sign out,</li>
	            <li>sign in via your dedicated account, and</li>
	            <li>click again on the profile picture to get back to this page.</li>
	        </ul>
	    </li>
	    <li>At the bottom of this page, click on the button for signing in via Google or Facebook and then continue the sign-in process
	        <ul>
	            <li>If you see both Google and Facebook buttons below, click on one of them, complete the signing in, and then do the same for the other button</li>
	        </ul>
	    </li>
	    <li>Then click on "Merge accounts now" to complete the procedure.</li>
	</ol>
</div>
<div>
    <input type="hidden" readonly="readonly" value="<%=uid%>" name="uid">
</div>
<%
if (piSum % SettingsServices.GOOGLE_PROFILE_IDENTITIES != 0) {
%>
  <div id="signin-button" class="show">
     <div class="g-signin" data-callback="loginFinishedCallback"
      data-approvalprompt="force"
      data-clientid="<%=Configuration.getGoogleClientId()%>"
      data-scope="https://www.googleapis.com/auth/plus.login"
      data-height="short"
      data-cookiepolicy="single_host_origin"
      >
    </div>
  </div>
<div>
<div id="googleReadyText" style="display: none;">Ready for merging</div>
<input type="hidden" name="googleAccessToken" id="googleAccessToken" value="" placeHolder="Google token" readonly="readonly">
</div>
<% }

if (piSum % SettingsServices.FACEBOOK_PROFILE_IDENTITIES != 0) {
%>
<div>
	<fb:login-button scope="public_profile,email,user_friends" onlogin="checkLoginState();">Sign in
	</fb:login-button>
</div>
<div>
<div id="fbReadyText" style="display: none;">Ready for merging</div>
<input type="hidden" name="fbAccessToken" id="fbAccessToken" value="" placeHolder="Facebook token" readonly="readonly">
</div>
<% } %>
<br>
<div>
<input type="submit" value="Merge accounts now" />
</div>
</form>

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
    var fbTokenInput = document.getElementById("fbAccessToken");
    fbTokenInput.value = access_token;
    showDiv("googleReadyText");
  }
</script>

  <script type="text/javascript">

  function loginFinishedCallback(authResult) {
    if (authResult) {
      if (authResult['error'] == undefined){
    	  document.getElementById("googleAccessToken").value = authResult.access_token;
    	  showDiv("googleReadyText");
  	      $('#signin-button').on("click", function (e) {
	          e.preventDefault();
	      });
      } else {
        console.log('An error occurred');
      }
    } else {
      console.log('Empty authResult');  // Un problème s'est produit
    }
  }
  
  function validation() {
	  var gtk = document.getElementById("googleAccessToken").value;
	  var fbtk = document.getElementById("fbAccessToken").value;
	  if ((gtk == '') && (fbtk == '')) {
		  alert("You haven't yet signed in with neither Google nor Facebook account");
		  return false;
	  }
  }
  
  function showDiv(id) {
	  var divEl = document.getElementById(id);
	  divEl.style.display = 'block';
  }

  </script>

  
<%

   }
   
   Boolean successful = (Boolean) session.getAttribute("successful");
   if (successful != null && successful.booleanValue()) {
	   session.removeAttribute("successful");
	   %>
	   <script type="text/javascript">
	       alert("Successful to update your profile information");
	   </script>
	   <%
   }
%>
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

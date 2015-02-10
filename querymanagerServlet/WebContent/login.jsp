<%@page import="eu.threecixty.Configuration" %>
<%@page import="eu.threecixty.oauth.model.App" %>
<%@page import="eu.threecixty.oauth.OAuthWrappers" %>
<%@page import="eu.threecixty.querymanager.rest.OAuthServices" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sign in to 3cixty</title>
</head>
<body>
<%
    String key = request.getParameter("key");
    App app = OAuthWrappers.retrieveApp(key);
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
    version    : 'v2.2'
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
    document.write("<input type='hidden' name='key' value='<%=key%>'>");
    document.write("</form>");
    document.getElementById('form').action = "<%=Configuration.get3CixtyRoot()%>/redirect_uri";
    document.getElementById('form').submit();
  }
</script>


<form id="form" action="<%=Configuration.get3CixtyRoot()%>/auth">

<div>


    <input type=hidden name="key" value="<%=key%>">
    <label style="width: 60%">Sign in to 3cixty platform. Note that 3cixty uses Google or Facebook account to authenticate the user.</label>
    

    <br>

     <img src="./Red-signin_Long_base_20dp.png" onclick="document.getElementById('form').submit();"
      style="border: none;padding: 0px; margin: 0px; cursor: pointer;" >

<fb:login-button scope="public_profile,email,user_friends" onlogin="checkLoginState();">
Sign in with Facebook
</fb:login-button>
</div>
 
</form>

</body>
</html>

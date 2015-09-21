<%@page import="eu.threecixty.querymanager.rest.Constants" %>
<%@page import="eu.threecixty.Configuration" %>
<%@ page language="java" import="eu.threecixty.profile.ThreeCixtySettings" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="google-signin-client_id" content="<%=Configuration.getGoogleClientId()%>">
<link href="<%=Configuration.get3CixtyRoot()%>/3cixty.css" rel="stylesheet" type="text/css">
<title>Settings</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<style type="text/css">
  .hide { display: none;}
  .show { display: block;}
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
%>

<form action="<%=Configuration.get3CixtyRoot()%>/linkAccounts" method="post">
<div>
    <input type="hidden" name="access_token" value="<%=accessToken%>">
</div>
<div>
</div>
<div>
    <input type="text" readonly="readonly" value="<%=uid%>" name="uid">
</div>
<div>
<a href="<%=Configuration.get3CixtyRoot()%>/getGoogleAccessToken" target="_blank" id="anchorGoogleToken">
<img alt="Sign in with Google" src="./gplus.png">
</a>
</div>
<div>
<input type="text" name="googleAccessToken" id="googleAccessToken" value="">
</div>
<div>
	<fb:login-button scope="public_profile,email,user_friends" onlogin="checkLoginState();">Facebook
	</fb:login-button>
</div>
<div>
<input type="text" name="fbAccessToken" id="fbAccessToken" value="">
</div>

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

  <div id="email" class="hide"></div>
  
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
  }
</script>
  

<script>
	var googleAccessTokenKey = "#access_token=";
	var loc = window.location.href.toString();
	var tokenIndex = loc.indexOf(googleAccessTokenKey);
	if (tokenIndex > 0) {
		document.getElementById("googleAccessToken").value = loc.substring(tokenIndex + googleAccessTokenKey.length);
	    $('#anchorGoogleToken').on("click", function (e) {
	        e.preventDefault();
	    });
	}
</script>

  <script type="text/javascript">
  /*
   * Déclenché lorsque l'utilisateur accepte la connexion, annule ou ferme la
   * boîte de dialogue d'autorisation.
   */
  function loginFinishedCallback(authResult) {
    if (authResult) {
      if (authResult['error'] == undefined){
        gapi.auth.setToken(authResult); // Stocker le jeton renvoyé.
        toggleElement('signin-button'); // Masquer le bouton de connexion lorsque l'ouverture de session réussit.
        getEmail();                     // Déclencher une requête pour obtenir l'adresse e-mail.
      } else {
        console.log('An error occurred');
      }
    } else {
      console.log('Empty authResult');  // Un problème s'est produit
    }
  }

  /*
   * Initie la requête au point de terminaison userinfo pour obtenir l'adresse
   * e-mail de l'utilisateur. Cette fonction dépend de gapi.auth.setToken, qui doit contenir un
   * jeton d'accès OAuth valide.
   *
   * Une fois la requête achevée, le rappel getEmailCallback est déclenché et reçoit
   * le résultat de la requête.
   */
  function getEmail(){
    // Charger les bibliothèques OAuth2 pour activer les méthodes userinfo.
    gapi.client.load('oauth2', 'v2', function() {
          var request = gapi.client.oauth2.userinfo.get();
          request.execute(getEmailCallback);
        });
  }

  function getEmailCallback(obj){
    var el = document.getElementById('email');
    var email = '';

    if (obj['email']) {
      email = 'Email: ' + obj['email'];
    }

    //console.log(obj);   // Retirer les commentaires pour inspecter l'objet complet.

    el.innerHTML = email;
    toggleElement('email');
  }

  function toggleElement(id) {
    var el = document.getElementById(id);
    if (el.getAttribute('class') == 'hide') {
      el.setAttribute('class', 'show');
    } else {
      el.setAttribute('class', 'hide');
    }
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
</body>
</html>

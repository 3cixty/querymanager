<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <meta name="description" content="" />
  <meta name="author" content="" />
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
 <meta charset="UTF-8">
 <link href="${pageContext.request.contextPath}/client/login/normalize.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/client/login/layout.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/client/login/basic.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/client/login/style2.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/client/login/style.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/client/login/fontello.css">
    <script src="${pageContext.request.contextPath}/client/login/jquery-1.js"></script>
  <title>Consent</title>
  <!-- Le styles -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/client/css/bootstrap.min.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/client/css/style.css" />

  <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
  <!--[if lt IE 9]>
  <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
  <![endif]-->
  
  <style type="text/css">
     .imageSize {
        width: 150px;
        height: 150px;
     }
  </style>
  
</head>
<body class="login-body">

<!-- 
<div class="head">
  <img src="${pageContext.request.contextPath}/client/img/surf-oauth.png"/>
</div>
 -->
 
<div class="wrapper">
  	<div class="login2-info">
    	<strong>${client.name}</strong> wants to retrieve your data from <strong>${client.resourceServer.name}</strong>
	</div>
 

    <div class="permission">
	    
	    <div class="logo-icon" style="background-image:url(${client.thumbNailUrl})"></div>
	    <div><img src="${pageContext.request.contextPath}/client/img/arrow.png" /></div>
	    <%-- <div class="icon-right-big"></div>
	    <img alt="${client.name}" title="${client.name}" src="${client.thumbNailUrl}"  style="background-color: #283339;"/>
	      	<img alt="${client.name}" title="${client.name}" src="${client.thumbNailUrl}"  class="imageSize"/> 
	    	
      	 	<img alt="${client.resourceServer.name}" title="${client.resourceServer.name}" src="${client.resourceServer.thumbNailUrl}" class="imageSize"  /> --%>
        <div class="logo-iconPlatform" style="background-image:url(${client.resourceServer.thumbNailUrl})"></div>
	    
    </div>
	
	<div class="connect-area">
    	<form id="accept" method="post" action="${pageContext.request.contextPath}${actionUri}">
      		<input type="hidden" name="AUTH_STATE" value="${AUTH_STATE}"/>
      		<div class="login2-info"><strong>Following data will be shared</strong></div>
      		<div class="login2-selection">
	      		<c:set var="profileScope" value="Profile" />
	      		<fieldset>
			        <c:forEach items="${client.scopes}" var="availableScope">
			        	<c:set var="checked" value="" />
			          	<c:forEach var="requestedScope" items="${requestedScopes}">
			            	<c:if test="${requestedScope eq availableScope}">
			              		<c:set var="checked" value="CHECKED" />
			            	</c:if>
			          	</c:forEach>
			          	<input type="checkbox" id="GRANTED_SCOPES" name="GRANTED_SCOPES" <c:out value="${checked}"/> value="${availableScope}"/>
			          	<span class="consent-label">${availableScope}
			            	<c:if test="${availableScope eq profileScope}">
				            	
			            	</c:if>
			          	</span>
			          	<br/>
			        </c:forEach>
	        		<div class="login-info">
	        		
	             		<strong>Note that checking the profile box you will give this app the ability to read and modify your first name and 
	             		last name in the 3cixty Platform.</strong>

	        			<strong>If you do not select any scopes, no private information can be read or modified by the requesting application.</strong>
	        		</div>
	      		</fieldset>
      		</div>
	      	<fieldset>
		        <div class="form-actions">
			          <button id="user_oauth_approval" name="user_oauth_approval" value="true" type="submit"
			                  class="social-btn btn-success">Grant permission</button>
			        
	        	</div>
	      	</fieldset>
    	</form>
    	<div class="login-benefitsCopyright">
			<div class="login3-info"> 
				© 2015, 3cixty. All Rights Reserved.
			</div>
		</div>
  	</div>
</div>
<!-- 
<div class="foot">
  <p>Powered by <a href="http://www.surfnet.nl/">SURFnet</a>. Fork me on <a href="https://github.com/OpenConextApps/oa-aas/">Github</a>. Licensed under the <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache License 2.0</a>.</p>
</div>
 -->
</body>
</html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1" />
  	<meta name="description" content="" />
  	<meta name="author" content="" />
	<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
	<meta http-equiv="Pragma" content="no-cache" />
	<meta http-equiv="Expires" content="0" />
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
 	<meta charset="UTF-8"/>
	<meta name="google-translate-customization" content="83bfcc196b36ca47-c4c32ed5fd4f4f55-g50148814a343d054-f"/>
 	
 	<link href="${pageContext.request.contextPath}/client/login/normalize.css" rel="stylesheet" type="text/css" media="screen">
    <link href="${pageContext.request.contextPath}/client/login/assets.css" rel="stylesheet" type="text/css" media="screen">
    <link href="${pageContext.request.contextPath}/client/login/layout.css" rel="stylesheet" type="text/css" media="screen">
    <link href="${pageContext.request.contextPath}/client/login/style2.css" rel="stylesheet" type="text/css" media="screen">
    <link href="${pageContext.request.contextPath}/client/login/style.css" rel="stylesheet" type="text/css" media="screen">
    <link href="${pageContext.request.contextPath}/client/login/fontello.css" rel="stylesheet" type="text/css" media="screen">
    <link href="${pageContext.request.contextPath}/client/login/landing.css" rel="stylesheet" type="text/css" media="screen">
	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/client/css/bootstrap.min.css" />
  	<link rel="stylesheet" href="${pageContext.request.contextPath}/client/css/style.css" />
	
	<script src="${pageContext.request.contextPath}/client/login/jquery-1.js"></script>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/client/login/google_translate.js"></script>
	<script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>
   
 	<title>Consent</title>
 	
  <style type="text/css">
     .imageSize {
        width: 150px;
        height: 150px;
     }
  </style>
  
</head>
<body class="login-body">
	<div class="wrapper">
	  	<h3 class="privacy-title"> 
	    	<strong>${client.name}</strong> wants to retrieve your data from <strong>${client.resourceServer.name}</strong>
		</h3>
	    <div>
	    <table>
            <tr>
                <td align="center"><div class="logo-icon" style="background-image:url(${client.thumbNailUrl})"></div>
                </td>
                <td align="center"><div><img src="${pageContext.request.contextPath}/client/img/arrow.png" /></div>
                </td>
                <td align="center"><div class="logo-iconPlatform" style="background-image:url(${client.resourceServer.thumbNailUrl})"></div>
                </td>
            </tr>
        </table>
		</div>
		<div class="privacy-desc">
	    	<form id="accept" method="post" action="${pageContext.request.contextPath}${actionUri}">
	      		<div>
		      		<input type="hidden" name="AUTH_STATE" value="${AUTH_STATE}"/>
		      		<h3 class="privacy-title"> Following data will be shared </h3>
		      			<div class="login-benefits">
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
					          	<span class="privacy-desc">${availableScope}
					            	<c:if test="${availableScope eq profileScope}">
						            	
					            	</c:if>
					          	</span>
					          	<br/>
					        </c:forEach>
			        		<p class="privacy-desc">
			        		
			             		Note that checking the profile box you will give this app the ability to read and modify your first name and 
			             		last name in the 3cixty Platform.
								<br>
			        			If you do not select any scopes, no private information can be read or modified by the requesting application.
			        		</p>
			      		</fieldset>
		      		</div>
			      	<fieldset>
				        <div class="form-actions">
					          <button id="user_oauth_approval" name="user_oauth_approval" value="true" type="submit"
					                  class="social-btn btn-success">Grant permission</button>
					        
			        	</div>
			      	</fieldset>
			      </div>
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
				<span>&copy 2015, 3cixty. All rights reserved</span>
			</div>
		</div>
	</div>
</body>
</html>
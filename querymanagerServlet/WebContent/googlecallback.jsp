<%@page import="eu.threecixty.querymanager.rest.OAuthServices" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Google callback</title>
</head>
<body>

<script type="text/javascript">
    
    function getAccessToken() {
    	var accessTokenKey = "&access_token=";
    	var loc = window.location.href.toString();
    	var tokenIndex1 = loc.indexOf(accessTokenKey);
    	if (tokenIndex1 < 0) {
    		return null;
    	} else {
    		var tokenIndex2 = loc.indexOf("&", tokenIndex1 + accessTokenKey.length);
        	if (tokenIndex2 < 0) {
        		return null;
        	} else {
        		var token = loc.substring(tokenIndex1 + accessTokenKey.length,  tokenIndex2);
        		return token;
        	}
    	}
    }
    
    function redirect() {
    	var token = getAccessToken();
    	if (token == null) {
    		window.location = "./error.jsp";
    	} else {
        	window.location = '<%=OAuthServices.REDIRECT_URI%>?google_access_token=' + token;
    	}
    }    
</script>

<%
    if (session.getAttribute(OAuthServices.ONLY_GOOGLE_ACCESS_TOKEN) == null) {
    	%>
    	<script type="text/javascript">
    	    redirect();
    	</script>
    	<%
    } else {
    	session.removeAttribute(OAuthServices.ONLY_GOOGLE_ACCESS_TOKEN);
    }
%>

</body>
</html>
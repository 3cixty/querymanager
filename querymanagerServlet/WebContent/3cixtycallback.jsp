<%@page import="eu.threecixty.querymanager.rest.Constants" %>
<%@page import="eu.threecixty.querymanager.rest.OAuthServices" %>
<%@page import="eu.threecixty.oauth.model.App" %>
<%@page import="eu.threecixty.oauth.OAuthWrappers" %>
<%@page import="java.net.URLEncoder" %>
<%@page import="java.io.PrintWriter" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>App's link callback. This is just to test</title>
</head>
<body>
<script type="text/javascript">
    function redirect() {
    	var accessTokenKey = "#access_token=";
    	var loc = window.location.href.toString();
    	var tokenIndex1 = loc.indexOf(accessTokenKey);
    	if (tokenIndex1 < 0) {
    		window.location = "./error.jsp";
    	} else {
    		var tokenIndex2 = loc.indexOf("&", tokenIndex1 + accessTokenKey.length);
        	if (tokenIndex2 < 0) {
        		window.location.href = "./error.jsp";
        	} else {
        		var token = loc.substring(tokenIndex1 + accessTokenKey.length,  tokenIndex2);
        		window.location = '<%=OAuthServices.REDIRECT_URI_CLIENT%>?accessToken=' + token;
        	}
    	}
    }
    
    redirect();
</script>


</form>
</body>
</html>
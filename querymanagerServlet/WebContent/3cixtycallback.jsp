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
    	var token = getParam("#access_token=");
    	var refresh_token = getParam("&refresh_token=");
    	var expires_in = getParam("&expires_in=");
    	var scope = getParam("&scope=");
    	if (token == null || refresh_token == null || expires_in == null) {
    		window.location = "./error.jsp";
    	} else {
    		window.location = '<%=OAuthServices.REDIRECT_URI_CLIENT%>?access_token=' + token 
    				+ "&refresh_token=" + refresh_token + "&expires_in=" + expires_in + "&scope=" + scope;
    	}
    }
    
    function getParam(paramKey) {
    	var loc = window.location.href.toString();
    	var tokenIndex1 = loc.indexOf(paramKey);
    	if (tokenIndex1 < 0) {
    		return null;
    	} else {
    		var tokenIndex2 = loc.indexOf("&", tokenIndex1 + paramKey.length);
        	if (tokenIndex2 < 0) {
        		return loc.substring(tokenIndex1 + paramKey.length);
        	} else {
        		var token = loc.substring(tokenIndex1 + paramKey.length,  tokenIndex2);
        		return token;
        	}
    	}
    }
    
   redirect();
</script>


</form>
</body>
</html>
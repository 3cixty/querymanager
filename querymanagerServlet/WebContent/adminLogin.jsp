<%@page import="eu.threecixty.Configuration" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="eu.threecixty.querymanager.rest.Constants"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="google-translate-customization" content="83bfcc196b36ca47-c4c32ed5fd4f4f55-g50148814a343d054-f"/>

<script type="text/javascript" src="login/google_translate.js"></script>
<script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>
		
		
<title>3cixty administrator login page</title>
</head>
<body>

<form action="<%=Configuration.get3CixtyRoot()%>/loginAdmin" method="post">

<div>
    Administrator username
</div>
<div>
    <input type="text" name="username">
</div>

<div>
    Password
</div>
<div>
    <input type="password" name="password">
</div>
<div>
    <input type="submit" name="submit" value="Login">
</div>
<input type="hidden" name="nextAction" value="<%=session.getAttribute("nextAction") == null ? "dashboard.jsp" : session.getAttribute("nextAction") %>">
</form>
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
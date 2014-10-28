<%@page import="eu.threecixty.Configuration" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Login to 3Cixty</title>
</head>
<body>
<div>
<%
    String key = request.getParameter("key");
%>
<form action="<%=Configuration.get3CixtyRoot()%>/auth">
    <input type=hidden name="key" value="<%=key%>">
    <label style="width: 60%">Sign in to 3cixty platform. Note that 3Cixty uses Google account to authenticate the user</label>
    <label style="width: 40%"><input type=submit value="Login" ></label>
</div>
 
</form>
</body>
</html>

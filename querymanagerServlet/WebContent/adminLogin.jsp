<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="eu.threecixty.querymanager.rest.Constants"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Admin login page</title>
</head>
<body>

<form action="./" method="post">

<div>
    Username
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
<input type="hidden" name="nextAction" value="<%=session.getAttribute("nextAction") == null ? "" : session.getAttribute("nextAction") %>">
</form>
</body>
</html>
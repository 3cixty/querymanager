<%@page import="eu.threecixty.Configuration" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Forgotten user management</title>
</head>
<%
    if (session.getAttribute("admin") == null) {
    	session.setAttribute("nextAction", "forgetUser.jsp");
    	response.sendRedirect(Configuration.get3CixtyRoot() + "/adminLogin.jsp");
    }
%>
<form action="<%=Configuration.get3CixtyRoot()%>/forgetUserWithKnows" method="post">
<div>
    <span>3cixty UID</span>
    <input type="text" name="uid">
</div>
<div>
    <span>Know(s)</span>
    <input type="text" name="uid">
</div>

<div>
    <span>Note that if there are more than one know, each know must be separated by a comma. If you want to forget a 3cixty user, please do not fill in the know</span>
</div>

<input type=submit value="forget">

</form>

<form action="<%=Configuration.get3CixtyRoot()%>/logoutAdmin">
  <input type=submit value="Logout">
</form>
</body>
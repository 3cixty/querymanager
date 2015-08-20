<%@page import="eu.threecixty.Configuration" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Admin page</title>
</head>
<body>

<%
    if (session.getAttribute("admin") == null) {
    	response.sendRedirect(Configuration.get3CixtyRoot() + "/adminLogin.jsp");
    } else {
%>
<div><%=session.getAttribute("successful") %></div>
<div>

<a href="./<%=session.getAttribute("nextAction")%>">Continue</a>

</div>

<div>

<a href="<%=Configuration.get3CixtyRoot()%>/logoutAdmin">Logout</a>

</div>
</body>
<% 
    }
%>
</html>

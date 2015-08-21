<%@page import="eu.threecixty.Configuration" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Admin page</title>
</head>
<body>
<div>
    <a href="<%=Configuration.get3CixtyRoot()%>/forgetUser.jsp">Forgotten User</a>
</div>
<div>
    <a href="<%=Configuration.get3CixtyRoot()%>/dashboard.jsp">Application calls report</a>
</div>
<%
    if (session.getAttribute("admin") != null) {
    	%>
    	<div>
            <a href="<%=Configuration.get3CixtyRoot()%>/logoutAdmin">Logout</a>
        </div>
        <%
    }
%>
</body>
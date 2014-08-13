<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="eu.threecixty.querymanager.rest.Constants"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Create an App Key Manager</title>
<style type="text/css">
input:valid {
	background: white;
}
input:invalid {
	background: red;
}
</style>
</head>
<body>
<%
    Boolean admin = (Boolean) session.getAttribute("admin");
    if (admin == null || admin.booleanValue() == false) {
    	response.sendRedirect(Constants.OFFSET_LINK_TO_ERROR_PAGE + "error.jsp");
    } else {
    	%>
    	
    	<form action="../<%=Constants.PREFIX_NAME %>/addappkeyadmin" method="post">
    	    <div>
    	        Username
    	    </div>
    	    <div>
    	        <input type="text" name="username" required>
    	    </div>
    	    
    	    <div>
    	        Password
    	    </div>
    	    <div>
    	        <input type="password" name="password" required>
    	    </div>
    	    
    	    <div>
    	        Re-Password
    	    </div>
    	    <div>
    	        <input type="password" name="password2" required>
    	    </div>
    	    
    	    <div>
    	        First Name
    	    </div>
    	    <div>
    	        <input type="text" name="firstName" required>
    	    </div>
    	    
    	    <div>
    	        Last Name
    	    </div>
    	    <div>
    	        <input type="text" name="lastName" required>
    	    </div>
    	    <div>
    	        <input type="submit" value="Create">
    	    </div>
    	</form>
    	
    	<%
    }
%>

<%
    if (session.getAttribute("successful") != null) {
    	%>
    	<script type="text/javascript">
    	    alert("Successful to create user: " + session.getAttribute("username"));
    	</script>
    	<%
    	session.removeAttribute("successful");
    	session.removeAttribute("username");
    }
%>
</body>
</html>
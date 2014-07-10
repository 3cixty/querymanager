<%@page import="eu.threecixty.profile.ThreeCixtySettings"%>
<%@ page language="java" import="eu.threecixty.keys.*"  contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Request a developer key</title>
<style type="text/css">
input[type="text"] {
	width: 480px;
	font-size: 13px;
}

input:valid {
	background: white;
}
input:invalid {
	background: red;
}

span {
	display: inline-block;
	width: 240px;
}

</style>

</head>
<body>

<%
    ThreeCixtySettings settings = (ThreeCixtySettings) session.getAttribute("settings");
    if (session.getAttribute("uid") == null || settings == null) {
	    response.sendRedirect("../error.jsp");
    } else {
        AppKey appKey = (AppKey) session.getAttribute("appkey");
        String accessToken = (String) session.getAttribute("accessToken");
        String domain = (appKey == null ? "" : appKey.getAppName());
%>

<form action="../services/key/performKeyRequest" method="post">
<div>
    <span>First Name</span>
    <input type="text" name="firstName" value="<%=settings.getFirstName()%>" readonly="readonly">
</div>
<div>
    <span>Last Name</span>
    <input type="text" name="lastName" value="<%=settings.getLastName()%>" readonly="readonly">
</div>
<div>
    <span>Email</span>
    <input type="text" name="email" required value="<%=(appKey == null ? "" : appKey.getOwner().getEmail())%>">
</div>
<div>
    <span>Application Type</span>
    <select name="domain">
        <option <%=domain.equals("Crowdsourcing") ? "selected" : "" %>>Crowdsourcing</option>
        <option <%=domain.equals("Exploration") ? "selected" : "" %>>Exploration</option>
        <option <%=domain.equals("Mobile Application (TI)") ? "selected" : "" %>>Mobile Application (TI)</option>
        <option <%=domain.equals("App Challenge") ? "selected" : "" %>>App Challenge</option>
        <option <%=domain.equals("Others") ? "selected" : "" %>>Others</option>
    </select>
</div>
<div style="height: 20px;"></div>

<%
    if (appKey == null) {
%>
<div align="center" style="width: 720px;">
        <input type="submit" value="Request a App Key">
</div>
</form>
<%
    } else {
%>
</form>

        <div>
            <span>Your development key</span>
            <input type="text" readonly="readonly" id="yourkey" value="<%=appKey.getValue()%>">
            <input type="button" data-clipboard-target="yourkey" value="Copy" id="d_clip_button"  >
            
        <script type="text/javascript">
            var parentScriptPath = "../";
        </script>            

    <script type="text/javascript" src="../javascripts/vendor/jquery.min.js"></script>
    <script type="text/javascript" src="../javascripts/v2.x/boot.js"></script>

        </div>

<%
    }
%>


<%
    }
%>
</body>
</html>
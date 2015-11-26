<%@page import="eu.threecixty.Configuration" %>
<%@page import="eu.threecixty.querymanager.rest.AdminServices" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="google-translate-customization" content="83bfcc196b36ca47-c4c32ed5fd4f4f55-g50148814a343d054-f"/>
	 	
<script type="text/javascript" src="login/google_translate.js"></script>
<script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>

<title>Admin page</title>
</head>
<body>

<%
    if (session.getAttribute("admin") == null) {
    	response.sendRedirect(Configuration.get3CixtyRoot() + "/adminLogin.jsp");
    } else {
%>
<div><%=session.getAttribute(AdminServices.RESULT_ATTR) %></div>
<div>

<a href="./<%=session.getAttribute("nextAction")%>">Continue</a>

</div>

<div>

<a href="<%=Configuration.get3CixtyRoot()%>/logoutAdmin">Logout</a>

</div>

<% 
    }
%>
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

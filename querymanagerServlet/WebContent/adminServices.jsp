<%@page import="eu.threecixty.Configuration" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="google-translate-customization" content="83bfcc196b36ca47-c4c32ed5fd4f4f55-g50148814a343d054-f"/>
	 	
<script type="text/javascript" src="login/google_translate.js"></script>
<script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>
		
<title>Administrator page</title>
</head>
<body>
<div>
    <a href="<%=Configuration.get3CixtyRoot()%>/forgetUser.jsp">Forget User</a>
</div>
<div>
    <a href="<%=Configuration.get3CixtyRoot()%>/dashboard.jsp">Application Platform API calls report</a>
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
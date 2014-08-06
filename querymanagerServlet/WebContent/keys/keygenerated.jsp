<%@page import="eu.threecixty.querymanager.rest.Constants" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Key generated</title>


</head>
<body>
<%
	if (session.getAttribute("key") == null) {
		response.sendRedirect(Constants.OFFSET_LINK_TO_ERROR_PAGE + "error.jsp");
	} else  {
%>

        <div>
            <div id="bloc1" >
                Your development key:
            </div>
            <div id="bloc2">
            <textarea id="fe_text" rows="10" cols="25" readonly="readonly" style="font-family: courier;font-size: 11px;"><%=session.getAttribute("key")%></textarea>
            </div>
        </div>
        <div>
 <p><button id="d_clip_button" class="my_clip_button" title="Copy the key" data-clipboard-target="fe_text" data-clipboard-text="Default clipboard text from attribute"><b>Copy</b></button></p>
        </div>

<%
	}
%>

        <script type="text/javascript">
            var parentScriptPath = "../";
        </script>

    <script type="text/javascript" src="../javascripts/vendor/jquery.min.js"></script>
    <script type="text/javascript" src="../javascripts/v2.x/boot.js"></script>


</body>
</html>
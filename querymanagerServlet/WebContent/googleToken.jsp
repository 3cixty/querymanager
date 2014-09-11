<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Get Google access token</title>
</head>
<body>

<form >
    <input type=button value="Login to Google to get access token" onclick="openWindowToLoginGoogle();">
    
    <script type="text/javascript">
        function openWindowToLoginGoogle() {
        	window.open("./getGoogleAccessToken", "_blank",
        			"toolbar=yes, scrollbars=yes, resizable=yes, top=500, left=500, width=400, height=400");
        }
    </script>
    
</form>
</body>
</html>
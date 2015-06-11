<%@page import="eu.threecixty.Configuration" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" import="eu.threecixty.querymanager.rest.Constants"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>KB</title>
</head>
<body>

<form id="form" action="<%=Configuration.get3CixtyRoot()%>/setKB" method="post">

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


<div id="divKB">
<input type="radio" name="kb" value="http://3cixty.eurecom.fr" id="eurecomKB" checked>EURECOM KB</br>
<input type="radio" name="kb" value="http://91.250.81.138:8890" id="hostEuropeKB">HostEurope KB
</div>

<div>
    <input type="hidden" name="virtuosoServer" id="virtuosoServer" value="http://3cixty.eurecom.fr">
</div>

<div>
   <input type="checkbox" name="reset" value="reset" id="reset" onclick="prepareToSubmit()" > Reset KB to default configuration
</div>

<div>
    <input type="submit" name="submit" value="submit">
</div>

<script type="text/javascript">
    function prepareToSubmit() {
    	if (document.getElementById("eurecomKB").checked) document.getElementById("virtuosoServer").value = document.getElementById("eurecomKB").value;
    	else document.getElementById("virtuosoServer").value = document.getElementById("hostEuropeKB").value;
    	var divKB = document.getElementById("divKB");
    	var form = document.getElementById("form");
    	if (document.getElementById("reset").checked) {
    		divKB.style.display = "none";
    		form.action = "<%=Configuration.get3CixtyRoot()%>/resetKB";
    	} else {
    		divKB.style.display = "";
    		form.action = "<%=Configuration.get3CixtyRoot()%>/setKB";
    	}
    }
</script>

</form>
</body>
</html>
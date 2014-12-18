<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User Profile & Tray Management</title>
</head>
<body>

<form id="formProfile" action="./getAllProfiles" method="post">

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

<div>
    Migrate from servlet version (e.g: v2) to current servlet
</div>
<div>
    <input type="text" name="version">
</div>

<div>
    <input type="button" name="showProfiles" value="Show all user profiles" onclick="cmdShowProfiles();">
    <input type="button" name="showTrays" value="Show all trays" onclick="cmdShowTrays();">
    <input type="button" name="migrateData" value="Migrate data" onclick="cmdMigrateData();">
</div>


<script type="text/javascript">
    function cmdShowProfiles() {
    	var frm = document.getElementById("formProfile");
    	frm.action = "./getAllProfiles";
    	frm.submit();
    }
    
    function cmdShowTrays() {
    	var frm = document.getElementById("formProfile");
    	frm.action = "./allTrays";
    	frm.submit();
    }
    
    function cmdMigrateData() {
    	var frm = document.getElementById("formProfile");
    	frm.action = "./copyProfiles";
    	frm.submit();
    }
</script>

</form>
</body>
</html>
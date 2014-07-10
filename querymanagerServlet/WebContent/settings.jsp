<%@ page language="java" import="eu.threecixty.profile.ThreeCixtySettings" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link href="./3cixty.css" rel="stylesheet" type="text/css">
<title>Settings</title>
</head>
<body>

<%
    String accessToken = (String) session.getAttribute("accessToken");
    String key = (String) session.getAttribute("key");

   if (session.getAttribute("uid") == null || accessToken == null || key == null) {
	   response.sendRedirect("./error.jsp");
   } else  {
        ThreeCixtySettings settings = (ThreeCixtySettings) session.getAttribute("settings");
%>

<form action="./services/settings/save" method="post">
<div>
    <input type="hidden" name="accessToken" value="<%=accessToken%>">
    <input type="hidden" name="key" value="<%=key%>">
</div>
<div>
<span >Google UID</span>
</div>
<div>
    <input type="text" readonly="readonly" value="<%=settings.getUid()%>" name="uid">
</div>
<div>
 <span >First Name<font color="red">*</font></span>
</div>
<div>
    <input type="text" name="firstName" value="<%=settings.getFirstName() == null ? "" : settings.getFirstName()%>" required>
</div>
<div><span >Last Name<font color="red">*</font></span></div>
<div>
    <input type="text" name="lastName" value="<%=settings.getLastName() == null ? "" : settings.getLastName()%>" required>
</div>
<div><span >Country</span></div>
<div>
    <input type="text" name="countryName" value="<%=settings.getCountryName() == null ? "" : settings.getCountryName()%>">
</div>
<div><span >City</span></div>
<div>
    <input type="text" name="townName" value="<%=settings.getTownName() == null ? "" : settings.getTownName()%>">
</div>
<div><span >Latitude</span></div>
<div>
    <input type="text" name="lat" value="<%=settings.getCurrentLatitude() == 0 ? "" : settings.getCurrentLatitude()%>">
</div>
<div><span >Longitude</span></div>
<div>
    <input type="text" name="lon" value="<%=settings.getCurrentLongitude() == 0 ? "" : settings.getCurrentLongitude()%>">
</div>
<div><span >Mobidot Account</span></div>
<div>
<input type="hidden" name="sources" value="Mobidot">
<input type="hidden" name="pi_ats" value="">
    <input type="text" name="pi_ids" value="<%=settings.getIdentities() == null ? "" : settings.getIdentities().size() == 0 ? "" : (settings.getIdentities().get(0) == null ? "" : (settings.getIdentities().get(0).getHasUserAccountID() == null ? "" : settings.getIdentities().get(0).getHasUserAccountID()))%>">
</div>
<div style="height: 10px;"></div>
<div align="justify" style="font-size: 11px;"  >
Disclaimer: The information marked by red star are required. For Mobidot account, if the user specifies it, it will help us crawl the mobility profile and associate mobility related preferences while augmenting the query. If the Mobidot account is not specified, we will not augment the query based on mobility preferences and we will not crawl the mobility profile of the user.
</div>
<div style="height: 10px;"></div>
<div align="center"  >
    <input type="submit" value="Save">
    <input type="button" value="Cancel" onclick="reset();">
</div>
</form>

<script type="text/javascript">
    function reset() {
    	var firstName = document.getElementById("firstName");
    	firstName.value = "<%=settings.getFirstName() == null ? "" : settings.getFirstName()%>";
    	var lastName = document.getElementById("lastName");
    	lastName.value = "<%=settings.getLastName() == null ? "" : settings.getLastName()%>";
    	var countryName = document.getElementById("countryName");
    	countryName.value = "<%=settings.getCountryName() == null ? "" : settings.getCountryName()%>";
    	var townName = document.getElementById("townName");
    	townName.value = "<%=settings.getTownName() == null ? "" : settings.getTownName()%>";
    	var lat = document.getElementById("lat");
    	lat.value = "<%=settings.getCurrentLatitude() == 0 ? "" : settings.getCurrentLatitude()%>";
    	var lon = document.getElementById("lon");
    	lon.value = "<%=settings.getCurrentLongitude() == 0 ? "" : settings.getCurrentLongitude()%>";
    	var pi_id = document.getElementById("pi_id");
    	pi_id.value = "<%=settings.getIdentities() == null ? "" : settings.getIdentities().size() == 0 ? "" : (settings.getIdentities().get(0) == null ? "" : (settings.getIdentities().get(0).getHasUserAccountID() == null ? "" : settings.getIdentities().get(0).getHasUserAccountID()))%>";
    }
</script>

<%

   }
   
   Boolean successful = (Boolean) session.getAttribute("successful");
   if (successful != null && successful.booleanValue()) {
	   session.removeAttribute("successful");
	   %>
	   <script type="text/javascript">
	       alert("Successful to update your profile information");
	   </script>
	   <%
   }
%>
</body>
</html>
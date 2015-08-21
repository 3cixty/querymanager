<%@page import="eu.threecixty.Configuration" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Forgotten user management</title>
<style type="text/css">
.div-table{
  display:table;         
  width:auto;         
  background-color:#eee;         
  border:1px solid  #666666;         
  border-spacing:5px;/*cellspacing:poor IE support for  this*/
}
.div-table-row{
  display:table-row;
  width:auto;
  clear:both;
}
.div-table-col{
  float:left;/*fix for  buggy browsers*/
  display:table-column;         
  width:200px;         
  background-color:#ccc;  
}
</style>
</head>
<%
    if (session.getAttribute("admin") == null) {
    	session.setAttribute("nextAction", "forgetUser.jsp");
    	response.sendRedirect(Configuration.get3CixtyRoot() + "/adminLogin.jsp");
    } else {
%>
<form action="<%=Configuration.get3CixtyRoot()%>/forgetUserWithKnows" method="post">
<div class="div-table">
<div class="div-table-row">
    <div class="div-table-col">3cixty UID</div>
    <div class="div-table-col">
    	<input type="text" name="uid">
    </div>
</div>
<div class="div-table-row">
    <div class="div-table-col">Know(s)</div>
    <div class="div-table-col">
    	<input type="text" name="uid">
    </div>
</div>
</div>
<div>
    <span>Note that if there are more than one know, each know must be separated by a comma. If you want to forget a 3cixty user, please do not fill in the know</span>
</div>

<input type=submit value="Forget">

</form>

<form action="<%=Configuration.get3CixtyRoot()%>/logoutAdmin">
  <input type=submit value="Logout">
</form>
</body>
<%
    }
%>
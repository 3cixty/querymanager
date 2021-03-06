<%@page import="eu.threecixty.Configuration" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="google-translate-customization" content="83bfcc196b36ca47-c4c32ed5fd4f4f55-g50148814a343d054-f"/>

<script type="text/javascript" src="login/google_translate.js"></script>
<script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>
		

<title>Forget user management</title>
<style type="text/css">
.div-table{
  display:table;         
  width:auto;         
  background-color:#eee;      
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

.div-table-col1{
  float:left;/*fix for  buggy browsers*/
  display:table-column;         
  width:auto;         
  background-color:#ccc;  
}
</style>
</head>
<body>
<%
    if (session.getAttribute("admin") == null) {
    	session.setAttribute("nextAction", "forgetUser.jsp");
    	response.sendRedirect(Configuration.get3CixtyRoot() + "/adminLogin.jsp");
    } else {
%>

<form id="formToForget" action="<%=Configuration.get3CixtyRoot()%>/forgetUserWithKnows" method="post">
<div class="div-table">
<div class="div-table-row">
    <div class="div-table-col">3cixty UID</div>
    <div class="div-table-col1">
    	<input type="text" name="uid">
    </div>
</div>
<div class="div-table-row">
    <div class="div-table-col">Know(s)</div>
    <div class="div-table-col1">
    	<input type="text" name="knows">
    </div>
</div>
</div>
<div>
    <span>Note that if there are more than one know, each know must be separated by a comma. If you want to forget a 3cixty user, please do not fill in the know</span>
</div>

</form>

<div class="div-table">
  <div class="div-table-row">
    <div class="div-table-col1"><input type=button value="Forget" onClick="formToForgetSubmit();"></div>
    <div class="div-table-col1">
    
      <form action="<%=Configuration.get3CixtyRoot()%>/logoutAdmin">
        <input type=submit value="Logout">
      </form>
    
    </div>
  </div>
</div>

<script type="text/javascript">
    function formToForgetSubmit() {
    	var form = document.getElementById("formToForget");
    	form.submit();
    }
</script>
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
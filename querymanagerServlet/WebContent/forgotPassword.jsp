<html>
<head>
<title>Forgot password</title>
</head>
<%
    String key = request.getParameter("key");
%>
<form action="./resetPassword" method="post">
  <input type="hidden" name="key" value='<%=key%>'>
  <div>
    <span>Email</span>
    <input type="text" name="email" id="email">
  </div>
  <input type="submit" value="Reset password">
</form>
</html>
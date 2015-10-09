<html>
<head>
<meta name="google-translate-customization" content="83bfcc196b36ca47-c4c32ed5fd4f4f55-g50148814a343d054-f"/>

<script type="text/javascript" src="login/google_translate.js"></script>
<script type="text/javascript" src="//translate.google.com/translate_a/element.js?cb=googleTranslateElementInit"></script>

<title>Forgot password</title>
</head>
<body>
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
<%@page import="eu.threecixty.cache.AppCache" %>
<%@page import="eu.threecixty.querymanager.rest.OAuthServices" %>
<html>
<head>
<title>Sign up for a new account</title>

<style type="text/css">
.main > div {
  display: inline-block;
  width: 49%;
  margin-top: 10px;
}

.two .register {
  border: none;
}
.two .register h3 {
  border-bottom-color: #909090;
}
.two .register .sep {
  border-color: #909090;
}

.register {
  width: 500px;
  margin: 10px auto;
  padding: 10px;
  border: 7px solid #72B372;
  border-radius: 10px;
  font-family: "Helvetica Neue", Helvetica, Arial, sans-serif;
  color: #444;
  background-color: #F0F0F0;
  box-shadow: 0 0 20px 0 #000000;
}
.register h3 {
  margin: 0 15px 20px;
  border-bottom: 2px solid #72B372;
  padding: 5px 10px 5px 0;
  font-size: 1.1em;
}
.register div {
  margin: 0 0 15px 0;
  border: none;
}
.register label {
  display: inline-block;
  width: 25%;
  text-align: right;
  margin: 10px;
}
.register input[type=text], .register input[type=password], .register input[type=email] {
  width: 65%;
  font-family: "Lucida Grande","Lucida Sans Unicode",Tahoma,Sans-Serif;
  padding: 5px;
  font-size: 0.9em;
  border-radius: 5px;
  background: rgba(0, 0, 0, 0.07);
}
.register input[type=text]:focus, .register input[type=password]:focus, .register input[type=email]:focus {
  background: #FFFFFF;
}
.register .button {
  font-size: 1em;
  border-radius: 8px;
  padding: 10px;
  border: 1px solid #59B969;
  box-shadow: 0 1px 0 0 #60BD49 inset;
  background: #63E651;
  background: -webkit-linear-gradient(#63E651, #42753E);
  background: -moz-linear-gradient(#63E651, #42753E);
  background: -o-linear-gradient(#63E651, #42753E);
  background: linear-gradient(#63e651, #42753e);
}
.register .button:hover {
  background: #51DB1C;
  background: -webkit-linear-gradient(#51DB1C, #6BA061);
  background: -moz-linear-gradient(#51DB1C, #6BA061);
  background: -o-linear-gradient(#51DB1C, #6BA061);
  background: linear-gradient(#51db1c, #6ba061);
}
.register .sep {
  border: 1px solid #72B372;
  position: relative;
  margin: 35px 20px;
}
.register .or {
  position: absolute;
  width: 50px;
  left: 50%;
  background: #F0F0F0;
  text-align: center;
  margin: -10px 0 0 -25px;
  line-height: 20px;
}

.pwdNote {
  font-family: serif;
  color: gray;
  font-style: italic;
}

</style>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="password.js"></script>
</head>
<%
String key = request.getParameter("key");
if (key != null) {
%>
 <div class="main">
      <div class="one">
        <div class="register">
          <h3>Create your account</h3>
          <form id="reg-form" onsubmit="return validation()" method="post" action="./signUp">
            <div>
              <label for="firstName">First Name</label>
              <input type="text" id="firstName" name="firstName" spellcheck="false" required/>
            </div>
            <div>
              <label for="lastName">Last Name</label>
              <input type="text" id="lastName" name="lastName" spellcheck="false" required/>
            </div>
            <div>
              <label for="email">Email</label>
              <input type="email" id="email" name="email" spellcheck="false" required/>
            </div>
            <div>
              <label for="password">Password</label>
              <input type="password" id="password" name="password" required />
            </div>
            <div>
              <label for="password-again">Password Again</label>
              <input type="password" id="password-again" name="password-again" required />
            </div>
            <div>
              <label></label>
              <input type="submit" value="Create Account" id="create-account" class="button"/>
            </div>
            <input type="hidden" name="key" value='<%=key%>'>
          </form>
          
          <span class="pwdNote">Password must contain at least one digit, one lower case, one upper case, and between 8 and 30 characters</span>
        </div>
      </div>
    </div>
    
    <script type="text/javascript">
       

       
       $(document).ready(function () {
    	   
    	   window.resizeTo(screen.width * 0.6, screen.height * 0.6);
       
           $("#email").blur(function() 
    	      {
    	        var pattern = new RegExp(/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
    	        var emailaddress = $("#email").val();
    		    
    		    if(!pattern.test(emailaddress)) 
    		        alert("Email is invalid");
    		    else {
    		        $.ajax({url: "./existEmail?email=" + emailaddress, success: function(result){
    		            if ("true" == result) {
    		            	alert("Email already existed!");
    		            }
    		        }});
    		    }  
    		});
       });
       
    </script>
    
</html>

<%
} else {
	%>
	Don't find application key!!!
	<%
}
%>
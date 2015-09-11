       function checkPassword(inputtxt)   
       {   
           var paswd=  /(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,30}/;
           if (paswd.test(inputtxt))   
           {
              return true;  
           }  
           else  
           {   
             return false;  
           }  
       }
       
       function validation()
       {
         var pwd1 = document.getElementById("password").value;
         var pwd2 = document.getElementById("password-again").value;
         if (pwd1 != pwd2)
         {
           alert("Passwords do not match");
           document.getElementById("password-again").focus();
           return false;
         } else {
        	 if (!checkPassword(pwd1)) {
                 alert("Password must contain at least one digit, one lower case, one upper case, and between 8 and 30 characters!");
                 document.getElementById("password").focus();
                 return false;
        	 }
         }
       }
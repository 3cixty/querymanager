<%@page import="java.util.Collection"%>
<%@page import="java.util.Iterator"%>
<%@page import="eu.threecixty.keys.AppKey"%>
<%@page import="eu.threecixty.keys.KeyManager"%>
<%@page import="eu.threecixty.querymanager.rest.Constants" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>App Key Management</title>
<style type="text/css">
input:valid {
	background: white;
}
input:invalid {
	background: red;
}

.selectedkey {
    border-color: red;
}
.normalkey {
    border-color: black;
}
</style>
</head>
<body>
<%
    Boolean permission = (Boolean) session.getAttribute("permission");
    if (permission == null || permission.booleanValue() == false) {
    	response.sendRedirect("../error.jsp");
    } else {
    %>
        <form action="../<%=Constants.PREFIX_NAME %>/key/addappkey" method="post">
            <div>First Name</div>
            <div><input type="text" name="firstName" id="firstName" required></div>
            
            <div>Last Name</div>
            <div><input type="text" name="lastName" id="lastName" required></div>
            
            <div>Email</div>
            <div><input type="text" name="email" id="email" required></div>
            
            <div>Application Type</div>
<div>
    <select name="domain" id="domain">
        <option>Crowdsourcing</option>
        <option>Exploration</option>
        <option>Mobile Application (TI)</option>
        <option>App Challenge</option>
        <option>Others</option>
    </select>
</div>

    <script type="text/javascript">
    
        var global_key_index = 0;
        
        function createElement(tagName, id, value, style, onclick, readonly, innerhtml, type, name) {
        	var el = document.createElement(tagName);
        	if (id != null) {
        	    el.setAttribute("id", id);
        	}
        	if (value != null) {
        	    el.setAttribute("value", value);
        	}
        	if (style != null) {
        	    el.setAttribute("style", style);
        	}
        	if (onclick != null) {
        	    el.setAttribute("onclick", onclick);
        	}
        	if (readonly != null) {
        	    el.setAttribute("readonly", readonly);
        	}
        	if (innerhtml != null) {
        	    el.innerHTML = innerhtml;
        	}
        	if (type != null) {
        		el.setAttribute("type", type);
        	}
        	if (name != null) {
        		el.setAttribute("name", name);
        	}
        	return el;
        }

        function append(parent, child) {
		    var textnode = document.createTextNode( " " );
		    parent.appendChild(textnode);
		    parent.appendChild(child);
        }

        function generateAppKey() {
        	var yourSelect = document.getElementById('domain')
        	var dataForm = "firstName=" + document.getElementById("firstName").value
        	+ "&lastName=" + document.getElementById("lastName").value
        	+ "&email=" + document.getElementById("email").value
        	+ "&domain=" + yourSelect.options[yourSelect.selectedIndex].value;
        	$.ajax({
        		  url: "../<%=Constants.PREFIX_NAME %>/key/addappkey",
        		  type: "POST",
        		  cache: false,
        		  data: dataForm,
        		  success: function(response){
        		    global_key_index++;
        		    var index = global_key_index;
        		    var form = document.getElementById("revokeform");
        		    var div = createElement('div', 'div_' + index, null, null, null, null, null);
        		    form.appendChild(div);
        		    
        		    var label1 = createElement('label', null, null, "width: 50px;display: inline-block;", null, null, index + ".", null, null);
        		    append(div, label1);
        		    
        		    var label2 = createElement('label', null, null, "width: 250px;display: inline-block;", null, null, document.getElementById("firstName").value + " " + document.getElementById("lastName").value, null, null);
        		    append(div, label2);
        		    
        		    var label3 = createElement('label', "uid_" + index, null, "font-style: italic; width: 250px;display: inline-block;", null, null, document.getElementById("email").value, null, null);
        		    append(div, label3);
        		    
        		    var input1 = createElement('input', "key_" + index, response, "font-family: new courier; font-size: 11px;", null, "readonly", null, "text", "key");
        		    append(div, input1);

        		    var input2 = createElement('input', null, "Select", null, "clickCopy(" + index + ");", null, null, "button", null);
        		    append(div, input2);

        		    var input3 = createElement('input', null, "Revoke", null, "clickRevoke(" + index + ");", null, null, "button", null);
        		    append(div, input3);
        		  },
        	      error: function (xhr, ajaxOptions, thrownError) {
        	    	  alert(xhr.responseText);
        	        }
        		});
        }
    </script>


            <div style="height: 10px;"></div>
            <div><input type="button" value="Generate AppKey" onclick="generateAppKey();"></div>
        </form>
        
        <div style="height: 10px;"></div>
        
        <script type="text/javascript">
            var parentScriptPath = "../";
        </script>
        
    <script type="text/javascript" src="../javascripts/vendor/jquery.min.js"></script>
    <script type="text/javascript" src="../javascripts/v2.x/boot.js"></script>
        
         
        <script type="text/javascript">
            function clickCopy(index) {
            	var els = document.getElementsByName("key");
            	for (var i = 1; i <= els.length; i++) {
            		//document.getElementById("key_" + i).className  = '';
            		els[i - 1].className = '';
            	}
            	document.getElementById("key_" + index).className = "selectedkey";
              	var clipButton = document.getElementById("d_clip_button");
            	clipButton.setAttribute("data-clipboard-target", "key_" + index);
            }

            function clickRevoke(index) {
            	var dataForm = "uid=" + document.getElementById("uid_" + index).innerHTML;
            	$.ajax({
            		  url: "../<%=Constants.PREFIX_NAME %>/key/revokeappkey",
            		  type: "POST",
            		  cache: false,
            		  data: dataForm,
            		  success: function(response){
            		    var el = document.getElementById("div_" + index);
            		    el.parentNode.removeChild(el);
            		  },
            	      error: function (xhr, ajaxOptions, thrownError) {
            	    	  alert(xhr.responseText);
            	        }
            		});
            	
            }
        </script>
        
        <div>List AppKeys <span style="width: 10px;" ></span><input type="button" data-clipboard-target="key_1" value="Copy Selected Key" id="d_clip_button"  ></div>
        <div style="height: 10px;"></div>
        <form action="../<%=Constants.PREFIX_NAME %>/key/revokeappkey" method="post" id="revokeform">
        <%
            Collection <AppKey> collections = KeyManager.getInstance().getAppKeys();
            Iterator <AppKey> appKeys = collections.iterator();
        
            int index = 0;
            int len = collections.size();
            for ( ; appKeys.hasNext(); ) {
            	AppKey appKey = appKeys.next();
            	index++;
            	%>
            	    
            	    <div id = "div_<%=index%>">
            	        <label style="width: 50px;display: inline-block;"><%=index %>.</label>
            	        <label style="width: 250px;display: inline-block;"><%=appKey.getOwner().getFirstName() + " " + appKey.getOwner().getLastName() %></label>
            	        <label id="uid_<%=index %>" style="font-style: italic;width: 250px;display: inline-block;"><%=appKey.getOwner().getEmail() %></label>
            	        <input type="text" name="key" id="key_<%=index %>"  style="font-family: new courier; font-size: 11px;" readonly="readonly" value="<%=appKey.getValue() %>" />
            	        <input type="button" name="copy_<%=index %>" id="d_clip_button<%=index %>" value="Select"  data-clipboard-target="key_<%=index %>"  onclick="clickCopy(<%=index %>);" >
            	        <input type="button" value="Revoke" onclick="clickRevoke(<%=index%>);">
            	    </div>
            	    
            	<%
            }
            if (len > 0) {
            	%>
            	<script type="text/javascript">
            	    global_key_index = <%=len%>;
            	    clickCopy(1);
            	</script>
            	<%
            }
        %>

        </form>
    <%
    }
%>
</body>
</html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <meta name="description" content="" />
  <meta name="author" content="" />
<meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate" />
<meta http-equiv="Pragma" content="no-cache" />
<meta http-equiv="Expires" content="0" />
  <title>Consent</title>
  <!-- Le styles -->
  <link rel="stylesheet" href="${pageContext.request.contextPath}/client/css/bootstrap.min.css" />
  <link rel="stylesheet" href="${pageContext.request.contextPath}/client/css/style.css" />

  <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
  <!--[if lt IE 9]>
  <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
  <![endif]-->
  
  <style type="text/css">
     .imageSize {
        width: 150px;
        height: 150px;
     }
  </style>
  
</head>
<body>

<!-- 
<div class="head">
  <img src="${pageContext.request.contextPath}/client/img/surf-oauth.png"/>
</div>
 -->
<div class="main">
  <div class="full">


    <div class="page-header">
      <h1><strong>${client.name}</strong> wants to retrieve data
        from <strong>${client.resourceServer.name}</strong></h1>
    </div>

    <div class="consent">
      <div style="background-color: #283339; width: 90px;">
      <img alt="${client.name}" title="${client.name}" src="${client.thumbNailUrl}"  />
      </div>
      <img src="${pageContext.request.contextPath}/client/img/arrow.png" />
      <img alt="${client.resourceServer.name}"
        title="${client.resourceServer.name}"
        src="${client.resourceServer.thumbNailUrl}" class="imageSize"  />
    </div>

    <form id="accept" method="post" action="${pageContext.request.contextPath}${actionUri}">
      <input type="hidden" name="AUTH_STATE" value="${AUTH_STATE}"/>

      <h2>This data will be shared</h2>
      <c:set var="profileScope" value="Profile" />
      <fieldset>
        <c:forEach items="${client.scopes}" var="availableScope">
          <c:set var="checked" value="" />
          <c:forEach var="requestedScope" items="${requestedScopes}">
            <c:if test="${requestedScope eq availableScope}">
              <c:set var="checked" value="CHECKED" />
            </c:if>
          </c:forEach>
          <input type="checkbox" id="GRANTED_SCOPES" name="GRANTED_SCOPES" <c:out value="${checked}"/>
                 value="${availableScope}"/>
          <span class="consent-label">${availableScope}
            <c:if test="${availableScope eq profileScope}">
             (<strong>Note that checking this box will give this app the ability to read and modify your first name, last name, and last known location in the 3cixty Platform.</strong>)
            </c:if>
          </span><br/>
        </c:forEach>
        <div><strong>If you do not select any scopes, no private information can be read or modified by the requesting application.</strong></div>
      </fieldset>
      <fieldset>
        <div class="form-actions">
        
          <button id="user_oauth_approval" name="user_oauth_approval" value="true" type="submit"
                  class="btn btn-success">Grant permission</button>
        </div>
      </fieldset>
    </form>
  </div>
</div>
<!-- 
<div class="foot">
  <p>Powered by <a href="http://www.surfnet.nl/">SURFnet</a>. Fork me on <a href="https://github.com/OpenConextApps/oa-aas/">Github</a>. Licensed under the <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache License 2.0</a>.</p>
</div>
 -->
</body>
</html>
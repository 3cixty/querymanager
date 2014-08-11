appkey: current version `v1`
======

# This document describes how to get an AppKey:

## APIs changed for version v1:

### Getting an App Key by using Google access token

- Go to `http://3cixty.com:8080/qm/requestKey?accessToken={YourGoogleAccessToken}` 
- Then fill in the form and request an AppKey

## APIs

### Getting an App Key by using Google access token 
 [This end point will soon be disabled as we evolve our APIs](https://github.com/3cixty/appkey)
- Go to `http://3cixty.com:8080/querymanagerServlet-1.0/services/key/requestKey?accessToken={YourGoogleAccessToken}` 
- Then fill in the form and request an AppKey

### Getting an App Key by sending a message to `congkinh.nguyen@inria.fr` with your following information:
- `First Name`
- `Last Name`
- `Email`
- `Application type` (*Crowdsourcing*, *Exploration App*, *Mobile Application - Telecom Italia*, *App Challenge*, *Others*)

## If you have right to manage App Keys, go to:
`http://3cixty.com:8080/querymanagerServlet-1.0/keys/appkeyadmin_login.jsp` to login so that you can create and revoke an App Key.

If you have any problems with getting an AppKey, send a message to Cong-Kinh Nguyen (congkinh.nguyen@inria.fr).



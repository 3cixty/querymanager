This document shows you how to deploy querymanagerServlet and how to make a remote query to QueryManager.

### Requirements:

- Install ```Mysql``` database, then create the database named `3cixty` and a user called `3cixty`.
  Please change the password defined in the querymanagerServlet/WebContent/WEB-INF/password.property to
  the `3cixty` user's password.

- Set all permissions for the database `3cixty` to the user `3cixty`

### How to deploy querymanagerServlet:

- Checkout the appkey repository ```https://github.com/3cixty/appkey.git``` and compile the module

- Checkout the storageProfile repository ```https://github.com/3cixty/profileStorage.git``` and compile the module

- Go to the ```querymanager``` repository and execute the following command:
  ```
  $ mvn clean install
  ```

- Go to the ```querymanagerServlet``` folder, copy the .war file in the ```target``` folder to your Web application server (Tomcat for example).

### How to invoke 3cixty's services
 
#### How to get an App key

- Go to ```http://3cixty.com:8080/qm/v1/requestKey?accessToken={YourAccessToken}``` where `{YourAccessToken}` is to be provided.

#### How to validate an App key

- The template for validating an App key:
 `http://3cixty.com:8080/qm/v1/validateKey?key={AnAppKey}` where `AnAppKey` is an App key.
 
  The HTTP code status of invoking Key validation is `200` if the key is valid, `400` otherwise.
  
#### How to invoke Query augmentation

- The template for calling query augmentation
  ```
  http://3cixty.com:8080/qm/v1/augmentAndExecute?accessToken={accessToken}&format={format}&query={query}&filter={filter}&key={key}
  ```

  Where:
  
  |parameter|value|
  |:---------|:-----|
  |{accessToken}|is an access token which lasts for one hour or false. When accessToken equaling to false, the query isn't augmented. When the accessToken is invalid (incorrect or expired), the servlet returns the code 400 for HTTP request with the message description **Access token is incorrect or expired** |
  |{format}|requested result format (rdf or json)|
  |{query}|a sparql query|
  |{filter}|**location**, **enteredRating**, **preferred** or **friends**. QueryManager will take this value to augment a query|
  |{key}|is an application key|
  
  
  Note that in the case you don't want to augment the query, you just don't provide the `filter` parameter information.
  
- Example for a full URL to invoke the service on local Tomcat server:

` http://3cixty.com:8080/qm/v1/augmentAndExecute?accessToken=ya29.1.AADtN_VLpeIK2WSwQp69sfyiGCyhbfsfgT2j_8aEFAx3JEN66f3MK-8FhP7cVd-XkHxENjA&format=json&query=SELECT%20%3Fcategory%20(COUNT(*)%20AS%20%3Fcount)%09%09%09WHERE%20%7B%09%09%09%09%3Fevent%20a%20lode%3AEvent%3B%09%09%09%09lode%3AhasCategory%20%3Fcategory%20.%7D%09%09%09GROUP%20BY%20%3Fcategory%20ORDER%20BY%20DESC%20(%3Fcount)%20LIMIT%2020&filter=location&key=MTAzOTE4MTMwOTc4MjI2ODMyNjkwMTQwNDIwMzM4NDgxMgF6Z3VpAG5qY2Itc2sD
`
 
  Where:
  - `ya29.1.AADtN_VLpeIK2WSwQp69sfyiGCyhbfsfgT2j_8aEFAx3JEN66f3MK-8FhP7cVd-XkHxENjA` is a `{accessToken}`
  - `json` is a `{format}`
  - `SELECT%20%3Fcategory%20(COUNT(*)%20AS%20%3Fcount)%09%09%09WHERE%20%7B%09%09%09%09%3Fevent%20a%20lode%3AEvent%3B%09%09%09%09lode%3AhasCategory%20%3Fcategory%20.%7D%09%09%09GROUP%20BY%20%3Fcategory%20ORDER%20BY%20DESC%20(%3Fcount)%20LIMIT%2020` is a `{query}`.
  - `location` is a `{filter}`
  - `MTAzOTE4MTMwOTc4MjI2ODMyNjkwMTQwNDIwMzM4NDgxMgF6Z3VpAG5qY2Itc2sD` is a dummy `{key}`
  
  The query is URLEncoded
 
  For the output of the query: if the format is 'json', the output also includes an array of augmented queries used to get that output. The array is in the JSON string format.

  Here is an example of the output:
  
  ```
  { "head": { "link": [], "vars": ["category", "count"] }, ..., "AugmentedQueries": [{"AugmentedQuery":"SELECT DISTINCT  ?category (count(*) AS ?count)\nWHERE\n  { ?event rdf:type lode:Event .\n    ?event lode:hasCategory ?category . \n    ?event    lode:atPlace        ?_augplace .\n    ?_augplace  vcard:adr         ?_augaddress .\n    ?_augaddress  vcard:country-name  ?_augcountryname .\n    FILTER ( ?_augcountryname = \"Italy\" )\n  }\nGROUP BY ?category\nORDER BY DESC(?count)\nLIMIT   20\n"}]}
  ```
 
#### How to invoke tray services


  
  The parameters and actions to call the tray servlet follow the documentation on Google Drive at ```https://docs.google.com/document/d/1jb9d1Kh63twbcWJry62rTHuqQaBIxq9LP9WTtcsXShg/edit?usp=drive_web```

  
#### How to update settings page from web form

-  Web link to go to:
  ```
  http://3cixty.com:8080/qm/v1/viewSettings?accessToken={accessToken}&key={key}
  ```
  Where:
  
  |parameter|value|
  |:---------|:-----|
  |{accessToken}|is an access token which lasts for one hour or false. When accessToken equaling to false, the query isn't augmented. When the accessToken is invalid (incorrect or expired), the servlet returns the code 400 for HTTP request with the message description **Access token is incorrect or expired** |
  |{key}|is an application key|

#### How to update settings page without using web form
- The template for updating profile information (take URL to show, but use `HTTP POST` in reality)
  ```
  http://3cixty.com:8080/qm/v1/saveSettings?accessToken={accessToken}&key={key}&townName={townName}&countryName={countryName}&lat={latitude}&lon={longitude}&pi_source[0]={pi_source[0]}&pi_id[0]={pi_id[0]}&pi_at[0]={pi_at[0]}&pi_source[1]={pi_source[1]}&pi_id[1]={pi_id[1]}&pi_at[1]={pi_at[1]}&...
  ```

  Where:
  
  |parameter|required|value|
  |:---------|:-----|:-----|
  |{accessToken}| yes| is an access token which lasts for one hour. When accessToken equaling to false, the query isn't augmented. When the accessToken is invalid (incorrect or expired), the servlet returns the code 400 for HTTP request with the message description **Access token is incorrect or expired** |
  |{key}| yes|is an application key|
  |{townName}| no| is a town name, for example **Milano**, **Paris**, etc.|
  |{countryName}| no| is a country name, for example **Italy**, **France**, etc.|
  |{lat}| no| is latitude value|
  |{lon}| no|is longitude|
  |{pi_source}| no| is profile information source, for example **Mobidot**, **Google**, **Facebook**|
  |{pi_id}| no| is profile information UID, for example UID from **Mobidot**, **Facebook**|
  |{pi_at}| no| is access token to access to the source described by {pi_source}|
  
  Note that group parameters `(pi_source, pi_id, pi_at)` go altogether. They can be an array of groups 

- Example for the template to update profile information:
  ```
  http://3cixty.com:8080/qm/v1/saveSettings?accessToken=ya29.1.AADtN_VLpeIK2WSwQp69sfyiGCyhbfsfgT2j_8aEFAx3JEN66f3MK-8FhP7cVd-XkHxENjA&key=MTAzOTE4MTMwOTc4MjI2ODMyNjkwMTQwNDIwMzM4NDgxMgF6Z3VpAG5qY2Itc2sD&townName=Milano&countryName=Italy&lat=2.12345&lon=46.1234&startDate=18-06-2014&endDate=21-06-2013&pi_source[0]=Facebook&pi_id[0]=112233445566&pi_at[0]=facebookFakeAccessToken&pi_source[1]=Mobidot&pi_id[1]=nguyen&pi_at[1]=fakeMobidotAccessToken
  ```

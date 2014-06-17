This document shows you how to deploy querymanagerServlet and how to make a remote query to QueryManager.

### How to deploy querymanagerServlet:

- Checkout the profiler repository <code>https://github.com/3cixty/profiler.git</code> and compile the module

- Go to the "querymanager" repository and execute the following command:
  ```
  $ mvn clean install
  ```

- Go to the "querymanagerServlet" folder, copy the .war file in the "target" folder to your Web application server (Tomcat for example).
  

### How to make a remote query to QueryManager:

- Suppose you deployed querymanagerServlet on your local server. Let the baseUrl point to the root path for querymanagerServlet you just deployed

- Invoke the services by the following template:

  ```
  ~baseUrl/queryManagerServlet?accessToken={accessToken}&isUsingPreferences={isUsingPreferences}&format={format}&query={query}&filter={filter}&friends={friends}
  ~baseUrl/trayServlet
  ~baseUrl/settingsServlet
  ```

###  How to invoke the services will be discussed by the following sections.
  
####  Query augmentation

- The template for calling query augmentation
  ```
  ~baseUrl/queryManagerServlet?accessToken={accessToken}&isUsingPreferences={isUsingPreferences}&format={format}&query={query}&filter={filter}&friends={friends}
  ```

  Where:
  
  |parameter|value|
  |:---------|:-----|
  |{accessToken}|is an access token which lasts for one hour or false. When accessToken equaling to false, the query isn't augmented. When the accessToken is invalid (incorrect or expired), the servlet returns the code 400 for HTTP request with the message description **Access token is incorrect or expired** |
  |{isUsingPreferences}|**true** or **false**. This is used to whether or not augment the query with the user preferences|
  |{format}|requested result format (rdf or json)|
  |{query}|a sparql query|
  |{filter}|**location**, **enteredRating**, **preferred** or **friends**. QueryManager will take this value to augment a query|
  
- Example for a full URL to invoke the service on local Tomcat server:
  [http://localhost:8080/querymanagerServlet-1.0/queryManagerServlet?accessToken=ya29.1.AADtN_VLpeIK2WSwQp69sfyiGCyhbfsfgT2j_8aEFAx3JEN66f3MK-8FhP7cVd-XkHxENjA&isUsingPreferences=false&format=json&query=SELECT%20%3Fcategory%20(COUNT(*)%20AS%20%3Fcount)%09%09%09WHERE%20%7B%09%09%09%09%3Fevent%20a%20lode%3AEvent%3B%09%09%09%09lode%3AhasCategory%20%3Fcategory%20.%7D%09%09%09GROUP%20BY%20%3Fcategory%20ORDER%20BY%20DESC%20(%3Fcount)%20LIMIT%2020&filter=location](http://localhost:8080/querymanagerServlet-1.0/queryManagerServlet?userKey=kinh&isUsingPreferences=false&format=json&query=SELECT%20%3Fcategory%20(COUNT(*)%20AS%20%3Fcount)%09%09%09WHERE%20%7B%09%09%09%09%3Fevent%20a%20lode%3AEvent%3B%09%09%09%09lode%3AhasCategory%20%3Fcategory%20.%7D%09%09%09GROUP%20BY%20%3Fcategory%20ORDER%20BY%20DESC%20(%3Fcount)%20LIMIT%2020&filter=location)
 
  Where:
  - [http://localhost:8080/querymanagerServlet-1.0](http://localhost:8080/querymanagerServlet-1.0) is the baseUrl
  - `ya29.1.AADtN_VLpeIK2WSwQp69sfyiGCyhbfsfgT2j_8aEFAx3JEN66f3MK-8FhP7cVd-XkHxENjA` is a `{accessToken}`
  - `false` is an `{isUsingPreferences}`
  - `json` is a `{format}`
  - `SELECT%20%3Fcategory%20(COUNT(*)%20AS%20%3Fcount)%09%09%09WHERE%20%7B%09%09%09%09%3Fevent%20a%20lode%3AEvent%3B%09%09%09%09lode%3AhasCategory%20%3Fcategory%20.%7D%09%09%09GROUP%20BY%20%3Fcategory%20ORDER%20BY%20DESC%20(%3Fcount)%20LIMIT%2020` is a `{query}`.
  - `location` is a `{filter}`
  
  The query is URLEncoded
 
  For the output of the query: if the format is 'json', the output also includes an array of augmented queries used to get that output. The array is in the JSON string format.

  Here is an example of the output:
  
  ```
  { "head": { "link": [], "vars": ["category", "count"] }, ..., "AugmentedQueries": [{"AugmentedQuery":"SELECT DISTINCT  ?category (count(*) AS ?count)\nWHERE\n  { ?event rdf:type lode:Event .\n    ?event lode:hasCategory ?category . \n    ?event    lode:atPlace        ?_augplace .\n    ?_augplace  vcard:adr         ?_augaddress .\n    ?_augaddress  vcard:country-name  ?_augcountryname .\n    FILTER ( ?_augcountryname = \"Italy\" )\n  }\nGROUP BY ?category\nORDER BY DESC(?count)\nLIMIT   20\n"}]}
  ```
 
#### Tray services

- The servlet to deal with Tray Items is called through HTTP POST at
  ```
  ~baseUrl/trayServlet
  ```
  
  The parameters and actions to call the tray servlet follow the documentation on Google Drive at [https://docs.google.com/document/d/1jb9d1Kh63twbcWJry62rTHuqQaBIxq9LP9WTtcsXShg/edit?usp=drive_web](https://docs.google.com/document/d/1jb9d1Kh63twbcWJry62rTHuqQaBIxq9LP9WTtcsXShg/edit?usp=drive_web)

  For example, if `querymanagerServlet.war` was deployed on local machine (Tomcat, port 8080), Tray services can be called at
  ```
  http://localhost:8080/querymanagerServlet-1.0/trayServlet
  ```
  
####  Updating profile information

  (Web Interface will soon be available)

- The template for updating profile information (take URL to show, but use `HTTP POST` in reality)
  ```
  ~baseUrl/settingsServlet?accessToken={accessToken}&townName={townName}&countryName={countryName}&lat={latitude}&lon={longitude}&startDate={startDate}&endDate={endDate}&pi_source[0]={pi_source[0]}&pi_id[0]={pi_id[0]}&pi_at[0]={pi_at[0]}&pi_source[1]={pi_source[1]}&pi_id[1]={pi_id[1]}&pi_at[1]={pi_at[1]}&...
  ```

  Where:
  
  |parameter|required|value|
  |:---------|:-----|:-----|
  |{accessToken}| yes| is an access token which lasts for one hour. When accessToken equaling to false, the query isn't augmented. When the accessToken is invalid (incorrect or expired), the servlet returns the code 400 for HTTP request with the message description **Access token is incorrect or expired** |
  |{townName}| no| is a town name, for example **Milano**, **Paris**, etc.|
  |{countryName}| no| is a country name, for example **Italy**, **France**, etc.|
  |{lat}| no| is latitude value|
  |{lon}| no|is longitude|
  |{startDate}| no| is a start date for an event which a user prefers to participate in. The start date format follows the pattern **dd-mm-yyyy**, for example **25-07-2015**|
  |{endDate}| no| is an end date for an event which a user prefers to participate in. The end date format is the same with the start date format|
  |{pi_source}| no| is profile information source, for example **Mobidot**, **Google**, **Facebook**|
  |{pi_id}| no| is profile information UID, for example UID from **Mobidot**, **Facebook**|
  |{pi_at}| no| is access token to access to the source described by {pi_source}|
  
  Note that group parameters `(pi_source, pi_id, pi_at)` go altogether. They can be an array of groups 

- Example for the template to update profile information:
  ```
  ~baseUrl/settingsServlet?accessToken=ya29.1.AADtN_VLpeIK2WSwQp69sfyiGCyhbfsfgT2j_8aEFAx3JEN66f3MK-8FhP7cVd-XkHxENjA&townName=Milano&countryName=Italy&lat=2.12345&lon=46.1234&startDate=18-06-2014&endDate=21-06-2013&pi_source[0]=Facebook&pi_id[0]=112233445566&pi_at[0]=facebookFakeAccessToken&pi_source[1]=Mobidot&pi_id[1]=nguyen&pi_at[1]=fakeMobidotAccessToken
  ```

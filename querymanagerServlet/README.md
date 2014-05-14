This document shows you how to deploy querymanager-webservice and how to make a remote query to QueryManager.

### How to deploy querymanager-webservice:

- Go to the "querymanager" repository and execute the following command:
  ```
  $ mvn clean install
  ```

- Go to the "querymanager-webservice" folder, copy the .war file in the "target" folder to your Web application server (Tomcat for example).
  

### How to make a remote query to QueryManager:

- Suppose you deployed querymanager-webservice on your local server. Let the baseUrl point to the root path for querymanager-webservice you just deployed

- Invoke the service by the following template:

  ```
  ~baseUrl/queryManagerServlet?accessToken={accessToken}&isUsingPreferences={isUsingPreferences}&format={format}&query={query}&filter={filter}&friends={friends}
  ```
  
  where:
  
  |parameter|value|
  |:---------|:-----|
  |{accessToken}|is an access token which lasts for one hour or false. When accessToken equaling to false, the query isn't augmented. When the accessToken is invalid (incorrect or expired), the servlet returns the code 400 for HTTP request with the message description <b>Access token is incorrect or expired<b> | 
  |{isUsingPreferences}|<b>true</b> or <b>false</b>. This is used to whether or not augment the query with the user preferences|
  |{format}|requested result format (rdf or json). When the format is <b>json</b>, the augmented query can be got through the <b>AugmentedQuery</b> key in the string return|
  |{query}|a sparql query|
  |{filter}|<b>location</b> (country and town name), <b>enteredRating</b> (the current user rated or visited) or <b>gpslocation</b> (GPS coordinates). QueryManager will take this value to augment a query|
  |{friends}|<b>true</b> or <b>false</b>. This value is used to augment a query based on either <b>my friends</b> or <b>I</b>|
  
-  Example for a full URL to invoke the service on local Tomcat server:
  
  [http://localhost:8080/querymanagerServlet-1.0/queryManagerServlet?accessToken=ya29.1.AADtN_VLpeIK2WSwQp69sfyiGCyhbfsfgT2j_8aEFAx3JEN66f3MK-8FhP7cVd-XkHxENjA&isUsingPreferences=false&format=json&query=SELECT%20%3Fcategory%20(COUNT(*)%20AS%20%3Fcount)%09%09%09WHERE%20%7B%09%09%09%09%3Fevent%20a%20lode%3AEvent%3B%09%09%09%09lode%3AhasCategory%20%3Fcategory%20.%7D%09%09%09GROUP%20BY%20%3Fcategory%20ORDER%20BY%20DESC%20(%3Fcount)%20LIMIT%2020&filter=location&friends=true](http://localhost:8080/querymanagerServlet-1.0/queryManagerServlet?userKey=kinh&isUsingPreferences=false&format=json&query=SELECT%20%3Fcategory%20(COUNT(*)%20AS%20%3Fcount)%09%09%09WHERE%20%7B%09%09%09%09%3Fevent%20a%20lode%3AEvent%3B%09%09%09%09lode%3AhasCategory%20%3Fcategory%20.%7D%09%09%09GROUP%20BY%20%3Fcategory%20ORDER%20BY%20DESC%20(%3Fcount)%20LIMIT%2020&filter=location&friends=true)
  
  Here:
  - [http://localhost:8080/querymanagerServlet-1.0](http://localhost:8080/querymanagerServlet-1.0) is the baseUrl
  - ya29.1.AADtN_VLpeIK2WSwQp69sfyiGCyhbfsfgT2j_8aEFAx3JEN66f3MK-8FhP7cVd-XkHxENjA is a {accessToken}
  - false is an {isUsingPreferences}
  - json is a {format}
  - SELECT%20%3Fcategory%20(COUNT(*)%20AS%20%3Fcount)%09%09%09WHERE%20%7B%09%09%09%09%3Fevent%20a%20lode%3AEvent%3B%09%09%09%09lode%3AhasCategory%20%3Fcategory%20.%7D%09%09%09GROUP%20BY%20%3Fcategory%20ORDER%20BY%20DESC%20(%3Fcount)%20LIMIT%2020 is a {query}.
  - location is a {filter} 
  - true is a {friends}
  
  For the query, the response in json is as the following:
  <code>
  { "head": { "link": [], "vars": ["category", "count"] }, ..., "AugmentedQuery": "SELECT  ?category (count(*) AS ?count)\nWHERE\n  { ?event rdf:type lode:Event .\n    ?event lode:hasCategory ?category . \n    ?event    lode:atPlace        ?_augplace .\n    ?_augplace  vcard:adr         ?_augaddress .\n    ?_augaddress  vcard:locality  ?_augcityname ;\n              vcard:country-name  ?_augcountryname .\n    FILTER ( ( ?_augcityname = \"Milano\" ) || ( ?_augcountryname = \"Italy\" ) )\n  }\nGROUP BY ?category\nORDER BY DESC(?count)\nLIMIT   20\n"}
  </code>
  
  The query is UTF-8 encoded

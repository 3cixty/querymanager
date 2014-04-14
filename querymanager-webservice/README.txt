This document shows you how to deploy querymanager-webservice and how to make a
remote query to QueryManager.

1. How to deploy querymanager-webservice:

- Go to the "querymanager" repository and execute the following command:
  $ mvn clean install

- Go to the "querymanager-webservice" folder, copy the .war file in the "target"
  folder to your Web application server (Tomcat for example).
  
  
2. How to make a remote query to QueryManager:

- Suppose that you deployed querymanager-webservice on your local server

- Suppose that baseUrl is to point to the root path for querymanager-webservice you just deployed

- Invoke the service by the following template:

  ~baseUrl/services/executeQuery/{userkey}/{isUsingPreferences}/{format}/{query}
  
  where:  	{userkey}: 				is user session which is to be decided
  
  			{isUsingPreferences}: 	true or false
  			
  			{format}:				rdf or json
  			
  			{query}: 				a sparql query
  			
  
  Example for a full URL to invoke the service on local  Tomcat server:
  
  http://localhost:8080/querymanager-webservice-1.0/services/executeQuery/kinh/false/json/select%20*%20where%20%7B%20%3Fs%20%3Fp%20%3Fo%20%7D
  
  http://localhost:8080/querymanager-webservice-1.0 is baseUrl
  kinh is a {userkey}
  false is an {isUsingPreferences}
  json is a {format}
  select%20*%20where%20%7B%20%3Fs%20%3Fp%20%3Fo%20%7D is a {query}, but the query was encoded by URL Encoding http://www.w3schools.com/tags/ref_urlencode.asp
  
  
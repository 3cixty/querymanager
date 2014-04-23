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
  ~baseUrl/queryManagerServlet?userKey={userkey}&isUsingPreferences={isUsingPreferences}/format={format}/query={query}
  ```
  
  where:
  
  |parameter|value|
  |:---------|:-----|
  |{userkey}|is user session which is to be decided|
  |{isUsingPreferences}|true or false. This is used to whether or not augment the query with the user preferences|
  |{format}|requested result format (rdf or json)|
  |{query}|a sparql query|
  
-  Example for a full URL to invoke the service on local Tomcat server:
  
  [http://localhost:8080/querymanager-webservice-1.0/queryManagerServlet?userKey=kinh&isUsingPreferences=false&format=json&query=select%20*%20where%20%7B%20%3Fs%20%3Fp%20%3Fo%20%7D](http://localhost:8080/querymanager-webservice-1.0/queryManagerServlet?userKey=kinh&isUsingPreferences=false&format=json&query=select%20*%20where%20%7B%20%3Fs%20%3Fp%20%3Fo%20%7D)
  
  Here:
  - [http://localhost:8080/querymanager-webservice-1.0](http://localhost:8080/querymanager-webservice-1.0) is the baseUrl
  - kinh is a {userkey}
  - false is an {isUsingPreferences}
  - json is a {format}
  - select%20*%20where%20%7B%20%3Fs%20%3Fp%20%3Fo%20%7D is a {query}. 
  
  The query is UTF-8 encoded
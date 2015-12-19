# Tutorial on the Deployement of the QueryManager

1. [MySQL configuration](#mysql)
2. [On Localhost](#localhost)
    1. [QueryManager compilation and installation](#compilation)
	2. [API documentation installation](#api-documentation)
	3. [Testing](#testing)
        1. [Get a 3cixty app key](#get-app-key)
	    2. [Test the 3cixty app key](#test-app-key) 
3. [On Prod Server](#prod-server)
4. [On Dev Server](#dev-server)

##MySQL configuration <a name="mysql"></a>

The following steps must be done only once in order to be able to compile and use the QueryManager

1. `mysql -u root -p`
2. `CREATE USER '3cixty'@'localhost' IDENTIFIED BY '3cixtydatabase001';`
3. `create database 3cixty;`
4. `GRANT ALL ON 3cixty.* TO '3cixty'@'localhost';`
5. `create database 3cixtyTest;`
6. `GRANT ALL ON 3cixtyTest.* TO '3cixty'@'localhost';`
7. `exit;`

##On Localhost <a name="localhost"></a>

###QueryManager compilation and installation <a name="compilation"></a>

1. `git clone https://github.com/3cixty/querymanager.git`
2. `cd querymanager`
3. `mvn -U clean install`
4. `cp querymanagerServlet/target/v2.war <TOMCAT_HOME>/webapps/`
5. `cp appkey/oauth/apis-authorization-server-war/target/apis-authorization-server-war-1.3.5.war <TOMCAT_HOME>/webapps/`
6. `<TOMCAT_HOME>/bin/shutdown.sh`
7. `<TOMCAT_HOME>/bin/startup.sh`

Now go to ```http://localhost:8080/v2/getScopes``` and the following result should be displayed:
```
[
	{ },
	{ }
]
```

###API documentation installation <a name="api-documentation"></a>

1. `git clone https://github.com/3cixty/apidocs.git`
2. `cd apidocs`
3. `sed -i 's/api.3cixty.com/localhost:8080/g' api.json`
4. `sed -i 's#https://localhost:8080#http://localhost:8080#g' api.json`
5. `sed -i 's/"https"/"http"/g' api.json`
6. `sed -i 's/<host>/localhost:8080/g' api.json`
7. `sed -i 's#\\\\\\\\#//#g' api.json`
8. `sed -i 's#\\\\#/#g' api.json`
9. `sed -i 's#https://<host>#http://localhost:8080/apidocs#g' index.html`
10. `sed -i 's#https://<host>#http://localhost:8080/apidocs#g' swagger-ui.js`
11. `cd ..`
12. `cp -R apidocs <TOMCAT_HOME>/webapps/`
13. `<TOMCAT_HOME>/bin/shutdown.sh`
14. `<TOMCAT_HOME>/bin/startup.sh`

Now go to ```http://localhost:8080/apidocs``` and the documentation page should be displayed

###Testing <a name="testing"></a>

####Get a 3cixty app key <a name="get-app-key"></a>

1. Go to http://localhost:8080/v2/googleToken.jsp
2. Click on the button "Login to Google to get access token"
3. Sign-in using a Google account
4. A token appears on the screen, it is the Google token, copy it
5. Go to ```http://localhost:8080/v2/getAppKey?google_access_token=<GOOGLE_TOKEN>&appid=MyID&appname=AppTest&category=Exploration&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fweb-client-sample%2Fwelcome.html```

The following result should be displayed:
```
{
  "key": "<APP_KEY>"
}
```

####Test the 3cixty app key <a name="test-app-key"></a>

1. `git clone https://github.com/3cixty/web-client-sample.git`
2. `cd web-client-sample`
3. `sed -i 's#https://api.3cixty.com#http://localhost:8080#g' login.html`
4. `sed -i 's/"[a-z0-9-]*"/"<APP_KEY>"/g' login.html`
5. `cd ..`
2. `cp -R web-client-sample <TOMCAT_HOME>/webapps/`
3. `<TOMCAT_HOME>/bin/shutdown.sh`
4. `<TOMCAT_HOME>/bin/startup.sh`
5. Go to http://localhost:8080/web-client-sample/login.html
6. Click on the button *Login using 3cixty*
7. Sign in using a Google account
8. Click on the button *Grant Permission*

If **Log in to Pizza4ever!!** is displayed, everything wents well

##On Prod server <a name="prod-server"></a>

Same steps than before but replace ```localhost:8080``` by ```api.3cixty.com``` and ```http``` by ```https```.

##On Dev server <a name="dev-server"></a>

Same steps than before but replace ```localhost:8080``` by ```dev.3cixty.com``` and ```http``` by ```https```.

cp 3cixty-db/resources/api.hibernate.cfg.xml 3cixty-db/resources/hibernate.cfg.xml
cp appkey/oauth/apis-authorization-server-war/src/main/resources/api.apis.application.properties appkey/oauth/apis-authorization-server-war/src/main/resources/apis.application.properties
cp 3cixty-db/resources/api.memcached.conf 3cixty-db/resources/memcached.conf
cp querymanagerServlet/WebContent/WEB-INF/api.3cixty.properties querymanagerServlet/WebContent/WEB-INF/3cixty.properties

mvn clean install -Dmaven.test.skip=true
THREE_CIXTY=v2
TOMCAT_HOME=/opt/apache-tomcat-8.0.22
sudo /etc/init.d/tomcat stop
sudo cp $TOMCAT_HOME/webapps/$THREE_CIXTY.war /backup-wars/$THREE_CIXTY-`date +%Y-%m-%d:%H:%M:%S`.war
sudo rm -rf $TOMCAT_HOME/webapps/$THREE_CIXTY*
sudo rm -rf $TOMCAT_HOME/webapps/apis-authorization-server-war*
sudo cp querymanagerServlet/target/$THREE_CIXTY.war $TOMCAT_HOME/webapps
sudo cp appkey/oauth/apis-authorization-server-war/target/apis-authorization-server-war-1.3.5.war $TOMCAT_HOME/webapps/
sudo /etc/init.d/tomcat start

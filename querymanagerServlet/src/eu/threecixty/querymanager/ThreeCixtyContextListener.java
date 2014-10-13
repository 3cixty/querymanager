package eu.threecixty.querymanager;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import eu.threecixty.db.DBConnection;
import eu.threecixty.keys.KeyManager;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.GoFlowImpl;
import eu.threecixty.profile.MobidotImpl;
import eu.threecixty.profile.RdfFileManager;
import eu.threecixty.profile.TrayStorage;
import eu.threecixty.querymanager.rest.GoFlowServer;
import eu.threecixty.querymanager.rest.QueryManagerServices;

@WebListener
public class ThreeCixtyContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent context) {
		DBConnection.getInstance().closeConnection();
	}

	public void contextInitialized(ServletContextEvent context) {
	    String realPath = context.getServletContext().getRealPath("/");
	    RdfFileManager.getInstance().setPathToRdfFile(realPath + "/WEB-INF/UserProfileKBmodelWithIndividuals.rdf");
	    TrayStorage.setPath(realPath);
	    QueryManagerServices.realPath = realPath;
	    KeyManager.getInstance().setPath(realPath + File.separatorChar + "keyapps" + File.separatorChar);
	    MobidotImpl.setPath(realPath);
	    GoFlowImpl.setPath(realPath);
	    GoFlowServer.setPath(realPath + File.separatorChar + "WEB-INF" + File.separatorChar + "goflow.properties");
	    DBConnection.getInstance().setPath(realPath);
	    OAuthWrappers.addScopesByDefault();
	}

}

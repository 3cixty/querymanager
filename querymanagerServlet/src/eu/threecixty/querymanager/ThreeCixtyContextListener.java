package eu.threecixty.querymanager;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import eu.threecixty.Configuration;
import eu.threecixty.db.DBConnection;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.GoFlowImpl;
import eu.threecixty.profile.MobidotImpl;
import eu.threecixty.profile.RdfFileManager;
import eu.threecixty.profile.TrayStorage;
import eu.threecixty.querymanager.rest.GoFlowServer;
import eu.threecixty.querymanager.rest.QueryManagerServices;
import eu.threecixty.querymanager.rest.CallLogServices;

@WebListener
public class ThreeCixtyContextListener implements ServletContextListener {
	private static final String FOLDER_ROOT = "3cixtyData";

	public void contextDestroyed(ServletContextEvent context) {
		DBConnection.getInstance().closeConnection();
	}

	public void contextInitialized(ServletContextEvent context) {
	    String realPath = context.getServletContext().getRealPath("/");
	    System.setProperty("contextPath", context.getServletContext().getContextPath());
	    String pathTo3CixtyDataFolder =  new File(new File(realPath).getParent()).getParent()
	    		+ File.separatorChar + FOLDER_ROOT;
	    Configuration.setPath(realPath);
	    Configuration.setVersion(context.getServletContext().getContextPath());
	    File rdfFile = new File(pathTo3CixtyDataFolder + File.separatorChar + "UserProfileKBmodelWithIndividuals.rdf");
	    if (!rdfFile.exists()) {
	    	File originalFile = new File(realPath + "/WEB-INF/UserProfileKBmodelWithIndividuals.rdf");
	    	originalFile.renameTo(rdfFile);
	    }
	    RdfFileManager.getInstance().setPathToRdfFile(rdfFile.getAbsolutePath());
	    TrayStorage.setPath(pathTo3CixtyDataFolder);
	    QueryManagerServices.realPath = realPath;
        CallLogServices.realPath = realPath;
	    MobidotImpl.setPath(pathTo3CixtyDataFolder);
	    GoFlowImpl.setPath(pathTo3CixtyDataFolder);
	    GoFlowServer.setPath(realPath + File.separatorChar + "WEB-INF" + File.separatorChar + "goflow.properties");
	    DBConnection.getInstance().setPath(realPath);
	    OAuthWrappers.addScopesByDefault();
	}

}

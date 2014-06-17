package eu.threecixty.querymanager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import eu.threecixty.profile.RdfFileManager;
import eu.threecixty.profile.TrayStorage;
import eu.threecixty.querymanager.rest.QueryManagerServices;

@WebListener
public class ThreeCixtyContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent context) {
		
	}

	public void contextInitialized(ServletContextEvent context) {
	    String realPath = context.getServletContext().getRealPath("/");
	    RdfFileManager.getInstance().setPathToRdfFile(realPath + "/WEB-INF/UserProfileKBmodelWithIndividuals.rdf");
	    TrayStorage.setPath(realPath);
	    QueryManagerServices.realPath = realPath;
	}

}

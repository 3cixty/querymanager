package eu.threecixty.querymanager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import eu.threecixty.profile.RdfFileManager;

@WebListener
public class ThreeCixtyContextListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent context) {
		
	}

	public void contextInitialized(ServletContextEvent context) {
	    String realPath = context.getServletContext().getRealPath("/");
	    RdfFileManager.getInstance().setPathToRdfFile(realPath + "/WEB-INF/UserProfileKBmodelWithIndividuals.rdf");
	}

}

package eu.threecixty.querymanager;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import eu.threecixty.Configuration;
import eu.threecixty.CrawlerCron.CrawlerCron;
import eu.threecixty.cache.CacheManager;
import eu.threecixty.oauth.OAuthBypassedManager;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.PersistenceWorkerManager;
import eu.threecixty.profile.RdfFileManager;
import eu.threecixty.profile.partners.GoFlowServer;
import eu.threecixty.querymanager.rest.AdminServices;
import eu.threecixty.querymanager.rest.QueryManagerServices;
import eu.threecixty.querymanager.rest.CallLogServices;

@WebListener
public class ThreeCixtyContextListener implements ServletContextListener {
	private static final String FOLDER_ROOT = "3cixtyData";

	public void contextDestroyed(ServletContextEvent context) {
		PersistenceWorkerManager.getInstance().stop();
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
	    CacheManager.getInstance().loadQueries(realPath + File.separatorChar + "WEB-INF"
	            + File.separatorChar + "cacheQueries");
	    RdfFileManager.getInstance().setPathToRdfFile(rdfFile.getAbsolutePath());
	    QueryManagerServices.realPath = realPath;
        CallLogServices.realPath = realPath;
        AdminServices.realPath = realPath;
	    GoFlowServer.setPath(realPath + File.separatorChar + "WEB-INF" + File.separatorChar + "goflow.properties");
	    OAuthWrappers.addScopesByDefault();
	    
	    AuthorizationBypassManager.getInstance().load();
	    OAuthBypassedManager.getInstance().addAppKeys(AuthorizationBypassManager.getInstance().getAppkeys());
	    
	    // create timer for crawling Mobidot information
	    CrawlerCron crawlerCron = new CrawlerCron();
	    crawlerCron.run();
	}

}

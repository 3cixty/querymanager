/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.querymanager;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import eu.threecixty.Configuration;
import eu.threecixty.CrawlerCron.CrawlerCron;

import eu.threecixty.cache.ProfileCacheManager;
import eu.threecixty.cache.TokenCacheManager;
import eu.threecixty.cache.TrayCacheManager;

import eu.threecixty.oauth.OAuthBypassedManager;
import eu.threecixty.oauth.OAuthWrappers;
import eu.threecixty.profile.PersistenceWorkerManager;
import eu.threecixty.profile.partners.GoFlowServer;
import eu.threecixty.querymanager.rest.AdminServices;
import eu.threecixty.querymanager.rest.QueryManagerServices;
import eu.threecixty.querymanager.rest.CallLogServices;

/**
 * 
 * This class is listener to initiate variables used by 3cixty classes.
 *
 */
@WebListener
public class ThreeCixtyContextListener implements ServletContextListener {
	private static final String FOLDER_ROOT = "3cixtyData";

	public void contextDestroyed(ServletContextEvent context) {

		TokenCacheManager.getInstance().stop();
		TrayCacheManager.getInstance().stop();
		ProfileCacheManager.getInstance().stop();
		PersistenceWorkerManager.getInstance().stop();
	}

	public void contextInitialized(ServletContextEvent context) {
		// get current system path
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

package eu.threecixty.monitor;

import java.io.File;
import java.io.IOException;
import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MonitorContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent context) {
	}

	@Override
	public void contextInitialized(ServletContextEvent context) {
		String realPath = context.getServletContext().getRealPath("/");
		String reportFile = realPath + File.separatorChar + "WEB-INF" + File.separatorChar + "report.properties";
		try {
			EmailUtils.loadProperties(reportFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Timer timer = new Timer();
		AccessTokenChecker tokenChecker = new AccessTokenChecker();
		tokenChecker.loadProperties(realPath + File.separatorChar + "WEB-INF" + File.separatorChar + "accesstoken.properties");
		tokenChecker.setTimer(timer);
		
		NearbyServiceChecker eventsChecker = new NearbyServiceChecker();
		eventsChecker.loadProperties(realPath + File.separatorChar + "WEB-INF" + File.separatorChar + "nearbyEvents.properties");
		eventsChecker.setTimer(timer);
		
		NearbyServiceChecker poIsChecker = new NearbyServiceChecker();
		poIsChecker.loadProperties(realPath + File.separatorChar + "WEB-INF" + File.separatorChar + "nearbyPoIs.properties");
		poIsChecker.setTimer(timer);
		
		eventsChecker.start();
		poIsChecker.start();
		tokenChecker.start();
	}

}

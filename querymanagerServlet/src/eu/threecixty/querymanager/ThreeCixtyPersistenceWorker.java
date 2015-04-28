package eu.threecixty.querymanager;


import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import eu.threecixty.profile.PersistenceWorkerManager;

public class ThreeCixtyPersistenceWorker implements ServletContextListener {

	private volatile Thread thread;
	
	@Override
	public void contextDestroyed(ServletContextEvent context) {
		if (thread != null) {
			thread.interrupt();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent context) {
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(2000);
						while (true) {
							boolean successful = PersistenceWorkerManager.getInstance().saveOrUpdate();
							if (!successful) break; // empty queue or errors due to DB connection
						}
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		});
		thread.start();
	}

}

package eu.threecixty.querymanager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import eu.threecixty.db.PersistentObjectForWorker;

public class ThreeCixtyPersistenceWorker implements ServletContextListener {

	 private static final Logger LOGGER = Logger.getLogger(
			 ThreeCixtyPersistenceWorker.class.getName());
	
	 private static final BlockingQueue<PersistentObjectForWorker> queue = new LinkedBlockingQueue<PersistentObjectForWorker>();

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
						PersistentObjectForWorker persistenceObj;
						while ((persistenceObj = queue.poll()) != null) {
							try {
								persistenceObj.saveOrUpdate();
							} catch (Exception e) {
								LOGGER.error(e.getMessage());
							}
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

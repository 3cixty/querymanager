package eu.threecixty.profile;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import eu.threecixty.db.PersistentObjectForWorker;

public class PersistenceWorkerManager {
	
	private volatile ThreadPoolExecutor threadPool;
	private BlockingQueue<Runnable> queue;

	private static final PersistenceWorkerManager INSTANCE = new PersistenceWorkerManager();	 
	
	public static PersistenceWorkerManager getInstance() {
		return INSTANCE;
	}
	
	public void add(final PersistentObjectForWorker obj) {
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				obj.saveOrUpdate();
			}
		};
		threadPool.execute(runnable);
	}
	
	public void stop() {
		// knows and friends are not critical
		threadPool.shutdown();
	}
	
	private PersistenceWorkerManager() {
		queue = new LinkedBlockingQueue<Runnable>();
		threadPool = new ThreadPoolExecutor(5, 20, 1, TimeUnit.SECONDS, queue);
	}
}

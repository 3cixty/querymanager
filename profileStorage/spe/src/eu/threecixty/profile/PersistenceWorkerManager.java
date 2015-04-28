package eu.threecixty.profile;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import eu.threecixty.db.PersistentObjectForWorker;

public class PersistenceWorkerManager {

	private static final PersistenceWorkerManager INSTANCE = new PersistenceWorkerManager();
	
	 private static final Logger LOGGER = Logger.getLogger(
			 PersistenceWorkerManager.class.getName());
	
	 private static final BlockingQueue<PersistentObjectForWorker> queue = new LinkedBlockingQueue<PersistentObjectForWorker>();
	
	public static PersistenceWorkerManager getInstance() {
		return INSTANCE;
	}
	
	public void add(PersistentObjectForWorker obj) {
		queue.add(obj);
	}

	public boolean saveOrUpdate() {
		PersistentObjectForWorker persistenceObj = queue.poll();
		if (persistenceObj == null) return false;
		try {
			// XXX: ignore if there is an error (don't re-add it again)
			return persistenceObj.saveOrUpdate();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return false;
	}
	
	
	private PersistenceWorkerManager() {
	}
}

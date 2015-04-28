package eu.threecixty.db;

public interface PersistentObjectForWorker {

	
	/**
	 * Persists this instance into DB.
	 * @return
	 */
	boolean saveOrUpdate();
}

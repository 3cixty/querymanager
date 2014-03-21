package eu.threecixty.querymanager;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.util.FileManager;

 class QueryManager implements IQueryManager {

	private String filenameOrURI=null;
	private ClassLoader classLoader;
	private Query query;
	private Query augmentedQuery;
	private String uid=null;
	private Model connectCache;
	private Model connection;
	
	public QueryManager() {
	}

	public String getFilenameOrURI() {
		return filenameOrURI;
	}

	public void setFilenameOrURI(String filenameOrURI) {
		this.filenameOrURI = filenameOrURI;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public Query getAugmentedQuery() {
		return augmentedQuery;
	}

	public void setAugmentedQuery(Query augmentedQuery) {
		this.augmentedQuery = augmentedQuery;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Model getConnectCache() {
		return connectCache;
	}

	public void setConnectCache(Model connectCache) {
		this.connectCache = connectCache;
	}

	public Model getConnection() {
		return connection;
	}

	public void setConnection(Model connection) {
		this.connection = connection;
	}

	public Query getQuery() {
		return query;
	}

	/**
	 * overloaded setQuery. If user is not a part of the system
	 * 
	 * @param: the query
	 * @return: void
	 */
	public void setQuery(Query inputQuery){
		this.query = inputQuery;
		this.augmentedQuery = inputQuery;
	}

	/**
	 * overloaded setQuery. If user is a part of the system
	 * 
	 * @param: the query and a user ID
	 * @return: nothing note: user ID is Thales privacy manager part. it is they
	 *          who authenticate the system
	 */
	public void setQuery(Query inputQuery, String UID) {
		setQuery(inputQuery);
		setUid(UID);
	}
	
	public void connectToCache() {
	}
	
	@Override
	public ResultSet checkInCacheAndReturnResult(Query inputQuery) {
		return null;
	}

	public void setAuthenticationParameters(String UID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connectToKnowledgebase(ClassLoader classLoader, String filenameOrURI) {
		FileManager.get().addLocatorClassLoader(classLoader);
		setConnection(FileManager.get().loadModel(filenameOrURI));
	}
	
	@Override
	public void setAugmentedQuery(Query augmentedQuery, ResultSet preferences) {
		if (preferences==null){
			this.augmentedQuery=augmentedQuery;
		}
		else { 
			// TODO: implement this part
		}
	}
	
	@Override
	public ResultSet extractPreferenceSocial(String UID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet extractPreferenceMobile(String UID) {
		// TODO Auto-generated method stub
		return null;
	}	

	@Override
	public ResultSet getResultFromKB(Query augmentedQuery,Model connection) {
		// TODO Auto-generated method stub
		QueryExecution qexec=QueryExecutionFactory.create(augmentedQuery,connection);
		ResultSet results;
		try{
			results = qexec.execSelect();
		}finally{
			qexec.close();
		}
		return results;
	}
	
	@Override
	public void storeResultInCache(Query inputQuery, Query augmentedQuery, ResultSet result) {
		// TODO Auto-generated method stub
		
	}

}

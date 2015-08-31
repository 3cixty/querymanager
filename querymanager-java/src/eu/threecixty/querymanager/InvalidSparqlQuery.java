package eu.threecixty.querymanager;

public class InvalidSparqlQuery extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3984295715165030035L;
	
	public InvalidSparqlQuery() {
		super("The query does not conform to SPARQL 1.1");
	}
	
	public InvalidSparqlQuery(String msg) {
		super(msg);
	}

	public InvalidSparqlQuery(Throwable thr) {
		super(thr);
	}
	
	public InvalidSparqlQuery(String msg, Throwable thr) {
		super(msg, thr);
	}
}

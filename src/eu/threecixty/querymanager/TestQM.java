package eu.threecixty.querymanager;

import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;

public class TestQM {
	public static boolean checkarg(String args[])
	{
		if (args.length > 2) {
			return true;
		} 
		else {
			System.out.println("incorrect input format all 3 fields are required");			
		}
		return false;
	}
	public static void sparqlTest(String filenameOrURI, String queryString, String uid, ClassLoader classLoader){
		QueryManager qm=new QueryManager();
		qm.setUid(uid);
		qm.setClassLoader(classLoader);
		qm.setFilenameOrURI(filenameOrURI);
		qm.connectToKnowledgebase(classLoader,filenameOrURI);
		qm.setQuery(QueryFactory.create(queryString));
		ResultSet finalResult = qm.checkInCacheAndReturnResult(qm.getQuery());
		if (finalResult==null){
			ResultSet resultSocial = qm.extractPreferenceSocial(qm.getUid());
			qm.setAugmentedQuery(qm.getAugmentedQuery(),resultSocial);
			ResultSet resultMobile = qm.extractPreferenceMobile(qm.getUid());
			qm.setAugmentedQuery(qm.getAugmentedQuery(),resultMobile);
			finalResult = qm.getResultFromKB(qm.getAugmentedQuery(),qm.getConnection());
			qm.storeResultInCache(qm.getQuery(),qm.getAugmentedQuery(),finalResult);
		}
	}	
	public static void main(String args[]) throws Exception {
		System.out.println("Format input in args: knowledgeBase Name, Sparql Query, UserID");	
		String queryString=""; /**"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-nx#> "
				+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "Select * WHERE { "
				+ "?person foaf:name?x . "
				+ "}";*/
		String kbPath="";//"C:/Users/ragarwal/Desktop/INRIA/postdoc work/3cixty work/data.rdf";
		String UID="";//rachit
		if (checkarg(args)==false) {
			sparqlTest(kbPath,queryString,UID,TestQM.class.getClassLoader());
		}
	}
}

package eu.threecixty.profile;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import eu.threecixty.Configuration;

public class GetSetQueryStrings {
	public static final String PROFILE_URI = "http://data.linkedevents.org/person/";
	
	private static final String PREFIX = Configuration.PREFIXES;
	
	private static String makeUser(String uid) {
		String query= " <"+ PROFILE_URI+uid+"> profile:userID \""+uid+"\" . ";
		return query;
	}
	/**
	 * insert User in the KB
	 * @param uid
	 * @return
	 */
	public static String setUser(String uid){
		String query=PREFIX
			+ " INSERT DATA { GRAPH <"+ getGraphName(uid) +"> "
			+ " { ";
				query+= makeUser(uid);
			query+= "}}";
			return query;
	}
	
	
	/**
	 * Remove user from the kb
	 * @param uid
	 * @return
	 */
	public static String removeUser(String uid){
		String query=PREFIX
			+ "DELETE { GRAPH <"+ getGraphName(uid) +"> "
			+ "{ ";
			query+= makeUser(uid);
			query+= "}}";// Where {GRAPH <"+ getGraphName(uid) +"> "
			//+ "{ ";
			//query+= makeUser(uid);
			//query+= "}}";
			return query;
	}
	
	/**
	 * Get the URI of the user
	 * @param uid
	 * @return
	 */
	public static String getUserURI(String uid){
		String query=PREFIX
				+ "select ?uri "
				+ " where {"
					+" ?uri profile:userID \""+uid+"\". "
					+ "}";
		return query;
	}
	/**
	 * select last crawl time
	 * @param uid
	 * @return
	 */
	public static String getLastCrawlTime(String uid) {
		String query=PREFIX
				+ "select ?lastCrawlTime "
				+ " from <" + getGraphName(uid) + ">"
				+ " where {"
					+" ?s profile:userID \""+uid+"\". "//100900047095598983805
					+ "?s profile:hasLastCrawlTime ?lastCrawlTime. "
					+ "}";
		return query;
	}
	/**
	 * insert last crawl time. if time=null or "" then insert 0
	 * @param uid
	 * @param time
	 * @return
	 */
	public static String setLastCrawlTime(String uid, String time ){
		String query=PREFIX
			+ "INSERT DATA { GRAPH <"+ getGraphName(uid) +"> "
			+ "{ ";
				if (time==null || time.isEmpty()) time="0";
				query+= "<"+PROFILE_URI+uid+"> profile:hasLastCrawlTime \""+time+"\" ."
			+ "}}";
			return query;
	}
	/**
	 * remove last crawl time from the KB
	 * @param uid
	 * @param time
	 * @return
	 */
	public static String removeLastCrawlTime(String uid ){
		String query=PREFIX
			+ "DELETE  Where { GRAPH <"+ getGraphName(uid)+">"
			+ "{ "
				+ "<"+PROFILE_URI+uid+"> profile:hasLastCrawlTime ?o ."
			+ "}}";
			return query;
			/*
			 { GRAPH <"+ getGraphName(uid)+">"
			+ "{ "
				+ "<"+PROFILE_URI+uid+"> profile:hasLastCrawlTime ?o ."
			+ "}}
			 */
	}
		
	/**
	 * select gender
	 * @param uid
	 * @return
	 */
	public static String getGender(String uid) {
		String query=PREFIX
				+ "select ?gender from <" + getGraphName(uid) + ">"
				+ " where "
					+ "{ <" + PROFILE_URI + uid + "> schema:gender ?gender  } ";
		return query;
	}
	/**
	 * insert gender. if gender=null or "" then insert "unknown"
	 * @param uid
	 * @param time
	 * @return
	 */
	public static String setGender(String uid, String gender ){
		String query=PREFIX
			+ "INSERT DATA { GRAPH <"+ getGraphName(uid) +"> "
			+ "{ ";
			if (gender==null || gender.isEmpty())
				gender="unknown";
			query+= "<"+PROFILE_URI+uid+"> schema:gender \""+gender+"\" ."
			+ "}}";
			return query;
	}
	/**
	 * remove gender from the KB
	 * @param uid
	 * @param time
	 * @return
	 */
	public static String removeGender(String uid){
		String query=PREFIX
			+ "   DELETE  Where {GRAPH <"+ getGraphName(uid)+">"
			+ "  { "
			+ " <"+PROFILE_URI+uid+"> schema:gender ?o .}}";
			return query;
			/*
			 { GRAPH <"+ getGraphName(uid)+">"
			+ "  { "
			+ "<"+PROFILE_URI+uid+"> schema:gender ?o ."
			+ "}}
			 */
	}
	
	/**
	 * Select name of the user from KB
	 * @param uid
	 * @return
	 */
	public static String getName(String uid) {
		String query=PREFIX
				+ "select ?givenname ?familyname from <" + getGraphName(uid) + ">"
				+ " where {"
				+ " <" + PROFILE_URI + uid + "> schema:givenName ?givenname ;  schema:familyName ?familyname. } ";
		return query;
	}
	
	private static String makeNameQuery(String uid,
			eu.threecixty.profile.oldmodels.Name name) {
		String query="";
		if (name.getGivenName()!=null&&!name.getGivenName().isEmpty())
			query+= "  <"+PROFILE_URI+uid+"> schema:givenName \""+name.getGivenName()+"\".";
		if (name.getFamilyName()!=null&&!name.getFamilyName().isEmpty())
			query+= "  <"+PROFILE_URI+uid+"> schema:familyName \""+name.getFamilyName()+"\".";
		return query;
	}
	
	private static String makeRemoveNameQuery(String uid) {
		String query="";
		query+= "  <"+PROFILE_URI+uid+"> schema:givenName ?givenName .";
		query+= "  <"+PROFILE_URI+uid+"> schema:familyName ?familyName .";
		return query;
	}
	/**
	 * insert name object of the User in the KB 
	 * @param uid
	 * @param name
	 * @return
	 */
	public static String setName(String uid, eu.threecixty.profile.oldmodels.Name name ){
		String query=PREFIX
			+ "   INSERT DATA { GRAPH <"+ getGraphName(uid) +">"
			+ "  { ";
			query+= makeNameQuery(uid, name);
			query+= "}}";
			return query;
	}
	
	/**
	 * remove name object of the user from the KB
	 * @param uid
	 * @param name
	 * @return
	 */
	public static String removeName(String uid){
		String query=PREFIX
			+ "   DELETE Where { GRAPH <"+ getGraphName(uid)+">"
			+ "  { ";
			query+= makeRemoveNameQuery(uid);
			query+= "}}";
			return query;
			/*
			 { GRAPH <"+ getGraphName(uid)+">"
			+ "  { ";
			query+= makeRemoveNameQuery(uid);
			query+= "}}
			 */
	}
	
	/**
	 * Select address of the user from the KB
	 * @param uid
	 * @return
	 */
	public static String getAddress(String uid) {
		String query=PREFIX
				+ "select ?address ?townname ?countryname ?staddress ?pcode ?homeLocation ?geoLocation ?longitude ?lat "
				+ " from <" + getGraphName(uid) + "> "
				+ " where {"
				    + "OPTIONAL { <" + PROFILE_URI + uid + "> schema:address ?address .} "
					+ "OPTIONAL {?address schema:postalCode ?pcode.}"
					+ "OPTIONAL {?address schema:streetAddress ?staddress.}"
					+ "OPTIONAL {?address schema:addressLocality ?townname.}"
					+ "OPTIONAL {?address schema:addressCountry ?countryname.}"
					+ "OPTIONAL { <" + PROFILE_URI + uid + "> schema:homeLocation ?homeLocation.}"
					+ "OPTIONAL {?homeLocation schema:geo ?geoLocation.}"
					+ "OPTIONAL {?geoLocation schema:latitude ?lat.}"
					+ "OPTIONAL {?geoLocation schema:longitude ?longitude. }"
					+ "}";
		return query;
	}
	
	private static String makeAddressQuery(String uid,
			eu.threecixty.profile.oldmodels.Address address) {
		String query= "  <"+address.getHasAddressURI()+"> rdf:type schema:PostalAddress."
				+ "  <"+PROFILE_URI+uid+"> schema:address <"+address.getHasAddressURI()+"> .";
		if (address.getCountryName()!=null&&!address.getCountryName().isEmpty())
			query+= "  <"+address.getHasAddressURI()+"> schema:addressCountry \""+address.getCountryName()+"\".";
		if (address.getTownName()!=null&&!address.getTownName().isEmpty())
			query+= "  <"+address.getHasAddressURI()+"> schema:addressLocality \""+address.getTownName()+"\".";
		if (address.getStreetAddress()!=null&&!address.getStreetAddress().isEmpty())
			query+= "  <"+address.getHasAddressURI()+"> schema:streetAddress \""+address.getStreetAddress()+"\".";
		if (address.getPostalCode()!=null&&!address.getPostalCode().isEmpty())
			query+= "  <"+address.getHasAddressURI()+"> schema:postalCode \""+address.getPostalCode()+"\".";
		if (address.getLongitute()!=0 || address.getLatitude()!=0){
			String id=address.getHasAddressURI()+"/HomeLocation";
			query+="<"+PROFILE_URI+uid+"> schema:homeLocation <"+id+"> .";
			query+="<"+id+"> rdf:type schema:Place .";
			String idgeo=id+"/GeoCoordinates";
			query+="<"+id+"> schema:geo <"+idgeo+"> .";
			query+="<"+idgeo+"> rdf:type schema:GeoCoordinates .\n";
			if (address.getLongitute()!=0)	
				query+= "  <"+idgeo+"> schema:longitude " + address.getLongitute()+" .\n";
			if (address.getLatitude()!=0)
				query+= "  <"+idgeo+"> schema:latitude "+address.getLatitude()+" .\n";
		}
		return query;
	}

	/**
	 * insert Address object of the user in the KB
	 * @param uid
	 * @param address
	 * @return
	 */
	public static String setAddress(String uid, eu.threecixty.profile.oldmodels.Address address){
		String query=PREFIX
			+ "   INSERT DATA { GRAPH <"+ getGraphName(uid)+">"
			+ "  {";
		if (address.getHasAddressURI()!=null&&!address.getHasAddressURI().isEmpty()){
			query+= makeAddressQuery(uid,address);
		}
		query+= "}}";
		return query;
	}
	
	/**
	 * remove Address of the user from the KB
	 * @param uid
	 * @param address
	 * @return
	 */
	public static String removeAddress(String uid){
		String query=PREFIX
				+ "   DELETE { GRAPH <"+ getGraphName(uid)+">"
				+ "  { ?s schema:address ?address."
					+ "?address rdf:type schema:PostalAddress . "
					+ "?address schema:postalCode ?pcode."
					+ "?address schema:streetAddress ?staddress."
					+ "?address schema:addressLocality ?townname."
					+ "?address schema:addressCountry ?countryname."
					+ "?s schema:homeLocation ?homeLocation."
					+ "?homeLocation schema:geo ?geoLocation."
					+ "?geoLocation schema:latitude ?lat."
					+ "?geoLocation schema:longitude ?longitude. "
				+ "}} where { GRAPH <"+ getGraphName(uid)+"> "
				+ "  { "
					+" ?s profile:userID \""+uid+"\". "
					+ "?s schema:address ?address. "
					+ "?address rdf:type schema:PostalAddress . "
					+ "OPTIONAL {?address schema:postalCode ?pcode.}"
					+ "OPTIONAL {?address schema:streetAddress ?staddress.}"
					+ "OPTIONAL {?address schema:addressLocality ?townname.}"
					+ "OPTIONAL {?address schema:addressCountry ?countryname.}"
					+ "OPTIONAL {?s schema:homeLocation ?homeLocation.}"
					+ "OPTIONAL {?homeLocation schema:geo ?geoLocation.}"
					+ "OPTIONAL {?geoLocation schema:latitude ?lat.}"
					+ "OPTIONAL {?geoLocation schema:longitude ?longitude. } } }";
		return query;
			
			/*
			 { GRAPH <"+ getGraphName(uid)+">"
				+ "  {";
				query+= makeRemoveAddressQuery(uid);
			query+= "}}
			 */
		}
	
	/**
	 * insert multiple knows of the user in the kb
	 * @param uid
	 * @param knows
	 * @return
	 */
	public static String setMultipleKnows(String uid, Set <String> knows){
		String query=PREFIX
				+ " INSERT DATA { GRAPH <"+ getGraphName(uid)+">"
				+ " { ";
		Iterator <String> iterators = knows.iterator();
		for ( ; iterators.hasNext(); ){
			String uidKnows=iterators.next();
			query+= "  <"+PROFILE_URI+uid+"> schema:knows <" + (uidKnows.contains(PROFILE_URI) ? uidKnows : PROFILE_URI+uidKnows) + "> .";
		}
		query+= "}}";
		return query;
	}
	/**
	 * remove specific know of the user from the kb
	 * @param uid
	 * @param uidKnows
	 * @return
	 */
	public static String removeSingleKnows(String uid, String uidKnows){
		String query=PREFIX
				+ " DELETE DATA { GRAPH <"+ getGraphName(uid)+">"
				+ " { "
				+ "  <"+PROFILE_URI+uid+"> schema:knows <"+PROFILE_URI+uidKnows+"> ."
				+ "}}";
				return query;
	}
	/**
	 * remove All knows of the user from the kb
	 * @param uid
	 * @param knows
	 * @return
	 */
	public static String removeAllKnows(String uid){
		String query=PREFIX
				+ " DELETE { GRAPH <"+ getGraphName(uid)+">"
				+ " { ";
			query+= "  <"+PROFILE_URI+uid+"> schema:knows ?o.";
			query+= "}} Where {GRAPH <" + getGraphName(uid) + ">{  <"+PROFILE_URI+uid+"> schema:knows ?o.}}";
		return query;
	}

	/**
	 * select knows of the user from the kb
	 * @param uid
	 * @return
	 */
	public static String getKnows(String uid) {
		String query=PREFIX
				+ "select ?uidknows "
				+ " from <" + getGraphName(uid) + ">"
				+ " where"
				+ "{ <" + PROFILE_URI + uid + "> schema:knows ?uidknows  } ";
		return query;
	}
	
	private static String makeProfileItentitiesQuery(String uid,
			eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentity) {
		String query= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> rdf:type foaf:OnLineAccount."
				+ "  <"+PROFILE_URI+uid+"> foaf:account <"+profileIdentity.getHasProfileIdentitiesURI()+"> .";
		if  (profileIdentity.getHasUserAccountID()!=null&&!profileIdentity.getHasUserAccountID().isEmpty())
			query+= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> foaf:accountName \""+profileIdentity.getHasUserAccountID()+"\" .";
		if  (profileIdentity.getHasUserInteractionMode().toString()!=null&&!profileIdentity.getHasUserInteractionMode().toString().isEmpty())
			query+= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> profile:userInteractionMode \""+profileIdentity.getHasUserInteractionMode()+"\" .";
		return query;
	}
	/**
	 * insert profile Identity of a user in the KB	
	 * @param uid
	 * @param profileIdentities
	 * @return
	 */
	public static String setProfileIdentities(String uid, eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentity){
		String query=PREFIX
			+ "   INSERT DATA { GRAPH <"+ getGraphName(uid)+">"
			+ " { ";
		if  (profileIdentity.getHasProfileIdentitiesURI()==null||profileIdentity.getHasProfileIdentitiesURI().isEmpty())
			profileIdentity.setHasProfileIdentitiesURI(PROFILE_URI+uid+"/Account/"+profileIdentity.getHasSourceCarrier());
		query+= makeProfileItentitiesQuery(uid, profileIdentity);
		query+= "}}";
		return query;
	}

	/**
	 * insert multiple profile Identities of a user in the KB
	 * @param uid
	 * @param profileIdentities
	 * @return
	 */
	public static String setMultipleProfileIdentities(String uid, Set <eu.threecixty.profile.oldmodels.ProfileIdentities> profileIdentities){
		String query=PREFIX
				+ " INSERT DATA { GRAPH <"+ getGraphName(uid)+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.ProfileIdentities> iterators = profileIdentities.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentity=iterators.next();
			if  (profileIdentity.getHasProfileIdentitiesURI()==null||profileIdentity.getHasProfileIdentitiesURI().isEmpty())
				profileIdentity.setHasProfileIdentitiesURI(PROFILE_URI+uid+"/Account/"+profileIdentity.getHasSourceCarrier());
			query+= makeProfileItentitiesQuery(uid, profileIdentity);			
		}
		query+= "}}";
		return query;
	}	
	
	/**
	 * remove multiple profile Identities of a user in the KB
	 * @param uid
	 * @param profileIdentities
	 * @return
	 */
	public static String removeAllProfileIdentitiesOfUser(String uid){
		String query=PREFIX
				+ "  DELETE WHERE { GRAPH <"+ getGraphName(uid)+">"
				+ " { ";
				query+= " <"+PROFILE_URI+uid+"> foaf:account ?pi . "
						+ " ?pi ?p ?o . ";
		query+= "}}";
		return query;
	}	
	/**
	 * select profile Identities of a user from the KB
	 * @param uid
	 * @return
	 */
	public static String getProfileIdentities(String uid) {
		String query=PREFIX
				+ "select ?pi ?piID ?uIM "
				+ " from <" + getGraphName(uid) + ">"
				+ " where {"
					+" ?s profile:userID \""+uid+"\". "
					+ "?s foaf:account ?pi. "
					+ "?pi foaf:accountName ?piID. "
					+ "?pi profile:userInteractionMode ?uIM. "
					+ "}";
		return query;
	}
	
	private static String makeLikeQuery(String uid,
			eu.threecixty.profile.oldmodels.Likes like) {
		String query="  <"+like.getHasLikesURI()+"> rdf:type profile:Like."
			+ "  <"+PROFILE_URI+uid+"> profile:like <"+like.getHasLikesURI()+"> .";
		if (like.getHasLikeName()!=null&&!like.getHasLikeName().isEmpty())
			query+= "  <"+like.getHasLikesURI()+"> schema:likeName \""+like.getHasLikeName()+"\" .";
		if (like.getHasLikeType().toString()!=null&&!like.getHasLikeType().toString().isEmpty())
			query+= "  <"+like.getHasLikesURI()+"> dc:subject \""+like.getHasLikeType()+"\" .";
		return query;
	}
	
	/**
	 * insert multiple likes of the user in the kb
	 * @param perferenceURI
	 * @param likes
	 * @return
	 */
	public static String setMultipleLikes(String uid, Set<eu.threecixty.profile.oldmodels.Likes> likes){
		String query=PREFIX
			+ "   INSERT DATA { GRAPH <"+ getGraphName(uid)+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.Likes> iterators = likes.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.Likes like=iterators.next();
			if (like.getHasLikesURI()==null||like.getHasLikesURI().isEmpty())
				like.setHasLikesURI(PROFILE_URI+uid+"/Likes/"+UUID.randomUUID().toString());
			query+= makeLikeQuery(uid, like);
		}
		query+= "}}";
		return query;
	}
	
	/**
	 * remove multiple user likes from the kb
	 * @param perferenceURI
	 * @param likes
	 * @return
	 */
	public static String removeAllLikesOfUser(String uid){
		String query=PREFIX
			+ "   DELETE { GRAPH <"+ getGraphName(uid)+">"
			+ " { ";
		query+= " <"+PROFILE_URI+uid+"> profile:like ?likes . "
				+ " ?likes rdf:type profile:Like . "
				+ " ?likes schema:likeName ?likeName . "
				+ " ?likes dc:subject ?liketype . ";
			query+= "}} Where{GRAPH <" + getGraphName(uid) + "> { <"+PROFILE_URI+uid+"> profile:like ?likes . "
				+ " ?likes rdf:type profile:Like . "
				+ " ?likes schema:likeName ?likeName . "
				+ " ?likes dc:subject ?liketype . "
				+ " }}";
		return query;
	}
	/**
	 * select user likes from the kb 
	 * @param uid
	 * @return
	 */
	public static String getLikes(String uid) {
		String query=PREFIX
				+ "select ?likes ?likeName ?liketype "
				+ " from <" + getGraphName(uid) + "> "
				+ " where {"
					+" ?s profile:userID \""+uid+"\". "
					+ "?s profile:like ?likes. "
					+ "?likes schema:likeName ?likeName."
					+ "?likes dc:subject ?liketype."
					+ "}";
		return query;
	}
		
	private static String makeTransportQuery(String uid, String transportUri) {
		String query = "  <"+transportUri+"> rdf:type profile:Mobility ."
			+ "  <"+PROFILE_URI+uid+"> profile:mobility <"+transportUri+"> .";
		return query;
	}
	/**
	 * insert transport of a user in the KB. This inserts only the transport uri not the transport object
	 * @param uid
	 * @param transportUri
	 * @return
	 */
	public static String setTransport(String uid, String transportUri){
		String query=PREFIX
			+ "   INSERT DATA { GRAPH <"+ getGraphName(uid)+">"
			+ " { ";
			if (transportUri!=null&&!transportUri.isEmpty()){
				query+= makeTransportQuery(uid, transportUri);
			}
			query+= "}}";
			return query;
	}
	
	/**
	 * insert multiple transport of a user in the KB. This inserts only the transport uri not the transport object
	 * @param uid
	 * @param transportUris
	 * @return
	 */
	public static String setMultipleTransport(String uid, Set<String> transportUris){
		String query=PREFIX
			+ "   INSERT DATA { GRAPH <"+ getGraphName(uid)+">"
			+ " { ";
			Iterator <String> iterators = transportUris.iterator();
			for ( ; iterators.hasNext(); ){
				String transportUri=iterators.next();
				if (transportUri!=null&&!transportUri.isEmpty()){
					query+= makeTransportQuery(uid, transportUri);
				}
			}
			query+= "}}";
			return query;
	}

	/**
	 * remove transport of a user in the KB. This removes only the transport uri not the transport object
	 * @param uid
	 * @param transportUri
	 * @return
	 */
	public static String removeTransport(String uid){
		String query=PREFIX
			+ "   DELETE { GRAPH <"+ getGraphName(uid)+">"
			+ " { "
			+"  <"+PROFILE_URI+uid+"> profile:mobility ?o ."
			+"  ?o rdf:type profile:Mobility .";
			query+= "}} where {GRAPH <" + getGraphName(uid) + ">{  <"+PROFILE_URI+uid+"> profile:mobility ?o ."
						+"  ?o rdf:type profile:Mobility .}}";
			return query;
	}
	
	/**
	 * select transport of a user in the KB. This selects only the transport uri not the transport object
	 * @param uid
	 * @return
	 */
	public static String getTransport(String uid) {
		String query=PREFIX
				+ "select ?transport "
				+ " from <" + getGraphName(uid) + "> "
				+ " where {"
					+" ?s profile:userID \""+uid+"\". "
					+ "?s profile:mobility ?transport. "
					+ "}";
		return query;
	}

	private static String makeAccompanyQuery(String transportURI,
			eu.threecixty.profile.oldmodels.Accompanying accompany) {
		String query= "  <"+transportURI+"> profile:accompany <"+accompany.getHasAccompanyURI()+"> .";
		if (accompany.getHasAccompanyUserid2ST()!=null&&! accompany.getHasAccompanyUserid2ST().isEmpty())
			query+= "  <"+accompany.getHasAccompanyURI()+"> profile:accompanyUser \""+accompany.getHasAccompanyUserid2ST()+"\" .";
		if (accompany.getHasAccompanyScore()!=null&&accompany.getHasAccompanyScore()>0)
			query+= "  <"+accompany.getHasAccompanyURI()+"> profile:score \""+accompany.getHasAccompanyScore()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
		if (accompany.getHasAccompanyValidity()!=null&&accompany.getHasAccompanyValidity()>0)
			query+= "  <"+accompany.getHasAccompanyURI()+"> profile:validity \""+accompany.getHasAccompanyValidity()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		if (accompany.getHasAccompanyTime()!=null&&accompany.getHasAccompanyTime()>0)
			query+= "  <"+accompany.getHasAccompanyURI()+"> profile:time \""+accompany.getHasAccompanyTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		query+="  <"+accompany.getHasAccompanyURI()+"> rdf:type profile:Accompany.";
		return query;
	}
	
	/**
	 * insert multiple accompanies in the kb. This function is same as setMultipleAccompanying(,)
	 * @param transportURI
	 * @param accompanys
	 * @return
	 */
	public static String setMultipleAccompanyingAssociatedToSpecificTransport(String uid, String transportURI, Set<eu.threecixty.profile.oldmodels.Accompanying> accompanys){
		String query=PREFIX
				+ "   INSERT DATA { GRAPH <"+ getGraphName(uid)+">"
				+ " { ";
			Iterator <eu.threecixty.profile.oldmodels.Accompanying> iterators = accompanys.iterator();
			for ( ; iterators.hasNext(); ){
				eu.threecixty.profile.oldmodels.Accompanying accompany =iterators.next();
				if (accompany.getHasAccompanyURI()==null||accompany.getHasAccompanyURI().isEmpty())
					accompany.setHasAccompanyURI(transportURI+"/Accompany/"+UUID.randomUUID().toString());
				
				query+= makeAccompanyQuery(transportURI, accompany);
				
			}
			query+= "}}";
			return query;
	}

	/**
	 * remove multiple accompanies in the kb. This function is same as removeMultipleAccompanying(,)
	 * @param transportUri
	 * @param accompanys
	 * @return
	 */
	public static String removeMultipleAccompanyingAssociatedToSpecificTransport(String uid, String transportURI){
		String query=PREFIX
				+ "   DELETE { GRAPH <"+ getGraphName(uid)+">"
				+ " { ";
		query+=" <"+transportURI+"> profile:accompany ?accompany. "
				+ "?accompany rdf:type profile:Accompany. "
				+ "?accompany profile:accompanyUser ?uid2 ."
				+ "?accompany profile:score ?score ."
				+ "?accompany profile:validity ?validity ."
				+ "?accompany profile:time ?acctime .  ";
			query+= "}} where {GRAPH <" + getGraphName(uid) + "> { <"+transportURI+"> profile:accompany ?accompany. "
					+ "?accompany rdf:type profile:Accompany. "
				+ "Optional {?accompany profile:accompanyUser ?uid2 .}"
				+ "Optional {?accompany profile:score ?score .}"
				+ "Optional {?accompany profile:validity ?validity .}"
				+ "Optional {?accompany profile:time ?acctime .}}}";
			return query;
	}

	/**
	 * select accompanies of a user associated to a given transport
	 * @param transportURI
	 * @return
	 */
	public static String getAccompanyingForTransport(String transportURI) {
		String query=PREFIX
				+ "select ?accompany ?uid2 ?score ?validity ?acctime "
					+ " where {"
					+ " <"+transportURI+"> profile:accompany ?accompany. "
					+ " Optional {?accompany profile:accompanyUser ?uid2 .}"
					+ " Optional {?accompany profile:score ?score .}"
					+ " Optional {?accompany profile:validity ?validity .}"
					+ " Optional {?accompany profile:time ?acctime .}"
					+ "}";
		return query;
	}
	
	/**
	 * insert personal places associated to a specific regular trip
	 * @param regularTripURI
	 * @param personalPlace
	 * @return
	 */
	public static String setPersonalPlacesAssociatedToSpecificRegularTrip(String uid, String regularTripURI, eu.threecixty.profile.oldmodels.PersonalPlace personalPlace ){
		String query=PREFIX
			+ "   INSERT DATA { GRAPH <"+ getGraphName(uid)+">"
			+ " { ";
		if (personalPlace.getHasPersonalPlaceURI()==null||personalPlace.getHasPersonalPlaceURI().isEmpty())
			personalPlace.setHasPersonalPlaceURI(regularTripURI+"/PersonalPlace/"+UUID.randomUUID().toString());
		
		query+= makePersonalPlaceQuery(regularTripURI, personalPlace);

		query+= "}}";
		return query;
	}
	private static String makePersonalPlaceQuery(String regularTripURI,
			eu.threecixty.profile.oldmodels.PersonalPlace personalPlace) {
		String query= "  <"+regularTripURI+"> profile:personalPlace <"+personalPlace.getHasPersonalPlaceURI()+"> .";
		if (personalPlace.getHasPersonalPlaceexternalIds()!=null&&!personalPlace.getHasPersonalPlaceexternalIds().isEmpty())
			query+= " <"+personalPlace.getHasPersonalPlaceURI()+"> profile:externalIDs \""+personalPlace.getHasPersonalPlaceexternalIds()+"\" .";
		if (personalPlace.getLatitude()!=null&&personalPlace.getLatitude()>0)
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:latitude \""+personalPlace.getLatitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
		if (personalPlace.getLongitude()!=null&&personalPlace.getLongitude()>0)
			query+="  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:longitude \""+personalPlace.getLongitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
		if (personalPlace.getHasPersonalPlaceStayDuration()!=null&&personalPlace.getHasPersonalPlaceStayDuration()>0)
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:stayDuration \""+personalPlace.getHasPersonalPlaceStayDuration()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		if (personalPlace.getHasPersonalPlaceAccuracy()!=null&&personalPlace.getHasPersonalPlaceAccuracy()>0)
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:accuracy \""+personalPlace.getHasPersonalPlaceAccuracy()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
		if (personalPlace.getHasPersonalPlaceStayPercentage()!=null&&personalPlace.getHasPersonalPlaceStayPercentage()>0)
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:stayPercentage \""+personalPlace.getHasPersonalPlaceStayPercentage()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
		if (personalPlace.getPostalcode()!=null&&!personalPlace.getPostalcode().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:postalCode \""+personalPlace.getPostalcode()+"\" .";
		if (personalPlace.getHasPersonalPlaceWeekdayPattern()!=null&&!personalPlace.getHasPersonalPlaceWeekdayPattern().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:weekDayPattern \""+personalPlace.getHasPersonalPlaceWeekdayPattern()+"\" .";
		if (personalPlace.getHasPersonalPlaceDayhourPattern()!=null&&!personalPlace.getHasPersonalPlaceDayhourPattern().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:dayHourPattern \""+personalPlace.getHasPersonalPlaceDayhourPattern()+"\" .";
		if (personalPlace.getHasPersonalPlaceType()!=null&&!personalPlace.getHasPersonalPlaceType().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:type \""+personalPlace.getHasPersonalPlaceType()+"\" .";
		if (personalPlace.getHasPersonalPlaceName()!=null&&!personalPlace.getHasPersonalPlaceName().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> rdfs:label \""+personalPlace.getHasPersonalPlaceName()+"\" .";
		//query+= "  profile:"+uid+"PersonalPlace/"+ID+" profile:hasUID \""+ID+"\" .";
		query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> rdf:type profile:PersonalPlace .";
		return query;
	}
	/**
	 * insert multiple personal places associated to a specific regular trip
	 * @param regularTripURI
	 * @param personalPlaces
	 * @return
	 */
	public static String setMultiplePersonalPlacesAssociatedToSpecificRegularTrip(String uid, String regularTripURI, Set<eu.threecixty.profile.oldmodels.PersonalPlace> personalPlaces ){
		String query=PREFIX
				+ "   INSERT DATA { GRAPH <"+ getGraphName(uid)+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.PersonalPlace> iterators = personalPlaces.iterator();
		for ( ; iterators.hasNext(); ){	
			eu.threecixty.profile.oldmodels.PersonalPlace personalPlace=iterators.next();
			if (personalPlace.getHasPersonalPlaceURI()==null||personalPlace.getHasPersonalPlaceURI().isEmpty())
				personalPlace.setHasPersonalPlaceURI(regularTripURI+"/PersonalPlace/"+UUID.randomUUID().toString());
				
			query+= makePersonalPlaceQuery(regularTripURI, personalPlace);

		}
		query+= "}}";
		return query;
	}
	private static String makeRemovePersonalPlaceQuery(String regularTripURI) {
		String query= "  <"+regularTripURI+"> profile:personalPlace ?pplace ."
		+ "?pplace profile:externalIDs ?externalIDs ."
		+ "?pplace profile:latitude ?latitude ."
		+ "?pplace profile:longitude ?longitude ."
		+ "?pplace profile:stayDuration ?stayDuration ."
		+ "?pplace profile:accuracy ?accuracy ."
		+ "?pplace profile:stayPercentage ?stayPercentage ."
		+ "?pplace profile:postalCode ?pcode ."
		+ "?pplace profile:weekDayPattern ?weekDayPattern ."
		+ "?pplace profile:dayHourPattern ?dayHourPattern ."
		+ "?pplace profile:type ?placeType ."
		+ "?pplace rdfs:label ?placeName ."
		+"  ?personalPlace rdf:type profile:PersonalPlace .";
		return query;
	}
	

	/**
	 * remove multiple personal places associated to a specific regular trip
	 * @param regularTripURI
	 * @param personalPlaces
	 * @return
	 */
	public static String removeMultiplePersonalPlacesAssociatedToSpecificRegularTrip(String uid, String regularTripURI){
		String query=PREFIX
				+ "   DELETE { GRAPH <"+ getGraphName(uid)+">"
				+ " { ";
				query+= makeRemovePersonalPlaceQuery(regularTripURI);
				query+= "}} Where {GRAPH <" + getGraphName(uid) + ">{"
				+"  <"+regularTripURI+"> profile:personalPlace ?pplace ."
				+ "Optional {?pplace profile:externalIDs ?externalIDs .}"
				+ "Optional {?pplace profile:latitude ?latitude .}"
				+ "Optional {?pplace profile:longitude ?longitude .}"
				+ "Optional {?pplace profile:stayDuration ?stayDuration .}"
				+ "Optional {?pplace profile:accuracy ?accuracy .}"
				+ "Optional {?pplace profile:stayPercentage ?stayPercentage .}"
				+ "Optional {?pplace profile:postalCode ?pcode .}"
				+ "Optional {?pplace profile:weekDayPattern ?weekDayPattern .}"
				+ "Optional {?pplace profile:dayHourPattern ?dayHourPattern .}"
				+ "Optional {?pplace profile:type ?placeType .}"
				+ "Optional {?pplace rdfs:label ?placeName .}"
				+"  ?personalPlace rdf:type profile:PersonalPlace .";
				query+= "}}";
			return query;
	}
	
	/**
	 * make Get Personal Places Query
	 * @return
	 */
	private static String makeGetPersonalPlacesQuery(){
		String query= "?pplace a profile:PersonalPlace. "
				+ "Optional {?pplace profile:externalIDs ?externalIDs .}"
				+ "Optional {?pplace profile:latitude ?latitude .}"
				+ "Optional {?pplace profile:longitude ?longitude .}"
				+ "Optional {?pplace profile:stayDuration ?stayDuration .}"
				+ "Optional {?pplace profile:accuracy ?accuracy .}"
				+ "Optional {?pplace profile:stayPercentage ?stayPercentage .}"
				+ "Optional {?pplace profile:postalCode ?pcode .}"
				+ "Optional {?pplace profile:weekDayPattern ?weekDayPattern .}"
				+ "Optional {?pplace profile:dayHourPattern ?dayHourPattern .}"
				+ "Optional {?pplace profile:type ?placeType .}"
				+ "Optional {?pplace rdfs:label ?placeName .}";
		return query;
	}
	
	/**
	 * select personal places associated for a regular trip of the user
	 * @param regularTripURI
	 * @return
	 */
	public static String getPersonalPlacesForRegularTrips(String regularTripURI) {
		String query=PREFIX
				+ "select ?pplace ?externalIDs ?latitude ?longitude ?stayDuration ?accuracy ?stayPercentage ?pcode ?weekDayPattern ?dayHourPattern ?placeType ?placeName "
				+ " where {"
					+ "<"+regularTripURI+"> profile:personalPlace ?pplace .";
					query+=makeGetPersonalPlacesQuery();
					query+= "}";
		return query;
	}
		
	/**
	 * make Regular Trip Query
	 * @param transportUri
	 * @param regularTrip
	 * @return
	 */
	private static String makeRegularTripQuery(String transportUri,
			eu.threecixty.profile.oldmodels.RegularTrip regularTrip) {
		String query= "  <"+transportUri+"> profile:regularTrip <"+regularTrip.getHasRegularTripURI()+"> .";
		if (regularTrip.getHasRegularTripName()!=null&&!regularTrip.getHasRegularTripName().isEmpty())
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> rdfs:label \""+regularTrip.getHasRegularTripName()+"\" .";
		if (regularTrip.getHasRegularTripDepartureTime()!=null&&regularTrip.getHasRegularTripDepartureTime()>0)
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:departureTime \""+regularTrip.getHasRegularTripDepartureTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		if (regularTrip.getHasRegularTripDepartureTimeSD()!=null&&regularTrip.getHasRegularTripDepartureTimeSD()>0)
			query+="  <"+regularTrip.getHasRegularTripURI()+"> profile:departureTimeSD \""+regularTrip.getHasRegularTripDepartureTimeSD()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		if (regularTrip.getHasRegularTripTravelTime()!=null&&regularTrip.getHasRegularTripTravelTime()>0)
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:travelTime \""+regularTrip.getHasRegularTripTravelTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		if (regularTrip.getHasRegularTripTravelTimeSD()!=null&&regularTrip.getHasRegularTripTravelTimeSD()>0)
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:travelTimeSD \""+regularTrip.getHasRegularTripTravelTimeSD()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		//if (regularTrip.getHasRegularTripFastestTravelTime()!=null&&regularTrip.getHasRegularTripFastestTravelTime()>0)
			//query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:fastestTravelTime \""+regularTrip.getHasRegularTripFastestTravelTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		if (regularTrip.getHasRegularTripLastChanged()!=null&&regularTrip.getHasRegularTripLastChanged()>0)
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:lastChanged \""+regularTrip.getHasRegularTripLastChanged()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		if (regularTrip.getHasRegularTripTotalDistance()!=null&&regularTrip.getHasRegularTripTotalDistance()>0)
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:totalDistance \""+regularTrip.getHasRegularTripTotalDistance()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
		if (regularTrip.getHasRegularTripTotalCount()!=null&&regularTrip.getHasRegularTripTotalCount()>0)
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:totalCount \""+regularTrip.getHasRegularTripTotalCount()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
		if (regularTrip.getHasModalityType().toString()!=null&&!regularTrip.getHasModalityType().toString().isEmpty())
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:tripModality \""+regularTrip.getHasModalityType()+"\" .";
		if (regularTrip.getHasRegularTripWeekdayPattern()!=null&&!regularTrip.getHasRegularTripWeekdayPattern().isEmpty())
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:weekdayPattern \""+regularTrip.getHasRegularTripWeekdayPattern()+"\" .";
		if (regularTrip.getHasRegularTripDayhourPattern()!=null&&!regularTrip.getHasRegularTripDayhourPattern().isEmpty())
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:dayhourPattern \""+regularTrip.getHasRegularTripDayhourPattern()+"\" .";
		//if (regularTrip.getHasRegularTripTravelTimePattern()!=null&&!regularTrip.getHasRegularTripTravelTimePattern().isEmpty())
		//	query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTimePattern \""+regularTrip.getHasRegularTripTravelTimePattern()+"\" .";
		if (regularTrip.getHasRegularTripWeatherPattern()!=null&&!regularTrip.getHasRegularTripWeatherPattern().isEmpty())
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:weatherPattern \""+regularTrip.getHasRegularTripWeatherPattern()+"\" .";
		query+= "  <"+regularTrip.getHasRegularTripURI()+"> rdf:type profile:RegularTrip .";
		return query;
	}
		
	/**
	 * insert multiple regular trip associated to a specific transport of a user in the kb
	 * @param transportUri
	 * @param regularTrips
	 * @return
	 */
	public static String setMultipleRegularTripsAssociatedToSpecificTransport(String uid, String transportUri, Set<eu.threecixty.profile.oldmodels.RegularTrip> regularTrips){
		String query=PREFIX
			+ "   INSERT DATA { GRAPH <"+ getGraphName(uid)+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.RegularTrip> iterators = regularTrips.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.RegularTrip regularTrip= iterators.next();
			if (regularTrip.getHasRegularTripURI()!=null&&!regularTrip.getHasRegularTripURI().isEmpty()){
				query+= makeRegularTripQuery(transportUri, regularTrip);
			}
		}
		query+= "}}";
		return query;
	}
	
	
	private static String makeRemoveRegularTripQuery(String transportUri) {
		String query= "  <"+transportUri+"> profile:regularTrip ?regularTrip ."
		+"?transport profile:regularTrip ?regularTrip. "
		+ "?regularTrip profile:id ?tripID ."
		+ "?regularTrip rdfs:label ?name ."
		+ "?regularTrip profile:departureTime ?departureTime ."
		+ "?regularTrip profile:departureTimeSD ?departuretimeSD ."
		+ "?regularTrip profile:travelTime ?travelTime ."
		+ "?regularTrip profile:travelTimeSD ?travelTimeSD ."
		//+ "Optional {?regularTrip profile:hasRegularTripFastestTravelTime ?fastestTravelTime .}"
		+ "?regularTrip profile:lastChanged ?lastChanged ."
		+ "?regularTrip profile:totalDistance ?totalDistance ."
		+ "?regularTrip profile:totalCount ?totalCount ."
		+ "?regularTrip profile:tripModality ?modalityType ."
		+ "?regularTrip profile:weekdayPattern ?weekdayPattern ."
		+ "?regularTrip profile:dayhourPattern ?dayhourPattern ."
		//+ "Optional {?regularTrip profile:hasRegularTripTimePattern ?timePattern .}"
		+ "?regularTrip profile:weatherPattern ?weatherPattern .";
		query+= "  ?regularTrip rdf:type profile:RegularTrip .";
		return query;
	}
	/**
	 * remove multiple regular trip associated to a specific transport of a user in the kb
	 * @param transportUri
	 * @param regularTrips
	 * @return
	 */
	public static String removeMultipleRegularTripsAssociatedToSpecificTransport(String uid, String transportUri){
		String query=PREFIX
			+ "   DELETE { GRAPH <"+ getGraphName(uid)+">"
			+ " { ";
				query+= makeRemoveRegularTripQuery(transportUri);
		query+= "}} where {GRAPH <" + getGraphName(uid) + ">{"
			+"  <"+transportUri+"> profile:regularTrip ?regularTrip ."
			+"?transport profile:regularTrip ?regularTrip. "
			+ "Optional {?regularTrip profile:id ?tripID .}"
			+ "Optional {?regularTrip rdfs:label ?name .}"
			+ "Optional {?regularTrip profile:departureTime ?departureTime .}"
			+ "Optional {?regularTrip profile:departureTimeSD ?departuretimeSD .}"
			+ "Optional {?regularTrip profile:travelTime ?travelTime .}"
			+ "Optional {?regularTrip profile:travelTimeSD ?travelTimeSD .}"
			//+ "Optional {?regularTrip profile:hasRegularTripFastestTravelTime ?fastestTravelTime .}"
			+ "Optional {?regularTrip profile:lastChanged ?lastChanged .}"
			+ "Optional {?regularTrip profile:totalDistance ?totalDistance .}"
			+ "Optional {?regularTrip profile:totalCount ?totalCount .}"
			+ "Optional {?regularTrip profile:tripModality ?modalityType .}"
			+ "Optional {?regularTrip profile:weekdayPattern ?weekdayPattern .}"
			+ "Optional {?regularTrip profile:dayhourPattern ?dayhourPattern .}"
			//+ "Optional {?regularTrip profile:hasRegularTripTimePattern ?timePattern .}"
			+ "Optional {?regularTrip profile:weatherPattern ?weatherPattern .}";
			query+= "  ?regularTrip rdf:type profile:RegularTrip .";
		query+= "}}";
		return query;
	}
	/**
	 * make get regular trips query
	 * @return
	 */
	private static String makeGetRegularTripsQuery(String transportUri){
		String query= "  <"+transportUri+"> profile:regularTrip ?regularTrip. "
				+ "Optional {?regularTrip profile:id ?tripID .}"
				+ "Optional {?regularTrip rdfs:label ?name .}"
				+ "Optional {?regularTrip profile:departureTime ?departureTime .}"
				+ "Optional {?regularTrip profile:departureTimeSD ?departuretimeSD .}"
				+ "Optional {?regularTrip profile:travelTime ?travelTime .}"
				+ "Optional {?regularTrip profile:travelTimeSD ?travelTimeSD .}"
				//+ "Optional {?regularTrip profile:hasRegularTripFastestTravelTime ?fastestTravelTime .}"
				+ "Optional {?regularTrip profile:lastChanged ?lastChanged .}"
				+ "Optional {?regularTrip profile:totalDistance ?totalDistance .}"
				+ "Optional {?regularTrip profile:totalCount ?totalCount .}"
				+ "Optional {?regularTrip profile:tripModality ?modalityType .}"
				+ "Optional {?regularTrip profile:weekdayPattern ?weekdayPattern .}"
				+ "Optional {?regularTrip profile:dayhourPattern ?dayhourPattern .}"
				//+ "Optional {?regularTrip profile:hasRegularTripTimePattern ?timePattern .}"
				+ "Optional {?regularTrip profile:weatherPattern ?weatherPattern .}";
		return query;
	}
	
	/**
	 * select regular trip associated to a specific transport of a user in the kb
	 * @param transportURI
	 * @return
	 */
	public static String getRegularTripsForTransport(String transportURI) {
		String query=PREFIX
				+ " select ?regularTrip ?tripID ?name ?departureTime ?departureTimeSD ?travelTime ?travelTimeSD ?lastChanged ?totalDistance ?totalCount ?modalityType ?weekdayPattern ?dayhourPattern  ?weatherPattern "//?pplace "
				+ " where {";
				query+=makeGetRegularTripsQuery(transportURI);
				query+= "}";
		return query;
	}
	
	/**
	 * select regular trip associated to a specific transport of a user in the kb
	 * @param transportURI
	 * @return
	 */
	public static String getRegularTripsURIForTransport(String transportURI) {
		String query=PREFIX
				+ " select ?regularTrip "
				+ " where {"
				+ "  <"+transportURI+"> profile:regularTrip ?regularTrip. "
				+ "}";
		return query;
	}
	/**
	 * make query for the TripPreference
	 * @param uid
	 * @param tripPreference
	 * @return
	 */
	private static String makeTripPreferenceQuery(String uid,
			eu.threecixty.profile.oldmodels.TripPreference tripPreference) {
		
		String uri=tripPreference.getHasTripPreferenceURI();
		String aboutSt=uri+"/aboutblankNode";
		String filterSt=uri+"/filterblankNode";
		String query= " <"+PROFILE_URI+uid+"> frap:holds <"+uri+"> .";
		query+= "  <"+uri+"> rdf:type frap:Preference .\n";
		query+= " <"+uri+"> frap:about <"+aboutSt+"> . "
		+ "<"+aboutSt+">  rdf:type frap:Pattern . "
		+ "<"+aboutSt+">  frap:filter <"+filterSt+"> . "
		+ "<"+filterSt+"> rdf:type frap:Filter . ";
		if (tripPreference.getHasPreferredMaxTotalDistance()!=null&&tripPreference.getHasPreferredMaxTotalDistance()>0)
			query+= "<"+filterSt+"> profile:hasPreferredMaxTotalDistance \""+tripPreference.getHasPreferredMaxTotalDistance()+"\"^^<http://www.w3.org/2001/XMLSchema#double> . \n";
		if (tripPreference.getHasPreferredTripDuration()!=null&&tripPreference.getHasPreferredTripDuration()>0)
			query+="<"+filterSt+"> profile:hasPreferredTripDuration \""+tripPreference.getHasPreferredTripDuration()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . \n";
		if (tripPreference.getHasPreferredTripTime()!=null&&tripPreference.getHasPreferredTripTime()>0)
			query+="<"+filterSt+"> profile:hasPreferredTripTime \""+tripPreference.getHasPreferredTripTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . \n";
		if (tripPreference.getHasPreferredCity()!=null&&!tripPreference.getHasPreferredCity().isEmpty())
			query+="<"+ filterSt +"> profile:hasPreferredCity \""+tripPreference.getHasPreferredCity()+"\" . \n";
		if (tripPreference.getHasPreferredCountry()!=null&&!tripPreference.getHasPreferredCountry().isEmpty())
			query+="<"+filterSt+">  profile:hasPreferredCountry \""+tripPreference.getHasPreferredCountry()+"\" . \n";
		if (tripPreference.getHasPreferredWeatherCondition()!=null&&!tripPreference.getHasPreferredWeatherCondition().isEmpty())
			query+="<"+filterSt +"> profile:hasPreferredWeatherCondition \""+tripPreference.getHasPreferredWeatherCondition()+"\" . \n";
		if (tripPreference.getHasPreferredMinTimeOfAccompany()!=null&&tripPreference.getHasPreferredMinTimeOfAccompany()>0)
			query+="<"+filterSt+"> profile:hasPreferredMinTimeOfAccompany \""+tripPreference.getHasPreferredMinTimeOfAccompany()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . \n";
		if (tripPreference.getHasModalityType()!=null)
			query+="<"+filterSt +"> profile:hasModalityType \""+tripPreference.getHasModalityType().toString()+"\" . \n";
		query+=" <"+PROFILE_URI+uid+"> frap:holds <"+uri+"> . \n";
		return query;
	}

	/**
	 * insert multiple Trip preferences of the user in the kb
	 * @param uid
	 * @param tripPreferences
	 * @return
	 */
	public static String setMultipleTripPreferences(String uid, Set<eu.threecixty.profile.oldmodels.TripPreference> tripPreferences){
		String query=PREFIX
				+ "   INSERT DATA { GRAPH <"+ getGraphName(uid)+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.TripPreference> iterators = tripPreferences.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.TripPreference tripPreference=iterators.next();
			if (tripPreference.getHasTripPreferenceURI()==null ||tripPreference.getHasTripPreferenceURI().isEmpty())
				tripPreference.setHasTripPreferenceURI(PROFILE_URI+uid+"/TripPreference/"+UUID.randomUUID().toString());
			
			query+= makeTripPreferenceQuery(uid, tripPreference);
			
		}
		query+= "}}";
		return query;
	}

	private static String makeRemoveTripPreferenceQuery() {

		String query= "?s frap:holds ?tripPreference. \n"
				+ "?tripPreference frap:about ?about . \n"
				+ "?about frap:filter ?filter . \n"
				+ "?filter profile:hasPreferredMaxTotalDistance ?preferredMaxTotalDistance . \n"
				+ "?filter profile:hasPreferredTripDuration ?preferredTripDuration . \n"
				+ "?filter profile:hasPreferredTripTime ?preferredTripTime . \n"
				+ "?filter profile:hasPreferredCity ?preferredCity . \n"
				+ "?filter profile:hasPreferredCountry ?preferredCountry . \n"
				+ "?filter profile:hasPreferredWeatherCondition ?preferredWeatherCondition . \n"
				+ "?filter profile:hasPreferredMinTimeOfAccompany ?preferredMinTimeOfAccompany . \n"
				+ "?filter profile:hasModalityType ?modality . \n";
		return query;
	}
	/**
	 * remove multiple Trip preferences of the user in the kb
	 * @param uid
	 * @param tripPreferences
	 * @return
	 */
	public static String removeMultipleTripPreferences(String uid){
		String query=PREFIX
				+ "   DELETE { GRAPH <"+ getGraphName(uid)+">"
				+ " { ";
				query+= makeRemoveTripPreferenceQuery();
		query+= "}} Where { GRAPH <" + getGraphName(uid) + "> {"
				+" ?s profile:userID \""+uid+"\" . \n"
				+" ?s frap:holds ?tripPreference. \n"
				+ "?tripPreference frap:about ?about . \n"
				+ "?about frap:filter ?filter . \n"
				+ " Optional {?filter profile:hasPreferredMaxTotalDistance ?preferredMaxTotalDistance .} \n"
				+ " Optional {?filter profile:hasPreferredTripDuration ?preferredTripDuration .} \n"
				+ " Optional {?filter profile:hasPreferredTripTime ?preferredTripTime .} \n"
				+ " Optional {?filter profile:hasPreferredCity ?preferredCity .} \n"
				+ " Optional {?filter profile:hasPreferredCountry ?preferredCountry .} \n"
				+ " Optional {?filter profile:hasPreferredWeatherCondition ?preferredWeatherCondition .} \n"
				+ " Optional {?filter profile:hasPreferredMinTimeOfAccompany ?preferredMinTimeOfAccompany .} \n"
				+ " Optional {?filter profile:hasModalityType ?modality .} \n";
				query+= "}}";
		System.out.println(query);
		return query;
	}
	/**
	 * select Trip preferences of the user in the kb
	 * @param uid
	 * @return
	 */
	public static String getTripPreferences(String uid) {
		String query=PREFIX
				+ "select ?tripPreference ?preferredMaxTotalDistance ?preferredTripDuration ?preferredTripTime ?preferredCity ?preferredCountry ?preferredWeatherCondition ?preferredMinTimeOfAccompany ?modality "
				+ " from <" + getGraphName(uid) + "> "
				+ " where {"
					+" ?s profile:userID \""+uid+"\" . "
					+ "?s frap:holds ?tripPreference. "
					+ "?tripPreference frap:about ?about . "
					+ "?about frap:filter ?filter . "
					+ "Optional {?filter profile:hasPreferredMaxTotalDistance ?preferredMaxTotalDistance .}"
					+ "Optional {?filter profile:hasPreferredTripDuration ?preferredTripDuration .}"
					+ "Optional {?filter profile:hasPreferredTripTime ?preferredTripTime .}"
					+ "Optional {?filter profile:hasPreferredCity ?preferredCity .}"
					+ "Optional {?filter profile:hasPreferredCountry ?preferredCountry .}"
					+ "Optional {?filter profile:hasPreferredWeatherCondition ?preferredWeatherCondition .}"
					+ "Optional {?filter profile:hasPreferredMinTimeOfAccompany ?preferredMinTimeOfAccompany .}"
					+ "Optional {?filter profile:hasModalityType ?modality .}"
					+ "}";
		return query;
	}
	
	/**
	 * make the query for placePreference
	 * @param uid
	 * @param placePreference
	 * @return
	 */
	private static String makePlacePreferenceQuery(String uid,
			eu.threecixty.profile.oldmodels.PlacePreference placePreference) {
		String uri=placePreference.getHasPlacePreferenceURI();
		String aboutSt=uri+"/aboutblankNode";
		String filterSt=uri+"/filterblankNode";
		String query= " <"+PROFILE_URI+uid+"> frap:holds <"+uri+"> .";
		query+= "  <"+uri+"> rdf:type frap:Preference .";
		
		query+= " <"+placePreference.getHasPlacePreferenceURI()+"> frap:about <"+aboutSt+"> . "
		+ "<"+aboutSt+">  rdf:type frap:Pattern . "
		+ "<"+aboutSt+">  frap:filter <"+filterSt+"> . "
		+ "<"+filterSt+"> rdf:type frap:Filter . ";
		
		if (placePreference.getHasPlaceDetailPreference()!=null){
			if (placePreference.getHasPlaceDetailPreference().getHasNatureOfPlace()!=null) 
				query+= "<"+filterSt+"> profile:hasNatureOfPlace \""+ placePreference.getHasPlaceDetailPreference().getHasNatureOfPlace()+"\" . ";
		}
		return query;
	}
	/**
	 * insert place preference of the user in the kb
	 * @param uid
	 * @param placePreference
	 * @return
	 */
	public static String setPlacePreferences(String uid, eu.threecixty.profile.oldmodels.PlacePreference placePreference ){
		String query=PREFIX
			+ "   INSERT DATA { GRAPH <"+ getGraphName(uid)+">"
			+ " { ";
		if (placePreference.getHasPlacePreferenceURI()==null ||placePreference.getHasPlacePreferenceURI().isEmpty())
			placePreference.setHasPlacePreferenceURI(PROFILE_URI+uid+"/PlacePreference/"+UUID.randomUUID().toString());
		
		query+= makePlacePreferenceQuery(uid, placePreference);

		query+= "}}";
		return query;
	}
	
	private static String makeRemovePlacePreferenceQuery() {
		
		String query=  "?s frap:holds ?placePreference. "
				+ "?placePreference frap:about ?about . "
				+ "?about frap:filter ?filter . "
				+ "?filter profile:hasNatureOfPlace ?natureOfPlace .";
		return query;
	}
	/**
	 * remove place preference of the user in the kb
	 * @param uid
	 * @param placePreference
	 * @return
	 */
	public static String removePlacePreferences(String uid){
		String query=PREFIX
			+ "   DELETE { GRAPH <"+ getGraphName(uid)+">"
			+ " { ";
			query+= makeRemovePlacePreferenceQuery();
		query+= "}} where { GRAPH <" + getGraphName(uid) + "> {"
				+" ?s profile:userID \""+uid+"\". "
				+" ?s frap:holds ?placePreference. "
				+ "?placePreference frap:about ?about . "
				+ "?about frap:filter ?filter . "
				+ "Optional {?filter profile:hasNatureOfPlace ?natureOfPlace .}";
				query+= "}}";
		return query;
	}

	/**
	 * select place preferences of the user in the kb
	 * @param uid
	 * @return
	 */
	public static String getPlacePreferences(String uid) {
		String query=PREFIX
				+ "select ?placePreference ?natureOfPlace "
				+ " from <" + getGraphName(uid) + "> "
				+ " where {"
					+" ?s profile:userID \""+uid+"\". "
					+ "?s frap:holds ?placePreference. "
					+ "?placePreference frap:about ?about . "
					+ "?about frap:filter ?filter . "
					+ "Optional {?filter profile:hasNatureOfPlace ?natureOfPlace .}"
					+ "}";
		return query;
	}

	/**
	 * Creates query to insert a given profile image to Virtuoso.
	 * @param uid
	 * @param profileImage
	 * @return
	 */
	public static String createQueryToInsertProfileImage(String uid, String profileImage) {
		String query=PREFIX
				+ "INSERT INTO GRAPH <"+ getGraphName(uid) +"> "
				+ "{ ";
					query+= "<" + PROFILE_URI + uid + "> foaf:img <" + profileImage + "> ."
				+ "}";
		return query;
	}
	
	/**
	 * Creates a query to delete the profile image of a given UID in Virtuoso. 
	 * @param uid
	 * @return
	 */
	public static String createQueryToDeleteProfileImage(String uid) {
		String query = " DELETE WHERE { GRAPH <" + getGraphName(uid) + ">" 
				+ "{ <" + PROFILE_URI + uid + "> foaf:img ?o  } \n}";
		return query;
	}
	
	/**
	 * Creates a query to get the profile image of a given UID.
	 * @param uid
	 * @return
	 */
	public static String createQueryToGetProfileImage(String uid) {
		String query=PREFIX
				+ "select ?profileImage from <" + getGraphName(uid) + ">"
				+ " where "
				+ "{ <" + PROFILE_URI + uid + "> foaf:img ?profileImage  } ";
		return query;
	}

	/**
	 * Delegate method to get user's private graph.
	 * @param uid
	 * 				User ID
	 * @return Private graph name
	 */
	private static String getGraphName(String uid) {
		return VirtuosoManager.getInstance().getGraph(uid);
	}
}

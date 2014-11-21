package eu.threecixty.profile;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import eu.threecixty.Configuration;

public class GetSetQueryStrings {
	private static final String PROFILE_URI = "http://data.linkedevents.org/person/";
	
	private static final String PREFIX = Configuration.PREFIXES;
	
	private static String makeUser(String uid) {
		String query= " <"+PROFILE_URI+uid+"> rdf:type foaf:Person. "
				+" <"+ PROFILE_URI+uid+"> profile:userID \""+uid+"\" . ";
		return query;
	}
	/**
	 * insert User in the KB
	 * @param uid
	 * @return
	 */
	public static String setUser(String uid){
		String query=PREFIX
			+ " INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH +"> "
			+ " { ";
				query+= makeUser(uid);
			query+= "}";
			return query;
	}
	/**
	 * insert Multiple User in the KB
	 * @param uids
	 * @return
	 */
	public static String setMultipleUser(Set<String> uids){
		String query= PREFIX
					+ "INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH +"> "
					+ "{ ";
					Iterator <String> iterators = uids.iterator();
					for ( ; iterators.hasNext(); ){
						String uid=iterators.next();
						query+= makeUser(uid);
					}
			query+= "}";
		return query;
	}
	
	/**
	 * Remove user from the kb
	 * @param uid
	 * @return
	 */
	public static String removeUser(String uid){
		String query=PREFIX
			+ "DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH +"> "
			+ "{ ";
			query+= makeUser(uid);
			query+= "}";
			return query;
	}
	/**
	 * Remove multiple user from the kb
	 * @param uids
	 * @return
	 */
	public static String removeMultipleUser(Set<String> uids){
		String query=PREFIX
					+ "DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH +"> "
					+ "{";
					Iterator <String> iterators = uids.iterator();
					for ( ; iterators.hasNext(); ){
						String uid=iterators.next();
						query+= makeUser(uid);
					}
			query+= "}";
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
					+ "?uri a foaf:Person. "
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
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "?s profile:hasLastCrawlTime ?lastCrawlTime. "
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
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
			+ "INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH +"> "
			+ "{ ";
				if (time==null || time.isEmpty()) time="0";
				query+= "<"+PROFILE_URI+uid+"> profile:hasLastCrawlTime \""+time+"\" ."
			+ "}";
			return query;
	}
	/**
	 * remove last crawl time from the KB
	 * @param uid
	 * @param time
	 * @return
	 */
	public static String removeLastCrawlTime(String uid, String time ){
		String query=PREFIX
			+ "DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ "{ "
				+ "<"+PROFILE_URI+uid+"> profile:hasLastCrawlTime \""+time+"\" ."
			+ "}";
			return query;
	}
		
	/**
	 * select gender
	 * @param uid
	 * @return
	 */
	public static String getGender(String uid) {
		String query=PREFIX
				+ "select ?gender "
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "?s schema:gender ?gender. "
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
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
			+ "INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH +"> "
			+ "{ ";
			if (gender==null || gender.isEmpty())
				gender="unknown";
			query+= "<"+PROFILE_URI+uid+"> schema:gender \""+gender+"\" ."
			+ "}";
			return query;
	}
	/**
	 * remove gender from the KB
	 * @param uid
	 * @param time
	 * @return
	 */
	public static String removeGender(String uid, String gender ){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ "  { "
			+ "<"+PROFILE_URI+uid+"> schema:gender \""+gender+"\" ."
			+ "}";
			return query;
	}
	
	/**
	 * Select name of the user from KB
	 * @param uid
	 * @return
	 */
	public static String getName(String uid) {
		String query=PREFIX
				+ "select ?givenname ?familyname "
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "?s schema:givenName ?givenname."
					+ "?s schema:familyName ?familyname."
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
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
	/**
	 * insert name object of the User in the KB 
	 * @param uid
	 * @param name
	 * @return
	 */
	public static String setName(String uid, eu.threecixty.profile.oldmodels.Name name ){
		String query=PREFIX
			+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH +">"
			+ "  { ";
			query+= makeNameQuery(uid, name);
			query+= "}";
			return query;
	}
	
	/**
	 * remove name object of the user from the KB
	 * @param uid
	 * @param name
	 * @return
	 */
	public static String removeName(String uid, eu.threecixty.profile.oldmodels.Name name ){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ "  { ";
			query+= makeNameQuery(uid, name);
			query+= "}";
			return query;
	}
	
	/**
	 * Select address of the user from the KB
	 * @param uid
	 * @return
	 */
	public static String getAddress(String uid) {
		String query=PREFIX
				+ "select ?address ?townname ?countryname ?staddress ?pcode ?homeLocation ?geoLocation ?longitude ?lat "
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "OPTIONAL {?s schema:address ?address. }"
					+ "OPTIONAL {?address schema:postalCode ?pcode.}"
					+ "OPTIONAL {?address schema:streetAddress ?staddress.}"
					+ "OPTIONAL {?address schema:addressLocality ?townname.}"
					+ "OPTIONAL {?address schema:addressCountry ?countryname.}"
					+ "OPTIONAL {?s schema:homeLocation ?homeLocation.}"
					+ "OPTIONAL {?homeLocation schema:geo ?geoLocation.}"
					+ "OPTIONAL {?geoLocation schema:latitude ?lat.}"
					+ "OPTIONAL {?geoLocation schema:longitude ?longitude. }"
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
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
			query+="<"+idgeo+"> rdf:type schema:GeoCoordinates";
			if (address.getLongitute()!=0)	
				query+= "  <"+idgeo+"> schema:longitude \""+address.getLongitute()+"\" .";
			if (address.getLatitude()!=0)
				query+= "  <"+idgeo+"> schema:latitude \""+address.getLatitude()+"\" .";
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
			+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ "  {";
		if (address.getHasAddressURI()!=null&&!address.getHasAddressURI().isEmpty()){
			query+= makeAddressQuery(uid,address);
		}
		query+= "}";
		return query;
	}
	
	/**
	 * remove Address of the user from the KB
	 * @param uid
	 * @param address
	 * @return
	 */
	public static String removeAddress(String uid, eu.threecixty.profile.oldmodels.Address address){
		String query=PREFIX
				+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ "  {";
			if (address.getHasAddressURI()!=null&&!address.getHasAddressURI().isEmpty()){
				query+= makeAddressQuery(uid,address);
			}
			query+= "}";
			return query;
		}
	
	/**
	 * insert specific knows of the user in the kb
	 * @param uid
	 * @param uidKnows
	 * @return
	 */
	public static String setKnows(String uid, String uidKnows){
		String query=PREFIX
				+ " INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { "
				+ "  <"+PROFILE_URI+uid+"> schema:knows <"+PROFILE_URI+uidKnows+"> ."
				+ "}";
				return query;
	}
	/**
	 * insert multiple knows of the user in the kb
	 * @param uid
	 * @param knows
	 * @return
	 */
	public static String setMultipleKnows(String uid, Set <String> knows){
		String query=PREFIX
				+ " INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <String> iterators = knows.iterator();
		for ( ; iterators.hasNext(); ){
			String uidKnows=iterators.next();
			query+= "  <"+PROFILE_URI+uid+"> schema:knows <"+PROFILE_URI+uidKnows+"> .";
		}
		query+= "}";
		return query;
	}
	/**
	 * remove specific know of the user from the kb
	 * @param uid
	 * @param uidKnows
	 * @return
	 */
	public static String removeKnows(String uid, String uidKnows){
		String query=PREFIX
				+ " DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { "
				+ "  <"+PROFILE_URI+uid+"> schema:knows <"+PROFILE_URI+uidKnows+"> ."
				+ "}";
				return query;
	}
	/**
	 * remove multiple knows of the user from the kb
	 * @param uid
	 * @param knows
	 * @return
	 */
	public static String removeMultipleKnows(String uid, Set <String> knows){
		String query=PREFIX
				+ " DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <String> iterators = knows.iterator();
		for ( ; iterators.hasNext(); ){
			String uidKnows=iterators.next();
			query+= "  <"+PROFILE_URI+uid+"> schema:knows <"+PROFILE_URI+uidKnows+"> .";
		}
		query+= "}";
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
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "OPTIONAL {"
						+ "?s schema:knows ?knows. "
						+ "?knows  profile:userID ?uidknows.  "
					+ "}"
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}
	
	private static String makeProfileItentitiesQuery(String uid,
			eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentity) {
		String query= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> rdf:type foaf:OnLineAccount."
				+ "  <"+PROFILE_URI+uid+"> foaf:account <"+profileIdentity.getHasProfileIdentitiesURI()+"> .";
		if  (profileIdentity.getHasSource()!=null&&!profileIdentity.getHasSource().isEmpty())
			query+= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> foaf:accountServiceHomepage <"+profileIdentity.getHasSource()+"> .";
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
			+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		if  (profileIdentity.getHasProfileIdentitiesURI()==null||profileIdentity.getHasProfileIdentitiesURI().isEmpty())
			profileIdentity.setHasProfileIdentitiesURI(PROFILE_URI+uid+"/Account/"+profileIdentity.getHasSourceCarrier());
		query+= makeProfileItentitiesQuery(uid, profileIdentity);
		query+= "}";
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
				+ " INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.ProfileIdentities> iterators = profileIdentities.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentity=iterators.next();
			if  (profileIdentity.getHasProfileIdentitiesURI()==null||profileIdentity.getHasProfileIdentitiesURI().isEmpty())
				profileIdentity.setHasProfileIdentitiesURI(PROFILE_URI+uid+"/Account/"+profileIdentity.getHasSourceCarrier());
			query+= makeProfileItentitiesQuery(uid, profileIdentity);			
		}
		query+= "}";
		return query;
	}	
	/**
	 * remove profile Identity of a user in the KB
	 * @param uid
	 * @param profileIdentities
	 * @return
	 */
	public static String removeProfileIdentities(String uid, eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentity){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		
		if  (profileIdentity.getHasProfileIdentitiesURI()!=null&&!profileIdentity.getHasProfileIdentitiesURI().isEmpty()){
			query+= makeProfileItentitiesQuery(uid, profileIdentity);
		}
		query+= "}";
		return query;
	}
	/**
	 * remove multiple profile Identities of a user in the KB
	 * @param uid
	 * @param profileIdentities
	 * @return
	 */
	public static String removeMultipleProfileIdentities(String uid, Set <eu.threecixty.profile.oldmodels.ProfileIdentities> profileIdentities){
		String query=PREFIX
				+ "  DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.ProfileIdentities> iterators = profileIdentities.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentity=iterators.next();
			if  (profileIdentity.getHasProfileIdentitiesURI()!=null&&!profileIdentity.getHasProfileIdentitiesURI().isEmpty()){
				query+= makeProfileItentitiesQuery(uid, profileIdentity);
			}
		}
		query+= "}";
		return query;
	}	
	/**
	 * select profile Identities of a user from the KB
	 * @param uid
	 * @return
	 */
	public static String getProfileIdentities(String uid) {
		String query=PREFIX
				+ "select ?pi ?source ?piID ?uIM "
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "?s foaf:account ?pi. "
					+ "?pi foaf:accountServiceHomepage ?source."
					+ "?pi foaf:accountName ?piID."
					+ "?pi profile:userInteractionMode ?uIM."
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}
	
	/**
	 * insert preference of a user in the KB. This inserts only the preference uri not the preference object
	 * @param uid
	 * @param preferenceURI
	 * @return
	 *//*
	public static String setPreferences(String uid, String preferenceURI){
		String query=PREFIX
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
			if (preferenceURI!=null&&!preferenceURI.isEmpty()){
				query+= "  <"+preferenceURI+"> rdf:type frap:Preference."
				+ "  <"+PROFILE_URI+uid+"> frap:holds <"+preferenceURI+"> .";
			}
			query+= "}";
			return query;
	}
	*//**
	 * remove preference of a user in the KB. This removes only the preference uri not the preference object
	 * @param uid
	 * @param preferenceURI
	 * @return
	 *//*
	public static String removePreferences(String uid,String preferenceURI){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		if (preferenceURI!=null&&!preferenceURI.isEmpty()){
			query+= "  <"+preferenceURI+"> rdf:type frap:Preference."
					+ "  <"+PROFILE_URI+uid+"> frap:holds <"+preferenceURI+"> .";
		}
		query+= "}";
		return query;
	}

	*//**
	 * get preference of a user in the KB. This selects only the preference uri
	 * @param uid
	 * @return
	 *//*
	public static String getPreferences(String uid) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?pref "
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "?s frap:holds ?pref. "
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}*/
	
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
	 * insert user like to the kb.
	 * @param perferenceURI
	 * @param like
	 * @return
	 */
	public static String setLikes(String uid, eu.threecixty.profile.oldmodels.Likes like){
		String query=PREFIX
			+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH +">"
			+ " { ";
		if (like.getHasLikesURI()==null||like.getHasLikesURI().isEmpty())
			like.setHasLikesURI(PROFILE_URI+uid+"/Likes/"+UUID.randomUUID().toString());
	
		query+= makeLikeQuery(uid, like);

		query+= "}";
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
			+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.Likes> iterators = likes.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.Likes like=iterators.next();
			if (like.getHasLikesURI()==null||like.getHasLikesURI().isEmpty())
				like.setHasLikesURI(PROFILE_URI+uid+"/Likes/"+UUID.randomUUID().toString());
			query+= makeLikeQuery(uid, like);
		}
		query+= "}";
		return query;
	}
	/**
	 * remove a user like from the kb
	 * @param perferenceURI
	 * @param like
	 * @return
	 */
	public static String removeLikes(String uid, eu.threecixty.profile.oldmodels.Likes like){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		if (like.getHasLikesURI()!=null&&!like.getHasLikesURI().isEmpty()){
			query+= makeLikeQuery(uid, like);
		}
		query+= "}";
		return query;
	}
	/**
	 * remove multiple user likes from the kb
	 * @param perferenceURI
	 * @param likes
	 * @return
	 */
	public static String removeMultipleLikes(String uid, Set<eu.threecixty.profile.oldmodels.Likes> likes){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.Likes> iterators = likes.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.Likes like=iterators.next();
			if (like.getHasLikesURI()!=null&&!like.getHasLikesURI().isEmpty()){
				query+= makeLikeQuery(uid, like);
			}
		}
		query+= "}";
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
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "?s profile:like ?likes. "
					+ "?likes schema:likeName ?likeName."
					+ "?likes dc:subject ?liketype."
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}
	/**
	 * select user like with specific like type from the kb
	 * @param uid
	 * @param likeType
	 * @return
	 */
	public static String getSpecificLikes(String uid,String likeType) {
		String query=PREFIX
				+ "select ?likes ?likeName ?liketype "
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "?s profile:like ?likes. "
					+ "?likes schema:likeName ?likeName."
					+ "?likes dc:subject ?liketype."
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+" FILTER (STR(?likeType) = \""+likeType+"\") "//Event
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
			+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
			if (transportUri!=null&&!transportUri.isEmpty()){
				query+= makeTransportQuery(uid, transportUri);
			}
			query+= "}";
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
			+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
			Iterator <String> iterators = transportUris.iterator();
			for ( ; iterators.hasNext(); ){
				String transportUri=iterators.next();
				if (transportUri!=null&&!transportUri.isEmpty()){
					query+= makeTransportQuery(uid, transportUri);
				}
			}
			query+= "}";
			return query;
	}
	/**
	 * remove transport of a user in the KB. This removes only the transport uri not the transport object
	 * @param uid
	 * @param transportUri
	 * @return
	 */
	public static String removeTransport(String uid, String transportUri){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
			if (transportUri!=null&&!transportUri.isEmpty()){
				query+= makeTransportQuery(uid, transportUri);
			}
			query+= "}";
			return query;
	}
	/**
	 * remove multiple transport of a user in the KB. This remove only the transport uri not the transport object
	 * @param uid
	 * @param transportUris
	 * @return
	 */
	public static String removeMultipleTransport(String uid, Set<String> transportUris){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
			Iterator <String> iterators = transportUris.iterator();
			for ( ; iterators.hasNext(); ){
				String transportUri=iterators.next();
				if (transportUri!=null&&!transportUri.isEmpty()){
					query+= makeTransportQuery(uid, transportUri);
				}
			}
			query+= "}";
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
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "?s profile:mobility ?transport. "
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
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
	 * insert multiple accompanies in the kb.
	 * @param transportURI
	 * @param accompanys
	 * @return
	 */
	public static String setMultipleAccompanying(String transportURI, Set<eu.threecixty.profile.oldmodels.Accompanying> accompanys){
		String query=PREFIX
			+ "   INSERT INTO  GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.Accompanying> iterators = accompanys.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.Accompanying accompany =iterators.next();
			if (accompany.getHasAccompanyURI()==null||accompany.getHasAccompanyURI().isEmpty())
				accompany.setHasAccompanyURI(transportURI+"/Accompany/"+UUID.randomUUID().toString());
			
			query+= makeAccompanyQuery(transportURI, accompany);
		}
		query+= "}";
		return query;
	}
	
	/**
	 * insert single accompany in the kb. 
	 * @param accompany
	 * @param transportURI
	 * @return
	 */
	public static String setAccompanyingAssociatedToSpecificTransport(eu.threecixty.profile.oldmodels.Accompanying accompany, String transportURI){
		String query=PREFIX
			+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		if (accompany.getHasAccompanyURI()==null||accompany.getHasAccompanyURI().isEmpty())
			accompany.setHasAccompanyURI(transportURI+"/Accompany/"+UUID.randomUUID().toString());
		
		query+= makeAccompanyQuery(transportURI, accompany);

		query+= "}";
		return query;
	}
	/**
	 * insert multiple accompanies in the kb. This function is same as setMultipleAccompanying(,)
	 * @param transportURI
	 * @param accompanys
	 * @return
	 */
	public static String setMultipleAccompanyingAssociatedToSpecificTransport(String transportURI, Set<eu.threecixty.profile.oldmodels.Accompanying> accompanys){
		String query=PREFIX
				+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { ";
			Iterator <eu.threecixty.profile.oldmodels.Accompanying> iterators = accompanys.iterator();
			for ( ; iterators.hasNext(); ){
				eu.threecixty.profile.oldmodels.Accompanying accompany =iterators.next();
				if (accompany.getHasAccompanyURI()==null||accompany.getHasAccompanyURI().isEmpty())
					accompany.setHasAccompanyURI(transportURI+"/Accompany/"+UUID.randomUUID().toString());
				
				query+= makeAccompanyQuery(transportURI, accompany);
				
			}
			query+= "}";
			return query;
	}
	/**
	 * remove multiple accompanies in the kb.
	 * @param transportURI
	 * @param accompanys
	 * @return
	 */
	public static String removeMultipleAccompanying(String transportURI, Set<eu.threecixty.profile.oldmodels.Accompanying> accompanys){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.Accompanying> iterators = accompanys.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.Accompanying accompany =iterators.next();
			if (accompany.getHasAccompanyURI()!=null&&!accompany.getHasAccompanyURI().isEmpty()){
				query+= makeAccompanyQuery(transportURI, accompany);
			}
		}
		query+= "}";
		return query;
	}
	/**
	 * remove single accompany in the kb.
	 * @param accompany
	 * @param transportURI
	 * @return
	 */
	public static String removeAccompanyingAssociatedToSpecificTransport(eu.threecixty.profile.oldmodels.Accompanying accompany, String transportURI){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		if (accompany.getHasAccompanyURI()!=null&&!accompany.getHasAccompanyURI().isEmpty()){
			query+= makeAccompanyQuery(transportURI, accompany);
		}
		query+= "}";
		return query;
	}
	/**
	 * remove multiple accompanies in the kb. This function is same as removeMultipleAccompanying(,)
	 * @param transportUri
	 * @param accompanys
	 * @return
	 */
	public static String removeMultipleAccompanyingAssociatedToSpecificTransport(String transportURI, Set<eu.threecixty.profile.oldmodels.Accompanying> accompanys){
		String query=PREFIX
				+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { ";
			Iterator <eu.threecixty.profile.oldmodels.Accompanying> iterators = accompanys.iterator();
			for ( ; iterators.hasNext(); ){
				eu.threecixty.profile.oldmodels.Accompanying accompany =iterators.next();
				if (accompany.getHasAccompanyURI()!=null&&!accompany.getHasAccompanyURI().isEmpty()){
					query+= makeAccompanyQuery(transportURI, accompany);
				}
			}
			query+= "}";
			return query;
	}
	
	/**
	 * make Get Accompany Query
	 * @return
	 */
	private static String makeGetAccompanyQuery(){
		String query= "?transport profile:accompany ?accompany. "
				+ "Optional {?accompany profile:accompanyUser ?uid2 .}"
				+ "Optional {?accompany profile:score ?score .}"
				+ "Optional {?accompany profile:validity ?validity .}"
				+ "Optional {?accompany profile:time ?acctime .}";
		return query;
	}
	/**
	 * select accompanies associated to the user
	 * @param uid
	 * @return
	 */
	public static String getAccompanying(String uid) {
		String query=PREFIX
				+ "select ?accompany ?uid2 ?score ?validity ?acctime "
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "?s profile:mobility ?transport. ";
					query+=makeGetAccompanyQuery();
					query+=" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
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
				+ " where {";
					query+=makeGetAccompanyQuery();
					query+=" FILTER (STR(?transport) = \""+transportURI+"\") "
					+ "}";
		return query;
	}
	
	/**
	 * insert personal places associated to a specific regular trip
	 * @param regularTripURI
	 * @param personalPlace
	 * @return
	 */
	public static String setPersonalPlacesAssociatedToSpecificRegularTrip(String regularTripURI, eu.threecixty.profile.oldmodels.PersonalPlace personalPlace ){
		String query=PREFIX
			+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		if (personalPlace.getHasPersonalPlaceURI()==null||personalPlace.getHasPersonalPlaceURI().isEmpty())
			personalPlace.setHasPersonalPlaceURI(regularTripURI+"/PersonalPlace/"+UUID.randomUUID().toString());
		
		query+= makePersonalPlaceQuery(regularTripURI, personalPlace);

		query+= "}";
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
	public static String setMultiplePersonalPlacesAssociatedToSpecificRegularTrip(String regularTripURI, Set<eu.threecixty.profile.oldmodels.PersonalPlace> personalPlaces ){
		String query=PREFIX
				+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.PersonalPlace> iterators = personalPlaces.iterator();
		for ( ; iterators.hasNext(); ){	
			eu.threecixty.profile.oldmodels.PersonalPlace personalPlace=iterators.next();
			if (personalPlace.getHasPersonalPlaceURI()==null||personalPlace.getHasPersonalPlaceURI().isEmpty())
				personalPlace.setHasPersonalPlaceURI(regularTripURI+"/PersonalPlace/"+UUID.randomUUID().toString());
				
			query+= makePersonalPlaceQuery(regularTripURI, personalPlace);

		}
		query+= "}";
		return query;
	}
	/**
	 * remove a personal places associated to a specific regular trip
	 * @param regularTripURI
	 * @param personalPlace
	 * @return
	 */
	public static String removePersonalPlacesAssociatedToSpecificRegularTrip(String regularTripURI, eu.threecixty.profile.oldmodels.PersonalPlace personalPlace ){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		if (personalPlace.getHasPersonalPlaceURI()!=null&&!personalPlace.getHasPersonalPlaceURI().isEmpty()){
			query+= makePersonalPlaceQuery(regularTripURI, personalPlace);

		}
		query+= "}";
		return query;
	}
	/**
	 * remove multiple personal places associated to a specific regular trip
	 * @param regularTripURI
	 * @param personalPlaces
	 * @return
	 */
	public static String removeMultiplePersonalPlacesAssociatedToSpecificRegularTrip(String regularTripURI, Set<eu.threecixty.profile.oldmodels.PersonalPlace> personalPlaces ){
		String query=PREFIX
				+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.PersonalPlace> iterators = personalPlaces.iterator();
		for ( ; iterators.hasNext(); ){	
			eu.threecixty.profile.oldmodels.PersonalPlace personalPlace=iterators.next();
			if (personalPlace.getHasPersonalPlaceURI()!=null&&!personalPlace.getHasPersonalPlaceURI().isEmpty()){
				query+= makePersonalPlaceQuery(regularTripURI, personalPlace);
			}
		}
		query+= "}";
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
	 * select personal places associated for a user
	 * @param uid
	 * @return
	 */
	public static String getPersonalPlaces(String uid) {
		String query=PREFIX
				+ "select ?pplace ?externalIDs ?latitude ?longitude ?stayDuration ?accuracy ?stayPercentage ?pcode ?weekDayPattern ?dayHourPattern ?placeType ?placeName "
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "?s profile:mobility ?transport. "
					+ "?transport profile:regularTrip ?regularTrip. "
					+ "?regularTrip profile:personalPlace ?pplace .";
					query+=makeGetPersonalPlacesQuery();
					query+=" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
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
					+ "?regularTrip profile:personalPlace ?pplace .";
					query+=makeGetPersonalPlacesQuery();
					query+=" FILTER (STR(?regularTrip) = \""+regularTripURI+"\") "
					+ "}";
		return query;
	}
	/**
	 * select personal place based on the URI
	 * @param uri
	 * @return
	 */
	public static String getPersonalPlacesFromURI(String uri) {
		String query=PREFIX
				+ "select ?pplace ?externalIDs ?latitude ?longitude ?stayDuration ?accuracy ?stayPercentage ?pcode ?weekDayPattern ?dayHourPattern ?placeType ?placeName "
				+ " where {";
				query+=makeGetPersonalPlacesQuery();
				query+=" FILTER (STR(?s) = \""+uri+"\") "
					+ "}";
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
	 * insert regular trip associated to a specific transport of a user in the kb
	 * @param transportUri
	 * @param regularTrip
	 * @return
	 */
	public static String setRegularTripsAssociatedToSpecificTransport(String transportUri, eu.threecixty.profile.oldmodels.RegularTrip regularTrip){
		String query=PREFIX
				+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { ";

			if (regularTrip.getHasRegularTripURI()!=null&&!regularTrip.getHasRegularTripURI().isEmpty()){
				query+= makeRegularTripQuery(transportUri, regularTrip);
			}
			query+= "}";
			return query;
	}
	
	/**
	 * insert multiple regular trip associated to a specific transport of a user in the kb
	 * @param transportUri
	 * @param regularTrips
	 * @return
	 */
	public static String setMultipleRegularTripsAssociatedToSpecificTransport(String transportUri, Set<eu.threecixty.profile.oldmodels.RegularTrip> regularTrips){
		String query=PREFIX
			+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.RegularTrip> iterators = regularTrips.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.RegularTrip regularTrip= iterators.next();
			if (regularTrip.getHasRegularTripURI()!=null&&!regularTrip.getHasRegularTripURI().isEmpty()){
				query+= makeRegularTripQuery(transportUri, regularTrip);
			}
		}
		query+= "}";
		return query;
	}
	/**
	 * remove regular trip associated to a specific transport of a user in the kb
	 * @param transportUri
	 * @param regularTrip
	 * @return
	 */
	public static String removeRegularTripsAssociatedToSpecificTransport(String transportUri, eu.threecixty.profile.oldmodels.RegularTrip regularTrip){
		String query=PREFIX
				+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { ";

			if (regularTrip.getHasRegularTripURI()!=null&&!regularTrip.getHasRegularTripURI().isEmpty()){
				query+= makeRegularTripQuery(transportUri, regularTrip);
			}
			query+= "}";
			return query;
	}
	/**
	 * remove multiple regular trip associated to a specific transport of a user in the kb
	 * @param transportUri
	 * @param regularTrips
	 * @return
	 */
	public static String removeMultipleRegularTripsAssociatedToSpecificTransport(String transportUri, Set<eu.threecixty.profile.oldmodels.RegularTrip> regularTrips){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.RegularTrip> iterators = regularTrips.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.RegularTrip regularTrip= iterators.next();
			if (regularTrip.getHasRegularTripURI()!=null&&!regularTrip.getHasRegularTripURI().isEmpty()){
				query+= makeRegularTripQuery(transportUri, regularTrip);
			}
		}
		query+= "}";
		return query;
	}
	/**
	 * make get regular trips query
	 * @return
	 */
	private static String makeGetRegularTripsQuery(){
		String query= "?transport profile:regularTrip ?regularTrip. "
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
	 * select regular trips associated to a user in the kb
	 * @param uid
	 * @return
	 */
	public static String getRegularTrips(String uid) {
		String query=PREFIX
				+ " select ?regularTrip ?tripID ?name ?departureTime ?departureTimeSD ?travelTime ?travelTimeSD ?lastChanged ?totalDistance ?totalCount ?modalityType ?weekdayPattern ?dayhourPattern ?weatherPattern "//?pplace "
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "?s profile:mobility ?transport. ";
					query+=makeGetRegularTripsQuery();
					query+=" FILTER (STR(?uid) = \""+uid+"\") "
					+ "}";
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
				query+=makeGetRegularTripsQuery();
				query+=" FILTER (STR(?transport) = \""+transportURI+"\") "
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
			eu.threecixty.profile.oldmodels.TripPreference tripPreference, String type) {
		String about="_:about";
		String filter="_:filter";
		if (type.isEmpty()){
			about="?about";
			filter="?filter";
		}
		String query= "  <"+tripPreference.getHasTripPreferenceURI()+"> rdf:type frap:Preference ."
				+ " <"+tripPreference.getHasTripPreferenceURI()+"> frap:about "+about+" . "
				+ about+" rdf:type frap:Pattern . "
				+ about+"  frap:filter "+filter+" . "
				+ filter+"  rdf:type frap:Filter . ";
		if (tripPreference.getHasPreferredMaxTotalDistance()!=null&&tripPreference.getHasPreferredMaxTotalDistance()>0)
			query+= filter+"  profile:hasPreferredMaxTotalDistance \""+tripPreference.getHasPreferredMaxTotalDistance()+"\"^^<http://www.w3.org/2001/XMLSchema#double> . ";
		if (tripPreference.getHasPreferredTripDuration()!=null&&tripPreference.getHasPreferredTripDuration()>0)
			query+=filter+"  profile:hasPreferredTripDuration \""+tripPreference.getHasPreferredTripDuration()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . ";
		if (tripPreference.getHasPreferredTripTime()!=null&&tripPreference.getHasPreferredTripTime()>0)
			query+= filter+" profile:hasPreferredTripTime \""+tripPreference.getHasPreferredTripTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . ";
		if (tripPreference.getHasPreferredCity()!=null&&!tripPreference.getHasPreferredCity().isEmpty())
			query+= filter+"   profile:hasPreferredCity \""+tripPreference.getHasPreferredCity()+"\" . ";
		if (tripPreference.getHasPreferredCountry()!=null&&!tripPreference.getHasPreferredCountry().isEmpty())
			query+= filter+"   profile:hasPreferredCountry \""+tripPreference.getHasPreferredCountry()+"\" . ";
		if (tripPreference.getHasPreferredWeatherCondition()!=null&&!tripPreference.getHasPreferredWeatherCondition().isEmpty())
			query+= filter+"   profile:hasPreferredWeatherCondition \""+tripPreference.getHasPreferredWeatherCondition()+"\" . ";
		if (tripPreference.getHasPreferredMinTimeOfAccompany()!=null&&tripPreference.getHasPreferredMinTimeOfAccompany()>0)
			query+= filter+"   profile:hasPreferredMinTimeOfAccompany \""+tripPreference.getHasPreferredMinTimeOfAccompany()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . ";
		if (tripPreference.getHasModalityType()!=null)
			query+= filter+"   profile:hasModalityType \""+tripPreference.getHasModalityType().toString()+"\" . ";
		query+= "  <"+PROFILE_URI+uid+"> frap:holds <"+tripPreference.getHasTripPreferenceURI()+"> . ";
		return query;
	}
	/**
	 * insert Trip preferences of the user in the kb
	 * @param preferenceURI
	 * @param tripPreference
	 * @return
	 */
	public static String setTripPreferences(String uid, eu.threecixty.profile.oldmodels.TripPreference tripPreference){
		String query=PREFIX
			+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		
		if (tripPreference.getHasTripPreferenceURI()!=null&&!tripPreference.getHasTripPreferenceURI().isEmpty())
			tripPreference.setHasTripPreferenceURI(PROFILE_URI+uid+"/Preference/TripPreference/"+UUID.randomUUID().toString());
	
		query+= makeTripPreferenceQuery(uid, tripPreference,"I");
		query+= "}";
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
				+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.TripPreference> iterators = tripPreferences.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.TripPreference tripPreference=iterators.next();
			if (tripPreference.getHasTripPreferenceURI()==null ||tripPreference.getHasTripPreferenceURI().isEmpty())
				tripPreference.setHasTripPreferenceURI(PROFILE_URI+uid+"/Preference/TripPreference/"+UUID.randomUUID().toString());
			
			query+= makeTripPreferenceQuery(uid, tripPreference,"I");
			
		}
		query+= "}";
		return query;
	}
	/**
	 * remove Trip preferences of the user in the kb
	 * @param uid
	 * @param tripPreference
	 * @return
	 */
	public static String removeTripPreferences(String uid, eu.threecixty.profile.oldmodels.TripPreference tripPreference){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		if (tripPreference.getHasTripPreferenceURI()!=null&&!tripPreference.getHasTripPreferenceURI().isEmpty()){
			query+= makeTripPreferenceQuery(uid, tripPreference,"");
		}
		query+= "}";
		return query;
	}
	/**
	 * remove multiple Trip preferences of the user in the kb
	 * @param uid
	 * @param tripPreferences
	 * @return
	 */
	public static String removeMultipleTripPreferences(String uid, Set<eu.threecixty.profile.oldmodels.TripPreference> tripPreferences){
		String query=PREFIX
				+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.TripPreference> iterators = tripPreferences.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.TripPreference tripPreference=iterators.next();
			if (tripPreference.getHasTripPreferenceURI()!=null&&!tripPreference.getHasTripPreferenceURI().isEmpty()){
				query+= makeTripPreferenceQuery(uid, tripPreference,"");
			}
		}
		query+= "}";
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
				+ " where {"
					+ "?s a frap:Person. "
					+" ?s profile:userID ?uid. "
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
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
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
			eu.threecixty.profile.oldmodels.PlacePreference placePreference, String type) {
		String query= " <"+PROFILE_URI+uid+"> frap:holds <"+placePreference.getHasPlacePreferenceURI()+"> .";
		query+= "  <"+placePreference.getHasPlacePreferenceURI()+"> rdf:type frap:Preference .";
		String about="_:about";
		String filter="_:filter";
		if (type.isEmpty()){
			about="?about";
			filter="?filter";
		}
		
		query+= " <"+placePreference.getHasPlacePreferenceURI()+"> frap:about "+about+" . "
		+ about+" rdf:type frap:Pattern . "
		+ about+" frap:filter "+filter+" . "
		+ filter+" rdf:type frap:Filter . ";
		
		if (placePreference.getHasPlaceDetailPreference()!=null){
			if (placePreference.getHasPlaceDetailPreference().getHasNatureOfPlace()!=null) 
				query+= filter+" profile:hasNatureOfPlace"+ placePreference.getHasPlaceDetailPreference().getHasNatureOfPlace()+" . ";
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
			+ "   INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		if (placePreference.getHasPlacePreferenceURI()==null ||placePreference.getHasPlacePreferenceURI().isEmpty())
			placePreference.setHasPlacePreferenceURI(PROFILE_URI+uid+"/Preference/PlacePreference/"+UUID.randomUUID().toString());
		
		query+= makePlacePreferenceQuery(uid, placePreference,"I");

		query+= "}";
		return query;
	}
	
	/**
	 * insert multiple place preferences of the user in the kb
	 * @param uid
	 * @param placePreferences
	 * @return
	 */
	public static String setMultiplePlacePreferences(String uid, Set<eu.threecixty.profile.oldmodels.PlacePreference> placePreferences){
		String query=PREFIX
			+ "  INSERT INTO GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.PlacePreference> iterators = placePreferences.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.PlacePreference placePreference=iterators.next();
			if (placePreference.getHasPlacePreferenceURI()==null || placePreference.getHasPlacePreferenceURI().isEmpty())
				placePreference.setHasPlacePreferenceURI(PROFILE_URI+uid+"/Preference/PlacePreference/"+UUID.randomUUID().toString());
			query+= makePlacePreferenceQuery(uid, placePreference,"I");

		}
		query+= "}";
		return query;
	}
	/**
	 * remove place preference of the user in the kb
	 * @param uid
	 * @param placePreference
	 * @return
	 */
	public static String removePlacePreferences(String uid, eu.threecixty.profile.oldmodels.PlacePreference placePreference ){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		if (placePreference.getHasPlacePreferenceURI()!=null){
			query+= makePlacePreferenceQuery(uid, placePreference,"");


		}
		query+= "}";
		return query;
	}
	/**
	 * remove multiple place preferences of the user in the kb
	 * @param preferenceURI
	 * @param placePreferenceURIs
	 * @return
	 */
	public static String removeMultiplePlacePreferences(String uid, Set<eu.threecixty.profile.oldmodels.PlacePreference> placePreferences ){
		String query=PREFIX
			+ "   DELETE FROM GRAPH <"+ VirtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.PlacePreference> iterators = placePreferences.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.PlacePreference placePreference=iterators.next();
			if (placePreference.getHasPlacePreferenceURI()!=null&&!placePreference.getHasPlacePreferenceURI().isEmpty()){
				query+= makePlacePreferenceQuery(uid, placePreference,"");

			}
		}
		query+= "}";
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
				+ " where {"
					+ "?s a foaf:Person. "
					+" ?s profile:userID ?uid. "
					+ "?s frap:holds ?placePreference. "
					+ "?placePreference frap:about ?about . "
					+ "?about frap:filter ?filter . "
					+ "Optional {?filter profile:hasNatureOfPlace ?natureOfPlace .}"
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}

	/**
	 * insert place Details preference of the user in the kb
	 * @param placePreferenceURI
	 * @param placeDetailPreference
	 * @return
	 *//*
	public static String setPlaceDetailPreference(String placePreferenceURI, eu.threecixty.profile.oldmodels.PlaceDetailPreference placeDetailPreference){
		String query=PREFIX
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
			if (placeDetailPreference.getHasPlaceDetailPreferenceURI()==null ||placeDetailPreference.getHasPlaceDetailPreferenceURI().isEmpty())
				placeDetailPreference.setHasPlaceDetailPreferenceURI(placePreferenceURI+"/PlaceDetailPreference/"+UUID.randomUUID().toString());
		
			query+= "  <"+placeDetailPreference.getHasPlaceDetailPreferenceURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
			query+= "  <"+placeDetailPreference.getHasPlaceDetailPreferenceURI()+"> rdf:type profile:PlaceDetailPreference.";
			query+= "  <"+placePreferenceURI+"> profile:hasPlaceDetailPreference <"+placeDetailPreference.getHasPlaceDetailPreferenceURI()+"> .";
			
			if (placeDetailPreference.getHasNatureOfPlace().toString()!=null&&! placeDetailPreference.getHasNatureOfPlace().toString().isEmpty()) 
				query+= "  <"+placeDetailPreference.getHasPlaceDetailPreferenceURI()+"> profile:hasNatureOfPlace \""+placeDetailPreference.getHasNatureOfPlace() +"\" .";
			query+= "}";
			return query;
	}
	*//**
	 * remove place detail preferences of the user in the kb
	 * @param placePreferenceURI
	 * @param placeDetailPreference
	 * @return
	 *//*
	public static String removePlaceDetailPreference(String placePreferenceURI, eu.threecixty.profile.oldmodels.PlaceDetailPreference placeDetailPreference){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
			if (placeDetailPreference.getHasPlaceDetailPreferenceURI()!=null&&!placeDetailPreference.getHasPlaceDetailPreferenceURI().isEmpty()){
				query+= "  <"+placeDetailPreference.getHasPlaceDetailPreferenceURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
				query+= "  <"+placeDetailPreference.getHasPlaceDetailPreferenceURI()+"> rdf:type profile:PlaceDetailPreference.";
				query+= "  <"+placePreferenceURI+"> profile:hasPlaceDetailPreference <"+placeDetailPreference.getHasPlaceDetailPreferenceURI()+"> .";
				
				if (placeDetailPreference.getHasNatureOfPlace().toString()!=null&&! placeDetailPreference.getHasNatureOfPlace().toString().isEmpty()) 
					query+= "  <"+placeDetailPreference.getHasPlaceDetailPreferenceURI()+"> profile:hasNatureOfPlace \""+placeDetailPreference.getHasNatureOfPlace() +"\" .";
			}
			query+= "}";
			return query;
	}
	*//**
	 * select place detail preferences of the user in the kb
	 * @param uid
	 * @return
	 *//*
	public static String getPlaceDetailPreference(String uid) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?PlaceDetailPreference ?natureOfPlace "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "?s profile:hasPreference ?pref. "
					+ "?pref profile:hasPlacePreference ?placePreference. "
					+"?placePreference profile:hasPlaceDetailPreference ?PlaceDetailPreference. "
					+"?PlaceDetailPreference profile:hasNatureOfPlace ?natureOfPlace. "
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}	
	*//**
	 * select place detail preferences associated to a URI 
	 * @param uri
	 * @return
	 *//*
	public static String getPlaceDetailPreferenceFromURI(String uri) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?PlaceDetailPreference ?natureOfPlace "
				+ " where {"
					+"?PlaceDetailPreference profile:hasNatureOfPlace ?natureOfPlace. "
					+" FILTER (STR(?PlaceDetailPreference) = \""+uri+"\") "//100900047095598983805
					+ "}";
		return query;
	}	
*/}

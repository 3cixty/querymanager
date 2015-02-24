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
			+ " DELETE Where { \n"
			+ " GRAPH <"+ getGraphName(uid) +"> { \n";
				query+= makeUser(uid);
			query+= "}\n"
			+ "}";
			return query;
	}
	
	/**
	 * Get the URI of the user
	 * @param uid
	 * @return
	 */
	public static String getUserURI(String uid){
		String query=PREFIX
				+ "select ?uri \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where { \n"
					+" ?uri profile:userID \""+uid+"\". \n"
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
				+ " select ?lastCrawlTime \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where { \n"
					+ " <"+PROFILE_URI+uid+"> profile:lastCrawlTime ?lastCrawlTime. \n"
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
			+ " INSERT DATA { "
				+ "GRAPH <"+ getGraphName(uid) +"> { \n";

					if (time==null || time.isEmpty()) time="0";
					
					query+= "<"+PROFILE_URI+uid+"> profile:lastCrawlTime \""+time+"\" . \n"
				+ " } \n"
				+ " }";
			return query;
	}
	/**
	 * remove last crawl time from the KB
	 * @param uid
	 * @return
	 */
	public static String removeLastCrawlTime(String uid ){
		String query=PREFIX
			+ " DELETE Where { \n"
			+ " GRAPH <"+ getGraphName(uid)+"> { \n"
				+ "<"+PROFILE_URI+uid+"> profile:lastCrawlTime ?o . \n"
			+ " } \n"
			+ " }";
			return query;
	}
		
	
	public static String createQueryToGetGenderNameImageAddressLastcrawl(String uid) {
		String query = PREFIX
				+ " select distinct ?gender ?givenname ?familyname ?profileImage ?address ?townname ?countryname ?staddress ?pcode ?longitude ?lat ?lastCrawlTime \n"
				+ " from <" + getGraphName(uid) + "> \n" 
				+ " where { \n"
					+ " OPTIONAL { <" + PROFILE_URI + uid + "> schema:gender ?gender. } \n"
					+ " OPTIONAL { <" + PROFILE_URI + uid + "> schema:givenName ?givenname. } \n"
					+ " OPTIONAL { <" + PROFILE_URI + uid + "> schema:familyName ?familyname. } \n"
					+ " OPTIONAL { <" + PROFILE_URI + uid + "> foaf:img ?profileImage . } \n"
					
				    + " OPTIONAL { <" + PROFILE_URI + uid + "> schema:address ?address . \n"
					+ "            ?address schema:postalCode ?pcode .} \n"
				    + " OPTIONAL { <" + PROFILE_URI + uid + "> schema:address ?address . \n"
					+ "            ?address schema:streetAddress ?staddress .} \n"
					+ " OPTIONAL { <" + PROFILE_URI + uid + "> schema:address ?address . \n"
					+ "            ?address schema:addressLocality ?townname .} \n"
					+ " OPTIONAL { <" + PROFILE_URI + uid + "> schema:address ?address . \n"
					+ "            ?address schema:addressCountry ?countryname .} \n"
					+ " OPTIONAL { <" + PROFILE_URI + uid + "> schema:homeLocation ?homeLocation . \n"
					+ "            ?homeLocation schema:geo ?geoLocation . \n"
					+ "            ?geoLocation schema:latitude ?lat . \n"
					+ "            ?geoLocation schema:longitude ?longitude .} \n"
					+ " OPTIONAL { <"+PROFILE_URI+uid+"> profile:lastCrawlTime ?lastCrawlTime. } \n"
				+ "} \n";
		return query;
	}
	
	/**
	 * select gender
	 * @param uid
	 * @return
	 */
	public static String getGender(String uid) {
		String query=PREFIX
				+ " select ?gender \n"
				+ " from <" + getGraphName(uid) + "> \n" 
				+ " where { \n"
					+ "<" + PROFILE_URI + uid + "> schema:gender ?gender. \n"
				+ "} \n";
		return query;
	}
	/**
	 * insert gender. if gender=null or "" then insert "unknown"
	 * @param uid
	 * @return
	 */
	public static String setGender(String uid, String gender ){
		String query=PREFIX
			+ "INSERT DATA { \n"
				+ "GRAPH <"+ getGraphName(uid) +"> { \n";
					
					if (gender==null || gender.isEmpty()) gender="unknown";
					
					query+= " <"+PROFILE_URI+uid+"> schema:gender \""+gender+"\" . \n"
				+ "} \n"
			+ "}";
			return query;
	}
	/**
	 * remove gender from the KB
	 * @param uid
	 * @return
	 */
	public static String removeGender(String uid){
		String query=PREFIX
			+ " DELETE  Where { \n"
				+ " GRAPH <"+ getGraphName(uid)+"> { \n"
					+ " <"+PROFILE_URI+uid+"> schema:gender ?o . \n"
				+ " } \n"
			+ "	}";
			return query;
	}
	
	/**
	 * Select name of the user from KB
	 * @param uid
	 * @return
	 */
	public static String getName(String uid) {
		String query=PREFIX
				+ " select ?givenname ?familyname \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where { \n"
				+ " <" + PROFILE_URI + uid + "> schema:givenName ?givenname ;  "
											+ "	schema:familyName ?familyname. \n"
				+ "} ";
		return query;
	}
	
	/**
	 * make insert name of the user
	 * @param uid
	 * @param name
	 * @return
	 */
	private static String makeNameQuery(String uid,
			eu.threecixty.profile.oldmodels.Name name) {
		String query="";
		
		if (name.getGivenName()!=null&&!name.getGivenName().isEmpty())
			query+= " <"+PROFILE_URI+uid+"> schema:givenName \""+name.getGivenName()+"\". \n";
		
		if (name.getFamilyName()!=null&&!name.getFamilyName().isEmpty())
			query+= " <"+PROFILE_URI+uid+"> schema:familyName \""+name.getFamilyName()+"\". \n";
		return query;
	}
	
	/**
	 * make remove name of the user
	 * @param uid
	 * @return
	 */
	private static String makeRemoveNameQuery(String uid) {
		String query= " <"+PROFILE_URI+uid+"> schema:givenName ?givenName . \n"
					+ " <"+PROFILE_URI+uid+"> schema:familyName ?familyName . \n";
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
			+ " INSERT DATA { \n"
			+ " GRAPH <"+ getGraphName(uid) +"> { \n";
				query+= makeNameQuery(uid, name);
			query+= "} \n"
			+ "}";
			return query;
	}
	
	/**
	 * remove name object of the user from the KB
	 * @param uid
	 * @return
	 */
	public static String removeName(String uid){
		String query=PREFIX
			+ " DELETE Where { \n "
			+ " GRAPH <"+ getGraphName(uid)+"> { ";
				query+= makeRemoveNameQuery(uid);
			query+= "} \n"
			+ "}";
			return query;
	}
	
	/**
	 * Select address of the user from the KB
	 * @param uid
	 * @return
	 */
	public static String getAddress(String uid) {
		String query=PREFIX
				+ " select ?address ?townname ?countryname ?staddress ?pcode ?homeLocation ?geoLocation ?longitude ?lat \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where { \n"
				    + "OPTIONAL { <" + PROFILE_URI + uid + "> schema:address ?address .} \n"
					+ "OPTIONAL {?address schema:postalCode ?pcode .} \n"
					+ "OPTIONAL {?address schema:streetAddress ?staddress .} \n"
					+ "OPTIONAL {?address schema:addressLocality ?townname .} \n"
					+ "OPTIONAL {?address schema:addressCountry ?countryname .} \n"
					+ "OPTIONAL { <" + PROFILE_URI + uid + "> schema:homeLocation ?homeLocation .} \n"
					+ "OPTIONAL {?homeLocation schema:geo ?geoLocation .} \n"
					+ "OPTIONAL {?geoLocation schema:latitude ?lat .} \n"
					+ "OPTIONAL {?geoLocation schema:longitude ?longitude .} \n"
					+ "}";
		return query;
	}
	/**
	 * make address query
	 * @param uid
	 * @param address
	 * @return
	 */
	private static String makeAddressQuery(String uid,
			eu.threecixty.profile.oldmodels.Address address) {
		String query= //" <"+address.getHasAddressURI()+"> rdf:type schema:PostalAddress. \n"
					 " <"+PROFILE_URI+uid+"> schema:address <"+address.getHasAddressURI()+"> . \n";
		
		if (address.getCountryName()!=null&&!address.getCountryName().isEmpty())
			query+= " <"+address.getHasAddressURI()+"> schema:addressCountry \""+address.getCountryName()+"\". \n";
		
		if (address.getTownName()!=null&&!address.getTownName().isEmpty())
			query+= " <"+address.getHasAddressURI()+"> schema:addressLocality \""+address.getTownName()+"\". \n";
		
		if (address.getStreetAddress()!=null&&!address.getStreetAddress().isEmpty())
			query+= " <"+address.getHasAddressURI()+"> schema:streetAddress \""+address.getStreetAddress()+"\". \n";
		
		if (address.getPostalCode()!=null&&!address.getPostalCode().isEmpty())
			query+= " <"+address.getHasAddressURI()+"> schema:postalCode \""+address.getPostalCode()+"\". \n";
		
		if (address.getLongitute()!=0 || address.getLatitude()!=0){
			String id=address.getHasAddressURI()+"/HomeLocation";
			
			query+=" <"+PROFILE_URI+uid+"> schema:homeLocation <"+id+"> . \n";
			//query+=" <"+id+"> rdf:type schema:Place . \n";
			
			String idgeo=id+"/GeoCoordinates";
			
			query+=" <"+id+"> schema:geo <"+idgeo+"> . \n";
			//query+=" <"+idgeo+"> rdf:type schema:GeoCoordinates . \n";
			
			if (address.getLongitute()!=0)	
				query+= " <"+idgeo+"> schema:longitude " + address.getLongitute()+" . \n";
			
			if (address.getLatitude()!=0)
				query+= " <"+idgeo+"> schema:latitude "+address.getLatitude()+" . \n";
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
			+ " INSERT DATA { \n"
			+ " GRAPH <"+ getGraphName(uid)+"> { \n";
		
				if (address.getHasAddressURI()!=null&&!address.getHasAddressURI().isEmpty()) 
					query+= makeAddressQuery(uid,address);
				
		query+= "} \n"
		+ "}";
		return query;
	}
	
	/**
	 * remove Address of the user from the KB
	 * @param uid
	 * @return
	 */
	public static String removeAddress(String uid){
		String query=PREFIX
				+ " DELETE Where { \n"
				+ " GRAPH <"+ getGraphName(uid)+"> { \n"
                    + " ?address ?p ?o . \n"
                    + " <"+PROFILE_URI+uid+"> schema:address ?address. \n"
				+ " } \n"
				+ " }";
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
				+ " INSERT DATA { \n"
				+ " GRAPH <"+ getGraphName(uid)+"> { \n";
					Iterator <String> iterators = knows.iterator();
					
					for ( ; iterators.hasNext(); ){
						String uidKnows=iterators.next();
						query+= " <"+PROFILE_URI+uid+"> schema:knows <" + (uidKnows.contains(PROFILE_URI) ? uidKnows : PROFILE_URI+uidKnows) + "> . \n";
					}
				query+= " } \n"
				+ "}";
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
				+ " DELETE Where { \n"
				+ " GRAPH <"+ getGraphName(uid)+"> { \n"
					+ " <"+PROFILE_URI+uid+"> schema:knows <"+PROFILE_URI+uidKnows+"> . \n"
					+ " } \n"
				+ " }";
				return query;
	}
	/**
	 * remove All knows of the user from the kb
	 * @param uid
	 * @return
	 */
	public static String removeAllKnows(String uid){
		String query=PREFIX
				+ " DELETE Where { \n"
				+ " GRAPH <" + getGraphName(uid) + "> { \n"
						+ " <"+PROFILE_URI+uid+"> schema:knows ?o. \n"
						+ "} \n"
				+ "}";
		return query;
	}

	/**
	 * select knows of the user from the kb
	 * @param uid
	 * @return
	 */
	public static String getKnows(String uid) {
		String query=PREFIX
				+ " select ?uidknows \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where { \n"
					+ "<" + PROFILE_URI + uid + "> schema:knows ?uidknows. \n"
				+ "} ";
		return query;
	}
	/**
	 * make profile Identity query
	 * @param uid
	 * @param profileIdentity
	 * @return
	 */
	private static String makeProfileItentitiesQuery(String uid,
			eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentity) {
		String query= //" <"+profileIdentity.getHasProfileIdentitiesURI()+"> rdf:type foaf:OnlineAccount. \n"
					 " <"+PROFILE_URI+uid+"> foaf:account <"+profileIdentity.getHasProfileIdentitiesURI()+"> . \n";
		
		if  (profileIdentity.getHasUserAccountID()!=null&&!profileIdentity.getHasUserAccountID().isEmpty())
			query+= " <"+profileIdentity.getHasProfileIdentitiesURI()+"> foaf:accountName \""+profileIdentity.getHasUserAccountID()+"\" . \n";
		
		if  (profileIdentity.getHasUserInteractionMode()!=null&&!profileIdentity.getHasUserInteractionMode().toString().isEmpty())
			query+= " <"+profileIdentity.getHasProfileIdentitiesURI()+"> profile:userInteractionMode \""+profileIdentity.getHasUserInteractionMode()+"\" . \n";
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
			+ " INSERT DATA { \n"
			+ " GRAPH <"+ getGraphName(uid)+"> { \n";
		
				if  (profileIdentity.getHasProfileIdentitiesURI()==null||profileIdentity.getHasProfileIdentitiesURI().isEmpty())
					profileIdentity.setHasProfileIdentitiesURI(PROFILE_URI+uid+"/Account/"+profileIdentity.getHasSourceCarrier());
				
				query+= makeProfileItentitiesQuery(uid, profileIdentity);
				
				query+= "} \n"
			+ "}";
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
				+ " INSERT DATA { \n"
				+ " GRAPH <"+ getGraphName(uid)+"> { \n";
					Iterator <eu.threecixty.profile.oldmodels.ProfileIdentities> iterators = profileIdentities.iterator();
					for ( ; iterators.hasNext(); ){
						eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentity=iterators.next();
						
						if  (profileIdentity.getHasProfileIdentitiesURI()==null||profileIdentity.getHasProfileIdentitiesURI().isEmpty())
							profileIdentity.setHasProfileIdentitiesURI(PROFILE_URI+uid+"/Account/"+profileIdentity.getHasSourceCarrier());
						
						query+= makeProfileItentitiesQuery(uid, profileIdentity);			
					}
					query+= " }\n"
				+ "}";
		return query;
	}	
	
	/**
	 * remove multiple profile Identities of a user in the KB
	 * @param uid
	 * @return
	 */
	public static String removeAllProfileIdentitiesOfUser(String uid){
		String query=PREFIX
				+ " DELETE WHERE { "
				+ " GRAPH <"+ getGraphName(uid)+"> { \n"
					+ " ?pi ?p ?o . \n"
					+ " <"+PROFILE_URI+uid+"> foaf:account ?pi . \n"
				+ "} \n"
			+ "}";
		return query;
	}	
	/**
	 * select profile Identities of a user from the KB
	 * @param uid
	 * @return
	 */
	public static String getProfileIdentities(String uid) {
		String query=PREFIX
				+ "select ?pi ?piID ?uIM \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where { \n"
					+ " <"+PROFILE_URI+uid+"> foaf:account ?pi. \n"
					+ " ?pi foaf:accountName ?piID. \n"
					+ " ?pi profile:userInteractionMode ?uIM. \n"
				+ " }";
		return query;
	}
	/**
	 * make like query
	 * @param uid
	 * @param like
	 * @return
	 */
	private static String makeLikeQuery(String uid,
			eu.threecixty.profile.oldmodels.Likes like) {
		String query=//" <"+like.getHasLikesURI()+"> rdf:type profile:Like. \n"
					" <"+PROFILE_URI+uid+"> profile:like <"+like.getHasLikesURI()+"> . \n";
		
		if (like.getHasLikeName()!=null&&!like.getHasLikeName().isEmpty())
			query+= "  <"+like.getHasLikesURI()+"> schema:likeName \""+like.getHasLikeName()+"\" . \n";
		
		if (like.getHasLikeType()!=null&&!like.getHasLikeType().toString().isEmpty())
			query+= "  <"+like.getHasLikesURI()+"> dc:subject \""+like.getHasLikeType()+"\" . \n";
		
		return query;
	}
	
	/**
	 * insert multiple likes of the user in the kb
	 * @param uid
	 * @param likes
	 * @return
	 */
	public static String setMultipleLikes(String uid, Set<eu.threecixty.profile.oldmodels.Likes> likes){
		String query=PREFIX
			+ " INSERT DATA { \n"
			+ " GRAPH <"+ getGraphName(uid)+"> { \n";
				Iterator <eu.threecixty.profile.oldmodels.Likes> iterators = likes.iterator();
				for ( ; iterators.hasNext(); ){
					eu.threecixty.profile.oldmodels.Likes like=iterators.next();
					
					if (like.getHasLikesURI()==null||like.getHasLikesURI().isEmpty())
						like.setHasLikesURI(PROFILE_URI+uid+"/Likes/"+UUID.randomUUID().toString());
					
					query+= makeLikeQuery(uid, like);
				}
			query+= "}\n"
			+ "}";
		return query;
	}
	
	/**
	 * remove multiple user likes from the kb
	 * @param uid
	 * @return
	 */
	public static String removeAllLikesOfUser(String uid){
		String query=PREFIX
			+ " DELETE Where { "
			+ "	GRAPH <" + getGraphName(uid) + "> { \n"
                    + " ?likes ?p ?o . \n"
                    + " <"+PROFILE_URI+uid+"> profile:like ?likes . \n"
				+ " }\n"
			+ "}";
		return query;
	}
	/**
	 * select user likes from the kb 
	 * @param uid
	 * @return
	 */
	public static String getLikes(String uid) {
		String query=PREFIX
				+ " select ?likes ?likeName ?liketype \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where { \n"
					+ " <"+PROFILE_URI+uid+"> profile:like ?likes. \n"
					+ " ?likes schema:likeName ?likeName. \n"
					+ " ?likes dc:subject ?liketype. \n"
					+ "}";
		return query;
	}
	/**
	 * make transport query
	 * @param uid
	 * @param transportUri
	 * @return
	 */
	private static String makeTransportQuery(String uid, String transportUri) {
		String query =//" <"+transportUri+"> rdf:type profile:Mobility . \n"
					 " <"+PROFILE_URI+uid+"> profile:mobility <"+transportUri+"> . \n";
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
			+ " INSERT DATA { \n"
			+ " GRAPH <"+ getGraphName(uid)+"> { \n";
		
				if (transportUri!=null&&!transportUri.isEmpty())
					query+= makeTransportQuery(uid, transportUri);

				query+= " }\n"
				+ " }";
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
			+ " INSERT DATA { \n"
			+ " GRAPH <"+ getGraphName(uid)+"> { \n";
					Iterator <String> iterators = transportUris.iterator();
					for ( ; iterators.hasNext(); ){
						String transportUri=iterators.next();
						if (transportUri!=null&&!transportUri.isEmpty()){
							query+= makeTransportQuery(uid, transportUri);
						}
					}
				query+= "}\n"
				+ "}";
			return query;
	}

	/**
	 * remove transport of a user in the KB. This removes only the transport uri not the transport object
	 * @param uid
	 * @return
	 */
	public static String removeTransport(String uid){
		String query=PREFIX
			+ " DELETE Where { "
			+ " GRAPH <"+ getGraphName(uid)+"> { \n"
                + " <"+PROFILE_URI+uid+"> profile:mobility ?o . \n"
			+ " } \n"
			+ "} ";
			return query;
	}
	
	/**
	 * select transport of a user in the KB. This selects only the transport uri not the transport object
	 * @param uid
	 * @return
	 */
	public static String getTransport(String uid) {
		String query=PREFIX
				+ " select ?transport \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where { \n"
					+ " <"+PROFILE_URI+uid+"> profile:mobility ?transport. \n"
				+ "}";
		return query;
	}
	/**
	 * make accompany query
	 * @param transportURI
	 * @param accompany
	 * @return
	 */
	private static String makeAccompanyQuery(String transportURI,
			eu.threecixty.profile.oldmodels.Accompanying accompany) {
		String query= " <"+transportURI+"> profile:accompany <"+accompany.getHasAccompanyURI()+"> . \n";
		
		if (accompany.getHasAccompanyUserid2ST()!=null&&! accompany.getHasAccompanyUserid2ST().isEmpty())
			query+= " <"+accompany.getHasAccompanyURI()+"> profile:accompanyUser \""+accompany.getHasAccompanyUserid2ST()+"\" . \n";
		
		if (accompany.getHasAccompanyScore()!=null&&accompany.getHasAccompanyScore()>0)
			query+= " <"+accompany.getHasAccompanyURI()+"> profile:score \""+accompany.getHasAccompanyScore()+"\"^^<http://www.w3.org/2001/XMLSchema#double> . \n";
		
		if (accompany.getHasAccompanyValidity()!=null&&accompany.getHasAccompanyValidity()>0)
			query+= " <"+accompany.getHasAccompanyURI()+"> profile:validity \""+accompany.getHasAccompanyValidity()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . \n";
		
		if (accompany.getHasAccompanyTime()!=null&&accompany.getHasAccompanyTime()>0)
			query+= " <"+accompany.getHasAccompanyURI()+"> profile:time \""+accompany.getHasAccompanyTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . \n";
		
		//query+="  <"+accompany.getHasAccompanyURI()+"> rdf:type profile:Accompany. \n";
		return query;
	}
	
	/**
	 * insert multiple accompanies in the kb. This function is same as setMultipleAccompanying(,)
	 * @param uid
	 * @param transportURI
	 * @param accompanys
	 * @return
	 */
	public static String setMultipleAccompanyingAssociatedToSpecificTransport(String uid, String transportURI, Set<eu.threecixty.profile.oldmodels.Accompanying> accompanys){
		String query=PREFIX
				+ " INSERT DATA { "
				+ " GRAPH <"+ getGraphName(uid)+"> { \n";
					Iterator <eu.threecixty.profile.oldmodels.Accompanying> iterators = accompanys.iterator();
					for ( ; iterators.hasNext(); ){
						eu.threecixty.profile.oldmodels.Accompanying accompany =iterators.next();
						
						if (accompany.getHasAccompanyURI()==null||accompany.getHasAccompanyURI().isEmpty())
							accompany.setHasAccompanyURI(transportURI+"/Accompany/"+UUID.randomUUID().toString());
						
						query+= makeAccompanyQuery(transportURI, accompany);
						
					}
					query+= "}\n"
				+ "}";
			return query;
	}

	/**
	 * remove multiple accompanies in the kb. This function is same as removeMultipleAccompanying(,)
	 * @param uid
	 * @param transportURI
	 * @return
	 */
	public static String removeMultipleAccompanyingAssociatedToSpecificTransport(String uid, String transportURI){
		String query=PREFIX
				+ " DELETE Where { \n"
				+ " GRAPH <"+ getGraphName(uid)+"> { \n"
                    + " ?accompany ?p ?o . \n"
                    + " <"+transportURI+"> profile:accompany ?accompany. \n"
				+ " }\n"
				+ " } ";
			return query;
	}

	/**
	 * select accompanies of a user associated to a given transport
	 * @param uid
	 * @param transportURI
	 * @return
	 */
	public static String getAccompanyingForTransport(String uid, String transportURI) {
		String query=PREFIX
				+ " select ?accompany ?uid2 ?score ?validity ?acctime \n"
				+ " from <" + getGraphName(uid) + "> \n"
					+ " where { \n"
					+ " <"+transportURI+"> profile:accompany ?accompany. \n"
					+ " Optional {?accompany profile:accompanyUser ?uid2 .} \n"
					+ " Optional {?accompany profile:score ?score .} \n"
					+ " Optional {?accompany profile:validity ?validity .} \n"
					+ " Optional {?accompany profile:time ?acctime .} \n"
					+ "} ";
		return query;
	}
	
	/**
	 * insert personal places associated to a specific regular trip
	 * @param uid
	 * @param regularTripURI
	 * @param personalPlace
	 * @return
	 */
	public static String setPersonalPlacesAssociatedToSpecificRegularTrip(String uid, String regularTripURI, eu.threecixty.profile.oldmodels.PersonalPlace personalPlace ){
		String query=PREFIX
			+ " INSERT DATA { \n"
			+ " GRAPH <"+ getGraphName(uid)+"> { \n";
				if (personalPlace.getHasPersonalPlaceURI()==null||personalPlace.getHasPersonalPlaceURI().isEmpty())
					personalPlace.setHasPersonalPlaceURI(regularTripURI+"/PersonalPlace/"+UUID.randomUUID().toString());
				
				query+= makePersonalPlaceQuery(regularTripURI, personalPlace);

			query+= "}\n"
			+ "}";
		return query;
	}
	/**
	 * make personal place query
	 * @param regularTripURI
	 * @param personalPlace
	 * @return
	 */
	private static String makePersonalPlaceQuery(String regularTripURI,
			eu.threecixty.profile.oldmodels.PersonalPlace personalPlace) {
		String query= "  <"+regularTripURI+"> profile:personalPlace <"+personalPlace.getHasPersonalPlaceURI()+"> .\n";
		
		if (personalPlace.getHasPersonalPlaceexternalIds()!=null&&!personalPlace.getHasPersonalPlaceexternalIds().isEmpty())
			query+= " <"+personalPlace.getHasPersonalPlaceURI()+"> profile:externalIDs \""+personalPlace.getHasPersonalPlaceexternalIds()+"\" .\n";
		
		if (personalPlace.getLatitude()!=null&&personalPlace.getLatitude()>0)
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:latitude \""+personalPlace.getLatitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .\n";
		
		if (personalPlace.getLongitude()!=null&&personalPlace.getLongitude()>0)
			query+="  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:longitude \""+personalPlace.getLongitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .\n";
		
		if (personalPlace.getHasPersonalPlaceStayDuration()!=null&&personalPlace.getHasPersonalPlaceStayDuration()>0)
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:stayDuration \""+personalPlace.getHasPersonalPlaceStayDuration()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .\n";
		
		if (personalPlace.getHasPersonalPlaceAccuracy()!=null&&personalPlace.getHasPersonalPlaceAccuracy()>0)
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:accuracy \""+personalPlace.getHasPersonalPlaceAccuracy()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .\n";
		
		if (personalPlace.getHasPersonalPlaceStayPercentage()!=null&&personalPlace.getHasPersonalPlaceStayPercentage()>0)
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:stayPercentage \""+personalPlace.getHasPersonalPlaceStayPercentage()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .\n";
		
		if (personalPlace.getPostalcode()!=null&&!personalPlace.getPostalcode().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:postalCode \""+personalPlace.getPostalcode()+"\" .\n";
		
		if (personalPlace.getHasPersonalPlaceWeekdayPattern()!=null&&!personalPlace.getHasPersonalPlaceWeekdayPattern().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:weekDayPattern \""+personalPlace.getHasPersonalPlaceWeekdayPattern()+"\" .\n";
		
		if (personalPlace.getHasPersonalPlaceDayhourPattern()!=null&&!personalPlace.getHasPersonalPlaceDayhourPattern().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:dayHourPattern \""+personalPlace.getHasPersonalPlaceDayhourPattern()+"\" .\n";
		
		if (personalPlace.getHasPersonalPlaceType()!=null&&!personalPlace.getHasPersonalPlaceType().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:type \""+personalPlace.getHasPersonalPlaceType()+"\" .\n";
		
		if (personalPlace.getHasPersonalPlaceName()!=null&&!personalPlace.getHasPersonalPlaceName().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> rdfs:label \""+personalPlace.getHasPersonalPlaceName()+"\" .\n";

		//query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> rdf:type profile:PersonalPlace .\n";
		return query;
	}
	/**
	 * insert multiple personal places associated to a specific regular trip
	 * @param uid
	 * @param regularTripURI
	 * @param personalPlaces
	 * @return
	 */
	public static String setMultiplePersonalPlacesAssociatedToSpecificRegularTrip(String uid, String regularTripURI, Set<eu.threecixty.profile.oldmodels.PersonalPlace> personalPlaces ){
		String query=PREFIX
				+ " INSERT DATA { \n"
				+ " GRAPH <"+ getGraphName(uid)+"> { \n";
				Iterator <eu.threecixty.profile.oldmodels.PersonalPlace> iterators = personalPlaces.iterator();
				for ( ; iterators.hasNext(); ){	
					eu.threecixty.profile.oldmodels.PersonalPlace personalPlace=iterators.next();
					if (personalPlace.getHasPersonalPlaceURI()==null||personalPlace.getHasPersonalPlaceURI().isEmpty())
						personalPlace.setHasPersonalPlaceURI(regularTripURI+"/PersonalPlace/"+UUID.randomUUID().toString());
						
					query+= makePersonalPlaceQuery(regularTripURI, personalPlace);
				}
				query+= "}\n"
				+ "}";
		return query;
	}
		

	/**
	 * remove multiple personal places associated to a specific regular trip
	 * @param uid
	 * @param regularTripURI
	 * @return
	 */
	public static String removeMultiplePersonalPlacesAssociatedToSpecificRegularTrip(String uid, String regularTripURI){
		String query=PREFIX
				+ " DELETE Where { \n"
				+ " GRAPH <"+ getGraphName(uid)+"> { \n"
	                + " ?pplace ?p ?o . \n"
					+ " <"+regularTripURI+"> profile:personalPlace ?pplace .\n"
				+ "}\n"
				+ "}";
			return query;
	}
    
    /**
     * remove multiple personal places associated to a transport
      * @param uid
     * @param transportURI
     * @return
     */
    public static String removeMultiplePersonalPlacesAssociatedToATransport(String uid, String transportURI){
        String query=PREFIX
        + " DELETE { \n"
        + " GRAPH <"+ getGraphName(uid)+"> { \n"
        + " ?pplace ?p ?o . \n"
        + " ?regularTrip profile:personalPlace ?pplace .\n"
        + "}\n"
        + "}\n"
        + "Where { \n"
        + " GRAPH <"+ getGraphName(uid)+"> { \n"
        + " ?pplace ?p ?o . \n"
        + " ?regularTrip profile:personalPlace ?pplace .\n"
        + " <"+ transportURI+"> profile:regularTrip ?regularTrip .\n"
        + "}\n"
        + "}";
        return query;
    }
	
	/**
	 * make Get Personal Places Query
	 * @return
	 */
	private static String makeGetPersonalPlacesQuery(){
		String query= "Optional {?pplace profile:externalIDs ?externalIDs .} \n"
				+ "Optional {?pplace profile:latitude ?latitude .} \n"
				+ "Optional {?pplace profile:longitude ?longitude .} \n"
				+ "Optional {?pplace profile:stayDuration ?stayDuration .} \n"
				+ "Optional {?pplace profile:accuracy ?accuracy .} \n"
				+ "Optional {?pplace profile:stayPercentage ?stayPercentage .} \n"
				+ "Optional {?pplace profile:postalCode ?pcode .} \n"
				+ "Optional {?pplace profile:weekDayPattern ?weekDayPattern .} \n"
				+ "Optional {?pplace profile:dayHourPattern ?dayHourPattern .} \n"
				+ "Optional {?pplace profile:type ?placeType .} \n"
				+ "Optional {?pplace rdfs:label ?placeName .} \n";
		return query;
	}
	
	/**
	 * select personal places associated for a regular trip of the user
	 * @param uid
	 * @param regularTripURI
	 * @return
	 */
	public static String getPersonalPlacesForRegularTrips(String uid, String regularTripURI) {
		String query=PREFIX
				+ "select ?pplace ?externalIDs ?latitude ?longitude ?stayDuration ?accuracy ?stayPercentage ?pcode ?weekDayPattern ?dayHourPattern ?placeType ?placeName \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where { \n"
					+ " <"+regularTripURI+"> profile:personalPlace ?pplace . \n";
					query+=makeGetPersonalPlacesQuery();
					query+= "}\n";
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
		String query= " <"+transportUri+"> profile:regularTrip <"+regularTrip.getHasRegularTripURI()+"> . \n";
		if (regularTrip.getHasRegularTripName()!=null&&!regularTrip.getHasRegularTripName().isEmpty())
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> rdfs:label \""+regularTrip.getHasRegularTripName()+"\" . \n";
		
		if (regularTrip.getHasRegularTripDepartureTime()!=null&&regularTrip.getHasRegularTripDepartureTime()>0)
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:departureTime \""+regularTrip.getHasRegularTripDepartureTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . \n";
		
		if (regularTrip.getHasRegularTripDepartureTimeSD()!=null&&regularTrip.getHasRegularTripDepartureTimeSD()>0)
			query+="  <"+regularTrip.getHasRegularTripURI()+"> profile:departureTimeSD \""+regularTrip.getHasRegularTripDepartureTimeSD()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . \n";
		
		if (regularTrip.getHasRegularTripTravelTime()!=null&&regularTrip.getHasRegularTripTravelTime()>0)
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:travelTime \""+regularTrip.getHasRegularTripTravelTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . \n";
		
		if (regularTrip.getHasRegularTripTravelTimeSD()!=null&&regularTrip.getHasRegularTripTravelTimeSD()>0)
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:travelTimeSD \""+regularTrip.getHasRegularTripTravelTimeSD()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . \n";
		
		if (regularTrip.getHasRegularTripLastChanged()!=null&&regularTrip.getHasRegularTripLastChanged()>0)
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:lastChanged \""+regularTrip.getHasRegularTripLastChanged()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . \n";
		
		if (regularTrip.getHasRegularTripTotalDistance()!=null&&regularTrip.getHasRegularTripTotalDistance()>0)
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:totalDistance \""+regularTrip.getHasRegularTripTotalDistance()+"\"^^<http://www.w3.org/2001/XMLSchema#double> . \n";
		
		if (regularTrip.getHasRegularTripTotalCount()!=null&&regularTrip.getHasRegularTripTotalCount()>0)
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:totalCount \""+regularTrip.getHasRegularTripTotalCount()+"\"^^<http://www.w3.org/2001/XMLSchema#double> . \n";
		
		if (regularTrip.getHasModalityType().toString()!=null&&!regularTrip.getHasModalityType().toString().isEmpty())
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:tripModality \""+regularTrip.getHasModalityType()+"\" . \n";
		
		if (regularTrip.getHasRegularTripWeekdayPattern()!=null&&!regularTrip.getHasRegularTripWeekdayPattern().isEmpty())
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:weekdayPattern \""+regularTrip.getHasRegularTripWeekdayPattern()+"\" . \n";
		
		if (regularTrip.getHasRegularTripDayhourPattern()!=null&&!regularTrip.getHasRegularTripDayhourPattern().isEmpty())
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:dayhourPattern \""+regularTrip.getHasRegularTripDayhourPattern()+"\" . \n";
		
		if (regularTrip.getHasRegularTripWeatherPattern()!=null&&!regularTrip.getHasRegularTripWeatherPattern().isEmpty())
			query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:weatherPattern \""+regularTrip.getHasRegularTripWeatherPattern()+"\" . \n";
		
		//query+= "  <"+regularTrip.getHasRegularTripURI()+"> rdf:type profile:RegularTrip . \n";
		return query;
	}
		
	/**
	 * insert multiple regular trip associated to a specific transport of a user in the kb
	 * @param uid	 
	 * @param transportUri
	 * @param regularTrips
	 * @return
	 */
	public static String setMultipleRegularTripsAssociatedToSpecificTransport(String uid, String transportUri, Set<eu.threecixty.profile.oldmodels.RegularTrip> regularTrips){
		String query=PREFIX
			+ " INSERT DATA { \n"
			+ " GRAPH <"+ getGraphName(uid)+"> { \n";
				Iterator <eu.threecixty.profile.oldmodels.RegularTrip> iterators = regularTrips.iterator();
				for ( ; iterators.hasNext(); ){
					eu.threecixty.profile.oldmodels.RegularTrip regularTrip= iterators.next();
					if (regularTrip.getHasRegularTripURI()!=null&&!regularTrip.getHasRegularTripURI().isEmpty()){
						query+= makeRegularTripQuery(transportUri, regularTrip);
					}
				}
			query+= "}\n"
			+ "}";
		return query;
	}
	
	
	/**
	 * remove multiple regular trip associated to a specific transport of a user in the kb
	 * @param uid
	 * @param transportUri
	 * @return
	 */
	public static String removeMultipleRegularTripsAssociatedToSpecificTransport(String uid, String transportUri){
		String query=PREFIX
			+ " DELETE Where { \n"
			+ " GRAPH <"+ getGraphName(uid)+"> { \n"
	            + " ?regularTrip ?p ?o . \n"
	            + " <"+transportUri+"> profile:regularTrip ?regularTrip . \n"
			+ " }\n"
			+ " }";
		return query;
	}
	/**
	 * make get regular trips query
	 * @param transportUri
	 * @return
	 */
	private static String makeGetRegularTripsQuery(String transportUri){
		String query= " <"+transportUri+"> profile:regularTrip ?regularTrip. \n"
				+ "Optional {?regularTrip profile:id ?tripID .} \n"
				+ "Optional {?regularTrip rdfs:label ?name .} \n"
				+ "Optional {?regularTrip profile:departureTime ?departureTime .} \n"
				+ "Optional {?regularTrip profile:departureTimeSD ?departuretimeSD .} \n"
				+ "Optional {?regularTrip profile:travelTime ?travelTime .} \n"
				+ "Optional {?regularTrip profile:travelTimeSD ?travelTimeSD .} \n"
				+ "Optional {?regularTrip profile:lastChanged ?lastChanged .} \n"
				+ "Optional {?regularTrip profile:totalDistance ?totalDistance .} \n"
				+ "Optional {?regularTrip profile:totalCount ?totalCount .} \n"
				+ "Optional {?regularTrip profile:tripModality ?modalityType .} \n"
				+ "Optional {?regularTrip profile:weekdayPattern ?weekdayPattern .} \n"
				+ "Optional {?regularTrip profile:dayhourPattern ?dayhourPattern .} \n"
				+ "Optional {?regularTrip profile:weatherPattern ?weatherPattern .} \n";
		return query;
	}
	
	/**
	 * select regular trip associated to a specific transport of a user in the kb
	 * @param uid
	 * @param transportURI
	 * @return
	 */
	public static String getRegularTripsForTransport(String uid, String transportURI) {
		String query=PREFIX
				+ " select ?regularTrip ?tripID ?name ?departureTime ?departureTimeSD ?travelTime ?travelTimeSD ?lastChanged ?totalDistance ?totalCount ?modalityType ?weekdayPattern ?dayhourPattern  ?weatherPattern \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where { \n";
					query+=makeGetRegularTripsQuery(transportURI);
				query+= "}";
		return query;
	}
	
	/**
	 * select regular trip associated to a specific transport of a user in the kb
	 * @param uid
	 * @param transportURI
	 * @return
	 */
	public static String getRegularTripsURIForTransport(String uid, String transportURI) {
		String query=PREFIX
				+ " select ?regularTrip \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where { \n"
					+ " <"+transportURI+"> profile:regularTrip ?regularTrip. \n"
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
		String query= " <"+PROFILE_URI+uid+"> frap:holds <"+uri+"> . \n"
					//+ " <"+uri+"> rdf:type frap:Preference . \n"
					+ " <"+uri+"> frap:about <"+aboutSt+"> . \n"
					//+ " <"+aboutSt+">  rdf:type frap:Pattern . \n"
					+ " <"+aboutSt+">  frap:filter <"+filterSt+"> . \n";
					//+ " <"+filterSt+"> rdf:type frap:Filter . \n";
		
		if (tripPreference.getHasPreferredMaxTotalDistance()!=null&&tripPreference.getHasPreferredMaxTotalDistance()>0)
			query+= " <"+filterSt+"> profile:hasPreferredMaxTotalDistance \""+tripPreference.getHasPreferredMaxTotalDistance()+"\"^^<http://www.w3.org/2001/XMLSchema#double> . \n";
		
		if (tripPreference.getHasPreferredTripDuration()!=null&&tripPreference.getHasPreferredTripDuration()>0)
			query+=" <"+filterSt+"> profile:hasPreferredTripDuration \""+tripPreference.getHasPreferredTripDuration()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . \n";
		
		if (tripPreference.getHasPreferredTripTime()!=null&&tripPreference.getHasPreferredTripTime()>0)
			query+=" <"+filterSt+"> profile:hasPreferredTripTime \""+tripPreference.getHasPreferredTripTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . \n";
		
		if (tripPreference.getHasPreferredCity()!=null&&!tripPreference.getHasPreferredCity().isEmpty())
			query+=" <"+ filterSt +"> profile:hasPreferredCity \""+tripPreference.getHasPreferredCity()+"\" . \n";
		
		if (tripPreference.getHasPreferredCountry()!=null&&!tripPreference.getHasPreferredCountry().isEmpty())
			query+=" <"+filterSt+">  profile:hasPreferredCountry \""+tripPreference.getHasPreferredCountry()+"\" . \n";
		
		if (tripPreference.getHasPreferredWeatherCondition()!=null&&!tripPreference.getHasPreferredWeatherCondition().isEmpty())
			query+=" <"+filterSt +"> profile:hasPreferredWeatherCondition \""+tripPreference.getHasPreferredWeatherCondition()+"\" . \n";
		
		if (tripPreference.getHasPreferredMinTimeOfAccompany()!=null&&tripPreference.getHasPreferredMinTimeOfAccompany()>0)
			query+=" <"+filterSt+"> profile:hasPreferredMinTimeOfAccompany \""+tripPreference.getHasPreferredMinTimeOfAccompany()+"\"^^<http://www.w3.org/2001/XMLSchema#long> . \n";
		
		if (tripPreference.getHasModalityType()!=null)
			query+=" <"+filterSt +"> profile:hasModalityType \""+tripPreference.getHasModalityType().toString()+"\" . \n";
		
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
				+ " INSERT DATA { \n"
				+ " GRAPH <"+ getGraphName(uid)+">  { \n";
					Iterator <eu.threecixty.profile.oldmodels.TripPreference> iterators = tripPreferences.iterator();
					for ( ; iterators.hasNext(); ){
						eu.threecixty.profile.oldmodels.TripPreference tripPreference=iterators.next();
						if (tripPreference.getHasTripPreferenceURI()==null ||tripPreference.getHasTripPreferenceURI().isEmpty())
							tripPreference.setHasTripPreferenceURI(PROFILE_URI+uid+"/TripPreference/"+UUID.randomUUID().toString());
						
						query+= makeTripPreferenceQuery(uid, tripPreference);
					}
				query+= "}\n"
				+ "}";
		return query;
	}

	/**
	 * remove multiple Trip preferences of the user in the kb
	 * @param uid
	 * @return
	 */
	public static String removeMultipleTripPreferences(String uid){
		String query=PREFIX
				+ " DELETE Where { \n"
				+ " GRAPH <"+ getGraphName(uid)+"> { \n"
	                + " ?filter ?p ?o . \n"
	                + " ?about frap:filter ?filter . \n"
	                + " ?tripPreference frap:about ?about . \n"
	                + " <"+PROFILE_URI+uid+"> frap:holds ?tripPreference. \n"
				+ " }\n"
				+ "}";
		return query;
	}
	/**
	 * select Trip preferences of the user in the kb
	 * @param uid
	 * @return
	 */
	public static String getTripPreferences(String uid) {
		String query=PREFIX
				+ "select ?tripPreference ?preferredMaxTotalDistance ?preferredTripDuration ?preferredTripTime ?preferredCity ?preferredCountry ?preferredWeatherCondition ?preferredMinTimeOfAccompany ?modality \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where { \n"
					+ " <"+PROFILE_URI+uid+"> frap:holds ?tripPreference. \n"
					+ " ?tripPreference frap:about ?about . \n"
					+ " ?about frap:filter ?filter . \n"
					+ " Optional {?filter profile:hasPreferredMaxTotalDistance ?preferredMaxTotalDistance .} \n"
					+ " Optional {?filter profile:hasPreferredTripDuration ?preferredTripDuration .} \n"
					+ " Optional {?filter profile:hasPreferredTripTime ?preferredTripTime .} \n"
					+ " Optional {?filter profile:hasPreferredCity ?preferredCity .} \n"
					+ " Optional {?filter profile:hasPreferredCountry ?preferredCountry .} \n"
					+ " Optional {?filter profile:hasPreferredWeatherCondition ?preferredWeatherCondition .} \n"
					+ " Optional {?filter profile:hasPreferredMinTimeOfAccompany ?preferredMinTimeOfAccompany .} \n"
					+ " Optional {?filter profile:hasModalityType ?modality .} \n"
					+ " Filter (fn:contains(STR(?tripPreference),\"TripPreference\")) \n"
				+ " }";
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
		String query= " <"+PROFILE_URI+uid+"> frap:holds <"+uri+"> .\n";
					//+ " <"+uri+"> rdf:type frap:Preference . \n";
		
		query+=" <"+placePreference.getHasPlacePreferenceURI()+"> frap:about <"+aboutSt+"> . \n"
			//+ " <"+aboutSt+"> rdf:type frap:Pattern . \n"
			+ " <"+aboutSt+"> frap:filter <"+filterSt+"> . \n";
			//+ " <"+filterSt+"> rdf:type frap:Filter . \n";
		
			if (placePreference.getHasPlaceDetailPreference()!=null){
				if (placePreference.getHasPlaceDetailPreference().getHasNatureOfPlace()!=null) 
					query+= " <"+filterSt+"> profile:hasNatureOfPlace \""+ placePreference.getHasPlaceDetailPreference().getHasNatureOfPlace()+"\" . \n";
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
			+ " INSERT DATA { \n"
			+ " GRAPH <"+ getGraphName(uid)+"> { \n";
			if (placePreference.getHasPlacePreferenceURI()==null ||placePreference.getHasPlacePreferenceURI().isEmpty())
				placePreference.setHasPlacePreferenceURI(PROFILE_URI+uid+"/PlacePreference/"+UUID.randomUUID().toString());
			
			query+= makePlacePreferenceQuery(uid, placePreference);
	
			query+= "}\n"
				+ "}";
		return query;
	}
	
	/**
	 * remove place preference of the user in the kb
	 * @param uid
	 * @return
	 */
	public static String removePlacePreferences(String uid){
		String query=PREFIX
			+ " DELETE Where { "
			+ " GRAPH <"+ getGraphName(uid)+"> { \n"
	            + " ?filter ?p ?o . \n"
	            + " ?about frap:filter ?filter . \n"
	            + " ?placePreference frap:about ?about . \n"
	            + " <"+PROFILE_URI+uid+"> frap:holds ?placePreference. \n"
			+ " }\n"
			+ " }";
		return query;
	}

	/**
	 * select place preferences of the user in the kb
	 * @param uid
	 * @return
	 */
	public static String getPlacePreferences(String uid) {
		String query=PREFIX
				+ " select ?placePreference ?natureOfPlace \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where { \n"
					+ " <"+PROFILE_URI+uid+"> frap:holds ?placePreference. \n"
					+ " ?placePreference frap:about ?about . \n"
					+ " ?about frap:filter ?filter . \n"
					+ " Optional {?filter profile:hasNatureOfPlace ?natureOfPlace .} \n"
					+ " Filter (fn:contains(STR(?placePreference),\"PlacePreference\")) \n"
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
				+ " INSERT DATA {\n"
				+ " GRAPH <"+ getGraphName(uid) +"> { \n"
					+" <" + PROFILE_URI + uid + "> foaf:img <" + profileImage + "> .\n"
				+ " }\n"
				+ " }";
		return query;
	}
	
	/**
	 * Creates a query to delete the profile image of a given UID in Virtuoso. 
	 * @param uid
	 * @return
	 */
	public static String createQueryToDeleteProfileImage(String uid) {
		String query = PREFIX
				+ " DELETE WHERE { "
				+ " GRAPH <" + getGraphName(uid) + "> \n" 
				+ " { <" + PROFILE_URI + uid + "> foaf:img ?o  } \n"
				+ " }";
		return query;
	}
	
	/**
	 * Creates a query to get the profile image of a given UID.
	 * @param uid
	 * @return
	 */
	public static String createQueryToGetProfileImage(String uid) {
		String query=PREFIX
				+ " select ?profileImage \n"
				+ " from <" + getGraphName(uid) + "> \n"
				+ " where \n"
				+ " { <" + PROFILE_URI + uid + "> foaf:img ?profileImage  } ";
		return query;
	}

	/**
	 * Delegate method to get user's private graph.
	 * @param uid
	 * @return Private graph name
	 */
	private static String getGraphName(String uid) {
		return VirtuosoManager.getInstance().getGraph(uid);
	}
}

package eu.threecixty.profile;

import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class GetSetQueryStrings {
	private static final String PROFILE_URI = "http://www.eu.3cixty.org/profile#";
	
	/**
	 * insert User in the KB
	 * @param uid
	 * @return
	 */
	public static String setUser(String uid){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH +">"
			+ "  {"
			+ "  profile:"+uid+" rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
			+ "  profile:"+uid+" rdf:type profile:UserProfile."
			+ "  profile:"+uid+" profile:hasUID \""+uid+"\" ."
			+ "}";
			return query;
	}
	/**
	 * insert Multiple User in the KB
	 * @param uids
	 * @return
	 */
	public static String setMultipleUser(Set<String> uids){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+ PROFILE_URI +">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH +">"
			+ "  {";
		Iterator <String> iterators = uids.iterator();
		for ( ; iterators.hasNext(); ){
			String uid=iterators.next();
			query+= "  profile:"+uid+" rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
			+ "  profile:"+uid+" rdf:type profile:UserProfile."
			+ "  profile:"+uid+" profile:hasUID \""+uid+"\" .";
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH +">"
			+ "  {"
			+ "  profile:"+uid+" rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
			+ "  profile:"+uid+" rdf:type profile:UserProfile."
			+ "  profile:"+uid+" profile:hasUID \""+uid+"\" ."
			+ "}";
			return query;
	}
	/**
	 * Remove multiple user from the kb
	 * @param uids
	 * @return
	 */
	public static String removeMultipleUser(Set<String> uids){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH +">"
			+ "  {";
		Iterator <String> iterators = uids.iterator();
		for ( ; iterators.hasNext(); ){
			String uid=iterators.next();
			query+= "  profile:"+uid+" rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
			+ "  profile:"+uid+" rdf:type profile:UserProfile."
			+ "  profile:"+uid+" profile:hasUID \""+uid+"\" .";
		}
		query+= "}";
		return query;
	}
	
	/**
	 * select last crawl time
	 * @param uid
	 * @return
	 */
	public static String getLastCrawlTime(String uid) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "select ?lastCrawlTime "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH +">"
			+ "  { ";
			if (time==null || time.isEmpty())
				time="0";
			query+= "  profile:"+uid+" profile:hasLastCrawlTime \""+time+"\" ."
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ "  { "
			+ "  profile:"+uid+" profile:hasLastCrawlTime \""+time+"\" ."
			+ "}";
			return query;
	}
		
	/**
	 * Select name of the user from KB
	 * @param uid
	 * @return
	 */
	public static String getName(String uid) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "select ?name ?givenname ?familyname ?middlename ?additionalname ?nickname"
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "?s vcard:hasName ?name. "
					+ "?name vcard:given-name ?givenname."
					+ "?name vcard:family-name ?familyname."
					+ "	Optional {?name profile:middleName ?middlename.}"
					+ "	Optional {?name vcard:additional-name ?additionalname.}"
					+ " Optional {?name vcard:nickname ?nickname.}"
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}
	/**
	 * insert name object of the User in the KB 
	 * @param uid
	 * @param name
	 * @return
	 */
	public static String setName(String uid, eu.threecixty.profile.oldmodels.Name name ){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH +">"
			+ "  { ";
			if  (name.getHasNameURI()!=null&&!name.getHasNameURI().isEmpty()){
				query+= "  <"+name.getHasNameURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
					+ "  <"+name.getHasNameURI()+"> rdf:type vcard:Name."
					+ "  profile:"+uid+" vcard:hasName <"+name.getHasNameURI()+"> .";
				if (name.getGivenName()!=null&&!name.getGivenName().isEmpty())
					query+= "  <"+name.getHasNameURI()+"> vcard:given-name \""+name.getGivenName()+"\".";
				if (name.getFamilyName()!=null&&!name.getFamilyName().isEmpty())
					query+= "  <"+name.getHasNameURI()+"> vcard:family-name \""+name.getFamilyName()+"\".";
				if (name.getMiddleName()!=null&&!name.getMiddleName().isEmpty())
					query+= "  <"+name.getHasNameURI()+"> profile:middleName \""+name.getMiddleName()+"\".";
				if (name.getAdditionalName()!=null&&!name.getAdditionalName().isEmpty())
					query+= "  <"+name.getHasNameURI()+"> vcard:additional-name \""+name.getAdditionalName()+"\".";
				if (name.getNickname()!=null&&!name.getNickname().isEmpty())
					query+= "  <"+name.getHasNameURI()+"> vcard:nickname "+name.getNickname()+"\" .";
			}
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ "  { ";
			if  (name.getHasNameURI()!=null&&!name.getHasNameURI().isEmpty()){
				query+= "  <"+name.getHasNameURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
					+ " <"+name.getHasNameURI()+"> rdf:type vcard:Name."
					+ "  profile:"+uid+" vcard:hasName <"+name.getHasNameURI()+"> .";
				if (name.getGivenName()!=null&&!name.getGivenName().isEmpty())
					query+= "  <"+name.getHasNameURI()+"> vcard:given-name \""+name.getGivenName()+"\".";
				if (name.getFamilyName()!=null&&!name.getFamilyName().isEmpty())
					query+= "  <"+name.getHasNameURI()+"> vcard:family-name \""+name.getFamilyName()+"\".";
				if (name.getMiddleName()!=null&&!name.getMiddleName().isEmpty())
					query+= "  <"+name.getHasNameURI()+"> profile:middleName \""+name.getMiddleName()+"\".";
				if (name.getAdditionalName()!=null&&!name.getAdditionalName().isEmpty())
					query+= "  <"+name.getHasNameURI()+"> vcard:additional-name \""+name.getAdditionalName()+"\".";
				if (name.getNickname()!=null&&!name.getNickname().isEmpty())
					query+= "  <"+name.getHasNameURI()+"> vcard:nickname "+name.getNickname()+"\" .";
			}
			query+= "}";
			return query;
	}
	
	/**
	 * Select address of the user from the KB
	 * @param uid
	 * @return
	 */
	public static String getAddress(String uid) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "select ?address ?townname ?countryname ?staddress ?pcode ?longitude ?lat "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "OPTIONAL {?s vcard:hasAddress ?address. }"
					+ "OPTIONAL {?address vcard:latitude ?lat.}"
					+ "OPTIONAL {?address vcard:longitude ?longitude. }"
					+ "OPTIONAL {?address vcard:postal-code ?pcode.}"
					+ "OPTIONAL {?address vcard:street-address ?staddress.}"
					+ "OPTIONAL {?address vcard:townName ?townname.}"
					+ "OPTIONAL {?address vcard:country-name ?countryname.}"
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}
	/**
	 * insert Address object of the user in the KB
	 * @param uid
	 * @param address
	 * @return
	 */
	public static String setAddress(String uid, eu.threecixty.profile.oldmodels.Address address){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ "  {";
		if (address.getHasAddressURI()!=null&&!address.getHasAddressURI().isEmpty()){
			query+= "  <"+address.getHasAddressURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
				+ "  <"+address.getHasAddressURI()+"> rdf:type vcard:Address."
				+ "  profile:"+uid+" vcard:hasAddress <"+address.getHasAddressURI()+"> .";
			if (address.getCountryName()!=null&&!address.getCountryName().isEmpty())
				query+= "  <"+address.getHasAddressURI()+"> vcard:country-name \""+address.getCountryName()+"\".";
			if (address.getTownName()!=null&&!address.getTownName().isEmpty())
				query+= "  <"+address.getHasAddressURI()+"> vcard:townName \""+address.getTownName()+"\".";
			if (address.getStreetAddress()!=null&&!address.getStreetAddress().isEmpty())
				query+= "  <"+address.getHasAddressURI()+"> vcard:street-address \""+address.getStreetAddress()+"\".";
			if (address.getPostalCode()!=null&&!address.getPostalCode().isEmpty())
				query+= "  <"+address.getHasAddressURI()+"> vcard:postal-code \""+address.getPostalCode()+"\".";
			if (address.getLongitute()!=0)
				query+= "  <"+address.getHasAddressURI()+"> vcard:longitude \""+address.getLongitute()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
			if (address.getLatitude()!=0)
				query+= "  <"+address.getHasAddressURI()+"> vcard:latitude \""+address.getLatitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
				+ "   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ "  {";
			if (address.getHasAddressURI()!=null&&!address.getHasAddressURI().isEmpty()){
				query+= "  <"+address.getHasAddressURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
					+ "  <"+address.getHasAddressURI()+"> rdf:type vcard:Address."
					+ "  profile:"+uid+" vcard:hasAddress <"+address.getHasAddressURI()+"> .";
				if (address.getCountryName()!=null&&!address.getCountryName().isEmpty())
					query+= "  <"+address.getHasAddressURI()+"> vcard:country-name \""+address.getCountryName()+"\".";
				if (address.getTownName()!=null&&!address.getTownName().isEmpty())
					query+= "  <"+address.getHasAddressURI()+"> vcard:townName \""+address.getTownName()+"\".";
				if (address.getStreetAddress()!=null&&!address.getStreetAddress().isEmpty())
					query+= "  <"+address.getHasAddressURI()+"> vcard:street-address \""+address.getStreetAddress()+"\".";
				if (address.getPostalCode()!=null&&!address.getPostalCode().isEmpty())
					query+= "  <"+address.getHasAddressURI()+"> vcard:postal-code \""+address.getPostalCode()+"\".";
				if (address.getLongitute()!=0)
					query+= "  <"+address.getHasAddressURI()+"> vcard:longitude \""+address.getLongitute()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (address.getLatitude()!=0)
					query+= "  <"+address.getHasAddressURI()+"> vcard:latitude \""+address.getLatitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
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
		String query="   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ " INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { "
				+ "  profile:"+uid+" foaf:knows profile:"+uidKnows+" ."
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
		String query="   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ " INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <String> iterators = knows.iterator();
		for ( ; iterators.hasNext(); ){
			String uidKnows=iterators.next();
			query+= "  profile:"+uid+" foaf:knows profile:"+uidKnows+" .";
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
		String query="   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ " DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { "
				+ "  profile:"+uid+" foaf:knows profile:"+uidKnows+" ."
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
		String query="   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ " DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <String> iterators = knows.iterator();
		for ( ; iterators.hasNext(); ){
			String uidKnows=iterators.next();
			query+= "  profile:"+uid+" foaf:knows profile:"+uidKnows+" .";
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
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?uidknows "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "OPTIONAL {"
						+ "?s foaf:knows ?knows. "
						+ "?knows  profile:hasUID ?uidknows.  "
					+ "}"
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}
	
	/**
	 * insert profile Identity of a user in the KB	
	 * @param uid
	 * @param profileIdentities
	 * @return
	 */
	public static String setProfileIdentities(String uid, eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentities){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		
		if  (profileIdentities.getHasProfileIdentitiesURI()==null||profileIdentities.getHasProfileIdentitiesURI().isEmpty())
			profileIdentities.setHasProfileIdentitiesURI(PROFILE_URI+uid+"/ProfileIdentities/"+profileIdentities.getHasSourceCarrier());
		query+= "  <"+profileIdentities.getHasProfileIdentitiesURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
				+ "  <"+profileIdentities.getHasProfileIdentitiesURI()+"> rdf:type profile:ProfileIdentities."
				+ "  profile:"+uid+" profile:hasProfileIdentities <"+profileIdentities.getHasProfileIdentitiesURI()+"> .";
		if  (profileIdentities.getHasSource()!=null&&!profileIdentities.getHasSource().isEmpty())
			query+= "  <"+profileIdentities.getHasProfileIdentitiesURI()+"> profile:hasSource <"+profileIdentities.getHasSource()+"> .";
		if  (profileIdentities.getHasUserAccountID()!=null&&!profileIdentities.getHasUserAccountID().isEmpty())
			query+= "  <"+profileIdentities.getHasProfileIdentitiesURI()+"> profile:hasUserAccountID \""+profileIdentities.getHasUserAccountID()+"\" .";
		if  (profileIdentities.getHasUserInteractionMode().toString()!=null&&!profileIdentities.getHasUserInteractionMode().toString().isEmpty())
			query+= "  <"+profileIdentities.getHasProfileIdentitiesURI()+"> profile:hasUserInteractionMode \""+profileIdentities.getHasUserInteractionMode()+"\" .";
		
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
		String query="   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ " INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.ProfileIdentities> iterators = profileIdentities.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentity=iterators.next();
			if  (profileIdentity.getHasProfileIdentitiesURI()==null||profileIdentity.getHasProfileIdentitiesURI().isEmpty())
				profileIdentity.setHasProfileIdentitiesURI(PROFILE_URI+uid+"/ProfileIdentities/"+profileIdentity.getHasSourceCarrier());
			
			query+= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
			query+= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> rdf:type profile:ProfileIdentities.";
			query+= "  profile:"+uid+" profile:hasProfileIdentities <"+profileIdentity.getHasProfileIdentitiesURI()+"> .";
			if  (profileIdentity.getHasSource()!=null&&!profileIdentity.getHasSource().isEmpty())
				query+= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> profile:hasSource <"+profileIdentity.getHasSource()+"> .";
			if  (profileIdentity.getHasUserAccountID()!=null&&!profileIdentity.getHasUserAccountID().isEmpty())
				query+= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> profile:hasUserAccountID \""+profileIdentity.getHasUserAccountID()+"\" .";
			if  (profileIdentity.getHasUserInteractionMode().toString()!=null&&!profileIdentity.getHasUserInteractionMode().toString().isEmpty())
				query+= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> profile:hasUserInteractionMode \""+profileIdentity.getHasUserInteractionMode()+"\" .";
			
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
	public static String removeProfileIdentities(String uid, eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentities){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		
		if  (profileIdentities.getHasProfileIdentitiesURI()!=null&&!profileIdentities.getHasProfileIdentitiesURI().isEmpty()){
			query+= "  <"+profileIdentities.getHasProfileIdentitiesURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
					+ "  <"+profileIdentities.getHasProfileIdentitiesURI()+"> rdf:type profile:ProfileIdentities."
					+ "  profile:"+uid+" profile:hasProfileIdentities <"+profileIdentities.getHasProfileIdentitiesURI()+"> .";
			if  (profileIdentities.getHasSource()!=null&&!profileIdentities.getHasSource().isEmpty())
				query+= "  <"+profileIdentities.getHasProfileIdentitiesURI()+"> profile:hasSource <"+profileIdentities.getHasSource()+"> .";
			if  (profileIdentities.getHasUserAccountID()!=null&&!profileIdentities.getHasUserAccountID().isEmpty())
				query+= "  <"+profileIdentities.getHasProfileIdentitiesURI()+"> profile:hasUserAccountID \""+profileIdentities.getHasUserAccountID()+"\" .";
			if  (profileIdentities.getHasUserInteractionMode().toString()!=null&&!profileIdentities.getHasUserInteractionMode().toString().isEmpty())
				query+= "  <"+profileIdentities.getHasProfileIdentitiesURI()+"> profile:hasUserInteractionMode \""+profileIdentities.getHasUserInteractionMode()+"\" .";
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
		String query="   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "  DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.ProfileIdentities> iterators = profileIdentities.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.ProfileIdentities profileIdentity=iterators.next();
			if  (profileIdentity.getHasProfileIdentitiesURI()!=null&&!profileIdentity.getHasProfileIdentitiesURI().isEmpty()){
				query+= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
				query+= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> rdf:type profile:ProfileIdentities.";
				query+= "  profile:"+uid+" profile:hasProfileIdentities <"+profileIdentity.getHasProfileIdentitiesURI()+"> .";
				if  (profileIdentity.getHasSource()!=null&&!profileIdentity.getHasSource().isEmpty())
					query+= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> profile:hasSource <"+profileIdentity.getHasSource()+"> .";
				if  (profileIdentity.getHasUserAccountID()!=null&&!profileIdentity.getHasUserAccountID().isEmpty())
					query+= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> profile:hasUserAccountID \""+profileIdentity.getHasUserAccountID()+"\" .";
				if  (profileIdentity.getHasUserInteractionMode().toString()!=null&&!profileIdentity.getHasUserInteractionMode().toString().isEmpty())
					query+= "  <"+profileIdentity.getHasProfileIdentitiesURI()+"> profile:hasUserInteractionMode \""+profileIdentity.getHasUserInteractionMode()+"\" .";
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
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?pi ?source ?piID ?uIM "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "?s profile:hasProfileIdentities ?pi. "
					+ "?pi profile:hasSource ?source."
					+ "?pi profile:hasUserAccountID ?piID."
					+ "?pi profile:hasUserInteractionMode ?uIM."
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}
	
	/**
	 * insert preference of a user in the KB. This inserts only the preference uri not the preference object
	 * @param uid
	 * @param preferenceURI
	 * @return
	 */
	public static String setPreferences(String uid, String preferenceURI){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
			if (preferenceURI!=null&&!preferenceURI.isEmpty()){
				query+= "  <"+preferenceURI+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
				+ "  <"+preferenceURI+"> rdf:type profile:Preference."
				+ "  profile:"+uid+" profile:hasPreference <"+preferenceURI+"> .";
			}
			query+= "}";
			return query;
	}
	/**
	 * remove preference of a user in the KB. This removes only the preference uri not the preference object
	 * @param uid
	 * @param preferenceURI
	 * @return
	 */
	public static String removePreferences(String uid,String preferenceURI){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		if (preferenceURI!=null&&!preferenceURI.isEmpty()){
			query+= "  <"+preferenceURI+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
			+ "  <"+preferenceURI+"> rdf:type profile:Preference."
			+ "  profile:"+uid+" profile:hasPreference <"+preferenceURI+"> .";
		}
		query+= "}";
		return query;
	}
	/**
	 * get preference of a user in the KB. This selects only the preference uri
	 * @param uid
	 * @return
	 */
	public static String getPreferences(String uid) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?pref "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "?s profile:hasPreference ?pref. "
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}
	
	/**
	 * insert user like to the kb.
	 * @param perferenceURI
	 * @param like
	 * @return
	 */
	public static String setLikes(String perferenceURI, eu.threecixty.profile.oldmodels.Likes like){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH +">"
			+ " { ";
		if (like.getHasLikesURI()==null||like.getHasLikesURI().isEmpty())
			like.setHasLikesURI(perferenceURI+"/Likes/"+UUID.randomUUID().toString());
	
		query+= "  <"+like.getHasLikesURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
			+ "  <"+like.getHasLikesURI()+"> rdf:type profile:Likes."
			+ "  <"+perferenceURI+"> profile:hasLike <"+like.getHasLikesURI()+"> .";
		if (like.getHasLikeName()!=null&&!like.getHasLikeName().isEmpty())
			query+= "  <"+like.getHasLikesURI()+"> profile:hasLikeName \""+like.getHasLikeName()+"\" .";
		if (like.getHasLikeType().toString()!=null&&!like.getHasLikeType().toString().isEmpty())
			query+= "  <"+like.getHasLikesURI()+"> profile:hasLikeType \""+like.getHasLikeType()+"\" .";

		query+= "}";
		return query;
	}
	/**
	 * insert multiple likes of the user in the kb
	 * @param perferenceURI
	 * @param likes
	 * @return
	 */
	public static String setMultipleLikes(String perferenceURI, Set<eu.threecixty.profile.oldmodels.Likes> likes){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.Likes> iterators = likes.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.Likes like=iterators.next();
			if (like.getHasLikesURI()==null||like.getHasLikesURI().isEmpty())
				like.setHasLikesURI(perferenceURI+"/Likes/"+UUID.randomUUID().toString());
			
			query+= "  <"+like.getHasLikesURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
				+ "  <"+like.getHasLikesURI()+"> rdf:type profile:Likes."
				+ "  <"+perferenceURI+"> profile:hasLike <"+like.getHasLikesURI()+"> .";
			if (like.getHasLikeName()!=null&&!like.getHasLikeName().isEmpty())
				query+= "  <"+like.getHasLikesURI()+"> profile:hasLikeName \""+like.getHasLikeName()+"\" .";
			if (like.getHasLikeType().toString()!=null&&!like.getHasLikeType().toString().isEmpty())
				query+= "  <"+like.getHasLikesURI()+"> profile:hasLikeType \""+like.getHasLikeType()+"\" .";
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
	public static String removeLikes(String perferenceURI, eu.threecixty.profile.oldmodels.Likes like){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		if (like.getHasLikesURI()!=null&&!like.getHasLikesURI().isEmpty()){
			query+= "  <"+like.getHasLikesURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
				+ "  <"+like.getHasLikesURI()+"> rdf:type profile:Likes."
				+ "  <"+perferenceURI+"> profile:hasLike <"+like.getHasLikesURI()+">.";
			if (like.getHasLikeName()!=null&&!like.getHasLikeName().isEmpty())
				query+= "  <"+like.getHasLikesURI()+"> profile:hasLikeName \""+like.getHasLikeName()+"\" .";
			if (like.getHasLikeType().toString()!=null&&!like.getHasLikeType().toString().isEmpty())
				query+= "  <"+like.getHasLikesURI()+"> profile:hasLikeType \""+like.getHasLikeType()+"\" .";
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
	public static String removeMultipleLikes(String perferenceURI, Set<eu.threecixty.profile.oldmodels.Likes> likes){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.Likes> iterators = likes.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.Likes like=iterators.next();
			if (like.getHasLikesURI()!=null&&!like.getHasLikesURI().isEmpty()){
				query+= "  <"+like.getHasLikesURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
					+ "  <"+like.getHasLikesURI()+"> rdf:type profile:Likes."
					+ "  <"+perferenceURI+"> profile:hasLike <"+like.getHasLikesURI()+"> .";
				if (like.getHasLikeName()!=null&&!like.getHasLikeName().isEmpty())
					query+= "  <"+like.getHasLikesURI()+"> profile:hasLikeName \""+like.getHasLikeName()+"\" .";
				if (like.getHasLikeType().toString()!=null&&!like.getHasLikeType().toString().isEmpty())
					query+= "  <"+like.getHasLikesURI()+"> profile:hasLikeType \""+like.getHasLikeType()+"\" .";
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
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?likes ?likeName ?liketype "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "?s profile:hasPreference ?pref. "
					+ "?pref profile:hasLike ?likes. "
					+ "?likes profile:hasLikeName ?likeName."
					+ "?likes profile:hasLikeType ?liketype."
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
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?likes ?likeName ?liketype "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "?s profile:hasPreference ?pref. "
					+ "?pref profile:hasLike ?likes. "
					+ "?likes profile:hasLikeName ?likeName."
					+ "?likes profile:hasLikeType ?liketype."
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+" FILTER (STR(?likeType) = \""+likeType+"\") "//Event
					+ "}";
		return query;
	}
	
	/**
	 * insert transport of a user in the KB. This inserts only the transport uri not the transport object
	 * @param uid
	 * @param transportUri
	 * @return
	 */
	public static String setTransport(String uid, String transportUri){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
			if (transportUri!=null&&!transportUri.isEmpty()){
				query+= "  <"+transportUri+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
					+ "  <"+transportUri+"> rdf:type profile:Transport ."
					+ "  profile:"+uid+" profile:hasTransport <"+transportUri+"> .";
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
			Iterator <String> iterators = transportUris.iterator();
			for ( ; iterators.hasNext(); ){
				String transportUri=iterators.next();
				if (transportUri!=null&&!transportUri.isEmpty()){
					query+= "  <"+transportUri+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
					query+= "  <"+transportUri+"> rdf:type profile:Transport.";
					query+= "  profile:"+uid+" profile:hasTransport <"+transportUri+"> .";
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
			if (transportUri!=null&&!transportUri.isEmpty()){
				query+= "  <"+transportUri+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>."
					+ "  <"+transportUri+"> rdf:type profile:Transport ."
					+ "  profile:"+uid+" profile:hasTransport <"+transportUri+"> .";
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
			Iterator <String> iterators = transportUris.iterator();
			for ( ; iterators.hasNext(); ){
				String transportUri=iterators.next();
				if (transportUri!=null&&!transportUri.isEmpty()){
					query+= "  <"+transportUri+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
					query+= "  <"+transportUri+"> rdf:type profile:Transport.";
					query+= "  profile:"+uid+" profile:hasTransport <"+transportUri+"> .";
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
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?transport "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "?s profile:hasTransport ?transport. "
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}

	/**
	 * insert multiple accompanies in the kb.
	 * @param transportURI
	 * @param accompanys
	 * @return
	 */
	public static String setMultipleAccompanying(String transportURI, Set<eu.threecixty.profile.oldmodels.Accompanying> accompanys){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO  GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.Accompanying> iterators = accompanys.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.Accompanying accompany =iterators.next();
			if (accompany.getHasAccompanyURI()==null||accompany.getHasAccompanyURI().isEmpty())
				accompany.setHasAccompanyURI(transportURI+"/Accompany/"+UUID.randomUUID().toString());
			
			query+= "  <"+transportURI+"> profile:hasAccompany <"+accompany.getHasAccompanyURI()+"> .";
			query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyUserID1 \""+accompany.getHasAccompanyUserid1ST()+"\" .";
			if (accompany.getHasAccompanyUserid2ST()!=null&&! accompany.getHasAccompanyUserid2ST().isEmpty())
				query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyUserID2 \""+accompany.getHasAccompanyUserid2ST()+"\" .";
			if (accompany.getHasAccompanyScore()!=null&&accompany.getHasAccompanyScore()>0)
				query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyScore \""+accompany.getHasAccompanyScore()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
			if (accompany.getHasAccompanyValidity()!=null&&accompany.getHasAccompanyValidity()>0)
				query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyValidity \""+accompany.getHasAccompanyValidity()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
			if (accompany.getHasAccompanyTime()!=null&&accompany.getHasAccompanyTime()>0)
				query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyTime \""+accompany.getHasAccompanyTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
			query+= "  <"+accompany.getHasAccompanyURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
			query+="  <"+accompany.getHasAccompanyURI()+"> rdf:type profile:Accompanying.";
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		if (accompany.getHasAccompanyURI()==null||accompany.getHasAccompanyURI().isEmpty())
			accompany.setHasAccompanyURI(transportURI+"/Accompany/"+UUID.randomUUID().toString());
		
		query+= "  <"+transportURI+"> profile:hasAccompany <"+accompany.getHasAccompanyURI()+"> .";
		query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyUserID1 \""+accompany.getHasAccompanyUserid1ST()+"\" .";
		if (accompany.getHasAccompanyUserid2ST()!=null && !accompany.getHasAccompanyUserid2ST().isEmpty())
			query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyUserID2 \""+accompany.getHasAccompanyUserid2ST()+"\" .";
		if (accompany.getHasAccompanyScore()!=null && accompany.getHasAccompanyScore()>0)
			query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyScore \""+accompany.getHasAccompanyScore()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
		if (accompany.getHasAccompanyValidity()!=null && accompany.getHasAccompanyValidity()>0)
			query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyValidity \""+accompany.getHasAccompanyValidity()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		if (accompany.getHasAccompanyTime()!=null && accompany.getHasAccompanyTime()>0)
			query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyTime \""+accompany.getHasAccompanyTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		query+= "  <"+accompany.getHasAccompanyURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
		query+="  <"+accompany.getHasAccompanyURI()+"> rdf:type profile:Accompanying.";

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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
				+ "   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { ";
			Iterator <eu.threecixty.profile.oldmodels.Accompanying> iterators = accompanys.iterator();
			for ( ; iterators.hasNext(); ){
				eu.threecixty.profile.oldmodels.Accompanying accompany =iterators.next();
				if (accompany.getHasAccompanyURI()==null||accompany.getHasAccompanyURI().isEmpty())
					accompany.setHasAccompanyURI(transportURI+"/Accompany/"+UUID.randomUUID().toString());
				
				query+= "  <"+transportURI+"> profile:hasAccompany <"+accompany.getHasAccompanyURI()+"> .";
				query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyUserID1 \""+accompany.getHasAccompanyUserid1ST()+"\" .";
				if (accompany.getHasAccompanyUserid2ST()!=null&&! accompany.getHasAccompanyUserid2ST().isEmpty())
					query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyUserID2 \""+accompany.getHasAccompanyUserid2ST()+"\" .";
				if (accompany.getHasAccompanyScore()!=null && accompany.getHasAccompanyScore()>0)
					query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyScore \""+accompany.getHasAccompanyScore()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (accompany.getHasAccompanyValidity()!=null&&accompany.getHasAccompanyValidity()>0)
					query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyValidity \""+accompany.getHasAccompanyValidity()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (accompany.getHasAccompanyTime()!=null&&accompany.getHasAccompanyTime()>0)
					query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyTime \""+accompany.getHasAccompanyTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				query+= "  <"+accompany.getHasAccompanyURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
				query+="  <"+accompany.getHasAccompanyURI()+"> rdf:type profile:Accompanying.";
				
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.Accompanying> iterators = accompanys.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.Accompanying accompany =iterators.next();
			if (accompany.getHasAccompanyURI()!=null&&!accompany.getHasAccompanyURI().isEmpty()){
				query+= "  <"+transportURI+"> profile:hasAccompany <"+accompany.getHasAccompanyURI()+"> .";
				query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyUserID1 \""+accompany.getHasAccompanyUserid1ST()+"\" .";
				if (accompany.getHasAccompanyUserid2ST()!=null&&! accompany.getHasAccompanyUserid2ST().isEmpty())
					query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyUserID2 \""+accompany.getHasAccompanyUserid2ST()+"\" .";
				if (accompany.getHasAccompanyScore()!=null&&accompany.getHasAccompanyScore()>0)
					query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyScore \""+accompany.getHasAccompanyScore()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (accompany.getHasAccompanyValidity()!=null&&accompany.getHasAccompanyValidity()>0)
					query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyValidity \""+accompany.getHasAccompanyValidity()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (accompany.getHasAccompanyTime()!=null&&accompany.getHasAccompanyTime()>0)
					query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyTime \""+accompany.getHasAccompanyTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				query+= "  <"+accompany.getHasAccompanyURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
				query+="  <"+accompany.getHasAccompanyURI()+"> rdf:type profile:Accompanying.";
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		if (accompany.getHasAccompanyURI()!=null&&!accompany.getHasAccompanyURI().isEmpty()){
			query+= "  <"+transportURI+"> profile:hasAccompany <"+accompany.getHasAccompanyURI()+"> .";
			query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyUserID1 \""+accompany.getHasAccompanyUserid1ST()+"\" .";
			if (accompany.getHasAccompanyUserid2ST()!=null&&! accompany.getHasAccompanyUserid2ST().isEmpty())
				query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyUserID2 \""+accompany.getHasAccompanyUserid2ST()+"\" .";
			if (accompany.getHasAccompanyScore()!=null&&accompany.getHasAccompanyScore()>0)
				query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyScore \""+accompany.getHasAccompanyScore()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
			if (accompany.getHasAccompanyValidity()!=null&&accompany.getHasAccompanyValidity()>0)
				query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyValidity \""+accompany.getHasAccompanyValidity()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
			if (accompany.getHasAccompanyTime()!=null&&accompany.getHasAccompanyTime()>0)
				query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyTime \""+accompany.getHasAccompanyTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
			query+= "  <"+accompany.getHasAccompanyURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
			query+="  <"+accompany.getHasAccompanyURI()+"> rdf:type profile:Accompanying.";
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
	public static String removeMultipleAccompanyingAssociatedToSpecificTransport(String transportUri, Set<eu.threecixty.profile.oldmodels.Accompanying> accompanys){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
				+ "   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { ";
			Iterator <eu.threecixty.profile.oldmodels.Accompanying> iterators = accompanys.iterator();
			for ( ; iterators.hasNext(); ){
				eu.threecixty.profile.oldmodels.Accompanying accompany =iterators.next();
				if (accompany.getHasAccompanyURI()!=null&&!accompany.getHasAccompanyURI().isEmpty()){
					query+= "  <"+transportUri+"> profile:hasAccompany <"+accompany.getHasAccompanyURI()+"> .";
					query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyUserID1 \""+accompany.getHasAccompanyUserid1ST()+"\" .";
					if (accompany.getHasAccompanyUserid2ST()!=null&&!accompany.getHasAccompanyUserid2ST().isEmpty())
						query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyUserID2 \""+accompany.getHasAccompanyUserid2ST()+"\" .";
					if (accompany.getHasAccompanyScore()!=null&&accompany.getHasAccompanyScore()>0)
						query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyScore \""+accompany.getHasAccompanyScore()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
					if (accompany.getHasAccompanyValidity()!=null&&accompany.getHasAccompanyValidity()>0)
						query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyValidity \""+accompany.getHasAccompanyValidity()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
					if (accompany.getHasAccompanyTime()!=null&&accompany.getHasAccompanyTime()>0)
						query+= "  <"+accompany.getHasAccompanyURI()+"> profile:hasAccompanyTime \""+accompany.getHasAccompanyTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
					query+= "  <"+accompany.getHasAccompanyURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
					query+="  <"+accompany.getHasAccompanyURI()+"> rdf:type profile:Accompanying.";
				}
			}
			query+= "}";
			return query;
	}
	/**
	 * select accompanies associated to the user
	 * @param uid
	 * @return
	 */
	public static String getAccompanying(String uid) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?accompany ?uid2 ?score ?validity ?acctime "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "?s profile:hasTransport ?transport. "
					+ "?transport profile:hasAccompany ?accompany. "
					+ "Optional {?accompany profile:hasAccompanyUserID2 ?uid2 .}"
					+ "Optional {?accompany profile:hasAccompanyScore ?score .}"
					+ "Optional {?accompany profile:hasAccompanyValidity ?validity .}"
					+ "Optional {?accompany profile:hasAccompanyTime ?acctime .}"
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}
	/**
	 * select accompanies of a user associated to a given transport
	 * @param transportURI
	 * @return
	 */
	public static String getAccompanyingForTransport(String transportURI) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?accompany ?uid2 ?score ?validity ?acctime "
				+ " where {"
					+ "?transport profile:hasAccompany ?accompany. "
					+ "Optional {?accompany profile:hasAccompanyUserID2 ?uid2 .}"
					+ "Optional {?accompany profile:hasAccompanyScore ?score .}"
					+ "Optional {?accompany profile:hasAccompanyValidity ?validity .}"
					+ "Optional {?accompany profile:hasAccompanyTime ?acctime .}"
					+" FILTER (STR(?transport) = \""+transportURI+"\") "
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		if (personalPlace.getHasPersonalPlaceURI()==null||personalPlace.getHasPersonalPlaceURI().isEmpty())
			personalPlace.setHasPersonalPlaceURI(regularTripURI+"/PersonalPlace/"+UUID.randomUUID().toString());
		
		query+= "  <"+regularTripURI+"> profile:hasPersonalPlace <"+personalPlace.getHasPersonalPlaceURI()+"> .";
		if (personalPlace.getHasPersonalPlaceexternalIds()!=null&&!personalPlace.getHasPersonalPlaceexternalIds().isEmpty())
			query+= " <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceExternalIDs \""+personalPlace.getHasPersonalPlaceexternalIds()+"\" .";
		if (personalPlace.getLatitude()!=null&&personalPlace.getLatitude()>0)
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:latitude \""+personalPlace.getLatitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
		if (personalPlace.getLongitude()!=null&&personalPlace.getLongitude()>0)
			query+="  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:longitude \""+personalPlace.getLongitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
		if (personalPlace.getHasPersonalPlaceStayDuration()!=null&&personalPlace.getHasPersonalPlaceStayDuration()>0)
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceStayDuration \""+personalPlace.getHasPersonalPlaceStayDuration()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		if (personalPlace.getHasPersonalPlaceAccuracy()!=null&&personalPlace.getHasPersonalPlaceAccuracy()>0)
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceAccuracy \""+personalPlace.getHasPersonalPlaceAccuracy()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
		if (personalPlace.getHasPersonalPlaceStayPercentage()!=null&&personalPlace.getHasPersonalPlaceStayPercentage()>0)
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceStayPercentage \""+personalPlace.getHasPersonalPlaceStayPercentage()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
		if (personalPlace.getPostalcode()!=null&&!personalPlace.getPostalcode().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> vcard:postal-code \""+personalPlace.getPostalcode()+"\" .";
		if (personalPlace.getHasPersonalPlaceWeekdayPattern()!=null&&!personalPlace.getHasPersonalPlaceWeekdayPattern().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceWeekDayPattern \""+personalPlace.getHasPersonalPlaceWeekdayPattern()+"\" .";
		if (personalPlace.getHasPersonalPlaceDayhourPattern()!=null&&!personalPlace.getHasPersonalPlaceDayhourPattern().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceDayHourPattern \""+personalPlace.getHasPersonalPlaceDayhourPattern()+"\" .";
		if (personalPlace.getHasPersonalPlaceType()!=null&&!personalPlace.getHasPersonalPlaceType().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceType \""+personalPlace.getHasPersonalPlaceType()+"\" .";
		if (personalPlace.getHasPersonalPlaceName()!=null&&!personalPlace.getHasPersonalPlaceName().isEmpty())
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceName \""+personalPlace.getHasPersonalPlaceName()+"\" .";
		//query+= "  profile:"+uid+"PersonalPlace/"+ID+" profile:hasUID \""+ID+"\" .";
		query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
		query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> rdf:type profile:PersonalPlace .";

		query+= "}";
		return query;
	}
	/**
	 * insert multiple personal places associated to a specific regular trip
	 * @param regularTripURI
	 * @param personalPlaces
	 * @return
	 */
	public static String setMultiplePersonalPlacesAssociatedToSpecificRegularTrip(String regularTripURI, Set<eu.threecixty.profile.oldmodels.PersonalPlace> personalPlaces ){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
				+ "   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.PersonalPlace> iterators = personalPlaces.iterator();
		for ( ; iterators.hasNext(); ){	
			eu.threecixty.profile.oldmodels.PersonalPlace personalPlace=iterators.next();
			if (personalPlace.getHasPersonalPlaceURI()==null||personalPlace.getHasPersonalPlaceURI().isEmpty())
				personalPlace.setHasPersonalPlaceURI(regularTripURI+"/PersonalPlace/"+UUID.randomUUID().toString());
				
			query+= "  <"+regularTripURI+"> profile:hasPersonalPlace <"+personalPlace.getHasPersonalPlaceURI()+"> .";
			if (personalPlace.getHasPersonalPlaceexternalIds()!=null&&!personalPlace.getHasPersonalPlaceexternalIds().isEmpty())
				query+= " <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceExternalIDs \""+personalPlace.getHasPersonalPlaceexternalIds()+"\" .";
			if (personalPlace.getLatitude()!=null&&personalPlace.getLatitude()>0)
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:latitude \""+personalPlace.getLatitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
			if (personalPlace.getLongitude()!=null&&personalPlace.getLongitude()>0)
				query+="  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:longitude \""+personalPlace.getLongitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
			if (personalPlace.getHasPersonalPlaceStayDuration()!=null&&personalPlace.getHasPersonalPlaceStayDuration()>0)
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceStayDuration \""+personalPlace.getHasPersonalPlaceStayDuration()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
			if (personalPlace.getHasPersonalPlaceAccuracy()!=null&&personalPlace.getHasPersonalPlaceAccuracy()>0)
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceAccuracy \""+personalPlace.getHasPersonalPlaceAccuracy()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
			if (personalPlace.getHasPersonalPlaceStayPercentage()!=null&&personalPlace.getHasPersonalPlaceStayPercentage()>0)
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceStayPercentage \""+personalPlace.getHasPersonalPlaceStayPercentage()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
			if (personalPlace.getPostalcode()!=null&&!personalPlace.getPostalcode().isEmpty())
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> vcard:postal-code \""+personalPlace.getPostalcode()+"\" .";
			if (personalPlace.getHasPersonalPlaceWeekdayPattern()!=null&&!personalPlace.getHasPersonalPlaceWeekdayPattern().isEmpty())
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceWeekDayPattern \""+personalPlace.getHasPersonalPlaceWeekdayPattern()+"\" .";
			if (personalPlace.getHasPersonalPlaceDayhourPattern()!=null&&!personalPlace.getHasPersonalPlaceDayhourPattern().isEmpty())
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceDayHourPattern \""+personalPlace.getHasPersonalPlaceDayhourPattern()+"\" .";
			if (personalPlace.getHasPersonalPlaceType()!=null&&!personalPlace.getHasPersonalPlaceType().isEmpty())
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceType \""+personalPlace.getHasPersonalPlaceType()+"\" .";
			if (personalPlace.getHasPersonalPlaceName()!=null&&!personalPlace.getHasPersonalPlaceName().isEmpty())
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceName \""+personalPlace.getHasPersonalPlaceName()+"\" .";
			//query+= "  profile:"+uid+"PersonalPlace/"+ID+" profile:hasUID \""+ID+"\" .";
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> rdf:type profile:PersonalPlace .";

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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		if (personalPlace.getHasPersonalPlaceURI()!=null&&!personalPlace.getHasPersonalPlaceURI().isEmpty()){
			query+= "  <"+regularTripURI+"> profile:hasPersonalPlace <"+personalPlace.getHasPersonalPlaceURI()+"> .";
			if (personalPlace.getHasPersonalPlaceexternalIds()!=null&&!personalPlace.getHasPersonalPlaceexternalIds().isEmpty())
				query+= " <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceExternalIDs \""+personalPlace.getHasPersonalPlaceexternalIds()+"\" .";
			if (personalPlace.getLatitude()!=null&&personalPlace.getLatitude()>0)
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:latitude \""+personalPlace.getLatitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
			if (personalPlace.getLongitude()!=null&&personalPlace.getLongitude()>0)
				query+="  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:longitude \""+personalPlace.getLongitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
			if (personalPlace.getHasPersonalPlaceStayDuration()!=null&&personalPlace.getHasPersonalPlaceStayDuration()>0)
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceStayDuration \""+personalPlace.getHasPersonalPlaceStayDuration()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
			if (personalPlace.getHasPersonalPlaceAccuracy()!=null&&personalPlace.getHasPersonalPlaceAccuracy()>0)
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceAccuracy \""+personalPlace.getHasPersonalPlaceAccuracy()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
			if (personalPlace.getHasPersonalPlaceStayPercentage()!=null&&personalPlace.getHasPersonalPlaceStayPercentage()>0)
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceStayPercentage \""+personalPlace.getHasPersonalPlaceStayPercentage()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
			if (personalPlace.getPostalcode()!=null&&!personalPlace.getPostalcode().isEmpty())
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> vcard:postal-code \""+personalPlace.getPostalcode()+"\" .";
			if (personalPlace.getHasPersonalPlaceWeekdayPattern()!=null&&!personalPlace.getHasPersonalPlaceWeekdayPattern().isEmpty())
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceWeekDayPattern \""+personalPlace.getHasPersonalPlaceWeekdayPattern()+"\" .";
			if (personalPlace.getHasPersonalPlaceDayhourPattern()!=null&&!personalPlace.getHasPersonalPlaceDayhourPattern().isEmpty())
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceDayHourPattern \""+personalPlace.getHasPersonalPlaceDayhourPattern()+"\" .";
			if (personalPlace.getHasPersonalPlaceType()!=null&&!personalPlace.getHasPersonalPlaceType().isEmpty())
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceType \""+personalPlace.getHasPersonalPlaceType()+"\" .";
			if (personalPlace.getHasPersonalPlaceName()!=null&&!personalPlace.getHasPersonalPlaceName().isEmpty())
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceName \""+personalPlace.getHasPersonalPlaceName()+"\" .";
			//query+= "  profile:"+uid+"PersonalPlace/"+ID+" profile:hasUID \""+ID+"\" .";
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
			query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> rdf:type profile:PersonalPlace .";
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
				+ "   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.PersonalPlace> iterators = personalPlaces.iterator();
		for ( ; iterators.hasNext(); ){	
			eu.threecixty.profile.oldmodels.PersonalPlace personalPlace=iterators.next();
			if (personalPlace.getHasPersonalPlaceURI()!=null&&!personalPlace.getHasPersonalPlaceURI().isEmpty()){
				query+= "  <"+regularTripURI+"> profile:hasPersonalPlace <"+personalPlace.getHasPersonalPlaceURI()+"> .";
				if (personalPlace.getHasPersonalPlaceexternalIds()!=null&&!personalPlace.getHasPersonalPlaceexternalIds().isEmpty())
					query+= " <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceExternalIDs \""+personalPlace.getHasPersonalPlaceexternalIds()+"\" .";
				if (personalPlace.getLatitude()!=null&&personalPlace.getLatitude()>0)
					query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:latitude \""+personalPlace.getLatitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (personalPlace.getLongitude()!=null&&personalPlace.getLongitude()>0)
					query+="  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:longitude \""+personalPlace.getLongitude()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (personalPlace.getHasPersonalPlaceStayDuration()!=null&&personalPlace.getHasPersonalPlaceStayDuration()>0)
					query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceStayDuration \""+personalPlace.getHasPersonalPlaceStayDuration()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (personalPlace.getHasPersonalPlaceAccuracy()!=null&&personalPlace.getHasPersonalPlaceAccuracy()>0)
					query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceAccuracy \""+personalPlace.getHasPersonalPlaceAccuracy()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (personalPlace.getHasPersonalPlaceStayPercentage()!=null&&personalPlace.getHasPersonalPlaceStayPercentage()>0)
					query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceStayPercentage \""+personalPlace.getHasPersonalPlaceStayPercentage()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (personalPlace.getPostalcode()!=null&&!personalPlace.getPostalcode().isEmpty())
					query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> vcard:postal-code \""+personalPlace.getPostalcode()+"\" .";
				if (personalPlace.getHasPersonalPlaceWeekdayPattern()!=null&&!personalPlace.getHasPersonalPlaceWeekdayPattern().isEmpty())
					query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceWeekDayPattern \""+personalPlace.getHasPersonalPlaceWeekdayPattern()+"\" .";
				if (personalPlace.getHasPersonalPlaceDayhourPattern()!=null&&!personalPlace.getHasPersonalPlaceDayhourPattern().isEmpty())
					query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceDayHourPattern \""+personalPlace.getHasPersonalPlaceDayhourPattern()+"\" .";
				if (personalPlace.getHasPersonalPlaceType()!=null&&!personalPlace.getHasPersonalPlaceType().isEmpty())
					query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceType \""+personalPlace.getHasPersonalPlaceType()+"\" .";
				if (personalPlace.getHasPersonalPlaceName()!=null&&!personalPlace.getHasPersonalPlaceName().isEmpty())
					query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> profile:hasPersonalPlaceName \""+personalPlace.getHasPersonalPlaceName()+"\" .";
				//query+= "  profile:"+uid+"PersonalPlace/"+ID+" profile:hasUID \""+ID+"\" .";
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
				query+= "  <"+personalPlace.getHasPersonalPlaceURI()+"> rdf:type profile:PersonalPlace .";
			}
		}
		query+= "}";
		return query;
	}
	/**
	 * select personal places associated for a user
	 * @param uid
	 * @return
	 */
	public static String getPersonalPlaces(String uid) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?pplace ?externalIDs ?latitude ?longitude ?stayDuration ?accuracy ?stayPercentage ?pcode ?weekDayPattern ?dayHourPattern ?placeType ?placeName "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "?s profile:hasTransport ?transport. "
					+ "?transport profile:hasRegularTrip ?regularTrip. "
					+ "?regularTrip profile:hasPersonalPlace ?pplace ."
					+ "Optional {?pplace profile:hasPersonalPlaceExternalIDs ?externalIDs .}"
					+ "Optional {?pplace profile:latitude ?latitude .}"
					+ "Optional {?pplace profile:longitude ?longitude .}"
					+ "Optional {?pplace profile:hasPersonalPlaceStayDuration ?stayDuration .}"
					+ "Optional {?pplace profile:hasPersonalPlaceAccuracy ?accuracy .}"
					+ "Optional {?pplace profile:hasPersonalPlaceStayPercentage ?stayPercentage .}"
					+ "Optional {?pplace vcard:postal-code ?pcode .}"
					+ "Optional {?pplace profile:hasUID ?id .}"
					+ "Optional {?pplace profile:hasPersonalPlaceWeekDayPattern ?weekDayPattern .}"
					+ "Optional {?pplace profile:hasPersonalPlaceDayHourPattern ?dayHourPattern .}"
					+ "Optional {?pplace profile:hasPersonalPlaceType ?placeType .}"
					+ "Optional {?pplace profile:hasPersonalPlaceName ?placeName .}"
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}
	/**
	 * select personal places associated for a regular trip of the user
	 * @param regularTripURI
	 * @return
	 */
	public static String getPersonalPlacesForRegularTrips(String regularTripURI) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?pplace ?externalIDs ?latitude ?longitude ?stayDuration ?accuracy ?stayPercentage ?pcode ?weekDayPattern ?dayHourPattern ?placeType ?placeName "
				+ " where {"
					+ "?regularTrip profile:hasPersonalPlace ?pplace ."
					+ "Optional {?pplace profile:hasPersonalPlaceExternalIDs ?externalIDs .}"
					+ "Optional {?pplace profile:latitude ?latitude .}"
					+ "Optional {?pplace profile:longitude ?longitude .}"
					+ "Optional {?pplace profile:hasPersonalPlaceStayDuration ?stayDuration .}"
					+ "Optional {?pplace profile:hasPersonalPlaceAccuracy ?accuracy .}"
					+ "Optional {?pplace profile:hasPersonalPlaceStayPercentage ?stayPercentage .}"
					+ "Optional {?pplace vcard:postal-code ?pcode .}"
					+ "Optional {?pplace profile:hasUID ?id .}"
					+ "Optional {?pplace profile:hasPersonalPlaceWeekDayPattern ?weekDayPattern .}"
					+ "Optional {?pplace profile:hasPersonalPlaceDayHourPattern ?dayHourPattern .}"
					+ "Optional {?pplace profile:hasPersonalPlaceType ?placeType .}"
					+ "Optional {?pplace profile:hasPersonalPlaceName ?placeName .}"
					+" FILTER (STR(?regularTrip) = \""+regularTripURI+"\") "
					+ "}";
		return query;
	}
	/**
	 * select personal place based on the URI
	 * @param uri
	 * @return
	 */
	public static String getPersonalPlacesFromURI(String uri) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?pplace ?externalIDs ?latitude ?longitude ?stayDuration ?accuracy ?stayPercentage ?pcode ?weekDayPattern ?dayHourPattern ?placeType ?placeName "
				+ " where {"
					+ "?s a profile:PersonalPlace. "
					+ "Optional {?s profile:hasPersonalPlaceExternalIDs ?externalIDs .}"
					+ "Optional {?s profile:latitude ?latitude .}"
					+ "Optional {?s profile:longitude ?longitude .}"
					+ "Optional {?s profile:hasPersonalPlaceStayDuration ?stayDuration .}"
					+ "Optional {?s profile:hasPersonalPlaceAccuracy ?accuracy .}"
					+ "Optional {?s profile:hasPersonalPlaceStayPercentage ?stayPercentage .}"
					+ "Optional {?s vcard:postal-code ?pcode .}"
					+ "Optional {?s profile:hasUID ?id .}"
					+ "Optional {?s profile:hasPersonalPlaceWeekDayPattern ?weekDayPattern .}"
					+ "Optional {?s profile:hasPersonalPlaceDayHourPattern ?dayHourPattern .}"
					+ "Optional {?s profile:hasPersonalPlaceType ?placeType .}"
					+ "Optional {?s profile:hasPersonalPlaceName ?placeName .}"
					+" FILTER (STR(?s) = \""+uri+"\") "
					+ "}";
		return query;
	}
	
	/**
	 * insert regular trip associated to a specific transport of a user in the kb
	 * @param transportUri
	 * @param regularTrip
	 * @return
	 */
	public static String setRegularTripsAssociatedToSpecificTransport(String transportUri, eu.threecixty.profile.oldmodels.RegularTrip regularTrip){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
				+ "   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { ";

			if (regularTrip.getHasRegularTripURI()!=null&&!regularTrip.getHasRegularTripURI().isEmpty()){
				query+= "  <"+transportUri+"> profile:hasRegularTrip <"+regularTrip.getHasRegularTripURI()+"> .";
				if (regularTrip.getHasRegularTripName()!=null&&!regularTrip.getHasRegularTripName().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripName \""+regularTrip.getHasRegularTripName()+"\" .";
				if (regularTrip.getHasRegularTripDepartureTime()!=null&&regularTrip.getHasRegularTripDepartureTime()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripDepartureTime \""+regularTrip.getHasRegularTripDepartureTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripDepartureTimeSD()!=null&&regularTrip.getHasRegularTripDepartureTimeSD()>0)
					query+="  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripDepartureTimeSD \""+regularTrip.getHasRegularTripDepartureTimeSD()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripTravelTime()!=null&&regularTrip.getHasRegularTripTravelTime()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTravelTime \""+regularTrip.getHasRegularTripTravelTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripTravelTimeSD()!=null&&regularTrip.getHasRegularTripTravelTimeSD()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTravelTimeSD \""+regularTrip.getHasRegularTripTravelTimeSD()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripFastestTravelTime()!=null&&regularTrip.getHasRegularTripFastestTravelTime()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripFastestTravelTime \""+regularTrip.getHasRegularTripFastestTravelTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripLastChanged()!=null&&regularTrip.getHasRegularTripLastChanged()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripLastChanged \""+regularTrip.getHasRegularTripLastChanged()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripTotalDistance()!=null&&regularTrip.getHasRegularTripTotalDistance()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTotalDistance \""+regularTrip.getHasRegularTripTotalDistance()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (regularTrip.getHasRegularTripTotalCount()!=null&&regularTrip.getHasRegularTripTotalCount()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTotalCount \""+regularTrip.getHasRegularTripTotalCount()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (regularTrip.getHasModalityType().toString()!=null&&!regularTrip.getHasModalityType().toString().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasModalityType \""+regularTrip.getHasModalityType()+"\" .";
				if (regularTrip.getHasRegularTripWeekdayPattern()!=null&&!regularTrip.getHasRegularTripWeekdayPattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripWeekdayPattern \""+regularTrip.getHasRegularTripWeekdayPattern()+"\" .";
				if (regularTrip.getHasRegularTripDayhourPattern()!=null&&!regularTrip.getHasRegularTripDayhourPattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripDayhourPattern \""+regularTrip.getHasRegularTripDayhourPattern()+"\" .";
				if (regularTrip.getHasRegularTripTravelTimePattern()!=null&&!regularTrip.getHasRegularTripTravelTimePattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTimePattern \""+regularTrip.getHasRegularTripTravelTimePattern()+"\" .";
				if (regularTrip.getHasRegularTripWeatherPattern()!=null&&!regularTrip.getHasRegularTripWeatherPattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripWeatherPattern \""+regularTrip.getHasRegularTripWeatherPattern()+"\" .";
				query+= "  <"+regularTrip.getHasRegularTripURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
				query+= "  <"+regularTrip.getHasRegularTripURI()+"> rdf:type profile:RegularTrip .";
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.RegularTrip> iterators = regularTrips.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.RegularTrip regularTrip= iterators.next();
			if (regularTrip.getHasRegularTripURI()!=null&&!regularTrip.getHasRegularTripURI().isEmpty()){
				query+= "  <"+transportUri+"> profile:hasRegularTrip <"+regularTrip.getHasRegularTripURI()+"> .";
				if (regularTrip.getHasRegularTripName()!=null&&!regularTrip.getHasRegularTripName().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripName \""+regularTrip.getHasRegularTripName()+"\" .";
				if (regularTrip.getHasRegularTripDepartureTime()!=null&&regularTrip.getHasRegularTripDepartureTime()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripDepartureTime \""+regularTrip.getHasRegularTripDepartureTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripDepartureTimeSD()!=null&&regularTrip.getHasRegularTripDepartureTimeSD()>0)
					query+="  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripDepartureTimeSD \""+regularTrip.getHasRegularTripDepartureTimeSD()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripTravelTime()!=null&&regularTrip.getHasRegularTripTravelTime()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTravelTime \""+regularTrip.getHasRegularTripTravelTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripTravelTimeSD()!=null&&regularTrip.getHasRegularTripTravelTimeSD()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTravelTimeSD \""+regularTrip.getHasRegularTripTravelTimeSD()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripFastestTravelTime()!=null&&regularTrip.getHasRegularTripFastestTravelTime()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripFastestTravelTime \""+regularTrip.getHasRegularTripFastestTravelTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripLastChanged()!=null&&regularTrip.getHasRegularTripLastChanged()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripLastChanged \""+regularTrip.getHasRegularTripLastChanged()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripTotalDistance()!=null&&regularTrip.getHasRegularTripTotalDistance()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTotalDistance \""+regularTrip.getHasRegularTripTotalDistance()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (regularTrip.getHasRegularTripTotalCount()!=null&&regularTrip.getHasRegularTripTotalCount()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTotalCount \""+regularTrip.getHasRegularTripTotalCount()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (regularTrip.getHasModalityType().toString()!=null&&!regularTrip.getHasModalityType().toString().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasModalityType \""+regularTrip.getHasModalityType()+"\" .";
				if (regularTrip.getHasRegularTripWeekdayPattern()!=null&&!regularTrip.getHasRegularTripWeekdayPattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripWeekdayPattern \""+regularTrip.getHasRegularTripWeekdayPattern()+"\" .";
				if (regularTrip.getHasRegularTripDayhourPattern()!=null&&!regularTrip.getHasRegularTripDayhourPattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripDayhourPattern \""+regularTrip.getHasRegularTripDayhourPattern()+"\" .";
				if (regularTrip.getHasRegularTripTravelTimePattern()!=null&&!regularTrip.getHasRegularTripTravelTimePattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTimePattern \""+regularTrip.getHasRegularTripTravelTimePattern()+"\" .";
				if (regularTrip.getHasRegularTripWeatherPattern()!=null&&!regularTrip.getHasRegularTripWeatherPattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripWeatherPattern \""+regularTrip.getHasRegularTripWeatherPattern()+"\" .";
				query+= "  <"+regularTrip.getHasRegularTripURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
				query+= "  <"+regularTrip.getHasRegularTripURI()+"> rdf:type profile:RegularTrip .";
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
				+ "   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { ";

			if (regularTrip.getHasRegularTripURI()!=null&&!regularTrip.getHasRegularTripURI().isEmpty()){
				query+= "  <"+transportUri+"> profile:hasRegularTrip <"+regularTrip.getHasRegularTripURI()+"> .";
				if (regularTrip.getHasRegularTripName()!=null&&!regularTrip.getHasRegularTripName().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripName \""+regularTrip.getHasRegularTripName()+"\" .";
				if (regularTrip.getHasRegularTripDepartureTime()!=null&&regularTrip.getHasRegularTripDepartureTime()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripDepartureTime \""+regularTrip.getHasRegularTripDepartureTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripDepartureTimeSD()!=null&&regularTrip.getHasRegularTripDepartureTimeSD()>0)
					query+="  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripDepartureTimeSD \""+regularTrip.getHasRegularTripDepartureTimeSD()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripTravelTime()!=null&&regularTrip.getHasRegularTripTravelTime()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTravelTime \""+regularTrip.getHasRegularTripTravelTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripTravelTimeSD()!=null&&regularTrip.getHasRegularTripTravelTimeSD()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTravelTimeSD \""+regularTrip.getHasRegularTripTravelTimeSD()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripFastestTravelTime()!=null&&regularTrip.getHasRegularTripFastestTravelTime()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripFastestTravelTime \""+regularTrip.getHasRegularTripFastestTravelTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripLastChanged()!=null&&regularTrip.getHasRegularTripLastChanged()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripLastChanged \""+regularTrip.getHasRegularTripLastChanged()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripTotalDistance()!=null&&regularTrip.getHasRegularTripTotalDistance()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTotalDistance \""+regularTrip.getHasRegularTripTotalDistance()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (regularTrip.getHasRegularTripTotalCount()!=null&&regularTrip.getHasRegularTripTotalCount()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTotalCount \""+regularTrip.getHasRegularTripTotalCount()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (regularTrip.getHasModalityType().toString()!=null&&!regularTrip.getHasModalityType().toString().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasModalityType \""+regularTrip.getHasModalityType()+"\" .";
				if (regularTrip.getHasRegularTripWeekdayPattern()!=null&&!regularTrip.getHasRegularTripWeekdayPattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripWeekdayPattern \""+regularTrip.getHasRegularTripWeekdayPattern()+"\" .";
				if (regularTrip.getHasRegularTripDayhourPattern()!=null&&!regularTrip.getHasRegularTripDayhourPattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripDayhourPattern \""+regularTrip.getHasRegularTripDayhourPattern()+"\" .";
				if (regularTrip.getHasRegularTripTravelTimePattern()!=null&&!regularTrip.getHasRegularTripTravelTimePattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTimePattern \""+regularTrip.getHasRegularTripTravelTimePattern()+"\" .";
				if (regularTrip.getHasRegularTripWeatherPattern()!=null&&!regularTrip.getHasRegularTripWeatherPattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripWeatherPattern \""+regularTrip.getHasRegularTripWeatherPattern()+"\" .";
				query+= "  <"+regularTrip.getHasRegularTripURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
				query+= "  <"+regularTrip.getHasRegularTripURI()+"> rdf:type profile:RegularTrip .";
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
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.RegularTrip> iterators = regularTrips.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.RegularTrip regularTrip= iterators.next();
			if (regularTrip.getHasRegularTripURI()!=null&&!regularTrip.getHasRegularTripURI().isEmpty()){
				query+= "  <"+transportUri+"> profile:hasRegularTrip <"+regularTrip.getHasRegularTripURI()+"> .";
				if (regularTrip.getHasRegularTripName()!=null&&!regularTrip.getHasRegularTripName().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripName \""+regularTrip.getHasRegularTripName()+"\" .";
				if (regularTrip.getHasRegularTripDepartureTime()!=null&&regularTrip.getHasRegularTripDepartureTime()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripDepartureTime \""+regularTrip.getHasRegularTripDepartureTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripDepartureTimeSD()!=null&&regularTrip.getHasRegularTripDepartureTimeSD()>0)
					query+="  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripDepartureTimeSD \""+regularTrip.getHasRegularTripDepartureTimeSD()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripTravelTime()!=null&&regularTrip.getHasRegularTripTravelTime()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTravelTime \""+regularTrip.getHasRegularTripTravelTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripTravelTimeSD()!=null&&regularTrip.getHasRegularTripTravelTimeSD()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTravelTimeSD \""+regularTrip.getHasRegularTripTravelTimeSD()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripFastestTravelTime()!=null&&regularTrip.getHasRegularTripFastestTravelTime()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripFastestTravelTime \""+regularTrip.getHasRegularTripFastestTravelTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripFastestTravelTime()!=null&&regularTrip.getHasRegularTripLastChanged()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripLastChanged \""+regularTrip.getHasRegularTripLastChanged()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (regularTrip.getHasRegularTripTotalDistance()!=null&&regularTrip.getHasRegularTripTotalDistance()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTotalDistance \""+regularTrip.getHasRegularTripTotalDistance()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (regularTrip.getHasRegularTripTotalCount()!=null&&regularTrip.getHasRegularTripTotalCount()>0)
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTotalCount \""+regularTrip.getHasRegularTripTotalCount()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (regularTrip.getHasModalityType().toString()!=null&&!regularTrip.getHasModalityType().toString().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasModalityType \""+regularTrip.getHasModalityType()+"\" .";
				if (regularTrip.getHasRegularTripWeekdayPattern()!=null&&!regularTrip.getHasRegularTripWeekdayPattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripWeekdayPattern \""+regularTrip.getHasRegularTripWeekdayPattern()+"\" .";
				if (regularTrip.getHasRegularTripDayhourPattern()!=null&&!regularTrip.getHasRegularTripDayhourPattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripDayhourPattern \""+regularTrip.getHasRegularTripDayhourPattern()+"\" .";
				if (regularTrip.getHasRegularTripTravelTimePattern()!=null&&!regularTrip.getHasRegularTripTravelTimePattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripTimePattern \""+regularTrip.getHasRegularTripTravelTimePattern()+"\" .";
				if (regularTrip.getHasRegularTripWeatherPattern()!=null&&!regularTrip.getHasRegularTripWeatherPattern().isEmpty())
					query+= "  <"+regularTrip.getHasRegularTripURI()+"> profile:hasRegularTripWeatherPattern \""+regularTrip.getHasRegularTripWeatherPattern()+"\" .";
				query+= "  <"+regularTrip.getHasRegularTripURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
				query+= "  <"+regularTrip.getHasRegularTripURI()+"> rdf:type profile:RegularTrip .";
			}
		}
		query+= "}";
		return query;
	}
	/**
	 * select regular trips associated to a user in the kb
	 * @param uid
	 * @return
	 */
	public static String getRegularTrips(String uid) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?regularTrip ?tripID ?name ?departureTime ?departureTimeSD ?travelTime ?travelTimeSD ?fastestTravelTime ?lastChanged ?totalDistance ?totalCount ?modalityType ?weekdayPattern ?dayhourPattern ?timePattern ?weatherPattern "//?pplace "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "?s profile:hasTransport ?transport. "
					+ "?transport profile:hasRegularTrip ?regularTrip. "
					+ "Optional {?regularTrip profile:hasRegularTripID ?tripID .}"
					+ "Optional {?regularTrip profile:hasRegularTripName ?name .}"
					+ "Optional {?regularTrip profile:hasRegularTripDepartureTime ?departureTime .}"
					+ "Optional {?regularTrip profile:hasRegularTripDepartureTimeSD ?departuretimeSD .}"
					+ "Optional {?regularTrip profile:hasRegularTripTravelTime ?travelTime .}"
					+ "Optional {?regularTrip profile:hasRegularTripTravelTimeSD ?travelTimeSD .}"
					+ "Optional {?regularTrip profile:hasRegularTripFastestTravelTime ?fastestTravelTime .}"
					+ "Optional {?regularTrip profile:hasRegularTripLastChanged ?lastChanged .}"
					+ "Optional {?regularTrip profile:hasRegularTripTotalDistance ?totalDistance .}"
					+ "Optional {?regularTrip profile:hasRegularTripTotalCount ?totalCount .}"
					+ "Optional {?regularTrip profile:hasModalityType ?modalityType .}"
					+ "Optional {?regularTrip profile:hasRegularTripWeekdayPattern ?weekdayPattern .}"
					+ "Optional {?regularTrip profile:hasRegularTripDayhourPattern ?dayhourPattern .}"
					+ "Optional {?regularTrip profile:hasRegularTripTimePattern ?timePattern .}"
					+ "Optional {?regularTrip profile:hasRegularTripWeatherPattern ?weatherPattern .}"
					+" FILTER (STR(?uid) = \""+uid+"\") "
					+ "}";
		return query;
	}
	/**
	 * select regular trip associated to a specific transport of a user in the kb
	 * @param transportURI
	 * @return
	 */
	public static String getRegularTripsForTransport(String transportURI) {
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?regularTrip ?tripID ?name ?departureTime ?departureTimeSD ?travelTime ?travelTimeSD ?fastestTravelTime ?lastChanged ?totalDistance ?totalCount ?modalityType ?weekdayPattern ?dayhourPattern ?timePattern ?weatherPattern "//?pplace "
				+ " where {"
					+ "?transport profile:hasRegularTrip ?regularTrip. "
					+ "Optional {?regularTrip profile:hasRegularTripID ?tripID .}"
					+ "Optional {?regularTrip profile:hasRegularTripName ?name .}"
					+ "Optional {?regularTrip profile:hasRegularTripDepartureTime ?departureTime .}"
					+ "Optional {?regularTrip profile:hasRegularTripDepartureTimeSD ?departuretimeSD .}"
					+ "Optional {?regularTrip profile:hasRegularTripTravelTime ?travelTime .}"
					+ "Optional {?regularTrip profile:hasRegularTripTravelTimeSD ?travelTimeSD .}"
					+ "Optional {?regularTrip profile:hasRegularTripFastestTravelTime ?fastestTravelTime .}"
					+ "Optional {?regularTrip profile:hasRegularTripLastChanged ?lastChanged .}"
					+ "Optional {?regularTrip profile:hasRegularTripTotalDistance ?totalDistance .}"
					+ "Optional {?regularTrip profile:hasRegularTripTotalCount ?totalCount .}"
					+ "Optional {?regularTrip profile:hasModalityType ?modalityType .}"
					+ "Optional {?regularTrip profile:hasRegularTripWeekdayPattern ?weekdayPattern .}"
					+ "Optional {?regularTrip profile:hasRegularTripDayhourPattern ?dayhourPattern .}"
					+ "Optional {?regularTrip profile:hasRegularTripTimePattern ?timePattern .}"
					+ "Optional {?regularTrip profile:hasRegularTripWeatherPattern ?weatherPattern .}"
					+" FILTER (STR(?transport) = \""+transportURI+"\") "
					+ "}";
		return query;
	}
	
	/**
	 * insert Trip preferences of the user in the kb
	 * @param preferenceURI
	 * @param tripPreference
	 * @return
	 */
	public static String setTripPreferences(String preferenceURI, eu.threecixty.profile.oldmodels.TripPreference tripPreference){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		if (tripPreference.getHasTripPreferenceURI()!=null&&!tripPreference.getHasTripPreferenceURI().isEmpty())
			tripPreference.setHasTripPreferenceURI(preferenceURI+"/TripPreference/"+UUID.randomUUID().toString());
	
		query+= "  <"+preferenceURI+"> profile:hasTripPrefernce <"+tripPreference.getHasTripPreferenceURI()+"> .";
		if (tripPreference.getHasPreferredMaxTotalDistance()!=null&&tripPreference.getHasPreferredMaxTotalDistance()>0)
			query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredMaxTotalDistance \""+tripPreference.getHasPreferredMaxTotalDistance()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
		if (tripPreference.getHasPreferredTripDuration()!=null&&tripPreference.getHasPreferredTripDuration()>0)
			query+="  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredTripDuration \""+tripPreference.getHasPreferredTripDuration()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		if (tripPreference.getHasPreferredTripTime()!=null&&tripPreference.getHasPreferredTripTime()>0)
			query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredTripTime \""+tripPreference.getHasPreferredTripTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		if (tripPreference.getHasPreferredCity()!=null&&!tripPreference.getHasPreferredCity().isEmpty())
			query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredCity \""+tripPreference.getHasPreferredCity()+"\" .";
		if (tripPreference.getHasPreferredCountry()!=null&&!tripPreference.getHasPreferredCountry().isEmpty())
			query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredCountry \""+tripPreference.getHasPreferredCountry()+"\" .";
		if (tripPreference.getHasPreferredWeatherCondition()!=null&&!tripPreference.getHasPreferredWeatherCondition().isEmpty())
			query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredWeatherCondition \""+tripPreference.getHasPreferredWeatherCondition()+"\" .";
		if (tripPreference.getHasPreferredMinTimeOfAccompany()!=null&&tripPreference.getHasPreferredMinTimeOfAccompany()>0)
			query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredMinTimeOfAccompany \""+tripPreference.getHasPreferredMinTimeOfAccompany()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
		if (tripPreference.getHasModalityType().toString()!=null&&!tripPreference.getHasModalityType().toString().isEmpty())
			query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasModalityType \""+tripPreference.getHasModalityType().toString()+"\" .";
		query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
		query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> rdf:type profile:TripPreference .";

		query+= "}";
		return query;
	}
	/**
	 * insert multiple Trip preferences of the user in the kb
	 * @param preferenceURI
	 * @param tripPreferences
	 * @return
	 */
	public static String setMultipleTripPreferences(String preferenceURI, Set<eu.threecixty.profile.oldmodels.TripPreference> tripPreferences){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
				+ "   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.TripPreference> iterators = tripPreferences.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.TripPreference tripPreference=iterators.next();
			if (tripPreference.getHasTripPreferenceURI()==null ||tripPreference.getHasTripPreferenceURI().isEmpty())
				tripPreference.setHasTripPreferenceURI(preferenceURI+"/TripPreference/"+UUID.randomUUID().toString());
			
			query+= "  <"+preferenceURI+"> profile:hasTripPrefernce <"+tripPreference.getHasTripPreferenceURI()+"> .";
			if (tripPreference.getHasPreferredMaxTotalDistance()!=null&&tripPreference.getHasPreferredMaxTotalDistance()>0)
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredMaxTotalDistance \""+tripPreference.getHasPreferredMaxTotalDistance()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
			if (tripPreference.getHasPreferredTripDuration()!=null&&tripPreference.getHasPreferredTripDuration()>0)
				query+="  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredTripDuration \""+tripPreference.getHasPreferredTripDuration()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
			if (tripPreference.getHasPreferredTripTime()!=null&&tripPreference.getHasPreferredTripTime()>0)
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredTripTime \""+tripPreference.getHasPreferredTripTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
			if (tripPreference.getHasPreferredCity()!=null&&!tripPreference.getHasPreferredCity().isEmpty())
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredCity \""+tripPreference.getHasPreferredCity()+"\" .";
			if (tripPreference.getHasPreferredCountry()!=null&&!tripPreference.getHasPreferredCountry().isEmpty())
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredCountry \""+tripPreference.getHasPreferredCountry()+"\" .";
			if (tripPreference.getHasPreferredWeatherCondition()!=null&&!tripPreference.getHasPreferredWeatherCondition().isEmpty())
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredWeatherCondition \""+tripPreference.getHasPreferredWeatherCondition()+"\" .";
			if (tripPreference.getHasPreferredMinTimeOfAccompany()!=null&&tripPreference.getHasPreferredMinTimeOfAccompany()>0)
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredMinTimeOfAccompany \""+tripPreference.getHasPreferredMinTimeOfAccompany()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
			if (tripPreference.getHasModalityType().toString()!=null&&!tripPreference.getHasModalityType().toString().isEmpty())
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasModalityType \""+tripPreference.getHasModalityType().toString()+"\" .";
			query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
			query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> rdf:type profile:TripPreference .";
			
		}
		query+= "}";
		return query;
	}
	/**
	 * remove Trip preferences of the user in the kb
	 * @param preferenceURI
	 * @param tripPreference
	 * @return
	 */
	public static String removeTripPreferences(String preferenceURI, eu.threecixty.profile.oldmodels.TripPreference tripPreference){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		if (tripPreference.getHasTripPreferenceURI()!=null&&!tripPreference.getHasTripPreferenceURI().isEmpty()){
			query+= "  <"+preferenceURI+"> profile:hasTripPrefernce <"+tripPreference.getHasTripPreferenceURI()+"> .";
			if (tripPreference.getHasPreferredMaxTotalDistance()!=null&&tripPreference.getHasPreferredMaxTotalDistance()>0)
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredMaxTotalDistance \""+tripPreference.getHasPreferredMaxTotalDistance()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
			if (tripPreference.getHasPreferredTripDuration()!=null&&tripPreference.getHasPreferredTripDuration()>0)
				query+="  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredTripDuration \""+tripPreference.getHasPreferredTripDuration()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
			if (tripPreference.getHasPreferredTripTime()!=null&&tripPreference.getHasPreferredTripTime()>0)
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredTripTime \""+tripPreference.getHasPreferredTripTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
			if (tripPreference.getHasPreferredCity()!=null&&!tripPreference.getHasPreferredCity().isEmpty())
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredCity \""+tripPreference.getHasPreferredCity()+"\" .";
			if (tripPreference.getHasPreferredCountry()!=null&&!tripPreference.getHasPreferredCountry().isEmpty())
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredCountry \""+tripPreference.getHasPreferredCountry()+"\" .";
			if (tripPreference.getHasPreferredWeatherCondition()!=null&&!tripPreference.getHasPreferredWeatherCondition().isEmpty())
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredWeatherCondition \""+tripPreference.getHasPreferredWeatherCondition()+"\" .";
			if (tripPreference.getHasPreferredMinTimeOfAccompany()!=null&&tripPreference.getHasPreferredMinTimeOfAccompany()>0)
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredMinTimeOfAccompany \""+tripPreference.getHasPreferredMinTimeOfAccompany()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
			if (tripPreference.getHasModalityType().toString()!=null&&!tripPreference.getHasModalityType().toString().isEmpty())
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasModalityType \""+tripPreference.getHasModalityType().toString()+"\" .";
			query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
			query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> rdf:type profile:TripPreference .";
		}
		query+= "}";
		return query;
	}
	/**
	 * remove multiple Trip preferences of the user in the kb
	 * @param preferenceURI
	 * @param tripPreferences
	 * @return
	 */
	public static String removeMultipleTripPreferences(String preferenceURI, Set<eu.threecixty.profile.oldmodels.TripPreference> tripPreferences){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
				+ "   prefix profile:<"+PROFILE_URI+">"
				+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
				+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
				+ " { ";
		Iterator <eu.threecixty.profile.oldmodels.TripPreference> iterators = tripPreferences.iterator();
		for ( ; iterators.hasNext(); ){
			eu.threecixty.profile.oldmodels.TripPreference tripPreference=iterators.next();
			if (tripPreference.getHasTripPreferenceURI()!=null&&!tripPreference.getHasTripPreferenceURI().isEmpty()){
				query+= "  <"+preferenceURI+"> profile:hasTripPrefernce <"+tripPreference.getHasTripPreferenceURI()+"> .";
				if (tripPreference.getHasPreferredMaxTotalDistance()!=null&&tripPreference.getHasPreferredMaxTotalDistance()>0)
					query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredMaxTotalDistance \""+tripPreference.getHasPreferredMaxTotalDistance()+"\"^^<http://www.w3.org/2001/XMLSchema#double> .";
				if (tripPreference.getHasPreferredTripDuration()!=null&&tripPreference.getHasPreferredTripDuration()>0)
					query+="  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredTripDuration \""+tripPreference.getHasPreferredTripDuration()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (tripPreference.getHasPreferredTripTime()!=null&&tripPreference.getHasPreferredTripTime()>0)
					query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredTripTime \""+tripPreference.getHasPreferredTripTime()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (tripPreference.getHasPreferredCity()!=null&&!tripPreference.getHasPreferredCity().isEmpty())
					query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredCity \""+tripPreference.getHasPreferredCity()+"\" .";
				if (tripPreference.getHasPreferredCountry()!=null&&!tripPreference.getHasPreferredCountry().isEmpty())
					query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredCountry \""+tripPreference.getHasPreferredCountry()+"\" .";
				if (tripPreference.getHasPreferredWeatherCondition()!=null&&!tripPreference.getHasPreferredWeatherCondition().isEmpty())
					query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredWeatherCondition \""+tripPreference.getHasPreferredWeatherCondition()+"\" .";
				if (tripPreference.getHasPreferredMinTimeOfAccompany()!=null&&tripPreference.getHasPreferredMinTimeOfAccompany()>0)
					query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasPreferredMinTimeOfAccompany \""+tripPreference.getHasPreferredMinTimeOfAccompany()+"\"^^<http://www.w3.org/2001/XMLSchema#long> .";
				if (tripPreference.getHasModalityType().toString()!=null&&!tripPreference.getHasModalityType().toString().isEmpty())
					query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> profile:hasModalityType \""+tripPreference.getHasModalityType().toString()+"\" .";
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
				query+= "  <"+tripPreference.getHasTripPreferenceURI()+"> rdf:type profile:TripPreference .";
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
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?tripPreference ?preferredMaxTotalDistance ?preferredTripDuration ?preferredTripTime ?preferredCity ?preferredCountry ?preferredWeatherCondition ?preferredMinTimeOfAccompany ?modality "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "?s profile:hasPreference ?pref. "
					+ "?pref profile:hasTripPrefernce ?tripPreference. "
					+ "Optional {?tripPreference profile:hasPreferredMaxTotalDistance ?preferredMaxTotalDistance .}"
					+ "Optional {?tripPreference profile:hasPreferredTripDuration ?preferredTripDuration .}"
					+ "Optional {?tripPreference profile:hasPreferredTripTime ?preferredTripTime .}"
					+ "Optional {?tripPreference profile:hasPreferredCity ?preferredCity .}"
					+ "Optional {?tripPreference profile:hasPreferredCountry ?preferredCountry .}"
					+ "Optional {?tripPreference profile:hasPreferredWeatherCondition ?preferredWeatherCondition .}"
					+ "Optional {?tripPreference profile:hasPreferredMinTimeOfAccompany ?preferredMinTimeOfAccompany .}"
					+ "Optional {?tripPreference profile:hasModalityType ?modality .}"
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}

	/**
	 * insert place preference of the user in the kb
	 * @param preferenceURI
	 * @param placePreferenceURI
	 * @return
	 */
	public static String setPlacePreferences(String preferenceURI, String placePreferenceURI ){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		if (placePreferenceURI==null ||placePreferenceURI.isEmpty())
			placePreferenceURI=preferenceURI+"/PlacePreference/"+UUID.randomUUID().toString();
		
		query+= " <"+preferenceURI+"> profile:hasPlacePreference <"+placePreferenceURI+"> .";
		query+= "  <"+placePreferenceURI+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
		query+= "  <"+placePreferenceURI+"> rdf:type profile:PlacePreference .";

		query+= "}";
		return query;
	}
	/**
	 * insert multiple place preferences of the user in the kb
	 * @param preferenceURI
	 * @param placePreferenceURIs
	 * @return
	 */
	public static String setMultiplePlacePreferences(String preferenceURI, Set<String> placePreferenceURIs ){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "  INSERT INTO GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <String> iterators = placePreferenceURIs.iterator();
		for ( ; iterators.hasNext(); ){
			String placePreferenceURI=iterators.next();
			if (placePreferenceURI!=null&&!placePreferenceURI.isEmpty()){
				query+= " <"+preferenceURI+"> profile:hasPlacePreference "+placePreferenceURI+" .";
				query+= "  <"+placePreferenceURI+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
				query+= "  <"+placePreferenceURI+"> rdf:type profile:PlacePreference .";
			}
		}
		query+= "}";
		return query;
	}
	/**
	 * remove place preference of the user in the kb
	 * @param preferenceURI
	 * @param placePreferenceURI
	 * @return
	 */
	public static String removePlacePreferences(String preferenceURI, String placePreferenceURI ){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		if (placePreferenceURI!=null){
			query+= " <"+preferenceURI+"> profile:hasPlacePreference <"+placePreferenceURI+"> .";
			query+= "  <"+placePreferenceURI+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
			query+= "  <"+placePreferenceURI+"> rdf:type profile:PlacePreference .";
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
	public static String removeMultiplePlacePreferences(String preferenceURI, Set<String> placePreferenceURIs ){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
			+ "   DELETE FROM GRAPH <"+ virtuosoConnection.GRAPH+">"
			+ " { ";
		Iterator <String> iterators = placePreferenceURIs.iterator();
		for ( ; iterators.hasNext(); ){
			String placePreferenceURI=iterators.next();
			if (placePreferenceURI!=null&&!placePreferenceURI.isEmpty()){
				query+= " <"+preferenceURI+"> profile:hasPlacePreference <"+placePreferenceURI+"> .";
				query+= "  <"+placePreferenceURI+"> rdf:type <http://www.w3.org/2002/07/owl#NamedIndividual>.";
				query+= "  <"+placePreferenceURI+"> rdf:type profile:PlacePreference .";
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
		String query="prefix profile:<"+PROFILE_URI+"> "
				+ "prefix vcard:<http://www.w3.org/2006/vcard/ns#> "
				+ "prefix foaf:<http://xmlns.com/foaf/0.1/>"
				+ "select ?placePreference ?placeDetailPreference "
				+ " where {"
					+ "?s a profile:UserProfile. "
					+" ?s profile:hasUID ?uid. "
					+ "?s profile:hasPreference ?pref. "
					+ "?pref profile:hasPlacePreference ?placePreference. "
					+ "Optional {?placePreference profile:hasPlaceDetailPreference ?placeDetailPreference .}"
					+" FILTER (STR(?uid) = \""+uid+"\") "//100900047095598983805
					+ "}";
		return query;
	}

	/**
	 * insert place Details preference of the user in the kb
	 * @param placePreferenceURI
	 * @param placeDetailPreference
	 * @return
	 */
	public static String setPlaceDetailPreference(String placePreferenceURI, eu.threecixty.profile.oldmodels.PlaceDetailPreference placeDetailPreference){
		String query="prefix vcard:<http://www.w3.org/2006/vcard/ns#>"
			+ "   prefix profile:<"+PROFILE_URI+">"
			+ "   prefix rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
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
	/**
	 * remove place detail preferences of the user in the kb
	 * @param placePreferenceURI
	 * @param placeDetailPreference
	 * @return
	 */
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
	/**
	 * select place detail preferences of the user in the kb
	 * @param uid
	 * @return
	 */
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
	/**
	 * select place detail preferences associated to a URI 
	 * @param uri
	 * @return
	 */
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
}
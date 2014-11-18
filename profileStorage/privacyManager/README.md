
Privacy Manager
=========


### Final goal
Privacy Manager will be the 3cixty infrastructure component holding the private knowledge base of user profiles and managing the access controls.

### Current features
  
* Storage and retrieval of user profiles
* Five programs test that show how to use the profile storage and privacy APIs
* Application and user privacy contract definition
* Implementation of the privacy contract request api that allow to request a privacy contract certification, update a privacy contract and get the current status of the request
* Implementation of storage of the user privacy contract
* Implementation of privacy management during user profiles retrieval
* A WEB application that allows to generate privacy contract for application with a simple interface
* A GUI for the privacy certification authority, in order to accept or refuse privacy contracts

# Last Release Version
----------

#### Identification
Privacy Manager version 1.2

#### Maven
In order to use the module in other Maven projects add the following dependency in the project's pom.xml:

     <dependency>
    	<groupId>eu.3cixty.privacy</groupId>
    	<artifactId>privacymanager</artifactId>
    	<version>1.2</version>
     </dependency>
   

# Changes log
----------

## Version 1.2
This version contains the modifications required by INRIA during the last integration meeting:

 -  groups definitions restricted to `SELF` (data access for a user is therefore limited to its own data)
 -  the `getAllUsersIDs` API is callable in an anonymous session (no user authentification required)
 -  all certification cycle handling is performed based on the `application id` (aka the app key)  
 -  new API `getCertificate()` for accessing the generated certificate
 -  the definition of the property pathes in the privacy contract, now contains an `additional attribute "label"`, which contain a textual description that can be presented to the user for approval
 -  a `privacy certification authority application` is provided to manage privacy contracts acceptance or refusal
 
Other modifications :

 -  re-organization of some APIs packaging (session creation and profile manager access)
 -  the privacy contract generator has been updated to cope with the new xsd definition 

### How to launch the Privacy Authority GUI

You can use the `privacy-admin-gui-launcher.bat` or `privacy-admin-gui-launcher.sh` scripts that are provided in the `privacyManager\utility\privacy-admin-gui` folder.

The program needs one argument : the path of the properties file used to configure the privacy DB (see `3CixtyPrivacyAuthority.properties`).

On program startup, the private key and certificate of the privacy autority are required in order to generate to sign the CSR in case of acceptance of a privacy contract request.

### Creation of an anonymous session

		ThreeCixtyFactory profileFactory = ThreeCixtyFactory.getInstance();
		Service service = profileFactory.getService( "ExploreMI 360", "1.0");
		Session 
			session = profileFactory.getSession( profileFactory.getAuthenticator(service, null) );


## Version 1.1

This version contains the implementation of the user profile retrieval respecting the rules of privacy defined between each user and applications used by the user.
 
An improvement of the PrivacyDB has also been done in order to facilitate the creation and reset of the base.

 
### How to use the PrivacyManager in order to ensure data privacy

####**1. Privacy contract database creation**
An application that wants to access the users profile must be registered in the privacy database.

So the first thing to do, is to create the privacy db.
You can do this with the following code :

    PrivacyAuthorityConf.setPropertyFile("3CixtyPrivacyAuthority.properties");
    PrivacyDBInitialize.resetAndInit(oldPasswordSA, newPasswordSA, 
									 passwordAPI, passwordAdmin);
  
`3CixtyPrivacyAuthority.properties` is the properties file in which you could configure the directory where the db will be created, and the fact that the db will be encrypted or not (uselful for debug). The encrypted mode must be prefered.

Below, an example of the `3CixtyPrivacyAuthority.properties` file

    # the path of the db where are stored the certification requests
    PrivacyAuthority.db.path=../../3cixty/DBPr
    
    # Ask for an encrypted DB or not.
    #Valid values are : true | false
    PrivacyAuthority.db.encrypted=true

The database creation needs 4 passwords :

 - the 1st is the password of the SA with which the previous database has been created 
 - the 2nd is the new password of the SA
 - the 3rd is the password that will be used by the registration API to store the privacy contract requests of the application
 - the 4th is the password  that will be used by the administration API (Certification authority) to accept or refuse the requests.

####**2. Application registration** 
The privacy contract registration request is done by the `CertificationAndPrivacyRequest` API.
This API could be accessed by the `PrivacyCertAuthorityFactory` by providing the configured password of the registration API (the 3rd).

Then the request is made by calling the `certifyMyContract` method.  
This operation needs 3 arguments :  
 - a `CertificationAndPrivacyRequest.PocInformation` that contains the information about the person to contact to have more information about the request.  
 - a `CRS` (Certification Request Signing) that could be generated with OpenSSL  
 - a `privacy contract`. This contract is an XML file that follows the schema privacyContract.xsd.

**A WEB application `simplePcGenService-1.1.war` is provided in this release, that allows to simply produce a privacy contract from a WEB page, with some typical features of the 3Cixty user profile.** 
 
If one of those arguments is not valid, the request will be immedialtly rejected.

Here, an code example on how to ask for a certification by the preivacy authority :

		CertificationAndPrivacyRequest auth = null;
		try {
			
			auth = PrivacyCertAuthorityFactory.build( passwordAPI );
			
			CertificationAndPrivacyRequest.PocInformation 
				poc = new CertificationAndPrivacyRequest.PocInformation("3cixty", "poalo sino", "poalo.sini@tin.it", "+336728972872");
			
			FileInputStream certificateSigningRequest;
			FileInputStream privacyContract;
			
			certificateSigningRequest = new FileInputStream("./src/test/resources/exploreMi360.csr");
			privacyContract = new FileInputStream("./src/test/resources/PrivacyContract_ExploreMi360_example.xml");

			reqId = auth.certifyMyContract(poc, certificateSigningRequest , privacyContract);
			assertNotNull( reqId.toString()  );
			
			
		} catch (FileNotFoundException e2) {
			fail( e2.getMessage() );
		}
		catch (PrivacyException e) {
			fail( e.getMessage() );
		}
		finally {
			if ( auth != null ) {
				auth.terminate();
			}
		}		

Don't forget the call the `terminate` method at the end to close the connection to the db.
 
Now, the application certification request is PENDING.
 
####**3. Certification authority**
Once an application has deposed its certification request, the certification authority must accept or refuse it.

For this, you could use the `Administrator` class.

    Administrator admin = new Administrator(passwordAdmin);
    admin.acceptRequest( reqId, certificateCA, privateKeyCA );
	admin.terminate();

The `passwordAdmin` is the password provided durng the privacy db creation as certification authority password.
  
`certificateCA` and `privateKeyCA`are respectively the X509 certificate and the private key of the certification authority, used to sign the CSR of the application.

Don't forget the call the `terminate` method at the end to close the connection to the db.

####**4. Contract between the user and an application**
When a User wants to use an application that uses the KB, this application must established a contract with the User.

**If no contract is established, the application will access no data of the user profile.**

The **User privacy Contract (UPC)** is an XML file that follows the `UserprivacyContract.xsd` schema.

To register the UserPrivacyContract, use the `PrivacyContractStorage` API that you could access by the following code :

    PrivacyAuthorityConf.setPropertyFile( privacyPropertyFilePath );
    FileInputStream is = new FileInputStream( "src/test/resources/UPC_ExploreMi360.xml" );
    UserPrivacyContract upc = PrivacyContractFactory.buildUserPrivacyContract( is );
    PrivacyContractStorageFactory.getInstance().store( 	
		userID, 
		PrivacyCertAuthorityTool.buildserviceID4Application(appName, appversion), 
		upc);

where appName and appVersion are respectively the application name and version provided in the application privacy contract.

####**5. User profile access**
Nothing has changed to get the profile of a user.
You can use the `getProfile` or `getProfileProperties` of the `ProfileManager` API.

But now the result could be different according to the privacy contract established between the user and the application.

In case where the application will not have access to some part of the user profile, no error will be displayed or thrown.  
The contents of the user profile will be filtered, and only the data accessible by the application will be returned.

####**6. SimplePrivacyContractGeneratorService web application**

A web application providing the SimplePrivacyContractGeneratorService is provided as a war file : `simplePcGenService-1.1.war`.
This service aims at generating coarse-grained privacy contracts by providing a way to
define required user profile domains.

Let’s suppose that the war is deployed in an app server configurated to serve it at path http://localhost:8080/ :

The WADL rest service definition file can be obtained at : http://localhost:8080/rest/application.wadl

The xml extract that define the service itself is as follow:

    <method id="generateContract" name="POST">
    <request>
    <representation mediaType="application/x-www-form-urlencoded">
    <param name="App_name" style="query" type="xs:string"/>
    <param name="App_description" style="query" type="xs:string"/>
    <param name="App_version" style="query" type="xs:string"/>
    <param name="App_author" style="query" type="xs:string"/>
    <param name="App_type" style="query" type="xs:string"/>
    <param name="domain" style="query" type="xs:string"/>
    <param name="data" style="query" type="xs:string"/>
    </representation>
    </request>
    <response>
    <representation mediaType="text/xml"/>
    </response>
    </method>

The REST service itself is present at : http://localhost:8080/rest/PrivacyContract
An example HTML page that contains a FORM and calls the service is present at : http://localhost:8080/

Valid strings for App_type and domain parameters should follow xsd definitions 
(see package org.theresis.humanization.privacy.generated AppType.java and Domain.java )

`App_type` : Web, Android, IOS, Windows Phone

`domain` : Communication, Cultural, Education, Entertainment, Financial, Food, Health, Religion, Sport, Tourism, Transport, Other

Valid strings for data parameter are : 

`data`: Profile, User, Tray, Friends, Queries, Preferences, Social

####**7. Privacy contracts contents**

Namespaces definition: namespaces that are used in the following property pathes have to be defined.

For instance, a partial definition may be :

    <pvc:namespaces>
    <pvc:namespace>
    <pvc:prefix>rdf</pvc:prefix>
    		<pvc:uri>http://www.w3.org/1999/02/22-rdf-syntax-ns#</pvc:uri>
    	</pvc:namespace>
    	<pvc:namespace>
    		<pvc:prefix>foaf</pvc:prefix>
    		<pvc:uri>http://xmlns.com/foaf/0.1/</pvc:uri>
    	</pvc:namespace>
    	…
    </pvc:namespaces>

Property paths definition :

They are used to defined the parts of the graph which access is required by an application in Privacy Contracts,
and which is allowed by users in User Privacy COntracts.

They correspond to an extension of classical SPARQL property path, and may contain logical conditions.
These conditions are aimed to enable application users to further restrict the access to some parts of the data graph
based on their privacy criteria.

The property paths that may be used in privacy contract should follow the following rules :

-	they are composed of a sequence of “/” separated  URIs (like regular SPARQL property paths).
-	the final “.” is optional
-	they may contain a conditional part defined in brackets after each property
-	the condition part may be composed of several “;” separated clauses that will be logically ANDed
-	each condition may end with a literal value, or with a logical test (the allowed operators are  !=, =, <, <=, >, >= )

for instance, the following path application required path :
profile:hasPreference/ profile:hasUserHotelRating / …
may be accepted by the user with the restricted form :
profile:hasPreference/ profile:hasUserHotelRating [profile:hasRating/ profile:hasUserDefinedRating != "4.0"^^xsd:double ; profile:hasHotelDetail/ profile:hasPlaceName “this name” ] / …
which will limit the profile:hasUserHotelRating property trasversal to preferences that have a rating different from 4.0 and with a place with the given name 
and thus will restrict the application reachable data graph.

## Version 1.0
  
This version contains the implementation of APIs introduced in version 0.4 to manage privacy in the profile storage component.
As these APIs have not been subject of a review by the project, we have implemented them unchanged, 
except for minor adjustments due to technical reasons.


There's one modification of naming in this version :   

`CertificationAndPrivacyAuthority becomes CertificationAndPrivacyRequest`

A new test file is provided, ```PrivacyAuthorityTest.java````, that shows how to use the APIs and also provides a test of this implementation.

      
##### Details of API implementation

* **CertificationAndPrivacyRequest API**  
The implementation uses a database to store the certification requests.
Thus, before using the CertificationAndPrivacyRequest API, you need to create the database with the following method :

			InitializePrivacyDB.doTheJob( String login, String password, String DBCryptkey)

	Currently, only the login **"SA"** with password **""** is allowed.

 	**DBCryptKey** is a key used to encrypt the database used by the privacy authority module to store the requests.  
	If the DBCryptKey is NULL, the database will not be encrypted.
	A valid random encrypting key could be generated with the following API
   
			CryptKeyGenerator.doTheJob

	The location of the database must be defined in a configuration file.  
	An example of the configuration file is provided in the testProject\src\test\resources, **3CixtyPrivacyAuthority.properties** :
 
	    	# the path of the db where are stored the certification requests
    		PrivacyAuthority.db.path=./3cixty/DB/privacydb

	And the configuration file path must be provided to privacy-authority mmodule with the fallowing method call :  

			PrivacyAuthorityConf.setPropertyFile(String propertyFilePath)
										 		  
	Then, a factory method is provided to create the implementation of the CertificationAndPrivacyRequest API :    

     	CertificationAndPrivacyRequest build( String login, String password, String DBCryptKey )
      
 	Currently, only the login **"SA"** with password **""** is allowed.

		

* **PrivacyContractStorage API**  
A factory method is provided to create the implementation :
  
		PrivacyContractStorage PrivacyContractStorageFactory.getInstance( )    
This API must be used to store and retrive user privacy contract.
  
## Version 0.4  
  
This new version contains the APIs definition to manage privacy in the profile storage component.  
Two main APIs :  

* **CertificationAndPrivacyAuthority**  
This API allow an application to ask for its registration in the 3Cixty platform. 
It must provides its privacy contract.  
A privacy contract must follow the format defined by the **PrivacyContract.xsd** schema.

* **PrivacyContractStorage**  
This API is used to retrieve applications' privacy contracts and store and retrieve users' privacy contract (contract established the user and an application)

Those APIs are documented in java files.  
The implementation of those APIs will be provided in future versions.

Note: 
The privacy APIs definition is also available in its own jar file: privacy-api-0.0.4.jar,
which does correspond to the delta between version 0.3 and 0.4 of privacy manager.

## Version 0.3

* Implements the merge/replace/delete profile with property path methods :
  *  mergeProfileProperties
  *  replaceProfileProperties
  *  deleteProfileProperties
* Improvement of the model validity : now a check is done on input data for merge or replace profile
* Modification of the getProfile behavior : previously, the method only returns the properties directly defined on the userProfile. Now, the subtree is returned.  
This behavior could be disabled by setting the option   ```ProfileStorage.jsonld.option.recursive``` to ```false``` in the configuration file
* [Issue #21][4] : the configuration option ```ProfileStorage.jsonld.option.compact``` was not taken into account. 

## Version alpha 0.2

* Implements the get profile with property path method, ```getProfileProperties```, defined by interface ```ProfileManager```
* Renamed package names pertaining to products issued by Theresis. As a consequence, the package ```eu.threecixty.privacy``` is now known as ```org.theresis.humanization```
* [Issue #18][3] : Modification of the JSON-LD output format in order to do not compact property values.
All property values are provided as arrays. 

## Version alpha 0.1.1
* repackaging

## Version alpha 0.1

* This initial release supports only full reading and writing of user profiles in memory.
* Property paths based interfaces are not implemented.
* No provacy management at all
* No security

# Tech
----
Privacy Manager uses a number of open source projects to work properly:

* [Jena] - A free and open source Java framework for building Semantic Web and Linked Data applications

The module supports [JSON-LD] and [SPARQL] 1.1 [Property Path][1]

# Installation
----

Installation is performed during the install Maven phase.

The artifact can also be manually installed in local Maven repository:

```mvn install:install-file -Dfile=libs/privacymanager-0.4.jar -DpomFile=libs/privacymanager-0.4.pom```

For more commands and install options see [Installing 3rd party library][2]

# Usage
------

### Configuration

##### Profile Storage

Copy the file ```res/3CixtyProfileStorage.properties``` and edit the copy in order to set its properties.

The default file looks like this:

    # the path of the ontology that represents the user profile
    ProfileStorage.ontology.path=./UserProfileKBmodel.rdf
    
    # the namespace of the previous ontology
    ProfileStorage.ontology.ns=http://www.eu.3cixty.org/profile#
    
    # Concept that is the user profile in the ontology (with no namespace)
    ProfileStorage.ontology.profile.concept=UserProfile
    
    # the property that is the key for user profile
    ProfileStorage.ontology.profile.key=hasUID
    
    # JSON-LD Option for property values. If true, when a property has 
	# only one value, the output is not an array
    # example : "hasGender" : "Male" versus "hasGender" : ["Male"]
    # Valid values are : true | false 
    ProfileStorage.jsonld.option.compact=false
    
    # JSON-LD Option for individual writting : If false, only direct statements 
	# of the individual are written. Otherwise, a recursive parsing is done to 
	# output all linked individuals
    # Valid values are : true | false 
    ProfileStorage.jsonld.option.recursive=true
    
Set the property ```ProfileStorage.ontology.path``` in the configuration file to specify the path to the ontology.

##### Privacy authority
Copy the file ```res/3CixtyPrivacyAuthority.properties``` and edit the copy in order to set its properties.  

The default file looks like this:

	# the path of the db where are stored the certification requests
	PrivacyAuthority.db.path=./3cixty/DB/privacydb

Set the property ```PrivacyAuthority.db.path``` in the configuration file to specify the path where the privacy authority will store its database.


### Main API interfaces

The main interfaces are:

* org.theresis.humanization.datastorage.ProfileManager
* org.theresis.humanization.datastorage.ProfileManagerFactory
* org.theresis.humanization.auth.Authenticator
* org.theresis.humanization.auth.Service
* org.theresis.humanization.auth.Session
* org.theresis.humanization.auth.SessionManager
* org.theresis.humanization.privacy.CertificationAndPrivacyRequest
* org.theresis.humanization.privacy.PrivacyContractStorage
* org.theresis.humanization.privacy.UserPrivacyContract
* org.theresis.humanization.privacy.generated.PrivacyContract


### Main implementation classes
Implementation classes provided in the distribution:  

* ```ProfileManagerFactory``` is implemented by ```org.theresis.humanization.profilestore.SimpleProfileManagerFactory```
* ```SessionManager``` is implemented by ```org.theresis.humanization.auth.simple.SimpleSessionManager```
* ```UserPrivacyContract```and ```PrivacyContract``` are implemented
* ```CertificationAndPrivacyRequest``` could be obtained by the  factory ```PrivacyCertAuthorityFactory```
* ```PrivacyContractStorage``` could be obtained by the  factory ```PrivacyContractStorageFactory```
* Other implementations are hidden and are obtained using the factory and manager. They are specific to the general implementation of the privacy framework.

#### Principles of use

*The security model is yet to be implemented. That's why the subject is openely discussed in this documentation*

The main operations are provided by the interface ```ProfileManager```. A ProfileManager is created using the factory ```ProfileManagerFactory```'s method ```getProfileManager(String)```. The method's argument is the path of the configuration file to be parsed.

Every method of ```ProfileManager``` requires a valid ```Session``` and sessions are requested from a ```SessionManager```. To open a session, you need to pass an ```Authenticator``` to ```ProfileManagerFactory```'s method ```getAuthenticator```. Each authenticator is tight to a requester service and user. Both of them are mandatory.


Services are represented by the interface ```Service``` and are addressed using ```ProfileManagerFactory.getService(String, String)```. The arguments are specific to the services security model implemented. They can be the service ID and an App Key.

Before authentication, a user typicaly consists of a pair of user name and credential, whose representation also depends on the user security model implemented. After authentication, a user should be only referenced by a unique identifier, possibly specific to the session.

A end-user can be anonymous. If the request is emanating from a software component or a service instead of a end-user (for example, a dataminer), the user specified for the sesssion must be specific to this component or be the system user (and cannot be anonymous)

#### Examples

##### Access to user Profile
The following code shows how to query all of the User identifiers from the KB:


    import java.util.Set;
    
    import org.theresis.humanization.authen.simple.SimpleSessionManager;
    import org.theresis.humanization.profilestore.SimpleProfileManagerFactory;
    
    import org.theresis.humanization.authen.*;
    import org.theresis.humanization.datastorage.*;
    
    try {
    ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance();
    String propertyPath = "C:/3cixty/config/3CixtyProfileStorage.properties";
    ProfileManager profileMgr = profileFactory.getProfileManager( propertyPath );
    
    // Get a reference on the dataminer service
    Service service = profileFactory.getService( "http://3cixty/dataminer",
    												"kACAH-1Ng1MImB85QDSJQSxhqbAA7acjdY9pTD9M" );
    
    // Get an authentication token
    Authenticator 
		auth = profileFactory.getAuthenticator(service,
    											"root",
												"admin",
												null ); // no additional security/protocol option
    
    // Open a session for the dataminer.
    // In this particular case, the requesting user is the system and not an end-user thus the
    // need for the user 'root'.
    Session session = SimpleSessionManager.getInstance().getSession( auth );
    
    // Get the list of users
    Set<String> userIDS = profileMgr.getAllUsersIDs( session );
    
    } catch (Exception e ) {
    	e.printStackTrace();
    }


##### Access to user profile properties
The following code shows how to format a property path to get the gender of the user

		String hasGenderPath = ":hasGender";
		List<StringpropertyPaths = new ArrayList<String>();
		propertyPaths.add( hasGenderPath );
			
		Collection<ValuedProperty
			propertyValues =  profileMgr.getProfileProperties(	currentSession, 
																userID, 
																propertyPaths);	
	
The following code shows how to format a property path to get the user account ID of all relations of the user

		String knowsPath = "foaf:knows/:hasProfileIdentities*/:hasUserAccountID";
		List<StringpropertyPaths = new ArrayList<String>();
		propertyPaths.add( knowsPath );
			
		Collection<ValuedProperty
			propertyValues =  profileMgr.getProfileProperties(	currentSession, 
																userID, 
																propertyPaths);	

##### Privacy contract definition
The privacy contract of an application could be defined by code or by an XML file.  
The following file is an example of what could be the privacy contract for the 3cixty ExploreMi360 application (that wants to access to the whishList) :
 
    <pvc:PrivacyContract schemaVersion="1.0" xsi:schemaLocation="eu.3cixty.privacy PrivacyContract.xsd" xmlns:pvc="eu.3cixty.privacy" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    	<pvc:application>
    		<pvc:name>ExploreMi 360</pvc:name>
    		<pvc:description>...</pvc:description>
    		<pvc:version>...</pvc:version>
    		<pvc:author>....</pvc:author>
    		<pvc:domains>
    			<pvc:domain>Tourism</pvc:domain>
    			<pvc:domain>Food</pvc:domain>
    			<pvc:domain>Entertainment</pvc:domain>
    		</pvc:domains>
    	</pvc:application>
    	<pvc:contract>
    		<pvc:namespaces>
    			<pvc:namespace>
    				<pvc:prefix>profile</pvc:prefix>
    				<pvc:uri>http://3cixty.eurecom.fr/ontology/profile/</pvc:uri>
    			</pvc:namespace>
    		</pvc:namespaces>
    		<pvc:propertyPaths>
    			<pvc:propertyPath pvc:type="optional">profile:trayElement</pvc:propertyPath>
    		</pvc:propertyPaths>
    	</pvc:contract>
    </pvc:PrivacyContract>

Here is an other example for an application that would access to the whish list, but that would get only restaurant items.  

    <pvc:PrivacyContract schemaVersion="1.0" xsi:schemaLocation="eu.3cixty.privacy PrivacyContract.xsd" xmlns:pvc="eu.3cixty.privacy" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    	<pvc:application>
    		<pvc:name>ExploreMi 360</pvc:name>
    		<pvc:description>...</pvc:description>
    		<pvc:version>...</pvc:version>
    		<pvc:author>....</pvc:author>
    		<pvc:domains>
    			<pvc:domain>Tourism</pvc:domain>
    			<pvc:domain>Food</pvc:domain>
    			<pvc:domain>Entertainment</pvc:domain>
    		</pvc:domains>
    	</pvc:application>
    	<pvc:contract>
    		<pvc:namespaces>
    			<pvc:namespace>
    				<pvc:prefix>profile</pvc:prefix>
    				<pvc:uri>http://3cixty.eurecom.fr/ontology/profile/</pvc:uri>
    			</pvc:namespace>
    		</pvc:namespaces>
    		<pvc:propertyPaths>
    			<pvc:propertyPath pvc:type="optional">profile:trayElement[ profile:itemType "Restaurant"]</pvc:propertyPath>
    		</pvc:propertyPaths>
    	</pvc:contract>
    </pvc:PrivacyContract>

##### Encrypting key generation
The following code shows how to get an encrypting key for the initialization of the privacy authority database

	String cryptKey = CryptKeyGenerator.doTheJob()

##### Initialization of the Privacy Authority database
The following code shows how to initialize the privacy authority database with no encryption.
	
	InitializePrivacyDB.doTheJob("SA", "", null);

The following code shows how to initialize the privacy authority database with encryption, where the key has been generated by the previous code.

	InitializePrivacyDB.doTheJob("SA", "", "23ee4f98ea895a52ae6e3e2de9081964");

##### Reset of the Privacy Authority database
In case of problem, you could reset the privacy authority database with this code :

	ResetPrivacyDB.doTheJob("SA", "", "23ee4f98ea895a52ae6e3e2de9081964");
The encrypting key must be those used to create the database.

##### Privacy contract registration request
The following code shows how to ask for the registration of an applictaion  privacy contract.
The encrypting key must be equal to those used to create the table.

		CertificationAndPrivacyRequest auth = null;
		String	cryptKey		= "23ee4f98ea895a52ae6e3e2de9081964";
		try {
			
			auth = PrivacyCertAuthorityFactory.build( "SA", ##, cryptKey);
			
			CertificationAndPrivacyRequest.PocInformation 
				poc = new CertificationAndPrivacyRequest.PocInformation("3cixty", "poalo sino", "paolo.sini@tin.it", "+336728972872");
			
			FileInputStream certificateSigningRequest;
			FileInputStream privacyContract;
			
			certificateSigningRequest = new FileInputStream("./src/test/resources/exploreMi360.csr");
			privacyContract = new FileInputStream("./src/test/resources/PrivacyContract_ExploreMi360_example.xml");

			reqId = auth.certifyMyContract(poc, certificateSigningRequest , privacyContract);
			assertNotNull( reqId.toString()  );
			
			
		} catch (FileNotFoundException e2) {
			fail( e2.getMessage() );
		}
		catch (PrivacyException e) {
			fail( e.getMessage() );
		}
		finally {
			if ( auth != null ) {
				auth.terminate();
			}
		}		

##### User privacy contract storage

A user privacy contract could be build and store with the following code :

		PrivacyContract appPrivacyContract = PrivacyContractFactory.buildPrivacyContract( pvcFilePath );
		Service aService = profileFactory.getService( appPrivacyContract.getApplication().getName(), "myAuthent" );
		try {			
			session = SimpleSessionManager.getInstance().getSession( profileFactory.getAuthenticator( aService, "U2678", "pwd", null ) );			
		} catch ( Exception e ) {

			fail("Exception caught when creating session for user " + "U2678" );
		}

		UserPrivacyContract	aUserPrivacyContract = new UserPrivacyContract( appPrivacyContract );
		PrivacyContractStorage pvcStorage = PrivacyContractStorageFactory.getInstance();
		pvcStorage.store( session.getUser(), aService, aUserPrivacyContract );


##### How to generate a CSR with OpenSSL

* Generate your private key:  
 	`   openssl genrsa –out myapp.key 1024`  
=> ```myapp.key``` is your private key. Keep it secret.


* Generate the certificate request   
	`	openssl req -new -key myapp.key > myapp.csr`  
Fill the required fields and be carefull with the common name that must be the URL of your server.  
Please fill the email address in order to get back the certificate.  
=> ```myapp.csr``` is your CSR. Send it to the certifictaion authority.


**Advice**  
Property path use the SPARQL1.1 Property Path format with some restrictions :
 
- do not specify variables at the beginning and end of the property path (?x)
- Do not use the ```^:propertyName``` form
- Only use namespaces defined in the ontology (for default prefix use ```:propertyName``` ) 

License
----

Copyright (c) 2014 Thales Services, All rights Reserved.

[jena]: https://jena.apache.org/
[json-ld]: http://json-ld.org/
[sparql]: http://www.w3.org/TR/rdf-sparql-query/
[1]: http://www.w3.org/TR/sparql11-property-paths/
[2]: http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html
[3]: https://github.com/3cixty/profileStorage/issues/18
[4]: https://github.com/3cixty/profileStorage/issues/21


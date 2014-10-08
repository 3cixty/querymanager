Privacy Manager
=========
**Final goal:**
Privacy Manager will be the 3cixty infrastructure component holding the private knowledge base of user profiles and managing the access controls.

**Current features**
  
* Storage and retrieval of user profiles
* Two programs test that show how to use the profile storage APIs
* Application and user privacy contract definition

Last Release Version
----
#### Identification
Privacy Manager version 0.4

#### Maven
In order to use the module in other Maven projects add the following dependency in the project's pom.xml:

     <dependency>
    	<groupId>eu.3cixty.privacy</groupId>
    	<artifactId>privacymanager</artifactId>
    	<version>0.4</version>
     </dependency>

Changes log
----

#### version 0.4
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

#### version 0.3

* Implements the merge/replace/delete profile with property path methods :
  *  mergeProfileProperties
  *  replaceProfileProperties
  *  deleteProfileProperties
* Improvement of the model validity : now a check is done on input data for merge or replace profile
* Modification of the getProfile behavior : previously, the method only returns the properties directly defined on the userProfile. Now, the subtree is returned.  
This behavior could be disabled by setting the option   ```ProfileStorage.jsonld.option.recursive``` to ```false``` in the configuration file
* [Issue #21][4] : the configuration option ```ProfileStorage.jsonld.option.compact``` was not taken into account. 

#### version alpha 0.2

* Implements the get profile with property path method, ```getProfileProperties```, defined by interface ```ProfileManager```
* Renamed package names pertaining to products issued by Theresis. As a consequence, the package ```eu.threecixty.privacy``` is now known as ```org.theresis.humanization```
* [Issue #18][3] : Modification of the JSON-LD output format in order to do not compact property values.
All property values are provided as arrays. 

#### version alpha 0.1.1
* repackaging

#### version alpha 0.1

* This initial release supports only full reading and writing of user profiles in memory.
* Property paths based interfaces are not implemented.
* No provacy management at all
* No security

Tech
-----------
Privacy Manager uses a number of open source projects to work properly:

* [Jena] - A free and open source Java framework for building Semantic Web and Linked Data applications

The module supports [JSON-LD] and [SPARQL] 1.1 [Property Path][1]

Installation
-----------

Installation is performed during the install Maven phase.

The artifact can also be manually installed in local Maven repository:

```mvn install:install-file -Dfile=libs/privacymanager-0.4.jar -DpomFile=libs/privacymanager-0.4.pom```

For more commands and install options see [Installing 3rd party library][2]

Usage
-----------

#### Configuration

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

#### Main API interfaces

The main interfaces are:

* org.theresis.humanization.datastorage.ProfileManager
* org.theresis.humanization.datastorage.ProfileManagerFactory
* org.theresis.humanization.auth.Authenticator
* org.theresis.humanization.auth.Service
* org.theresis.humanization.auth.Session
* org.theresis.humanization.auth.SessionManager
* org.theresis.humanization.privacy.CertificationAndPrivacyAuthority
* org.theresis.humanization.privacy.PrivacyContractStorage
* org.theresis.humanization.privacy.UserPrivacyContract
* org.theresis.humanization.privacy.generated.PrivacyContract


#### Main implementation classes
Implementation classes provided in the distribution:  

* ```ProfileManagerFactory``` is implemented by ```org.theresis.humanization.profilestore.SimpleProfileManagerFactory```
* ```SessionManager``` is implemented by ```org.theresis.humanization.auth.simple.SimpleSessionManager```
* ```UserPrivacyContract```and ```PrivacyContract``` are implemented
* Other APIs of ```org.theresis.humanization.privacy``` package currently have not implementation
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
The privacy contract of an applictaion could be defined by code or by an XML file.  
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

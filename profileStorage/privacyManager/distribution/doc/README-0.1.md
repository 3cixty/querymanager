Privacy Manager
=========
Privacy Manager is the component holding the private knowledge base of user profiles and managing the access controls.

Features:

  - User-centric privacy of profiles
  - Fully semantical
  - Semi-opened privacy framework for service providers
  - Security management

Version
----
Privacy Manager version 0.3

#### Maven
In order to use the module in other Maven projects add the following dependency in the project's pom.xml:


    <dependency>
    	<groupId>eu.3cixty.privacy</groupId>
    	<artifactId>privacymanager</artifactId>
    	<version>0.3</version>
    </dependency>


Tech
-----------
Privacy Manager uses a number of open source projects to work properly:

* [Jena] - A free and open source Java framework for building Semantic Web and Linked Data applications

The module supports [JSON-LD] and [SPARQL] 1.1 [Property Path][1]

Installation
-----------

Installation is performed during the install Maven phase.

The artifact can also be manually installed in local Maven repository:

```mvn install:install-file -Dfile=libs/privacymanager-0.3.jar -DpomFile=libs/privacymanager-0.3.pom```

For more commands and install options see [Installing 3rd party library][2]

Usage
-----------

#### Configuration

Copy the file ```res/3CixtyProfileStorage.properties``` and edit the copy in order to set its properties.

The default file looks like this:


    # the path of the ontology that represents the user profile
    ProfileStorage.ontology.path=src/test/resources/UserProfileKBmodelWithIndividuals.rdf
    
    # the namespace of the previous ontology
    ProfileStorage.ontology.ns=http://www.eu.3cixty.org/profile#
    
    # Concept that is the user profile in the ontology (with no namespace)
    ProfileStorage.ontology.profile.concept=UserProfile
    
    # the property that is the key for user profile
    ProfileStorage.ontology.profile.key=hasUID
    
    # JSON-LD Option for property values. If true, when a property has only one value, the output is not an array
    # example : "hasGender" : "Male" versus "hasGender" : ["Male"]
    # valid values are : true | false 
    ProfileStorage.jsonld.option.compact=false
    
    #JSON-LD Option for individual writting : If false, only direct statements of the individual 
    # are written. Otherwise, a recursive parsing is done to  output all linked individuals
    # valid values are : true | false 
    ProfileStorage.jsonld.option.recursive=true


Set the property ```ProfileStorage.ontology.path``` in the configuration file to specify the path to the ontology.

#### Main API interfaces

The main interfaces are:  

- eu.threecixty.privacy.datastorage.ProfileManager
- eu.threecixty.privacy.datastorage.ProfileManagerFactory
- eu.threecixty.privacy.auth.Authenticator
- eu.threecixty.privacy.auth.Service
- eu.threecixty.privacy.auth.Session
- eu.threecixty.privacy.auth.SessionManager

#### Main implementation classes
Implementation classes provided in the distribution:
* ```ProfileManagerFactory``` is implemented by ```com.thalesgroup.theresis.perso.datastorage.impl.simple.SimpleProfileManagerFactory```

* ```SessionManager``` is implemented by ```com.thalesgroup.theresis.perso.datastorage.impl.simple.SimpleSessionManager```

* Other implementations are hidden and are obtained using the factory and manager. They are specific to the general implementation of the privacy framework.

#### Principles of use

*The security model is yet to be defined and implemented. That's why the subject is openely discussed in this documentation*

The main operations are provided by the interface ```ProfileManager```. A ProfileManager is created using the factory ```ProfileManagerFactory```'s method ```getProfileManager(String)```. The method's argument is the path of the configuration file to be parsed.

Every method of ```ProfileManager``` requires a valid ```Session``` and sessions are requested from a ```SessionManager```. To open a session, you need to pass an ```Authenticator``` to ```ProfileManagerFactory```'s method ```getAuthenticator```. Each authenticator is tight to a requester service and user. Both of them are mandatory.


Services are represented by the interface ```Service``` and are addressed using ```ProfileManagerFactory.getService(String, String)```. The arguments are specific to the services security model implemented. They can be the service ID and an App Key.

Before authentication, a user typicaly consists of a pair of user name and credential, whose representation also depends on the user security model implemented. After authentication, a user should be only referenced by a unique identifier, possibly specific to the session.

A end-user can be anonymous. If the request is emanating from a software component or a service instead of a end-user (for example, a dataminer), the user specified for the sesssion must be specific to this component or be the system user (and cannot be anonymous)

#### Examples

The following code shows how to query all of the User identifiers from the KB:


    import java.util.Set;
    
    import com.thalesgroup.theresis.perso.authen.impl.simple.SimpleSessionManager;
    import com.thalesgroup.theresis.perso.datastorage.impl.simple.SimpleProfileManagerFactory;
    
    import eu.threecixty.privacy.authen.Authenticator;
    import eu.threecixty.privacy.authen.Service;
    import eu.threecixty.privacy.authen.Session;
    import eu.threecixty.privacy.datastorage.ProfileManager;
    import eu.threecixty.privacy.datastorage.ProfileManagerFactory;
    ```
    ```
    try {
   		
		SimpleProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance();
		String propertyPath = "C:/3cixty/config/3CixtyProfileStorage.properties";
		ProfileManager profileMgr = profileFactory.getProfileManager( propertyPath );
	  
	    // Get a reference on the dataminer service
	    Service service = profileFactory.getService( "http://3cixty/dataminer",
	    "kACAH-1Ng1MImB85QDSJQSxhqbAA7acjdY9pTD9M" );
	    
	    // Get an authentication token
	    Authenticator auth = profileFactory.getAuthenticator(service,"root","admin", null ); // no additional security/protocol option
	    
	    // Open a session for the dataminer.
	    // In this particular case, the requesting user is the system and not an end-user thus the
	    // need for the user 'root'.
	    Session session = SimpleSessionManager.getInstance().getSession( auth );
	    
	    // Get the list of users
	    Set<String> userIDS = profileMgr.getAllUsersIDs( session );
    
    } catch (Exception e ) {
    	e.printStackTrace();
    }


License
----

Copyright (c) 2014 Thales Services, All rights Reserved.

[jena]: https://jena.apache.org/
[json-ld]: http://json-ld.org/
[sparql]: http://www.w3.org/TR/rdf-sparql-query/
[1]: http://www.w3.org/TR/sparql11-property-paths/
[2]: http://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html
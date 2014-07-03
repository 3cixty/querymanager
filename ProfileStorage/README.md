ProfileStorage
=========

ProfileStorage is a library holding the private knowledge base of user profiles and managing the access controls.

Features:
  - User-centric privacy of profiles
  - Fully semantical
  - Semi-opened privacy framework for service providers
  - Security management

Version
----
Alpha 0.1

This version implements [ProfileStorage-API] version 1.1

Tech
-----------
ProfileStorage uses a number of open source projects to work properly:

* [Jena] - awesome web-based text editor

The module supports [JSON-LD] and [SPARQL] 1.1 [Property Path][1]

Installation
-----------
Installation is performed through Maven executed from the root directory of the ProfileStorage project:

```mvn install```

You can also run it from Eclipse or an other Maven integrated IDE.

Usage
-----------
#### Maven
In order to use the module in other Maven projects add the following dependency in the project's pom.xml:

```
<dependency>
    <groupId>eu.3cixty.privacy</groupId>
    <artifactId>profile-storage</artifactId>
    <version>0.1</version>
</dependency>

```

#### Configuration

Copy the file ```res/3CixtyProfileStorage.properties``` and edit the copy in order to set its properties.

The default file looks like this:

```
# the path of the ontology that represents the user profile
ProfileStorage.ontology.path=D:/Users/T0125851/Projets/3cixty/ontology/UserProfileKBmodel_V1_func.rdf
# the namespace of the previous ontology
ProfileStorage.ontology.ns=http://www.eu.3cixty.org/profile#

# Concept that is the user profile in the ontology (with no namespace)
ProfileStorage.ontology.profile.concept=UserProfile

# the property that is the key for user profile
ProfileStorage.ontology.profile.key=hasUID

ProfileStorage.ontology.db.path=D:/Users/T0125851/Projets/3cixty/DB
```

Set the property ```ProfileStorage.ontology.path``` in the configuration file to specify the path to the ontology.

#### Principles of use

* The main operations are available through the interface ```eu.threecixty.privacy.datastorage.ProfileManager```.

* Instances of this interface are obtained through the factory ```eu.threecixty.privacy.datastorage.ProfileManagerFactory```.

* ProfileStorage supplies an implementation of the factory: ```com.thalesgroup.theresis.perso.datastorage.impl.simple.SimpleProfileManagerFactory``` 

* Every method of ```ProfileManager``` requires a valid ```Session``` object.

* Services are identified and authentified through the interface ``````

#### Examples

The following code shows how to query all of the User identifiers from the KB:

```
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
    ProfileManagerFactory profileFactory = SimpleProfileManagerFactory.getInstance();
    String propertyPath = "C:/3cixty/config/3CixtyProfileStorage.properties";
    ProfileManager profileMgr = profileFactory.getProfileManager( propertyPath );

    // Get a reference on the dataminer service
    Service service = profileFactory.getService( "http://3cixty/dataminer",
        "kACAH-1Ng1MImB85QDSJQSxhqbAA7acjdY9pTD9M" );

    // Get an authentication token
    Authenticator auth = profileFactory.getAuthenticator(service,
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
```

License
----

Copyright (c) 2014 Thales Services, All rights Reserved.

[json-ld]: http://json-ld.org/
[sparql]: http://www.w3.org/TR/rdf-sparql-query/
[1]: http://www.w3.org/TR/sparql11-property-paths/
[profilestorage-api]: https://github.com/3cixty/profileStorage/tree/master/ProfileStorage-api
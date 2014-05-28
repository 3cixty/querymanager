[Identification]

Privacy Manager (Thales)


[Project description]

This component provides methods to set/get the values of user profiles and manages policies assigned to the profiles.

The privacy API is centered on some semantic entities, such as Scope and Resource, which are derived from Entity.
These entities, along with Policy and Credential to secure access to profiles, are passed to a Storage that manage the persistence of the profiles.

The security model implemented revolves around these specifications:
- a profile data can only be addressed by its URL which is derived from its model’s ontology.
- in the default implementation of Credential, the identity is just a string.
- authentication is not currently supported, especially when a credential is submitted. Therefore, a profile should be accessed by trusted applications or users for the moment.
- the caller can define access modes, i.e. a policy, on the profile’s members and/or the profile as a whole.
- the policy is set when creating the profile and cannot be changed later. Further access to the profile will be challenged against the policy set when that profile was created, not the one specified by the caller application. The policy cannot be changed after having been set once, even by the entity having created the preference. The system checks for integrity failures and reports them as security fault.
- these requirements apply to the file system storage:
   o the privacy module hides the URL of the profiles that are exposed out-of the privacy infrastructure.
   o the serialized value can be ciphered. This is specified in the policy details. Ciphering strongly increases the protection of sensitive profiles or data but has a cost in term of performance.


[For developers]

Here are the requirements for developer to define their own stored data types managed by Privacy Manager. They are essentially inherited by the Java serialization paradigm used in the Privacy module:
In the following paragraph, "stored data" represents the profile's data or a member of a profile depending of the degree of fragmentation of the data being serialized.
	- each stored data MUST be represented by a class in Java
	- the more fragmented is the entities representing the data, the better
	- the class representing a stored data MUST implement the interface java.io.Serializable and the methods int hashCode() and boolean equals(Object) properly.
	- every non transient member in the class must implement java.io.Serializable itself (primitive types are compatible too), member that are not to serialize must have the 'transient' modifier.
	- the class representing a stored data MUST have a static member int serialVersionUID of random value. The value of the serialVersionUID member SHOULD be regenerated after a change is made in the structure of the class, typically when removing or adding a field or modifying the type of a field.
	- the class representing a stored data MUST have a default constructor accepting no argument. It can be declared private if necessary. Better is to declare it public. If you no default constructor is provided then methods of the java.io.Serializable interface MUST be overriden.

The values stored using Privacy Manager are usually persistent on the system even after uninstalling the Privacy Manager.


[Limits of use]

	- The Privacy module is not backward compatible, i.e it can handle only one version of the implementation of a stored entity.

	- The implementation of the Privacy module prevents direct reading of protected values and application restricted access to the Privacy API.
	  It relies on the internal storage protection, hiding the true file names and an optional ciphering.
          Since the specifications said that no user input is required for use of the privacy data, the protection offered by the ciphering subsystem should be considered moderately weak and is certainly not suited for storage of sensitive or secret data.

	- encryption is time consuming. Use with care.


[Known issues]

There is none.
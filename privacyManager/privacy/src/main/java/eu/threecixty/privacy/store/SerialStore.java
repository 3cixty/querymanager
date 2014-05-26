package eu.threecixty.privacy.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.Properties;
import java.util.UUID;

import eu.threecixty.privacy.policies.BasePolicy;
import eu.threecixty.privacy.security.CipheredStreamer;
import eu.threecixty.privacy.security.KeyPlatform;
import eu.threecixty.privacy.semantic.Resource;
import eu.threecixty.privacy.semantic.Scope;
import eu.threecixty.privacy.storage.Credential;
import eu.threecixty.privacy.storage.Policy;
import eu.threecixty.privacy.storage.Storage;


/**
 * Concrete implementation of interface {@link Storage} serializing objects
 * implementing java.io.Serializable (user data and policies alike)
 */
public class SerialStore implements Storage<Serializable> {
	
	public static final String PROP_STORE_DIR = "eu.3cixty.store.dir";
	// Root directory for storage on disk
	// Should be set by calling {@link #setRootDir(String)}
	private final String mRootDir;
	
	private final class FileStreamer implements StoreStreamer {

		/** Return an opened FileOutputStream to write a resource's raw value */
		public OutputStream getOutputStream(Resource<?> resource, Credential credential)
				throws IOException {
			
			String ontology = resource.getEntityAsString() + "$value";
			Value registeredResource = getOrCreateResourceEntry(
					credential.getSubject().getEntityAsString(), ontology, true);
			
			if (registeredResource == null)
				return null; // something went wrong
			
			String filename = registeredResource.getResource();
			return new FileOutputStream(new File(mRootDir, filename));
		}

		/** Return an opened FileInputStream to read a resource's raw value. */
		public InputStream getInputStream(Resource<?> resource, Credential credential)
				throws IOException {
			
			String ontology = resource.getEntityAsString() + "$value";
			Value registeredResource = getOrCreateResourceEntry(
					credential.getSubject().getEntityAsString(), ontology, false);
			
			if (registeredResource == null)
				return null;
			
			String filename = registeredResource.getResource();
			return new FileInputStream(new File(mRootDir, filename));
		}
	};

	private final StoreStreamer mPolicyStreamer = new StoreStreamer() {

		public OutputStream getOutputStream(Resource<?> resource, Credential credential)
				throws IOException {

			String ontology = resource.getEntityAsString() + "$policy";
			Value registeredResource = getOrCreateResourceEntry(
					credential.getSubject().getEntityAsString(), ontology, true);
			if (registeredResource == null)
				return null;
			
			String filename = registeredResource.getResource();
			FileOutputStream filestream = new FileOutputStream(new File(mRootDir, filename));
			try {
				ObjectOutputStream oos = new ObjectOutputStream(filestream);
				return oos;
			} catch (IOException io) {
				filestream.close();
				throw io;
			}
		}

		public InputStream getInputStream(Resource<?> resource, Credential credential) throws IOException {
			String ontology = resource.getEntityAsString() + "$policy";
			Value registeredResource = getOrCreateResourceEntry(
					credential.getSubject().getEntityAsString(), ontology,
					false);
			if (registeredResource == null)
				return null;

			String filename = registeredResource.getResource();
			FileInputStream filestream = new FileInputStream(new File(mRootDir, filename));
			try {
				ObjectInputStream ois = new ObjectInputStream(filestream);
				return ois;
			} catch (IOException io) {
				filestream.close();
				throw io;
			}
		}
	};
	
	protected Value getOrCreateResourceEntry(String user,
			String ontology, boolean createIfMissing) throws IOException {
		Value registeredResource = dataSource.getResourceByOntology(ontology);
		if ((registeredResource == null) && createIfMissing) {
			// Need to create the resource.
			User registeredUser = dataSource.findUser(user);
			if (registeredUser == null) {
				// When the user is not registered yet add it to the index
				registeredUser = dataSource.addUser(user, null);
				if (registeredUser == null) {
					throw new IOException("could not create user in store index");
				}
			}

			registeredResource = dataSource.addResource(registeredUser.getId(), ontology,
					UUID.randomUUID().toString(), "filesystem:internal");
			if (registeredResource == null) {
				throw new IOException("could not create resource in store index");
			}
		}
		
		return registeredResource;
	}
	
	private final StoreIndex dataSource;

	/**
	 * Create an implementation of {@link Storage} serializing the data to the
	 * file system.
	 * 
	 * @param props
	 *            the creation properties. Set {@link #PROP_STORE_DIR} in order
	 *            to define the location of the file system repository. The
	 *            default location is the working directory (".")
	 * @param dataSource
	 *            the index to map entities to the files
	 */
	public SerialStore(Properties props, StoreIndex dataSource) {
		mRootDir = props.getProperty(PROP_STORE_DIR, ".");
		
		new File(mRootDir).mkdirs();
		this.dataSource = dataSource;
	}

	public StoreIndex getDataSource() {
		return dataSource;
	}

	public <V extends Serializable> V getValue(Resource<V> resource,
			Credential credential) throws SecurityException, IOException {
		
		StoreStreamer streamer;
		try {
			streamer = getDataStreamer(resource, credential);
		} catch (GeneralSecurityException e1) {
			throw new SecurityException(e1);
		}
		
		ObjectInputStream in = null;
		Object obj = null;
		try {
			InputStream rawInput = streamer.getInputStream(resource, credential);
			if (rawInput == null) {
				throw new IOException("could not get input stream to alter resource's value");
			}
			
			in = new ObjectInputStream(rawInput);
			obj = in.readObject();

		} catch (ClassNotFoundException e) {
			throw new IOException(e);
		} finally {
			IOUtils.closeQuietly(in);
		}

		// TODO to fix later: Type safety: Unchecked cast from Object to V
		return (V) obj;
	}

	public <V extends Serializable, Y> void setValue(Resource<V> resource,
			Y value, Credential credential) throws SecurityException, IOException {
		
		StoreStreamer streamer;
		try {
			streamer = getDataStreamer(resource, credential);
		} catch (GeneralSecurityException e1) {
			throw new SecurityException(e1);
		}

		ObjectOutputStream out = null;
		try {
			OutputStream rawOutput = streamer.getOutputStream(resource, credential);
			if (rawOutput == null) {
				throw new IOException("could not get output stream to alter resource's value");
			}
			
			out = new ObjectOutputStream(rawOutput);
			out.writeObject(value);
			out.flush();
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	private StoreStreamer getDataStreamer(
			Resource<? extends Serializable> resource, Credential credential)
			throws SecurityException, IOException, GeneralSecurityException {
		
		BasePolicy policy = (BasePolicy) getPolicy(resource, credential);
		if (policy.getCipher()) {
			return new CipheredStreamer(KeyPlatform.keys.getKey(credential), new FileStreamer());
		} else {
			return new FileStreamer();
		}
	}
	
	/**
	 * @throws IOException
	 *             Same as ancestor class except that FileNotFoundException is
	 *             caught and null is returned instead.
	 */
	public Policy getPolicy(Resource<? extends Serializable> resource,
			Credential credential) throws SecurityException, IOException {
		
		Policy policy = null;
		ObjectInputStream in = null;
		
		try {
			in = (ObjectInputStream) mPolicyStreamer.getInputStream(resource, credential);
			if (in != null) {
				policy = (Policy) in.readObject();
			}
		} catch (ClassCastException e) {
			
			throw new IOException(e);
			
		} catch (FileNotFoundException e) {
			
			// It is a normal thing when the resource was not written yet.
			// Just return a null policy.
			
		} catch (ClassNotFoundException e) {
			
			throw new IOException(e);
			
		} finally {
			
			closeSilently(in);
		}

		return policy;
	}

	public void setPolicy(Resource<? extends Serializable> resource,
			Policy policy, Credential credential) throws SecurityException, IOException {
		
		ObjectOutputStream out = null;
		try {
			out = (ObjectOutputStream) mPolicyStreamer.getOutputStream(resource, credential);
			if (out == null)
				throw new IOException("could not create an output stream to write policy of " + resource);
			
			out.writeObject(policy);
			out.flush();
		} finally {
			closeSilently(out);
		}
	}

	private void closeSilently(OutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
			}
		}
	}

	private void closeSilently(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}

	public Credential newCredential(Scope scope, String user, String password)
			throws SecurityException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Policy newReadAccessPolicy(Scope scope) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public Policy newWriteAccessPolicy(Scope scope) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

}

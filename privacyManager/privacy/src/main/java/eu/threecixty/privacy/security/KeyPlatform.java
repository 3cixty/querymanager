package eu.threecixty.privacy.security;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import eu.threecixty.privacy.storage.Credential;
import eu.threecixty.privacy.store.IOUtils;

public final class KeyPlatform {

	public static final KeyPlatform keys = new KeyPlatform();

//	private static final String LOG_TAG = "KeyPlatform";
	
	private KeyStore keystore;
	
	private String keyStorePassword;
	
	private KeyPlatform() {
		super();
	}

	public void load() 
	throws NoSuchAlgorithmException,
			CertificateException,
			IOException,
			KeyStoreException {

		if (keystore != null) throw new IllegalStateException();
		String type = KeyStore.getDefaultType();
		keystore = KeyStore.getInstance(type);
		
		char[] passwdChars = toCharArray(getKeyStorePassword());
		
		FileInputStream fileInput = null;
		final String keystoreFilename = getKeystoreName();
		
		try {
			try {
				fileInput = new FileInputStream(keystoreFilename);
			} catch (FileNotFoundException fileNotFound) {
				// The keystore file does not exist yet: create it.
//				Log.d(LOG_TAG, "keystore is missing. going to create it.");
				fileInput = null;
			}
			
			keystore.load(fileInput, passwdChars);
	
			if (fileInput == null) {
				storeKeystore(passwdChars, keystoreFilename);
			}
		} finally {
			IOUtils.closeQuietly(fileInput);
		}
	}

	private String getKeystoreName() {
		final String keystoreFilename = ".keystore";
		return keystoreFilename;
	}

	private void storeKeystore(char[] passwdChars,
			final String keystoreFilename) throws FileNotFoundException,
			KeyStoreException, IOException, NoSuchAlgorithmException,
			CertificateException {
		FileOutputStream fileOutput = null;
		try {
			fileOutput = new FileOutputStream(keystoreFilename);
			keystore.store(fileOutput, passwdChars);
		} finally {
			IOUtils.closeQuietly(fileOutput);
		}
	}

	public static char[] toCharArray(String str) {
		char[] chars = new char[0];
		if (str != null) {
			chars = str.toCharArray();
		}
		
		return chars;
	}

	public Key getKey(Credential credential) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
		
		String alias = credential.getSubject().getEntityAsString();
		char[] kspwd = KeyPlatform.toCharArray(KeyPlatform.keys.getKeyStorePassword());		
		Key k = KeyPlatform.keys.getKey(alias, kspwd);
		if (k == null) {
			// Using SymmetricKey.deriveKeyPbkdf2() would be much safer
			// but what about the salt? How can the key be shared with other
			// trusted parties that have access to the resources as well but
			// a different credential?
			k = SymmetricKey.generateAESKey(256);
			KeyPlatform.keys.addKey(alias, k, kspwd, new Certificate[0]);
			
			try {
				storeKeystore(kspwd, getKeystoreName());
			} catch (Exception e) {
//				Log.w(LOG_TAG, "could not write keystore", e);
			}
		}
		
		return k;
	}

	public Certificate[] importCertificateChain(String certificateChainFileName)
			throws CertificateException, IOException {
		// Load the certificate chain (in X.509 DER encoding).
		CertificateFactory certificateFactory = CertificateFactory
				.getInstance("X.509");
		// Required because Java is STUPID. You can't just cast the result
		// of toArray to Certificate[].
		java.security.cert.Certificate[] chain = {};
		FileInputStream certificateStream = null;
		
		try {
			certificateStream = new FileInputStream(certificateChainFileName);
			chain = certificateFactory.generateCertificates(certificateStream)
					.toArray(chain);
		} finally {
			certificateStream.close();
		}
		
		return chain;
	}

	public void addKey(String alias, Key key, char[] password,
			Certificate[] chain) throws KeyStoreException {
		if (keystore == null) throw new IllegalStateException("keystore not initialized");

		keystore.setKeyEntry(alias, key, password, chain);
	}
	
	public Key getKey(String alias, char[] password)
			throws UnrecoverableKeyException, KeyStoreException,
			NoSuchAlgorithmException {
		if (keystore == null) throw new IllegalStateException("keystore not initialized");

		return keystore.getKey(alias, password);
	}
	
	/*package*/ String getKeyStorePassword() {
		return this.keyStorePassword;
	}

	public void setKeyStorePassword(String keyStorePassword) {
		// TODO Disable the following trace to avoid leaking sensitive
		// information on trace system when not debugging.
//		Log.d(LOG_TAG, "keystore password set to " + keyStorePassword);
		this.keyStorePassword = keyStorePassword;
	}

	public void unload() {
		try {
			storeKeystore(toCharArray(getKeyStorePassword()), getKeystoreName());
		} catch (Exception e) {
//			Log.w(LOG_TAG, "could not write keystore before closure", e);
		}
		
		this.keystore = null;
		this.keyStorePassword = null;
	}
	
}

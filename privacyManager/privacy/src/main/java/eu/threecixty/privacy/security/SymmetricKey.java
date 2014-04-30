package eu.threecixty.privacy.security;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is originally from the following site:
 * http://android-developers.blogspot.fr/2013/02/using-cryptography-to-store-credentials.html
 */
public class SymmetricKey {
    /**
     * Author of the base of this implementation.
     */
    public static final String UTILS_AUTHOR = "http://android-developers.blogspot.fr/2013/02/using-cryptography-to-store-credentials.html";

	/**
	 * Generate a truly random AES key. A reasonable approach is to generate the
	 * key when an application is first launched.
	 * 
	 * The security of this approach relies on safeguarding the generated key,
	 * which is is predicated on the security of the internal storage. Leaving
	 * the target file unencrypted (but set to MODE_PRIVATE) would provide
	 * similar security.
	 * 
	 * @param keyLength
	 *            Key length in bits. AES should at least accept 128, 192 or
	 *            256. 256 is recommended.
	 * 
	 * @throws NoSuchAlgorithmException
	 *             When AES is not supported
	 */
	public static SecretKey generateAESKey(int keyLength) throws NoSuchAlgorithmException {
		SecureRandom secureRandom = new SecureRandom();
		// Do *not* seed secureRandom! Automatically seeded from system entropy.
		KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
		keyGenerator.init(keyLength, secureRandom);
		SecretKey key = keyGenerator.generateKey();
		return key;
	}
	
	/**
	 * If your app needs additional encryption, a recommended approach is to
	 * require a passphase or PIN to access your application. This passphrase
	 * could be fed into PBKDF2 to generate the encryption key. (PBKDF2 is a
	 * commonly used algorithm for deriving key material from a passphrase,
	 * using a technique known as "key stretching")
	 * 
	 * @param passphraseOrPin
	 * @param salt
	 *            The salt should be a random string, again generated using
	 *            SecureRandom and persisted on internal storage alongside any
	 *            encrypted data. This is important to mitigate the risk of
	 *            attackers using a rainbow table to precompute password hashes.
	 *            Its length MUST be keyLength / 8.
	 * @param keyLength
	 *            AES should at least accept 128, 192 or 256. 256 is
	 *            recommended.
	 * @param iterations Number of PBKDF2 hardening rounds to use. Larger values increase
	 * computation time. You should select a value that causes computation
	 * to take >100ms. As reference, Android and iOS are using 10000 for the time being.
	 */
	public static SecretKey deriveKeyPbkdf2(char[] passphraseOrPin, byte[] salt, int keyLength, int iterations)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		
		/*
		 * Important: store the arguments on disk to derive the key later.
		 * Passphrase or PIN should be queried from the user or obtained from a
		 * secured system.
		 */

		// Derive the key from the password
		SecretKeyFactory secretKeyFactory = SecretKeyFactory
				.getInstance("PBKDF2WithHmacSHA1");
		KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations,
				keyLength);
		
		// We don't use the SecretKey produced by the factory as is, but use its
		// encoded value to create a new SecretKeySpec object. That is done
		// because the output of generateSecret() is actually a PBEKey instance
		// which does not contain an initialized IV -- the Cipher object expects
		// that from a PBEKey and will throw an exception if it is not present
		byte[] keyBytes = secretKeyFactory.generateSecret(keySpec).getEncoded();
	    SecretKey secretKey = new SecretKeySpec(keyBytes, "AES");
		
	    // The correct way to use the secret key would be:
	    
//	    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//	    byte[] iv = new byte[cipher.getBlockSize());
//	    random.nextBytes(iv);
//	    IvParameterSpec ivParams = new IvParameterSpec(iv);
//	    cipher.init(Cipher.ENCRYPT_MODE, key, ivParams);
//	    byte[] ciphertext = cipher.doFinal(plaintext.getBytes("UTF-8"));
	    
		return secretKey;
	}

	/**
	 * Randomly generate a salt that can be passed to {@link #generateKey(char[], byte[])}
	 * 
	 * @param saltLength
	 *            The salt size in bytes. Should be the same length as the key,
	 *            e.g. saltLength=32 in case of a AES-256 key (32=256/8).
	 * @return the salt bytes
	 */
	public static byte[] generateSalt(int saltLength) {
		SecureRandom random = new SecureRandom();
	    byte[] salt = new byte[saltLength];
	    random.nextBytes(salt);
	    
	    return salt;
	}
	
}

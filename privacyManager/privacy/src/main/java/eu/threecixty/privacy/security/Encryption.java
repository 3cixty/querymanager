package eu.threecixty.privacy.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


/**
 * This class is from the following site:
 * http://www.java2s.com/Code/Android/Security/AESEncryption.htm with some
 * modification.
 * @author http://www.java2s.com/Code/Android/Security/AESEncryption.htm
 */
public class Encryption implements EncryptionAlgorithm {
    /**
     * Author of the base of this implementation.
     */
    public static final String UTILS_AUTHOR = "http://www.java2s.com/Code/Android/Security/AESEncryption.htm";
    private Key skey;
    private Cipher cipher;

    /**
     * Initializes with raw key.
     * @param keyraw The raw key. Must NOT be null.
     * @throws UnsupportedEncodingException When no UTF-8 support found.
     * @throws NoSuchAlgorithmException When no MD5 support found.
     * @throws NoSuchPaddingException When the AES/ECB/PKCS5Padding is not supported.
     */
    public Encryption(byte[] keyraw) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
        if (keyraw == null) {
            throw new IllegalArgumentException("null key given");
        } else {
            skey = new SecretKeySpec(keyraw, "AES");
            cipher = newCipher();
        }
    }

    /**
     * Initializes with a password to use for encrypt.
     * @param passphrase The string password.
     * @throws UnsupportedEncodingException When no UTF-8 support found.
     * @throws NoSuchAlgorithmException When no MD5 support found.
     * @throws NoSuchPaddingException When the AES/ECB/PKCS5Padding is not supported.
     */
    public Encryption(String passphrase) throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPaddingException {
        byte[] bytesOfMessage = passphrase.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] thedigest = md.digest(bytesOfMessage);
        skey = new SecretKeySpec(thedigest, "AES");
        cipher = newCipher();
    }
    
	/**
	 * Initializes with a key.
	 * 
	 * @param key
	 *            The key. Must NOT be null.
	 * @param transform
	 *            the name of the transformation, e.g., DES/CBC/PKCS5Padding.
	 *            See the {@linkplain Cipher section in the Java Cryptography
	 *            Architecture Standard Algorithm Name Documentation} for
	 *            information about standard transformation names. If this
	 *            parameter is null or an empty string then a default
	 *            transformation is used instead.
	 * @throws NoSuchPaddingException
	 *             if transformation is null, empty, in an invalid format, or if
	 *             no Provider supports a CipherSpi implementation for the
	 *             specified algorithm.
	 * @throws NoSuchAlgorithmException
	 *             if transformation contains a padding scheme that is not
	 *             available
	 */
    public Encryption(Key key, String transform) throws NoSuchAlgorithmException, NoSuchPaddingException  {
    	 if (key == null) {
             throw new IllegalArgumentException("null key given");
         } else {
	    	skey = key;
	    	cipher = newCipher(transform);
         }
    }
    
    /**
     * Encrypts the bytes.
     * @param plaintext The bytes to encrypt.
     * @return The encrypted value.
     * @throws EncryptionException On encryption exception.
     */
    public byte[] encrypt(byte[] plaintext) throws EncryptionException {
        // returns byte array encrypted with key
        try {
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            return cipher.doFinal(plaintext);
        } catch (InvalidKeyException e) {
            throw new EncryptionException(e);
        } catch (IllegalBlockSizeException e) {
            throw new EncryptionException(e);
        } catch (BadPaddingException e) {
            throw new EncryptionException(e);
        }
    }
    
    /**
     * Deciphers the ciphered bytes.
     * @param ciphertext The data to decipher.
     * @return The deciphered value.
     * @throws EncryptionException On encryption exception.
     */
    public byte[] decrypt(byte[] ciphertext) throws EncryptionException {
        // returns byte array deciphered with key
        try {
            cipher.init(Cipher.DECRYPT_MODE, skey);
            return cipher.doFinal(ciphertext);
        } catch (InvalidKeyException e) {
            throw new EncryptionException(e);
        } catch (IllegalBlockSizeException e) {
            throw new EncryptionException(e);
        } catch (BadPaddingException e) {
            throw new EncryptionException(e);
        }
    }

	public OutputStream getCipherOutputStream(OutputStream output, int cipherMode) throws EncryptionException {
        try {
        	Cipher cipher = newCipher();
			cipher.init(cipherMode, skey);
	    	return new CipherOutputStream(output, cipher);
		} catch (InvalidKeyException e) {
            throw new EncryptionException(e);
		} catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(e);
		} catch (NoSuchPaddingException e) {
            throw new EncryptionException(e);
		}
    }

	public InputStream getCipherInputStream(InputStream input, int cipherMode) throws EncryptionException {
        try {
        	Cipher cipher = newCipher();
			cipher.init(cipherMode, skey);
	    	return new CipherInputStream(input, cipher);
		} catch (InvalidKeyException e) {
            throw new EncryptionException(e);
		} catch (NoSuchAlgorithmException e) {
            throw new EncryptionException(e);
		} catch (NoSuchPaddingException e) {
            throw new EncryptionException(e);
		}
    }

	private Cipher newCipher() throws NoSuchAlgorithmException,
			NoSuchPaddingException {
		return Cipher.getInstance("AES/ECB/PKCS5Padding");
	}

	private Cipher newCipher(String transformation) throws NoSuchAlgorithmException,
			NoSuchPaddingException {
		if (transformation == null || transformation.length() == 0)
			return newCipher();
		else
			return Cipher.getInstance(transformation);
	}

}

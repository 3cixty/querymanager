package eu.threecixty.privacy.security;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Encryption algorithm.
 */
public interface EncryptionAlgorithm {
    /**
     * Encrypts the given bytes.
     * @param bytes
     *            The bytes to encrypt.
     * @return The encrypted bytes.
     * @throws EncryptionException When the encryiption fails.
     */
	public abstract byte[] encrypt(byte[] bytes) throws EncryptionException;

    /**
     * Decrypts the given bytes.
     * @param bytes
     *            The bytes to decrypt.
     * @return The decrypted bytes.
     * @throws EncryptionException When the encryiption fails.
     */
    public abstract byte[] decrypt(byte[] bytes) throws EncryptionException;

    /**
	 * Constructs a CipherInputStream from an InputStream and encryption.
	 * 
	 * @param input
	 *            the InputStream object
	 * @param cipherMode
	 *            the operation mode of encryption (this is one of the
	 *            following: Cipher.ENCRYPT_MODE, Cipher.DECRYPT_MODE,
	 *            Cipher.WRAP_MODE or Cipher.UNWRAP_MODE)
	 * @return a new Cipher input stream
	 * @throws EncryptionException
     */
	public abstract InputStream getCipherInputStream(InputStream input, int cipherMode)
			throws EncryptionException;

	/**
	 * Constructs a CipherOutputStream from an OutputStream and encryption.
	 * 
	 * @param output
	 *            the OutputStream object
	 * @param cipherMode
	 *            the operation mode of encryption (this is one of the
	 *            following: Cipher.ENCRYPT_MODE, Cipher.DECRYPT_MODE,
	 *            Cipher.WRAP_MODE or Cipher.UNWRAP_MODE)
	 * @return a new Cipher output stream
	 * @throws EncryptionException
	 */
	public abstract OutputStream getCipherOutputStream(OutputStream output, int cipherMode)
			throws EncryptionException;
}
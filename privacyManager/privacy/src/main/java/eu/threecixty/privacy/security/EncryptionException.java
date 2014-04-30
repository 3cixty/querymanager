package eu.threecixty.privacy.security;

/**
 * A custom exception for encoding.
 *
 */
public class EncryptionException extends Exception {

    /**
	 * Serial version UID
	 */
	private static final long serialVersionUID = -916609339174957311L;

	/**
     * Inherited from {@link Exception}.
     */
    public EncryptionException() {
        super();
    }

    /**
     * Inherited from {@link Exception}.
     * @param message The message.
     * @param cause The root cause.
     */
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Inherited from {@link Exception}.
     * @param message The message.
     */
    public EncryptionException(String message) {
        super(message);
    }

    /**
     * Inherited from {@link Exception}.
     * @param cause The root cause.
     */
    public EncryptionException(Throwable cause) {
        super(cause);
    }

}
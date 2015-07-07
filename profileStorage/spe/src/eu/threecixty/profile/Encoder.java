package eu.threecixty.profile;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * This class is used to encode UID from Google / Facebook UID.
 * <br>
 * Note that there is possibility that Google and Facebook have the same UID.
 *
 * @author Cong-Kinh Nguyen
 *
 */
public class Encoder {

	private static final Encoder INSTANCE = new Encoder();
	
	public static Encoder getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Encodes the given UID.
	 * @param uid
	 * @return
	 */
	public String encode(String uid) {
		if (uid == null) return null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte [] b = md.digest(uid.getBytes());
			return UUID.nameUUIDFromBytes(b).toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Encoder() {
	}
}

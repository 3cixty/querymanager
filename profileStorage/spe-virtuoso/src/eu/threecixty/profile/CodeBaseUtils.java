package eu.threecixty.profile;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;

public class CodeBaseUtils {

	private static final String UTF8 = "UTF-8";

	/**
	 * Encodes a given string with base64.
	 * @param str
	 * @return
	 */
	public static String encode(String str) {
		try {
			return Base64.encodeBase64String(str.getBytes(UTF8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Decodes a given string with base64.
	 * @param encodedStr
	 * @return
	 */
	public static String decode(String encodedStr) {
		try {
			return new String(Base64.decodeBase64(encodedStr), UTF8);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Prohibits instantiations.
	 */
	private CodeBaseUtils() {
	}
}

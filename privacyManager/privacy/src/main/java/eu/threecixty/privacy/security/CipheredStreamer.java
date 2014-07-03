package eu.threecixty.privacy.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.Key;

import javax.crypto.Cipher;

import eu.threecixty.privacy.semantic.Resource;
import eu.threecixty.privacy.storage.Credential;
import eu.threecixty.privacy.store.IOUtils;
import eu.threecixty.privacy.store.StoreStreamer;

public final class CipheredStreamer implements StoreStreamer {

//	private static final String LOG_TAG = "CipheredStreamer";
	private final StoreStreamer mRawStreamer;
	private final Encryption crypt;

	public CipheredStreamer(Key key, StoreStreamer raw) throws GeneralSecurityException {
		this.mRawStreamer = raw;
//		Log.d(LOG_TAG, "key encoded: " + toByteString(key.getEncoded()));
		this.crypt = new Encryption(key, null);
	}
	
	@SuppressWarnings("unused")
	private String toByteString(byte[] bytes) {
		StringWriter sw = new StringWriter();
		boolean first = true;
		for (byte b : bytes) {
			if (!first) sw.append(':');
			sw.append(Integer.toHexString((int)b));
			first = false;
		}
		
		return sw.toString();
	}

	public OutputStream getOutputStream(Resource<?> resource, Credential credential)
			throws IOException {
		
		OutputStream cos = null;
		OutputStream os = mRawStreamer.getOutputStream(resource, credential);
		if (os != null) {
			try {
				cos = crypt.getCipherOutputStream(os, Cipher.ENCRYPT_MODE);
			} catch (Exception e) {
//				Log.e(LOG_TAG, "could not get cipher output stream", e);
				IOUtils.closeQuietly(os);
				cos = null;
			}
		}
		
		return cos;
	}

	public InputStream getInputStream(Resource<?> resource, Credential credential) throws IOException {
		
		InputStream cis = null;
		InputStream is = mRawStreamer.getInputStream(resource, credential);
		if (is != null) {
			try {
				cis = crypt.getCipherInputStream(is, Cipher.DECRYPT_MODE);
			} catch (Exception e) {
//				Log.e(LOG_TAG, "could not get cipher input stream", e);
				IOUtils.closeQuietly(is);
				cis = null;
			}
		}
		
		return cis;
	}

}

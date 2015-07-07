package eur.threecixty.profile;

import org.junit.Assert;
import org.junit.Test;

import eu.threecixty.profile.Encoder;
import eu.threecixty.profile.Utils;

public class EncoderTests {

	@Test
	public void testEncodeGoogleUID() {
		String uid = "123456789";
		String encoded = Encoder.getInstance().encode(uid, Utils.UidSource.GOOGLE);
		Assert.assertNotNull(encoded);
		String encoded2 = Encoder.getInstance().encode(uid, Utils.UidSource.GOOGLE);
		Assert.assertTrue(encoded.equals(encoded2));
	}
	
	@Test
	public void testEncodeFacebookUID() {
		String uid = "123456789";
		String encoded = Encoder.getInstance().encode(uid, Utils.UidSource.FACEBOOK);
		Assert.assertNotNull(encoded);
		String encoded2 = Encoder.getInstance().encode(uid, Utils.UidSource.FACEBOOK);
		Assert.assertTrue(encoded.equals(encoded2));
	}
	
	@Test
	public void testEncodeGoogleFacebookWithSameUID() {
		String uid = "123456789";
		String googleEncoded = Encoder.getInstance().encode(uid, Utils.UidSource.GOOGLE);
		Assert.assertNotNull(googleEncoded);
		String facebookEncoded = Encoder.getInstance().encode(uid, Utils.UidSource.FACEBOOK);
		Assert.assertNotNull(facebookEncoded);
		// make sure that even Google & Facebook UID are the same, hashed code is still different
		Assert.assertFalse(googleEncoded.equals(facebookEncoded));
	}
}

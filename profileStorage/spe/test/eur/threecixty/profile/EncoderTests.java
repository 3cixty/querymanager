package eur.threecixty.profile;

import org.junit.Assert;
import org.junit.Test;

import eu.threecixty.profile.Encoder;

public class EncoderTests {

	@Test
	public void testEncode() {
		String uid = "123456789";
		String encoded = Encoder.getInstance().encode(uid);
		Assert.assertNotNull(encoded);
		String encoded2 = Encoder.getInstance().encode(uid);
		Assert.assertTrue(encoded.equals(encoded2));
	}
	
	@Test
	public void testEncodeTwoUIDs() {
		String uid1 = "123456789";
		String encoded1 = Encoder.getInstance().encode(uid1);
		Assert.assertNotNull(encoded1);
		String uid2 = "123456798";
		String encoded2 = Encoder.getInstance().encode(uid2);
		Assert.assertFalse(encoded1.equals(encoded2));
	}
}

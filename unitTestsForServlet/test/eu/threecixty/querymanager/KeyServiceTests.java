package eu.threecixty.querymanager;

import org.junit.Assert;
import org.junit.Test;

public class KeyServiceTests extends HTTPCall {

	private static final String KEY_SERVICE = "http://localhost:8080/querymanagerServlet-1.0/services/key";
	
	private static final String A_VALID_KEY = "MTAzOTE4MTMwOTc4MjI2ODMyNjkwMTQwNDIyNzAzMzIwMzpfIWdjAWhmLjpmXF9x";
	
	@Test
	public void testValidateKey() {
		try {

			// URL = http://localhost:8080/querymanagerServlet-1.0/services/key/validate?key=MTAzOTE4MTMwOTc4MjI2ODMyNjkwMTQwNDIyNzAzMzIwMzpfIWdjAWhmLjpmXF9x
			StringBuffer buffer = new StringBuffer();
			sendGET(KEY_SERVICE , "validate?key=" + A_VALID_KEY, buffer);
			Assert.assertTrue(buffer.toString().equalsIgnoreCase("ok"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

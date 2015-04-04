package eu.threecixty.profile;

import org.junit.Assert;
import org.junit.Test;

public class TrayTests {
	
	String token = "123456789";

	@Test
	public void testAddTray() {
		Tray tray = new Tray();
		boolean attended = false;
		String title = "abc...xyz@&é";
		String elementId = System.currentTimeMillis() + "";
		String source = "TestSource";
		String type = "Events";
		String imageUrl = "fake URL";
		String dateTimeAttended = "Fake DateTime";
		int rating = 4;
		tray.setToken(token);
		tray.setElement_id(elementId);
		tray.setElement_title(title);
		tray.setElement_type(type);
		tray.setSource(source);
		tray.setAttend(attended);
		tray.setAttend_datetime(dateTimeAttended);
		tray.setImage_url(imageUrl);
		tray.setRating(rating);
		boolean ok = TrayUtils.addTray(tray);
		Assert.assertTrue(ok);
		
		// try to add the second time, should fail
		ok = TrayUtils.addTray(tray);
		Assert.assertFalse(ok);
	}
	
	@Test
	public void testUpdateTray() {
		Tray tray = new Tray();
		boolean attended = false;
		String title = "abc...xyz@&é";
		String elementId = System.currentTimeMillis() + "";
		String source = "TestSource";
		String type = "Events";
		String imageUrl = "fake URL";
		String dateTimeAttended = "Fake DateTime";
		int rating = 4;
		tray.setToken(token);
		tray.setElement_id(elementId);
		tray.setElement_title(title);
		tray.setElement_type(type);
		tray.setSource(source);
		tray.setAttend(attended);
		tray.setAttend_datetime(dateTimeAttended);
		tray.setImage_url(imageUrl);
		tray.setRating(rating);
		boolean ok = TrayUtils.addTray(tray);
		Assert.assertTrue(ok);
		
		// try to add the second time, should fail
		ok = TrayUtils.addTray(tray);
		Assert.assertFalse(ok);
	}
}

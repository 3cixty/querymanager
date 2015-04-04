package eu.threecixty.profile;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TrayTests {
	
	String token = "123456789";

	@Test
	public void testAddAndLoadTray() {
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
		
		Tray loadedTray = TrayUtils.getTray(token, elementId);
		Assert.assertNotNull(loadedTray);
		Assert.assertTrue(equal(tray, loadedTray));
	}
	
	@Test
	public void testUpdateAndLoadTray() {
		Tray tray = new Tray();
		boolean attended = true;
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
		
		String newTitle = "new Title";
		tray.setElement_title(newTitle);
		ok = TrayUtils.updateTray(tray);
		Assert.assertTrue(ok);
		
		Tray loadedTray = TrayUtils.getTray(token, elementId);
		Assert.assertNotNull(loadedTray);
		Assert.assertTrue(equal(tray, loadedTray));
	}
	
	@Test
	public void testDeleteTray() {
		String newtoken = System.currentTimeMillis() + "";
		Tray tray = new Tray();
		boolean attended = true;
		String title = "abc...xyz@&é";
		String elementId = System.currentTimeMillis() + "";
		String source = "TestSource";
		String type = "Events";
		String imageUrl = "fake URL";
		String dateTimeAttended = "Fake DateTime";
		int rating = 4;
		tray.setToken(newtoken);
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
		
		ok = TrayUtils.deleteTray(tray);
		Assert.assertTrue(ok);
	}
	
	@Test
	public void testGetTrays() {
		String newtoken = System.currentTimeMillis() + "";
		Tray tray = new Tray();
		boolean attended = true;
		String title = "abc...xyz@&é";
		String elementId = System.currentTimeMillis() + "";
		String source = "TestSource";
		String type = "Events";
		String imageUrl = "fake URL";
		String dateTimeAttended = "Fake DateTime";
		int rating = 4;
		tray.setToken(newtoken);
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
		
		List <Tray> trays = TrayUtils.getTrays(newtoken);
		Assert.assertTrue(trays.size() == 1);
		Assert.assertTrue(equal(tray, trays.get(0)));
	}
	
	private boolean equal(Tray tray1, Tray tray2) {
		if (!tray1.getToken().equals(tray2.getToken())) return false;
		if (!equalStr(tray1.getElement_id(), tray2.getElement_id())) return false;
		if (!equalStr(tray1.getElement_title(), tray2.getElement_title())) return false;
		if (!equalStr(tray1.getElement_type(), tray2.getElement_type())) return false;
		if (!equalStr(tray1.getImage_url(), tray2.getImage_url())) return false;
		if (!equalStr(tray1.getSource(), tray2.getSource())) return false;
		if (tray1.getRating() != tray2.getRating()) return false;
		if (tray1.isAttend() != tray2.isAttend()) return false; 
		return true;
	}
	
	private boolean equalStr(String str1, String str2) {
		if (str1 == null && str2 != null) return false;
		if (str2 == null && str1 != null) return false;
		if (str1 == null && str2 == null) return true;
		return str1.equals(str2);
	}
}

package eu.threecixty.profile;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TrayTests {

	@Test
	public void testAddAndLoadTray() {
		Tray tray = createTray();
		boolean ok = TrayUtils.addTray(tray);
		Assert.assertTrue(ok);
		String elementId = tray.getElement_id();
		// try to add the second time, should fail
		ok = TrayUtils.addTray(tray);
		Assert.assertFalse(ok);
		
		String token = tray.getToken();
		
		Tray loadedTray = TrayUtils.getTray(token, elementId);
		Assert.assertNotNull(loadedTray);
		Assert.assertTrue(equal(tray, loadedTray));
	}
	
	@Test
	public void testUpdateAndLoadTray() {
		Tray tray = createTray();
		boolean ok = TrayUtils.addTray(tray);
		Assert.assertTrue(ok);
		String elementId = tray.getElement_id();
		
		String newTitle = "new Title";
		tray.setElement_title(newTitle);
		ok = TrayUtils.updateTray(tray);
		Assert.assertTrue(ok);
		String token = tray.getToken();
		Tray loadedTray = TrayUtils.getTray(token, elementId);
		Assert.assertNotNull(loadedTray);
		Assert.assertTrue(equal(tray, loadedTray));
	}
	
	@Test
	public void testDeleteTray() {
		Tray tray = createTray();
		String token = tray.getToken();
		boolean ok = TrayUtils.addTray(tray);
		Assert.assertTrue(ok);
		
		ok = TrayUtils.deleteTray(tray);
		Assert.assertTrue(ok);

		List <Tray> trays = TrayUtils.getTrays(token);
		Assert.assertTrue(trays.size() == 0);
	}
	
	@Test
	public void testGetTrays() {
		
		Tray tray = createTray();
		boolean ok = TrayUtils.addTray(tray);
		Assert.assertTrue(ok);
		
		List <Tray> trays = TrayUtils.getTrays(tray.getToken());
		Assert.assertTrue(trays.size() == 1);
		Assert.assertTrue(equal(tray, trays.get(0)));
	}
	
	private Tray createTray() {
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
		return tray;
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

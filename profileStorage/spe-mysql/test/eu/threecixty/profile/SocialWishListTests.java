package eu.threecixty.profile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class SocialWishListTests {

	@Test
	public void test() throws Exception {
		String _3cixtyUID = System.currentTimeMillis() + "";
		UserProfile userProfile = new UserProfile();
		userProfile.setHasUID(_3cixtyUID);
		Set <String> knows = new HashSet <String>();
		String know1 = System.currentTimeMillis() + "12";
		String know2 = System.currentTimeMillis() + "34" ;
		knows.add(know1);
		knows.add(know2);
		userProfile.setKnows(knows);
		new MySQLProfileManagerImpl().saveProfile(userProfile);
		
		UserProfile loadedProfile = new MySQLProfileManagerImpl().getProfile(_3cixtyUID);
		Assert.assertNotNull(loadedProfile);
		
		
		// create know1 profile with two trays
		UserProfile profile1 = new UserProfile();
		userProfile.setHasUID(know1);
		new MySQLProfileManagerImpl().saveProfile(profile1);
		String elementId1 = "Tray 0001";
		String type = "Poi";
		Tray tray1 = createTray(know1, elementId1, type);
		MySQLTrayManager.getInstance().addTray(tray1);
		String elementId2 = "Tray 0002";
		Tray tray2 = createTray(know1, elementId2, type);
		MySQLTrayManager.getInstance().addTray(tray2);
		
		// create know2 profile with one tray
		UserProfile profile2 = new UserProfile();
		userProfile.setHasUID(know2);
		new MySQLProfileManagerImpl().saveProfile(profile2);
		String elementId3 = "Tray 0003";
		Tray tray3 = createTray(know1, elementId3, type);
		MySQLTrayManager.getInstance().addTray(tray3);
		String elementId4 = "Tray 0004";
		Tray tray4 = createTray(know1, elementId4, "Event");
		MySQLTrayManager.getInstance().addTray(tray4);
		
		List <String> listPoIs = SocialWishListUtils.getPoIsFromFriendsWishList(_3cixtyUID);
		Assert.assertNotNull(listPoIs);
		Assert.assertTrue(listPoIs.size() == 3);
		Assert.assertTrue(listPoIs.contains(elementId1));
		Assert.assertTrue(listPoIs.contains(elementId2));
		Assert.assertTrue(listPoIs.contains(elementId3));
		
		List <String> listEvents = SocialWishListUtils.getEventsFromFriendsWishList(_3cixtyUID);
		Assert.assertNotNull(listEvents);
		Assert.assertTrue(listEvents.size() == 1);
		Assert.assertTrue(listEvents.contains(elementId4));
	}
	
	private Tray createTray(String uid, String elementId, String type) {
		Tray tray = new Tray();
		boolean attended = true;
		String title = "abc...xyz@&é";
		String source = "TestSource";
		String imageUrl = "fake URL";
		String dateTimeAttended = "Fake DateTime";
		int rating = 4;
		tray.setToken(uid);
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
}

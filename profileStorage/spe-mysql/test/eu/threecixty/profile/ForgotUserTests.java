package eu.threecixty.profile;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ForgotUserTests {

	private static ForgottenUserManager forgottenUserManager;
	
	@BeforeClass
	public static void setup() {
		forgottenUserManager = new ForgottenUserManagerImpl();
	}
	
	@Test
	public void testIsBlockedByUidOwner() throws Exception {
		String fakeUID = System.currentTimeMillis() + "";
		Assert.assertFalse(forgottenUserManager.isBlockedByUidOwner(fakeUID));
		
		String uid = System.currentTimeMillis() + "";
		boolean ok = forgottenUserManager.setPreventUserFromCrawling(uid);
		Assert.assertTrue(ok);
		
		Assert.assertTrue(forgottenUserManager.isBlockedByUidOwner(uid));
	}
	
	@Test
	public void testRemove() throws Exception {
		String uid = System.currentTimeMillis() + "";
		boolean ok = forgottenUserManager.setPreventUserFromCrawling(uid);
		Assert.assertTrue(ok);
		Assert.assertTrue(forgottenUserManager.isBlockedByUidOwner(uid));
		
		boolean removed = forgottenUserManager.remove(uid);
		Assert.assertTrue(removed);
		
		Assert.assertFalse(forgottenUserManager.isBlockedByUidOwner(uid)); // already removed
	}
	
	@Test
	public void testIsCrawlable() throws Exception {
		String uid = System.currentTimeMillis() + "";
		String know = System.currentTimeMillis() + "";
		boolean crawlable1 = forgottenUserManager.isCrawlable(uid, know);
		Assert.assertTrue(crawlable1);
		
		boolean ok = forgottenUserManager.add(uid, know);
		Assert.assertTrue(ok);
		boolean crawlable2 = forgottenUserManager.isCrawlable(uid, know);
		Assert.assertFalse(crawlable2);
		
		boolean removed = forgottenUserManager.remove(uid, know);
		Assert.assertTrue(removed);
		
		boolean crawlable3 = forgottenUserManager.isCrawlable(uid, know);
		Assert.assertTrue(crawlable3);
	}
	
	@Test
	public void testAddAndRemoveForSet() {
		String uid = System.currentTimeMillis() + "";
		String know1 = System.currentTimeMillis() + "001";
		String know2 = System.currentTimeMillis() + "002";
		String know3 = System.currentTimeMillis() + "003";
		Set <String> knows = new HashSet <String>();
		knows.add(know1);
		knows.add(know2);
		knows.add(know3);
		boolean added = forgottenUserManager.add(uid, knows);
		Assert.assertTrue(added);
		
		boolean crawlable1 = forgottenUserManager.isCrawlable(uid, know2);
		Assert.assertFalse(crawlable1);
		
		String non_existedKnow = System.currentTimeMillis() + "0011";
		
		Set <String> removedSet = new HashSet <String>();
		removedSet.add(know2);
		removedSet.add(know1);
		removedSet.add(non_existedKnow);
		boolean removed = forgottenUserManager.remove(uid, removedSet);
		Assert.assertTrue(removed);
		
		boolean crawlable2 = forgottenUserManager.isCrawlable(uid, know2);
		Assert.assertTrue(crawlable2);
	}
}

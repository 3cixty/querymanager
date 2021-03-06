/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import eu.threecixty.profile.oldmodels.Accompanying;
import eu.threecixty.profile.oldmodels.Address;
import eu.threecixty.profile.oldmodels.Name;
import eu.threecixty.profile.oldmodels.ProfileIdentities;

public class UserProfileTests {

	@Test
	public void testName() {
		String _3cixtyUID = System.currentTimeMillis() + "";
		Name name = new Name();
		String firstName = "Abc123àçè";
		String lastName = "noName";
		name.setFamilyName(lastName);
		name.setGivenName(firstName);
		UserProfile userProfile = new UserProfile();
		userProfile.setHasUID(_3cixtyUID);
		userProfile.setHasName(name);
		try {
			new MySQLProfileManagerImpl().saveProfile(userProfile);
		} catch (TooManyConnections e) {
			e.printStackTrace();
		}
		
		UserProfile loadedProfile = null;
		try {
			loadedProfile = new MySQLProfileManagerImpl().getProfile(_3cixtyUID);
			Name loadedName = loadedProfile.getHasName();
			Assert.assertTrue(firstName.equals(loadedName.getGivenName()));
			Assert.assertTrue(lastName.equals(loadedName.getFamilyName()));
		} catch (TooManyConnections e) {
			e.printStackTrace();
		}
		
		lastName = null;
		loadedProfile.getHasName().setFamilyName(lastName);
		try {
			new MySQLProfileManagerImpl().saveProfile(loadedProfile);
			
			loadedProfile = new MySQLProfileManagerImpl().getProfile(_3cixtyUID);
			Name loadedName = loadedProfile.getHasName();
			Assert.assertTrue(loadedName.getFamilyName() == null);
		} catch (TooManyConnections e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testAddress() throws TooManyConnections {
		String _3cixtyUID = System.currentTimeMillis() + "";
		Address address = new Address();
		String country = "France";
		String town = "Paris";
		String street = "78 volduceau";
		double latitude = 48.3;
		double longitude = 19.7;
		String postalCode = "75000";
		address.setCountryName(country);
		address.setTownName(town);
		address.setStreetAddress(street);
		address.setPostalCode(postalCode);
		address.setLatitude(latitude);
		address.setLongitute(longitude);
		UserProfile userProfile = new UserProfile();
		userProfile.setHasUID(_3cixtyUID);
		userProfile.setHasAddress(address);

		new MySQLProfileManagerImpl().saveProfile(userProfile);
		
		UserProfile loadedProfile = new MySQLProfileManagerImpl().getProfile(_3cixtyUID);
		Address loadedAddress = loadedProfile.getHasAddress();
		Assert.assertTrue(country.equals(loadedAddress.getCountryName()));
		Assert.assertTrue(town.equals(loadedAddress.getTownName()));
		Assert.assertTrue(street.equals(loadedAddress.getStreetAddress()));
		Assert.assertTrue(postalCode.equals(loadedAddress.getPostalCode()));
		Assert.assertTrue(latitude == loadedAddress.getLatitude());
		Assert.assertTrue(longitude == loadedAddress.getLongitute());
		
		country = null;
		loadedProfile.getHasAddress().setCountryName(country);
		new MySQLProfileManagerImpl().saveProfile(loadedProfile);
		UserProfile loadedProfile2 = new MySQLProfileManagerImpl().getProfile(_3cixtyUID);
		String loadedCountry = loadedProfile2.getHasAddress().getCountryName();
		Assert.assertTrue(loadedCountry == null || loadedCountry.equals(""));
	}
	
	@Test
	public void testProfileImageAndLastTime() throws TooManyConnections {
		String _3cixtyUID = System.currentTimeMillis() + "";
		String profileImage = "fake url img";
		UserProfile userProfile = new UserProfile();
		userProfile.setHasUID(_3cixtyUID);
		userProfile.setProfileImage(profileImage);
		String lastTime = System.currentTimeMillis() + "";
		userProfile.setHasLastCrawlTime(lastTime);
		new MySQLProfileManagerImpl().saveProfile(userProfile);
		
		UserProfile loadedProfile = new MySQLProfileManagerImpl().getProfile(_3cixtyUID);
		Assert.assertTrue(profileImage.equals(loadedProfile.getProfileImage()));
		Assert.assertTrue(lastTime.equals(loadedProfile.getHasLastCrawlTime()));
	}
	
	@Test
	public void testKnows() throws TooManyConnections {
		String _3cixtyUID = System.currentTimeMillis() + "";
		UserProfile userProfile = new UserProfile();
		userProfile.setHasUID(_3cixtyUID);
		Set <String> knows = new HashSet <String>();
		String know1 = "123456789";
		String know2 = "987654321";
		knows.add(know1);
		knows.add(know2);
		userProfile.setKnows(knows);
		new MySQLProfileManagerImpl().saveProfile(userProfile);
		
		UserProfile loadedProfile = new MySQLProfileManagerImpl().getProfile(_3cixtyUID);
		Set <String> loadedKnows = loadedProfile.getKnows();
		Assert.assertTrue(loadedKnows.contains(know1));
		Assert.assertTrue(loadedKnows.contains(know2));
		Assert.assertTrue(loadedKnows.size() == 2);
		
		loadedKnows.remove(know2);
		String know3 = "333333333333";
		loadedKnows.add(know3);
		loadedProfile.setKnows(loadedKnows);
		new MySQLProfileManagerImpl().saveProfile(loadedProfile);
		
		UserProfile loadedProfile2 = new MySQLProfileManagerImpl().getProfile(_3cixtyUID);
		Set <String> loadedKnows2 = loadedProfile2.getKnows();
		Assert.assertTrue(loadedKnows2.contains(know1));
		Assert.assertTrue(!loadedKnows2.contains(know2));
		Assert.assertTrue(loadedKnows2.contains(know3));
	}
	
	@Test
	public void testAccounts() throws TooManyConnections {
		String _3cixtyUID = System.currentTimeMillis() + "";
		UserProfile userProfile = new UserProfile();
		userProfile.setHasUID(_3cixtyUID);

		Set <ProfileIdentities> pis = new HashSet <ProfileIdentities>();
		ProfileIdentities pi1 = new ProfileIdentities();
		String source1 = "Google";
		String account1 = "88888888888";
		pi1.setHasSourceCarrier(source1);
		pi1.setHasUserAccountID(account1);
		pis.add(pi1);
		
		ProfileIdentities pi2 = new ProfileIdentities();
		String source2 = "Mobidot";
		String account2 = "11111111";
		pi2.setHasSourceCarrier(source2);
		pi2.setHasUserAccountID(account2);
		pis.add(pi2);
		
		userProfile.setHasProfileIdenties(pis);
		new MySQLProfileManagerImpl().saveProfile(userProfile);
		
		UserProfile loadedProfile = new MySQLProfileManagerImpl().getProfile(_3cixtyUID);
		Set <ProfileIdentities> loadedPis = loadedProfile.getHasProfileIdenties();
		Assert.assertTrue(loadedPis.size() == 2);
		Assert.assertTrue(exists(account1, source1, loadedPis));
		Assert.assertTrue(exists(account2, source2, loadedPis));
		
		ProfileIdentities pi3 = new ProfileIdentities();
		String source3 = "Facebook";
		String account3 = "FFFFFFFFFF";
		pi3.setHasSourceCarrier(source3);
		pi3.setHasUserAccountID(account3);
		loadedPis.clear();
		loadedPis.add(pi3);
		loadedProfile.setHasProfileIdenties(loadedPis);
		new MySQLProfileManagerImpl().saveProfile(loadedProfile);
		
		UserProfile loadedProfile2 = new MySQLProfileManagerImpl().getProfile(_3cixtyUID);
		Set <ProfileIdentities> loadedPis2 = loadedProfile2.getHasProfileIdenties();
		Assert.assertTrue(loadedPis2.size() == 1);
		Assert.assertTrue(exists(account3, source3, loadedPis2));
	}
	
	@Test
	public void testAccompanyings() throws TooManyConnections {
		String _3cixtyUID = System.currentTimeMillis() + "";
		UserProfile userProfile = new UserProfile();
		userProfile.setHasUID(_3cixtyUID);

		Accompanying accompany = new Accompanying();
		Long id1 = 12L;
		String IDUser2 = "32";
		String IDUser1 = "22";
		Double score = 2d;
		Long time = 100L;
		Long validity = 200L;
        accompany.setHasAccompanyId(id1);
        accompany.setHasAccompanyUserid2ST(IDUser2);
        accompany.setHasAccompanyUserid1ST(IDUser1);
        accompany.setHasAccompanyScore(score);
        accompany.setHasAccompanyTime(time);
        accompany.setHasAccompanyValidity(validity);
        
        Set <Accompanying> accompanyings = new HashSet <Accompanying>();
        accompanyings.add(accompany);
        userProfile.setAccompanyings(accompanyings);
        new MySQLProfileManagerImpl().saveProfile(userProfile);
        
        UserProfile loadedProfile = new MySQLProfileManagerImpl().getProfile(_3cixtyUID);
        Set <Accompanying> loadedAccompanyings = loadedProfile.getAccompanyings();
        Assert.assertNotNull(loadedAccompanyings);
        Assert.assertTrue(loadedAccompanyings.size() == 1);
        Accompanying loadedAccompanying = loadedAccompanyings.iterator().next();
        Assert.assertTrue(accompany.getHasAccompanyId().equals(loadedAccompanying.getHasAccompanyId()));
        Assert.assertTrue(accompany.getHasAccompanyUserid1ST().equals(loadedAccompanying.getHasAccompanyUserid1ST()));
        Assert.assertTrue(accompany.getHasAccompanyUserid2ST().equals(loadedAccompanying.getHasAccompanyUserid2ST()));
        Assert.assertTrue(accompany.getHasAccompanyScore().equals(loadedAccompanying.getHasAccompanyScore()));
        Assert.assertTrue(accompany.getHasAccompanyTime().equals(loadedAccompanying.getHasAccompanyTime()));
        Assert.assertTrue(accompany.getHasAccompanyValidity().equals(loadedAccompanying.getHasAccompanyValidity()));
	}
	
	
	@Test
	public void testListGoogleUids() throws Exception {
		String _3cixtyUID = System.currentTimeMillis() + "";
		UserProfile userProfile = new UserProfile();
		userProfile.setHasUID(_3cixtyUID);

		Set <ProfileIdentities> pis = new HashSet <ProfileIdentities>();
		ProfileIdentities pi1 = new ProfileIdentities();
		String source1 = "Google";
		String account1 = "99999999";
		pi1.setHasSourceCarrier(source1);
		pi1.setHasUserAccountID(account1);
		pis.add(pi1);
		
		ProfileIdentities pi2 = new ProfileIdentities();
		String source2 = "Mobidot";
		String account2 = "11111111";
		pi2.setHasSourceCarrier(source2);
		pi2.setHasUserAccountID(account2);
		pis.add(pi2);
		
		userProfile.setHasProfileIdenties(pis);
		new MySQLProfileManagerImpl().saveProfile(userProfile);
		
		Set <String> _3cixtyUIDs = new HashSet <String>();
		_3cixtyUIDs.add(_3cixtyUID);
		
		List <String> googleUids = UserUtils.getGoogleUidsFrom3cixtyUIDs(_3cixtyUIDs);
		Assert.assertTrue(googleUids.size() == 1);
		Assert.assertTrue(account1.equals(googleUids.get(0)));
	}
	
	@Test
	public void testFind3cixtyUids() throws Exception {
		String _3cixtyUID = System.currentTimeMillis() + "";
		UserProfile userProfile = new UserProfile();
		userProfile.setHasUID(_3cixtyUID);

		Set <ProfileIdentities> pis = new HashSet <ProfileIdentities>();
		ProfileIdentities pi1 = new ProfileIdentities();
		String source1 = "Google";
		String account1 = "77777";
		pi1.setHasSourceCarrier(source1);
		pi1.setHasUserAccountID(account1);
		pis.add(pi1);
		
		ProfileIdentities pi2 = new ProfileIdentities();
		String source2 = "Mobidot";
		String account2 = "2222222";
		pi2.setHasSourceCarrier(source2);
		pi2.setHasUserAccountID(account2);
		pis.add(pi2);
		
		userProfile.setHasProfileIdenties(pis);
		new MySQLProfileManagerImpl().saveProfile(userProfile);
		
		List <String> googleUids = new LinkedList<String>();
		googleUids.add(account1);
		
		Set <String> loaded3cixtyUids = new MySQLProfileManagerImpl().find3cixtyUIDs(googleUids, source1, null);
		// may contain 3cixty UIDs which were generated during precedent tests
		Assert.assertTrue(loaded3cixtyUids.size() >= 1);
		Assert.assertTrue(loaded3cixtyUids.contains(_3cixtyUID));
		
		List <String> mobidotUids = new LinkedList<String>();
		mobidotUids.add(account2);
		Set <String> loaded3cixtyUids2 = new MySQLProfileManagerImpl().find3cixtyUIDs(mobidotUids, source2, null);
		// may contain 3cixty UIDs which were generated during precedent tests
		Assert.assertTrue(loaded3cixtyUids2.size() >= 1);
		Assert.assertTrue(loaded3cixtyUids2.contains(_3cixtyUID));
	}
	
	private boolean exists(String accountID, String source, Set <ProfileIdentities> pis) {
		for (ProfileIdentities pi: pis) {
			if (pi.getHasUserAccountID().equals(accountID)
					&& pi.getHasSourceCarrier().equals(source)) return true;
		}
		return false;
	}
}

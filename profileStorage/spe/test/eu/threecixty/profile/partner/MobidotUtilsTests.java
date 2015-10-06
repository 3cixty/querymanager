package eu.threecixty.profile.partner;

import org.junit.Assert;
import org.junit.Test;

import eu.threecixty.profile.partners.MobidotUserUtils;

public class MobidotUtilsTests {

	@Test
	public void testName() {
		int ID=0;
		try{
			 ID=MobidotUserUtils.getMaxMobidotID(10118, 9429);
			System.out.println(ID);
		}catch(Exception e){
			e.printStackTrace();
		}
		Assert.assertTrue(ID==9429);
	}

}

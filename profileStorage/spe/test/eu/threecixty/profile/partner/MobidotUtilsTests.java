package eu.threecixty.profile.partner;

import org.junit.Assert;
import org.junit.Test;

import eu.threecixty.profile.partners.MobidotUserUtils;

public class MobidotUtilsTests {

	@Test
	public void testName() {
		String ID="";
		try{
			 ID=MobidotUserUtils.getMaxMobidotID("10118", "9429");
			System.out.println(ID);
		}catch(Exception e){
			e.printStackTrace();
		}
		Assert.assertTrue(ID.equals("9429"));
	}

}

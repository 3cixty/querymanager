
/**
 * @file	PrivacyAuthorityTest.java
 * @brief 
 * @date	Oct 9, 2014
 * @author	Flore Lantheaume
 * @copyright THALES 2014. All rights reserved.
 * THALES PROPRIETARY/CONFIDENTIAL.
 */


package eu.threecixty.privacymanager;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.theresis.humanization.privacy.CertificationAndPrivacyRequest;
import org.theresis.humanization.privacy.CertificationAndPrivacyRequest.RequestStatus;
import org.theresis.humanization.privacy.CertificationRequestId;
import org.theresis.humanization.privacy.PrivacyCertAuthorityFactory;
import org.theresis.humanization.privacy.PrivacyDBInitialize;
import org.theresis.humanization.privacy.PrivacyException;
import org.theresis.humanization.privacy.conf.PrivacyAuthorityConf;

/**
 * Test of the privacy authority
 */
@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PrivacyAuthorityTest {

	private static String					passwordSA		= "toto";
	private static String					passwordAPI		= "titi";
	private static String					passwordAdmin	= "tata";
	private static CertificationRequestId	reqId			= null;
	private static CertificationRequestId	reqIdUpdate		= null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		PrivacyAuthorityConf.setPropertyFile("DefaultPrivacyAuthority.properties");
		PrivacyDBInitialize.resetAndInit(passwordSA, passwordSA, passwordAPI, passwordAdmin);
		reqId = null;
		reqIdUpdate = null;
	}
	
	
	@Test
	public void test1_certifyContractPEMCSR() {
		
		System.out.println("*** certifyContractPEMCSR");
		
		CertificationAndPrivacyRequest auth = null;
		try {
			
			auth = PrivacyCertAuthorityFactory.build( passwordAPI );
			
			CertificationAndPrivacyRequest.PocInformation 
				poc = new CertificationAndPrivacyRequest.PocInformation("3cixty", "poalo sino", "poalo.sini@tin.it", "+336728972872");
			
			FileInputStream certificateSigningRequest;
			FileInputStream privacyContract;
			
			certificateSigningRequest = new FileInputStream("./src/test/resources/exploreMi360.csr");
			privacyContract = new FileInputStream("./src/test/resources/PrivacyContract_ExploreMi360_example.xml");

			reqId = auth.certifyMyContract(poc, certificateSigningRequest , privacyContract);
			assertNotNull( reqId.toString()  );
			
			
		} catch (FileNotFoundException e2) {
			fail( e2.getMessage() );
		}
		catch (PrivacyException e) {
			fail( e.getMessage() );
		}
		finally {
			if ( auth != null ) {
				auth.terminate();
			}
		}		
	}
	
	@Test
	public void test2_certifyContractDERCSR() {
		
		System.out.println("*** certifyContractDERCSR");
		
		CertificationAndPrivacyRequest auth = null;
		try {

			auth = PrivacyCertAuthorityFactory.build( passwordAPI );
			
			CertificationAndPrivacyRequest.PocInformation 
				poc = new CertificationAndPrivacyRequest.PocInformation("3cixty", "poalo sino", "poalo.sini@tin.it", "+336728972872");
			
			FileInputStream certificateSigningRequest;
			FileInputStream privacyContract;
			
			certificateSigningRequest = new FileInputStream("./src/test/resources/exploreMi360.der");
			privacyContract = new FileInputStream("./src/test/resources/PrivacyContract_ExploreMi360_example.xml");

			CertificationRequestId reqId = auth.certifyMyContract(poc, certificateSigningRequest , privacyContract);
			assertNotNull( reqId );
			
		} catch (FileNotFoundException e2) {
			fail( e2.getMessage() );
		}
		catch (PrivacyException e) {
			assertEquals("Application ExploreMi 360 is already certified. Use updateMyContract to update the privacy contract", e.getMessage() );
		}
		finally {
			if ( auth != null ) {
				auth.terminate();
			}
		}		
	}	
	
	@Test
	public void test3_getRequestStatusPending() {
		
		System.out.println("*** getRequestStatusPending");
		
		CertificationAndPrivacyRequest auth = null;
		try {

			auth = PrivacyCertAuthorityFactory.build( passwordAPI );
			
			FileInputStream certificateSigningRequest;
			certificateSigningRequest = new FileInputStream("./src/test/resources/exploreMi360.csr");

			RequestStatus reqStatus = auth.getRequestStatus( certificateSigningRequest, reqId );
			
			assertEquals( RequestStatus.PENDING, reqStatus );
			
		} catch (FileNotFoundException e2) {
			fail( e2.getMessage() );
		}
		catch (PrivacyException e) {
			assertEquals("Application ExploreMi 360 is already certified. Use updateMyContract to update the privacy contract", e.getMessage() );
		}
		finally {
			if ( auth != null ) {
				auth.terminate();
			}
		}		
	}
	
	@Test
	public void test4_getUnknownRequestStatus() {
		
		System.out.println("*** getUnknownRequestStatus");
				
		CertificationAndPrivacyRequest auth = null;
		try {

			auth = PrivacyCertAuthorityFactory.build( passwordAPI );
			
			FileInputStream certificateSigningRequest;
			certificateSigningRequest = new FileInputStream("./src/test/resources/exploreMi360.csr");

			RequestStatus reqStatus = auth.getRequestStatus( certificateSigningRequest, new CertificationRequestId( UUID.randomUUID() ) );
			
			assertEquals( RequestStatus.UNKNOWN, reqStatus );
			
		} catch (FileNotFoundException e2) {
			fail( e2.getMessage() );
		}
		catch (PrivacyException e) {
			assertEquals("Application ExploreMi 360 is already certified. Use updateMyContract to update the privacy contract", e.getMessage() );
		}
		finally {
			if ( auth != null ) {
				auth.terminate();
			}
		}		
	}	
	
	@Test
	public void test5_updateSameContract() {
		
		System.out.println("*** updateSameContract()");
		
		CertificationAndPrivacyRequest auth = null;
		try {
			
			auth = PrivacyCertAuthorityFactory.build( passwordAPI );
						
			FileInputStream certificateSigningRequest;
			FileInputStream privacyContract;
			
			certificateSigningRequest = new FileInputStream("./src/test/resources/exploreMi360.csr");
			privacyContract = new FileInputStream("./src/test/resources/PrivacyContract_ExploreMi360_example.xml");

			reqId = auth.updateMyContract(certificateSigningRequest , privacyContract);
						
		} catch (FileNotFoundException e2) {
			fail( e2.getMessage() );
		}
		catch (PrivacyException e) {
			assertTrue(e.getMessage().compareToIgnoreCase("Privacy contract already registered for this applictaion version ExploreMi 360. Please modify your applictaion version to update the contract.")== 0);
		}
		finally {
			if ( auth != null ) {
				auth.terminate();
			}
		}		
	}	
	
	@Test
	public void test6_updateContractUnknowApp() {
		
		System.out.println("*** updateContractUnknowApp()");
		
		CertificationAndPrivacyRequest auth = null;
		try {
			
			auth = PrivacyCertAuthorityFactory.build( passwordAPI );
						
			FileInputStream certificateSigningRequest;
			FileInputStream privacyContract;
			
			certificateSigningRequest = new FileInputStream("./src/test/resources/exploreMi360.csr");
			privacyContract = new FileInputStream("./src/test/resources/PrivacyContract_RestoMi_example.xml");
			
			reqId = auth.updateMyContract(certificateSigningRequest , privacyContract);
			fail();
						
		} catch (FileNotFoundException e2) {
			fail( e2.getMessage() );
		}
		catch (PrivacyException e) {
			assertTrue(e.getMessage().compareToIgnoreCase("Unknown Application RestoMi. Please use certifyMyContract to register your application.")== 0);
		}
		finally {
			if ( auth != null ) {
				auth.terminate();
			}
		}		
	}	
	
	@Test
	public void test7_updateContract() {
		
		System.out.println("*** updateContract()");
		
		CertificationAndPrivacyRequest auth = null;
		try {
			
			auth = PrivacyCertAuthorityFactory.build( passwordAPI );
						
			FileInputStream certificateSigningRequest;
			FileInputStream privacyContract;
			
			certificateSigningRequest = new FileInputStream("./src/test/resources/exploreMi360.csr");
			privacyContract = new FileInputStream("./src/test/resources/Update_PrivacyContract_ExploreMi360_example.xml");

			reqIdUpdate = auth.updateMyContract(certificateSigningRequest , privacyContract);
			
			assertTrue( reqIdUpdate != null );
						
		} catch (FileNotFoundException e2) {
			fail( e2.getMessage() );
		}
		catch (PrivacyException e) {
			fail( e.getMessage() );
		}
		finally {
			if ( auth != null ) {
				auth.terminate();
			}
		}		
	}		
	
}

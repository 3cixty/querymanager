/**
 * @file	CertificationToolBox.java
 * @brief 
 * @date	Dec 3, 2014
 * @author	Flore Lantheaume
 * @copyright THALES 2014. All rights reserved.
 * THALES PROPRIETARY/CONFIDENTIAL.
*/

package eu.threecixty.privacymanager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;

import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.theresis.humanization.privacy.CertificationAndPrivacyRequest;
import org.theresis.humanization.privacy.CertificationRequestId;
import org.theresis.humanization.privacy.PrivacyCertAuthorityFactory;
import org.theresis.humanization.privacy.PrivacyException;
import org.theresis.humanization.privacy.ca.Administrator;
import org.theresis.humanization.privacy.generated.PrivacyContract;

/**
 */
public class CertificationToolBox {

	private static X509Certificate		certificateCA = null;
	private static PrivateKey 			privateKeyCA = null;
	
	static {
		
		try {
			FileInputStream fis;
			fis = new FileInputStream("./src/test/resources/test-ca.crt");

			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Collection<? extends Certificate> c = cf.generateCertificates(fis);
			Iterator<? extends Certificate> i = c.iterator();
			i.hasNext();
			certificateCA = (X509Certificate)i.next();
									
			String password = "toto";
			String privateKeyFileName = "./src/test/resources/test-ca.key";
			File privateKeyFile = new File(privateKeyFileName); // private key file in PEM format
			// TODO : traiter le cas DER ... A VOIR
			PEMParser pemParser = new PEMParser(new FileReader(privateKeyFile));
			Object pemKey = pemParser.readObject();
			PEMDecryptorProvider decProv = new   JcePEMDecryptorProviderBuilder().build(password.toCharArray());
			JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
			KeyPair kp;
			if (pemKey instanceof PEMEncryptedKeyPair) {
			    kp = converter.getKeyPair(((PEMEncryptedKeyPair) pemKey).decryptKeyPair(decProv));				
			}
			else {
				 // Unencrypted key - no password needed
			    PEMKeyPair ukp = (PEMKeyPair) pemKey;
			    kp = converter.getKeyPair(ukp);				
			}
			privateKeyCA = kp.getPrivate();
			pemParser.close();		
		}
		catch (FileNotFoundException e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void certifyApplicationContract( String appId, String csrPath, String pcPath, String pwd)
	{
		CertificationAndPrivacyRequest auth = null;
		try {
			
			auth = PrivacyCertAuthorityFactory.build( );
			
			CertificationAndPrivacyRequest.PocInformation 
				poc = new CertificationAndPrivacyRequest.PocInformation("3cixty", "poalo sino", "poalo.sini@tin.it", "+336728972872");
			
			FileInputStream certificateSigningRequest;
			FileInputStream privacyContract;
			
			certificateSigningRequest = new FileInputStream( csrPath);
			privacyContract = new FileInputStream( pcPath);

			CertificationRequestId reqId = auth.certifyMyContract( appId, poc, certificateSigningRequest , privacyContract);
						
			acceptRequest( reqId, pwd);
			
			// debug
			PrivacyContract pc = auth.getPrivacyContract(appId, "1.0");
			System.out.println("coucou flore");
			if (  pc.getContract().getPublication() != null ) {
				System.out.println(" public path = " + pc.getContract().getPublication().getPropertyPath() );
			}
			// fin debug			
			
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		catch (PrivacyException e) {
			e.printStackTrace();
		}
		finally {
			if ( auth != null ) {
				auth.terminate();
			}
		}				
	}
	
	public static void acceptRequest( CertificationRequestId reqId, String passwordAdmin) 
			throws PrivacyException 
	{
		Administrator admin = new Administrator(passwordAdmin);
		admin.acceptRequest( reqId, certificateCA, privateKeyCA );
		admin.terminate();
	}
}

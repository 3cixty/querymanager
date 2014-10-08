package eu.threecixty.privacymanager;

import static org.junit.Assert.fail;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.theresis.humanization.privacy.generated.Domain;
import org.theresis.humanization.privacy.generated.PrivacyContract;


@RunWith(JUnit4.class)
public class PrivacyApisTest {

	@Test
	public void generatePrivacyContract( ) {

		PrivacyContract privacyContract = new PrivacyContract( );
		
		PrivacyContract.Application pc_app = new PrivacyContract.Application( );
		pc_app.setAuthor( "Cactus Software Ltd");
		pc_app.setDescription( "Cactus cult");
		PrivacyContract.Application.Domains domains = new PrivacyContract.Application.Domains( );
		domains.getDomain().add( Domain.RELIGION );
		domains.getDomain().add( Domain.OTHER );
		pc_app.setDomains( domains );
		pc_app.setName( "Saguaro");
		pc_app.setVersion( "0.1beta");
		privacyContract.setApplication( pc_app );
		
		PrivacyContract.Contract pc_contract = new PrivacyContract.Contract( );
		PrivacyContract.Contract.Namespaces nss = new PrivacyContract.Contract.Namespaces( ); 
		PrivacyContract.Contract.Namespaces.Namespace ns1 = new PrivacyContract.Contract.Namespaces.Namespace( );
		ns1.setPrefix( "cactus" );
		ns1.setUri( "www.cacsoft.eu/ontos/cactus/1.0/" );
		nss.getNamespace().add(ns1);
		pc_contract.setNamespaces(nss);
		
		PrivacyContract.Contract.PropertyPaths pps = new PrivacyContract.Contract.PropertyPaths( );
		PrivacyContract.Contract.PropertyPaths.PropertyPath pp1 = new PrivacyContract.Contract.PropertyPaths.PropertyPath( );
		pp1.setType("mandatory");
		pp1.setValue(":/cactusType/label");
		pps.getPropertyPath().add(pp1);
		PrivacyContract.Contract.PropertyPaths.PropertyPath pp2 = new PrivacyContract.Contract.PropertyPaths.PropertyPath( );
		pp2.setType("optional");
		pp2.setValue(":/flowerKind/label");
		pps.getPropertyPath().add(pp2);
		pc_contract.setPropertyPaths(pps);
		pc_contract.setTextDescription("Help !\nI kneed somebody, help !");
		privacyContract.setContract( pc_contract );
        
		try {
			JAXBContext jc;
			jc = JAXBContext.newInstance( "org.theresis.humanization.privacy.generated" );
	        Marshaller m = jc.createMarshaller();
	        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

        	System.out.println( "SaguaroPrivacyContract.xml dump --- begin ---\n" );
	        m.marshal( privacyContract, System.out );
        	System.out.println( "\nSaguaroPrivacyContract.xml dump --- end ---\n" );
	        
		} catch (JAXBException e) {
			fail(e.getMessage());
		}
	}
}

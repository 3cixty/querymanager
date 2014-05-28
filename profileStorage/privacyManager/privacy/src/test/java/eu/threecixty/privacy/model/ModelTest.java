package eu.threecixty.privacy.model;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import eu.threecixty.privacy.semantic.Entity;
import eu.threecixty.privacy.semantic.Model;
import eu.threecixty.privacy.semantic.Resource;
import eu.threecixty.privacy.semantic.Scope;
import eu.threecixty.privacy.semantic.Statement;

public class ModelTest {

	Model model;
	Entity paymentByVisa;
	Entity paymentByMastercard;
	
	@Before
	public void setUp() throws Exception {
		model = new ModelImpl("http://purl.org/goodrelations/v1");
		paymentByVisa = model.newEntity("PaymentMethodCreditCard|VISA");
		paymentByMastercard = model.newEntity("PaymentMethodCreditCard|MASTERCARD");
	}

	@Test
	public void testEntityGetters() {
		assertEquals("http://purl.org/goodrelations/v1#PaymentMethodCreditCard|VISA", paymentByVisa.getEntityAsString());
		assertEquals("VISA", paymentByVisa.getEntityIDAsString());
		assertEquals("PaymentMethodCreditCard", paymentByVisa.getEntityTypeAsShortString());
		assertEquals("http://purl.org/goodrelations/v1#PaymentMethodCreditCard", paymentByVisa.getEntityTypeAsString());
		assertEquals("http://purl.org/goodrelations/v1", paymentByVisa.getOntologyURL());

		assertEquals("http://purl.org/goodrelations/v1#PaymentMethodCreditCard|MASTERCARD", paymentByMastercard.getEntityAsString());
		assertEquals("MASTERCARD", paymentByMastercard.getEntityIDAsString());
		assertEquals("PaymentMethodCreditCard", paymentByMastercard.getEntityTypeAsShortString());
		assertEquals("http://purl.org/goodrelations/v1#PaymentMethodCreditCard", paymentByMastercard.getEntityTypeAsString());
		assertEquals("http://purl.org/goodrelations/v1", paymentByMastercard.getOntologyURL());
	}
	
	@Test
	public void testGetEntities() {
		Set<Entity> entities = model.getEntities();
		assertEquals(2, entities.size());
		assertTrue(entities.contains(paymentByVisa));
		assertTrue(entities.contains(paymentByMastercard));
	}

	@Test
	public void testNewScope() {
		Scope scope = model.newScope("Currency|Bitcoin");
		assertEquals("http://purl.org/goodrelations/v1#Currency|Bitcoin", scope.getEntityAsString());
		assertEquals("Bitcoin", scope.getEntityIDAsString());
		assertEquals("Currency", scope.getEntityTypeAsShortString());
		assertEquals("http://purl.org/goodrelations/v1#Currency", scope.getEntityTypeAsString());
		assertEquals("http://purl.org/goodrelations/v1", scope.getOntologyURL());
	}

	@Test
	public void testNewResource() {
		// The URI used in this test is dummy one: it is not defined in http://purl.org/goodrelations/v1
		Resource<String> iban = model.newResource("IBAN", String.class);
		assertEquals("http://purl.org/goodrelations/v1#IBAN", iban.getEntityAsString());
		assertEquals(null, iban.getEntityIDAsString());
		assertEquals("IBAN", iban.getEntityTypeAsShortString());
		assertEquals("http://purl.org/goodrelations/v1#IBAN", iban.getEntityTypeAsString());
		assertEquals("http://purl.org/goodrelations/v1", iban.getOntologyURL());
	}

	@Test
	public void testNewStatement() {
		// The URI used in this test is dummy one: it is not defined in http://purl.org/goodrelations/v1
		Statement stmt = model.newStatement("GetAccountDeposit");
		assertEquals("http://purl.org/goodrelations/v1#GetAccountDeposit", stmt.getEntityAsString());
		assertEquals(null, stmt.getEntityIDAsString());
		assertEquals("GetAccountDeposit", stmt.getEntityTypeAsShortString());
		assertEquals("http://purl.org/goodrelations/v1#GetAccountDeposit", stmt.getEntityTypeAsString());
		assertEquals("http://purl.org/goodrelations/v1", stmt.getOntologyURL());
	}

}

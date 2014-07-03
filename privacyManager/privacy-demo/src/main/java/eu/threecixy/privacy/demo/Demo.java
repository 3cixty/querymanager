package eu.threecixy.privacy.demo;

import java.io.Serializable;
import java.util.Properties;
import java.util.Set;

import eu.threecixty.privacy.model.DefaultModelFactory;
import eu.threecixty.privacy.security.SecureStorage;
import eu.threecixty.privacy.semantic.Entity;
import eu.threecixty.privacy.semantic.Model;
import eu.threecixty.privacy.semantic.ModelFactory;
import eu.threecixty.privacy.semantic.Scope;
import eu.threecixty.privacy.storage.Credential;
import eu.threecixty.privacy.storage.Storage;
import eu.threecixty.privacy.store.SerialStore;
import eu.threecixty.privacy.store.StoreIndex;
import eu.threecixty.privacy.store.db.SQLIndex;

public class Demo {

	public static void main(String[] args) {

		// Create the semantic model
		ModelFactory factory = new DefaultModelFactory();
		final Model model = createProfileModel(factory);
		
		// Create a local store on file system
		Properties props = new Properties();
		props.setProperty(SerialStore.PROP_STORE_DIR, "./demo-fs");
		
		StoreIndex storeIndex = new SQLIndex("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/3cixty-index");
		SerialStore serialStore = new SerialStore(props, storeIndex);
		
		// Add the security layer to the store.
		Storage<Serializable> store = new SecureStorage<Serializable>(serialStore);
		
		// Create a global credential for this demo.
		Credential credential = new Credential() {

			Scope self = model.newScope("self");
			
			public Scope getSubject() {
				return self;
			}
			
		};
	}

	private static Model createProfileModel(ModelFactory factory) {
		Model model = factory.newModel("http://www.eu.3cixty.org/profile");

		Entity entity;

		entity = model.newEntity("author");
		entity = model.newEntity("isSameAs");
		entity = model.newEntity("hasAccompany");
		entity = model.newEntity("hasAccompanyTrip1");
		entity = model.newEntity("hasAccompanyTrip2");
		entity = model.newEntity("hasEducation");
		entity = model.newEntity("hasEmployer");
		entity = model.newEntity("hasEmployerInformation");
		entity = model.newEntity("hasEventDetail");
		entity = model.newEntity("hasEventDetailPreference");

		entity = model.newResource("Accompanying", null);
		entity = model.newResource("Course", null);
		entity = model.newResource("Education", null);
		entity = model.newResource("Employer", null);
		entity = model.newResource("EmployerInformation", null);
		entity = model.newResource("EventDetailPreference", null);
		entity = model.newResource("EventDetails", null);
		entity = model.newResource("EventPreference", null);
		entity = model.newResource("HotelDetail", null);
		entity = model.newResource("HotelDetailPreference", null);
		entity = model.newResource("HotelPreference", null);
		entity = model.newResource("InfraRoute", null);
		entity = model.newResource("Language", null);
		entity = model.newResource("LanguageDetails", null);
		entity = model.newResource("Like", Long.class);
		entity = model.newResource("MappedLocation", null);
		entity = model.newResource("Measurement", null);
		entity = model.newResource("ModalityStatistics", null);
		entity = model.newResource("PersonalPlace", null);
		entity = model.newResource("PlaceDetail", null);
		entity = model.newResource("PlaceDetailPreference", null);
		entity = model.newResource("PlacePreference", null);
		entity = model.newResource("Preference", null);
		entity = model.newResource("ProfileIdentities", null);
		entity = model.newResource("QueryHistory", null);
		entity = model.newResource("Rating", null);
		entity = model.newResource("RatingPreference", null);
		entity = model.newResource("RegularTrip", null);
		entity = model.newResource("School", null);
		entity = model.newResource("SchoolAttended", null);
		entity = model.newResource("Skill", null);
		entity = model.newResource("SocialPreference", null);
		entity = model.newResource("TemporalDetails", null);
		entity = model.newResource("Transport", null);
		entity = model.newResource("Trip", null);
		entity = model.newResource("TripMeasurement", null);
		entity = model.newResource("TripPreference", null);
		entity = model.newResource("UserEnteredRatings", null);
		entity = model.newResource("UserEventRating", null);
		entity = model.newResource("UserHotelRating", null);
		entity = model.newResource("UserPlaceRating", null);
		entity = model.newResource("UserProfile", null);
		entity = model.newResource("Weather", null);

		Set<Entity> entities = model.getEntities();
		return model;
	}

}

package eu.threecixty.CrawlerCron;

import java.net.URL;

import org.junit.Test;

import eu.threecixty.profile.RdfFileManager;

public class CrawlerTests {

	@Test
	public void test() {
		URL resourceUrl = MobilityCrawlerCron.class.getResource("/UserProfileKBmodelWithIndividuals.rdf");
		RdfFileManager.getInstance().setPathToRdfFile(resourceUrl.getPath());
		
		MobilityCrawlerCron task = new MobilityCrawlerCron();
		task.run();
		
		

	}
}

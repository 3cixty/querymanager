package eu.threecixty.privacy.model;

import eu.threecixty.privacy.semantic.Model;
import eu.threecixty.privacy.semantic.ModelFactory;

public class DefaultModelFactory implements ModelFactory {

	public Model newModel(String ontologyURL) {
		return new ModelImpl(ontologyURL);
	}

}

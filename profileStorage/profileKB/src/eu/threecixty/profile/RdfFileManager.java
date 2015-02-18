package eu.threecixty.profile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


/**
 * This class is used to read and update RDF file.
 * @author Cong-Kinh NGUYEN
 *
 */
public class RdfFileManager {
	
	private static final String NULLPATH_EXCEPTION = "Path to RDF file is not set";

	/**Attribute which is absolute path to RDF file*/
	private String absolutePath;
	
	private Model model = null;
	
	public static RdfFileManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * Sets the absolute path to RDF file.
	 * @param absolutePath
	 */
	public void setPathToRdfFile(String absolutePath) {
		this.absolutePath = absolutePath;
	}

	/**
	 * Gets absolute path to RDF file.
	 * @return
	 */
	public String getPathToRdfFile() {
		return absolutePath;
	}
	
	/**
	 * Gets RDF model.
	 * @return
	 */
	public Model getRdfModel() {
		if (model == null) {
			synchronized (this) {
				if (model == null) {
					if (absolutePath == null || "".equals(absolutePath)) {
						throw new RuntimeException(NULLPATH_EXCEPTION);
					}

					try {
						InputStream input = new FileInputStream(absolutePath);
						if (input != null) {
						    model = ModelFactory.createDefaultModel().read(input, "UTF-8");
						    input.close();
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return model;
	}

	/**
	 * Prohibits instantiations.
	 */
	private RdfFileManager() {
	}
	
	/**Singleton holder*/
	private static class SingletonHolder {
		private static final RdfFileManager INSTANCE = new RdfFileManager();
	}
}
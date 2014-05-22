package eu.threecixty.profile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 * This class is used to read and update RDF file.
 * @author Cong-Kinh NGUYEN
 *
 */
public class RdfFileManager {
	private static final Object _sync = new Object();
	
	private static final String NULLPATH_EXCEPTION = "Path to RDF file is not set";
	
	private static RdfFileManager singleton;

	/**Attribute which is absolute path to RDF file*/
	private String absolutePath;
	
	private Model model = null;
	
	public static RdfFileManager getInstance() {
		if (singleton == null) {
			synchronized (_sync) {
				if (singleton == null) singleton = new RdfFileManager();
			}
		}
		return singleton;
	}

	/**
	 * Sets the absolute path to RDF file.
	 * @param absolutePath
	 */
	public void setPathToRdfFile(String absolutePath) {
		this.absolutePath = absolutePath;
	}
	
	/**
	 * Gets RDF model.
	 * @return
	 */
	public Model getRdfModel() {
		if (model == null) {
			synchronized (_sync) {
				if (model == null) {
					if (absolutePath == null || "".equals(absolutePath)) {
						throw new RuntimeException(NULLPATH_EXCEPTION);
					}

					try {
						InputStream input = new FileInputStream(absolutePath);
						if (input != null) {
						    model = ModelFactory.createDefaultModel().read(input, "UTF-8");
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return model;
	}

	/**
	 * Writes a given RDF model to file.
	 * @param model
	 */
	public synchronized void writeModel(Model model) {
		if (model == null) return;
		if (absolutePath == null) throw new RuntimeException(NULLPATH_EXCEPTION);
		try {
			OutputStream output = new FileOutputStream(absolutePath);
			model.write(output, "RDF/XML-ABBREV");
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Prohibits instantiations.
	 */
	private RdfFileManager() {
	}
}

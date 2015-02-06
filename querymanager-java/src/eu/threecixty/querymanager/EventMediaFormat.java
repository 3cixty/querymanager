package eu.threecixty.querymanager;

/**
 * This class is used to define supported EventMedia format.
 *
 * @author Cong-Kinh Nguyen
 *
 */
public class EventMediaFormat {
	private String format;
	
	public static final EventMediaFormat RDF = new EventMediaFormat("rdf");
	public static final EventMediaFormat JSON = new EventMediaFormat("json");
	
	public static EventMediaFormat parse(String format) {
		if (format == null) return null;
		if (format.equals(RDF.format)) return RDF;
		if (format.equals(JSON.format)) return JSON;
		
		return null;
	}
	
	private EventMediaFormat(String format) {
		this.format = format;
	}
}

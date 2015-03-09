package eu.threecixty.querymanager.filter;

import com.thetransactioncompany.cors.CORSConfiguration;
import com.thetransactioncompany.cors.CORSFilter;
import com.thetransactioncompany.cors.Origin;
import com.thetransactioncompany.cors.OriginException;
import com.thetransactioncompany.cors.ValidatedOrigin;

public class DynamicCORSFilter extends CORSFilter {

	/**This attribute is used to dynamically add CORSConfiguration*/
	private static DynamicCORSFilter currentFilter;
	
	public DynamicCORSFilter() {
		super();
		currentFilter = this;
	}
	
	public DynamicCORSFilter(final CORSConfiguration config) {
		super(config);
		currentFilter = this;
	}

	public void addConfiguration(String allowedOrigin) {
		try {
			ValidatedOrigin validatedOrigin = new Origin(allowedOrigin).validate();
			this.getConfiguration().allowedOrigins.add(validatedOrigin);
		} catch (OriginException e) {
			e.printStackTrace();
		}
	}
	
	public static DynamicCORSFilter getCurrentFilter() {
		return currentFilter;
	}
}

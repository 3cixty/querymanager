package eu.threecixty.querymanager.filter;

import java.util.List;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import com.thetransactioncompany.cors.CORSConfiguration;
import com.thetransactioncompany.cors.CORSFilter;
import com.thetransactioncompany.cors.Origin;
import com.thetransactioncompany.cors.OriginException;
import com.thetransactioncompany.cors.ValidatedOrigin;

import eu.threecixty.oauth.OAuthWrappers;

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
	
	public void init(final FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
		
		List <String> allRedirectUris = OAuthWrappers.getAllRedirectUris();
		
		for (String allowedOrigin: allRedirectUris) {
			String processedStr = getRootRedirectUri(allowedOrigin);
			if (processedStr != null) addConfiguration(processedStr);
		}
	}

	public void addConfiguration(String allowedOrigin) {
		try {
			ValidatedOrigin validatedOrigin = new Origin(allowedOrigin).validate();
			this.getConfiguration().allowedOrigins.add(validatedOrigin);
		} catch (OriginException e) {
			e.printStackTrace();
		}
	}
	
	private String getRootRedirectUri(String redirect_uri) {
		int index = redirect_uri.lastIndexOf("/"); // redirect_uri must contain protocol, scheme (http://, https://)
		if (index == 7 || index == 9) return redirect_uri;
		if (index < 9) return null;
		return redirect_uri.substring(0, index);
	}
	
	public static DynamicCORSFilter getCurrentFilter() {
		return currentFilter;
	}
}

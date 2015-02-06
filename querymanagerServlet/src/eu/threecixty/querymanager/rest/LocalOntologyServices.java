package eu.threecixty.querymanager.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/" + Constants.PREFIX_NAME)
public class LocalOntologyServices {

	@GET
	@Path("/foaf")
	public String getFoaf() {
		String fContent = getContent("foaf.owl");
		return (fContent == null) ? "" : fContent;
	}

	@GET
	@Path("/vcard")
	public String getVCard() {
		String vContent = getContent("vcard.owl");
		return (vContent == null) ? "" : vContent;
	}

	@GET
	@Path("/linkedevents")
	public String getLinkedEvents() {
		String vContent = getContent("linkedevents.owl");
		return (vContent == null) ? "" : vContent;
	}

    private String getContent(String filename) {
		try {
			InputStream inStream = new FileInputStream(QueryManagerServices.realPath + File.separatorChar 
					+ "WEB-INF" + File.separatorChar + filename);
			StringBuilder sb = new StringBuilder();
			if (inStream != null) {
				byte [] b = new byte[1024];
				int readBytes = 0;
				while ((readBytes = inStream.read(b)) >= 0) {
					sb.append(new String(b, 0, readBytes));
				}
				inStream.close();
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }
}

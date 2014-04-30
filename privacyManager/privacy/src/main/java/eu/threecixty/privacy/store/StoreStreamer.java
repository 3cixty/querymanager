package eu.threecixty.privacy.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import eu.threecixty.privacy.semantic.Resource;
import eu.threecixty.privacy.storage.Credential;

public interface StoreStreamer {
	OutputStream getOutputStream(Resource<?> resource, Credential credential) throws IOException;

	InputStream getInputStream(Resource<?> resource, Credential credential) throws IOException;
}
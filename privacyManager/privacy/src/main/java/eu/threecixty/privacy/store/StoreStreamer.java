package eu.threecixty.privacy.store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import eu.threecixty.privacy.semantic.Resource;
import eu.threecixty.privacy.storage.Credential;

/**
 * Provides access to {@link Store} managed resources as input and output
 * streams. This access mode is well suited to media meant to be accessed by
 * stream, such as File IO.
 */
public interface StoreStreamer {
	/**
	 * Return an output stream on a {@link Store} managed resource for writing.
	 * @param resource
	 * @param credential
	 * @return
	 * @throws IOException
	 */
	OutputStream getOutputStream(Resource<?> resource, Credential credential)
			throws IOException;

	/**
	 * Return an input stream on a {@link Store} managed resource for reading.
	 * @param resource
	 * @param credential
	 * @return
	 * @throws IOException
	 */
	InputStream getInputStream(Resource<?> resource, Credential credential)
			throws IOException;
}

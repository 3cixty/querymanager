package eu.threecixty.cache;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

public class MemcachedUtils {

	private static final Logger LOGGER = Logger.getLogger(
			MemcachedUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	private static String memcachedServers; // server1:11211 server2:11211
	
	public static MemcachedClient createClient() {
		if (memcachedServers == null) loadMemcachedConfig();
		if (memcachedServers != null) {
		    try {
				return new MemcachedClient(AddrUtil.getAddresses(memcachedServers));
			} catch (IOException e) {
				if (DEBUG_MOD) LOGGER.info("Couldn't connect to memcached servers: " + memcachedServers);
			}
		}
		return null;
	}
	
	private static void loadMemcachedConfig() {
		InputStream input = MemcachedUtils.class.getResourceAsStream("/memcached.conf");
		if (input != null) {
			StringBuilder builder = new StringBuilder();
			byte[] b = new byte[1024];
			int readBytes = 0;
			try {
				while ((readBytes = input.read(b)) >= 0) {
					builder.append(new String(b, 0, readBytes)); // ASCII chars
				}
				input.close();
				memcachedServers = builder.toString().trim();
			} catch (IOException e) {
				if (DEBUG_MOD) LOGGER.info("IOException " + e.getMessage());
			}
		}
	}

	private MemcachedUtils() {
	}
}

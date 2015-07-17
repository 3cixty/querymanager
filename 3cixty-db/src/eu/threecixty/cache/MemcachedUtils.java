package eu.threecixty.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

public class MemcachedUtils {

	private static final Logger LOGGER = Logger.getLogger(
			MemcachedUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	// server1:11211,server2:11211
	private static List <String> memcachedServers;
	
	public static List <MemcachedClient> createClients() {
		List <MemcachedClient> rets = new ArrayList <MemcachedClient>();
		if (memcachedServers == null || memcachedServers.size() == 0) loadMemcachedConfig();
		for (String memcachedServer: memcachedServers) {
			MemcachedClient memcachedClient = createClient(memcachedServer);
			if (memcachedClient != null) rets.add(memcachedClient);
		}
		return rets;
	}
	
	public static MemcachedClient getMemcachedClient(List <MemcachedClient> clients, String key) {
		if (clients == null || key == null) return null;
		int size = clients.size();
		if (size == 0) return null;
		if (size == 1) return clients.get(0);
		int hashCode = key.hashCode();
		int mode = hashCode % size;
		return clients.get(Math.abs(mode));
	}
	
	private static MemcachedClient createClient(String memcachedServer) {
		if (memcachedServer != null) {
		    try {
				return new MemcachedClient(AddrUtil.getAddresses(memcachedServer));
			} catch (IOException e) {
				if (DEBUG_MOD) LOGGER.info("Couldn't connect to memcached servers: " + memcachedServer);
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
				memcachedServers = new ArrayList <String>();
				String allMemcachedServers = builder.toString();
				String [] arr = allMemcachedServers.split(",");
				for (int i = 0; i < arr.length; i++) {
					memcachedServers.add(arr[i].trim());
				}
			} catch (IOException e) {
				if (DEBUG_MOD) LOGGER.info("IOException " + e.getMessage());
			}
		}
	}

	private MemcachedUtils() {
	}
}

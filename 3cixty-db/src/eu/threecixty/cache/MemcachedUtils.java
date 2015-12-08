/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.cache;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

/**
 * 
 * Utility class to find the corresponding memcached server with a given key.
 *
 */
public class MemcachedUtils {

	private static final Logger LOGGER = Logger.getLogger(
			MemcachedUtils.class.getName());

	 /**Attribute which is used to improve performance for logging out information*/
	private static final boolean DEBUG_MOD = LOGGER.isInfoEnabled();
	
	// server1:11211,server2:11211
	private static List <String> memcachedServers;
	
	/**
	 * Creates a list of clients which connect to memcached servers.
	 * @return
	 */
	public static List <MemcachedClient> createClients() {
		List <MemcachedClient> rets = new ArrayList <MemcachedClient>();
		if (memcachedServers == null || memcachedServers.size() == 0) loadMemcachedConfig();
		for (String memcachedServer: memcachedServers) {
			MemcachedClient memcachedClient = createClient(memcachedServer);
			if (memcachedClient != null) rets.add(memcachedClient);
		}
		return rets;
	}
	
	/**
	 * Gets the corresponding client with a given key to connect to the memcached server which
	 * stores information about the value of the given key.
	 * @param clients
	 * @param key
	 * @return
	 */
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
	
	/**
	 * Loads configuration file.
	 */
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

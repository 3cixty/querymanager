/*===========================================================
This file is part of the 3cixty platform software.

The 3cixty platform software contains proprietary and confidential information
of Inria. All rights reserved. Reproduction, adaptation or distribution, in
whole or in part, is forbidden except by express written permission of Inria.
Version v2, December 2015.
Authors: Cong-Kinh Nguyen, Rachit Agarwal, Animesh Pathak.
Copyright (C) 2015, Inria.
===========================================================*/

package eu.threecixty.profile;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import eu.threecixty.db.PersistentObjectForWorker;

/**
 * 
 * This class is used to update knows on background.
 *
 */
public class PersistenceWorkerManager {
	
	private volatile ThreadPoolExecutor threadPool;
	private BlockingQueue<Runnable> queue;

	private static final PersistenceWorkerManager INSTANCE = new PersistenceWorkerManager();	 
	
	public static PersistenceWorkerManager getInstance() {
		return INSTANCE;
	}
	
	public void add(final PersistentObjectForWorker obj) {
		Runnable runnable = new Runnable() {
			
			@Override
			public void run() {
				obj.saveOrUpdate();
			}
		};
		threadPool.execute(runnable);
	}
	
	public void stop() {
		threadPool.shutdown();
	}
	
	private PersistenceWorkerManager() {
		queue = new LinkedBlockingQueue<Runnable>();
		threadPool = new ThreadPoolExecutor(5, 20, 1, TimeUnit.SECONDS, queue);
	}
}

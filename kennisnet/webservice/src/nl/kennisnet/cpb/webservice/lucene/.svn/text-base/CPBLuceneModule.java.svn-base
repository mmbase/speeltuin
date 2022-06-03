/*
 * MMBase Lucene module
 *
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 */
package nl.kennisnet.cpb.webservice.lucene;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mmbase.module.Module;
import org.mmbase.module.core.MMBase;
import org.mmbase.util.ResourceLoader;
import org.mmbase.storage.implementation.database.DatabaseStorageManagerFactory;
import org.mmbase.storage.StorageManagerFactory;


/**
 * This module enables the Lucene fulltext search engine to be used to index the
 * MMBase cloud.
 * 
 * @author Wouter Heijke
 * @version $Revision: 1.1.2.2 $
 */
public class CPBLuceneModule extends Module {
	private static Log log = LogFactory.getLog(CPBLuceneModule.class);

	//private Indexer indexer;

	// timer to schedule the indexing
	private static Timer timer = null;

	// Milliseconds how long the thread will sleep
	private long interval;

	private int startHour;

	// Should we index on startup, default is true
	private boolean indexOnStartup = true;

	// Only create the index once, then use Observer changes to perform updates
	private boolean smartIndexing;

	private String configFile = "lucenemodule.xml";

	private String configroot;

	public static String indexPath = "";

	private Indexer indexer;

	//private DataSource ds;

	/** The mmbase instance */
	private MMBase mmbase = null;

	/**
	 * Number of tries to get cloud. A second between each try.
	 */
	private static final int GETCLOUD_NUM_OF_TRIES = 60;

	/**
	 * Time to wait between tries.
	 */
	private static final long GETCLOUD_INTERVAL_TRIES = 1000L;

	/**
	 * Number of seconds to wait before starting on-startup-index.
	 */
	private Integer onStartupWait = null;

	/**
	 * @see org.mmbase.module.Module#onload()
	 */
	public void onload() {
		// implement
	}

	/**
	 * @see org.mmbase.module.Module#init()
	 */
	public void init() {
			// Wait for mmbase to be up and running.
		mmbase = (MMBase) Module.getModule("mmbaseroot");

		indexPath = getInitParameter("index-path");
		if (indexPath != null) {
		    indexPath = indexPath + "/";
        }

		// Initialize the module.
		String intervalStr = getInitParameter("interval");
		if (intervalStr == null) {
			interval = 0;
		} else {
			interval = Long.parseLong(intervalStr) * 1000L * 60L;
		}

		String startHourStr = getInitParameter("starthour");
		if (startHourStr == null) {
			throw new IllegalArgumentException("starthour");
		} else {
			startHour = Integer.parseInt(startHourStr);
		}

		// If there is a init paramater index-on-startup use it (default: false)
		String indexOnStartupStr = getInitParameter("index-on-startup");
		if (indexOnStartupStr != null) {
			indexOnStartup = Boolean.valueOf(indexOnStartupStr).booleanValue();
			log.debug("index on startup set to:" + indexOnStartup);
		} else {
			log.warn("index-on-startup property not set");
		}

		// If there is a init paramater index-on-startup use it (default: false)
		String smartIndexStr = getInitParameter("re-use-index");
		if (smartIndexStr != null) {
			smartIndexing = Boolean.valueOf(smartIndexStr).booleanValue();
		} else {
			log.warn("re-use-index property not set");
		}

		String onStartupWaitStr = getInitParameter("index-on-startup-wait");
		if (onStartupWaitStr != null) {
			onStartupWait = new Integer(onStartupWaitStr);
		}

		List files = ResourceLoader.getConfigurationRoot().getFiles("");
		if (files.size() > 0) {
			configroot = ((File) files.get(0)).getAbsolutePath();
		}
        
        // register observer
        indexer = new Indexer();
        log.info("index path from config:" + indexPath);
        if (indexPath != null && !"".equals(indexPath)) {
            indexer.setPath(indexPath);
        } else {
            indexer.setPath("");
        }
        indexer.init();
        indexer.registerObserver();

        
		// index just once if indexOnStartup is true
		if (indexOnStartup) {
			Thread runOnce = new Thread(new IndexingTimerTask());
			runOnce.setDaemon(true);
			runOnce.start();
		}
        
        // schedule task if interval > 0
        if (interval > 0) {
            
            
            
    		// Create a new Timer object; true for daemon
    		timer = new Timer(true);
    
    		// Get the current date and set the time to startHour
    		Calendar cal = Calendar.getInstance();
    		cal.set(Calendar.HOUR_OF_DAY, startHour);
    		cal.set(Calendar.MINUTE, 0);
    		cal.set(Calendar.SECOND, 0);
    
    		// If the current date is after startHour then add one day
    		if (Calendar.getInstance().after(cal)) {
    			cal.add(Calendar.DAY_OF_YEAR, 1);
    		}
    		Date time = cal.getTime();
    
    		// Schedule the actual thread at startHour with a period of interval
    		timer.scheduleAtFixedRate(new IndexingTimerTask(), time, interval);
    		
        }
	}

	private class IndexingTimerTask extends TimerTask {
		/**
		 * The thread in which the external links will be checked
		 */

		public void run() {
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			if (onStartupWait != null) {
				log.debug("LuceneModule waiting for " + onStartupWait + " seconds");
				try {
					Thread.sleep(onStartupWait.intValue() * 1000L);
				} catch (InterruptedException e) {
					// ignore
				}
				// only wait the first time
				onStartupWait = null;
			}

			log.info("LuceneModule thread started");
			startIndexing(!smartIndexing);
		}
	}

	/**
	 * Start indexing the cloud after MMBase has really started.
	 * Returns true if indexing is done
	 */
	private void startIndexing(boolean createIndex) {	
		int count = 0;
		try {
			while (!mmbase.getState()) {
				// not started, sleep another minute
				Thread.sleep(60000);
			}
		} catch (InterruptedException e) {
			// ignore
		}
		log.info("LuceneModule start indexing");	
		indexer.runningFullIndex = true;
		indexer.indexAll(createIndex);
		indexer.runningFullIndex = false;
		log.info("LuceneModule end indexing, receiving updates to index now");
		log.info("LuceneModule indexed count = " + count);
	}
}

/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */

package server;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import server.MMServ;
import server.campaign.CampaignMain;

@SuppressWarnings({"unchecked","serial"})
public final class SMWLogger {//final - no extension of the server logger

	private static final int rotations = 5; // Configurable
	private static final int normFileSize = 1000000; // Configurable
	private static final int bigFileSize = 5000000; //Configurable
	private static final int hugeFileSize = 10000000; //Configurable
	
	private static boolean logging = false;
	private static boolean addSeconds = false;
	//private static boolean clientlog = false;

	private File logDir;

	private Logger mainLog;		// Log for main channel
	private Logger factionLog;	// Log for faction mails (all factions)
	private Logger gameLog;		// Log for game reports (started, canceled and reported)
	private Logger resultsLog;	// Log for game results as sent to players
	private Logger cmdLog;		// Log for all issued commands
	private Logger pmLog;		// Log for PMs
	private Logger bmLog;		// Log for Black Market results
	private Logger infoLog;		// Log for server generic messages (informative)
	private Logger warnLog;		// Log for server warnings (problems)
	private Logger errLog;		// Log for server errors (troubles!)
	private Logger modLog;		// Log for moderators, normal log (double reg, 2nd/same, modchannel)
	private Logger tickLog;		// Log for tick events (rankings, production, player stats)
	private Logger ipLog;		// Log connecting IPs.
	private Logger dbLog;		// Log MySQL Database issues
	private Logger debugLog;   // for all debug messages
	
	private MMNetFormatter mmnetFormatter;

	private FileHandler mainHandler;
	private FileHandler factionHandler;
	private FileHandler gameHandler;
	private FileHandler resultsHandler;
	private FileHandler cmdHandler;
	private FileHandler pmHandler;
	private FileHandler bmHandler;
	private FileHandler infoHandler;
	private FileHandler warnHandler;
	private FileHandler errHandler;
	private FileHandler modHandler;
	private FileHandler tickHandler;
	private FileHandler ipHandler;
	private FileHandler dbHandler;
	private FileHandler debugHandler;
	
	public static class MMNetFormatter extends SimpleFormatter {

		@Override
		public String format (LogRecord record) {

			GregorianCalendar now = new GregorianCalendar();
			now.setTimeInMillis(record.getMillis());
			StringBuilder sb = new StringBuilder();
			
			sb.append(now.get(Calendar.YEAR));
			if(now.get(Calendar.MONTH) < 9)
				sb.append("0");
			sb.append((now.get(Calendar.MONTH) + 1));
			if(now.get(Calendar.DAY_OF_MONTH) < 10)
				sb.append("0");
			sb.append(now.get(Calendar.DAY_OF_MONTH) + " ");
			if(now.get(Calendar.HOUR_OF_DAY) < 10)
				sb.append("0");
			sb.append(now.get(Calendar.HOUR_OF_DAY) + ":");
			if(now.get(Calendar.MINUTE) < 10)
				sb.append("0");
			if(addSeconds) {
				sb.append(now.get(Calendar.MINUTE) + ":");
				if(now.get(Calendar.SECOND) < 10)
					sb.append("0");
				sb.append(now.get(Calendar.SECOND) /* +"|" + record.getLevel() */ +"|"+ formatMessage(record) + "\n");
			} else {
				sb.append(now.get(Calendar.MINUTE) /* +"|" + record.getLevel() */ +"|"+ formatMessage(record) + "\n");
			}
			return sb.toString();
		}
	}

	public SMWLogger() {

		if(logging) return;

		logDir = new File("logs");
		if(!logDir.exists()) {
			try {
				if(!logDir.mkdirs()) {
					MMServ.mmlog.errLog("WARNING: logging directory cannot be created!");
					MMServ.mmlog.errLog("WARNING: disabling log subsystem");
					return;
				}
			} catch (Exception e) {
				MMServ.mmlog.errLog(e);
			}
		}
		else if(!logDir.isDirectory()) {
			MMServ.mmlog.errLog("WARNING: logging directory is not a directory!");
			MMServ.mmlog.errLog("WARNING: disabling log subsystem");
			return;
		}

		if(!logDir.canWrite()) {
			MMServ.mmlog.errLog("WARNING: cannot write in logging directory!");
			MMServ.mmlog.errLog("WARNING: disabling log subsystem");
			return;
		}

		mmnetFormatter = new MMNetFormatter();

		try {
		    
				mainHandler = new FileHandler(logDir.getPath() + "/mainlog", bigFileSize, rotations, true);
				mainHandler.setLevel(Level.INFO);
				mainHandler.setFilter(null);
				mainHandler.setFormatter(mmnetFormatter);
				mainLog = Logger.getLogger("mainLogger");
				mainLog.setUseParentHandlers(false);
				mainLog.addHandler(mainHandler);
	
				gameHandler = new FileHandler(logDir.getPath() + "/gamelog", bigFileSize, rotations, true);
				gameHandler.setLevel(Level.INFO);
				gameHandler.setFilter(null);
				gameHandler.setFormatter(mmnetFormatter);
				gameLog = Logger.getLogger("gameLogger");
				gameLog.setUseParentHandlers(false);
				gameLog.addHandler(gameHandler);
				
				resultsHandler = new FileHandler(logDir.getPath() + "/resultslog", bigFileSize, rotations, true);
				resultsHandler.setLevel(Level.INFO);
				resultsHandler.setFilter(null);
				resultsHandler.setFormatter(mmnetFormatter);
				resultsLog = Logger.getLogger("resultsLogger");
				resultsLog.setUseParentHandlers(false);
				resultsLog.addHandler(resultsHandler);
	
				cmdHandler = new FileHandler(logDir.getPath() + "/cmdlog", hugeFileSize, rotations, true);
				cmdHandler.setLevel(Level.INFO);
				cmdHandler.setFilter(null);
				cmdHandler.setFormatter(mmnetFormatter);
				cmdLog = Logger.getLogger("cmdLogger");
				cmdLog.setUseParentHandlers(false);
				cmdLog.addHandler(cmdHandler);
	
				pmHandler = new FileHandler(logDir.getPath() + "/pmlog", bigFileSize, rotations, true);
				pmHandler.setLevel(Level.INFO);
				pmHandler.setFilter(null);
				pmHandler.setFormatter(mmnetFormatter);
				pmLog = Logger.getLogger("pmLogger");
				pmLog.setUseParentHandlers(false);
				pmLog.addHandler(pmHandler);
	
				bmHandler = new FileHandler(logDir.getPath() + "/bmlog", normFileSize, rotations, true);
				bmHandler.setLevel(Level.INFO);
				bmHandler.setFilter(null);
				bmHandler.setFormatter(mmnetFormatter);
				bmLog = Logger.getLogger("bmLogger");
				bmLog.setUseParentHandlers(false);
				bmLog.addHandler(bmHandler);
	
				infoHandler = new FileHandler(logDir.getPath() + "/infolog", normFileSize, rotations, true);
				infoHandler.setLevel(Level.INFO);
				infoHandler.setFilter(null);
				infoHandler.setFormatter(mmnetFormatter);
				infoLog = Logger.getLogger("infoLogger");
				infoLog.setUseParentHandlers(false);
				infoLog.addHandler(infoHandler);
	
				warnHandler = new FileHandler(logDir.getPath() + "/warnlog", normFileSize, rotations, true);
				warnHandler.setLevel(Level.INFO);
				warnHandler.setFilter(null);
				warnHandler.setFormatter(mmnetFormatter);
				warnLog = Logger.getLogger("warnLogger");
				warnLog.setUseParentHandlers(false);
				warnLog.addHandler(warnHandler);
	
				errHandler = new FileHandler(logDir.getPath() + "/errlog", normFileSize, rotations, true);
				errHandler.setLevel(Level.INFO);
				errHandler.setFilter(null);
				errHandler.setFormatter(mmnetFormatter);
				errLog = Logger.getLogger("errLogger");
				errLog.setUseParentHandlers(false);
				errLog.addHandler(errHandler);
	
				modHandler = new FileHandler(logDir.getPath() + "/modlog", bigFileSize, rotations, true);
				modHandler.setLevel(Level.INFO);
				modHandler.setFilter(null);
				modHandler.setFormatter(mmnetFormatter);
				modLog = Logger.getLogger("modLogger");
				modLog.setUseParentHandlers(false);
				modLog.addHandler(modHandler);
	
				tickHandler = new FileHandler(logDir.getPath() + "/ticklog", normFileSize, rotations, true);
				tickHandler.setLevel(Level.INFO);
				tickHandler.setFilter(null);
				tickHandler.setFormatter(mmnetFormatter);
				tickLog = Logger.getLogger("tickLogger");
				tickLog.setUseParentHandlers(false);
				tickLog.addHandler(tickHandler);
				
				//104857600 is exactly 100 megabytes
				ipHandler = new FileHandler(logDir.getPath() + "/iplog", 104857600, rotations, true);
				ipHandler.setLevel(Level.INFO);
				ipHandler.setFilter(null);
				ipHandler.setFormatter(mmnetFormatter);
				ipLog = Logger.getLogger("ipLogger");
				ipLog.setUseParentHandlers(false);
				ipLog.addHandler(ipHandler);

				dbHandler = new FileHandler(logDir.getPath() + "/dblog", 104857600, rotations, true);
				dbHandler.setLevel(Level.INFO);
				dbHandler.setFilter(null);
				dbHandler.setFormatter(mmnetFormatter);
				dbLog = Logger.getLogger("dbLogger");
				dbLog.setUseParentHandlers(false);
				dbLog.addHandler(dbHandler);

				debugHandler = new FileHandler(logDir.getPath() + "/debuglog", hugeFileSize, rotations, true);
				debugHandler.setLevel(Level.INFO);
				debugHandler.setFilter(null);
				debugHandler.setFormatter(mmnetFormatter);
				debugLog = Logger.getLogger("debugLogger");
				debugLog.setUseParentHandlers(false);
				debugLog.addHandler(debugHandler);
	

				logging = true;

		} catch (Exception e) {
			MMServ.mmlog.errLog(e);
		}
	}

	public void mainLog(String s) {
		if(logging) mainLog.info(s);
	}

	public void factionLog(String s,String LogName) {
        factionLog = Logger.getLogger(LogName);
		factionLog.info(s);
	}

	public void gameLog(String s) {
		if(logging) gameLog.info(s);
	}
	
	public void resultsLog(String s) {
		if(logging) resultsLog.info(s);
	}

	public void cmdLog(String s) {
		
		if (logging) {
			/*
			 * exclude hm and factionmail commands,
			 * as there is a seperate factionlog
			 */
			String lower = s.toLowerCase();
			if(lower.indexOf("hm#") == -1 && lower.indexOf("factionmail#") == -1)
				cmdLog.info(s);
		}
	}

	public void pmLog(String s) {
		if(logging) pmLog.info(s);
	}

	public void bmLog(String s) {
		if(logging) bmLog.info(s);
	}

	public void infoLog(String s) {
		if(logging) infoLog.info(s);
	}

	public void log(String s) {
		if(logging) infoLog.info(s);
	}

	public void warnLog(String s) {
		if(logging) warnLog.info(s);
	}

	public void errLog(String s) {
		if(logging){
		    errLog.info(s);
		    if ( CampaignMain.cm != null )
		        CampaignMain.cm.doSendErrLog(s);
		}
	}

	public void errLog(Exception e) {
		if(logging) {
			errLog.warning("[" + e.toString() + "]");
		    if ( CampaignMain.cm != null )
		        CampaignMain.cm.doSendErrLog("[" + e.toString() + "]");
			StackTraceElement[] t = e.getStackTrace();
			for(int i = 0; i < t.length; i++){
				errLog.warning("   " + t[i].toString());
			    if ( CampaignMain.cm != null )
		        	CampaignMain.cm.doSendErrLog("   " + t[i].toString());
			}
		}
	}

	public void debugLog(String s) {
		if(logging){
		    debugLog.info(s);
		}
	}

	public void debugLog(Exception e) {
		if(logging) {
			errLog.warning("[" + e.toString() + "]");
			StackTraceElement[] t = e.getStackTrace();
			for(int i = 0; i < t.length; i++){
				debugLog.warning("   " + t[i].toString());
			}
		}
	}

	public void modLog(String s) {
		if(logging) modLog.info(s);
	}

	public void tickLog(String s) {
		if(logging) tickLog.info(s);
	}
	
	public void ipLog(String s) {
		if(logging) ipLog.info(s);
	}

	public void dbLog(String s) {
		if(logging) dbLog.info(s);
	}


	public void enableSeconds(boolean b) {
		SMWLogger.addSeconds = b;
	}

	public void enableLogging(boolean b) {
		SMWLogger.logging = b;
	}

    public void createFactionLogger(String logName){
        
        try{
            factionHandler = new FileHandler(logDir.getPath() + "/"+logName, bigFileSize, rotations, true);
            factionHandler.setLevel(Level.INFO);
            factionHandler.setFilter(null);
            factionHandler.setFormatter(mmnetFormatter);
            factionLog = Logger.getLogger(logName);
            factionLog.setUseParentHandlers(false);
            factionLog.addHandler(factionHandler);
            factionLog.info(logName+" log touched");
        }catch(Exception ex){
            MMServ.mmlog.errLog(ex);
        }

    }
    
	public synchronized static void addToModeratorLog(String s) {
	    try {
	        FileOutputStream out = new FileOutputStream(
	                "./campaign/moderator.log", true);
	        PrintStream p = new PrintStream(out);
	        Date d = new Date(System.currentTimeMillis());
	        p.println(d + ":" + s);
	        p.close();
	        out.close();
	        CampaignMain.cm.doSendModMail("MODLOG: ", s);
	    } catch (Exception ex) {
	        MMServ.mmlog.errLog("Problems writing modlog to file");
	        MMServ.mmlog.errLog(ex);
	    }
	}
}

/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 * Original Author - McWizard (Helge Richter)
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

package server.campaign.commands;

import java.util.StringTokenizer;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;

/**
 * Set a faction's message of the day. Can be of arbitrary length and use HTML.
 * 
 * Syntax: /c setmotd#text
 */
public class SetMOTDCommand implements Command {
	
	int accessLevel = 0;
	String syntax = "";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		SPlayer p = CampaignMain.cm.getPlayer(Username);
		int minExp = CampaignMain.cm.getIntegerConfig("MinMOTDExp");
		
		//unregistered players can't set MOTD.
		if (p == null) {
			CampaignMain.cm.toUser("Unregistered players cannot set an MOTD.",Username,true);
			return;
		}
		
		if (p.getExperience() < minExp) {
			CampaignMain.cm.toUser("Your exp is too low you must have " + minExp + " XP in order to use setmotd.",Username,true);
			return;
		}
		
		String motd = "";
		try {
			
			//there may be #'s in HTML. Use all tokens and restore #'s.
			motd = command.nextToken();
			while (command.hasMoreTokens())
				motd += "#" + command.nextToken();
			
		} catch (Exception e) {
			CampaignMain.cm.toUser("Improper syntax. Try: /setmotd text or /setmotd clear",Username,true);
			return;
		}
		
		if (motd.trim().equals("") || motd.trim().equalsIgnoreCase("clear")) {
			p.getMyHouse().setMotd("");
			CampaignMain.cm.toUser("MOTD cleared.",Username,true);
			return;
		}
		
		int size = motd.length();
		if (size > 7000) {
			CampaignMain.cm.toUser("MOTD's may contain up to 7000 charachters. Your message was " + size + "chars long. Reduce its length and try again.",Username,true);
			return;
		}
		
		
		p.getMyHouse().setMotd(motd + "<p> -- Set by " + p.getName());
		CampaignMain.cm.toUser("MOTD set. Use /c motd to review.",Username,true);
				
		
	}//end process()
}//end setMOTDCommand.java

/*
 * MekWars - Copyright (C) 2007 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 * Original Author - JTighe (Torren@users.sourceforge.net)
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

import server.campaign.SPlayer;
import server.campaign.CampaignMain;

public class SetAutoReorderCommand implements Command {
	
	int accessLevel = 0;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		if ( !CampaignMain.cm.getBooleanConfig("UsePartsRepair") )
			return;
		 	
		SPlayer p = CampaignMain.cm.getPlayer(Username);
		
		try {
			p.setAutoReorder(Boolean.parseBoolean(command.nextToken()));
		}//end try
		catch (NumberFormatException ex) {
			CampaignMain.cm.toUser("SetAutoAutoReorder command failed. Check your input. It should be something like this: /c setAutoReorder#True/False",Username);
			return;
		}//end catch
		
		CampaignMain.cm.toUser("Auto Reorder set.",Username,true);
		
	}//end process() 
}//end SetAutoEjectCommand class


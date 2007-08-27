/*
 * MekWars - Copyright (C) 2007 
 * 
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

/**
 * @author jtighe
 * This command is used to set Black Market Settings
 * for max/min cost and production.
 * 
 */
package server.campaign.commands.admin;

import java.util.StringTokenizer;

import common.Equipment;

import server.MWServ;
import server.campaign.commands.Command;
import server.campaign.CampaignMain;
import server.MWChatServer.auth.IAuthenticator;

public class AdminSetBlackMarketSettingCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
	public void process(StringTokenizer command,String Username) {
		
		//access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		//get config var and new setting
		String key = "";
		String minCost = "";
		String maxCost = "";
		String minProduction = "";
		String maxProduction = "";
		
		
		try {
			key = command.nextToken();
			minCost = command.nextToken();
			maxCost = command.nextToken();
			minProduction = command.nextToken();
			maxProduction = command.nextToken();
			
			Equipment bme = CampaignMain.cm.getBlackMarketEquipmentTable().get(key);
			
			if ( bme == null ) {
				bme = new Equipment();
				bme.setEquipmentInternalName(key);
			}
			
			bme.setMinCost(Double.parseDouble(minCost));
			bme.setMaxCost(Double.parseDouble(maxCost));
			bme.setMinProduction(Integer.parseInt(minProduction));
			bme.setMaxProduction(Integer.parseInt(maxProduction));
			
			CampaignMain.cm.getBlackMarketEquipmentTable().put(key, bme);
			
		}catch (Exception ex){
			MWServ.mwlog.errLog(ex);
		}
		
		//NOTE:
		//NO MODMAIL for setting changes. Server Config GUI would spam too much.
		
	}//end process
}
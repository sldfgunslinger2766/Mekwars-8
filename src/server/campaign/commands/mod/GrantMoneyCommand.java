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

package server.campaign.commands.mod;

import java.util.StringTokenizer;

import server.MWChatServer.auth.IAuthenticator;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.commands.Command;

public class GrantMoneyCommand implements Command {
	
	int accessLevel = IAuthenticator.MODERATOR;
	String syntax = "Player Name#Amount";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		SPlayer p = CampaignMain.cm.getPlayer(command.nextToken());
		int amount = Integer.parseInt(command.nextToken());
		if (p != null) {
			p.addMoney(amount);
			CampaignMain.cm.toUser("AM:You've been granted " + CampaignMain.cm.moneyOrFluMessage(true,false,amount,true)+" from " + Username,p.getName(),true);
			CampaignMain.cm.toUser("AM:You granted " + CampaignMain.cm.moneyOrFluMessage(true,false,amount,true)+" to " + p.getName(),Username,true);
			//server.MWLogger.modLog(Username + " granted " + CampaignMain.cm.moneyOrFluMessage(true,false,amount,true)+" to " + p.getName());
			CampaignMain.cm.doSendModMail("NOTE",Username + " granted " + CampaignMain.cm.moneyOrFluMessage(true,false,amount,true)+" to " + p.getName());
		}
		
	}
}
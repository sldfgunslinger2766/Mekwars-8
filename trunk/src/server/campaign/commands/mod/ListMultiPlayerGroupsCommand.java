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
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SPlayer;
import server.campaign.commands.Command;

import common.House;

import server.MWChatServer.auth.IAuthenticator;

@SuppressWarnings({"unchecked","serial"})
public class ListMultiPlayerGroupsCommand implements Command {
	
	int accessLevel = IAuthenticator.MODERATOR;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	
	public void process(StringTokenizer command,String Username) {
		
		//access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		//WARNING: CODE EFFECIENCY VERY BAD. COULD CAUSE HIGH SERVER LOAD IF USED OFTEN
		String toSend = "List of Multiplayergroups:";
		
		/*
		 * INCREDIBLY EVIL!
		 */
		Hashtable<String,SPlayer> allPlayers = new Hashtable<String,SPlayer>();
		for (House vh : CampaignMain.cm.getData().getAllHouses()) {
			SHouse h = (SHouse)vh;
			allPlayers.putAll(h.getAllOnlinePlayers());
		}
		/*
		 * End PHENOMENAL EVIL.
		 */
		
		Hashtable result = new Hashtable();
		Enumeration e = allPlayers.elements();
		while (e.hasMoreElements())
		{
			//Check all players for equal Groupentries..
			SPlayer p = (SPlayer)e.nextElement();
			if (p.getGroupAllowance() != 0)
			{
				Vector v;
				if (result.get(p.getGroupAllowance()) == null)
					v = new Vector(1,1);
				else
					v = (Vector)result.get(p.getGroupAllowance());
				v.add(p);
				result.put(p.getGroupAllowance(),v);
			}
		}
		
		e = result.keys();
		while (e.hasMoreElements()) {
			Integer GroupID = (Integer)e.nextElement();
			Vector members = (Vector)(result.get(GroupID));
			toSend += "<br>Group #" + GroupID + ":";
			for (int i=0; i < members.size();i++) {
				SPlayer p = (SPlayer)members.elementAt(i);
				toSend += p.getName() + " + ";
			}
			toSend = toSend.substring(0,toSend.lastIndexOf("+")-1);
			
		}
		CampaignMain.cm.toUser(toSend,Username,true);
		
	}
}
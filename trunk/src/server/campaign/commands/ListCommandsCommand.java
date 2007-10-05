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

package server.campaign.commands;


import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

import server.campaign.CampaignMain;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;

public class ListCommandsCommand implements Command {
	
	int accessLevel = IAuthenticator.REGISTERED;
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	String syntax = "";
	public String getSyntax() { return syntax;}

	
	public void process(StringTokenizer command,String Username) {
		
		//access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		Hashtable<String,Command> commandTable = CampaignMain.cm.getServerCommands();
		Enumeration<String> commands = commandTable.keys();
		
		String starter = "";
		
		String[] commandArray = new String[commandTable.size()];
		int x = 0;
		
		if ( command.hasMoreElements())
			starter = command.nextToken();
		
		while (commands.hasMoreElements()) {
			
			String commandName = commands.nextElement();
			
			if ( commandName.equalsIgnoreCase("SendClientDataCommand") )
				continue;
			Command commandMethod = commandTable.get(commandName);
			
			if (!starter.equals("")) {
			    if ( userLevel < commandMethod.getExecutionLevel())
			        continue;
			    else if ( commandName.toLowerCase().indexOf(starter.toLowerCase()) > -1 ) {
			    	String syntax = commandMethod.getSyntax().trim().length() < 1 ? " " : commandMethod.getSyntax();
			        commandArray[x] = commandName.substring(0,1)+commandName.substring(1,commandName.length()).toLowerCase()+"#"+syntax+"#"+commandMethod.getExecutionLevel();
			    }
				else
					continue;
			} else{
			    if ( userLevel >= commandMethod.getExecutionLevel()) {
			    	String syntax = commandMethod.getSyntax().trim().length() < 1 ? " " : commandMethod.getSyntax();
			        commandArray[x] = commandName.substring(0,1)+commandName.substring(1,commandName.length()).toLowerCase()+"#"+syntax+"#"+commandMethod.getExecutionLevel();
			    }
			    else
			        continue;
			}
			x++;
		}
		
		if (x < commandTable.size() ){
			String[] tempArray = new String[x];
			for (int count = 0; count < x;count++)
				tempArray[count] = commandArray[count];
			commandArray = new String[x];
			commandArray = tempArray.clone();
		}
		
		Arrays.sort(commandArray);
		String result = "SM|";
		result += "<font color=\"black\">";
		result += "<table><tr><th>Command Name</th><th>Syntax</th><th>Access Level</th></tr>";

		for( x=0; x<commandArray.length; x++){
		    StringTokenizer commandList = new StringTokenizer(commandArray[x],"#");
		    result += "<tr><td>"+commandList.nextToken()+"</td><td>"+commandList.nextToken()+"</td><td>"+commandList.nextToken()+ "</td></tr>";
		}
		result +="</table>";
		CampaignMain.cm.toUser(result,Username,false);
	}
}
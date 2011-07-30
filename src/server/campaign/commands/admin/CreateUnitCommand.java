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

package server.campaign.commands.admin;

import java.util.StringTokenizer;

import server.MWChatServer.auth.IAuthenticator;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.SUnit;
import server.campaign.commands.Command;
import server.campaign.pilot.SPilotSkills;
import server.campaign.pilot.SPilot;
import server.campaign.pilot.skills.SPilotSkill;
import server.campaign.pilot.skills.TraitSkill;
import server.mwmysql.HistoryHandler;

// syntanx /c createunit#filename#flavortext#gunnery#pilot#skill1,skill2,skill3
public class CreateUnitCommand implements Command {
	
	int accessLevel = IAuthenticator.ADMIN;
	String syntax = "filename#flavortext#gunnery#pilot#weightclass#skill1,skill2,skill3[Random]";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	public void process(StringTokenizer command,String Username) {
		
		//access level check
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
        SPlayer p = CampaignMain.cm.getPlayer(Username);
		String filename;
		String FlavorText;
		String gunnery;
		String piloting;

		try {
			filename = command.nextToken();
			FlavorText = command.nextToken();
			gunnery = command.nextToken();
			piloting = command.nextToken();
		}catch(Exception ex) {
			CampaignMain.cm.toUser(syntax, Username);
			return;
		}
		
		int weight = SUnit.LIGHT;
		
		if ( command.hasMoreElements() )
			weight = Integer.parseInt(command.nextToken());
		
		SUnit cm = new SUnit(FlavorText,filename,weight);
		
		SPilot pilot = null;
		if ( gunnery.equals("99") || piloting.equals("99") )
		    pilot = new SPilot("Vacant",99,99);
		else
		    pilot = new SPilot(SPilot.getRandomPilotName(CampaignMain.cm.getR()),Integer.parseInt(gunnery),Integer.parseInt(piloting));
		
        pilot.setCurrentFaction("Common");
		if (command.hasMoreTokens()){
			String skillTokens = command.nextToken();
			StringTokenizer skillList = new StringTokenizer(skillTokens,",");
			
			while (skillList.hasMoreTokens()){
				String skill = skillList.nextToken();
				SPilotSkill pSkill = null; 
				if ( skill.equalsIgnoreCase("random") )
					pSkill = SPilotSkills.getRandomSkill(pilot, cm.getType() );
				else					
					pSkill = SPilotSkills.getPilotSkill(skill);
				
				if ( pSkill != null ){
                    if ( pSkill instanceof TraitSkill){
                        ((TraitSkill)pSkill).assignTrait(pilot);
                    }
                    pSkill.addToPilot(pilot);
                    pSkill.modifyPilot(pilot);
                }
			}
		}
		cm.setPilot(pilot);
		
		p.addUnit(cm, true);
		CampaignMain.cm.toUser("Unit created: " + filename + " " + FlavorText + " " + gunnery + " " + piloting+" "+pilot.getSkillString(true) + ". ID #" + cm.getId(),Username,true);
		//server.CampaignData.mwlog.modLog(Username + " created a unit: " + filename + " " + FlavorText + " " + gunnery + " " + piloting+" "+pilot.getSkillString(true));	
		CampaignMain.cm.doSendModMail("NOTE",Username + " created a unit: " + filename + " " + FlavorText + " " + gunnery + " " + piloting+" "+pilot.getSkillString(true));
		if(CampaignMain.cm.isKeepingUnitHistory()) {
			CampaignMain.cm.MySQL.addHistoryEntry(HistoryHandler.HISTORY_TYPE_UNIT, cm.getDBId(), HistoryHandler.UNIT_CREATED, cm.getProducer());
			CampaignMain.cm.MySQL.addHistoryEntry(HistoryHandler.HISTORY_TYPE_PILOT, pilot.getDBId(), HistoryHandler.PILOT_ASSIGNED, "Assigned to " + cm.getModelName());
			CampaignMain.cm.MySQL.addHistoryEntry(HistoryHandler.HISTORY_TYPE_UNIT, cm.getDBId(), HistoryHandler.PILOT_ASSIGNED, pilot.getName() + " assigned to unit");
		}
	}
}

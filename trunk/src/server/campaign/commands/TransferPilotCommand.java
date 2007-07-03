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

import java.util.StringTokenizer;

import common.Unit;

import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SPlayer;
import server.campaign.pilot.SPilot;

public class TransferPilotCommand implements Command {

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

		SPlayer player = CampaignMain.cm.getPlayer(Username);
		SHouse house = player.getMyHouse();

		//make sure PPQs are allowed
		if (!new Boolean(house.getConfig("AllowPersonalPilotQueues")).booleanValue()) {
			CampaignMain.cm.toUser("Pilot queues are not enabled on this server.",Username,true);
			return;
		}

		//Don't let pilots transfer while on welfare (no idea why though?)
		if (player.mayAcquireWelfareUnits()){
			CampaignMain.cm.toUser("You may not transfer any of your pilots while you are on welfare.",Username,true);
			return;
		}

		//Newbie House may not send units!
		if (player.getMyHouse().isNewbieHouse()){
			CampaignMain.cm.toUser("Players in " + player.getMyHouse().getName() + " may not transfer pilots.", Username, true);
			return;
		}

		//Acquire needed Data
		String targetPlayer = (String)command.nextElement();
		int pUnitType = Integer.parseInt((String)command.nextElement());
		int pWeightClass =Integer.parseInt((String)command.nextElement());
		int pPosition = Integer.parseInt((String)command.nextElement());

		//target exists
		SPlayer targetplayer = CampaignMain.cm.getPlayer(targetPlayer);
		if (targetplayer == null) {
			CampaignMain.cm.toUser("Players in SOL may not transfer pilots.", Username, true);
			return;
		}

		//The receiving player must have enough room
		if (targetplayer.getPersonalPilotQueue().getPilotQueue(pUnitType,pWeightClass).size() > Integer.parseInt(house.getConfig("MaxAllowedPilotsInQueueToBuyFromHouse"))){
			CampaignMain.cm.toUser(Username + "tried to send you a p[ilot, but your faction allows only "+new Integer(house.getConfig("MaxAllowedPilotsInQueueToBuyFromHouse")).intValue()+" pilots of each type in your barracks.",targetplayer.getName(),true);
			CampaignMain.cm.toUser(targetplayer.getName()+"'s barracks are currently full!",Username,true);
			return;
		}

		//get the pilot, removing him from the PPQ
		SPilot pilot = (SPilot) player.getPersonalPilotQueue().getPilot(pUnitType,pWeightClass,pPosition);
		if (pilot == null) {
			CampaignMain.cm.toUser("Unable to find that Pilot. Try again", Username);
			return;
		}

		/*
		 * Technically speaking, we should update the player's PPQ here; however,
		 * doing so wastes a ton of bandwidth. If the player's attempt to transfer
		 * the pilot fails, we have to add it back to the queue and re-send the
		 * client update. Instead, remove it, insert it back in the queue if the
		 * transfer fails, and only send an update after the attempt clears.
		 */
		//CampaignMain.cm.toUser("PL|PPQ|"+player.getPlayerPersonalPilotQueue().toString(true),Username,false);

		//Not the same faction?
		if (!targetplayer.getMyHouse().equals(player.getMyHouse()) && !targetplayer.getMyHouse().getHouseFightingFor(targetplayer).equals(player.getMyHouse())) {
			CampaignMain.cm.toUser(targetplayer.getName() + " is not in your faction. You cannot send him units.", Username, true);
			player.getPersonalPilotQueue().addPilot(pilot, pWeightClass);
			if(CampaignMain.cm.isUsingMySQL())
				CampaignMain.cm.MySQL.linkPilotToPlayer(pilot.getPilotId(), Username);
			return;
			//Target has no room?
		} else if (!targetplayer.getMyHouse().isLoggedIntoFaction(targetplayer.getName())) {
			CampaignMain.cm.toUser(targetplayer.getName() + " is not logged in. You may only transfer to players who are online.", Username, true);
			player.getPersonalPilotQueue().addPilot(pilot, pWeightClass);
			if(CampaignMain.cm.isUsingMySQL())
				CampaignMain.cm.MySQL.linkPilotToPlayer(pilot.getPilotId(), Username);
			return;
			//Same IP address?
		} else if (new Boolean(house.getConfig("IPCheck")).booleanValue()) {
			if (CampaignMain.cm.getServer().getIP(player.getName()).toString().equals(CampaignMain.cm.getServer().getIP(targetplayer.getName()).toString())) {
				CampaignMain.cm.toUser(targetplayer.getName() + " has the same IP as you do. You can't send them pilots.", Username, true);
				player.getPersonalPilotQueue().addPilot(pilot, pWeightClass);
				if(CampaignMain.cm.isUsingMySQL())
					CampaignMain.cm.MySQL.linkPilotToPlayer(pilot.getPilotId(), Username);
				return;
			}
		}

		/*
		 * Nothing prevents it from happening, so send the unit and update
		 * the players' client-side queue representations.
		 */
		targetplayer.getPersonalPilotQueue().addPilot(pilot, pWeightClass);
		if(CampaignMain.cm.isUsingMySQL())
			CampaignMain.cm.MySQL.linkPilotToPlayer(pilot.getPilotId(), targetplayer.getName());
		CampaignMain.cm.toUser("PL|RPPPQ|"+pUnitType+"|"+pWeightClass+"|"+pPosition,Username,false);
		CampaignMain.cm.toUser("PL|AP2PPQ|"+pUnitType+"|"+pWeightClass+"|"+pilot.toFileFormat("#",true),targetPlayer,false);
		//NOTE: No need to do checkQueueAndWarn b/c transfers that overload barracks are forbidden above.

		/* 
		 * build a skill desctiption. This looks exactly like ShortResolver.getNewPilotDescription
		 * and could probably be factored out. We use so many different pilot descritpion strings that
		 * it's ridiculous.
		 * 
		 * TODO: Rewrite and consolidate pilot description strings used in the GUI, mystatus, etc.
		 */
		String description = "";
		if (pUnitType == Unit.MEK || pUnitType == Unit.VEHICLE)
			description =  "[" + pilot.getGunnery()+"/"+pilot.getPiloting();
		else
			description = "[" + pilot.getGunnery();

		String skills = pilot.getSkillString(true);
		if (!skills.equals(" "))
			description += skills;

		description = description.trim() + "]";

		//tell the players in main. include unit type and skills, per RFE 1535927
		CampaignMain.cm.toUser("You transferred a " + Unit.getTypeClassDesc(pUnitType) + " pilot, " + pilot.getName() + " " + description + ", to " + targetplayer.getName() + ".", Username, true);
		CampaignMain.cm.toUser(player.getName() + " transferred a " + Unit.getTypeClassDesc(pUnitType) + "pilot, " + pilot.getName() + " " + description + ", to your command.", targetPlayer, true);

	}
}
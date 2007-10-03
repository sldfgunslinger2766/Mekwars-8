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

import java.util.ArrayList;
import java.util.StringTokenizer;

import common.util.StringUtils;

import megamek.common.AmmoType;
import megamek.common.Entity;
import megamek.common.Mounted;
import server.campaign.CampaignMain;
import server.campaign.SPlayer;
import server.campaign.SUnit;
import server.campaign.commands.Command;
import server.MWChatServer.auth.IAuthenticator;

/**
 * @author Helge Richter
 */
public class FixAmmoCommand implements Command {
	
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
		
		String targetName;
		SPlayer target = null;
		int unitID;
		
		try {
			targetName = command.nextToken();
			unitID = Integer.parseInt(command.nextToken());
		}catch (Exception ex) {
			CampaignMain.cm.toUser("Invalid Syntax: /FixAmmo Player#UnitID", Username);
			return;
		}
		
		target = CampaignMain.cm.getPlayer(targetName);
		
		if (target == null) {
			CampaignMain.cm.toUser("Target player could not be found. Try again.", Username, true);
			return;
		}
		
		//non null target, so attempt the scrap
		SUnit m = target.getUnit(unitID);
		
		//break out if the player doesn't have a unit with that id
		if (m == null) {
			CampaignMain.cm.toUser("Target player doesn't have a unit with ID# " + unitID + ".", Username, true);
			return;
		}
		
		fixAmmo(m);
		//tell the player the unit has been fixed
		CampaignMain.cm.toUser(targetName + "'s " + m.getModelName() + "'s ammo has been fixed.", Username, true);
		CampaignMain.cm.toUser(Username + " has fixed the ammo on your " + m.getModelName() + " (ID#" + m.getId() + ")", targetName, true);
		CampaignMain.cm.doSendModMail("NOTE",Username + " fixed the ammo on "+StringUtils.aOrAn(m.getModelName(),true) + " belonging to " + targetName);
        target.setSave();
        target.checkAndUpdateArmies(m);
        CampaignMain.cm.toUser("PL|UU|"+m.getId()+"|"+m.toString(true),targetName,false);
	}
	/**
	 * If the units ammo is different from the baseline entity the ammo is fixed.
	 * @param unit
	 */
	private void fixAmmo(SUnit unit) {
		Entity baseLine = null;
		Entity en = unit.getEntity();
		
		baseLine = unit.loadMech(unit.getUnitFilename());
		
		ArrayList<Mounted> baseLineAmmo = baseLine.getAmmo();
		ArrayList<Mounted> currentAmmo = en.getAmmo();
		
		for ( int pos = 0; pos < baseLineAmmo.size(); pos++) {
			AmmoType baseAmmo = (AmmoType)baseLineAmmo.get(pos).getType();
			AmmoType ammo = (AmmoType)currentAmmo.get(pos).getType();
			
			if ( !ammo.getInternalName().startsWith(baseAmmo.getInternalName()) ) {
				unit.getEntity().getAmmo().set(pos, baseLineAmmo.get(pos));
			}
		}
	}

}//end ModFullRepairCommand
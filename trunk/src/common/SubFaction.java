/*
 * MekWars - Copyright (C) 2007 
 * 
 * Original author - jtighe (torren@users.sourceforge.net)
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

package common;

import java.util.Properties;
import java.util.StringTokenizer;

public class SubFaction{
	
	private static Properties defaultSettings = new Properties();
	private Properties factionSettings = null;
	public int DBId = 0;
	
	public SubFaction(){
		factionSettings = new Properties(SubFaction.getDefault());
	}
	
	public SubFaction(String name){
		factionSettings = new Properties(SubFaction.getDefault());
		factionSettings.setProperty("Name", name);	
	}

	public SubFaction(String name, String accessLevel){
		factionSettings = new Properties(SubFaction.getDefault());
		factionSettings.setProperty("Name", name);
		factionSettings.setProperty("AccessLevel", accessLevel);
	}
	
	public static Properties getDefault(){
		defaultSettings.setProperty("Name", "");
		defaultSettings.setProperty("AccessLevel", "0");
		defaultSettings.setProperty("CanBuyNewLightMek", "true");
		defaultSettings.setProperty("CanBuyNewMediumMek", "true");
		defaultSettings.setProperty("CanBuyNewHeavyMek", "true");
		defaultSettings.setProperty("CanBuyNewAssaultMek", "true");
		defaultSettings.setProperty("CanBuyUsedLightMek", "true");
		defaultSettings.setProperty("CanBuyUsedMediumMek", "true");
		defaultSettings.setProperty("CanBuyUsedHeavyMek", "true");
		defaultSettings.setProperty("CanBuyUsedAssaultMek", "true");
		defaultSettings.setProperty("CanBuyNewLightVehicle", "true");
		defaultSettings.setProperty("CanBuyNewMediumVehicle", "true");
		defaultSettings.setProperty("CanBuyNewHeavyVehicle", "true");
		defaultSettings.setProperty("CanBuyNewAssaultVehicle", "true");
		defaultSettings.setProperty("CanBuyUsedLightVehicle", "true");
		defaultSettings.setProperty("CanBuyUsedMediumVehicle", "true");
		defaultSettings.setProperty("CanBuyUsedHeavyVehicle", "true");
		defaultSettings.setProperty("CanBuyUsedAssaultVehicle", "true");
		defaultSettings.setProperty("CanBuyNewLightInfantry", "true");
		defaultSettings.setProperty("CanBuyNewMediumInfantry", "true");
		defaultSettings.setProperty("CanBuyNewHeavyInfantry", "true");
		defaultSettings.setProperty("CanBuyNewAssaultInfantry", "true");
		defaultSettings.setProperty("CanBuyUsedLightInfantry", "true");
		defaultSettings.setProperty("CanBuyUsedMediumInfantry", "true");
		defaultSettings.setProperty("CanBuyUsedHeavyInfantry", "true");
		defaultSettings.setProperty("CanBuyUsedAssaultInfantry", "true");
		defaultSettings.setProperty("CanBuyNewLightProtoMek", "true");
		defaultSettings.setProperty("CanBuyNewMediumProtoMek", "true");
		defaultSettings.setProperty("CanBuyNewHeavyProtoMek", "true");
		defaultSettings.setProperty("CanBuyNewAssaultProtoMek", "true");
		defaultSettings.setProperty("CanBuyUsedLightProtoMek", "true");
		defaultSettings.setProperty("CanBuyUsedMediumProtoMek", "true");
		defaultSettings.setProperty("CanBuyUsedHeavyProtoMek", "true");
		defaultSettings.setProperty("CanBuyUsedAssaultProtoMek", "true");
		defaultSettings.setProperty("CanBuyNewLightBattleArmor", "true");
		defaultSettings.setProperty("CanBuyNewMediumBattleArmor", "true");
		defaultSettings.setProperty("CanBuyNewHeavyBattleArmor", "true");
		defaultSettings.setProperty("CanBuyNewAssaultBattleArmor", "true");
		defaultSettings.setProperty("CanBuyUsedLightBattleArmor", "true");
		defaultSettings.setProperty("CanBuyUsedMediumBattleArmor", "true");
		defaultSettings.setProperty("CanBuyUsedHeavyBattleArmor", "true");
		defaultSettings.setProperty("CanBuyUsedAssaultBattleArmor", "true");
		defaultSettings.setProperty("MinELO", "0");
		defaultSettings.setProperty("MinExp", "0");
		
		return defaultSettings;
	}
	
	public String getConfig(String key){
		
		if ( !factionSettings.containsKey(key) ){
			
			if ( SubFaction.getDefault().containsKey(key) )
				return SubFaction.getDefault().getProperty(key);
			
			System.err.println("Unable to find subfaction config: "+key);
			return "-1";
		}
		
		return factionSettings.getProperty(key);
	}
	
	public void setConfig(String key, String value){
		factionSettings.setProperty(key, value);
	}
	
	public String toString(){
		StringBuffer result = new StringBuffer();
		
		if ( factionSettings.size() < 1 )
			return "# #";
		for (Object key : factionSettings.keySet()){
			result.append(key.toString());
			result.append("#");
			result.append(factionSettings.getProperty(key.toString()));
			result.append("#");
		}
		
		return result.toString();
	}
	
	public void fromString(String settings){
		StringTokenizer propertyList = new StringTokenizer(settings,"#");
		
		while ( propertyList.hasMoreElements() ){
			
			String key = propertyList.nextToken();
			
			if ( !propertyList.hasMoreElements() )
				return;
			
			String value = propertyList.nextToken();
			setConfig(key, value);
		}
	}
}
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

package server.campaign.util;

import gd.xml.ParseException;
import gd.xml.XMLParser;
import gd.xml.XMLResponder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Vector;
import java.awt.Dimension;

import server.MMServ;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SPlanet;
import server.campaign.SUnitFactory;

import common.AdvanceTerrain;
import common.Continent;
import common.Influences;
import common.PlanetEnvironments;
import common.UnitFactory;

@SuppressWarnings({"unchecked","serial"})
public class XMLPlanetDataParser implements XMLResponder
{
	String lastElement = "";
	String lastInfFaction = "";
	String Name = "";
	String MFName = null;
	String MFSize = null;
	String MFFounder = null;
	String XCood = null;
	String YCood = null;
	
	int Income;
	int MFTicksUntilRefresh = 0;
	int MFRefreshSpeed = 100;
	int Type = 0;
	
	HashMap<Integer,Integer> Influence = new HashMap<Integer,Integer>();//House ID, Amount
	Vector<SPlanet> planets = new Vector<SPlanet>();
	
	Vector<UnitFactory> unitFactories = new Vector<UnitFactory>();
	
	private String filename;
	private String prefix;
	private String Description = "";
	private PlanetEnvironments PlanEnv  = new PlanetEnvironments();
	private AdvanceTerrain AdvTerr = null;
	public TreeMap<Integer,AdvanceTerrain> AdvTerrTreeMap = new TreeMap<Integer,AdvanceTerrain>();
	private TreeMap<String,String> OpFlags = new TreeMap<String,String>();
	boolean conquerable = true;
	private int counter = 1;
	int xmap = 1;
	int ymap = 1;
	int xboard = 16;
	int yboard = 17;
	int lowtemp = 25;
	int hitemp = 25;
	double gravity = 1.0;
	boolean vacuum = false;
	int nightchance = 0;
	int nightmod = 0;
	boolean map = false;
	String mapname = "";
	String aterrainName = "";
	
	int CompProduction = 0;
	int Warehousesize = 0;
	boolean inWarehouse = false;
	boolean inContinent = false;
	boolean hasAdvanceTerrain = false;
	int terrainProb = 0;
	String terrainName = "";
	String OriginalOwner = CampaignMain.cm.getConfig("NewbieHouseName");
	String OpFlag = "";
	String OpName = "";
	boolean isHomeWorld = false;
	
	public XMLPlanetDataParser(String filename) {
		
		this.filename = filename;
		try {
			XMLParser xp = new XMLParser();
			xp.parseXML(this);
		} catch (Exception ex) {
			MMServ.mmlog.errLog(ex);
		}
	}
	
	public Vector<SPlanet> getPlanets() {
		return planets;
	}
	
	/* DTD METHODS */
	
	public void recordNotationDeclaration(String name, String pubID, String sysID) throws ParseException {
		System.out.print(prefix+"!NOTATION: "+name);
		if (sysID!=null) System.out.print("  sysID = "+sysID);
		MMServ.mmlog.mainLog("");
	}
	
	public void recordEntityDeclaration(String name, String value, String pubID, String sysID, String notation) throws ParseException {
		System.out.print(prefix+"!ENTITY: "+name);
		if (value!=null) System.out.print("  value = "+value);
		if (pubID!=null) System.out.print("  pubID = "+pubID);
		if (sysID!=null) System.out.print("  sysID = "+sysID);
		if (notation!=null) System.out.print("  notation = "+notation);
		MMServ.mmlog.mainLog("");
	}
	
	public void recordElementDeclaration(String name, String content) throws ParseException {
		System.out.print(prefix+"!ELEMENT: "+name);
		MMServ.mmlog.mainLog("  content = "+content);
	}
	
	public void recordAttlistDeclaration(String element, String attr, boolean notation, String type, String defmod, String def) throws ParseException {
		System.out.print(prefix+"!ATTLIST: "+element);
		System.out.print("  attr = "+attr);
		System.out.print("  type = " + ((notation) ? "NOTATIONS " : "") + type);
		System.out.print("  def. modifier = "+defmod);
		MMServ.mmlog.mainLog( (def==null) ? "" : "  def = "+notation);
	}
	
	public void recordDoctypeDeclaration(String name, String pubID, String sysID) throws ParseException {
		System.out.print(prefix+"!DOCTYPE: "+name);
		if (pubID!=null) System.out.print("  pubID = "+pubID);
		if (sysID!=null) System.out.print("  sysID = "+sysID);
		MMServ.mmlog.mainLog("");
		prefix = "";
	}
	
	/* DOC METHDODS */
	
	public void recordDocStart() {
	}
	
	public void recordDocEnd() {
		MMServ.mmlog.mainLog("");
		MMServ.mmlog.mainLog("Parsing finished without error");
	}
	
	public void recordElementStart(String name, Hashtable attr) throws ParseException {
		//MMServ.mmlog.mainLog(prefix+"Element: "+name);
		lastElement = name;
		if (name.equalsIgnoreCase("WAREHOUSE"))
			inWarehouse = true;
		if (name.equalsIgnoreCase("CONTINENT"))
			inContinent = true;
		if (name.equalsIgnoreCase("ADVANCETERRAIN")){
			hasAdvanceTerrain = true;
			AdvTerr = new AdvanceTerrain();
			if ( !CampaignMain.cm.getBooleanConfig("UseStaticMaps")){
				CampaignMain.cm.getConfig().setProperty("UseStaticMaps","true");
				CampaignMain.cm.saveConfigureFile(CampaignMain.cm.getConfig(),CampaignMain.cm.getServer().getConfigParam("CAMPAIGNCONFIG"));
			}
		}
		
		/*      if (attr!=null) {
		 Enumeration e = attr.keys();
		 System.out.print(prefix);
		 String conj = "";
		 while (e.hasMoreElements()) {
		 Object k = e.nextElement();
		 System.out.print(conj+k+" = "+attr.get(k));
		 conj = ", ";
		 }
		 MMServ.mmlog.mainLog("");
		 }
		 prefix = prefix+"  ";*/
	}
	
	public void recordElementEnd(String name) throws ParseException {
		
		if (name.equalsIgnoreCase("TIMEZONE")) {
			MMServ.mmlog.errLog("planets.xml contains TIMEZONE field. No longer necessary!");
		}
		
		if (name.equalsIgnoreCase("UNITFACTORY")) {
			if (MFName != null && MFFounder != null && MFSize != null) {
				
				//if (Type == 0)
				//	Type = Unit.MEK;
				
				SUnitFactory mf = new SUnitFactory(MFName,null,MFSize,MFFounder,MFTicksUntilRefresh,MFRefreshSpeed,Type);
				unitFactories.add(mf);
				
				//RESET VARIABLES
				MFName = null;
				MFSize = null;
				MFFounder = null;
				MFTicksUntilRefresh = 0;
				MFRefreshSpeed = 100;
				Type = 0;
			}
		}
		if (name.equalsIgnoreCase("CONTINENT")){
			Continent cont = new Continent(terrainProb, CampaignMain.cm.getData().getTerrainByName(terrainName));
			PlanEnv.add(cont);
			terrainProb = 0;
			terrainName = "";
			inContinent = false;
			if ( hasAdvanceTerrain )
				AdvTerrTreeMap.put(cont.getEnvironment().getId(),AdvTerr);
		}
		if ( name.equalsIgnoreCase("ADVANCETERRAIN")){
			AdvTerr.setDisplayName(aterrainName);
			AdvTerr.setGravity(gravity);
			AdvTerr.setHighTemp(hitemp);
			AdvTerr.setLowTemp(lowtemp);
			AdvTerr.setStaticMap(map);
			AdvTerr.setStaticMapName(mapname);
			AdvTerr.setXBoardSize(xboard);
			AdvTerr.setXSize(xmap);
			AdvTerr.setNightChance(nightchance);
			AdvTerr.setNightTempMod(nightmod);
			AdvTerr.setYBoardSize(yboard);
			AdvTerr.setYSize(ymap);
			AdvTerr.setVacuum(vacuum);
		}
		
		if ( name.equalsIgnoreCase("OPNAME") ) {
			OpFlags.put(OpFlag,OpName);
			OpFlag = "";
			OpName = "";
		}
		
		if (name.equalsIgnoreCase("PLANET"))
		{
			MMServ.mmlog.mainLog("PLANET READ");
			SPlanet p;
			p = new SPlanet(
					counter++,
					Name,
					null,
					Income,
					CompProduction,
					Double.parseDouble(XCood),
					Double.parseDouble(YCood));
			for (int i = 0; i < unitFactories.size();i++) {
				SUnitFactory MF  = (SUnitFactory)unitFactories.get(i);
				MF.setPlanet(p);
			}
			p.setUnitFactories(unitFactories);
			p.setEnvironments(PlanEnv);
			p.setDescription(Description);
			p.setBaysProvided(Warehousesize);
			MMServ.mmlog.mainLog("Influence: " + Influence);
			//This has to be called last since the Bays provided are added to the faction then for instance
			p.setInfluence(new Influences(Influence));
			p.setConquerable(conquerable);
			p.setMapSize(new Dimension(xmap,ymap));
			p.setBoardSize(new Dimension(xboard,yboard));
			p.setTemp(new Dimension(lowtemp,hitemp));
			p.setGravity(gravity);
			p.setVacuum(vacuum);
			p.setOwner(null,p.checkOwner(),false);//no old owner, no updates
			if ( hasAdvanceTerrain)
				p.getAdvanceTerrain().putAll(AdvTerrTreeMap);
			/*        for ( Integer id: AdvTerrTreeMap.keySet() ){
			 p.getAdvanceTerrain().put(id,AdvTerrTreeMap.get(id));
			 }*/
			p.setOriginalOwner(OriginalOwner);
			p.setPlanetFlags(OpFlags);
			p.setHomeWorld(isHomeWorld);
			
			planets.add(p);
			
			//RESET VARIABLES
			conquerable = true;
			Name = null;
			Income = 0;
			XCood = null;
			YCood = null;
			Influence = new HashMap<Integer,Integer>();
			OpFlags.clear();
			unitFactories = new Vector<UnitFactory>();
			Description = "";
			PlanEnv = new PlanetEnvironments();
			Warehousesize = 0;
			CompProduction = 0;
			hasAdvanceTerrain = false;
			xboard = -1;
			yboard = -1;
			nightchance = 0;
			nightmod = 0;
			map = false;
			mapname = "";
			aterrainName = "";
			gravity = 1.0;
			vacuum = false;
			lowtemp = 25;
			hitemp = 25;
			ymap = -1;
			xmap = -1;
			isHomeWorld = false;
			
			OriginalOwner = CampaignMain.cm.getConfig("NewbieHouseName");
			AdvTerr = null;
			AdvTerrTreeMap.clear();
			
		}
		if (name.equalsIgnoreCase("WAREHOUSE"))
			inWarehouse = false;
	}
	
	public void recordPI(String name, String pValue) {
		MMServ.mmlog.mainLog(prefix+"*"+name+" PI: "+pValue);
	}
	
	public void recordCharData(String charData) {
		//MMServ.mmlog.mainLog(prefix+charData);
		if (!charData.equalsIgnoreCase("")) {
			//do nothing; //MMServ.mmlog.mainLog(lastElement + " --> " + charData);
		} else
			lastElement = "";
		
		if (lastElement.equalsIgnoreCase("NAME")) {	
			Name = charData;
			MMServ.mmlog.mainLog(Name);
		} else if (lastElement.equalsIgnoreCase("INCOME"))
			Income = Integer.parseInt(charData);
		else if (lastElement.equalsIgnoreCase("XCOOD"))
			XCood = charData;
		else if (lastElement.equalsIgnoreCase("YCOOD"))
			YCood = charData;
		else if (lastElement.equalsIgnoreCase("WAREHOUSE"))
			Warehousesize = Integer.parseInt(charData);
		else if (lastElement.equalsIgnoreCase("FACTION"))
			lastInfFaction = charData;
		else if (lastElement.equalsIgnoreCase("AMOUNT")) {
			SHouse h = CampaignMain.cm.getHouseFromPartialString(lastInfFaction,null);
			if (h != null) {
				Influence.put(new Integer(h.getId()),new Integer(charData));
				MMServ.mmlog.mainLog("Parsed: " + h.toString() + " - " + charData);
			}
			else
				MMServ.mmlog.mainLog("ERROR READING FACTION: " + lastInfFaction);
		}
		
		else if (lastElement.equalsIgnoreCase("FACTORYNAME"))
			MFName = charData;
		else if (lastElement.equalsIgnoreCase("FOUNDER"))
			MFFounder = charData;
		else if (lastElement.equalsIgnoreCase("SIZE")) {
			if (inWarehouse)
				Warehousesize = Integer.parseInt(charData);
			else if (inContinent)
				terrainProb = Integer.parseInt(charData);
			else
				MFSize = charData;
		} else if (lastElement.equalsIgnoreCase("TICKSUNTILREFRESH"))
			MFTicksUntilRefresh = Integer.parseInt(charData);
		else if (lastElement.equalsIgnoreCase("REFRESHSPEED"))
			MFRefreshSpeed = Integer.parseInt(charData);
		//else if (lastElement.equalsIgnoreCase("TIMEZONENAME"))
		//	TZData.setName(charData);
		//else if (lastElement.equalsIgnoreCase("TIMEZONEPROPABILITY"))
		//	TZData.setPropability(Integer.parseInt(charData));
		else if (lastElement.equalsIgnoreCase("TYPE")) {
			if (charData.equalsIgnoreCase("VEHICLE"))
				Type = Type + UnitFactory.BUILDVEHICLES;
			else if (charData.equalsIgnoreCase("INFANTRY"))
				Type = Type + UnitFactory.BUILDINFANTRY;
			else if (charData.equalsIgnoreCase("BATTLEARMOR"))
				Type = Type + UnitFactory.BUILDBATTLEARMOR;
			else if (charData.equalsIgnoreCase("PROTOMEK"))
				Type = Type + UnitFactory.BUILDPROTOMECHS;
			else
				Type = Type + UnitFactory.BUILDMEK;
		} else if (lastElement.equalsIgnoreCase("COMPPRODUCTION"))
			CompProduction = Integer.parseInt(charData);
		else if (lastElement.equalsIgnoreCase("CONQUERABLE"))
			conquerable = Boolean.parseBoolean(charData);
		else if (lastElement.equalsIgnoreCase("TERRAIN"))
			terrainName = charData;
		else if (lastElement.endsWith("XMAP"))
			xmap = Integer.parseInt(charData);
		else if (lastElement.endsWith("YMAP"))
			ymap = Integer.parseInt(charData);
		else if (lastElement.endsWith("XBOARD"))
			xboard = Integer.parseInt(charData);
		else if (lastElement.endsWith("YBOARD"))
			yboard = Integer.parseInt(charData);
		else if (lastElement.endsWith("LOWTEMP"))
			lowtemp = Integer.parseInt(charData);
		else if (lastElement.endsWith("HITEMP"))
			hitemp = Integer.parseInt(charData);
		else if (lastElement.endsWith("GRAVITY"))
			gravity = Double.parseDouble(charData);
		else if (lastElement.endsWith("VACUUM"))
			vacuum = Boolean.parseBoolean(charData);
		else if (lastElement.endsWith("MAP"))
			map = Boolean.parseBoolean(charData);
		else if (lastElement.endsWith("NIGHTCHANCE"))
			nightchance = Integer.parseInt(charData);
		else if (lastElement.endsWith("NIGHTMOD"))
			nightmod = Integer.parseInt(charData);
		else if (lastElement.endsWith("MAPNAME"))
			mapname = charData;
		else if (lastElement.endsWith("TERRAINNAME"))
			aterrainName = charData;
		else if (lastElement.endsWith("ORIGINALOWNER"))
			OriginalOwner = charData;
		else if (lastElement.endsWith("OPKEY"))
			OpFlag = charData;
		else if (lastElement.endsWith("OPNAME"))
			OpName = charData;
		else if (lastElement.endsWith("HOMEWORLD"))
			isHomeWorld = Boolean.parseBoolean(charData);
		
		
	}
	
	public void recordComment(String comment) {
		MMServ.mmlog.mainLog(prefix+"*Comment: "+comment);
	}
	
	/* INPUT METHODS */
	public InputStream getDocumentStream() throws ParseException {
		try { return new FileInputStream(filename); }
		catch (FileNotFoundException e) { throw new ParseException("could not find the specified file"); }
	}
	
	public InputStream resolveExternalEntity(String name, String pubID, String sysID) throws ParseException {
		if (sysID!=null) {
			File f = new File((new File(filename)).getParent(), sysID);
			try { return new FileInputStream(f); }
			catch (FileNotFoundException e) { throw new ParseException("file not found ("+f+")"); }
		}
		//else
		return null;
	}
	
	public InputStream resolveDTDEntity(String name, String pubID, String sysID) throws ParseException {
		return resolveExternalEntity(name, pubID, sysID);
	}
	
	
	public static String newLineToBR(String data)
	{
		StringTokenizer tokened = new StringTokenizer(data,"\n");
		String result = new String();
		while (tokened.hasMoreElements())
		{
			result += tokened.nextElement();
			if (tokened.hasMoreElements())
				result += "<BR>";
		}
		return result;
	}
	
}

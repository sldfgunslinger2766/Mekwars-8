	/*
	 * MekWars - Copyright (C) 2007 
	 * 
	 * Original author - Bob Eldred (billypinhead@users.sourceforge.net)
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

	
package server.mwmysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import common.CampaignData;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SPlanet;
import server.campaign.SPlayer;
import server.campaign.operations.OperationReportEntry;
import server.campaign.pilot.SPilot;

import common.Unit;

public class mysqlHandler{
  private MWmysql MySQLCon = null;
  private planetHandler ph = null;
  private factoryHandler fh = null;
  private PilotHandler pih = null;
  private UnitHandler uh = null;
  private FactionHandler fah = null;
  private PlayerHandler plh = null;
  private PhpBBConnector phpBBCon = null;
  private HistoryHandler hh = null;

  private final int currentDBVersion = 34;
  
  public void closeMySQL(){
	  MySQLCon.close();
	  if(CampaignMain.cm.isSynchingBB())
		  phpBBCon.close();
  }
  
  public boolean isSynchingBB() {
      boolean isUsing = Boolean.parseBoolean(CampaignMain.cm.getServer().getConfigParam("MYSQL_SYNCHPHPBB"));

      if (isUsing && phpBBCon == null) {
          phpBBCon = new PhpBBConnector();
          phpBBCon.init();
      } else if (!isUsing && phpBBCon != null) {
          phpBBCon.close();
          phpBBCon = null;
      }
      
      if(isUsing) {
    	  try {
    		  if(phpBBCon.con.isClosed()){
    			  phpBBCon = new PhpBBConnector();
    			  phpBBCon.init();
    		  }
    	  } catch (SQLException e) {
    		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.isSynchingBB: " + e.getMessage());
    		  CampaignData.mwlog.dbLog(e);
    	  }
      }
      return isUsing;
  }
  
  public int getUnitDBIdFromMWId(int MWId) {
	  return uh.getUnitDBIdFromMWId(MWId);
  }
  
  public void clearArmies(int userID) {
	  Connection c = getConnection();
	  PreparedStatement ps = null;
	  try {
		  ps = c.prepareStatement("DELETE from playerarmies WHERE playerID = ?");
		  ps.setInt(1, userID);
		  ps.executeUpdate();
		  ps.close();
	  } catch (SQLException e) {
		 CampaignData.mwlog.dbLog("SQLException in mysqlHandler.clearArmies: " + e.getMessage());
         CampaignData.mwlog.dbLog(e);
	  } finally {
		  if (ps != null)  {
			  try {
				  ps.close();
			  } catch (SQLException e) {}
		  }
		  returnConnection(c);
	  }
  }
  
  public int getFactoryIdByNameAndPlanet(String fName, String pName) {
	  return fh.getFactoryIdByNameAndPlanet(fName, pName);
  }
  
  public void deleteArmy(int userID, int armyID) {
	  PreparedStatement ps = null;
	  Connection c = getConnection();
	  
	  try {
		  ps = c.prepareStatement("DELETE from playerarmies WHERE playerID = ? AND armyID = ?");
		  ps.setInt(1, userID);
		  ps.setInt(2, armyID);
		  ps.executeUpdate();
		  ps.close();
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.deleteArmy: " + e.getMessage());
          CampaignData.mwlog.dbLog(e);
	  } finally {
		  if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {}
		  }
		  returnConnection(c);
	  }
  }
  
  public boolean addUserToForum(String name, String pass, String email) {
	  return phpBBCon.addToForum(name, pass, email);
  }
  
  public void addUserToHouseForum(int userID, int forumID) {
	  phpBBCon.addToHouseForum(userID, forumID);
  }
  
  public void removeUserFromHouseForum(int userID, int houseForumID) {
	  phpBBCon.removeFromHouseForum(userID, houseForumID);
  }
  
  public void backupDB(long time) {
	  MySQLCon.backupDB(time);
  }
  
  public void deleteFactory(int FactoryID){
    fh.deleteFactory(FactoryID);
  }

  public void deletePlanetFactories(String planetName){
    fh.deletePlanetFactories(planetName);
  }

  public void loadFactories(SPlanet planet){
    fh.loadFactories(planet);
  }
  
  public int countPlanets() {
	  return ph.countPlanets();
  }
  
  public void loadPlanets(CampaignData data) {
	  ph.loadPlanets(data);
  }
  
  public void saveInfluences(SPlanet planet) {
	  ph.saveInfluences(planet);
  }
  
  public void saveEnvironments(SPlanet planet) {
	  ph.saveEnvironments(planet);
  }
  
  public void savePlanetFlags(SPlanet planet) {
	  ph.savePlanetFlags(planet);
  }
  
  public void deletePlanet(int PlanetID) {
	  ph.deletePlanet(PlanetID);
  }
  
  public void loadFactionPilots(SHouse h) {
	  ResultSet rs = null;
	  Statement stmt = null;
	  Connection c = getConnection();
	  try {
		  stmt = c.createStatement();

		  for (int x = Unit.MEK; x < Unit.MAXBUILD; x++) {
			  h.getPilotQueues().setFactionID(h.getDBId());
			  rs = stmt.executeQuery("SELECT pilotID from pilots WHERE factionID = " + h.getId() + " AND pilotType= " + x);
			  while(rs.next()) {
				  SPilot p = pih.loadPilot(rs.getInt("pilotID"));
				  h.getPilotQueues().loadPilot(x, p);
			  }			  
		  }
		  if(rs!=null)
			  rs.close();
		  stmt.close();
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQL Error in mysqlHandler.loadFactionPilots: " + e.getMessage());
          CampaignData.mwlog.dbLog(e);
	  } finally {
		  if (rs != null) {
			  try {
				  rs.close();
			  } catch (SQLException e) {}
		  }
		  if (stmt != null) {
			  try {
				  stmt.close();
			  } catch (SQLException e) {}
		  } 
	  }
	  returnConnection(c);
  }
  
  public void deleteFactionPilots(int factionID) {
	  pih.deleteFactionPilots(factionID);
  }
  
  public void deleteFactionPilots(int factionID, int type) {
	  pih.deleteFactionPilots(factionID, type);
  }
  
  public void deletePlayerPilots(int playerID) {
	  pih.deletePlayerPilots(playerID);
  }
  
  public void deletePlayerPilots(int playerID, int unitType, int unitWeight) {
	  pih.deletePlayerPilots(playerID, unitType, unitWeight);
  }
  
  public void deletePilot(int pilotID) {
	  pih.deletePilot(pilotID);
  }
  
  public SPilot loadUnitPilot(int unitID) {
	  return pih.loadUnitPilot(unitID);
  }
  
  public SPilot loadPilot(int pilotID) {
	  return pih.loadPilot(pilotID);
  }
  
  public void unlinkUnit(int unitID) {
	  uh.unlinkUnit(unitID);
  }
  
  public void linkUnitToPlayer(int unitID, int playerID) {
	  uh.linkUnitToPlayer(unitID, playerID);
  }
  
  public void linkUnitToFaction(int unitID, int factionID){
	  uh.linkUnitToFaction(unitID, factionID);
  }
  
  public void deleteUnit(int unitID) {
	  uh.deleteUnit(unitID);
  }
  
  public void loadFactions(CampaignData data) {
	  fah.loadFactions(data);
  }

  public int countFactions() {
	  return fah.countFactions();
  }
  
  public int countPlayers() {
	  return plh.countPlayers();
  }
  
  public int getPlayerIDByName(String name) {
	  return plh.getPlayerIDByName(name);
  }
  
  public void setPlayerPassword(int ID, String password) {
	  plh.setPassword(ID, password);
  }
  
  public void setPlayerAccess(int ID, int level) {
	  plh.setPlayerAccess(ID, level);
  }
  
  public boolean matchPassword(String playerName, String pass) {
	  return plh.matchPassword(playerName, pass);
  }
  
  public boolean playerExists(String name) {
	  return plh.playerExists(name);
  }
  
  public void deletePlayer(SPlayer p) {
	  plh.deletePlayer(p, true);
  }
  
  public void deletePlayer(SPlayer p, boolean deleteForumAccount) {
	  plh.deletePlayer(p, deleteForumAccount);
  }
  
  public void deleteForumAccount(int forumID) {
	  phpBBCon.deleteForumAccount(forumID);
  }
  
  public void purgeStalePlayers(long days) {
	  plh.purgeStalePlayers(days);
  }
  
  public void saveSubFaction(String SubFactionString, int houseID) {
	  fah.saveSubFaction(SubFactionString, houseID);
  }
  
  public void deleteSubFaction(String subFactionName, int houseID) {
	  fah.deleteSubFaction(subFactionName, houseID);
  }
  
  public int getDBVersion() {
	  Connection c = getConnection();
	  Statement stmt = null;
	  ResultSet rs = null;
	  int toReturn = 0;
	  try {
		  stmt = c.createStatement();
		  rs = stmt.executeQuery("SELECT config_value from config WHERE config_key = 'mekwars_database_version'");
		  if(rs.next()) {
			  toReturn = rs.getInt("config_value");
		  }
		  rs.close();
		  stmt.close();
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQL Error in mysqlHandler.getDBVersion: " + e.getMessage());
          CampaignData.mwlog.dbLog(e);
	  } finally {
		  if (rs != null) {
			  try {
				  rs.close();
			  } catch (SQLException e) {}
		  }
		  if (stmt != null) {
			  try {
				  stmt.close();
			  } catch (SQLException e) {}
		  }
		  returnConnection(c);
	  }
	  return toReturn;
  }
  
  private boolean databaseIsUpToDate() {
	  if(getDBVersion() == currentDBVersion){
		  CampaignData.mwlog.dbLog("Database up to date");
		  return true;
	  }
	  CampaignData.mwlog.dbLog("Database is an incorrect version!  Please update.");
	  CampaignData.mwlog.dbLog("Current Version: " + currentDBVersion + "   --   Your version: " + getDBVersion());
	  return false;
  }
  
  public void checkAndUpdateDB() {
	  if(databaseIsUpToDate())
		  return;
	  CampaignData.mwlog.mainLog("Database out of date.  Shutting down to avoid data corruption.");
	  CampaignData.mwlog.mainLog("Required version: " + currentDBVersion + ", your version: " + getDBVersion());
	  System.exit(0);
  }

  public PreparedStatement BBgetPreparedStatement(String sql) {
	  try {
		  return getBBConn().prepareStatement(sql);
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.BBgetPreparedStatement: " + e.getMessage());
          CampaignData.mwlog.dbLog(e);
		  return null;
	  }
  }
  
  public PreparedStatement BBgetPreparedStatement(String sql, int autoGeneratedKeys) {
	  try {
		  return getBBConn().prepareStatement(sql, autoGeneratedKeys);
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.BBgetPreparedStatement(String, Int): " + e.getMessage());
          CampaignData.mwlog.dbLog(e);
		  return null;
	  }
  }
  
  public PreparedStatement BBgetPreparedStatement(String sql, int[] columnIndexes) {
	  try {
		  return getBBConn().prepareStatement(sql, columnIndexes);
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.BBgetPreparedStatement(String, int[]): " + e.getMessage());
          CampaignData.mwlog.dbLog(e);
		  return null;
	  }
  }

  public PreparedStatement BBgetPreparedStatement(String sql, String[] columnNames) {
	  try {
		  return getBBConn().prepareStatement(sql, columnNames);
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.BBgetPreparedStatement(String, String[]): " + e.getMessage());
          CampaignData.mwlog.dbLog(e);
		  return null;
	  }
  }

  public PreparedStatement BBgetPreparedStatement(String sql, int resultSetType, int resultSetConcurrency) {
	  try {
		  return getBBConn().prepareStatement(sql, resultSetType, resultSetConcurrency);
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.BBgetPreparedStatement(String, int, int): " + e.getMessage());
          CampaignData.mwlog.dbLog(e);
		  return null;
	  }
  }
  
  public PreparedStatement BBgetPreparedStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
	  try {
		  return getBBConn().prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.BBgetPreparedStatement(String, int, int, int): " + e.getMessage());
          CampaignData.mwlog.dbLog(e);
		  return null;
	  }
  }
  
  public Statement BBgetStatement() {
	  try {
		  return getBBConn().createStatement();
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.BBgetStatement: " + e.getMessage());
          CampaignData.mwlog.dbLog(e);
		  return null;
	  }
  }
  
  public Statement BBgetStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) {
	  try {
		  return getBBConn().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.BBgetStatement(int, int, int): " + e.getMessage());
		  CampaignData.mwlog.dbLog("resultSetType: " + resultSetType);
		  CampaignData.mwlog.dbLog("resultSetConcurrency: " + resultSetConcurrency);
		  CampaignData.mwlog.dbLog("resultSetHoldability: " + resultSetHoldability);
          CampaignData.mwlog.dbLog(e);
		  return null;
	  }
  }
    
  public Statement BBgetStatement(int resultSetType, int resultSetConcurrency) {
	  try {
		  return getBBConn().createStatement(resultSetType, resultSetConcurrency);
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.BBgetStatement(int, int): " + e.getMessage());
		  CampaignData.mwlog.dbLog("resultSetType: " + resultSetType);
		  CampaignData.mwlog.dbLog("resultSetConcurrency: " + resultSetConcurrency);
          CampaignData.mwlog.dbLog(e);
		  return null;
	  }
  }
  
  public int getHouseForumID(String houseName) {
	  return phpBBCon.getHouseForumID(houseName);
  }
  
  public int getUserForumID(String userName, String userEmail) {
	  return phpBBCon.getUserForumID(userName, userEmail);
  }
  
  public String getActivationKey(int userID) {
	  return phpBBCon.getActivationKey(userID);
  }
  
  public void validateUser(int forumID) {
	  phpBBCon.validateUser(forumID);
  }
  
  public void changeForumName(String oldname, String newname) {
	  phpBBCon.changeForumName(oldname, newname);
  }
  
  public void addHistoryEntry(int historyType, int unitID, int eventType, String fluff) {
	  hh.addHistoryEntry(historyType, unitID, eventType, fluff);
  }
  
  public void commitBattleReport(OperationReportEntry opData) {
	  hh.commitBattleReport(opData);
  }
  
  public void saveConfig() {
	  PreparedStatement ps = null;
	  Properties configs = CampaignMain.cm.getConfig();
	  Set<Object> keys = configs.keySet();
	  String key = "";
	  Iterator<Object> keyIterator = keys.iterator();
	  Connection c = getConnection();
	  try {
		  ps = c.prepareStatement("REPLACE into campaign_config SET config_name=?, config_value=?");
		  while(keyIterator.hasNext()) {  
			key = (String) keyIterator.next();
			ps.setString(1, key);
			ps.setString(2, configs.getProperty(key));
			ps.executeUpdate();
		}
		  ps.close();
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.saveConfig: " + e.getMessage());
		  CampaignData.mwlog.dbLog(e);
	  } finally {
		  if(ps != null)
			  try {
				  ps.close();
			  } catch (SQLException ex) {
				  
			  }
	  }
	  returnConnection(c);
  }
  
  public boolean configIsSaved() {
	  PreparedStatement ps = null;
	  ResultSet rs = null;
	  boolean isSaved = false;
	  Connection c = getConnection();
	  
	  try {
		ps = c.prepareStatement("SELECT COUNT(*) as num from campaign_config");
		rs = ps.executeQuery();
		if(rs.next()) {
			if(rs.getInt("num") > 0) {
				isSaved = true;
			}
		}
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.configIsSaved: " + e.getMessage());
		  CampaignData.mwlog.dbLog(e);
	  } finally {
		  try {
			  if(rs != null)
				  rs.close();
			  if(ps != null)
				  ps.close();
		  } catch (SQLException ex) {  }
		  returnConnection(c);
	  }
	  CampaignData.mwlog.dbLog("Campaign saved in DB: " + Boolean.toString(isSaved));
	  return isSaved;
  }
  
  public void loadConfig(Properties config) {
	  PreparedStatement ps = null;
	  ResultSet rs = null;
	  Connection c = getConnection();
	  
	  try {
		  ps = c.prepareStatement("SELECT * from campaign_config");
		  rs = ps.executeQuery();
		  while(rs.next()) {
			  config.put(rs.getString("config_name"), rs.getString("config_value"));
		  }
	  } catch (SQLException e) {
		  CampaignData.mwlog.dbLog("SQLException in mysqlHandler.loadConfig: " + e.getMessage());
		  CampaignData.mwlog.dbLog(e);
	  } finally {
		  try {
			  if(rs != null)
				  rs.close();
			  if(ps != null)
				  ps.close();
		  } catch (SQLException ex) {}
	  }
	  returnConnection(c);
  }
  
  public void addMechstatUnitScrap(String name) {
	  hh.addMechstat(name, HistoryHandler.MECHSTAT_TYPE_UNITSCRAPPED);
  }
  
  public void addMechstatUnitWin(String name) {
	  hh.addMechstat(name, HistoryHandler.MECHSTAT_TYPE_GAMEWON);
  }
  
  public void addMechstatUnitGamePlayed(String name) {
	  hh.addMechstat(name, HistoryHandler.MECHSTAT_TYPE_GAMEPLAYED);
  }
  
  public void addMechstatUnitDestroyed(String name) {
	  hh.addMechstat(name, HistoryHandler.MECHSTAT_TYPE_UNITDESTROYED);
  }
  
  public Connection getConnection() {
	  if (MySQLCon == null) {
		  MySQLCon = new MWmysql();
	  } 
	  return MySQLCon.getCon();
  }
  
  public void returnConnection(Connection c) {
	  MySQLCon.returnCon(c);
	  }
  
  public Connection getBBConn() {
	  if (phpBBCon == null) {
		  phpBBCon = new PhpBBConnector();
		  phpBBCon.init();
	  }
	  return phpBBCon.con;
  }
  
  public mysqlHandler(){
    this.MySQLCon = new MWmysql();
    if(Boolean.parseBoolean(CampaignMain.cm.getServer().getConfigParam("MYSQL_SYNCHPHPBB"))) {
    	this.phpBBCon = new PhpBBConnector();
    	phpBBCon.init();
    }
    this.ph = new planetHandler();
    this.fh = new factoryHandler();
    this.pih = new PilotHandler();
    this.uh = new UnitHandler();
    this.fah = new FactionHandler();
    this.plh = new PlayerHandler();
  	this.hh = new HistoryHandler(getConnection());

    this.checkAndUpdateDB();
  }
}

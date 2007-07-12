package server.mwmysql;

import java.awt.Dimension;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeMap;

import common.AdvanceTerrain;
import common.CampaignData;
import common.Continent;
import common.Influences;
import common.PlanetEnvironment;
import common.util.Position;
import server.MMServ;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SPlanet;
import server.campaign.SUnitFactory;

public class planetHandler {
  Connection con = null;

  /**
   * TODO: Change this to use entirely PreparedStatements
   * 
   */
  
  public int countPlanets() {
	  int num = 0;
	  try {

	  ResultSet rs = null;
	  Statement stmt = con.createStatement();
	  String sql = "SELECT COUNT(*) as numplanets from planets";
	  
	  rs = stmt.executeQuery(sql);
	  rs.next();
	  num = rs.getInt("numplanets");

	  
	  } catch (SQLException e) {
		  MMServ.mmlog.dbLog("SQL Error in countPlanets: " + e.getMessage());
	  }
	  return num;
  }
  
  public void deletePlanet(int PlanetID) {
	  try {
		  Statement stmt = con.createStatement();
		  stmt.executeUpdate("DELETE from planets WHERE PlanetID = " + PlanetID);
		  stmt.executeUpdate("DELETE from planetenvironments WHERE PlanetID = " + PlanetID);
		  stmt.executeUpdate("DELETE from planetfactories WHERE PlanetID = " + PlanetID);
		  stmt.executeUpdate("DELETE from planetflags WHERE PlanetID = " + PlanetID);
		  stmt.executeUpdate("DELETE from planetinfluences WHERE PlanetID = " + PlanetID);
	  } catch (SQLException e) {
		  MMServ.mmlog.dbLog("SQL Error in deletePlanet: " + e.getMessage());
	  }
	  
  }

  @SuppressWarnings("unchecked")
public void loadPlanets(CampaignData data) {
	
	  try {
		  ResultSet rs = null;

		  Statement stmt = con.createStatement();
		  String sql = "SELECT * from planets";
		  
		  rs = stmt.executeQuery(sql);
		  while(rs.next()) {
			  SPlanet p = new SPlanet();

			  p.setCompProduction(rs.getInt("pCompProd"));
 		      p.setPosition(new Position(rs.getFloat("pXpos"), rs.getFloat("pYpos")));
			  p.setDescription(rs.getString("pDesc"));
			  p.setBaysProvided(rs.getInt("pBays"));
			  p.setConquerable(Boolean.parseBoolean(rs.getString("pIsConquerable")));
			  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			  try {
				  p.setTimestamp(sdf.parse(rs.getString("pLastChanged")));
			    } catch (Exception ex) {
			    	MMServ.mmlog.errLog("The following exception is not critical, but will cause useless bandwidth usage: please fix!");
			    	MMServ.mmlog.errLog(ex);
			    	p.setTimestamp(new Date(0));
			    }

			  p.setId(rs.getInt("pMWID"));

			  p.setMapSize(new Dimension(rs.getInt("pMapSizeWidth"), rs.getInt("pMapSizeHeight")));
			  p.setBoardSize(new Dimension(rs.getInt("pBoardSizeWidth"), rs.getInt("pBoardSizeHeight")));
			  p.setTemp(new Dimension(rs.getInt("pTempWidth"), rs.getInt("pTempHeight")));
			  p.setGravity(rs.getFloat("pGravity"));
			  p.setVacuum(Boolean.parseBoolean(rs.getString("pVacuum")));
			  p.setNightChance(rs.getInt("pNightChance"));
			  p.setNightTempMod(rs.getInt("pNightTempMod"));
			  p.setMinPlanetOwnerShip(rs.getInt("pMinPlanetOwnership"));
			  p.setHomeWorld(Boolean.parseBoolean(rs.getString("pIsHomeworld")));
			  p.setOriginalOwner(rs.getString("pOriginalOwner"));
			  p.setConquestPoints(rs.getInt("pMaxConquestPoints"));
			  p.setName(rs.getString("pName"));
			  p.setDBID(rs.getInt("PlanetID"));
			  
			  // Add the vectors now
			  

			  // Influences
			  loadInfluences(p, data);
			  
			  // Environments
			  loadEnvironments(p, data);

			  
			  // Flags
			  loadPlanetFlags(p, data);

			  // Factories
			  CampaignMain.cm.MySQL.loadFactories(p);
					  			  
			  CampaignMain.cm.addPlanet(p);
			  p.setOwner(null, p.checkOwner(), false);
		  }


	  } catch (SQLException e) {
		  MMServ.mmlog.dbLog("SQL Error in loadPlanets: " + e.getMessage());
		}
  }
  
  @SuppressWarnings("unchecked")
public void loadInfluences(SPlanet p, CampaignData data) {
	  try {
		  ResultSet rs1 = null;
		  Statement stmt = con.createStatement();
		  
		  HashMap influence = new HashMap();
		  
		  rs1 = stmt.executeQuery("SELECT * from planetinfluences WHERE planetID = " + p.getDBID());

		  
		  while(rs1.next()) {
			  Integer HouseInf = new Integer(rs1.getInt("influence"));
			  String HouseName = rs1.getString("FactionName");
			  SHouse h = (SHouse)data.getHouseByName(HouseName);
			  if(h!= null){
			  influence.put(new Integer(h.getId()), HouseInf);

			  } else
				  MMServ.mmlog.errLog("House not found: " + HouseName);
		  }
		  p.setInfluence(new Influences(influence));
	  } catch (SQLException e) {
		  MMServ.mmlog.dbLog("SQL Error in loadInfluences: " + e.getMessage());
	  }
  }

  public void loadPlanetFlags(SPlanet p, CampaignData data) {
	  try {
		  ResultSet rs2 = null;
		  Statement stmt = con.createStatement();
		  
		  TreeMap<String, String> map = new TreeMap<String, String>();
			 
		  rs2 = stmt.executeQuery("SELECT * from planetflags WHERE planetID = " + p.getDBID());
		  while(rs2.next()) {
			  String key = rs2.getString("PlanetFlag");
			  if ( CampaignMain.cm.getData().getPlanetOpFlags().containsKey(key))
				  map.put(key, CampaignMain.cm.getData().getPlanetOpFlags().get(key));
		  }
		  p.setPlanetFlags(map);
	  } catch (SQLException e) {
		  MMServ.mmlog.dbLog("SQL Error in loadPlanetFlags: " + e.getMessage());
	  }
  }
  
  public void loadEnvironments(SPlanet p, CampaignData data) {
	  try {
	  
	  ResultSet rs3 = null;
	  Statement stmt = con.createStatement();
	  
	  rs3 = stmt.executeQuery("SELECT * from planetenvironments WHERE PlanetID = " + p.getDBID());
	  
	  while(rs3.next()) {
		  int size = rs3.getInt("ContinentSize");
	//	  String terrain = rs3.getString("TerrainData");
		  PlanetEnvironment planetEnvironment = null;
		  int terrainNumber = 0;
		  
		  try {
/**
 * This is returning a string.  We need to figure out what the hell.
 * 
 */
			  terrainNumber = rs3.getInt("TerrainData");
			  planetEnvironment = data.getTerrain(terrainNumber);
		  } catch (Exception ex) {
			  planetEnvironment = data.getTerrain(terrainNumber);
		  }
		  Continent PE = new Continent(size, planetEnvironment);
		  if(CampaignMain.cm.getBooleanConfig("UseStaticMaps")) {
			  AdvanceTerrain aTerrain = new AdvanceTerrain();
			  
			  String tempHolder  = rs3.getString("AdvanceTerrainData");
			  if (tempHolder.length() > 0 ) {
				 StringTokenizer ST = new StringTokenizer(tempHolder, "$");
				 aTerrain.setDisplayName(ST.nextToken());
				 aTerrain.setXSize(Integer.parseInt(ST.nextToken()));
				 aTerrain.setYSize(Integer.parseInt(ST.nextToken()));
				 aTerrain.setStaticMap(Boolean.parseBoolean(ST.nextToken()));
				 aTerrain.setXBoardSize(Integer.parseInt(ST.nextToken()));
				 aTerrain.setYBoardSize(Integer.parseInt(ST.nextToken()));
				 aTerrain.setLowTemp(Integer.parseInt(ST.nextToken()));
				 aTerrain.setHighTemp(Integer.parseInt(ST.nextToken()));
				 aTerrain.setGravity(Double.parseDouble(ST.nextToken()));
				 aTerrain.setVacuum(Boolean.parseBoolean(ST.nextToken()));
				 aTerrain.setNightChance(Integer.parseInt(ST.nextToken()));
				 aTerrain.setNightTempMod(Integer.parseInt(ST.nextToken()));
				 aTerrain.setStaticMapName(ST.nextToken());
			  } else {
				  aTerrain = new AdvanceTerrain(tempHolder);
			  }
			  p.getAdvanceTerrain().put(new Integer(PE.getEnvironment().getId()), aTerrain);
		  }
		  p.getEnvironments().add(PE);
	  } } catch (SQLException e) {
		  MMServ.mmlog.dbLog("SQL Error in loadEnvironments: " + e.getMessage());
	  }
  }
  
  public void savePlanet(SPlanet planet)
  {
	  try {
		  if (planet.getDBID()==0) {
			  // It's a new planet, INSERT it.
			  Statement stmt = con.createStatement();
			  ResultSet rs = null;
			  StringBuffer sql = new StringBuffer();
			  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			  PreparedStatement ps;
			  
			  sql.append("INSERT into planets set pCompProd = ?, "); 
			  sql.append("pXpos = ?, ");
			  sql.append("pYpos = ?, ");
			  sql.append("pDesc = ?, ");
			  sql.append("pBays = ?, ");
			  sql.append("pIsConquerable = ?, ");
			  sql.append("pLastChanged = ?, ");
			  sql.append("pMWID = ?, ");
			  sql.append("pMapSizeWidth = ?, ");
			  sql.append("pMapSizeHeight = ?, ");
			  sql.append("pBoardSizeWidth = ?, ");
			  sql.append("pBoardSizeHeight = ?, ");
			  sql.append("pTempWidth = ?, ");
			  sql.append("pTempHeight = ?, ");
			  sql.append("pGravity = ?, ");
			  sql.append("pVacuum = ?, ");
			  sql.append("pNightChance = ?, ");
			  sql.append("pNightTempMod = ?, ");
			  sql.append("pMinPlanetOwnership = ?, ");
			  sql.append("pIsHomeworld = ?, ");
			  sql.append("pOriginalOwner = ?, ");
			  sql.append("pMaxConquestPoints = ?, ");
			  sql.append("pName = ?");
			  
			  ps=con.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
			  ps.setInt(1, planet.getCompProduction());
			  ps.setDouble(2, planet.getPosition().getX());
			  ps.setDouble(3, planet.getPosition().getY());
			  ps.setString(4, planet.getDescription());
			  ps.setInt(5, planet.getBaysProvided());
			  ps.setString(6, String.valueOf(planet.isConquerable()));
			  ps.setString(7, sdf.format(planet.getLastChanged()));
			  ps.setInt(8, planet.getId());
			  ps.setInt(9, planet.getMapSize().width);
			  ps.setInt(10, planet.getMapSize().height);
			  ps.setInt(11, planet.getBoardSize().width);
			  ps.setInt(12, planet.getBoardSize().height);
			  ps.setInt(13, planet.getTemp().width);
			  ps.setInt(14, planet.getTemp().height);
			  ps.setDouble(15, planet.getGravity());
			  ps.setString(16, String.valueOf(planet.isVacuum()));
			  ps.setInt(17, planet.getNightChance());
			  ps.setInt(18, planet.getNightTempMod());
			  ps.setInt(19, planet.getMinPlanetOwnerShip());
			  ps.setString(20, String.valueOf(planet.isHomeWorld()));
			  ps.setString(21, planet.getOriginalOwner());
			  ps.setInt(22, planet.getConquestPoints());
			  ps.setString(23, planet.getName());
			  
			  ps.executeUpdate();
			  
			  
			  rs=ps.getGeneratedKeys();
			  if(rs.next()){
				  int pid = rs.getInt(1);
				  planet.setDBID(pid);
				  
				  /**
				   * If it didn't get us an ID, there's not much point in doing the following:
				   * 
				   * Now, we need to save all the vectors:
				   * Factories
				   * Influence
				   * Environments
				   * planet flags
				   */
				  if(planet.getUnitFactories()!=null){
					  for (int i = 0; i < planet.getUnitFactories().size(); i++) {
						SUnitFactory MF = (SUnitFactory) planet.getUnitFactories().get(i);
						CampaignMain.cm.MySQL.saveFactory(MF);
					  }
				  }
				  // Save Influences
				  saveInfluences(planet);
				  
				  // Save Environments
				  
				  saveEnvironments(planet);
				  
				  // Save Planet Flags
				  if(planet.getPlanetFlags().size() > 0)
				    savePlanetFlags(planet);
				  
			  }
			  if(rs!=null)
				  rs.close();
			  if(stmt!=null)
				  stmt.close();
		  }
		  else {
			  // It's already in the database, UPDATE it
			  Statement stmt = con.createStatement();
			  StringBuffer sql = new StringBuffer();
			  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			  PreparedStatement ps;
			  
			  sql.append("UPDATE planets set pCompProd = ?, "); 
			  sql.append("pXpos = ?, ");
			  sql.append("pYpos = ?, ");
			  sql.append("pDesc = ?, ");
			  sql.append("pBays = ?, ");
			  sql.append("pIsConquerable = ?, ");
			  sql.append("pLastChanged = ?, ");
			  sql.append("pMWID = ?, ");
			  sql.append("pMapSizeWidth = ?, ");
			  sql.append("pMapSizeHeight = ?, ");
			  sql.append("pBoardSizeWidth = ?, ");
			  sql.append("pBoardSizeHeight = ?, ");
			  sql.append("pTempWidth = ?, ");
			  sql.append("pTempHeight = ?, ");
			  sql.append("pGravity = ?, ");
			  sql.append("pVacuum = ?, ");
			  sql.append("pNightChance = ?, ");
			  sql.append("pNightTempMod = ?, ");
			  sql.append("pMinPlanetOwnership = ?, ");
			  sql.append("pIsHomeworld = ?, ");
			  sql.append("pOriginalOwner = ?, ");
			  sql.append("pMaxConquestPoints = ?, ");
			  sql.append("pName = ? ");
			  sql.append("WHERE PlanetID = ?");
			  
			  ps=con.prepareStatement(sql.toString());
			  
			  ps.setInt(1, planet.getCompProduction());
			  ps.setDouble(2, planet.getPosition().getX());
			  ps.setDouble(3, planet.getPosition().getY());
			  ps.setString(4, planet.getDescription());
			  ps.setInt(5, planet.getBaysProvided());
			  ps.setBoolean(6, planet.isConquerable());
			  ps.setString(7, sdf.format(planet.getLastChanged()));
			  ps.setInt(8, planet.getId());
			  ps.setInt(9, planet.getMapSize().width);
			  ps.setInt(10, planet.getMapSize().height);
			  ps.setInt(11, planet.getBoardSize().width);
			  ps.setInt(12, planet.getBoardSize().height);
			  ps.setInt(13, planet.getTemp().width);
			  ps.setInt(14, planet.getTemp().height);
			  ps.setDouble(15, planet.getGravity());
			  ps.setBoolean(16, planet.isVacuum());
			  ps.setInt(17, planet.getNightChance());
			  ps.setInt(18, planet.getNightTempMod());
			  ps.setInt(19, planet.getMinPlanetOwnerShip());
			  ps.setBoolean(20, planet.isHomeWorld());
			  ps.setString(21, planet.getOriginalOwner());
			  ps.setInt(22, planet.getConquestPoints());
			  ps.setString(23, planet.getName());
			  ps.setInt(24, planet.getDBID());
			  
			  ps.executeUpdate();
				  
				  /**
				   * Now, we need to save all the vectors:
				   * Factories
				   * Influence
				   * Environments
				   * planet flags
				   */
				  if(planet.getUnitFactories()!=null){
					  for (int i = 0; i < planet.getUnitFactories().size(); i++) {
						SUnitFactory MF = (SUnitFactory) planet.getUnitFactories().get(i);
						CampaignMain.cm.MySQL.saveFactory(MF);
					  }
				  }
				  // Save Influences
				  saveInfluences(planet);
				  
				  // Save Environments
				  
				  saveEnvironments(planet);
				  
				  // Save Planet Flags
				  if(planet.getPlanetFlags().size() > 0)
				    savePlanetFlags(planet);
				  
			  if(stmt!=null)
				  stmt.close();
		  }
	  }
	  catch(SQLException e) {
		  MMServ.mmlog.dbLog(e.getMessage());
	  }
	  
  }
  
  public void saveEnvironments(SPlanet p) {
	  Statement stmt = null;
	  StringBuffer sql = new StringBuffer();
	  
	  try {
		stmt = con.createStatement();
		sql.append("DELETE from planetenvironments WHERE PlanetID = " + p.getDBID());
		stmt.executeUpdate(sql.toString());
		Iterator it = p.getEnvironments().iterator();
		while(it.hasNext()){
			Continent t = (Continent) it.next();
			int size = t.getSize();
			StringBuffer atData = new StringBuffer();
			StringBuffer tName = new StringBuffer();
			
			tName.append(t.getEnvironment().getName());
			int tId = t.getEnvironment().getId();
			
			if(CampaignMain.cm.getBooleanConfig("UseStaticMaps")){
				/**
				 *  This is a hack.  Right now, to get this working,
				 *  I'm going to just store the terrain.fromString()
				 *  string.  I'll come back and break this out later.
				 */
				AdvanceTerrain aTerrain = p.getAdvanceTerrain().get(new Integer(t.getEnvironment().getId()));
				if( aTerrain == null )
					aTerrain = new AdvanceTerrain();
				if ( aTerrain.getDisplayName().length() <= 1)
					aTerrain.setDisplayName(t.getEnvironment().getName());
				atData.append(aTerrain.toString());
			}
			sql.setLength(0);
			sql.append("INSERT into planetenvironments set ");
			sql.append("PlanetID = " + p.getDBID() + ", ");
			sql.append("ContinentSize = " + size + ", ");
			sql.append("TerrainData = '" + tId + "'");
			if(CampaignMain.cm.getBooleanConfig("UseStaticMaps"))
			  sql.append(", AdvanceTerrainData = '" + atData.toString() + "'");
			stmt.executeUpdate(sql.toString());
		}
		stmt.close();
	  } catch (SQLException e) {
		  MMServ.mmlog.dbLog("SQL Error in saveEnvironments: " + e.getMessage());
	  }
  }
  
  public void savePlanetFlags(SPlanet p) {
	  Statement stmt = null;

	  StringBuffer sql = new StringBuffer();
	  
	  try {
		  stmt = con.createStatement();
		  
		  sql.append("DELETE from planetflags WHERE PlanetID = " + p.getDBID());
		  stmt.executeUpdate(sql.toString());
		  
		  for (String key : p.getPlanetFlags().keySet() ) {
			  sql.setLength(0);
			  sql.append("INSERT into planetflags set PlanetID = " + p.getDBID() + "PlanetFlag = '" + key + "'");
			  stmt.executeUpdate(sql.toString());
		  	}
		  stmt.close();
		  
		  }
	  catch (SQLException e) {
		  MMServ.mmlog.dbLog("SQL Error in savePlanetFlags: " + e.getMessage());
	  }
  }


  public void saveInfluences(SPlanet p) {
	  Iterator it = p.getInfluence().getHouses().iterator();
	  Statement stmt = null;	  
	  ResultSet rs = null;
	  StringBuffer sql = new StringBuffer();
	  int pid = p.getDBID();
	  
	  try {
		  stmt = con.createStatement();
		  sql.append("DELETE from planetinfluences WHERE PlanetID = " + pid);
		  stmt.executeUpdate(sql.toString());
	  } catch (SQLException e) {
		  MMServ.mmlog.dbLog("SQL Error in saveInfluences: " + e.getMessage());
	  }
	  
	  while (it.hasNext()) {
		  SHouse next = (SHouse) it.next();
		  String iName = next.getName().replace("'", "\'");
		  
		  int iInf = p.getInfluence().getInfluence(next.getId());
		  try {
			sql.setLength(0);
			sql.append("INSERT into planetinfluences set PlanetID = " + pid + ", ");
			sql.append("FactionName = '" + iName + "', ");
			sql.append("Influence = " + iInf);
			stmt.executeUpdate(sql.toString());
			
		  }
		  catch (SQLException e) {
		  MMServ.mmlog.dbLog("SQL ERROR in saveInluences: " + e.getMessage());
		  }
		try {
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
		} catch (SQLException e) {
			MMServ.mmlog.dbLog("Error closing resources in saveInfluences: " + e.getMessage());
		}
	  }
  }
   
  // CONSTRUCTOR
  public planetHandler(Connection c)
    {
    this.con = c;
    }
}

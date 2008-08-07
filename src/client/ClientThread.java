/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 * Original author Helge Richter (McWizard)
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

package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import java.io.File;

import client.campaign.CUnit;
import client.campaign.CArmy;
import client.util.SerializeEntity;

import common.AdvancedTerrain;
import common.CampaignData;
import common.MegaMekPilotOption;
import common.MMGame;
import common.PlanetEnvironment;
import common.Unit;
import common.campaign.Buildings;
import common.campaign.pilot.skills.PilotSkill;
import common.util.UnitUtils;

import megamek.client.Client;
import megamek.client.bot.BotClient;
import megamek.client.bot.TestBot;
import megamek.client.bot.ui.AWT.BotGUI;
import megamek.client.ui.AWT.ClientGUI;
import megamek.common.Board;
import megamek.common.Entity;
import megamek.common.IGame.Phase;
import megamek.common.event.GameBoardChangeEvent;
import megamek.common.event.GameBoardNewEvent;
import megamek.common.event.GameEndEvent;
import megamek.common.event.GameEntityChangeEvent;
import megamek.common.event.GameEntityNewEvent;
import megamek.common.event.GameEntityNewOffboardEvent;
import megamek.common.event.GameEntityRemoveEvent;
import megamek.common.event.GameListener;
import megamek.common.event.GameEvent;
import megamek.common.event.GameMapQueryEvent;
import megamek.common.event.GameNewActionEvent;
import megamek.common.event.GamePhaseChangeEvent;
import megamek.common.event.GamePlayerChangeEvent;
import megamek.common.event.GamePlayerChatEvent;
import megamek.common.event.GamePlayerConnectedEvent;
import megamek.common.event.GamePlayerDisconnectedEvent;
import megamek.common.event.GameReportEvent;
import megamek.common.event.GameSettingsChangeEvent;
import megamek.common.event.GameTurnChangeEvent;
import megamek.common.MapSettings;
import megamek.common.Mech;
import megamek.common.options.GameOptions;
import megamek.common.options.IBasicOption;
import megamek.common.options.Option;
import megamek.common.options.IOption;
import megamek.common.options.IOptionGroup;
import megamek.common.Building;
import megamek.common.Coords;
import megamek.common.IGame;
import megamek.common.IOffBoardDirections;
import megamek.common.Pilot;
import megamek.common.PlanetaryConditions;
import megamek.common.Player;
import megamek.common.MechWarrior;
import megamek.common.util.BuildingTemplate;
import megamek.client.CloseClientListener;
import megamek.common.preference.IClientPreferences;
import megamek.common.preference.PreferenceManager;

class ClientThread extends Thread implements GameListener, CloseClientListener {

    // VARIABLES
    private String myname;
    private String serverip;
    private String serverName;
    private int serverport;
    private MWClient mwclient;
    private Client client;
    private ClientGUI awtGui;
    private megamek.client.ui.swing.ClientGUI swingGui;
    private boolean awtGUI = false;

    private int turn = 0;
    private ArrayList<Unit> mechs = new ArrayList<Unit>();
    private ArrayList<CUnit> autoarmy = new ArrayList<CUnit>();// from server's
    // auto army
    CArmy army = null;
    BotClient bot = null;
    private Phase currentPhase = IGame.Phase.PHASE_DEPLOYMENT;

    final int N = 0;
    final int NE = 1;
    final int SE = 2;
    final int S = 3;
    final int SW = 4;
    final int NW = 5;

    // CONSTRUCTOR
    public ClientThread(String name, String servername, String ip, int port, MWClient mwclient, ArrayList<Unit> mechs, ArrayList<CUnit> autoarmy) {
        myname = name.trim();
        serverName = servername;
        serverip = ip;
        serverport = port;
        this.mwclient = mwclient;
        this.mechs = mechs;
        this.autoarmy = autoarmy;
        if (serverip.indexOf("127.0.0.1") != -1) {
            this.serverip = "127.0.0.1";
        }
    }

    // METHODS
    public int getTurn() {
        return turn;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void run() {
        boolean playerUpdate = false;
        boolean nightGame = false;
        awtGUI = mwclient.getConfig().isParam("USEAWTINTERFACE");
        CArmy currA = mwclient.getPlayer().getLockedArmy();
        client = new Client(myname, serverip, serverport);
        client.game.addGameListener(this);
        client.addCloseClientListener(this);
        if (awtGUI) {
            awtGui = new ClientGUI(client);
            awtGui.initialize();
            swingGui = null;
        } else {
            awtGui = null;
            swingGui = new megamek.client.ui.swing.ClientGUI(client);
            swingGui.initialize();
        }

        if (mwclient.getGameOptions().size() < 1) {
            mwclient.setWaiting(true);

            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c RequestOperationSettings");
            while (mwclient.isWaiting()) {
                try {
                    mwclient.addToChat("Retrieving Operation Data Please Wait..");
                    Thread.sleep(1000);
                } catch (Exception ex) {

                }
            }
        }

        // client.game.getOptions().
        Vector<IBasicOption> xmlGameOptions = new Vector<IBasicOption>(1, 1);
        Vector<IOption> loadOptions = client.game.getOptions().loadOptions();

        // Load Defaults first.
        Enumeration<IOption> options = client.game.getOptions().getOptions();
        while (options.hasMoreElements()) {
            IOption option = (IOption) options.nextElement();
            switch (option.getType()) {
            case IOption.BOOLEAN:
                xmlGameOptions.add((new Option(new GameOptions(), option.getName(), (Boolean) option.getDefault())));
                break;
            case IOption.FLOAT:
                xmlGameOptions.add((new Option(new GameOptions(), option.getName(), (Float) option.getDefault())));
                break;
            case IOption.STRING:
                xmlGameOptions.add((new Option(new GameOptions(), option.getName(), (String) option.getDefault())));
                break;
            case IOption.INTEGER:
                xmlGameOptions.add((new Option(new GameOptions(), option.getName(), (Integer) option.getDefault())));
                break;
            case IOption.CHOICE:
                xmlGameOptions.add((new Option(new GameOptions(), option.getName(), (String) option.getDefault())));
                break;
            }
        }

        xmlGameOptions = sortAndShrinkGameOptions(xmlGameOptions, loadOptions, this.mwclient.getGameOptions());

        // Check for a night game and set nightGame Variable.
        // This needed to be done since it was possible that a slow connection
        // would
        // keep the client from getting an update from the server before the
        // entities
        // where added to the game.
        for (IBasicOption option : xmlGameOptions) {
            if ((option.getName().equalsIgnoreCase("night_battle") || option.getName().equalsIgnoreCase("dusk")) && option.getValue().toString().equalsIgnoreCase("true")) {
                nightGame = true;
                break;
            }
        }

        try {
            client.connect();
        } catch (Exception ex) {
            client = null;
            mwclient.showInfoWindow("Couldn't join this game!");
            CampaignData.mwlog.infoLog(serverip + " " + serverport);
            return;
        }
        // client.retrieveServerInfo();
        try {
            while (client.getLocalPlayer() == null) {
                sleep(50);
            }

            // if game is running, shouldn't do the following, so detect the
            // phase
            for (int i = 0; i < 1000 && client.game.getPhase() == IGame.Phase.PHASE_UNKNOWN; i++) {
                sleep(50);
            }

            // Lets start with the environment set first then do everything
            // else.
            if (this.mwclient.getCurrentEnvironment() != null) {
                // creates the playboard*/
                MapSettings mySettings = new MapSettings(mwclient.getMapSize().width, mwclient.getMapSize().height, 1, 1);
                // MapSettings mySettings = new MapSettings(16, 17, 2, 2);
                AdvancedTerrain aTerrain = this.mwclient.getCurrentAdvancedTerrain();

                if ((aTerrain != null) && aTerrain.isStaticMap()) {

                    mySettings = new MapSettings(aTerrain.getXSize(), aTerrain.getYSize(), aTerrain.getXBoardSize(), aTerrain.getYBoardSize());

                    // MMClient.mwClientLog.clientErrLog("Board x:
                    // "+myClient.getBoardSize().width+"Board y:
                    // "+myClient.getBoardSize().height+"Map x:
                    // "+myClient.getMapSize().width+"Map y:
                    // "+myClient.getMapSize().height);
                    ArrayList<String> boardvec = new ArrayList<String>();
                    if (aTerrain.getStaticMapName().toLowerCase().endsWith("surprise")) {
                        int maxBoards = aTerrain.getXBoardSize() * aTerrain.getYBoardSize();
                        for (int i = 0; i < maxBoards; i++) {
                            boardvec.add(MapSettings.BOARD_SURPRISE);
                        }

                        mySettings.setBoardsSelectedVector(boardvec);

                        if (aTerrain.getStaticMapName().indexOf("/") > -1) {
                            String folder = aTerrain.getStaticMapName().substring(0, aTerrain.getStaticMapName().lastIndexOf("/"));
                            mySettings.setBoardsAvailableVector(scanForBoards(aTerrain.getXSize(), aTerrain.getYSize(), folder));
                        } else if (aTerrain.getStaticMapName().indexOf("\\") > -1) {
                            String folder = aTerrain.getStaticMapName().substring(0, aTerrain.getStaticMapName().lastIndexOf("\\"));
                            mySettings.setBoardsAvailableVector(scanForBoards(aTerrain.getXSize(), aTerrain.getYSize(), folder));
                        } else
                            mySettings.setBoardsAvailableVector(scanForBoards(aTerrain.getXSize(), aTerrain.getYSize(), ""));
                    } else if (aTerrain.getStaticMapName().toLowerCase().endsWith("generated")) {
                        PlanetEnvironment env = this.mwclient.getCurrentEnvironment();
                        /* Set the map-gen values */
                        mySettings.setElevationParams(env.getHillyness(), env.getHillElevationRange(), env.getHillInvertProb());
                        mySettings.setWaterParams(env.getWaterMinSpots(), env.getWaterMaxSpots(), env.getWaterMinHexes(), env.getWaterMaxHexes(), env.getWaterDeepProb());
                        mySettings.setForestParams(env.getForestMinSpots(), env.getForestMaxSpots(), env.getForestMinHexes(), env.getForestMaxHexes(), env.getForestHeavyProb());
                        mySettings.setRoughParams(env.getRoughMinSpots(), env.getRoughMaxSpots(), env.getRoughMinHexes(), env.getRoughMaxHexes());
                        mySettings.setSwampParams(env.getSwampMinSpots(), env.getSwampMaxSpots(), env.getSwampMinHexes(), env.getSwampMaxHexes());
                        mySettings.setPavementParams(env.getPavementMinSpots(), env.getPavementMaxSpots(), env.getPavementMinHexes(), env.getPavementMaxHexes());
                        mySettings.setIceParams(env.getIceMinSpots(), env.getIceMaxSpots(), env.getIceMinHexes(), env.getIceMaxHexes());
                        mySettings.setRubbleParams(env.getRubbleMinSpots(), env.getRubbleMaxSpots(), env.getRubbleMinHexes(), env.getRubbleMaxHexes());
                        mySettings.setFortifiedParams(env.getFortifiedMinSpots(), env.getFortifiedMaxSpots(), env.getFortifiedMinHexes(), env.getFortifiedMaxHexes());
                        mySettings.setSpecialFX(env.getFxMod(), env.getProbForestFire(), env.getProbFreeze(), env.getProbFlood(), env.getProbDrought());
                        mySettings.setRiverParam(env.getRiverProb());
                        mySettings.setCliffParam(env.getCliffProb());
                        mySettings.setRoadParam(env.getRoadProb());
                        mySettings.setCraterParam(env.getCraterProb(), env.getCraterMinNum(), env.getCraterMaxNum(), env.getCraterMinRadius(), env.getCraterMaxRadius());
                        mySettings.setAlgorithmToUse(env.getAlgorithm());
                        mySettings.setInvertNegativeTerrain(env.getInvertNegativeTerrain());
                        mySettings.setMountainParams(env.getMountPeaks(), env.getMountWidthMin(), env.getMountWidthMax(), env.getMountHeightMin(), env.getMountHeightMax(), env.getMountStyle());

                        if (env.getTheme().length() > 1)
                            mySettings.setTheme(env.getTheme());
                        else
                            mySettings.setTheme("");

                        int maxBoards = aTerrain.getXBoardSize() * aTerrain.getYBoardSize();
                        for (int i = 0; i < maxBoards; i++) {
                            boardvec.add(MapSettings.BOARD_GENERATED);
                        }

                        mySettings.setBoardsSelectedVector(boardvec);
                        if (aTerrain.getStaticMapName().indexOf("/") > -1) {
                            String folder = aTerrain.getStaticMapName().substring(0, aTerrain.getStaticMapName().lastIndexOf("/"));
                            mySettings.setBoardsAvailableVector(scanForBoards(aTerrain.getXSize(), aTerrain.getYSize(), folder));
                        } else if (aTerrain.getStaticMapName().indexOf("\\") > -1) {
                            String folder = aTerrain.getStaticMapName().substring(0, aTerrain.getStaticMapName().lastIndexOf("\\"));
                            mySettings.setBoardsAvailableVector(scanForBoards(aTerrain.getXSize(), aTerrain.getYSize(), folder));
                        } else
                            mySettings.setBoardsAvailableVector(scanForBoards(aTerrain.getXSize(), aTerrain.getYSize(), ""));

                        if (mwclient.getBuildingTemplate() != null && mwclient.getBuildingTemplate().getTotalBuildings() > 0) {
                            ArrayList<BuildingTemplate> buildingList = generateRandomBuildings(mySettings, mwclient.getBuildingTemplate());
                            mySettings.setBoardBuildings(buildingList);
                        } else if (!env.getCityType().equalsIgnoreCase("NONE")) {
                            mySettings.setRoadParam(0);
                            mySettings.setCityParams(env.getRoads(), env.getCityType(), env.getMinCF(), env.getMaxCF(), env.getMinFloors(), env.getMaxFloors(), env.getCityDensity(), env.getTownSize());
                        }
                    } else {
                        boardvec.add(aTerrain.getStaticMapName());
                        mySettings.setBoardsSelectedVector(boardvec);
                    }
                    
                    PlanetaryConditions planetCondition = new PlanetaryConditions();
                    
                    planetCondition.setGravity((float)aTerrain.getGravity());
                    planetCondition.setTemperature(aTerrain.getTemperature());
                    planetCondition.setAtmosphere(aTerrain.getAtmosphere());
                    planetCondition.setEMI(aTerrain.hasEMI());
                    planetCondition.setFog(aTerrain.getFog());
                    planetCondition.setLight(aTerrain.getLightConditions());
                    planetCondition.setShiftingWindDirection(aTerrain.hasShifitingWindDirection());
                    planetCondition.setShiftingWindStrength(aTerrain.hasShifitingWindStrength());
                    planetCondition.setTerrainAffected(aTerrain.isTerrainAffected());
                    planetCondition.setWeather(aTerrain.getWeatherConditions());
                    planetCondition.setWindDirection(aTerrain.getWindDirection());
                    planetCondition.setWindStrength(aTerrain.getWindStrength());
                    
                    client.sendPlanetaryConditions(planetCondition);
                    
                    mySettings.setMedium(mwclient.getMapMedium());
                    client.sendMapSettings(mySettings);
                } else {
                    PlanetEnvironment env = this.mwclient.getCurrentEnvironment();
                    /* Set the map-gen values */
                    mySettings.setElevationParams(env.getHillyness(), env.getHillElevationRange(), env.getHillInvertProb());
                    mySettings.setWaterParams(env.getWaterMinSpots(), env.getWaterMaxSpots(), env.getWaterMinHexes(), env.getWaterMaxHexes(), env.getWaterDeepProb());
                    mySettings.setForestParams(env.getForestMinSpots(), env.getForestMaxSpots(), env.getForestMinHexes(), env.getForestMaxHexes(), env.getForestHeavyProb());
                    mySettings.setRoughParams(env.getRoughMinSpots(), env.getRoughMaxSpots(), env.getRoughMinHexes(), env.getRoughMaxHexes());
                    mySettings.setSwampParams(env.getSwampMinSpots(), env.getSwampMaxSpots(), env.getSwampMinHexes(), env.getSwampMaxHexes());
                    mySettings.setPavementParams(env.getPavementMinSpots(), env.getPavementMaxSpots(), env.getPavementMinHexes(), env.getPavementMaxHexes());
                    mySettings.setIceParams(env.getIceMinSpots(), env.getIceMaxSpots(), env.getIceMinHexes(), env.getIceMaxHexes());
                    mySettings.setRubbleParams(env.getRubbleMinSpots(), env.getRubbleMaxSpots(), env.getRubbleMinHexes(), env.getRubbleMaxHexes());
                    mySettings.setFortifiedParams(env.getFortifiedMinSpots(), env.getFortifiedMaxSpots(), env.getFortifiedMinHexes(), env.getFortifiedMaxHexes());
                    mySettings.setSpecialFX(env.getFxMod(), env.getProbForestFire(), env.getProbFreeze(), env.getProbFlood(), env.getProbDrought());
                    mySettings.setRiverParam(env.getRiverProb());
                    mySettings.setCliffParam(env.getCliffProb());
                    mySettings.setRoadParam(env.getRoadProb());
                    mySettings.setCraterParam(env.getCraterProb(), env.getCraterMinNum(), env.getCraterMaxNum(), env.getCraterMinRadius(), env.getCraterMaxRadius());
                    mySettings.setAlgorithmToUse(env.getAlgorithm());
                    mySettings.setInvertNegativeTerrain(env.getInvertNegativeTerrain());
                    mySettings.setMountainParams(env.getMountPeaks(), env.getMountWidthMin(), env.getMountWidthMax(), env.getMountHeightMin(), env.getMountHeightMax(), env.getMountStyle());
                    if (env.getTheme().length() > 1)
                        mySettings.setTheme(env.getTheme());
                    else
                        mySettings.setTheme("");

                    /* select the map */
                    ArrayList<String> boardvec = new ArrayList<String>();
                    boardvec.add(MapSettings.BOARD_GENERATED);
                    mySettings.setBoardsSelectedVector(boardvec);

                    if (mwclient.getBuildingTemplate() != null && mwclient.getBuildingTemplate().getTotalBuildings() > 0) {
                        ArrayList<BuildingTemplate> buildingList = generateRandomBuildings(mySettings, mwclient.getBuildingTemplate());
                        mySettings.setBoardBuildings(buildingList);
                    } else if (!env.getCityType().equalsIgnoreCase("NONE")) {
                        mySettings.setRoadParam(0);
                        mySettings.setCityParams(env.getRoads(), env.getCityType(), env.getMinCF(), env.getMaxCF(), env.getMinFloors(), env.getMaxFloors(), env.getCityDensity(), env.getTownSize());
                    }

                    mySettings.setMedium(mwclient.getMapMedium());
                    /* sent to server */
                    client.sendMapSettings(mySettings);
                }

            }

            /*
             * Add bots, if being used in this game.
             */
            if (mwclient.isUsingBots()) {
                String name = "War Bot" + client.getLocalPlayer().getId();
                bot = new TestBot(name, client.getHost(), client.getPort());
                bot.game.addGameListener(new BotGUI(bot));
                try {
                    bot.connect();
                    sleep(125);
                    while (bot.getLocalPlayer() == null) {
                        sleep(50);
                    }
                    // if game is running, shouldn't do the following, so detect
                    // the phase
                    for (int i = 0; i < 1000 && bot.game.getPhase() == IGame.Phase.PHASE_UNKNOWN; i++) {
                        sleep(50);
                    }
                } catch (Exception ex) {
                    CampaignData.mwlog.errLog("Bot Error!");
                    CampaignData.mwlog.errLog(ex);
                }
                bot.retrieveServerInfo();
                sleep(125);

                if (awtGUI)
                    awtGui.getBots().put(name, bot);
                else
                    swingGui.getBots().put(name, bot);

                if (mwclient.isBotsOnSameTeam())
                    bot.getLocalPlayer().setTeam(5);
                Random r = new Random();

                bot.getLocalPlayer().setStartingPos(r.nextInt(11));
                bot.sendPlayerInfo();
                sleep(125);
            }

            if ((client.game != null && client.game.getPhase() == IGame.Phase.PHASE_LOUNGE)) {

                if (this.mechs.size() > 0 && xmlGameOptions.size() > 0) {
                    /*
                     * Vector<IBasicOption> tempVector = new Vector<IBasicOption>(10,1); while ( xmlGameOptions.size() > 0 ){ tempVector.clear(); int count = Math.min(10, xmlGameOptions.size()); for (; count > 0 ; count-- ) tempVector.add(xmlGameOptions.remove(0)); client.sendGameOptions("",tempVector); sleep(150); }
                     */
                    client.sendGameOptions("", xmlGameOptions);
                }

                IClientPreferences cs = PreferenceManager.getClientPreferences();
                cs.setStampFilenames(Boolean.parseBoolean(mwclient.getserverConfigs("MMTimeStampLogFile")));
                cs.setShowUnitId(Boolean.parseBoolean(mwclient.getserverConfigs("MMShowUnitId")));
                cs.setKeepGameLog(Boolean.parseBoolean(mwclient.getserverConfigs("MMKeepGameLog")));
                cs.setGameLogFilename(mwclient.getserverConfigs("MMGameLogName"));
                if (mwclient.getConfig().getParam("UNITCAMO").length() > 0) {
                    client.getLocalPlayer().setCamoCategory(Player.ROOT_CAMO);
                    client.getLocalPlayer().setCamoFileName(mwclient.getConfig().getParam("UNITCAMO"));
                    playerUpdate = true;
                }

                if (bot != null) {
                    bot.getLocalPlayer().setNbrMFConventional(mwclient.getPlayer().getConventionalMinesAllowed());
                    bot.getLocalPlayer().setNbrMFVibra(mwclient.getPlayer().getVibraMinesAllowed());
                } else {
                    client.getLocalPlayer().setNbrMFConventional(mwclient.getPlayer().getConventionalMinesAllowed());
                    client.getLocalPlayer().setNbrMFVibra(mwclient.getPlayer().getVibraMinesAllowed());
                }

                for (Iterator<Unit> i = this.mechs.iterator(); i.hasNext();) {
                    // Get the Mek
                    CUnit mek = (CUnit) i.next();
                    // Get the Entity
                    Entity entity = mek.getEntity();
                    // Set the TempID for autoreporting
                    entity.setExternalId(mek.getId());
                    // entity.setId(mek.getId());
                    // Set the owner
                    entity.setOwner(client.getLocalPlayer());
                    // Set if unit is a commander in this army.
                    entity.setCommander(currA.isCommander(mek.getId()));

                    // if not a night game no reason to have the slites set.
                    if (!nightGame) {
                        entity.setSpotlight(false);
                        entity.setSpotlightState(false);
                    } else {
                        entity.setSpotlight(true);
                        entity.setSpotlightState(true);
                    }

                    // Set the correct home edge for off board units
                    if (entity.isOffBoard()) {
                        int direction = IOffBoardDirections.NORTH;
                        switch (mwclient.getPlayerStartingEdge()) {
                        case 4:
                        case 14:
                            direction = IOffBoardDirections.EAST;
                            break;
                        case 5:
                        case 6:
                        case 7:
                        case 15:
                        case 16:
                        case 17:
                            direction = IOffBoardDirections.SOUTH;
                            break;
                        case 8:
                        case 18:
                            direction = IOffBoardDirections.WEST;
                            break;
                        default:
                            direction = IOffBoardDirections.NORTH;
                            break;
                        }
                        entity.setOffBoard(entity.getOffBoardDistance(), direction);
                    }

                    // Add Pilot to entity
                    entity.setCrew(createEntityPilot(mek));
                    // Add Mek to game
                    client.sendAddEntity(entity);
                    // Wait a few secs to not overuse bandwith
                    sleep(125);
                }

                /*
                 * Army mechs already loaded (see previous for loop). Now try to load the artillery units generated by the server (see AutoArmy.java in the server.campaign pacakage for generation details).
                 */
                Iterator<CUnit> autoIt = this.autoarmy.iterator();
                while (autoIt.hasNext()) {

                    // get the unit
                    CUnit autoUnit = autoIt.next();

                    // get the entity
                    Entity entity = autoUnit.getEntity();

                    // Had issues with Id's so we are now setting them.
                    // entity.setId(autoUnit.getId());
                    entity.setExternalId(autoUnit.getId());

                    // Set the owner
                    if (bot != null)
                        entity.setOwner(bot.getLocalPlayer());
                    else
                        entity.setOwner(client.getLocalPlayer());

                    if (entity.getCrew().getName().equalsIgnoreCase("Unnamed") || entity.getCrew().getName().equalsIgnoreCase("vacant")) {
                        // set the pilot
                        Pilot pilot = new Pilot("AutoArtillery", 4, 5);
                        entity.setCrew(pilot);
                    } else {
                        entity.setCrew(createEntityPilot(autoUnit));
                    }

                    // CampaignData.mwlog.errLog(entity.getModel()+"
                    // direction "+entity.getOffBoardDirection());
                    // add the unit to the game.
                    if (bot != null)
                        bot.sendAddEntity(entity);
                    else
                        client.sendAddEntity(entity);

                    // Wait a few secs to not overuse bandwith
                    sleep(125);
                }// end while(more autoarty)

                if (mwclient.getPlayerStartingEdge() != Buildings.EDGE_UNKNOWN) {
                    client.getLocalPlayer().setStartingPos(mwclient.getPlayerStartingEdge());
                    playerUpdate = true;
                }

                if (this.mechs.size() > 0) {
                    // check armies for C3Network mechs

                    synchronized (currA) {

                        if (currA.getC3Network().size() > 0) {
                            // Thread.sleep(125);
                            playerUpdate = true;
                            for (int slave : currA.getC3Network().keySet()) {
                                linkMegaMekC3Units(currA, slave, currA.getC3Network().get(slave));
                            }

                            if (awtGUI)
                                awtGui.chatlounge.refreshEntities();
                            else
                                swingGui.chatlounge.refreshEntities();
                        }
                    }
                }

                if (mwclient.getPlayer().getTeamNumber() > 0) {
                    client.getLocalPlayer().setTeam(mwclient.getPlayer().getTeamNumber());
                    playerUpdate = true;
                }

                if (playerUpdate) {
                    client.sendPlayerInfo();
                    if (bot != null)
                        bot.sendPlayerInfo();
                }

            }

        } catch (Exception e) {
            CampaignData.mwlog.errLog(e);
        }
    }

    /**
     * redundant code since MM does not always send a discon event.
     */
    public void gamePlayerStatusChange(GameEvent e) {
    }

    public void gameTurnChange(GameTurnChangeEvent e) {
        if (client != null) {
            if (this.getTurn() == 0 && (myname.equals(serverName) || serverName.startsWith("[Dedicated]")))
                mwclient.serverSend("SHS|" + serverName + "|Running");
            else if (client.game.getPhase() != currentPhase && client.game.getOptions().booleanOption("paranoid_autosave") && !client.getLocalPlayer().isObserver()) {
                sendServerGameUpdate();
                currentPhase = client.game.getPhase();
            }
            turn += 1;

        }
    }

    public void gamePhaseChange(GamePhaseChangeEvent e) {

        // String result = "";
        // String winnerName ="";
        String name = "";

        try {

            if (client.game.getPhase() == IGame.Phase.PHASE_VICTORY) {

                // Make sure the player is fully connected.
                while (client.getLocalPlayer() == null) {
                    sleep(50);
                }

                // clear out everything.
                mwclient.getPlayer().setConventionalMinesAllowed(0);
                mwclient.getPlayer().setVibraMinesAllowed(0);
                mwclient.setUsingBots(false);
                // clear out everything from this game
                mwclient.setEnvironment(null, null, 0);
                mwclient.setAdvancedTerrain(null);
                mwclient.setPlayerStartingEdge(Buildings.EDGE_UNKNOWN);
                mwclient.getGameOptions().clear();
                // get rid of any and all bots.

                if (awtGUI) {
                    for (Iterator<Client> i = awtGui.getBots().values().iterator(); i.hasNext();) {
                        i.next().die();
                    }
                    awtGui.getBots().clear();
                } else {
                    for (Iterator<Client> i = swingGui.getBots().values().iterator(); i.hasNext();) {
                        i.next().die();
                    }
                    swingGui.getBots().clear();
                }
                // observers need not report
                if (client.game.getAllEntitiesOwnedBy(client.getLocalPlayer()) < 1)
                    return;

                MMGame toUse = mwclient.getServers().get(serverName);
                mwclient.serverSend("SGR|" + toUse.getHostName());
                CampaignData.mwlog.infoLog("GAME END");

                if (mwclient.getPlayer().getName().equalsIgnoreCase(name)) {
                    if (toUse.getHostName().startsWith("[Dedicated]"))
                        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "mail " + toUse.getHostName() + ",checkrestartcount");
                }

                clearGameOptions();
                client.game.reset();
            }// end victory

            /*
             * Reporting phases show deaths - units that try to stand and blow their ammo, units that have ammo explode from head, etc. This is also an opportune time to correct isses with the gameRemoveEntity ISU's. Removals happen ASAP, even if the removal condition and final condition of the unit are not the same (ie - remove on Engine crits even when a CT core comes later in the round).
             */
            else if (client.game.getPhase() == IGame.Phase.PHASE_END_REPORT) {

                // observers need not report
                if (client.getLocalPlayer().isObserver())
                    return;

                sendServerGameUpdate();

                /*
                 * Wrecked entities include all those which were devastated, ejected, or removed but salvagable according to MegaMek. We want to check them for CT-cores. Enumeration en = client.game.getWreckedEntities(); while (en.hasMoreElements()) { Entity currEntity = (Entity)en.nextElement(); /* MM is now reporting post-attack IS instead of pre-attack IS in gameEntityRemove, so this check shouldn't be necessary anymore. //if (currEntity instanceof Mech || currEntity instanceof QuadMech) { // //if a mech-type, override grouping if its salvage and CT 0 // if (currEntity.getInternal(Mech.LOC_CT) <= 0) // mwclient.serverSend("IPU|" + this.serializeEntity(currEntity, false, true)); // //} if (currEntity instanceof MechWarrior) mwclient.serverSend("IPU|" + this.serializeEntity(currEntity, false, false)); } //constantly update the onfield warriors. en = client.game.getEntities(); while
                 * (en.hasMoreElements()) { Entity currEntity = (Entity)en.nextElement(); if ( currEntity.getOwner().getName().startsWith("War Bot")) continue; if (currEntity instanceof MechWarrior) mwclient.serverSend("IPU|" + this.serializeEntity(currEntity, false, false)); } /* This is probably extraneous - retreats should be properly handled in the movement phase and do not involve damage transferal which could lead to a final status different from the removal status. //en = client.game.getRetreatedEntities(); //while (en.hasMoreElements()) { // Entity currEntity = (Entity)en.nextElement(); // mwclient.serverSend("IPU|" + this.serializeEntity(currEntity, false)); //}
                 */
            }

        }// end try
        catch (Exception ex) {
            CampaignData.mwlog.errLog("Error reporting game!");
            CampaignData.mwlog.errLog(ex);
        }
    }

    /*
     * When an entity is removed from play, check the reason. If the unit is ejected, captured or devestated and the player is invovled in the game at hand, report the removal to the server. The server stores these reports in pilotTree and deathTree in order to auto-resolve games after a player disconnects. NOTE: This send the *first possible* removal condition, which means that a unit which is simultanously head killed and then CT cored will show as salvageable.
     */
    public void gameEntityRemove(GameEntityRemoveEvent e) {// only send if the
        // player is
        // actually involved
        // in the game

        if (client.getLocalPlayer().isObserver())
            return;

        // get the entity
        megamek.common.Entity removedE = e.getEntity();
        if (removedE.getOwner().getName().startsWith("War Bot"))
            return;

        String toSend = SerializeEntity.serializeEntity(removedE, true, false, mwclient.isUsingAdvanceRepairs());
        mwclient.serverSend("IPU|" + toSend);
    }

    public void gamePlayerConnected(GamePlayerConnectedEvent e) {
    }

    public void gamePlayerDisconnected(GamePlayerDisconnectedEvent e) {
    }

    public void gamePlayerChange(GamePlayerChangeEvent e) {
    }

    public void gamePlayerChat(GamePlayerChatEvent e) {
    }

    public void gameReport(GameReportEvent e) {
    }

    public void gameEnd(GameEndEvent e) {
    }

    public void gameBoardNew(GameBoardNewEvent e) {
    }

    public void gameBoardChanged(GameBoardChangeEvent e) {
    }

    public void gameSettingsChange(GameSettingsChangeEvent e) {
    }

    public void gameMapQuery(GameMapQueryEvent e) {
    }

    public void gameEntityNew(GameEntityNewEvent e) {
    }

    public void gameEntityNewOffboard(GameEntityNewOffboardEvent e) {
    }

    public void gameEntityChange(GameEntityChangeEvent e) {
    }

    public void gameNewAction(GameNewActionEvent e) {
    }

    /*
     * from megamek.client.CloseClientListener clientClosed() Thanks to MM for adding the listener. And to MMNet for the poorly documented code change.
     */
    public void clientClosed() {

        PreferenceManager.getInstance().save();

        if (bot != null) {
            bot.die();
            bot = null;
        }

        // client.die();
        client = null;// explicit null of the MM client. Wasn't/isn't being
        // GC'ed.
        mwclient.closingGame(serverName);
        System.gc();
    }

    /**
     * @author jtighe
     * @param army
     * @param slaveid
     * @param masterid
     *            This function goes through and makes sure the slave is linked to the master unit
     */
    public void linkMegaMekC3Units(CArmy army, Integer slaveid, Integer masterid) {
        Entity c3Unit = null;
        Entity c3Master = null;

        while (c3Unit == null || c3Master == null) {
            try {
                if (c3Unit == null)
                    c3Unit = client.game.getEntity(slaveid);

                if (c3Master == null)
                    c3Master = client.game.getEntity(masterid);

                sleep(10);// give the queue time to refresh
            } catch (Exception ex) {
                CampaignData.mwlog.errLog("Error in linkMegaMekC3Units");
                CampaignData.mwlog.errLog(ex);
            }
        }

        // catch for some funky stuff
        if (c3Unit == null || c3Master == null) {
            CampaignData.mwlog.errLog("Null Units c3Unit: " + c3Unit + " C3Master: " + c3Master);
            return;
        }

        try {
            CUnit masterUnit = (CUnit) army.getUnit(masterid.intValue());
            // CampaignData.mwlog.errLog("Master Unit:
            // "+masterUnit.getModelName());
            // CampaignData.mwlog.errLog("Slave Unit:
            // "+c3Unit.getModel());
            if (!masterUnit.hasC3SlavesLinkedTo(army) && masterUnit.hasBeenC3LinkedTo(army) && (masterUnit.getC3Level() == Unit.C3_MASTER || masterUnit.getC3Level() == Unit.C3_MMASTER)) {
                // CampaignData.mwlog.errLog("Unit:
                // "+c3Master.getModel()+" id: "+c3Master.getExternalId());
                if (c3Master.getC3MasterId() == Entity.NONE) {
                    c3Master.setShutDown(false);
                    c3Master.setC3Master(c3Master);
                    client.sendUpdateEntity(c3Master);
                }
                /*
                 * if ( c3Master.hasC3MM() ) CampaignData.mwlog.errLog("hasC3MM"); else CampaignData.mwlog.errLog("!hasC3MM");
                 */
            } else if (c3Master.getC3MasterId() != Entity.NONE) {
                c3Master.setShutDown(false);
                c3Master.setC3Master(Entity.NONE);
                client.sendUpdateEntity(c3Master);
            }
            // CampaignData.mwlog.errLog("c3Unit: "+c3Unit.getModel()+"
            // Master: "+c3Master.getModel());
            c3Unit.setShutDown(false);
            c3Unit.setC3Master(c3Master);
            // CampaignData.mwlog.errLog("c3Master Set to
            // "+c3Unit.getC3MasterId()+" "+c3Unit.getC3NetId());
            client.sendUpdateEntity(c3Unit);
        } catch (Exception ex) {
            CampaignData.mwlog.errLog(ex);
            CampaignData.mwlog.errLog("Error in setting up C3Network");
        }
    }

    /*
     * Taken form Megamek Code for use with MekWars The call was private and was needed. Thanks to Ben Mazur and all of the MM coders we hope for a long and happy relation ship. Torren.
     */

    public static Comparator<? super Object> stringComparator() {
        return new Comparator<Object>() {
            public int compare(Object o1, Object o2) {
                String s1 = ((String) o1).toLowerCase();
                String s2 = ((String) o2).toLowerCase();
                return s1.compareTo(s2);
            }
        };
    }

    /**
     * Scans the boards directory for map boards of the appropriate size and returns them.
     */
    private ArrayList<String> scanForBoards(int boardWidth, int boardHeight, String folder) {
        ArrayList<String> boards = new ArrayList<String>();
        // Board Board = client.game.getBoard();

        File boardDir = new File("data/boards/"+folder);

        // just a check...
        if (!boardDir.isDirectory()) {
            return boards;
        }

        // scan files
        String[] fileList = boardDir.list();
        Vector<String> tempList = new Vector<String>(1, 1);
        Comparator<? super String> sortComp = ClientThread.stringComparator();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].indexOf(".board") == -1) {
                continue;
            }
            
            String path = fileList[i];
            if ( folder.trim().length() > 0 )
                path = folder+"/"+fileList[i];
            
            if (Board.boardIsSize(path, boardWidth, boardHeight)) {
                tempList.addElement(path.substring(0, path.lastIndexOf(".board")));
            }
        }

        // if there are any boards, add these:
        if (tempList.size() > 0) {
            boards.add(MapSettings.BOARD_RANDOM);
            boards.add(MapSettings.BOARD_SURPRISE);
            boards.add(MapSettings.BOARD_GENERATED);
            Collections.sort(tempList, sortComp);
            for (int loop = 0; loop < tempList.size(); loop++) {
                boards.add(tempList.elementAt(loop));
            }
        } else {
            boards.add(MapSettings.BOARD_GENERATED);
        }

        return boards;
    }

    private ArrayList<BuildingTemplate> generateRandomBuildings(MapSettings mapSettings, Buildings buildingTemplate) {

        ArrayList<BuildingTemplate> buildingList = new ArrayList<BuildingTemplate>();
        ArrayList<String> buildingTypes = new ArrayList<String>();

        int width = mapSettings.getBoardWidth();
        int height = mapSettings.getBoardHeight();
        int minHeight = 0;
        int minWidth = 0;

        switch (buildingTemplate.getStartingEdge()) {
        case Buildings.NORTH:
            height = 5;
            minHeight = 1;
            break;
        case Buildings.SOUTH:
            if (height > 5)
                minHeight = height - 5;
            height = 5;
            break;
        case Buildings.EAST:
            if (width > 5)
                minWidth = width - 5;
            width = 5;
            break;
        case Buildings.WEST:
            width = 5;
            minWidth = 1;
            break;
        default:
            break;
        }

        StringTokenizer types = new StringTokenizer(buildingTemplate.getBuildingType(), ",");

        while (types.hasMoreTokens())
            buildingTypes.add(types.nextToken());

        int typeSize = buildingTypes.size();

        Random r = new Random();

        TreeSet<String> tempMap = new TreeSet<String>();
        Coords coord = new Coords();
        String stringCoord = "";

        for (int count = 0; count < buildingTemplate.getTotalBuildings(); count++) {
            int loops = 0;
            boolean CFx2 = false;
            ArrayList<Coords> coordList = new ArrayList<Coords>();
            do {
                if (loops++ > 100) {
                    CFx2 = true;
                    break;
                }

                int x = r.nextInt(width) + minWidth;
                int y = r.nextInt(height) + minHeight;

                if (x >= mapSettings.getBoardWidth())
                    x = mapSettings.getBoardWidth() - 2;
                else if (x <= 1)
                    x = 2;

                if (y >= mapSettings.getBoardHeight())
                    y = mapSettings.getBoardHeight() - 2;
                else if (y <= 1)
                    y = 2;

                coord = new Coords(x, y);

                stringCoord = x + "," + y;
            } while (tempMap.contains(stringCoord));

            tempMap.add(stringCoord);
            coordList.add(coord);

            int floors = buildingTemplate.getMaxFloors() - buildingTemplate.getMinFloors();

            if (floors <= 0)
                floors = buildingTemplate.getMinFloors();
            else
                floors = r.nextInt(floors) + buildingTemplate.getMinFloors();

            int totalCF = buildingTemplate.getMaxCF() - buildingTemplate.getMinCF();

            if (totalCF <= 0)
                totalCF = buildingTemplate.getMinCF();
            else
                totalCF = r.nextInt(totalCF) + buildingTemplate.getMinCF();

            if (CFx2)
                totalCF *= 2;

            int type = 1;
            try {
                if (typeSize == 1)
                    type = Integer.parseInt(buildingTypes.get(0));
                else
                    type = Integer.parseInt(buildingTypes.get(r.nextInt(typeSize)));
            } catch (Exception ex) {
            } // someone entered a bad building type.

            buildingList.add(new BuildingTemplate(type, coordList, totalCF, floors, -1));
        }

        return buildingList;
    }

    public int getBuildingsLeft() {
        Enumeration<Building> buildings = client.game.getBoard().getBuildings();
        int buildingCount = 0;
        while (buildings.hasMoreElements()) {
            buildings.nextElement();
            buildingCount++;
        }
        return buildingCount;
    }

    private void sendServerGameUpdate() {
        // Report the mech stat
        /*
         * Enumeration<Entity> en = client.game.getDevastatedEntities(); while (en.hasMoreElements()) { Entity ent = en.nextElement(); if ( ent.getOwner().getName().startsWith("War Bot") || ( !(ent instanceof MechWarrior) && !UnitUtils.hasArmorDamage(ent) && !UnitUtils.hasISDamage(ent) && !UnitUtils.hasCriticalDamage(ent) && !UnitUtils.hasLowAmmo(ent) && !UnitUtils.hasEmptyAmmo(ent))) continue; mwclient.serverSend("IPU|"+this.serializeEntity(ent, true, false)); } en = client.game.getGraveyardEntities(); while (en.hasMoreElements()) { Entity ent = en.nextElement(); if ( ent.getOwner().getName().startsWith("War Bot") || ( !(ent instanceof MechWarrior) && !UnitUtils.hasArmorDamage(ent) && !UnitUtils.hasISDamage(ent) && !UnitUtils.hasCriticalDamage(ent) && !UnitUtils.hasLowAmmo(ent) && !UnitUtils.hasEmptyAmmo(ent))) continue; if (ent instanceof Mech && ent.getInternal(Mech.LOC_CT) <= 0)
         * mwclient.serverSend("IPU|"+this.serializeEntity(ent, true, true)); else mwclient.serverSend("IPU|"+this.serializeEntity(ent, true, false)); }
         */

        // Only send data for units currently on the board.
        // any units removed from play will have already sent thier final
        // update.
        Enumeration<Entity> en = client.game.getEntities();
        while (en.hasMoreElements()) {
            Entity ent = en.nextElement();
            if (ent.getOwner().getName().startsWith("War Bot") || (!(ent instanceof MechWarrior) && !UnitUtils.hasArmorDamage(ent) && !UnitUtils.hasISDamage(ent) && !UnitUtils.hasCriticalDamage(ent) && !UnitUtils.hasLowAmmo(ent) && !UnitUtils.hasEmptyAmmo(ent)))
                continue;
            if (ent instanceof Mech && ent.getInternal(Mech.LOC_CT) <= 0)
                mwclient.serverSend("IPU|" + SerializeEntity.serializeEntity(ent, true, true, mwclient.isUsingAdvanceRepairs()));
            else
                mwclient.serverSend("IPU|" + SerializeEntity.serializeEntity(ent, true, false, mwclient.isUsingAdvanceRepairs()));
        }
        /*
         * en = client.game.getRetreatedEntities(); while (en.hasMoreElements()) { Entity ent = en.nextElement(); if ( ent.getOwner().getName().startsWith("War Bot") || ( !(ent instanceof MechWarrior) && !UnitUtils.hasArmorDamage(ent) && !UnitUtils.hasISDamage(ent) && !UnitUtils.hasCriticalDamage(ent) && !UnitUtils.hasLowAmmo(ent) && !UnitUtils.hasEmptyAmmo(ent))) continue; if (ent instanceof Mech && ent.getInternal(Mech.LOC_CT) <= 0) mwclient.serverSend("IPU|"+this.serializeEntity(ent, true, true)); else mwclient.serverSend("IPU|"+this.serializeEntity(ent, true, false)); }
         */
    }

    private Vector<IBasicOption> sortAndShrinkGameOptions(Vector<IBasicOption> defaults, Vector<IOption> serverGameOptions, Vector<IOption> OperationGameOptions) {

        Vector<IBasicOption> returnedOptions = new Vector<IBasicOption>(OperationGameOptions.size(), 1);
        Hashtable<String, IBasicOption> gameHash = new Hashtable<String, IBasicOption>();

        // Start with a base of Server options
        for (IOption option : serverGameOptions) {
            gameHash.put(option.getName(), option);
        }
        // Over write the server options with the Operation options
        for (IOption option : OperationGameOptions) {
            gameHash.put(option.getName(), option);
        }

        // Only add options to the return list that are different from the game
        // defaults.
        for (IBasicOption option : defaults) {

            IBasicOption currentOption = gameHash.get(option.getName());

            if (currentOption != null && !option.getValue().toString().equals(currentOption.getValue().toString())) {
                returnedOptions.add(currentOption);
            }
        }

        returnedOptions.trimToSize();

        return returnedOptions;
    }

    public Pilot createEntityPilot(Unit mek) {
        // get and set the options
        Pilot pilot = null;
        pilot = new Pilot(mek.getPilot().getName(), mek.getPilot().getGunnery(), mek.getPilot().getPiloting());

        // Hits defaults to 0 so no reason to keep checking over and over again.
        pilot.setHits(mek.getPilot().getHits());

        Iterator<MegaMekPilotOption> iter = mek.getPilot().getMegamekOptions().iterator();
        while (iter.hasNext()) {
            MegaMekPilotOption po = iter.next();
            if (po.getMmname().equals("weapon_specialist")) {
                pilot.getOptions().getOption(po.getMmname()).setValue(mek.getPilot().getWeapon());
            } else if (po.getMmname().equals("edge")) {
                pilot.getOptions().getOption(po.getMmname()).setValue(mek.getPilot().getSkills().getPilotSkill(PilotSkill.EdgeSkillID).getLevel());
            } else {
                pilot.getOptions().getOption(po.getMmname()).setValue(po.isValue());
            }
        }

        return pilot;
    }

    private void clearGameOptions() {

        Vector<IBasicOption> defaultOptions = new Vector<IBasicOption>(10, 1);

        for (Enumeration<IOptionGroup> i = client.game.getOptions().getGroups(); i.hasMoreElements();) {
            IOptionGroup group = i.nextElement();

            for (Enumeration<IOption> j = group.getOptions(); j.hasMoreElements();) {
                IOption option = j.nextElement();

                option.clearValue();
                defaultOptions.add(option);
            }
        }

        client.sendGameOptions("", defaultOptions);
    }
}
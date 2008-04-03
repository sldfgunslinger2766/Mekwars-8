/*
 * MekWars - Copyright (C) 2004 
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megamek)
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

package client.campaign;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import megamek.common.AmmoType;
import megamek.common.CriticalSlot;
import megamek.common.Entity;
import megamek.common.Infantry;
import megamek.common.Mech;
import megamek.common.Mounted;
import megamek.common.WeaponType;

import client.MWClient;

import common.CampaignData;
import common.House;
import common.MegaMekPilotOption;
import common.Unit;
import common.campaign.pilot.Pilot;
import common.campaign.pilot.skills.PilotSkill;
import common.util.TokenReader;
import common.util.UnitUtils;

/**
 * Class for unit object used by Client
 */
public class CUnit extends Unit {

    // VARIABLES
    protected Entity UnitEntity;

    private int BV;
    private int scrappableFor = 0;// value if scrapped
    private boolean pilotIsRepairing = false;
    private MWClient mwclient;

    // CONSTRUCTORS
    public CUnit() {
        init();
    }

    public CUnit(MWClient mwclient) {
        this.mwclient = mwclient;
        init();
    }

    // PRIVATE METHODS
    private void init() {
        UnitEntity = null;
        BV = 0;
        setStatus(STATUS_OK);
        setProducer("unknown origin");
    }

    // PUBLIC METHODS
    public boolean setData(String data) {

        StringTokenizer ST;
        String element;
        String unitDamage = null;
        CampaignData.mwlog.infoLog("PDATA: " + data);

        ST = new StringTokenizer(data, "$");
        element = TokenReader.readString(ST);
        if (!element.equals("CM")) {
            return (false);
        }

        setUnitFilename(TokenReader.readString(ST));
        setId((TokenReader.readInt(ST)));
        setStatus(TokenReader.readInt(ST));

        setProducer(TokenReader.readString(ST));
        String pilotname = "John Denver";
        int gunnery = 4;
        int piloting = 5;
        int exp = 0;
        Pilot p = null;
        StringTokenizer STR = new StringTokenizer(TokenReader.readString(ST), "#");
        pilotname = TokenReader.readString(STR);
        exp = TokenReader.readInt(STR);
        gunnery = TokenReader.readInt(STR);
        piloting = TokenReader.readInt(STR);
        p = new Pilot(pilotname, gunnery, piloting);
        p.setExperience(exp);
        int skillAmount = TokenReader.readInt(STR);
        for (int i = 0; i < skillAmount; i++) {
            PilotSkill skill = new PilotSkill(TokenReader.readInt(STR), TokenReader.readString(STR), TokenReader.readInt(STR), TokenReader.readString(STR));

            if (skill.getName().equals("Weapon Specialist"))// WS skill has an
                // extra var
                p.setWeapon(TokenReader.readString(STR));

            if (skill.getName().equals("Trait"))// Trait skill has an extra var
                p.setTraitName(TokenReader.readString(STR));

            if (skill.getName().equals("Edge")) {
                p.setTac(TokenReader.readBoolean(STR));
                p.setKO(TokenReader.readBoolean(STR));
                p.setHeadHit(TokenReader.readBoolean(STR));
                p.setExplosion(TokenReader.readBoolean(STR));
            }
            p.getSkills().add(skill);
        }

        p.setKills(TokenReader.readInt(STR));

        p.setHits(TokenReader.readInt(STR));

        int mmoptionsamount = TokenReader.readInt(ST);
        for (int i = 0; i < mmoptionsamount; i++) {
            MegaMekPilotOption mo = new MegaMekPilotOption(TokenReader.readString(ST), Boolean.parseBoolean(TokenReader.readString(ST)));
            p.addMegamekOption(mo);
        }

        setType(TokenReader.readInt(ST));
        // setType(getEntityType(UnitEntity));
        setPilot(p);
        BV = Math.max(TokenReader.readInt(ST), 0);

        setWeightclass(TokenReader.readInt(ST));
        // if (this.getType() == Unit.MEK || this.getType() == Unit.VEHICLE)
        // setWeightclass(getEntityWeight(UnitEntity));
        setId(TokenReader.readInt(ST));

        createEntity();
        if (UnitEntity == null) {
            CampaignData.mwlog.errLog("Cannot load entity!");
            return (false);
        }

        this.getC3Type(UnitEntity);

        // don't try to set ammo and eject on an OMG
        if (this.getModelName().startsWith("Error") || this.getModelName().startsWith("OMG")) {
            UnitEntity.setExternalId(this.getId());
            UnitEntity.setCrew(new megamek.common.Pilot(p.getName(), p.getGunnery(), p.getPiloting()));
            return true;
        }

        // set autoeject if its a mech
        if (UnitEntity instanceof Mech && ST.hasMoreElements())
            ((Mech) UnitEntity).setAutoEject(Boolean.parseBoolean(TokenReader.readString(ST)));

        // then set up ammo loadout
        {
            try {
                int maxCrits = TokenReader.readInt(ST);
                ArrayList<Mounted> e = UnitEntity.getAmmo();
                for (int count = 0; count < maxCrits; count++) {
                    int weaponType = TokenReader.readInt(ST);
                    String ammoName = TokenReader.readString(ST);
                    int shots = TokenReader.readInt(ST);
                    boolean hotloaded = TokenReader.readBoolean(ST);

                    Mounted mWeapon = e.get(count);

                    AmmoType at = this.getEntityAmmo(weaponType, ammoName);
                    mWeapon.changeAmmoType(at);
                    mWeapon.setShotsLeft(shots);
                    mWeapon.setHotLoad(hotloaded);
                }
            } catch (Exception ex) {
                // ammo crits change or something bad. just continue with the
                // next unit
                return true;
            }
        }// end ammo

        // setup rapid fire Machine guns, if any
        {
            int maxMachineGuns = TokenReader.readInt(ST);
            for (int count = 0; count < maxMachineGuns; count++) {
                int location = TokenReader.readInt(ST);
                int slot = TokenReader.readInt(ST);
                boolean selection = TokenReader.readBoolean(ST);
                CriticalSlot cs = UnitEntity.getCritical(location, slot);

                Mounted mg = UnitEntity.getEquipment(cs.getIndex());

                mg.setRapidfire(selection);

            }
        }// Machine Guns

        UnitEntity.setSpotlight(Boolean.parseBoolean(TokenReader.readString(ST)));

        UnitEntity.setSpotlightState(Boolean.parseBoolean(TokenReader.readString(ST)));

        UnitEntity.setTargSysType(Math.max(0, TokenReader.readInt(ST)));

        scrappableFor = TokenReader.readInt(ST);

        unitDamage = TokenReader.readString(ST);

        pilotIsRepairing = TokenReader.readBoolean(ST);

        this.setRepairCosts(TokenReader.readInt(ST), TokenReader.readInt(ST));

        UnitEntity.setExternalId(this.getId());
        UnitEntity.setCrew(new megamek.common.Pilot(p.getName(), p.getGunnery(), p.getPiloting()));

        if (unitDamage != null)
            UnitUtils.applyBattleDamage(UnitEntity, unitDamage);

        return (true);
    }

    /**
     * Method which generates data for an auto unit. Since auto units have no
     * unique properties this can be assembled client side rather than sent from
     * the server.
     * 
     * @urgru 1/4/05
     */
    public void setAutoUnitData(String filename, int distance, int edge) {
        setUnitFilename(filename);
        // setProducer("Autounit");
        setPilot(new Pilot("Autopilot", 4, 5));
        // setType(Unit.VEHICLE);//auto units are always vehs ...
        createEntity();// make the entity
        if (distance > 0)
            UnitEntity.setOffBoard(distance, edge);// move it offboard
    }

    /**
     * @return a smaller description
     */
    public String getSmallDescription() {
        if (this.getType() == Unit.MEK || this.getType() == Unit.VEHICLE)
            return getModelName() + " [" + getPilot().getGunnery() + "/" + getPilot().getPiloting() + "]";

        if (this.getType() == Unit.INFANTRY || this.getType() == Unit.BATTLEARMOR) {
            if (((Infantry) this.UnitEntity).isAntiMek())
                return getModelName() + " [" + getPilot().getGunnery() + "/" + getPilot().getPiloting() + "]";
            return getModelName() + " [" + getPilot().getGunnery() + "]";
        }
        return getModelName() + " [" + getPilot().getGunnery() + "]";
    }

    public String getDisplayInfo(String armyText) {
        String tinfo = "";

        if (this.getType() == Unit.MEK && !UnitEntity.isOmni())
            tinfo = "<html><body>#" + getId() + " " + UnitEntity.getChassis() + ", " + getModelName();
        else
            tinfo = "<html><body>#" + getId() + " " + getModelName();

        if (this.getType() == Unit.MEK || this.getType() == Unit.VEHICLE)
            tinfo += " (" + getPilot().getName() + ", " + getPilot().getGunnery() + "/" + getPilot().getPiloting() + ") <br>";
        else if (this.getType() == Unit.BATTLEARMOR || this.getType() == Unit.INFANTRY) {

            if (((Infantry) this.UnitEntity).isAntiMek())
                tinfo += " (" + getPilot().getName() + ", " + getPilot().getGunnery() + "/" + getPilot().getPiloting() + ") <br>";
            else
                tinfo += " (" + getPilot().getName() + ", " + getPilot().getGunnery() + ") <br>";
        } else
            tinfo += " (" + getPilot().getName() + ", " + getPilot().getGunnery() + ") <br>";

        if (this.getType() == Unit.VEHICLE)
            tinfo += " Movement: " + this.getEntity().getMovementModeAsString() + "<br>";

        tinfo += "BV: " + BV + " // Exp: " + getPilot().getExperience() + " // Kills: " + getPilot().getKills() + "<br> ";

        if (getPilot().getSkills().size() > 0) {
            tinfo += "Skills: ";
            /*
             * Iterator it = getPilot().getSkills().getSkillIterator(); while
             * (it.hasNext()) { tinfo += ((PilotSkill) it.next()).getName(); if
             * (it.hasNext()) tinfo += ", "; }
             */
            tinfo += getPilot().getSkillString(false, mwclient.getData().getHouseByName(mwclient.getPlayer().getHouse()).getBasePilotSkill(this.getType()));
            tinfo += "<br>";
        }

        if (getPilot().getHits() > 0)
            tinfo += "Hits: " + Integer.toString(getPilot().getHits()) + "<br>";

        if (!armyText.equals(""))
            tinfo += armyText + "<br>";

        String capacity = this.getEntity().getUnusedString();
        if (capacity != null && capacity.startsWith("Troops")) {
            capacity = capacity.substring(9);// strip "Troops - " from string
            tinfo += "Cargo: " + capacity + "<br>";
        }
        if (this.getLifeTimeRepairCost() > 0) {
            tinfo += "Repair Costs: " + this.getCurrentRepairCost() + "/" + this.getLifeTimeRepairCost() + "<br>";
        }
        tinfo += getProducer();

        if (scrappableFor > 0 && !Boolean.parseBoolean(mwclient.getserverConfigs("UseAdvanceRepair")) && !Boolean.parseBoolean(mwclient.getserverConfigs("UseSimpleRepair")))
            tinfo += "<br><br><b>Scrap Value: " + mwclient.moneyOrFluMessage(true, false, scrappableFor) + "</b>";

        tinfo += "</body></html>";
        return (tinfo);
    }

    public String getModelName() {

        if (getType() != MEK) {
            StringBuilder name = new StringBuilder(new StringTokenizer(this.getEntity().getShortNameRaw()).nextToken());
            name.append(" ").append(this.getEntity().getModel());
            return name.toString();
        }

        if (this.getEntity().isOmni())
            return UnitEntity.getChassis() + " " + UnitEntity.getModel();
        // else
        return this.getEntity().getModel();
    }

    public int getBV() {
        if (BV < 0)
            return 0;

        // else
        return BV;
    }

    public Entity getEntity() {
        return UnitEntity;
    }

    /**
     * Tries to set UnitEntity from the global MekFileName
     */
    public void createEntity() {
        // MMClient.mwClientLog.clientErrLog("Filename: " + getUnitFilename());
        UnitEntity = UnitUtils.createEntity(getUnitFilename());

        if (UnitEntity == null) {
            CampaignData.mwlog.errLog("Error unit failed to load. Exiting.");
            System.exit(1);
        }

        if ( UnitEntity.getChassis().equals("Error") ) {
            setProducer("Unable to find " + getUnitFilename() + " on clients system!");
        }
        // setType(getEntityType(UnitEntity));
        this.getC3Type(UnitEntity);
    }

    public boolean isOmni() {
        boolean isOmni = this.getEntity().isOmni();
        String targetChassis = this.getEntity().getChassis();

        if (this.getType() == Unit.VEHICLE && !isOmni) {
            try {
                FileInputStream fis = new FileInputStream("./data/mechfiles/omnivehiclelist.txt");
                BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
                while (dis.ready()) {
                    String chassie = dis.readLine();
                    // check to see if the chassies listed in the file match
                    // omni vehicle chassies.
                    if (targetChassis.equalsIgnoreCase(chassie)) {
                        dis.close();
                        fis.close();
                        return true;
                    }
                }
                dis.close();
                fis.close();
            } catch (Exception ex) {

            }
        }

        return isOmni;
    }

    public int getOriginalBV() {
        return UnitEntity.calculateBattleValue(false);
    }

    public void applyRepairs(String data) {
        createEntity();
        UnitUtils.applyBattleDamage(UnitEntity, data);
    }

    public boolean getPilotIsReparing() {
        return pilotIsRepairing;
    }

    // STATIC METHODS
    /**
     * A method which returns the MU cost of a specified campaign unit.
     * 
     * @return int - # of MU it takes to buy a unit of the given weight class
     */
    public static int getPriceForUnit(MWClient mwclient, int weightclass, int type_id, House producer) {

        int result = Integer.MAX_VALUE;
        try {
            String classtype = Unit.getWeightClassDesc(weightclass) + Unit.getTypeClassDesc(type_id) + "Price";

            if (type_id == Unit.MEK)
                result = Integer.parseInt(mwclient.getserverConfigs(Unit.getWeightClassDesc(weightclass) + "Price"));
            else
                result = Integer.parseInt(mwclient.getserverConfigs(classtype));

            // modify the result by the faction price modifier
            result += producer.getHouseUnitPriceMod(type_id, weightclass);

            // dont allow negative pricing
            if (result < 0)
                result = 0;
        } catch (Exception ex) {
            CampaignData.mwlog.errLog(ex);
        }
        return result;
    }// end getPriceForCUnit()

    /**
     * A method which returns the influence cost of a specified campaign mech.
     * 
     * @return int - # if IP it takes to buy a mech of the given units weight
     *         class
     */
    public static int getInfluenceForUnit(MWClient mwclient, int weightclass, int type_id, House producer) {

        int result = Integer.MAX_VALUE;
        String classtype = Unit.getWeightClassDesc(weightclass) + Unit.getTypeClassDesc(type_id) + "Inf";

        if (type_id == Unit.MEK)
            result = Integer.parseInt(mwclient.getserverConfigs(Unit.getWeightClassDesc(weightclass) + "Inf"));
        else
            result = Integer.parseInt(mwclient.getserverConfigs(classtype));

        // modify the result by the faction price modifier
        result += producer.getHouseUnitFluMod(type_id, weightclass);

        // dont allow negative pricing
        if (result < 0)
            result = 0;

        return result;
    }

    /**
     * A method which returns the PP COST of a unit. Meks and Vehicles are
     * segregated by weightclass. Infantry are flat priced accross
     * 
     * all weight classes. @ param weight - the weight class to be checked @ return
     * int - the PP cost
     */
    public static int getPPForUnit(MWClient mwclient, int weightclass, int type_id, House producer) {

        int result = Integer.MAX_VALUE;
        String classtype = Unit.getWeightClassDesc(weightclass) + Unit.getTypeClassDesc(type_id) + "PP";

        if (type_id == Unit.MEK)
            result = Integer.parseInt(mwclient.getserverConfigs(Unit.getWeightClassDesc(weightclass) + "PP"));
        else
            result = Integer.parseInt(mwclient.getserverConfigs(classtype));

        // adjust PP cost by faction specific mod
        result += producer.getHouseUnitComponentMod(type_id, weightclass);

        // dont allow a unit to consume negative PP
        if (result < 0)
            result = 0;

        return result;
    }

    public static double getArmorCost(Entity unit, MWClient client) {
        double cost = 0.0;

        if (Boolean.parseBoolean(client.getserverConfigs("UsePartsRepair")))
            return 0;

        String armorCost = "CostPoint" + UnitUtils.getArmorShortName(unit);
        cost = Double.parseDouble(client.getserverConfigs(armorCost));

        return cost;
    }

    public static double getStructureCost(Entity unit, MWClient client) {
        double cost = 0.0;

        if (Boolean.parseBoolean(client.getserverConfigs("UsePartsRepair")))
            return 0;

        String armorCost = "CostPoint" + UnitUtils.getInternalShortName(unit) + "IS";
        cost = Double.parseDouble(client.getserverConfigs(armorCost));

        return cost;
    }

    public static double getCritCost(Entity unit, MWClient client, CriticalSlot crit) {
        double cost = 0.0;

        if (Boolean.parseBoolean(client.getserverConfigs("UsePartsRepair")))
            return 0;

        if (crit == null)
            return 0;

        if (crit.isBreached() && !crit.isDamaged())
            return 0;
        // else
        if (UnitUtils.isEngineCrit(crit))
            cost = Double.parseDouble(client.getserverConfigs("EngineCritRepairCost"));
        else if (crit.getType() == CriticalSlot.TYPE_SYSTEM)
            if (crit.isMissing())
                cost = Double.parseDouble(client.getserverConfigs("SystemCritReplaceCost"));
            else
                cost = Double.parseDouble(client.getserverConfigs("SystemCritRepairCost"));
        else {
            Mounted mounted = unit.getEquipment(crit.getIndex());

            if (mounted.getType() instanceof WeaponType) {
                WeaponType weapon = (WeaponType) mounted.getType();
                if (weapon.hasFlag(WeaponType.F_ENERGY))
                    if (crit.isMissing())
                        cost = Double.parseDouble(client.getserverConfigs("EnergyWeaponCritReplaceCost"));
                    else
                        cost = Double.parseDouble(client.getserverConfigs("EnergyWeaponCritRepairCost"));
                else if (weapon.hasFlag(WeaponType.F_BALLISTIC))
                    if (crit.isMissing())
                        cost = Double.parseDouble(client.getserverConfigs("BallisticCritReplaceCost"));
                    else
                        cost = Double.parseDouble(client.getserverConfigs("BallisticCritRepairCost"));
                else if (weapon.hasFlag(WeaponType.F_MISSILE))
                    if (crit.isMissing())
                        cost = Double.parseDouble(client.getserverConfigs("MissileCritReplaceCost"));
                    else
                        cost = Double.parseDouble(client.getserverConfigs("MissileCritRepairCost"));
                else // use the misc eq costs.
                if (crit.isMissing())
                    cost = Double.parseDouble(client.getserverConfigs("EquipmentCritReplaceCost"));
                else
                    cost = Double.parseDouble(client.getserverConfigs("EquipmentCritRepairCost"));
            } else // use the misc eq costs.
            if (crit.isMissing())
                cost = Double.parseDouble(client.getserverConfigs("EquipmentCritReplaceCost"));
            else
                cost = Double.parseDouble(client.getserverConfigs("EquipmentCritRepairCost"));
        }

        cost = Math.max(cost, 1);
        return cost;
    }
}// end CUnit.java

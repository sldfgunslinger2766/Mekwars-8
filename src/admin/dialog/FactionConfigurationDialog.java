/*
 * MekWars - Copyright (C) 2007 
 * 
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

/**
 * @author jtighe
 * 
 * Server Configuration Page. All new Server Options need to be added
 * To this page as well.
 */

package admin.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import client.MWClient;
import client.gui.SpringLayoutHelper;

public final class FactionConfigurationDialog implements ActionListener {
	
	private final static String okayCommand = "okay";
	private final static String cancelCommand = "cancel";
	private String windowName = "";
	
	private JTextField baseTextField = new JTextField(5);
    private JCheckBox  BaseCheckBox = new JCheckBox();
    
    private final JButton okayButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");
	
	private JDialog dialog;
	private JOptionPane pane;
	
	private String houseName = "";
	
	JTabbedPane ConfigPane = new JTabbedPane(SwingConstants.TOP);
	
    MWClient mwclient = null;
	/**
	 * @author jtighe
	 * 
	 * Opens the server config page in the client.
	 * @param client
	 */
    
    /**
     * @author Torren (Jason Tighe)
     * 12/29/2005
     * 
     * I've completely redone how the Server config dialog works
     * There are 2 basic fields now baseTextField which is a JTextField
     * and baseCheckBox which is a JCheckBox.
     * 
     * When you add a new server config add the labels to the tab
     * then use the base fields to add the ver. make sure to set the base
     * field's name method this is used to populate and save.
     * 
     *  ex: BaseTextField.setName("DefaultServerOptionsVariable");
     *  
     * Two recursive methods populate and save the data to the server
     *  
     * findAndPopulateTextAndCheckBoxes(JPanel)
     * findAndSaveConfigs(JPanel)
     *  
     * This change to the code removes the tediousness of having to add a 
     * new var to 3 locations when it is use. Now only 1 location needs to added
     * and that is the vars placement on the tab in the UI.
     */
	public FactionConfigurationDialog(MWClient mwclient, String houseName) {
		
        this.mwclient = mwclient;
        this.houseName = houseName;
        this.windowName = "MekWars "+houseName+" Configuration";
        
		//TAB PANELS (these are added to the root pane as tabs)
		JPanel repodPanel = new JPanel();
		JPanel technicianPanel = new JPanel();
		JPanel unitPanel = new JPanel();
		JPanel unit2Panel = new JPanel();
		JPanel factionPanel = new JPanel();
		JPanel directSellPanel = new JPanel();
		JPanel productionPanel = new JPanel();//was factoryOptions
		JPanel rewardPanel = new JPanel();
		JPanel pilotsPanel = new JPanel();//allows SO's set up pilot options and personal pilot queue options
		JPanel pilotSkillsModPanel = new JPanel();//Allows the SO's to set the mods for each skill type that affects the MM game.
		JPanel pilotSkillsPanel = new JPanel();//allows SO's to select what pilot skills they want for what unit types.
		
		/* 
		 * REPOD PANEL CONSTRUCTION
		 * 
		 * Repod contols. Costs, factory usage, table options, etc.
		 * 
		 * Use nested layouts. A Box containing a Flow and 3 Springs.
		 */
		JPanel repodBoxPanel = new JPanel();
		JPanel repodCBoxGridPanel = new JPanel(new GridLayout(2,3));
		JPanel repodSpringGrid = new JPanel(new GridLayout(2,2));
		JPanel refreshSpring = new JPanel(new SpringLayout());
		JPanel cbillSpring = new JPanel(new SpringLayout());
		JPanel componentSpring = new JPanel(new SpringLayout());
		JPanel fluSpring = new JPanel(new SpringLayout());
		repodBoxPanel.setLayout(new BoxLayout(repodBoxPanel, BoxLayout.Y_AXIS));
		repodSpringGrid.add(cbillSpring);
		repodSpringGrid.add(fluSpring);
		repodSpringGrid.add(componentSpring);
		repodSpringGrid.add(refreshSpring);
		repodBoxPanel.add(repodCBoxGridPanel);
		repodBoxPanel.add(repodSpringGrid);
		
		//set up the flow panel
        BaseCheckBox = new JCheckBox("Cost "+mwclient.moneyOrFluMessage(true,true,-1));

        BaseCheckBox.setToolTipText("Check to enable "+mwclient.moneyOrFluMessage(true,true,-1) +" charges for repods");
        BaseCheckBox.setName("DoesRepodCost");
		repodCBoxGridPanel.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Use Factory");

        BaseCheckBox.setToolTipText("Check to have repodding use a factory");
        BaseCheckBox.setName("RepodUsesFactory");
		repodCBoxGridPanel.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Uses Comps");

        BaseCheckBox.setToolTipText("Check to have repodding consume components");
        BaseCheckBox.setName("RepodUsesComp");
		repodCBoxGridPanel.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Common Table");

        BaseCheckBox.setToolTipText("Check to allow all factions to repod from common table.");
        BaseCheckBox.setName("UseCommonTableForRepod");
		repodCBoxGridPanel.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Allow Random");

        BaseCheckBox.setToolTipText("Check to allow random repods.");
        BaseCheckBox.setName("RandomRepodAllowed");
		repodCBoxGridPanel.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Random Only");

        BaseCheckBox.setToolTipText("If checked, only random repods are allowed.");
        BaseCheckBox.setName("RandomRepodOnly");
		repodCBoxGridPanel.add(BaseCheckBox);		
		
		//and then the various springs. MU first.
        baseTextField = new JTextField(5);
		cbillSpring.add(new JLabel("Light "+mwclient.moneyOrFluMessage(true,true,-1)+":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(true,true,-1)+" required to repod a light unit");
        baseTextField.setName("RepodCostLight");
		cbillSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		cbillSpring.add(new JLabel("Medium "+mwclient.moneyOrFluMessage(true,true,-1)+":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(true,true,-1)+" required to repod a medium unit");
        baseTextField.setName("RepodCostMedium");
		cbillSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		cbillSpring.add(new JLabel("Heavy "+mwclient.moneyOrFluMessage(true,true,-1)+":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(true,true,-1)+" required to repod a heavy unit");
        baseTextField.setName("RepodCostHeavy");
		cbillSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		cbillSpring.add(new JLabel("Assault "+mwclient.moneyOrFluMessage(true,true,-1)+":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(true,true,-1)+" required to repod an assault unit");
        baseTextField.setName("RepodCostAssault");
		cbillSpring.add(baseTextField);
		
		//now the flu spring
        baseTextField = new JTextField(5);
		fluSpring.add(new JLabel("Light "+mwclient.moneyOrFluMessage(false,true,-1)+":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(false,true,-1)+" required to repod a light unit");
        baseTextField.setName("RepodFluLight");
		fluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		fluSpring.add(new JLabel("Medium "+mwclient.moneyOrFluMessage(false,true,-1)+":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(false,true,-1)+" required to repod a medium unit");
        baseTextField.setName("RepodFluMedium");
		fluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		fluSpring.add(new JLabel("Heavy "+mwclient.moneyOrFluMessage(false,true,-1)+":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(false,true,-1)+" required to repod a heavy unit");
        baseTextField.setName("RepodFluHeavy");
		fluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		fluSpring.add(new JLabel("Assault "+mwclient.moneyOrFluMessage(false,true,-1)+":", SwingConstants.TRAILING));
        baseTextField.setToolTipText(mwclient.moneyOrFluMessage(false,true,-1)+" required to repod an assault unit");
        baseTextField.setName("RepodFluAssault");
		fluSpring.add(baseTextField);
		
		//then the component spring ...
        baseTextField = new JTextField(5);
		componentSpring.add(new JLabel("Light Components:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Components required to repod a light unit");
        baseTextField.setName("RepodCompLight");
		componentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		componentSpring.add(new JLabel("Medium Components:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Components required to repod a medium unit");
        baseTextField.setName("RepodCompMedium");
		componentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		componentSpring.add(new JLabel("Heavy Components:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Components required to repod a heavy unit");
        baseTextField.setName("RepodCompHeavy");
		componentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		componentSpring.add(new JLabel("Assault Components:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Components required to repod an assault unit");
        baseTextField.setName("RepodCompAssault");
		componentSpring.add(baseTextField);
		
		//then, the refresh times
        baseTextField = new JTextField(5);
		refreshSpring.add(new JLabel("Light Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Num. miniticks requires to refresh a factory which pods a light unit");
        baseTextField.setName("RepodRefreshTimeLight");
		refreshSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		refreshSpring.add(new JLabel("Medium Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Num. miniticks requires to refresh a factory which pods a medium unit");
        baseTextField.setName("RepodRefreshTimeMedium");
		refreshSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		refreshSpring.add(new JLabel("Heavy Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Num. miniticks requires to refresh a factory which pods a heavy unit");
        baseTextField.setName("RepodRefreshTimeHeavy");
		refreshSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		refreshSpring.add(new JLabel("Assault Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Num. miniticks requires to refresh a factory which pods an assault unit");
        baseTextField.setName("RepodRefreshTimeAssault");
		refreshSpring.add(baseTextField);
		
		//and last, the random modifier
		JPanel repodRandomFlowTemp = new JPanel(new SpringLayout());
        baseTextField = new JTextField(5);
		repodRandomFlowTemp.add(new JLabel("Percent Discount for Random:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount to reduce repod costs when a pod is random, instead of targetted");
        baseTextField.setName("RepodRandomMod");
		repodRandomFlowTemp.add(baseTextField);
		
        baseTextField = new JTextField(5);
        repodRandomFlowTemp.add(new JLabel("No Factory Repod Folder:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>If repoding does not use factories then all repods will check this folder<br>for the build tables for the house.</html>");
        baseTextField.setName("NoFactoryRepodFolder");
        repodRandomFlowTemp.add(baseTextField);

        //finalize the layout.
		SpringLayoutHelper.setupSpringGrid(cbillSpring,4,2);
		SpringLayoutHelper.setupSpringGrid(fluSpring,4,2);
		SpringLayoutHelper.setupSpringGrid(refreshSpring,4,2);
		SpringLayoutHelper.setupSpringGrid(componentSpring,4,2);
        SpringLayoutHelper.setupSpringGrid(repodRandomFlowTemp,1,4);
		repodBoxPanel.add(repodRandomFlowTemp);//add the temp panel for the mod. this needs to be rewritten.
		repodPanel.add(repodBoxPanel);
		
		/*
		 * TECH PANEL CONSTRUCTION
		 * 
		 * Technician (and bays from XP) options.
		 */
		JPanel techsBox = new JPanel();
		techsBox.setLayout(new BoxLayout(techsBox, BoxLayout.Y_AXIS));
		JPanel techsCBoxFlow = new JPanel();
		JPanel techsSendRecPayFlow = new JPanel();
		JPanel techSpring = new JPanel(new SpringLayout());
		techsBox.add(techsCBoxFlow);
		techsBox.add(techsSendRecPayFlow);
		techsBox.add(techSpring);
		
		//the basic CBox flow
        BaseCheckBox = new JCheckBox("Use Techs");
        BaseCheckBox.setToolTipText("Unchecking disables technicians. Not advised.");

        BaseCheckBox.setName("UseTechnicians");
        techsCBoxFlow.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox("Use XP");

        BaseCheckBox.setToolTipText("Check grants additional technicians w/ XP.");
        BaseCheckBox.setName("UseExperience");
        techsCBoxFlow.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox("Decreasing Cost");

        BaseCheckBox.setToolTipText("Checking lowers tech hiring costs w/ XP.");
        BaseCheckBox.setName("DecreasingTechCost");
		techsCBoxFlow.add(BaseCheckBox);

		//the sendRecPay flow.
        BaseCheckBox = new JCheckBox("Sender Pays");

        BaseCheckBox.setToolTipText("If checked, a player sending a unit will pay techs.");
        BaseCheckBox.setName("SenderPaysOnTransfer");
        techsSendRecPayFlow.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Recipient Pays");

        BaseCheckBox.setToolTipText("If checked, a player receiving a unit will pay techs.");
        BaseCheckBox.setName("ReceiverPaysOnTransfer");
		techsSendRecPayFlow.add(BaseCheckBox);
		
		//set up the spring
        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Base Tech Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Starting cost to hire a technician");
        baseTextField.setName("BaseTechCost");
		techSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("XP for Decrease:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount of XP required to reduce hiring cost by 1 "+mwclient.moneyOrFluMessage(true,true,-1));
        baseTextField.setName("XPForDecrease");
		techSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Minimum Tech Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Lowest hiring price. XP cannot reduce below this level.");
        baseTextField.setName("MinimumTechCost");
		techSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Additive Per Tech:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" +
				"Use additive costs -- each tech costs as much as the last one, plus the additive. EG -<br>"+
				"with .05 set, the first tech would cost .05, the second .10, the third .15, the fourth .20,<br>"+
				"such that your first 4 techs cost haf a Cbill (total) to maintain, while the 10th tech costs<br>"+
				"half a "+mwclient.moneyOrFluMessage(true,true,-1)+" all by himself. A cap on this price can be set, after which there is no further<br>"+
				"increase. The ceiling ABSOLUTELY MUST be a multiple of the additive.</HTML>");
        baseTextField.setName("AdditivePerTech");
		techSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Additive Ceiling:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Additive ceiling. Post-game per-tech costs don't increase past this level.");
        baseTextField.setName("AdditiveCostCeiling");
		techSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Transfer Payment:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Percentage of usual post-game cost charged if transfer fees are enabled.");
        baseTextField.setName("TransferPayment");
		techSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Maint Increase:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount maintainance level is increased each slice a unit is maintained");
        baseTextField.setName("MaintainanceIncrease");
		techSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Maint Decrease:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount maintainance level is lowered each slice a unit is unmaintained");
        baseTextField.setName("MaintainanceDecrease");
		techSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Base Unmaint Level:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Mainatainance level set when a unit is first unmaintained. Set to 100 to disable.");
        baseTextField.setName("BaseUnmaintainedLevel");
		techSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Unmaintain Penalty:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("" +
				"<HTML>Maintainance reduction for units which are already below 100. If the BaseLevel is lower than current<br>" +
				"level minus penalty, it is used instead. Example1: A unit has a maintainance level of 90 and is set to<br>" +
				"unmaintained status. The unmaint penalty is 10 and base elvel is 75. 90-10 = 80, so the base level of 75 is<br>" +
				"set. Example2: A unit has an mlevel of 80 and is set to unmaintained. 80 - 10 = 70. 70 is set and the base<br>" +
				"level (75) is ignored.</HTML>");
        baseTextField.setName("UnmaintainedPenalty");
        techSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Transfer Scrap Level:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Units @ or under this maint. level must survive a scrap check<br>to be transfered. Set to 0 to disable</HTML>");
        baseTextField.setName("TransferScrapLevel");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Techs To Proto Point Ratio:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Ratio of Techs to 5 Protos Default 1 tech</HTML>");
        baseTextField.setName("TechsToProtoPointRatio");
		techSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Techs per Light Mek:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a light Mek</HTML>");
        baseTextField.setName("TechsForLightMek");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Medium Mek:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a medium Mek</HTML>");
        baseTextField.setName("TechsForMediumMek");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Techs per Heavy Mek:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a heavy Mek</HTML>");
        baseTextField.setName("TechsForHeavyMek");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Assault Mek:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain an assault Mek</HTML>");
        baseTextField.setName("TechsForAssaultMek");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Techs per Light Vehicle:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a light Vehicle</HTML>");
        baseTextField.setName("TechsForLightVehicle");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Techs per Medium Vehicle:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a medium Vehicle</HTML>");
        baseTextField.setName("TechsForMediumVehicle");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Heavy Vehicle:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a heavy Vehicle</HTML>");
        baseTextField.setName("TechsForHeavyVehicle");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Assault Vehicle:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain an assault Vehicle</HTML>");
        baseTextField.setName("TechsForAssaultVehicle");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Techs per Light Infantry:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a light Infantry</HTML>");
        baseTextField.setName("TechsForLightInfantry");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Medium Infantry:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a medium Infantry</HTML>");
        baseTextField.setName("TechsForMediumInfantry");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Heavy Infantry:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a heavy Infantry</HTML>");
        baseTextField.setName("TechsForHeavyInfantry");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Assault Infantry:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain an assault Infantry</HTML>");
        baseTextField.setName("TechsForAssaultInfantry");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Techs per Light BattleArmor:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a light BattleArmor</HTML>");
        baseTextField.setName("TechsForLightBattleArmor");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		techSpring.add(new JLabel("Techs per Medium BattleArmor:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a medium BattleArmor</HTML>");
        baseTextField.setName("TechsForMediumBattleArmor");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Heavy BattleArmor:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain a heavy BattleArmor</HTML>");
        baseTextField.setName("TechsForHeavyBattleArmor");
		techSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        techSpring.add(new JLabel("Techs per Assault BattleArmor:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Number of Techs it takes to maintain an assault BattleArmor</HTML>");
        baseTextField.setName("TechsForAssaultBattleArmor");
		techSpring.add(baseTextField);

		SpringLayoutHelper.setupSpringGrid(techSpring,14,4);

		//finalize the layout
		technicianPanel.add(techsBox);

		/*
		 * PILOT SKILLS Panel
		 */
		JPanel mekPilotSkillsSpring = new JPanel(new SpringLayout());
		JPanel vehiclePilotSkillsSpring = new JPanel(new SpringLayout());
		JPanel infantryPilotSkillsSpring = new JPanel(new SpringLayout());
		JPanel protomechPilotSkillsSpring = new JPanel(new SpringLayout());
		JPanel battlearmorPilotSkillsSpring = new JPanel(new SpringLayout());
		JPanel bannedWSWeaponsSpring = new JPanel(new SpringLayout());
		
		JPanel mainSpring = new JPanel(new SpringLayout());

		
		mekPilotSkillsSpring.add(new JLabel("Mek", SwingConstants.TRAILING));
		mekPilotSkillsSpring.add(new JLabel("Pilot Skills"));
		
        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("DM", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Meks Only:<br>Set Chance for a pilot to gain dodge maneuver</body></html>");
        baseTextField.setName("chanceforDMforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("MS", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Meks Only:<br>Set Chance for a pilot to gain melee specialist</body></html>");
        baseTextField.setName("chanceforMSforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("PR", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Meks Only:<br>Set Chance for a pilot to gain pain resistance</body></html>");
        baseTextField.setName("chanceforPRforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("SV", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain survivalist</body></html>");
        baseTextField.setName("chanceforSVforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("IM", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain iron man</body></html>");
        baseTextField.setName("chanceforIMforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("MA", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain maneuvering ace</body></html>");
        baseTextField.setName("chanceforMAforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("NAP", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Piloting</body></html>");
        baseTextField.setName("chanceforNAPforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("NAG", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Gunnery</body></html>");
        baseTextField.setName("chanceforNAGforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("AT", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        baseTextField.setName("chanceforATforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("TG", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain tactical genius</body></html>");
        baseTextField.setName("chanceforTGforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("WS", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain weapon specialist</body></html>");
        baseTextField.setName("chanceforWSforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("G/B", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/Ballistic</body></html>");
        baseTextField.setName("chanceforGBforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("G/L", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/laser</body></html>");
        baseTextField.setName("chanceforGLforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("G/M", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        baseTextField.setName("chanceforGMforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("Trait", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain a trait</body></html>");
        baseTextField.setName("chanceforTNforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		mekPilotSkillsSpring.add(new JLabel("EI", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Enhanced Interface</body></html>");
        baseTextField.setName("chanceforEIforMek");
		mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        mekPilotSkillsSpring.add(new JLabel("GT", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gifted</body></html>");
        baseTextField.setName("chanceforGTforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        mekPilotSkillsSpring.add(new JLabel("QS", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Quick Study</body></html>");
        baseTextField.setName("chanceforQSforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        mekPilotSkillsSpring.add(new JLabel("MT", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the Med Tech skill</body></html>");
        baseTextField.setName("chanceforMTforMek");
        mekPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        mekPilotSkillsSpring.add(new JLabel("Edge", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Allows Pilot to reroll 1 roll(per level) per game.</html>");
        baseTextField.setName("chanceforEDforMek");
        mekPilotSkillsSpring.add(baseTextField);

        vehiclePilotSkillsSpring.add(new JLabel("Vee", SwingConstants.TRAILING));
		vehiclePilotSkillsSpring.add(new JLabel("Crew Skills", SwingConstants.LEADING));

        baseTextField = new JTextField(5);
		vehiclePilotSkillsSpring.add(new JLabel("MA", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain maneuvering ace</body></html>");
        baseTextField.setName("chanceforMAforVehicle");
		vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		vehiclePilotSkillsSpring.add(new JLabel("NAP", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Piloting</body></html>");
        baseTextField.setName("chanceforNAPforVehicle");
		vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		vehiclePilotSkillsSpring.add(new JLabel("NAG", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Gunnery</body></html>");
        baseTextField.setName("chanceforNAGforVehicle");
		vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		vehiclePilotSkillsSpring.add(new JLabel("AT", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        baseTextField.setName("chanceforATforVehicle");
		vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		vehiclePilotSkillsSpring.add(new JLabel("TG", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain tactical genius</body></html>");
        baseTextField.setName("chanceforTGforVehicle");
		vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		vehiclePilotSkillsSpring.add(new JLabel("WS", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain weapon specialist</body></html>");
        baseTextField.setName("chanceforWSforVehicle");
		vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		vehiclePilotSkillsSpring.add(new JLabel("G/B", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/ballistic</body></html>");
        baseTextField.setName("chanceforGBforVehicle");
		vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		vehiclePilotSkillsSpring.add(new JLabel("G/L", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/laser</body></html>");
        baseTextField.setName("chanceforGLforVehicle");
		vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		vehiclePilotSkillsSpring.add(new JLabel("G/M", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        baseTextField.setName("chanceforGMforVehicle");
		vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		vehiclePilotSkillsSpring.add(new JLabel("Trait", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain trait</body></html>");
        baseTextField.setName("chanceforTNforVehicle");
		vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		vehiclePilotSkillsSpring.add(new JLabel("EI", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Enhanced Interface</body></html>");
        baseTextField.setName("chanceforEIforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        vehiclePilotSkillsSpring.add(new JLabel("GT", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gifted</body></html>");
        baseTextField.setName("chanceforGTforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        vehiclePilotSkillsSpring.add(new JLabel("QS", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Quick Study</body></html>");
        baseTextField.setName("chanceforQSforVehicle");
        vehiclePilotSkillsSpring.add(baseTextField);

		infantryPilotSkillsSpring.add(new JLabel("Inf", SwingConstants.TRAILING));
		infantryPilotSkillsSpring.add(new JLabel("Squad Skills", SwingConstants.LEADING));

        baseTextField = new JTextField(5);
		infantryPilotSkillsSpring.add(new JLabel("MA", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain maneuvering ace</body></html>");
        baseTextField.setName("chanceforMAforInfantry");
		infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		infantryPilotSkillsSpring.add(new JLabel("NAP", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Piloting</body></html>");
        baseTextField.setName("chanceforNAPforInfantry");
		infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		infantryPilotSkillsSpring.add(new JLabel("NAG", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Gunnery</body></html>");
        baseTextField.setName("chanceforNAGforInfantry");
		infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		infantryPilotSkillsSpring.add(new JLabel("AT", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain astech</body></html>");
        baseTextField.setName("chanceforATforInfantry");
		infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		infantryPilotSkillsSpring.add(new JLabel("TG", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain tactical genius</body></html>");
        baseTextField.setName("chanceforTGforInfantry");
		infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		infantryPilotSkillsSpring.add(new JLabel("WS", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain weapon specialist</body></html>");
        baseTextField.setName("chanceforWSforInfantry");
		infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		infantryPilotSkillsSpring.add(new JLabel("G/B", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/ballistic</body></html>");
        baseTextField.setName("chanceforGBforInfantry");
		infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		infantryPilotSkillsSpring.add(new JLabel("G/L", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/laser</body></html>");
        baseTextField.setName("chanceforGLforInfantry");
		infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		infantryPilotSkillsSpring.add(new JLabel("G/M", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        baseTextField.setName("chanceforGMforInfantry");
		infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		infantryPilotSkillsSpring.add(new JLabel("Trait", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain a trait</body></html>");
        baseTextField.setName("chanceforTNforInfantry");
		infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		infantryPilotSkillsSpring.add(new JLabel("EI", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Enhanced Interface</body></html>");
        baseTextField.setName("chanceforEIforInfantry");
		infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        infantryPilotSkillsSpring.add(new JLabel("GT", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gifted</body></html>");
        baseTextField.setName("chanceforGTforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        infantryPilotSkillsSpring.add(new JLabel("QS", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Quick Study</body></html>");
        baseTextField.setName("chanceforQSforInfantry");
        infantryPilotSkillsSpring.add(baseTextField);

		protomechPilotSkillsSpring.add(new JLabel("Proto", SwingConstants.TRAILING));
		protomechPilotSkillsSpring.add(new JLabel("Pilot Skills", SwingConstants.LEADING));

        baseTextField = new JTextField(5);
		protomechPilotSkillsSpring.add(new JLabel("MA", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain maneuvering ace</body></html>");
        baseTextField.setName("chanceforMAforProtoMek");
		protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		protomechPilotSkillsSpring.add(new JLabel("NAP", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Piloting</body></html>");
        baseTextField.setName("chanceforNAPforProtoMek");
		protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		protomechPilotSkillsSpring.add(new JLabel("NAG", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Gunnery</body></html>");
        baseTextField.setName("chanceforNAGforProtoMek");
		protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		protomechPilotSkillsSpring.add(new JLabel("AT", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain astech</body></html>");
        baseTextField.setName("chanceforATforProtoMek");
		protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		protomechPilotSkillsSpring.add(new JLabel("TG", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain tactical genius</body></html>");
        baseTextField.setName("chanceforTGforProtoMek");
		protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		protomechPilotSkillsSpring.add(new JLabel("WS", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain weapon specialist</body></html>");
        baseTextField.setName("chanceforWSforProtoMek");
		protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		protomechPilotSkillsSpring.add(new JLabel("G/B", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/ballistic</body></html>");
        baseTextField.setName("chanceforGBforProtoMek");
		protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		protomechPilotSkillsSpring.add(new JLabel("G/L", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/laser</body></html>");
        baseTextField.setName("chanceforGLforProtoMek");
		protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		protomechPilotSkillsSpring.add(new JLabel("G/M", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        baseTextField.setName("chanceforGMforProtoMek");
		protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		protomechPilotSkillsSpring.add(new JLabel("Trait", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to a trait</body></html>");
        baseTextField.setName("chanceforTNforProtoMek");
		protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		protomechPilotSkillsSpring.add(new JLabel("EI", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Enhanced Interface</body></html>");
        baseTextField.setName("chanceforEIforProtoMek");
		protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        protomechPilotSkillsSpring.add(new JLabel("GT", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gifted</body></html>");
        baseTextField.setName("chanceforGTforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        protomechPilotSkillsSpring.add(new JLabel("QS", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Quick Study</body></html>");
        baseTextField.setName("chanceforQSforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        protomechPilotSkillsSpring.add(new JLabel("MT", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain the Med Tech Skill</body></html>");
        baseTextField.setName("chanceforMTforProtoMek");
        protomechPilotSkillsSpring.add(baseTextField);

		battlearmorPilotSkillsSpring.add(new JLabel("BA", SwingConstants.TRAILING));
		battlearmorPilotSkillsSpring.add(new JLabel("Squad Skills", SwingConstants.LEADING));

        baseTextField = new JTextField(5);
		battlearmorPilotSkillsSpring.add(new JLabel("MA", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain maneuvering ace</body></html>");
        baseTextField.setName("chanceforMAforBattleArmor");
		battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		battlearmorPilotSkillsSpring.add(new JLabel("NAP", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Piloting</body></html>");
        baseTextField.setName("chanceforNAPforBattleArmor");
		battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		battlearmorPilotSkillsSpring.add(new JLabel("NAG", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Natural Aptitude Gunnery</body></html>");
        baseTextField.setName("chanceforNAGforBattleArmor");
		battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		battlearmorPilotSkillsSpring.add(new JLabel("AT", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain astech</body></html>");
        baseTextField.setName("chanceforATforBattleArmor");
		battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		battlearmorPilotSkillsSpring.add(new JLabel("TG", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain tactical genius</body></html>");
        baseTextField.setName("chanceforTGforBattleArmor");
		battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		battlearmorPilotSkillsSpring.add(new JLabel("WS", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain weapon specialist</body></html>");
        baseTextField.setName("chanceforWSforBattleArmor");
		battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		battlearmorPilotSkillsSpring.add(new JLabel("G/B", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/ballistic</body></html>");
        baseTextField.setName("chanceforGBforBattleArmor");
		battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		battlearmorPilotSkillsSpring.add(new JLabel("G/L", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/laser</body></html>");
        baseTextField.setName("chanceforGLforBattleArmor");
		battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		battlearmorPilotSkillsSpring.add(new JLabel("G/M", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gunnery/missile</body></html>");
        baseTextField.setName("chanceforGMforBattleArmor");
		battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		battlearmorPilotSkillsSpring.add(new JLabel("Trait", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain a trait</body></html>");
        baseTextField.setName("chanceforTNforBattleArmor");
		battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		battlearmorPilotSkillsSpring.add(new JLabel("EI", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Enhanced Interface</body></html>");
        baseTextField.setName("chanceforEIforBattleArmor");
		battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        battlearmorPilotSkillsSpring.add(new JLabel("GT", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain gifted</body></html>");
        baseTextField.setName("chanceforGTforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        battlearmorPilotSkillsSpring.add(new JLabel("QS", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Set Chance for a pilot to gain Quick Study</body></html>");
        baseTextField.setName("chanceforQSforBattleArmor");
        battlearmorPilotSkillsSpring.add(baseTextField);
		
        SpringLayoutHelper.setupSpringGrid(mekPilotSkillsSpring,2);
		SpringLayoutHelper.setupSpringGrid(vehiclePilotSkillsSpring,2);
		SpringLayoutHelper.setupSpringGrid(infantryPilotSkillsSpring,2);
		SpringLayoutHelper.setupSpringGrid(protomechPilotSkillsSpring,2);
		SpringLayoutHelper.setupSpringGrid(battlearmorPilotSkillsSpring,2);

		
        baseTextField = new JTextField(5);
		bannedWSWeaponsSpring.add(new JLabel("Banned WS Weapons:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html><body>Add what Weapons you do not want pilots to get Weapon Specalist in/body></html>");
        baseTextField.setName("BannedWSWeapons");
		bannedWSWeaponsSpring.add(baseTextField);

		SpringLayoutHelper.setupSpringGrid(bannedWSWeaponsSpring,2);

		//1x5 grid
		JPanel skillGrid = new JPanel(new GridLayout(1,5));
		
		skillGrid.add(mekPilotSkillsSpring);
		skillGrid.add(vehiclePilotSkillsSpring);
		skillGrid.add(infantryPilotSkillsSpring);
		skillGrid.add(protomechPilotSkillsSpring);
		skillGrid.add(battlearmorPilotSkillsSpring);
		
		mainSpring.add(skillGrid);
		mainSpring.add(bannedWSWeaponsSpring);
		
		SpringLayoutHelper.setupSpringGrid(mainSpring,2,1);
		
		pilotSkillsPanel.add(mainSpring);


		
		/*
		 * Pilot Skills Panel
		 * 
		 */
		   
		JPanel SkillModSpring = new JPanel(new SpringLayout());
		
		
        baseTextField = new JTextField(5);
		SkillModSpring.add(new JLabel("Dodge Maneuver Mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Flat BV Mod for Dodge Maneuver");
        baseTextField.setName("DodgeManeuverBaseBVMod");
		SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		SkillModSpring.add(new JLabel("Melee Specialist Mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base BV Mod for Melee Specialist");
        baseTextField.setName("MeleeSpecialistBaseBVMod");
		SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		SkillModSpring.add(new JLabel("Hatchet Mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Base BV Mod per Hatchet/Sword<br> [(Base Increase)(unit tonage/10)]<br>+(hatchet mod * number of physical weapons)</html>");
        baseTextField.setName("HatchetRating");
		SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		SkillModSpring.add(new JLabel("Pain Resistance BV Mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Base BV mod for Pain Resistance<br>a base BV increase, and this would be applied for<br>every ammo critical or<br>Gauss weapon the unit has");
        baseTextField.setName("PainResistanceBaseBVMod");
		SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		SkillModSpring.add(new JLabel("Iron Man BV Mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Base BV mod for Iron Man<br>a base BV increase, and this would be applied for<br>every ammo critical or<br>Gauss weapon the unit has");
        baseTextField.setName("IronManBaseBVMod");
		SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		SkillModSpring.add(new JLabel("MA BV Mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Maneuvering Ace Base BV mod<br>(\"Base Increase\")(\"Unit's top speed\"/\"Speed rating\")</html>");
        baseTextField.setName("ManeuveringAceBaseBVMod");
		SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		SkillModSpring.add(new JLabel("MA Speed Rating", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Maneuvering Ace Base BV mod<br>(\"Base Increase\")(\"Unit's top speed\"/\"Speed rating\")</html>");
        baseTextField.setName("ManeuveringAceSpeedRating");
		SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		SkillModSpring.add(new JLabel("Tactical Genius BV", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Flat BV amount added for Tactical Genius</html>");
        baseTextField.setName("TacticalGeniusBVMod");
		SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		SkillModSpring.add(new JLabel("Weapon Specialist Precent", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Percent of the Total BV of all<br>the weapons that the WS skill cover<br>is added on to the unit<br>Eample RFL-IIC with WS LPL<br>CLLPL has a bv of 265 if set to 100%<br>the BV add would be 1060 (265x4)*100%</html>");
        baseTextField.setName("WeaponSpecialistBaseBVMod");
		SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		SkillModSpring.add(new JLabel("G/B bv mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Total number of ballistic weapons<br>are multipled by the BV mod and added to the unit</html>");
        baseTextField.setName("GunneryBallisticBaseBVMod");
		SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		SkillModSpring.add(new JLabel("G/L bv mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Total number of laser weapons<br>are multipled by the BV mod and added to the unit</html>");
        baseTextField.setName("GunneryLaserBaseBVMod");
		SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		SkillModSpring.add(new JLabel("G/M bv mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Total number of missile weapons<br>are multipled by the BV mod and added to the unit</html>");
        baseTextField.setName("GunneryMissileBaseBVMod");
		SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		SkillModSpring.add(new JLabel("EI bv mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>BV mod added to the unit due to EI</html>");
        baseTextField.setName("EnhancedInterfaceBaseBVMod");
		SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Edge bv mod", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>BV mod added to the unit due to Edge Skill</html>");
        baseTextField.setName("EdgeBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Max Edge", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Max number of edges a pilot can have per game<br>This is akin to levels.</html>");
        baseTextField.setName("MaxEdgeChanges");
        SkillModSpring.add(baseTextField);

		SpringLayoutHelper.setupSpringGrid(SkillModSpring,4);

		pilotSkillsModPanel.add(SkillModSpring);

		/*
		 * Pilots options panel
		 */
		JPanel pilotCBoxGrid = new JPanel(new GridLayout(4,4));
		JPanel pilotOptionsSpring1 = new JPanel(new SpringLayout());
		JPanel pilotOptionsSpring2 = new JPanel(new SpringLayout());
		
		//pilotSpring1, 7 elements
        baseTextField = new JTextField(5);
		pilotOptionsSpring1.add(new JLabel("Skill Change:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("% chance for a new pilot to have a maxtech skill");
        baseTextField.setName("BornSkillChance");
		pilotOptionsSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		pilotOptionsSpring1.add(new JLabel("Skill Gain:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("% chance for a pilot to get a skill instead of a gunnery/piloting upgrade");
        baseTextField.setName("SkillLevelChance");
		pilotOptionsSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		pilotOptionsSpring1.add(new JLabel("XP Loss:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("% chance for a pilot to lose accumulated XP in the Queue");
        baseTextField.setName("ClearXPInQue");
		pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
		pilotOptionsSpring1.add(new JLabel("Cost For Mek Pilot:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Cost to buy a new Mek pilot from the faction pools<HTML>");
        baseTextField.setName("CostToBuyNewPilot");
		pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
		pilotOptionsSpring1.add(new JLabel("Max Pilots From House:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Cap for pilots in a players personal queue<br>if they have less they can purchase<br>from the faction pools<br>if Allow Players to Buy<br>with full Queues is checked");
        baseTextField.setName("MaxAllowedPilotsInQueueToBuyFromHouse");
		pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
		pilotOptionsSpring1.add(new JLabel("Base Pilot Survival:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Base Survival Rate for an ejected pilot<br>If the %planet control is less then this this<br>amount is used.</html>");
        baseTextField.setName("BasePilotSurvival");
		pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("Trapped In Mech Survival:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Unique to in-mech pilots (engine kills). Penalty"
             +"<br>for being in a stationary unit when the capture"
             +"<br>crews come around and sweep the field.</html>");
        baseTextField.setName("TrappedInMechSurvivalMod");
        pilotOptionsSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
		pilotOptionsSpring1.add(new JLabel("Convert Pilots:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>% Chance that captured pilots are converted<br>and sent to faction/player pools</html>");
        baseTextField.setName("ChanceToConvertCapturedPilots");
		pilotOptionsSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("Damage Per Hit:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Amount of damage the pilot will take per hit they receive in game<br>NOTE: This amount will be translated back into CBT hits<br>When sent back to the clients.</html>");
        baseTextField.setName("AmountOfDamagePerPilotHit");
        pilotOptionsSpring1.add(baseTextField);
        
        baseTextField = new JTextField(5);
        pilotOptionsSpring1.add(new JLabel("Cost For Proto Pilot:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>Cost to buy a new Proto pilot from the faction pools<HTML>");
        baseTextField.setName("CostToBuyNewProtoPilot");
        pilotOptionsSpring1.add(baseTextField);

		//PilotSpring2 - 7 elements
        baseTextField = new JTextField(5);
		pilotOptionsSpring2.add(new JLabel("Total Skill to Retire:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Total skill (Piloting + Gunnery) of pilot must be equal to or less than this number in order to retire for free.</html>");
        baseTextField.setName("TotalSkillForFreeRetirement");
		pilotOptionsSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		pilotOptionsSpring2.add(new JLabel("Early Retire Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" +
		        mwclient.moneyOrFluMessage(true,true,-1)+" cost PER LEVEL to retire a pilot before free. For<br>" +
				"example, if Skill to Retire is 6, a pilot is 4/5 (Total:9)<br>" +
				"and the cost is 10, it will cost (9-6)*10=30 "+mwclient.moneyOrFluMessage(true,true,-1)+" to<br>" +
				"retire the 4/5.</html>");
        baseTextField.setName("CostPerLevelToRetireEarly");
		pilotOptionsSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		pilotOptionsSpring2.add(new JLabel("Best Gunnery:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Best Gunnery Skill allowed.");
        baseTextField.setName("BestGunnerySkill");
		pilotOptionsSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		pilotOptionsSpring2.add(new JLabel("Best Piloting", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Best Piloting skill allowed.");
        baseTextField.setName("BestPilotingSkill");
		pilotOptionsSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		pilotOptionsSpring2.add(new JLabel("Best Total Pilot", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Lowest skill total (Gunnery + Piloting = Total) allowed.");
        baseTextField.setName("BestTotalPilot");
		pilotOptionsSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		pilotOptionsSpring2.add(new JLabel("Base level Up Roll", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" +
				"Basic 1dX required used for level up. If roll" +
				"is less than pilot XP, pilot gains a level.</html>");
        baseTextField.setName("BaseRollToLevel");
		pilotOptionsSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		pilotOptionsSpring2.add(new JLabel("Roll Multiplier", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" +
				"Base * Multiplier * (10 - total skill). If Base is 1000, and<br>" +
				"multiplier is 2, and skill is 3/4 (7), pilot will need to roll<br>" +
				"lower than his XP on 1d6000 (1000Base * 2Multi * 3Levels = 6000).</html>");
        baseTextField.setName("MultiplierPerPreviousLevel");
		pilotOptionsSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring2.add(new JLabel("Health per Tick", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The number of points a pilot will heal in one tick<br>NOTE: with PPQ on pilots must be in the queue to heal<br>With PPQ off pilots will heal while in their units.</html>");
        baseTextField.setName("PilotAmountHealedPerTick");
        pilotOptionsSpring2.add(baseTextField);

        baseTextField = new JTextField(5);
        pilotOptionsSpring2.add(new JLabel("MedTech per Tick", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The number of points a pilot will heal in one tick if they have the medtech skill<br>NOTE: with PPQ on pilots must be in the queue to heal<br>With PPQ off pilots will heal while in their units.</html>");
        baseTextField.setName("MedTechAmountHealedPerTick");
        pilotOptionsSpring2.add(baseTextField);

		SpringLayoutHelper.setupSpringGrid(pilotOptionsSpring1,2);
		SpringLayoutHelper.setupSpringGrid(pilotOptionsSpring2,2);
		
		//pilot cboxes
        BaseCheckBox = new JCheckBox("Elite BV Mod");
        BaseCheckBox.setToolTipText("Increase BV of units which are <2/X or X/<2 above FASA levels.");
        BaseCheckBox.setName("ElitePilotsBVMod");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("MaxTech Skills");
        BaseCheckBox.setName("PilotSkills");
        BaseCheckBox.setToolTipText("Allow MaxTech pilot skills (Manuv. Ace, Pain Resist, etc)");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Unlevel@Queue");
        BaseCheckBox.setToolTipText("<HTML>" +
				"Unchecking allows Pilots to keep skills and XP in queue" +
				"after their rides die. Disabling this is discouraged.</HTML>");
        BaseCheckBox.setName("ReduceSkillsInQue");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Green Pilots");
        BaseCheckBox.setToolTipText("Check in order to allow green pilots. 4/6, 5/5, etc.");
        BaseCheckBox.setName("AllowGreenPilots");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow PPQ");
        BaseCheckBox.setToolTipText("<HTML>Allow Personal Pilot Queues<br>Players are allowed to keep their own pilots instead of them going to the faction pools</HTML>");
        BaseCheckBox.setName("AllowPersonalPilotQueues");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Extra Pilots");
        BaseCheckBox.setToolTipText("<HTML>When checked the players can buy<br>pilots from the faction pool<br>even if they already have pilots of that<br>type/class in their pools</HTML>");
        BaseCheckBox.setName("AllowPlayerToBuyPilotsFromHouseWhenPoolIsFull");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Downed Pilots Roll");
        BaseCheckBox.setToolTipText("<HTML>When checked a downed pilot must make a survival roll<br>to see if they make it home<br>or are captured</HTML>");
        BaseCheckBox.setName("DownPilotsMustRollForSurvival");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Retirement");
        BaseCheckBox.setToolTipText("Allow players to retire their pilots.");
        BaseCheckBox.setName("PilotRetirementAllowed");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Early Retirement");
        BaseCheckBox.setToolTipText("Allow players to pay a fee in order to retire their pilots earlier than normal.");
        BaseCheckBox.setName("EarlyRetirementAllowed");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Elite Retirements");
        BaseCheckBox.setToolTipText("<html>Randomly retire elite pilots who can't level any more. Rolls to retire are<br>" +
        		"against the same target as their final level up. This automated retirement is separate<br>" +
        		"from player-initiated retirement and will work even if \"Allow Retirement\" is disabled.</html>");
        BaseCheckBox.setName("RandomRetirementOfElites");
        pilotCBoxGrid.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox("Common Names Only");
        BaseCheckBox.setToolTipText("Pilot names are only pulled from the Pilotnames.txt");
        BaseCheckBox.setName("UseCommonPilotNameFileOnly");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Random Pilot Levels");
        BaseCheckBox.setToolTipText("<html>" +
	    		"Disable to use RPG style pilot levelling. Pilots must gain<br>" +
	    		"Base * Multiplier * (10-Skill) XP to reach next level.<br>" +
	    		"Random roll to level up is removed - only raw XP is used.</html>");
        BaseCheckBox.setName("UseRandomPilotLevelups");
        pilotCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Pilot Damage Transfers");
        BaseCheckBox.setToolTipText("<html>If a pilot takes damage in a game it'll transfer back to the campaign<br>and the pilot will need to heal up.</html>");
        BaseCheckBox.setName("AllowPilotDamageToTransfer");
        pilotCBoxGrid.add(BaseCheckBox);
		
		//finalize the layout
		JPanel pilotBox = new JPanel(new SpringLayout());
		JPanel pilotFlow = new JPanel();
		pilotFlow.add(pilotOptionsSpring1);
		pilotFlow.add(pilotOptionsSpring2);
		pilotBox.add(pilotFlow);
		pilotBox.add(pilotCBoxGrid);

		SpringLayoutHelper.setupSpringGrid(pilotBox,2,1);
		
		pilotsPanel.add(pilotBox);
		
		/*
		 * UNITS PANEL CONSTRUCTION.
		 * 
		 * Unit options.
		 */
		JPanel mekCbillsSpring = new JPanel(new SpringLayout());
		JPanel mekFluSpring = new JPanel(new SpringLayout());
		JPanel mekComponentSpring = new JPanel(new SpringLayout());
		JPanel vehCbillsSpring = new JPanel(new SpringLayout());
		JPanel vehFluSpring = new JPanel(new SpringLayout());
		JPanel vehComponentSpring = new JPanel(new SpringLayout());
		JPanel infCbillsSpring = new JPanel(new SpringLayout());
		JPanel infFluSpring = new JPanel(new SpringLayout());
		JPanel infComponentSpring = new JPanel(new SpringLayout());
		
		JPanel unitSpringGrid = new JPanel(new GridLayout(3,3));
		unitSpringGrid.add(mekCbillsSpring);
		unitSpringGrid.add(mekFluSpring);
		unitSpringGrid.add(mekComponentSpring);
		unitSpringGrid.add(vehCbillsSpring);
		unitSpringGrid.add(vehFluSpring);
		unitSpringGrid.add(vehComponentSpring);
		unitSpringGrid.add(infCbillsSpring);
		unitSpringGrid.add(infFluSpring);
		unitSpringGrid.add(infComponentSpring);
		
		JPanel unitsMiscSpring = new JPanel(new SpringLayout());
		
		JPanel unitCBoxGrid = new JPanel(new GridLayout(3,4));
		
		//MEKs
        baseTextField = new JTextField(5);
		mekCbillsSpring.add(new JLabel("Light Mek Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for a light mek.");
        baseTextField.setName("LightPrice");
		mekCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		mekCbillsSpring.add(new JLabel("Medium Mek Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for a medium mek.");
        baseTextField.setName("MediumPrice");
		mekCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		mekCbillsSpring.add(new JLabel("Heavy Mek Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for a heavy mek.");
        baseTextField.setName("HeavyPrice");
		mekCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		mekCbillsSpring.add(new JLabel("Assault Mek Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for an assault mek.");
        baseTextField.setName("AssaultPrice");
		mekCbillsSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(mekCbillsSpring,4,2);
		
        baseTextField = new JTextField(5);
		mekFluSpring.add(new JLabel("Light Mek Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for a light mek.");
        baseTextField.setName("LightInf");
		mekFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		mekFluSpring.add(new JLabel("Medium Mek Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for a medium mek.");
        baseTextField.setName("MediumInf");
		mekFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		mekFluSpring.add(new JLabel("Heavy Mek Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for a heavy mek.");
        baseTextField.setName("HeavyInf");
		mekFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		mekFluSpring.add(new JLabel("Assault Mek Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for an assault mek.");
        baseTextField.setName("AssaultInf");
		mekFluSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(mekFluSpring,4,2);
		
        baseTextField = new JTextField(5);
		mekComponentSpring.add(new JLabel("Light Mek PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct a light mek.");
        baseTextField.setName("LightPP");
		mekComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		mekComponentSpring.add(new JLabel("Medium Mek PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct a medium mek.");
        baseTextField.setName("MediumPP");
		mekComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		mekComponentSpring.add(new JLabel("Heavy Mek PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct a heavy mek.");
        baseTextField.setName("HeavyPP");
		mekComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		mekComponentSpring.add(new JLabel("Assault Mek PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct an assault mek.");
        baseTextField.setName("AssaultPP");
		mekComponentSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(mekComponentSpring,4,2);
		
		//VEHICLES
        baseTextField = new JTextField(5);
		vehCbillsSpring.add(new JLabel("Light Veh Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for a light veh.");
        baseTextField.setName("LightVehiclePrice");
		vehCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		vehCbillsSpring.add(new JLabel("Medium Veh Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for a medium veh.");
        baseTextField.setName("MediumVehiclePrice");
		vehCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		vehCbillsSpring.add(new JLabel("Heavy Veh Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for a heavy veh.");
        baseTextField.setName("HeavyVehiclePrice");
		vehCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		vehCbillsSpring.add(new JLabel("Assault Veh Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for an assault veh.");
        baseTextField.setName("AssaultVehiclePrice");
		vehCbillsSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(vehCbillsSpring,4,2);
		
        baseTextField = new JTextField(5);
		vehFluSpring.add(new JLabel("Light Veh Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for a light veh.");
        baseTextField.setName("LightVehicleInf");
		vehFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		vehFluSpring.add(new JLabel("Medium Veh Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for a medium veh.");
        baseTextField.setName("MediumVehicleInf");
		vehFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		vehFluSpring.add(new JLabel("Heavy Veh Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for a heavy veh.");
        baseTextField.setName("HeavyVehicleInf");
		vehFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		vehFluSpring.add(new JLabel("Assault Veh Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for an assault veh.");
        baseTextField.setName("AssaultVehicleInf");
		vehFluSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(vehFluSpring,4,2);
		
        baseTextField = new JTextField(5);
		vehComponentSpring.add(new JLabel("Light Veh PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct a light veh.");
        baseTextField.setName("LightVehiclePP");
		vehComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		vehComponentSpring.add(new JLabel("Medium Veh PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct a medium veh.");
        baseTextField.setName("MediumVehiclePP");
		vehComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		vehComponentSpring.add(new JLabel("Heavy Veh PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct a heavy veh.");
        baseTextField.setName("HeavyVehiclePP");
		vehComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		vehComponentSpring.add(new JLabel("Assault Veh PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to construct an assault veh.");
        baseTextField.setName("AssaultVehiclePP");
		vehComponentSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(vehComponentSpring,4,2);
		
		//INFANTRY
        baseTextField = new JTextField(5);
		infCbillsSpring.add(new JLabel("Light Inf Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for light infantry.");
        baseTextField.setName("LightInfantryPrice");
		infCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		infCbillsSpring.add(new JLabel("Medium Inf Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for medium infantry.");
        baseTextField.setName("MediumInfantryPrice");
		infCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		infCbillsSpring.add(new JLabel("Heavy Inf Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for heavy infantry.");
        baseTextField.setName("HeavyInfantryPrice");
		infCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		infCbillsSpring.add(new JLabel("Assault Inf Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for assault infantry.");
        baseTextField.setName("AssaultInfantryPrice");
		infCbillsSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(infCbillsSpring,4,2);
		
        baseTextField = new JTextField(5);
		infFluSpring.add(new JLabel("Light Inf Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for light infantry.");
        baseTextField.setName("LightInfantryInf");
		infFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		infFluSpring.add(new JLabel("Medium Inf Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for medium infantry.");
        baseTextField.setName("MediumInfantryInf");
		infFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		infFluSpring.add(new JLabel("Heavy Inf Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for heavy infantry.");
        baseTextField.setName("HeavyInfantryInf");
		infFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		infFluSpring.add(new JLabel("Assault Inf Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for assault infantry.");
        baseTextField.setName("AssaultInfantryInf");
		infFluSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(infFluSpring,4,2);
		
        baseTextField = new JTextField(5);
		infComponentSpring.add(new JLabel("Light Inf PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make light infantry.");
        baseTextField.setName("LightInfantryPP");
		infComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		infComponentSpring.add(new JLabel("Medium Inf PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make medium infantry.");
        baseTextField.setName("MediumInfantryPP");
		infComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		infComponentSpring.add(new JLabel("Heavy Inf PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make heavy infantry.");
        baseTextField.setName("HeavyInfantryPP");
		infComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		infComponentSpring.add(new JLabel("Assault Inf PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make assault infantry.");
        baseTextField.setName("AssaultInfantryPP");
		infComponentSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(infComponentSpring,4,2);
		
		//set up the Misc. spring
        baseTextField = new JTextField(5);
		unitsMiscSpring.add(new JLabel("Max Armies:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max # of armies a unit can join");
        baseTextField.setName("UnitsInMultipleArmiesAmount");
		unitsMiscSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        unitsMiscSpring.add(new JLabel("Cost Multiplier:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>This is set to lower or raise the calculated cost.<br>i.e. cost is 10 mil for a unit .1(10%) will set it to 1 mil.</html>");
        baseTextField.setName("CostModifier");
        unitsMiscSpring.add(baseTextField);

		SpringLayoutHelper.setupSpringGrid(unitsMiscSpring,4);
		
		//unit cboxes
        BaseCheckBox = new JCheckBox("Use Vehs");

        BaseCheckBox.setToolTipText("Uncheck to disable Vehs.");
        BaseCheckBox.setName("UseVehicle");
        unitCBoxGrid.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Use Inf");

        BaseCheckBox.setToolTipText("Uncheck to disable Infantry.");
        BaseCheckBox.setName("UseInfantry");
        unitCBoxGrid.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Light Inf");

        BaseCheckBox.setToolTipText("Check to have all inf count as light.");
        BaseCheckBox.setName("UseOnlyLightInfantry");
        unitCBoxGrid.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Light Vehs");

        BaseCheckBox.setToolTipText("Check to have all vehs count as light.");
        BaseCheckBox.setName("UseOnlyOneVehicleSize");
        unitCBoxGrid.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Free Foot");

        BaseCheckBox.setToolTipText("Check to have Foot Inf take 0 techs/bays");
        BaseCheckBox.setName("FootInfTakeNoBays");
        unitCBoxGrid.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Use BA");

        BaseCheckBox.setToolTipText("Uncheck to disable BattleArmor.");
        BaseCheckBox.setName("UseBattleArmor");
        unitCBoxGrid.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Use Proto");

        BaseCheckBox.setToolTipText("Uncheck to disable ProtoMeks.");
        BaseCheckBox.setName("UseProtoMek");
        unitCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Real Cost");

        BaseCheckBox.setToolTipText("<html>Check to use MM/FASA calculated costs for each unit.<br>Requires a reboot of the server.<br>Note MM does not calculate costs for some infantry and all protos.</html>");
        BaseCheckBox.setEnabled(false);
        BaseCheckBox.setName("UseCalculatedCosts");
        unitCBoxGrid.add(BaseCheckBox);
        
		//finalize the layout
		JPanel unitBox = new JPanel();
		unitBox.setLayout(new BoxLayout(unitBox, BoxLayout.Y_AXIS));
		
		unitBox.add(unitSpringGrid);
		unitBox.add(unitsMiscSpring);
		unitBox.add(unitCBoxGrid);
	
		unitPanel.add(unitBox);


		//Units 2
		//3x2 grod of springs filled w/ component costs
		JPanel protoCbillsSpring = new JPanel(new SpringLayout());
		JPanel protoFluSpring = new JPanel(new SpringLayout());
		JPanel protoComponentSpring = new JPanel(new SpringLayout());
		JPanel baCbillsSpring = new JPanel(new SpringLayout());
		JPanel baFluSpring = new JPanel(new SpringLayout());
		JPanel baComponentSpring = new JPanel(new SpringLayout());
		JPanel unitCommanderSpring = new JPanel(new SpringLayout());
		
		JPanel unit2SpringGrid = new JPanel(new GridLayout(2,3));
		unit2SpringGrid.add(protoCbillsSpring);
		unit2SpringGrid.add(protoFluSpring);
		unit2SpringGrid.add(protoComponentSpring);

		unit2SpringGrid.add(baCbillsSpring);
		unit2SpringGrid.add(baFluSpring);
		unit2SpringGrid.add(baComponentSpring);
		
        baseTextField = new JTextField(5);
		protoCbillsSpring.add(new JLabel("Light Proto Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for light proto.");
        baseTextField.setName("LightProtoMekPrice");
		protoCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		protoCbillsSpring.add(new JLabel("Medium Proto Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for medium proto.");
        baseTextField.setName("MediumProtoMekPrice");
		protoCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		protoCbillsSpring.add(new JLabel("Heavy Proto Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for heavy proto.");
        baseTextField.setName("HeavyProtoMekPrice");
		protoCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		protoCbillsSpring.add(new JLabel("Assault Proto Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for assault proto.");
        baseTextField.setName("AssaultProtoMekPrice");
		protoCbillsSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(protoCbillsSpring,4,2);
		
        baseTextField = new JTextField(5);
		protoFluSpring.add(new JLabel("Light Proto Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for light proto.");
        baseTextField.setName("LightProtoMekInf");
		protoFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		protoFluSpring.add(new JLabel("Medium Proto Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for medium proto.");
        baseTextField.setName("MediumProtoMekInf");
		protoFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		protoFluSpring.add(new JLabel("Heavy Proto Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for heavy proto.");
        baseTextField.setName("HeavyProtoMekInf");
		protoFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		protoFluSpring.add(new JLabel("Assault Proto Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for assault proto.");
        baseTextField.setName("AssaultProtoMekInf");
		protoFluSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(protoFluSpring,4,2);
		
        baseTextField = new JTextField(5);
		protoComponentSpring.add(new JLabel("Light Proto PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make light proto.");
        baseTextField.setName("LightProtoMekPP");
		protoComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		protoComponentSpring.add(new JLabel("Medium Proto PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make medium proto.");
        baseTextField.setName("MediumProtoMekPP");
		protoComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		protoComponentSpring.add(new JLabel("Heavy Proto PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make heavy proto.");
        baseTextField.setName("HeavyProtoMekPP");
		protoComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		protoComponentSpring.add(new JLabel("Assault Proto PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make assault proto.");
        baseTextField.setName("AssaultProtoMekPP");
		protoComponentSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(protoComponentSpring,4,2);
		
        baseTextField = new JTextField(5);
		baCbillsSpring.add(new JLabel("Light BA Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for light ba.");
        baseTextField.setName("LightBattleArmorPrice");
		baCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		baCbillsSpring.add(new JLabel("Medium BA Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for medium ba.");
        baseTextField.setName("MediumBattleArmorPrice");
		baCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		baCbillsSpring.add(new JLabel("Heavy BA Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for heavy ba.");
        baseTextField.setName("HeavyBattleArmorPrice");
		baCbillsSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		baCbillsSpring.add(new JLabel("Assault BA Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base "+mwclient.moneyOrFluMessage(true,true,-1)+" for assault ba.");
        baseTextField.setName("AssaultBattleArmorPrice");
		baCbillsSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(baCbillsSpring,4,2);
		
        baseTextField = new JTextField(5);
		baFluSpring.add(new JLabel("Light BA Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for light battlearmor.");
        baseTextField.setName("LightBattleArmorInf");
		baFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		baFluSpring.add(new JLabel("Medium BA Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for medium battlearmor.");
        baseTextField.setName("MediumBattleArmorInf");
		baFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		baFluSpring.add(new JLabel("Heavy BA Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for heavy battlearmor.");
        baseTextField.setName("HeavyBattleArmorInf");
		baFluSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		baFluSpring.add(new JLabel("Assault BA Flu:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base flu for assault battlearmor.");
        baseTextField.setName("AssaultBattleArmorInf");
		baFluSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(baFluSpring,4,2);
		
        baseTextField = new JTextField(5);
		baComponentSpring.add(new JLabel("Light BA PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make light battlearmor.");
        baseTextField.setName("LightBattleArmorPP");
		baComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		baComponentSpring.add(new JLabel("Medium BA PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make medium battlearmor.");
        baseTextField.setName("MediumBattleArmorPP");
		baComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		baComponentSpring.add(new JLabel("Heavy BA PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make heavy battlearmor.");
        baseTextField.setName("HeavyBattleArmorPP");
		baComponentSpring.add(baseTextField);
		
        baseTextField = new JTextField(5);
		baComponentSpring.add(new JLabel("Assault BA PP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Base components to make assault battlearmor.");
        baseTextField.setName("AssaultBattleArmorPP");
		baComponentSpring.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(baComponentSpring,4,2);

		//flow layout of labels and text boxes w/ non-original multipliers
		JPanel unit2TextFlow = new JPanel();
		
		baseTextField = new JTextField(5);
		unit2TextFlow.add(new JLabel("NonOrig Money Multi:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" +
        		"Cost multiplier for units purchased from a factory not originally<br>" +
        		"owned by purchasing player's faction. Examples:<br>" +
        		"1: 80 CBill Faction Base * 1.15 CBillMultiplier = 92 CBill final cost.<br>" +
        		"2: 80 CBill Faction Base * 1.00 CBillMultiplier = 80 CBill final cost.<br>" +
        		"3: 80 CBill Faction Base * 0.75 CBillMultiplier = 60 CBill final cost.</html>");
        baseTextField.setName("NonOriginalCBillMultiplier");
        unit2TextFlow.add(baseTextField);
		
		baseTextField = new JTextField(5);
		unit2TextFlow.add(new JLabel("NonOrig Flu Multi:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" +
        		"Flu price multiplier for units purchased from a factory not originally<br>" +
        		"owned by purchasing player's faction. See Money Multi for examples.</html>");
        baseTextField.setName("NonOriginalInfluenceMultiplier");
        unit2TextFlow.add(baseTextField);
        
        baseTextField = new JTextField(5);
		unit2TextFlow.add(new JLabel("NonOrig PP Multi:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" +
        		"Component use multiplier for units purchased from a factory not originally<br>" +
        		"owned by purchasing player's faction. See Money Multi for examples.</html>");
        baseTextField.setName("NonOriginalComponentMultiplier");
        unit2TextFlow.add(baseTextField);
		
		
        BaseCheckBox = new JCheckBox("Allow Mek Commanders");
        BaseCheckBox.setToolTipText("<html>Allow meks to be set as unit commanders<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowUnitCommanderMek");
        unitCommanderSpring.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox("Allow Vee Commanders");
        BaseCheckBox.setToolTipText("<html>Allow vehicles to be set as unit commanders<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowUnitCommanderVehicle");
        unitCommanderSpring.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox("Allow Inf Commanders");
        BaseCheckBox.setToolTipText("<html>Allow infantry to be set as unit commanders<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowUnitCommanderInfantry");
        unitCommanderSpring.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox("Allow Proto Commanders");
        BaseCheckBox.setToolTipText("<html>Allow protomeks to be set as unit commanders<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowUnitCommanderProtoMek");
        unitCommanderSpring.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox("Allow BA Commanders");
        BaseCheckBox.setToolTipText("<html>Allow battlearmor to be set as unit commanders<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowUnitCommanderBattleArmor");
        unitCommanderSpring.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox("Allow VTOL Commanders");
        BaseCheckBox.setToolTipText("<html>Allow VTOL to be set as unit commanders<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowUnitCommanderVTOL");
        unitCommanderSpring.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox("Allow Fighting Without Commanders");
        BaseCheckBox.setToolTipText("<html>Allow players to go active without any unit commanders set in their armies<br>for the kill all unit commanders operation victory condition</html>");
        BaseCheckBox.setName("allowGoingActiveWithoutUnitCommanders");
        unitCommanderSpring.add(BaseCheckBox);
		SpringLayoutHelper.setupSpringGrid(unitCommanderSpring,3);

        //build complete panel, wrapped in box
        JPanel unit2Box = new JPanel();
        unit2Box.setLayout(new BoxLayout(unit2Box, BoxLayout.Y_AXIS));
        unit2Box.add(unit2SpringGrid);
        unit2Box.add(unit2TextFlow);
        unit2Box.add(unitCommanderSpring);
        
		unit2Panel.add(unit2Box);
		
		/*
		 * CONSTRUCT MEZZO/Pricemod PANEL
		 */
		JPanel MekSpring = new JPanel(new SpringLayout());
		JPanel VehicleSpring = new JPanel(new SpringLayout());
		JPanel InfantrySpring = new JPanel(new SpringLayout());
		JPanel BattleArmorSpring = new JPanel(new SpringLayout());
		JPanel ProtoMekSpring = new JPanel(new SpringLayout());
		
		JPanel buySellSpring =  new JPanel(new SpringLayout());
		
        baseTextField = new JTextField(5);
		MekSpring.add(new JLabel("Light Mek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Mek.");
        baseTextField.setName("SellDirectLightMekPrice");
		MekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		MekSpring.add(new JLabel("Medium Mek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Mek.");
        baseTextField.setName("SellDirectMediumMekPrice");
		MekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		MekSpring.add(new JLabel("Heavy Mek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Mek.");
        baseTextField.setName("SellDirectHeavyMekPrice");
		MekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		MekSpring.add(new JLabel("Assault Mek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Mek.");
        baseTextField.setName("SellDirectAssaultMekPrice");
		MekSpring.add(baseTextField);

		SpringLayoutHelper.setupSpringGrid(MekSpring, 4, 2);

        baseTextField = new JTextField(5);
		VehicleSpring.add(new JLabel("Light Vehicle Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Vehicle.");
        baseTextField.setName("SellDirectLightVehiclePrice");
		VehicleSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		VehicleSpring.add(new JLabel("Medium Vehicle Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Vehicle.");
        baseTextField.setName("SellDirectMediumVehiclePrice");
		VehicleSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		VehicleSpring.add(new JLabel("Heavy Vehicle Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Vehicle.");
        baseTextField.setName("SellDirectHeavyVehiclePrice");
		VehicleSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		VehicleSpring.add(new JLabel("Assault Vehicle Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Vehicle.");
        baseTextField.setName("SellDirectAssaultVehiclePrice");
		VehicleSpring.add(baseTextField);

		SpringLayoutHelper.setupSpringGrid(VehicleSpring, 4, 2);

        baseTextField = new JTextField(5);
		InfantrySpring.add(new JLabel("Light Infantry Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Infantry.");
        baseTextField.setName("SellDirectLightInfantryPrice");
        InfantrySpring.add(baseTextField);

        baseTextField = new JTextField(5);
		InfantrySpring.add(new JLabel("Medium Infantry Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Infantry.");
        baseTextField.setName("SellDirectMediumInfantryPrice");
		InfantrySpring.add(baseTextField);

        baseTextField = new JTextField(5);
		InfantrySpring.add(new JLabel("Heavy Infantry Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Infantry.");
        baseTextField.setName("SellDirectHeavyInfantryPrice");
		InfantrySpring.add(baseTextField);

        baseTextField = new JTextField(5);
		InfantrySpring.add(new JLabel("Assault Infantry Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a Infantry.");
        baseTextField.setName("SellDirectAssaultInfantryPrice");
		InfantrySpring.add(baseTextField);

		SpringLayoutHelper.setupSpringGrid(InfantrySpring, 4, 2);

        baseTextField = new JTextField(5);
		BattleArmorSpring.add(new JLabel("Light BattleArmor Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a BattleArmor.");
        baseTextField.setName("SellDirectLightBattleArmorPrice");
		BattleArmorSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		BattleArmorSpring.add(new JLabel("Medium BattleArmor Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a BattleArmor.");
        baseTextField.setName("SellDirectMediumBattleArmorPrice");
		BattleArmorSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		BattleArmorSpring.add(new JLabel("Heavy BattleArmor Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a BattleArmor.");
        baseTextField.setName("SellDirectHeavyBattleArmorPrice");
		BattleArmorSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		BattleArmorSpring.add(new JLabel("Assault BattleArmor Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a BattleArmor.");
        baseTextField.setName("SellDirectAssaultBattleArmorPrice");
		BattleArmorSpring.add(baseTextField);

		SpringLayoutHelper.setupSpringGrid(BattleArmorSpring, 4, 2);

        baseTextField = new JTextField(5);
		ProtoMekSpring.add(new JLabel("Light ProtoMek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a ProtoMek.");
        baseTextField.setName("SellDirectLightProtoMekPrice");
		ProtoMekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		ProtoMekSpring.add(new JLabel("Medium ProtoMek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a ProtoMek.");
        baseTextField.setName("SellDirectMediumProtoMekPrice");
		ProtoMekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		ProtoMekSpring.add(new JLabel("Heavy ProtoMek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a ProtoMek.");
        baseTextField.setName("SellDirectHeavyProtoMekPrice");
		ProtoMekSpring.add(baseTextField);

        baseTextField = new JTextField(5);
		ProtoMekSpring.add(new JLabel("Assault ProtoMek Pricemod:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount added to pricemod to direct sell a ProtoMek.");
        baseTextField.setName("SellDirectAssaultProtoMekPrice");
		ProtoMekSpring.add(baseTextField);

		SpringLayoutHelper.setupSpringGrid(ProtoMekSpring, 4, 2);

		buySellSpring.add(MekSpring);
		buySellSpring.add(VehicleSpring);
		buySellSpring.add(InfantrySpring);
		buySellSpring.add(BattleArmorSpring);
		buySellSpring.add(ProtoMekSpring);
		
		SpringLayoutHelper.setupSpringGrid(buySellSpring , 2, 3);

		JPanel buySellSpring2 =  new JPanel();
		buySellSpring2.setLayout(new BoxLayout(buySellSpring2,BoxLayout.Y_AXIS));
		
        //finalize layout
        BaseCheckBox = new JCheckBox("Use Direct Sell");
        BaseCheckBox.setName("UseDirectSell");
        
		buySellSpring2.add(BaseCheckBox);
		buySellSpring2.add(buySellSpring);

		//SpringLayoutHelper.setupSpringGrid(buySellSpring2 , 1, 3);
		directSellPanel.add(buySellSpring2);
		
		/*
		 * FACTION TAB CONSTRUCTION
		 */
		JPanel factionSpring1 = new JPanel(new SpringLayout());
		JPanel factionSpring2 = new JPanel(new SpringLayout());
		
        baseTextField = new JTextField(5);
		factionSpring1.add(new JLabel("Medium XP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("XP required to buy medium units");
        baseTextField.setName("MinEXPforMedium");
		factionSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		factionSpring1.add(new JLabel("Heavy XP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("XP required to buy heavy units");
        baseTextField.setName("MinEXPforHeavy");
		factionSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		factionSpring1.add(new JLabel("Assault XP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("XP required to buy assault units");
        baseTextField.setName("MinEXPforAssault");
		factionSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		factionSpring1.add(new JLabel("Min House Techs:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" +
				"Minimum number of faction techs. Assigned to player<br>" +
				"*if* faction supplied bays are lower than the min.");
        baseTextField.setName("MinimumHouseBays");
		factionSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		factionSpring1.add(new JLabel("EXP for Bay:",SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount of experience a player needs to gain 1 bay.");
        baseTextField.setName("ExperienceForBay");
		factionSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		factionSpring1.add(new JLabel("Max Bays from EXP:",SwingConstants.TRAILING));
        baseTextField.setToolTipText("Maximum number of bays a player may get from experience.");
        baseTextField.setName("MaxBaysFromEXP");
		factionSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		factionSpring1.add(new JLabel("Donations Allowed:",SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of donations players are allowed each tick.");
        baseTextField.setName("DonationsAllowed");
		factionSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		factionSpring1.add(new JLabel("Scraps Allowed:",SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of scraps players are allowed each tick.");
        baseTextField.setName("ScrapsAllowed");
		factionSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		factionSpring1.add(new JLabel("Max MOTD Length:",SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max number of characters allowed in the MOTD.");
        baseTextField.setName("MaxMOTDLength");
		factionSpring1.add(baseTextField);
		
		SpringLayoutHelper.setupSpringGrid(factionSpring1, 2);
		
		//faction spring #2
        baseTextField = new JTextField(5);
		factionSpring2.add(new JLabel("Welfare Ceiling:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max # of Cbills a player can have to collect welfare");
        baseTextField.setName("WelfareCeiling");
		factionSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		factionSpring2.add(new JLabel("Total Hangar BV for Welfare:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("The Max BV a player can have to collect welfare.");
        baseTextField.setName("WelfareTotalUnitBVCeiling");
		factionSpring2.add(baseTextField);
		
		baseTextField = new JTextField(5);
		factionSpring2.add(new JLabel("Cost Multi @ Donate:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" +
        		"Cost to donate a unit is determined by multiplying the faction's base purchase<br>" +
        		"cost by this value. Negative numbers will pay the player.<br>" +
        		"Example: Purchase cost of 100 CBills * Multi of .25 = Pay 25 CB to donate.<br>" +
        		"Example: Purchase cost of 50 CBills * Multi of 0 = Free to donate.<br>" +
        		"Example: Purchase cost of 80 CBills * Multi of -.10 = Get paid 8 CB.</html>");
        baseTextField.setName("DonationCostMultiplier");
		factionSpring2.add(baseTextField);
		
		baseTextField = new JTextField(5);
		factionSpring2.add(new JLabel("Cost Multi to Buy Used:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" +
        		"Cost to purchase a used unit, determined by multiplying a unit's new purchase<br>" +
        		"cost by this value. Final cost cannot be negative.<br>" +
        		"Example: New cost of 100 CBills * Multi of .50 = Costs 50 to buy used.<br>" +
        		"Example: Purchase cost of 50 CBills * Multi of 0 = Free to buy used.</html>");
        baseTextField.setName("UsedPurchaseCostMulti");
		factionSpring2.add(baseTextField);
		
		baseTextField = new JTextField(5);
		factionSpring2.add(new JLabel("Cost Multi @ Scrap:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>" +
        		"Cost to scrap a unit is determined by multiplying the faction's base purchase<br>" +
        		"cost by this value. Negative numbers will pay the player. This value is used for<br>" +
        		"all units if AR is off, and for fully repaired meks if AR is on." +
        		"Example: Purchase cost of 100 CBills * Multi of .50 = Pay 50 CB to scrap.<br>" +
        		"Example: Purchase cost of 75 CBills * Multi of 0.0 = Free to scrap.<br>" +
        		"Example: Purchase cost of 80 CBills * Multi of -.25 = Get paid 20 CB.</html>");
        baseTextField.setName("ScrapCostMultiplier");
		factionSpring2.add(baseTextField);
        
        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Armor Scrap Cost:",SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Float field, AR Only<br>Percent of a unit's buy price to charge someone for scrapping a unit with minor armor damage<br>Negative number will give money to the player<br>.1 = 10%</html>");
        baseTextField.setName("CostToScrapOnlArmorDamage");
        factionSpring2.add(baseTextField);
        
        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Critical Scrap Cost:",SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Float field, AR Only<br>Percent of a unit's buy price to charge someone for scrapping a unit with damaged criticals<br>Negative number will give money to the player<br>.1 = 10%</html>");
        baseTextField.setName("CostToScrapCriticallyDamaged");
        factionSpring2.add(baseTextField);
        
        baseTextField = new JTextField(5);
        factionSpring2.add(new JLabel("Engined:",SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Float field, AR Only<br>Percent of a unit's buy price to charge someone for scrapping an engined unit<br>Negative number will give money to the player<br>.1 = 10%</html>");
        baseTextField.setName("CostToScrapEngined");
        factionSpring2.add(baseTextField);
        
        baseTextField = new JTextField(20);
        factionSpring2.add(new JLabel("Forum Group Name:",SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Database with phpBB Integration only</html>");
        baseTextField.setName("ForumGroupName");
        factionSpring2.add(baseTextField);
        
		SpringLayoutHelper.setupSpringGrid(factionSpring2,2);
		
		//setup CBoxes
		JPanel factionCBoxSpring = new JPanel(new SpringLayout());
				
        BaseCheckBox = new JCheckBox("Donate @ Unenroll");
        BaseCheckBox.setToolTipText("<html>If checked, players that unenroll will donate<br>all their units to the house bays.</html>");
        BaseCheckBox.setName("DonateUnitsUponUnenrollment");
        factionCBoxSpring.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox("Faction Names on Games");
        BaseCheckBox.setToolTipText("<html>If checked, faction names will replace player names in<br>completed game descriptions.</html>");
        BaseCheckBox.setName("ShowCompleteGameInfoOnTick");
        factionCBoxSpring.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox("Faction Names in News");
        BaseCheckBox.setToolTipText("<html>If checked, faction names will replace player names in<br>news feed description of games.</html>");
        BaseCheckBox.setName("ShowCompleteGameInfoInNews");
        factionCBoxSpring.add(BaseCheckBox);
              
        BaseCheckBox = new JCheckBox("Auto Promote Sub Factions");
        BaseCheckBox.setToolTipText("<html>If checked, a player will be automatically promoted<br>to the next higher sub faction,<br>if they are qualified.</html>");
        BaseCheckBox.setName("autoPromoteSubFaction");
        factionCBoxSpring.add(BaseCheckBox);
              
        SpringLayoutHelper.setupSpringGrid(factionCBoxSpring,3);

		//finalize the layout
		JPanel factionBox = new JPanel();
		factionBox.setLayout(new BoxLayout(factionBox, BoxLayout.Y_AXIS));
		JPanel factionSpringFlow = new JPanel();
		factionSpringFlow.add(factionSpring1);
		factionSpringFlow.add(factionSpring2);
		factionBox.add(factionSpringFlow);
		factionBox.add(factionCBoxSpring);
		factionPanel.add(factionBox);
		
		/*
		 * PRODUCTION/FACTORY PANEL CONSTRUCTION
		 */
		JPanel refreshSpringPanel = new JPanel(new SpringLayout());
		JPanel salesSpringPanel = new JPanel(new SpringLayout());
		JPanel apSpringPanel = new JPanel(new SpringLayout());
		JPanel prodMiscPanel = new JPanel(new SpringLayout());
		JPanel prodCBoxSpring = new JPanel(new SpringLayout());
		
		//refresh spring
        baseTextField = new JTextField(5);
		refreshSpringPanel.add(new JLabel("Light Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Miniticks to refresh a light factory");
        baseTextField.setName("LightRefresh");
		refreshSpringPanel.add(baseTextField);	
		
        baseTextField = new JTextField(5);
		refreshSpringPanel.add(new JLabel("Medium Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Miniticks to refresh a medium factory");
        baseTextField.setName("MediumRefresh");
		refreshSpringPanel.add(baseTextField);
		
        baseTextField = new JTextField(5);
		refreshSpringPanel.add(new JLabel("Heavy Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Miniticks to refresh a heavy factory");
        baseTextField.setName("HeavyRefresh");
		refreshSpringPanel.add(baseTextField);
		
        baseTextField = new JTextField(5);
		refreshSpringPanel.add(new JLabel("Assault Refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Miniticks to refresh a assault factory");
        baseTextField.setName("AssaultRefresh");
		refreshSpringPanel.add(baseTextField);
		
		//sales spring
        baseTextField = new JTextField(5);
		salesSpringPanel.add(new JLabel("Light Sale Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Length of faction bm sale of light unit, in ticks");
        baseTextField.setName("LightSaleTicks");
		salesSpringPanel.add(baseTextField);	
		
        baseTextField = new JTextField(5);
		salesSpringPanel.add(new JLabel("Medium Sale Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Length of faction bm sale of medium unit, in ticks");
        baseTextField.setName("MediumSaleTicks");
		salesSpringPanel.add(baseTextField);
		
        baseTextField = new JTextField(5);
		salesSpringPanel.add(new JLabel("Heavy Sale Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Length of faction bm sale of heavy unit, in ticks");
        baseTextField.setName("HeavySaleTicks");
		salesSpringPanel.add(baseTextField);
		
        baseTextField = new JTextField(5);
		salesSpringPanel.add(new JLabel("Assault Sale Time:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Length of faction bm sale of assault unit, in ticks");
        baseTextField.setName("AssaultSaleTicks");
		salesSpringPanel.add(baseTextField);	
		
		//autoproduction spring
        baseTextField = new JTextField(5);
		apSpringPanel.add(new JLabel("Lights to AP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of units worth of stored components to trigger an AP attempt for light units");
        baseTextField.setName("APAtMaxLightUnits");
		apSpringPanel.add(baseTextField);	
		
        baseTextField = new JTextField(5);
		apSpringPanel.add(new JLabel("Mediums to AP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of units worth of stored components to trigger an AP attempt for medium units");
        baseTextField.setName("APAtMaxMediumUnits");
		apSpringPanel.add(baseTextField);
		
        baseTextField = new JTextField(5);
		apSpringPanel.add(new JLabel("Heavies to AP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of units worth of stored components to trigger an AP attempt for heavy units");
        baseTextField.setName("APAtMaxHeavyUnits");
		apSpringPanel.add(baseTextField);
		
        baseTextField = new JTextField(5);
		apSpringPanel.add(new JLabel("Assaults to AP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of units worth of stored components to trigger an AP attempt for assault units");
        baseTextField.setName("APAtMaxAssaultUnits");
		apSpringPanel.add(baseTextField);
		
        baseTextField = new JTextField(5);
        apSpringPanel.add(new JLabel("AP Failure Rate:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("% of autoproduction attempts which fail and destroy components");
        baseTextField.setName("AutoProductionFailureRate");
        apSpringPanel.add(baseTextField);
        

		//factory misc spring
        baseTextField = new JTextField(5);
		prodMiscPanel.add(new JLabel("Max Light Units:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max num. of light units, of each type, in factionbays.");
        baseTextField.setName("MaxLightUnits");
		prodMiscPanel.add(baseTextField);	
		
        baseTextField = new JTextField(5);
		prodMiscPanel.add(new JLabel("Max Other Units:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Max num. of non-light units, of each type, in factionbays.");
        baseTextField.setName("MaxOtherUnits");
		prodMiscPanel.add(baseTextField);
		
        baseTextField = new JTextField(5);
		prodMiscPanel.add(new JLabel("Comp Gain Every:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" +
				"Number of ticks which should pass before component gains<br>" +
				"are aggregated and displayed to a faction. Recommended: 4</HTML>");
        baseTextField.setName("ShowComponentGainEvery");
		prodMiscPanel.add(baseTextField);
		
        baseTextField = new JTextField(5);
        prodMiscPanel.add(new JLabel("Disputed Planet Color:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>Hex color a planet will show up as<br>When no single faction owns more<br>then the minimum amount of land.</html");
        baseTextField.setName("DisputedPlanetColor");
        prodMiscPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        prodMiscPanel.add(new JLabel("Min Planet OwnerShip:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>The Least amount of land a Faction own on a planet to control it");
        baseTextField.setName("MinPlanetOwnerShip");
        prodMiscPanel.add(baseTextField);

        baseTextField = new JTextField(5);
        prodMiscPanel.add(new JLabel("Auto Factory Refresh:", SwingConstants.TRAILING));
        baseTextField.setName("MinPlanetOwnerShip");
        prodMiscPanel.add(baseTextField);

        //Check Box Spring
        BaseCheckBox = new JCheckBox();
		prodCBoxSpring.add(new JLabel("Produce w/o factory:", SwingConstants.TRAILING));
        BaseCheckBox.setToolTipText("If checked, components will be produced even if no factory of a type/weightclass is owned");
        BaseCheckBox.setName("ProduceComponentsWithNoFactory");
		prodCBoxSpring.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox();
		prodCBoxSpring.add(new JLabel("Output Multipliers:", SwingConstants.TRAILING));
        BaseCheckBox.setToolTipText("If checked, personal production multipliers will be shown on ticks");
        BaseCheckBox.setName("ShowOutputMultiplierOnTick");
		prodCBoxSpring.add(BaseCheckBox);
		
		
		//lay out the springs
		SpringLayoutHelper.setupSpringGrid(refreshSpringPanel,4,2);
		SpringLayoutHelper.setupSpringGrid(salesSpringPanel,4,2);
		SpringLayoutHelper.setupSpringGrid(apSpringPanel,5,2);
		SpringLayoutHelper.setupSpringGrid(prodMiscPanel,2);
		SpringLayoutHelper.setupSpringGrid(prodCBoxSpring,1,4);
		
		//finalize the layout
		JPanel prodGrid = new JPanel(new GridLayout(3,2));
		prodGrid.add(refreshSpringPanel);
		prodGrid.add(salesSpringPanel);
		prodGrid.add(apSpringPanel);
		prodGrid.add(prodMiscPanel);
		prodGrid.add(prodCBoxSpring);
		
		productionPanel.add(prodGrid);

		
		/*
		 * REWARD MENU CONSTRUCTION
		 */
		JPanel rewardBox = new JPanel();
		rewardBox.setLayout(new BoxLayout(rewardBox, BoxLayout.Y_AXIS));
		JPanel rewardCBoxGrid = new JPanel(new SpringLayout());
		JPanel rewardGrid = new JPanel(new GridLayout(1,2));
		JPanel rewardSpring1 = new JPanel(new SpringLayout());
		JPanel rewardSpring2 = new JPanel(new SpringLayout());
		
		JLabel rewardAllowHeader = new JLabel("Allow rewards to be used for:");
		rewardAllowHeader.setAlignmentX(Component.CENTER_ALIGNMENT);
		
        BaseCheckBox = new JCheckBox("DISPLAY");

        BaseCheckBox.setToolTipText("If checked, reward levels are shown to players. RECOMMENDED.");
        BaseCheckBox.setName("ShowReward");
        rewardCBoxGrid.add(BaseCheckBox);
        
        BaseCheckBox = new JCheckBox(mwclient.moneyOrFluMessage(false,true,-1));

        BaseCheckBox.setToolTipText("Check to allow players to exchange RP for flu");
        BaseCheckBox.setName("AllowInfluenceForRewards");
        rewardCBoxGrid.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Techs");

        BaseCheckBox.setToolTipText("Check to allow players to exchange RP for techs");
        BaseCheckBox.setName("AllowTechsForRewards");
        rewardCBoxGrid.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Units");

        BaseCheckBox.setToolTipText("Check to allow players to exchange RP for units");
        BaseCheckBox.setName("AllowUnitsForRewards");
        rewardCBoxGrid.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Rares");

        BaseCheckBox.setToolTipText("Check to allow players to get RARE units with RP");
        BaseCheckBox.setName("AllowRareUnitsForRewards");
        rewardCBoxGrid.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Repods");

        BaseCheckBox.setToolTipText("<html>Check to allow players to repod units with RP<br>This allows a player to repod a unit<br>even if its not on their build table<br>Random repod options based<br>on the random repod settings</html>");
        BaseCheckBox.setName("GlobalRepodAllowed");
        rewardCBoxGrid.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Refresh");

        BaseCheckBox.setToolTipText("Check to allow players to refresh factories with RP");
        BaseCheckBox.setName("AllowFactoryRefreshForRewards");
        rewardCBoxGrid.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Repairs");

        BaseCheckBox.setToolTipText("Check to allow players to repair units with RP");
        BaseCheckBox.setName("AllowRepairsForRewards");
        rewardCBoxGrid.add(BaseCheckBox);
		
        BaseCheckBox = new JCheckBox("Crit Repairs");
        BaseCheckBox.setToolTipText("Check to allow players to individual crits with RP");
        BaseCheckBox.setName("AllowCritRepairsForRewards");
        rewardCBoxGrid.add(BaseCheckBox);
        
        SpringLayoutHelper.setupSpringGrid(rewardCBoxGrid,4);
        
		//set up spring1
        baseTextField = new JTextField(5);
		rewardSpring1.add(new JLabel("Max Reward Points:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP Cap");
        baseTextField.setName("XPRewardCap");
		rewardSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		rewardSpring1.add(new JLabel("XP for Reward:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Rollover for 1 RP");
        baseTextField.setName("XPRollOverCap");
		rewardSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		rewardSpring1.add(new JLabel("Techs for RP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Number of techs hired with 1 RP");
        baseTextField.setName("TechsForARewardPoint");
		rewardSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		rewardSpring1.add(new JLabel("Flu for RP:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Amount of flu given in exhcange for 1 RP");
        baseTextField.setName("InfluenceForARewardPoint");
		rewardSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		rewardSpring1.add(new JLabel("RP for Proto:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>RP to get a protomech<br> For final cost, add to RP for desired weightclass.</html>");
        baseTextField.setName("RewardPointsForProto");
		rewardSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
		rewardSpring1.add(new JLabel("RP for BA:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>RP to get a battle armor squad<br> For final cost, add to RP for desired weightclass.</html>");
        baseTextField.setName("RewardPointsForBA");
		rewardSpring1.add(baseTextField);

        baseTextField = new JTextField(5);
		rewardSpring1.add(new JLabel("Rare Multiplier:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Rares units cost [Normal RP]*[Rare Multiplier]");
        baseTextField.setName("RewardPointMultiplierForRare");
		rewardSpring1.add(baseTextField);
		
        baseTextField = new JTextField(5);
		rewardSpring1.add(new JLabel("NonHouse Multiplier:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("How much should the unit cost be multiplied by if not using faction build tables");
        baseTextField.setName("RewardPointNonHouseMultiplier");
		rewardSpring1.add(baseTextField);
		
        if ( Boolean.parseBoolean(mwclient.getserverConfigs("UseAdvanceRepair")) ){ 
            baseTextField = new JTextField(5);
            rewardSpring1.add(new JLabel("RP to buy Green Tech:", SwingConstants.TRAILING));
            baseTextField.setToolTipText("RP to buy 1 green tech.");
            baseTextField.setName("RewardPointsForGreen");
            rewardSpring1.add(baseTextField);
    
            baseTextField = new JTextField(5);
            rewardSpring1.add(new JLabel("RP to Buy Vet Tech:", SwingConstants.TRAILING));
            baseTextField.setToolTipText("RP to buy 1 vet tech.");
            baseTextField.setName("RewardPointsForVet");
            rewardSpring1.add(baseTextField);

            baseTextField = new JTextField(5);
            rewardSpring1.add(new JLabel("RP to Repair a crit:", SwingConstants.TRAILING));
            baseTextField.setToolTipText("<html>RP to repair 1 crit.<br>NOTE: this is a double field!</html>");
            baseTextField.setName("RewardPointsForCritRepair");
            rewardSpring1.add(baseTextField);

        }

        baseTextField = new JTextField(5);
		rewardSpring1.add(new JLabel("Repod Cost:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>How much should the repod cost<br>Random repods cost 1/2 this</htlm>");
        baseTextField.setName("GlobalRepodWithRPCost");
		rewardSpring1.add(baseTextField);

		SpringLayoutHelper.setupSpringGrid(rewardSpring1,2);
		
		//set up spring2 
        baseTextField = new JTextField(5);
		rewardSpring2.add(new JLabel("RP for Mek:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get a mek. For final cost, add to RP for desired weightclass.");
        baseTextField.setName("RewardPointsForAMek");
		rewardSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		rewardSpring2.add(new JLabel("RP for Veh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get a vehicle. For final cost, add to RP for desired weightclass.");
        baseTextField.setName("RewardPointsForAVeh");
		rewardSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		rewardSpring2.add(new JLabel("RP for Inf:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get an infantry unit. For final cost, add to RP for desired weightclass.");
        baseTextField.setName("RewardPointsForInf");
		rewardSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		rewardSpring2.add(new JLabel("RP for Light:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get a light unit. Add to RP for desired type.");
        baseTextField.setName("RewardPointsForALight");
		rewardSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		rewardSpring2.add(new JLabel("RP for Medium:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get a medium unit. Add to RP for desired type.");
        baseTextField.setName("RewardPointsForAMed");
		rewardSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		rewardSpring2.add(new JLabel("RP for Heavy:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get a heavy unit. Add to RP for desired type.");
        baseTextField.setName("RewardPointsForAHeavy");
		rewardSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		rewardSpring2.add(new JLabel("RP for Assault:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to get an assault unit. Add to RP for desired type.");
        baseTextField.setName("RewardPointsForAnAssault");
		rewardSpring2.add(baseTextField);
		
        baseTextField = new JTextField(5);
		rewardSpring2.add(new JLabel("RP to refresh:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("RP to refresh a factions factory.");
        baseTextField.setName("RewardPointToRefreshFactory");
		rewardSpring2.add(baseTextField);

        if ( Boolean.parseBoolean(mwclient.getserverConfigs("UseAdvanceRepair")) ){ 
            baseTextField = new JTextField(5);
            rewardSpring2.add(new JLabel("RP to buy Reg Tech:", SwingConstants.TRAILING));
            baseTextField.setToolTipText("RP to buy 1 reg tech.");
            baseTextField.setName("RewardPointsForReg");
            rewardSpring2.add(baseTextField);
    
            baseTextField = new JTextField(5);
            rewardSpring2.add(new JLabel("RP to Buy Elite Tech:", SwingConstants.TRAILING));
            baseTextField.setToolTipText("RP to buy 1 elite tech.");
            baseTextField.setName("RewardPointsForElite");
            rewardSpring2.add(baseTextField);

            baseTextField = new JTextField(5);
            rewardSpring2.add(new JLabel("RP to Repair a unit:", SwingConstants.TRAILING));
            baseTextField.setToolTipText("RP to repair 1 unit.");
            baseTextField.setName("RewardPointsForRepair");
            rewardSpring2.add(baseTextField);

        }

        baseTextField = new JTextField(5);
        rewardSpring2.add(new JLabel("Rewards Repod Folder:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Folder to pull repod data from for use with rewards");
        baseTextField.setName("RewardsRepodFolder");
        rewardSpring2.add(baseTextField);
        
        baseTextField = new JTextField(5);
        rewardSpring2.add(new JLabel("Rewards Rare Build Table:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("Build table that will be used in the rewards folder");
        baseTextField.setName("RewardsRareBuildTable");
        rewardSpring2.add(baseTextField);
        
		SpringLayoutHelper.setupSpringGrid(rewardSpring2,2);
		
		//finalize the layout
		rewardGrid.add(rewardSpring1);
		rewardGrid.add(rewardSpring2);
		rewardBox.add(rewardAllowHeader);
		rewardBox.add(rewardCBoxGrid);
		rewardBox.add(rewardGrid);
		rewardPanel.add(rewardBox);
		// Set the actions to generate
		okayButton.setActionCommand(okayCommand);
		cancelButton.setActionCommand(cancelCommand);
		okayButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		/*
		 * NEW OPTIONS - need to be sorted into proper menus.
		 */
		
		// Set tool tips (balloon help)	
		okayButton.setToolTipText("Save Options");
		cancelButton.setToolTipText("Exit without saving options");
	
		ConfigPane.addTab("Direct Sales",null,directSellPanel,"Units the lifeblood of the game");
		ConfigPane.addTab("Faction",null,factionPanel,"House Stuff");
		ConfigPane.addTab("Factory Options",null,productionPanel,"Factories That Can Do");
		ConfigPane.addTab("Pilots",null,pilotsPanel,"Pilot Options");
		ConfigPane.addTab("Pilot Skills",null,pilotSkillsPanel,"Server Configurable Pilot Skills");
		ConfigPane.addTab("Pilot Skill Mods",null,pilotSkillsModPanel,"Server Configurable Pilot Skills Modifiers");
		ConfigPane.addTab("Repodding",null,repodPanel,"Repod");
		ConfigPane.addTab("Rewards",null,rewardPanel,"Reward Points");
		ConfigPane.addTab("Techs",null,technicianPanel,"Techs");
		ConfigPane.addTab("Units",null,unitPanel,"Care and Feeding of Your Units");
		ConfigPane.addTab("Units 2",null,unit2Panel,"More Care and Feeding of Your Units");
		
		//Create the panel that will hold the entire UI
		JPanel mainConfigPanel = new JPanel();
		
		// Set the user's options
		Object[] options = { okayButton, cancelButton };
		
		// Create the pane containing the buttons
		pane = new JOptionPane(ConfigPane, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.DEFAULT_OPTION, null, options, null);
		
		// Create the main dialog and set the default button
		dialog = pane.createDialog(mainConfigPanel, windowName);
		dialog.getRootPane().setDefaultButton(cancelButton);
		
		
        for ( int pos = ConfigPane.getComponentCount()-1; pos >= 0; pos-- ){
            JPanel panel = (JPanel) ConfigPane.getComponent(pos);
            findAndPopulateTextAndCheckBoxes(panel);
            
        }


        //Show the dialog and get the user's input
		dialog.setLocation(mwclient.getMainFrame().getLocation().x+10,mwclient.getMainFrame().getLocation().y);
		dialog.setModal(true);
		dialog.pack();
		dialog.setVisible(true);
		
		if (pane.getValue() == okayButton)
		{
            
            for ( int pos = ConfigPane.getComponentCount()-1; pos >= 0; pos-- ){
                JPanel panel = (JPanel) ConfigPane.getComponent(pos);
                findAndSaveConfigs(panel);
            }
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX+ "c AdminSaveFactionConfigs#"+houseName);
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX+ "c CampaignConfig");
			
			mwclient.reloadData();
			
		}
		else 
			dialog.dispose();
	}

    /**
     * This Method tunnels through all of the panels to find the textfields
     * and checkboxes. Once it find one it grabs the Name() param of the object
     * and uses that to find out what the setting should be from the
     * mwclient.getserverConfigs() method.
     * @param panel
     */
    public void findAndPopulateTextAndCheckBoxes(JPanel panel){
        String key = null;
        
        for ( int fieldPos = panel.getComponentCount()-1; fieldPos >= 0; fieldPos--){
            
            Object field = panel.getComponent(fieldPos);
            
            if ( field instanceof JPanel)
                findAndPopulateTextAndCheckBoxes((JPanel)field);
            else if ( field instanceof JTextField){
                JTextField textBox = (JTextField)field;
                
                key = textBox.getName();
                if ( key == null )
                    continue;
                
                textBox.setMaximumSize(new Dimension(100,10));
                try{
                    //bad hack need to format the message for the last time the backup happened
                    if (key.equals("LastAutomatedBackup") ){
                        SimpleDateFormat sDF = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
                        Date date = new Date(Long.parseLong(mwclient.getserverConfigs(key)));
                        textBox.setText(sDF.format(date));
                    }
                    else
                        textBox.setText(mwclient.getserverConfigs(key));
                }catch(Exception ex){
                    textBox.setText("N/A");
                }
            }else if ( field instanceof JCheckBox){
                JCheckBox checkBox = (JCheckBox)field;
                
                key = checkBox.getName();
                if ( key == null ){
                    MWClient.mwClientLog.clientErrLog("Null Checkbox: "+checkBox.getToolTipText());
                    continue;
                }
                checkBox.setSelected(Boolean.parseBoolean(mwclient.getserverConfigs(key)));
                
            }else if ( field instanceof JRadioButton){
                JRadioButton radioButton = (JRadioButton)field;
                
                key = radioButton.getName();
                if ( key == null ){
                    MWClient.mwClientLog.clientErrLog("Null RadioButton: "+radioButton.getToolTipText());
                    continue;
                }
                radioButton.setSelected(Boolean.parseBoolean(mwclient.getserverConfigs(key)));
                
            }//else continue
        }
    }

    /**
     * This method will tunnel through all of the panels of the config UI
     * to find any changed text fields or checkboxes. Then it will send the 
     * new configs to the server.
     * @param panel
     */
    public void findAndSaveConfigs(JPanel panel){
        String key = null;
        String value = null;
        for ( int fieldPos = panel.getComponentCount()-1; fieldPos >= 0; fieldPos--){
            
            Object field = panel.getComponent(fieldPos);

            //found another JPanel keep digging!
            if ( field instanceof JPanel )
                findAndSaveConfigs((JPanel)field);
            else if ( field instanceof JTextField){
                JTextField textBox = (JTextField)field;
                
                value = textBox.getText();
                key = textBox.getName();
                
                if ( key == null || value == null )
                    continue;
                
                //don't need to save this the system does it on its own
                // --Torren.
                if (key.equals("LastAutomatedBackup") )
                    continue;
                    
                //reduce bandwidth only send things that have changed.
                if ( !mwclient.getserverConfigs(key).equalsIgnoreCase(value) )
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX+ "c AdminChangeFactionConfig#"+houseName+"#"+key+"#"+value+"#CONFIRM"); 
            } else if ( field instanceof JCheckBox){
                JCheckBox checkBox = (JCheckBox)field;
                
                value = Boolean.toString(checkBox.isSelected());
                key = checkBox.getName();
                
                if ( key == null || value == null )
                    continue;
                //reduce bandwidth only send things that have changed.
                if ( !mwclient.getserverConfigs(key).equalsIgnoreCase(value) )
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX+ "c AdminChangeFactionConfig#"+houseName+"#"+key+"#"+value+"#CONFIRM"); 
            }else if ( field instanceof JRadioButton){
                JRadioButton radioButton = (JRadioButton)field;
                
                value = Boolean.toString(radioButton.isSelected());
                key = radioButton.getName();
                
                if ( key == null || value == null )
                    continue;
                //reduce bandwidth only send things that have changed.
                if ( !mwclient.getserverConfigs(key).equalsIgnoreCase(value) )
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX+ "c AdminChangeFactionConfig#"+houseName+"#"+key+"#"+value+"#CONFIRM"); 
            }//else continue
        }

    }
    
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals(okayCommand)) {
			pane.setValue(okayButton);
			dialog.dispose();
		} else if (command.equals(cancelCommand)) {
			pane.setValue(cancelButton);
			dialog.dispose();
		}
	}
}
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

package client.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import common.House;
import common.Planet;
import common.Unit;
import common.UnitFactory;
import common.util.UnitUtils;

import client.MWClient;
import client.campaign.CUnit;
import client.gui.SpringLayoutHelper;

@SuppressWarnings({"unchecked","serial"})
public final class RewardPointsDialog implements ActionListener, KeyListener{
	
	//store the client backlink for other things to use
	private MWClient mwclient = null; 
	
	private final static String okayCommand = "Okay";
	private final static String cancelCommand = "Cancel";
	private final static String unitCommand = "Units";
	private final static String weightCommand = "Weight";
	private final static String rewardCommand = "Reward";
	private final static String factionCommand = "House";
	private final static String repodCommand = "Repod";
	private final static String refreshCommand = "Refresh";
    private final static String techComboCommand = "TechCombo";
    private final static String repairCommand = "Repair";

	//private final static String amountCommand = "Amount";
	
	private final static String windowName = "Reward Points";
	
	
	//BUTTONS
	private final JButton okayButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");	
	
	//TEXT FIELDS
	//tab names
	private final JLabel costLabel= new JLabel();
	private final JLabel rewardLabel = new JLabel("Reward Type:",SwingConstants.TRAILING);
	private final JLabel factionLabel = new JLabel("House Table:",SwingConstants.TRAILING);
	private final JLabel unitLabel = new JLabel("Unit Type:",SwingConstants.TRAILING);
	private final JLabel weightLabel = new JLabel("Weight Class:",SwingConstants.TRAILING);
	private final JLabel repodLabel = new JLabel("Repod Selection:",SwingConstants.TRAILING);
	private final JLabel pUnitsLabel = new JLabel("Unit:",SwingConstants.TRAILING);
	private final JLabel refreshLabel = new JLabel("Refresh:",SwingConstants.TRAILING);
    private final JLabel techComboLabel = new JLabel("Tech Type:",SwingConstants.TRAILING);
    private final JLabel repairLabel = new JLabel("Repair:",SwingConstants.TRAILING);
	
	private JComboBox unitComboBox = new JComboBox();
	private final String[] weightChoices = {"Light", "Medium", "Heavy", "Assault"};
	private final JComboBox weightComboBox = new JComboBox(weightChoices);
	private JComboBox rewardsComboBox = new JComboBox();
	private JComboBox factionComboBox = new JComboBox();
	private JComboBox repodComboBox = new JComboBox();
	private JComboBox pUnitsComboBox = new JComboBox();
	private JComboBox refreshComboBox = new JComboBox();
    private JComboBox repairComboBox = new JComboBox();
    private final String[] techChoices = {"Green", "Reg", "Vet", "Elite"};
    private final JComboBox techComboBox = new JComboBox(techChoices);
	
	private final JTextField amountText = new JTextField(5);
	private final JLabel amountLabel = new JLabel("RP to use:",SwingConstants.TRAILING);
	int cost = 0;
	
	//STOCK DIALOUG AND PANE
	private JDialog dialog;
	private JOptionPane pane;
	
	JTabbedPane ConfigPane = new JTabbedPane(SwingConstants.TOP);
	
	public RewardPointsDialog(MWClient c) {
		
		//save the client
		this.mwclient = c;
		
		//COMBO BOXES
		TreeSet names = new TreeSet();
		names.add("Common"); //start with the common faction
		for (Iterator factions = mwclient.getData().getAllHouses().iterator(); factions.hasNext();)
			names.add(((House) factions.next()).getName());
		
		//check for the use of rare and add if used
		if (Boolean.parseBoolean(mwclient.getserverConfigs("AllowRareUnitsForRewards")))
		    names.add("Rare");
		
		factionComboBox = new JComboBox(names.toArray());
		
		names = new TreeSet();
		if (Boolean.parseBoolean(mwclient.getserverConfigs("AllowTechsForRewards")))
		    names.add("Techs");
		if (Boolean.parseBoolean(mwclient.getserverConfigs("AllowInfluenceForRewards")))
		    names.add(mwclient.getserverConfigs("FluLongName"));
		if (Boolean.parseBoolean(mwclient.getserverConfigs("AllowUnitsForRewards"))){
          names.add(unitCommand);
          //private final String[] unitChoices = {"Mek", "Vehicle", "Infantry", "ProtoMek","BattleArmor" };

          Vector<String> unitList = new Vector<String>(5,1);
          
          unitList.add("Mek");
          if ( Boolean.parseBoolean(mwclient.getserverConfigs("UseVehicle")) )
              unitList.add("Vehicle");
          if ( Boolean.parseBoolean(mwclient.getserverConfigs("UseInfantry")) )
              unitList.add("Infantry");
          if ( Boolean.parseBoolean(mwclient.getserverConfigs("UseProtoMek")))
              unitList.add("ProtoMek");
          if ( Boolean.parseBoolean(mwclient.getserverConfigs("UseBattleArmor")))
              unitList.add("BattleArmor");

          unitComboBox = new JComboBox(unitList.toArray());
       }
        
		if (Boolean.parseBoolean(mwclient.getserverConfigs("GlobalRepodAllowed"))) {
		    names.add(repodCommand);
		    TreeSet repodOptions = new TreeSet();
		    
		    if (Boolean.parseBoolean(mwclient.getserverConfigs("RandomRepodOnly")))
		        repodOptions.add("Random");
		    else{
		        if (Boolean.parseBoolean(mwclient.getserverConfigs("RandomRepodAllowed")))
		            repodOptions.add("Random");
		        repodOptions.add("Select");
		    }
		    
		    repodComboBox = new JComboBox(repodOptions.toArray());
		    repodOptions.clear();
		    Iterator units = c.getPlayer().getHangar().iterator();
		    while (units.hasNext()){
		        CUnit unit = (CUnit)units.next();
		        if ( !unit.isOmni() )
		            continue;
		        repodOptions.add("#"+unit.getId()+" "+unit.getModelName());
		    }
		    pUnitsComboBox = new JComboBox(repodOptions.toArray());
		}
        if (Boolean.parseBoolean(mwclient.getserverConfigs("AllowRepairsForRewards"))){
            names.add(repairCommand);
            TreeSet damagedUnits = new TreeSet();
            for ( CUnit unit: mwclient.getPlayer().getHangar() ){
                if ( UnitUtils.hasArmorDamage(unit.getEntity()) || UnitUtils.hasCriticalDamage(unit.getEntity()) )
                    damagedUnits.add("#"+unit.getId()+" "+unit.getModelName());
            }
            repairComboBox = new JComboBox(damagedUnits.toArray());
        }

        if ( Boolean.parseBoolean(mwclient.getserverConfigs("AllowFactoryRefreshForRewards"))){
		    TreeSet factories = new TreeSet();
		    House faction = mwclient.getData().getHouseByName(mwclient.getPlayer().getHouse());
		    Iterator planets = mwclient.getData().getAllPlanets().iterator();
		    names.add(refreshCommand);
		    while ( planets.hasNext() ){
		        Planet planet = (Planet)planets.next();
		        if ( !planet.isOwner(faction.getId()) )
		            continue;
		        Iterator unitFactories = planet.getUnitFactories().iterator();
		        while (unitFactories.hasNext()){
		            UnitFactory factory = (UnitFactory)unitFactories.next();
		            if ( factory.getTicksUntilRefresh() > 0)
		                factories.add(planet.getName()+": "+factory.getName()+ "("+factory.getTicksUntilRefresh()+")");
		        }
		    }
		    refreshComboBox = new JComboBox(factories.toArray());
		}
		
		rewardsComboBox = new JComboBox(names.toArray());

		//stored values.
		cost = 0;

		//Set the tooltips and actions for dialouge buttons
		okayButton.setActionCommand(okayCommand);
		cancelButton.setActionCommand(cancelCommand);
		factionComboBox.setActionCommand(factionCommand);
		rewardsComboBox.setActionCommand(rewardCommand);
		weightComboBox.setActionCommand(weightCommand);
        unitComboBox.setActionCommand(unitCommand);
        repodComboBox.setActionCommand(repodCommand);
		refreshComboBox.setActionCommand(refreshCommand);
        techComboBox.setActionCommand(techComboCommand);
        repairComboBox.setActionCommand(repairCommand);
		//amountText.setActionCommand(amountCommand);
		
		okayButton.addActionListener(this);
		cancelButton.addActionListener(this);
		okayButton.setToolTipText("Save Options");
		cancelButton.setToolTipText("Exit without saving changes");
		factionComboBox.addActionListener(this);
		rewardsComboBox.addActionListener(this);
		weightComboBox.addActionListener(this);
        unitComboBox.addActionListener(this);
        repodComboBox.addActionListener(this);
		pUnitsComboBox.addActionListener(this);
		refreshComboBox.addActionListener(this);
        techComboBox.addActionListener(this);
        repairComboBox.addActionListener(this);

        amountText.addKeyListener(this);
		
		//CREATE THE PANELS
		JPanel rewardPanel = new JPanel();//player name, etc
		
		/*
		 * Format the Reward Points panel. Spring layout.
		 */
		rewardPanel.setLayout(new BoxLayout(rewardPanel,BoxLayout.Y_AXIS));
		
		JPanel comboPanel = new JPanel(new SpringLayout());
		JPanel costPanel = new JPanel();
		
		comboPanel.add(rewardLabel);
		rewardsComboBox.setToolTipText("Select your Reward Type");
		comboPanel.add(rewardsComboBox);
		
		comboPanel.add(factionLabel);
		factionComboBox.setToolTipText("Select the faction build table you wish to use");
		comboPanel.add(factionComboBox);
		
		comboPanel.add(unitLabel);
		unitComboBox.setToolTipText("Select the Unit Type");
		comboPanel.add(unitComboBox);
		
		comboPanel.add(weightLabel);
		weightComboBox.setToolTipText("Unit Weight Class");
		comboPanel.add(weightComboBox);
		
		comboPanel.add(pUnitsLabel);
		pUnitsComboBox.setToolTipText("Unit");
		comboPanel.add(pUnitsComboBox);

        comboPanel.add(repodLabel);
		repodComboBox.setToolTipText("Repod Selection Type");
		comboPanel.add(repodComboBox);
		
		comboPanel.add(refreshLabel);
		refreshComboBox.setToolTipText("Refresh Factory");
		comboPanel.add(refreshComboBox);

        if ( mwclient.isUsingAdvanceRepairs() ){
            comboPanel.add(techComboLabel);
            techComboBox.setToolTipText("Tech Selection Type");
            techComboBox.setSelectedIndex(0);
            comboPanel.add(techComboBox);
        }

        comboPanel.add(repairLabel);
        repairComboBox.setToolTipText("Repair Unit with RPs");
        comboPanel.add(repairComboBox);
        
		comboPanel.add(amountLabel);
		comboPanel.add(amountText);
		
		//run the spring layout
		SpringLayoutHelper.setupSpringGrid(comboPanel,2);
		
		rewardPanel.add(comboPanel);
		costPanel.add(costLabel);
		rewardPanel.add(costPanel);
		
		costLabel.setText("Result: no expenditure");
		
        try{
            factionComboBox.setSelectedItem(mwclient.getPlayer().getHouse());
        }catch (Exception ex){
            factionComboBox.setSelectedIndex(0);
        }
        techComboBox.setSelectedIndex(0);
		
		if (Boolean.parseBoolean(mwclient.getserverConfigs("AllowUnitsForRewards"))) {
			rewardsComboBox.setSelectedItem("Units");
            weightComboBox.setSelectedIndex(0);
            unitComboBox.setSelectedIndex(0);
		}
		else
			rewardsComboBox.setSelectedIndex(0);

		JPanel mainPanel = new JPanel();
		
		// Set the user's options
		Object[] options = { okayButton, cancelButton };
		
		// Create the pane containing the buttons
		pane = new JOptionPane(rewardPanel,JOptionPane.PLAIN_MESSAGE,JOptionPane.DEFAULT_OPTION, null, options, null);
		
		// Create the main dialog and set the default button
		dialog = pane.createDialog(mainPanel, windowName);
		dialog.getRootPane().setDefaultButton(cancelButton);
		dialog.setLocation(Math.max(mwclient.getMainFrame().getLocation().x,mwclient.getMainFrame().getLocation().x+((mwclient.getMainFrame().getWidth()/2)-(dialog.getWidth()/2))),Math.max(mwclient.getMainFrame().getLocation().y+(mwclient.getMainFrame().getHeight()/2)-dialog.getHeight()/2,mwclient.getMainFrame().getLocation().y));

		//Show the dialog and get the user's input
		dialog.setModal(true);
		dialog.pack();
		dialog.setVisible(true);
		
		if (pane.getValue() == okayButton) {
			
		}
		else 
			dialog.dispose();
	}
	
	public void keyTyped(KeyEvent e){
	}
	
	public void keyReleased(KeyEvent e){
	    String selection = (String)rewardsComboBox.getSelectedItem();
	    cost = Integer.parseInt(amountText.getText());
	    if (!selection.equals("Units")){
		    if ( selection.equals("Techs")){
                if ( !mwclient.isUsingAdvanceRepairs() ){
    		        int total = cost * Integer.parseInt(mwclient.getserverConfigs("TechsForARewardPoint"));
    		        costLabel.setText("Result: Hire " +total+" Techs");
                }
		    }
		    else if ( selection.equals("Repod") ){
		        cost = Integer.parseInt(mwclient.getserverConfigs("GlobalRepodWithRPCost"));
		        if ( ((String)repodComboBox.getSelectedItem()).equals("Random") )
		            cost /= 2;
				costLabel.setText("RP Required: "+cost+" RP");
		    }
		    else if ( selection.equals(refreshCommand) ){
		        cost = Integer.parseInt(mwclient.getserverConfigs("RewardPointToRefreshFactory"));
		        costLabel.setText("RP Required: "+cost+" RP");
		        dialog.repaint();
		    }
		    else{
		        int total = cost * Integer.parseInt(mwclient.getserverConfigs("InfluenceForARewardPoint"));
		        costLabel.setText("Result: Gain "+mwclient.moneyOrFluMessage(false,true,total));
		    }
	    }
	}

	public void keyPressed(KeyEvent e){
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if (command.equals(okayCommand)) {
		    String selection = (String)rewardsComboBox.getSelectedItem();
		    
		    if ( selection.equals("Units") ){
		        String type = (String)unitComboBox.getSelectedItem();
		        String weight = (String)weightComboBox.getSelectedItem();
		        String faction = (String)factionComboBox.getSelectedItem();
			    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c userewardpoints#2#"+ type + "#" + weight + "#" + faction);
		    }
		    else if ( selection.equals("Techs")){
                if ( mwclient.isUsingAdvanceRepairs() )
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c userewardpoints#0#"+ techComboBox.getSelectedIndex());
                else
                    mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c userewardpoints#0#"+ amountText.getText());
		    }
		    else if ( selection.equals("Repod") ){
		        if ( pUnitsComboBox.getComponentCount() < 1)
		            dialog.dispose();
		        String options = "#GLOBAL";
		        if ( ((String)repodComboBox.getSelectedItem()).equals("Random") )
		        options +="#RANDOM";
		        StringTokenizer unitid = new StringTokenizer((String)pUnitsComboBox.getSelectedItem()," ");
		        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c repod"+unitid.nextToken()+options);
		    }
		    else if ( selection.equals(refreshCommand) ){
		        if ( refreshComboBox.getComponentCount() < 1)
		            dialog.dispose();
		        String factoryInfo = (String)refreshComboBox.getSelectedItem();
		        String planet = factoryInfo.substring(0,factoryInfo.indexOf(":")).trim();
		        String factory = factoryInfo.substring(planet.length()+2,factoryInfo.indexOf("(")).trim();
		        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c refreshFactory#"+planet+"#"+factory);
		    }
            else if ( selection.equals(repairCommand)){
                String selectionName = (String)repairComboBox.getSelectedItem();
                mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c userewardpoints#3#"+selectionName.trim().substring(0,selectionName.indexOf(" ")));
            }
		    else{//flu
		        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c userewardpoints#1#"+ amountText.getText());
		    }
		    
			dialog.dispose();
		} else if (command.equals(cancelCommand)) {
			pane.setValue(cancelButton);
			dialog.dispose();
		} 
		else if (command.equals(rewardCommand)){
		    String selection = (String)rewardsComboBox.getSelectedItem();
		    if ( selection.equals("Units")){
		        makeVisible(true,false,false);
				unitComboBox.setSelectedIndex(0);
				weightComboBox.setSelectedIndex(0);        
                try{
                    factionComboBox.setSelectedItem(mwclient.getPlayer().getHouse());
                }catch (Exception ex){
                    factionComboBox.setSelectedIndex(0);
                }

				cost = getUnitRPCost();
				costLabel.setText("RP Required: "+cost+" RP");
		    }
		    else if (selection.equals("Techs")){
                makeVisible(false,false,false);
                if ( mwclient.isUsingAdvanceRepairs() ){

                    int type = techComboBox.getSelectedIndex();
                    int total = Integer.parseInt(mwclient.getserverConfigs("RewardPointsFor"+UnitUtils.techDescription(type)));
                    costLabel.setText("Hire 1 "+UnitUtils.techDescription(type)+" tech for "+total+"rp");
                    techComboBox.setVisible(true);
                    techComboLabel.setVisible(true);
                    
                    amountText.setVisible(false);
                    amountLabel.setVisible(false);
                }else{
                    amountText.setText("0");
    				cost = Integer.parseInt(amountText.getText());
    		        int total = cost * Integer.parseInt(mwclient.getserverConfigs("TechsForARewardPoint"));
    		        costLabel.setText("Result: Hire "+total+" Techs");
    				costLabel.repaint();
                }
		    }
		    else if ( selection.equals("Repod") ){
		        if ( pUnitsComboBox.getItemCount() >= 1)
		            pUnitsComboBox.setSelectedIndex(0);
		        cost = Integer.parseInt(mwclient.getserverConfigs("GlobalRepodWithRPCost"));
		        if ( ((String)repodComboBox.getSelectedItem()).equals("Random") )
		            cost /= 2;
				costLabel.setText("RP Required: "+cost+" RP");
				makeVisible(false,true,false);
		    }
		    else if ( selection.equals(refreshCommand) ){
		        if ( refreshComboBox.getItemCount() >= 1)
		           refreshComboBox.setSelectedIndex(0);
		        cost = Integer.parseInt(mwclient.getserverConfigs("RewardPointToRefreshFactory"));
		        costLabel.setText("RP Required: "+cost+" RP");
		        makeVisible(false,false,true);
		    }
            else if (selection.equals(repairCommand)){
                makeVisible(false,false,false);

                if ( repairComboBox.getItemCount() > 0)
                    repairComboBox.setSelectedIndex(0);
                costLabel.setText("Repair Cost: "+mwclient.getserverConfigs("RewardPointsForRepair"));
                repairComboBox.setVisible(true);
                repairLabel.setVisible(true);
                
                amountText.setVisible(false);
                amountLabel.setVisible(false);
            }
		    else{
		        amountText.setText("0");
				cost = Integer.parseInt(amountText.getText());
		        int total = cost * Integer.parseInt(mwclient.getserverConfigs("InfluenceForARewardPoint"));
		        costLabel.setText("Result: Gain "+mwclient.moneyOrFluMessage(false,true,total));
				makeVisible(false,false,false);
		    }
		}
		else if ( command.equals(repodCommand)){
	        cost = Integer.parseInt(mwclient.getserverConfigs("GlobalRepodWithRPCost"));
	        if ( ((String)repodComboBox.getSelectedItem()).equals("Random") )
	            cost /= 2;
			costLabel.setText("RP Required: "+cost+" RP");
		}
		else if (command.equals(weightCommand) 
		        || command.equals(unitCommand)
		        || command.equals(factionCommand)){
			cost = getUnitRPCost();
			costLabel.setText("RP Required: "+cost+" RP");
		}
        else if (command.equals(techComboCommand)){
            makeVisible(false,false,false);

            int type = techComboBox.getSelectedIndex();
            int total = Integer.parseInt(mwclient.getserverConfigs("RewardPointsFor"+UnitUtils.techDescription(type)));
            costLabel.setText("Hire 1 "+UnitUtils.techDescription(type)+" tech for "+total+"rp");
            techComboBox.setVisible(true);
            techComboLabel.setVisible(true);
            
            amountText.setVisible(false);
            amountLabel.setVisible(false);
        }
        else if (command.equals(repairCommand)){
            makeVisible(false,false,false);

            costLabel.setText("Repair Cost: "+mwclient.getserverConfigs("RewardPointsForRepair"));
            repairComboBox.setVisible(true);
            repairLabel.setVisible(true);
            
            amountText.setVisible(false);
            amountLabel.setVisible(false);
        }
	}
	
	private void makeVisible(boolean visible, boolean repod, boolean refresh){
		unitComboBox.setVisible(visible);
		weightComboBox.setVisible(visible);
		factionComboBox.setVisible(visible);
		unitLabel.setVisible(visible);
		weightLabel.setVisible(visible);
		factionLabel.setVisible(visible);
        
		repodComboBox.setVisible(repod);
		repodLabel.setVisible(repod);
		pUnitsComboBox.setVisible(repod);
		pUnitsLabel.setVisible(repod);

		refreshComboBox.setVisible(refresh);
		refreshLabel.setVisible(refresh);
		
		if ( repod || refresh ){
		    amountLabel.setVisible(false);
		    amountText.setVisible(false);
		}
		else{
		    amountLabel.setVisible(!visible);
		    amountText.setVisible(!visible);
		}
		    
        techComboBox.setVisible(false);
        techComboLabel.setVisible(false);
        repairComboBox.setVisible(false);
        repairLabel.setVisible(false);

	}
	
	private int getUnitRPCost(){
        
        if ( !Boolean.parseBoolean(mwclient.getserverConfigs("AllowUnitsForRewards")) )
            return 0;
        
        int type = Unit.getTypeIDForName((String)unitComboBox.getSelectedItem());
	    int weight = Unit.getWeightIDForName((String)weightComboBox.getSelectedItem());
	    String House = (String)factionComboBox.getSelectedItem();
	    int cost = 0;
	    
	    //find the type cost
	    if ( type == Unit.MEK )
	        cost = Integer.parseInt(mwclient.getserverConfigs("RewardPointsForAMek"));
	    else if ( type == Unit.VEHICLE )
	        cost = Integer.parseInt(mwclient.getserverConfigs("RewardPointsForAVeh"));
	    else if ( type == Unit.INFANTRY )                   
	        cost = Integer.parseInt(mwclient.getserverConfigs("RewardPointsForInf"));
	    else if ( type == Unit.PROTOMEK)
	        cost = Integer.parseInt(mwclient.getserverConfigs("RewardPointsForProto"));
	    else
	        cost = Integer.parseInt(mwclient.getserverConfigs("RewardPointsForBA"));
		    
	    if ( weight == Unit.LIGHT)
	        cost += Integer.parseInt(mwclient.getserverConfigs("RewardPointsForALight"));
	    else if ( weight == Unit.MEDIUM)
	        cost += Integer.parseInt(mwclient.getserverConfigs("RewardPointsForAMed"));
	    else if ( weight == Unit.HEAVY)
	        cost += Integer.parseInt(mwclient.getserverConfigs("RewardPointsForAHeavy"));
	    else if ( weight == Unit.ASSAULT)
	        cost += Integer.parseInt(mwclient.getserverConfigs("RewardPointsForAnAssault"));

	    if ( House.equals("Rare"))
	        cost *= Double.parseDouble(mwclient.getserverConfigs("RewardPointMultiplierForRare"));
	    else if ( !House.equals("Common") && !House.equals(mwclient.getPlayer().getHouse()))
	        cost *= Double.parseDouble(mwclient.getserverConfigs("RewardPointNonHouseMultiplier"));
	    
	    return cost;
	}

}//end RewardPointsDialog.java

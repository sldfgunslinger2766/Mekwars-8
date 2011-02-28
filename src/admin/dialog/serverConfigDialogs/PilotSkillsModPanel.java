/*
 * MekWars - Copyright (C) 2011
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */

package admin.dialog.serverConfigDialogs;

import java.awt.GridLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import client.MWClient;

import admin.dialog.VerticalLayout;

import common.util.SpringLayoutHelper;

/**
 * @author jtighe
 * @author Spork
 */
public class PilotSkillsModPanel extends JPanel {

	private static final long serialVersionUID = 677126097682711286L;
	private JTextField baseTextField = new JTextField(5);
	private JCheckBox BaseCheckBox = new JCheckBox();
	
	public PilotSkillsModPanel(MWClient mwclient) {
		super();
        /*
         * Pilot Skills Panel BV mods
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

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("VDNI", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>BV mod added to the unit due to VDNI.</html>");
        baseTextField.setName("VDNIBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("BVDNI", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>BV mod added to the unit due to Buffered VDNI.</html>");
        baseTextField.setName("BufferedVDNIBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Pain Shunt", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<html>BV mod added to the unit due to Pain Shunt.</html>");
        baseTextField.setName("PainShuntBaseBVMod");
        SkillModSpring.add(baseTextField);

        baseTextField = new JTextField(5);
        SkillModSpring.add(new JLabel("Gifted % Mod", SwingConstants.TRAILING));
        if (Boolean.parseBoolean(mwclient.getserverConfigs("PlayersCanBuyPilotUpgrades"))) {
            baseTextField.setToolTipText("<html><body>Note Double Field<br>The amount off the cost of other upgrades a Gifted Pilot gets.<br>Example .05 for 5% off</body></html>");
        } else {
            baseTextField.setToolTipText("<html><body>Pilots receive an extra x% chance to gain a skill when they fail<br>to level Piloting or Gunnery after a win</body></html>");
        }
        baseTextField.setName("GiftedPercent");
        SkillModSpring.add(baseTextField);

        JPanel GunneryModPanel = new JPanel();
        
        GunneryModPanel.setLayout(new GridLayout(3,2));
        
        BaseCheckBox = new JCheckBox("Flat G/B Mod");
        BaseCheckBox.setName("USEFLATGUNNERYBALLISTICMODIFIER");
        GunneryModPanel.add(BaseCheckBox);

        baseTextField = new JTextField(5);
        baseTextField.setName("GunneryBallisticBaseBVMod");
        baseTextField.setToolTipText("BV Mod per Ballistic Weapon");
        GunneryModPanel.add(baseTextField);
        
        BaseCheckBox = new JCheckBox("Flat G/L Mod");
        BaseCheckBox.setName("USEFLATGUNNERYLASERMODIFIER");
        GunneryModPanel.add(BaseCheckBox);

        baseTextField = new JTextField(5);
        baseTextField.setName("GunneryLaserBaseBVMod");
        baseTextField.setToolTipText("BV Mod per Laser Weapon");
        GunneryModPanel.add(baseTextField);
        
        BaseCheckBox = new JCheckBox("Flat G/M Mod");
        BaseCheckBox.setName("USEFLATGUNNERYMISSILEMODIFIER");
        GunneryModPanel.add(BaseCheckBox);

        baseTextField = new JTextField(5);
        baseTextField.setName("GunneryMissileBaseBVMod");
        baseTextField.setToolTipText("BV Mod per Missile Weapon");
        GunneryModPanel.add(baseTextField);
        
        
        SpringLayoutHelper.setupSpringGrid(SkillModSpring, 4);

        setLayout(new VerticalLayout(10));
        
        add(SkillModSpring);
        add(GunneryModPanel);
	}

}
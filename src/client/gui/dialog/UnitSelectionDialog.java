/*
 * MekWars - Copyright (C) 2005
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

//awt imports
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

//util imports
import java.util.StringTokenizer;

//swing imports
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.SpringLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import megamek.common.Infantry;

import common.Unit;

//mekwars imports
import client.MWClient;
import client.campaign.CUnit;
import common.util.SpringLayoutHelper;

/*
 * Dialog, based on HouseNameDialog, which allows players
 * to search for players using partial strings. Takes a
 * boolean to indicate whether to use all players, or only
 * those in the player's faction.
 * 
 * @urgru 6.17.05
 */
@SuppressWarnings({"unchecked","serial"})
public class UnitSelectionDialog extends JDialog implements ActionListener {
	
	//variables
	
	//combo box to pick unit from
	private JComboBox possibleUnits = new JComboBox();
	
	//buttons
	private final JButton okayButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");	
	private final String okayCommand = "Okay";
	
	//label
	private JLabel comboLabel = new JLabel();
	
	private String toReturn = "-1";
	//private boolean factionOnly = false;
	
	//constructor
	public UnitSelectionDialog(MWClient mwclient, String boxText, String labelText) {
		
		//super, and variable saves
		super(mwclient.getMainFrame(),boxText, true);//dummy frame as owner
		
		//make the label
		comboLabel = new JLabel(labelText, SwingConstants.CENTER);
		comboLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		comboLabel.setAlignmentY(Component.CENTER_ALIGNMENT);
		
		//populate the combo box
		possibleUnits.setModel(new DefaultComboBoxModel(mwclient.getPlayer().getHangar()) {
			@Override
			public Object getElementAt(int index) {
				CUnit mm = (CUnit)super.getElementAt(index);
				if ( mm.getType() == Unit.MEK || mm.getType() == Unit.VEHICLE )
				    return (mm.getId() +" "+ mm.getModelName() + " [" + mm.getPilot().getGunnery() + "/" + mm.getPilot().getPiloting()+"]");
				
				if ( mm.getType() == Unit.INFANTRY || mm.getType() == Unit.BATTLEARMOR ){
				    if ( ((Infantry)mm.getEntity()).isAntiMek() )
				        return (mm.getId() +" "+ mm.getModelName() + " [" + mm.getPilot().getGunnery() + "/" + mm.getPilot().getPiloting()+"]");
	                return (mm.getId() +" "+ mm.getModelName() + " [" + mm.getPilot().getGunnery() +"]");
				}
				return (mm.getId() +" "+ mm.getModelName() + " [" + mm.getPilot().getGunnery() +"]");
			}
		});
		
		Dimension newDim = new Dimension();
		newDim.setSize(possibleUnits.getMinimumSize().getWidth() * 1.25, possibleUnits.getMinimumSize().getHeight());
		possibleUnits.setMaximumSize(newDim);
	
		//set up listeners for the buttons
		okayButton.setActionCommand(okayCommand);
		okayButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		//do some formatting. rawr.
		JPanel springPanel = new JPanel(new SpringLayout());
		springPanel.add(comboLabel);
		springPanel.add(possibleUnits);
		SpringLayoutHelper.setupSpringGrid(springPanel,2,1);
		
		JPanel buttonFlow = new JPanel();
		buttonFlow.add(okayButton);
		buttonFlow.add(cancelButton);
		
		JPanel generalLayout = new JPanel();
		generalLayout.setLayout(new BoxLayout(generalLayout, BoxLayout.Y_AXIS));
		generalLayout.add(new JLabel("\n"));
		generalLayout.add(springPanel);
		generalLayout.add(new JLabel("\n"));
		generalLayout.add(buttonFlow);
		generalLayout.add(new JLabel("\n"));
		this.getContentPane().add(generalLayout);
		this.pack();
		
		this.checkMinimumSize();
		this.setResizable(true);
		
		//center the dialog.
		this.setLocationRelativeTo(null);
	}
	
	/**
	 * OK or CANCEL buttons pressed. Handle any
	 * changes and then close the dialouge.
	 */
	public void actionPerformed(ActionEvent event) {
		
		String command = event.getActionCommand();
		
		if (command.equals(okayCommand)) {
			
			int index = possibleUnits.getSelectedIndex();
			if (index < 0)
				return;
			
			String mms = (String)possibleUnits.getSelectedItem();
			StringTokenizer st = new StringTokenizer(mms);
			String id = st.nextToken();
			
			this.setUnitID(id);
			this.setVisible(false);
			//this.dispose();
			return;		
		}
		
		//dispose of the dialog
		this.dispose();
		
	}//end actionPerformed
	
	private void checkMinimumSize() {
		
		Dimension curDim = this.getSize();
		
		int height = 0;
		int width = 0;
		boolean shouldRedraw = false;
		
		if (curDim.getWidth() < 275) {
			width = 275;
			shouldRedraw = true;
		} else
			width = (int)curDim.getWidth();
		
		if (curDim.getHeight() < 200) {
			height = 200;
			shouldRedraw = true;
		} else
			height = (int)curDim.getHeight();
		
		if (shouldRedraw) {
			this.setSize(new Dimension(width, height));
		}
		
	}//end checkMinimumSize
	
	private void setUnitID(String id){
		this.toReturn = id;
	}
	
	public String getUnitID(){
		return this.toReturn;
	}
}

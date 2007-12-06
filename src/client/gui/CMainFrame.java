/*
 * MekWars - Copyright (C) 2004
 * 
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
 * Original author Helge Richter (McWizard)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package client.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.BoxLayout;

import megamek.MegaMek;
import megamek.client.ui.AWT.UnitLoadingDialog;

import client.CUser;
import client.MWClient;
import client.campaign.CCampaign;
import client.campaign.CPlayer;
import client.campaign.CArmy;
import client.campaign.CUnit;

import client.gui.dialog.*;//use all

import common.House;
import common.Unit;
import common.campaign.pilot.Pilot;
import common.util.StringUtils;

public class CMainFrame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1198882220815512476L;
	
	JPanel contentPane;
	
	JToolBar ToolBar = new JToolBar();
	
	JMenuBar jMenuBar1 = new JMenuBar();
	
	//FILE Menu
	JMenu jMenuFile = new JMenu();
	JMenuItem jMenuFileConnect = new JMenuItem();
	JMenuItem jMenuFileDisconnect = new JMenuItem();
	JMenuItem jMenuFileRegister = new JMenuItem();
	JMenuItem jMenuFileMail = new JMenuItem();
	JMenuItem jMenuFileLastOnline = new JMenuItem();
	JMenuItem jMenuFileExit = new JMenuItem();
	JMenuItem jMenuFileConfig = new JMenuItem();
	
	//CAMPAIGN Menu
	JMenu jMenuCampaign = new JMenu();
	
	JMenuItem jMenuCampaignSubStatus = new JMenu();//submenu in Campaign
	JMenuItem jMenuCampaignSubTechs = new JMenu();
	JMenuItem jMenuCampaignSubBays = new JMenu();
	JMenuItem jMenuCampaignSubTransfer = new JMenu();
	JMenuItem jMenuCampaignSubAttack = new JMenu();
	JMenuItem jMenuCampaignSubMerc = new JMenu();
	JMenuItem jMenuCampaignSubOther = new JMenu();
    
	JMenuItem jMenuCampaignMyStatus = new JMenuItem();
	
	JMenuItem jMenuCampaignLogin = new JMenuItem();
	JMenuItem jMenuCampaignActivate = new JMenuItem();
	JMenuItem jMenuCampaignDeactivate = new JMenuItem();
	JMenuItem jMenuCampaignLogout = new JMenuItem();
	
	JMenuItem jMenuCampaignPlayers = new JMenuItem();
	JMenuItem jMenuCampaignISStatus = new JMenuItem();
	JMenuItem jMenuCampaignHouses = new JMenuItem();
	
	JMenuItem jMenuCampaignHouseStatus = new JMenuItem();
	JMenuItem jMenuCampaignCheckAttack = new JMenuItem();
	JMenuItem jMenuCampaignRange = new JMenuItem();
	
	JMenuItem jMenuCampaignTransferUnit = new JMenuItem();
	JMenuItem jMenuCampaignTransferMoney = new JMenuItem();
	JMenuItem jMenuCampaignTransferPilot = new JMenuItem();
	
	JMenuItem jMenuCampaignLogo = new JMenuItem();
	JMenuItem jMenuCampaignPersonalPilotQueue = new JMenuItem();
	JMenuItem jMenuCampaignDonatePersonalPilot = new JMenuItem();
	JMenuItem jMenuCampaignDirectSell = new JMenuItem();
	JMenuItem jMenuCampaignDefect = new JMenuItem();
	JMenuItem jMenuCampaignRewardPoints = new JMenuItem();
	JMenuItem jMenuCampaignPartsCache = new JMenuItem();
	
	JMenuItem jMenuSubCampaignFireTechs = new JMenuItem();
	JMenuItem jMenuSubCampaignHireTechs = new JMenuItem();
	
	JMenuItem jMenuSubCampaignSellBays = new JMenuItem();
	JMenuItem jMenuSubCampaignBuyBays = new JMenuItem();
	
	JMenuItem jMenuCampaignBuyPilots = new JMenuItem();

	JMenuItem jMenuMercStatus = new JMenuItem();
	JMenuItem jMenuMercUnemployed = new JMenuItem();
	JMenuItem jMenuMercContracted = new JMenuItem();
	JMenuItem jMenuMercOfferContract = new JMenuItem();
	
	/*
	 * ATTACK/GAME Menu is a class unto itself and
	 * needs constant update calls.
	 */
	AttackMenu jMenuAttackMenu;
	
	//HOST Menu
	JMenu jMenuHost = new JMenu();
	
	JMenuItem jMenuCSHostAndJoin = new JMenuItem();
	JMenuItem jMenuCSHostDedicated = new JMenuItem();
	JMenuItem jMenuCSHostLoad = new JMenuItem();
	JMenuItem jMenuCSHostLoadAndJoin = new JMenuItem();
	JMenuItem jMenuCSHostStop = new JMenuItem();
	
	//OPTIONS menu components
	JMenu jMenuOptions = new JMenu();
	
	JCheckBoxMenuItem jMenuOptionsAutoScroll = new JCheckBoxMenuItem();
	JCheckBoxMenuItem jMenuOptionsMute = new JCheckBoxMenuItem();
    JMenuItem jMenuOptionsReloadAllData = new JMenuItem();
	
    //Leadership Menu
    JMenu jMenuLeaderShip = new JMenu();
    
	JMenuItem jMenuLeaderPromote = new JMenuItem();
	JMenuItem jMenuLeaderDemote = new JMenuItem();
	JMenuItem jMenuLeaderFluff = new JMenuItem();
	JMenuItem jMenuLeaderMute = new JMenuItem();

	//HELP Menu
	JMenu jMenuHelp = new JMenu();
	
	JMenuItem jMenuHelpAbout = new JMenuItem();
	JMenuItem jMenuHelpHelp = new JMenuItem();
	JMenuItem jMenuHelpViewUnit = new JMenuItem();
	JMenuItem jMenuHelpViewBuildTables = new JMenuItem();
	JMenuItem jMenuHelpViewTraits = new JMenuItem();
	JMenuItem jMenuHelpPilotSkills = new JMenuItem();
	
	//These are simple holders for when real menus
	//is generated/returned from  the admin plugin.
	JMenu jMenuMod = new JMenu();
	JMenu jMenuAdmin = new JMenu();
	JMenu jMenuOperations = new JMenu();
	
	public MWClient mwclient;
	
	CMainPanel MainPanel;	
	CCampaign theCampaign;
	CPlayer thePlayer;
	
	private boolean hasAdminMenus = false;
	boolean useAdvanceRepairs = false;
    boolean usePersonalPilotQueues = false;
	
	//CONSTRUCTOR
	public CMainFrame(MWClient myC) {
		mwclient = myC;
		theCampaign = mwclient.getCampaign();
		thePlayer = mwclient.getPlayer();
		MainPanel = new CMainPanel(mwclient, this);
		useAdvanceRepairs = mwclient.isUsingAdvanceRepairs();
        usePersonalPilotQueues = Boolean.parseBoolean(mwclient.getserverConfigs("AllowPersonalPilotQueues"));
		/*
		 * ATTACK/GAME Menu is a class unto itself and
		 * needs constant update calls. Have to build it
		 * here so its mwclient isn't null and it isn't
		 * being handed to createMenu() as a null itself.
		 */
		jMenuAttackMenu = new AttackMenu(mwclient, -1, "-1");
		
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setResizable(true);
		setSize(new Dimension(640, 480));
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setTitle(mwclient.getConfigParam("CAMPAIGNSERVERNAME") + " (MekWars Client " + MWClient.CLIENT_VERSION + ")");
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout());
		try {
			//factored out to reduce bloat
			createMenu();
		} catch (Exception e) {
			MWClient.mwClientLog.clientErrLog(e);
		}
		setJMenuBar(jMenuBar1);
		enableMenu();
		repaint();
		contentPane.add(MainPanel, BorderLayout.CENTER);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				if (mwclient.isServerRunning()) {
					int result = JOptionPane.showConfirmDialog(mwclient.getMainFrame(), "Are you sure you want to exit?","You are hosting a game!",JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						mwclient.goodbye();
						System.exit(0);
					}
				} else {
					mwclient.goodbye();
					System.exit(0);
				}
			}
		});
	}
	
	public CMainPanel getMainPanel() {
		return MainPanel;
	}

	//Initialising Components	
	public void enableMenu() {
		boolean disconnected = false;
		boolean loggedout = false;
		boolean loggedin = false;
		boolean reserve = false;
		boolean active = false;
		//boolean fighting = false;
		boolean admin = false;
		boolean mod = false;
		
		if (mwclient.getMyStatus() == MWClient.STATUS_DISCONNECTED) {
			disconnected = true;
		}
		if (mwclient.getMyStatus() == MWClient.STATUS_LOGGEDOUT) {
			loggedout = true;
		}
		if (mwclient.getMyStatus() == MWClient.STATUS_RESERVE) {
			loggedin = true;
			reserve = true;
		}
		if (mwclient.getMyStatus() == MWClient.STATUS_ACTIVE) {
			loggedin = true;
			active = true;
		}
		if (mwclient.getMyStatus() == MWClient.STATUS_FIGHTING) {
			loggedin = true;
			//fighting = true;
		}
		if (mwclient.isAdmin()) {
			admin = true;
		}
		if (mwclient.isMod()) {
			mod = true;
		}
		
		/*
		 * jMenuCampaign.setEnabled(!disconnected);
		 * jMenuCommander.setEnabled(loggedin); jMenuTask.setEnabled(active);
		 * jMenuHost.setEnabled(!disconnected);
		 * 
		 * jMenuFileConnect.setEnabled(disconnected);
		 * jMenuFileConnectTo.setEnabled(disconnected);
		 * jMenuFileRegister.setEnabled(!disconnected);
		 * jMenuFileMail.setEnabled(!disconnected);
		 * jMenuFileLastOnline.setEnabled(!disconnected);
		 * 
		 * jMenuCampaignTasks.setEnabled(loggedin);
		 * jMenuCampaignPlayers.setEnabled(loggedin);
		 * jMenuCampaignISStatus.setEnabled(loggedin);
		 * jMenuCampaignHouses.setEnabled(loggedin);
		 * jMenuCampaignPlanet.setEnabled(loggedin);
		 * jMenuCampaignPlanetRange.setEnabled(loggedin);
		 * jMenuCampaignBMStatus.setEnabled(loggedin);
		 */
		//    jMenuCampaignTraderStatus.setEnabled(loggedin);
		/*
		 * jMenuCampaignMercStatus.setEnabled(loggedin);
		 * jMenuCampaignUMercs.setEnabled(loggedin);
		 * jMenuCampaignTick.setEnabled(loggedin);
		 * 
		 * jMenuCampaignLogin.setEnabled(loggedout);
		 * jMenuCampaignActivate.setEnabled(reserve);
		 * jMenuCampaignDeactivate.setEnabled(active);
		 * jMenuCampaignLogout.setEnabled(loggedin);
		 * 
		 * jMenuCampaignEnroll.setEnabled(loggedout);
		 */
		//jMenuCampaignUnenroll.setEnabled(loggedin);
		
		//Client.errorMessage("Mod "+mod+" Admin "+admin+" Level "+Client.getUser(Client.getUsername()).getUserlevel()+" Status: "+this.getClient().getMyStatus()+" StatusII: "+Client.getMyStatus());
		
		
		if ( (mod || admin) && !hasAdminMenus ) {
			
			mwclient.loadServerCommmands();
			
			File loadJar = new File("./MekWarsAdmin.jar");
			
			//dont print an entire trace if the jar is missing.
			if (!loadJar.exists())
				MWClient.mwClientLog.clientErrLog("Player/Server menu creation skipped. No MekWarsAdmin.jar present.");
			else {
				//assume mod
				try {
					URLClassLoader loader = new URLClassLoader(new URL[] {loadJar.toURI().toURL()});
					Class c = loader.loadClass("admin.ModeratorMenu");
					Object o = c.newInstance();
					c.getDeclaredMethod("createMenu", new Class[] {MWClient.class}).invoke(o,new Object[] {mwclient});
					JMenu tempMenu = (JMenu)o;
					jMenuMod.setText(tempMenu.getText());
					/*for ( int i = 0; i < tempMenu.getMenuComponentCount();i++)
					 jMenuMod.add(tempMenu.getItem(i));*/
					jMenuBar1.add(tempMenu);
					jMenuBar1.remove(jMenuMod);
//					jMenuBar1.add((JMenu)o);
				} catch (Exception ex) {
					MWClient.mwClientLog.clientErrLog("ModeratorMenu creation FAILED!");
					MWClient.mwClientLog.clientErrLog(ex);
				}
				try {
					URLClassLoader loader = new URLClassLoader(new URL[] {loadJar.toURI().toURL()});
					Class c = loader.loadClass("admin.AdminMenu");
					Object o = c.newInstance();
					c.getDeclaredMethod("createMenu", new Class[] {MWClient.class}).invoke(o,new Object[] {mwclient});
					JMenu tempMenu = (JMenu)o;
					if ( tempMenu.getItemCount() > 0 )
						jMenuBar1.add(tempMenu);
					//jMenuBar1.remove(jMenuAdmin);
				} catch (Exception ex) {
					MWClient.mwClientLog.clientErrLog("AdminMenu creation FAILED!");
					MWClient.mwClientLog.clientErrLog(ex);
				}
			}//end else(Admin.jar exists)
			
			if ( new File("./MekWarsOpEditor.jar").exists() ){
				jMenuOperations.setText("Operations");
				JMenuItem item = new JMenuItem("Op Editor");
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try{
							URLClassLoader loader = new URLClassLoader(new URL[] {new File("./MekWarsOpEditor.jar").toURI().toURL()});
							Class c = loader.loadClass("OperationsEditor.MainOperations");
							Object o = c.newInstance();
							c.getDeclaredMethod("main", new Class[] {Object.class}).invoke(o,new Object[] {mwclient});
						
						}catch(Exception ex){
							ex.printStackTrace();
						}
						
						//new OperationsEditor.dialog.OperationsDialog(mwclient);
					}
				});
				jMenuOperations.add(item);
			    JMenuItem jMenuRetrieveOperationFile = new JMenuItem();
			    JMenuItem jMenuSetOperationFile = new JMenuItem();
			    JMenuItem jMenuSetNewOperationFile = new JMenuItem();
			    JMenuItem jMenuSendAllOperationFiles = new JMenuItem();
			    JMenuItem jMenuUpdateOperations= new JMenuItem();

		        jMenuRetrieveOperationFile.setText("Retrieve Operation File");
		        jMenuRetrieveOperationFile.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		                jMenuRetrieveOperationFile_actionPerformed(e);
		            }
		        });

		        jMenuSetOperationFile.setText("Set Operation File");
		        jMenuSetOperationFile.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		                jMenuSetOperationFile_actionPerformed(e);
		            }
		        });

		        jMenuSetNewOperationFile.setText("Set New Operation File");
		        jMenuSetNewOperationFile.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		                jMenuSetNewOperationFile_actionPerformed(e);
		            }
		        });

		        jMenuSendAllOperationFiles.setText("Send All Local Op Files");
		        jMenuSendAllOperationFiles.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		                jMenuSendAllOperationFiles_actionPerformed(e);
		            }
		        });

		        jMenuUpdateOperations.setText("Update Operations");
		        jMenuUpdateOperations.addActionListener(new ActionListener() {
		            public void actionPerformed(ActionEvent e) {
		                jMenuUpdateOperations_actionPerformed(e);
		            }
		        });

				int userLevel = this.mwclient.getUser(this.mwclient.getUsername()).getUserlevel();
		        if ( userLevel >= mwclient.getData().getAccessLevel("RetrieveOperation") )
		        	jMenuOperations.add(jMenuRetrieveOperationFile);
		        if ( userLevel >= mwclient.getData().getAccessLevel("SetOperation") ){
		        	jMenuOperations.add(jMenuSetOperationFile);
		        	jMenuOperations.add(jMenuSetNewOperationFile);
		        	jMenuOperations.add(jMenuSendAllOperationFiles);
		        }
		        if ( userLevel >= mwclient.getData().getAccessLevel("UpdateOperations") )
		        	jMenuOperations.add(jMenuUpdateOperations);

				jMenuBar1.add(jMenuOperations);
			}
			this.hasAdminMenus = true;
		}//end if(is admin or mod)
		
		
		//jMenuAdmin.setVisible(admin);
		jMenuMod.setVisible(mod);
		
		jMenuCampaign.setVisible(!disconnected);
		jMenuCampaignMyStatus.setVisible(loggedin);
		jMenuCampaignSubAttack.setVisible(loggedin);
		jMenuCampaignSubMerc.setVisible(loggedin);
		jMenuCampaignSubTransfer.setVisible(loggedin);
		jMenuCampaignSubStatus.setVisible(loggedin);
		jMenuCampaignSubTechs.setVisible(loggedin);
		jMenuCampaignSubBays.setVisible(useAdvanceRepairs && loggedin);
		jMenuCampaignSubOther.setVisible(loggedin);
		//jMenuTask.setVisible(loggedin);
		jMenuHost.setVisible(!disconnected);
		
        if ( mwclient.getUser(mwclient.getPlayer().getName()).getUserlevel() >= Integer.parseInt(mwclient.getserverConfigs("factionLeaderLevel")) )
        	jMenuLeaderShip.setVisible(true);
        else
        	jMenuLeaderShip.setVisible(false);
        

		jMenuFileConnect.setVisible(disconnected);
		jMenuFileDisconnect.setVisible(!disconnected);
		jMenuFileRegister.setVisible(!disconnected);
		jMenuFileMail.setVisible(!disconnected);
		jMenuFileLastOnline.setVisible(!disconnected);
		jMenuFileConfig.setVisible(true);
		
		jMenuCampaignLogin.setVisible(loggedout);
		jMenuCampaignActivate.setVisible(reserve);
		jMenuCampaignDeactivate.setVisible(active);
		jMenuCampaignLogout.setVisible(loggedin);
		jMenuCampaignPersonalPilotQueue.setVisible(usePersonalPilotQueues);
		jMenuCampaignTransferPilot.setVisible(usePersonalPilotQueues);
        jMenuCampaignDonatePersonalPilot.setVisible(usePersonalPilotQueues);
		jMenuCampaignDirectSell.setVisible(Boolean.parseBoolean(mwclient.getserverConfigs("UseDirectSell")));
		
	}
	
	protected void createMenu() throws Exception {
		jMenuFile.setText("File");
		jMenuFile.setMnemonic('F');
		
		jMenuFileConnect.setText("Connect");
		jMenuFileConnect.setMnemonic('o');
		jMenuFileConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileConnect_actionPerformed();
			}
		});
		
		jMenuFileDisconnect.setText("Disconnect");
		jMenuFileDisconnect.setMnemonic('D');
		jMenuFileDisconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mwclient.getConnector().closeConnection();
			}
		});
		
		jMenuFileRegister.setText("Register Nickname");
		jMenuFileRegister.setMnemonic('R');
		jMenuFileRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileRegister_actionPerformed();
			}
		});
		
		jMenuFileMail.setText("Mail User");
		jMenuFileMail.setMnemonic('M');
		jMenuFileMail.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileMail_actionPerformed(null);
			}
		});
		
		jMenuFileLastOnline.setText("Last Online");
		jMenuFileLastOnline.setMnemonic('L');
		jMenuFileLastOnline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileLastOnline_actionPerformed();
			}
		});
		
		jMenuFileConfig.setText("Configuration");
		jMenuFileConfig.setMnemonic('C');
		jMenuFileConfig.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new ConfigurationDialog(mwclient);
			}
		});
		
		jMenuFileExit.setText("Exit");
		jMenuFileExit.setMnemonic('X');
		jMenuFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileExit_actionPerformed();
			}
		});
		
		jMenuCampaign.setText("Campaign");
		jMenuCampaign.setMnemonic('C');
		
		jMenuCampaignLogin.setText("Log in!");
		jMenuCampaignLogin.setMnemonic('I');
		jMenuCampaignLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c login");
			}
		});
		
		jMenuCampaignActivate.setText("Activate");
		//    jMenuCampaignActivate.setMnemonic('A');
		jMenuCampaignActivate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c activate#"+MWClient.CLIENT_VERSION);
			}
		});
		
		jMenuCampaignDeactivate.setText("Deactivate");
		//    jMenuCampaignDeactivate.setMnemonic('R');
		jMenuCampaignDeactivate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c deactivate");
			}
		});
		
		jMenuCampaignLogout.setText("Log Out");
		//    jMenuCampaignLogout.setMnemonic('O');
		jMenuCampaignLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c logout");
			}
		});
		
		jMenuCampaignPlayers.setText("Players Status");
		jMenuCampaignPlayers.setMnemonic('P');
		jMenuCampaignPlayers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c players");
			}
		});
		
		jMenuCampaignISStatus.setText("Planetary Control");
		jMenuCampaignISStatus.setMnemonic('C');
		jMenuCampaignISStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCampaignISStatus_actionPerformed();
			}
		});
		
		jMenuCampaignHouses.setText("Houses Status");
		jMenuCampaignHouses.setMnemonic('H');
		jMenuCampaignHouses.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c housestatus");
			}
		});
		
		jMenuCampaign.setText("Campaign");
		jMenuCampaign.setMnemonic('C');
		
		if ( useAdvanceRepairs ){
			jMenuCampaignSubBays.setText("Bays");
			jMenuCampaignSubBays.setMnemonic('B');
		}
		jMenuCampaignSubTechs.setText("Personnel");
		jMenuCampaignSubTechs.setMnemonic('E');
		
		jMenuCampaignSubTransfer.setText("Transfer");
		jMenuCampaignSubTransfer.setMnemonic('T');
		
		jMenuCampaignSubAttack.setText("Front Line");
		jMenuCampaignSubAttack.setMnemonic('F');
		
		jMenuCampaignSubStatus.setText("Status");
		jMenuCampaignSubStatus.setMnemonic('U');
		
		jMenuCampaignSubOther.setText("Other");
		jMenuCampaignSubOther.setMnemonic('O');
		
		jMenuCampaignMyStatus.setText("My Status");
		jMenuCampaignMyStatus.setMnemonic('M');
		jMenuCampaignMyStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c mystatus");
			}
		});
		
		//jMenuCampaignHouseStatus.setText("My House Status");
		//jMenuCampaignHouseStatus.setMnemonic('H');
		//jMenuCampaignHouseStatus.addActionListener(new ActionListener() {
		//    public void actionPerformed(ActionEvent e) {
		//        Client.sendChat(MWClient.CAMPAIGN_PREFIX + "c status");
		//    }
		//});
		
		jMenuCampaignCheckAttack.setText("Attack Options");
		jMenuCampaignCheckAttack.setMnemonic('A');
		jMenuCampaignCheckAttack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCommanderCheckAttack_actionPerformed(-1);
			}
		});
		
		jMenuCampaignRange.setText("Range Calculator");
		jMenuCampaignRange.setMnemonic('R');
		jMenuCampaignRange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCommanderRange_actionPerformed();
			}
		});
		
		jMenuCampaignTransferUnit.setText("Transfer Unit");
		jMenuCampaignTransferUnit.setMnemonic('U');
		jMenuCampaignTransferUnit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCommanderTransferUnit_actionPerformed(null, -1);
			}
		});
		
		jMenuCampaignTransferMoney.setText("Transfer " + mwclient.moneyOrFluMessage(true,true,-2));
		jMenuCampaignTransferMoney.setMnemonic('C');
		jMenuCampaignTransferMoney.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCommanderTransferMoney_actionPerformed(null);
			}
		});
		
		jMenuCampaignLogo.setText("Set Logo");
		jMenuCampaignLogo.setMnemonic('L');
		jMenuCampaignLogo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCommanderLogo_actionPerformed();
			}
		});
		
		jMenuCampaignPersonalPilotQueue.setText("View Pilot Queue");
		jMenuCampaignPersonalPilotQueue.setMnemonic('Q');
		jMenuCampaignPersonalPilotQueue.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCommanderPersonalPilotQueue_actionPerformed();
			}
		});
		
		jMenuCampaignDonatePersonalPilot.setText("Donate Pilot");
		jMenuCampaignDonatePersonalPilot.setMnemonic('o');
		jMenuCampaignDonatePersonalPilot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCommanderDonatePersonalPilot_actionPerformed();
			}
		});
		
		jMenuCampaignDirectSell.setText("Direct Sell Unit");
		jMenuCampaignDirectSell.setMnemonic('S');
		jMenuCampaignDirectSell.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCommanderDirectSell_actionPerformed(null,null);
			}
		});
		
		jMenuCampaignTransferPilot.setText("Send Pilot");
		jMenuCampaignTransferPilot.setMnemonic('T');
		jMenuCampaignTransferPilot.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCommanderTransferPilot_actionPerformed(null);
			}
		});
		
		jMenuCampaignDefect.setText("Defect");
		jMenuCampaignDefect.setMnemonic('D');
		jMenuCampaignDefect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCommanderDefect_actionPerformed();
			}
		});
				
		jMenuCampaignRewardPoints.setText("Use Reward Points");
		jMenuCampaignRewardPoints.setMnemonic('P');
		jMenuCampaignRewardPoints.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mwclient.rewardPointsDialog();
			}
		});
		
		jMenuCampaignPartsCache.setText("View Parts");
		jMenuCampaignPartsCache.setMnemonic('V');
		jMenuCampaignPartsCache.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCampaignPartsCache_actionPerformed();
			}
		});
		
		if ( useAdvanceRepairs ){
			jMenuSubCampaignBuyBays.setText("Lease Bays");
			jMenuSubCampaignBuyBays.setMnemonic('L');
			jMenuSubCampaignBuyBays.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jMenuCommanderBuyBays_actionPerformed();
				}
			});
			
			jMenuSubCampaignSellBays.setText("Return Bays");
			jMenuSubCampaignSellBays.setMnemonic('R');
			jMenuSubCampaignSellBays.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					jMenuCommanderSellBays_actionPerformed();
				}
			});
		}
		
		if ( usePersonalPilotQueues ) {
			jMenuCampaignBuyPilots.setText("Buy Pilots");
			jMenuCampaignBuyPilots.setMnemonic('P');
			jMenuCampaignBuyPilots.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
			    	jMenuCampaignSubOtherBuyPilots_actionPerformed();
			    }
			});
		}
		
		jMenuSubCampaignHireTechs.setText("Hire Techs");
		jMenuSubCampaignHireTechs.setMnemonic('H');
		jMenuSubCampaignHireTechs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCommanderHireTechs_actionPerformed();
			}
		});
		
		jMenuSubCampaignFireTechs.setText("Fire Techs");
		jMenuSubCampaignFireTechs.setMnemonic('F');
		jMenuSubCampaignFireTechs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuCommanderFireTechs_actionPerformed();
			}
		});
		
		jMenuCampaignSubMerc.setText("Mercenaries");
		jMenuCampaignSubMerc.setMnemonic('r');
		
		jMenuMercOfferContract.setText("Offer a Mercenary Contract");
		jMenuMercOfferContract.setMnemonic('O');
		jMenuMercOfferContract.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuMercOfferContract_actionPerformed();
			}
		});
		
		jMenuMercStatus.setText("Mercenary Status");
		jMenuMercStatus.setMnemonic('M');
		jMenuMercStatus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuMercStatus_actionPerformed();
			}
		});
		jMenuMercUnemployed.setText("Unemployed Mercs");
		jMenuMercUnemployed.setMnemonic('U');
		jMenuMercUnemployed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c unemployedmercs");
			}
		});
		jMenuMercContracted.setText("Contracted Mercs");
		jMenuMercContracted.setMnemonic('C');
		jMenuMercContracted.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c housecontracts");
			}
		});
		
		//jMenuTask.setText("Task");
		//jMenuTask.setMnemonic('T');
		
		jMenuHost.setText("Host");
		jMenuHost.setMnemonic('S');
		
		jMenuCSHostAndJoin.setText("Start Hosting (and Join)");
		jMenuCSHostAndJoin.setMnemonic('H');
		jMenuCSHostAndJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startHost();
				mwclient.startHost(false, true, false);
			}
		});
		
		jMenuCSHostDedicated.setText("Start Dedicated Host");
		jMenuCSHostDedicated.setMnemonic('D');
		jMenuCSHostDedicated.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startHost();
				mwclient.startHost(true, false, false);
			}
		});
		
		jMenuCSHostLoad.setText("Start Dedicated Host (Load Savegame)");
		jMenuCSHostLoad.setMnemonic('L');
		jMenuCSHostLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startHost();
				mwclient.startHost(true, false, true);
			}
		});
		
		jMenuCSHostLoadAndJoin.setText("Start Hosting (Load Savegame and Join)");
		jMenuCSHostLoadAndJoin.setMnemonic('S');
		jMenuCSHostLoadAndJoin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startHost();
				mwclient.startHost(false, false, true);
			}
		});
		
		jMenuCSHostStop.setText("Stop Hosting");
		jMenuCSHostStop.setMnemonic('S');
		jMenuCSHostStop.setEnabled(false);
		jMenuCSHostStop.setVisible(false);
		jMenuCSHostStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopHost();
				mwclient.stopHost();
			}
		});
		
		jMenuOptions.setText("Options");
		jMenuOptions.setMnemonic('I');
		
		jMenuOptionsAutoScroll.setText("Auto Scroll");
		jMenuOptionsAutoScroll.setMnemonic('A');
		jMenuOptionsAutoScroll.setState(MainPanel.getCommPanel().autoTextUpdate);
		jMenuOptionsAutoScroll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				boolean newValue = !MainPanel.getCommPanel().autoTextUpdate;
				MainPanel.getCommPanel().autoTextUpdate = newValue;
				jMenuOptionsAutoScroll.setState(newValue);
				
                mwclient.getConfig().setParam("AUTOSCROLL", Boolean.toString(newValue));
				mwclient.getConfig().saveConfig();
			}
		});
		
		jMenuOptionsMute.setText("Mute");
		jMenuOptionsMute.setMnemonic('M');
		jMenuOptionsMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mwclient.setSoundMuted(jMenuOptionsMute.getState());
			}
		});
        
        
        jMenuOptionsReloadAllData.setText("Reload Data");
        jMenuOptionsReloadAllData.setMnemonic('D');
        jMenuOptionsReloadAllData.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mwclient.reloadData();
            }
        });

        jMenuLeaderShip.setText("LeaderShip");

        jMenuLeaderPromote.setText("Promote Player");
        jMenuLeaderPromote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuLeaderPromote_actionPerformed();
			}
		});
        
        jMenuLeaderDemote.setText("Demote Player");
        jMenuLeaderDemote.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuLeaderDemote_actionPerformed();
			}
		});
        
        jMenuLeaderFluff.setText("Fluff Player");
        jMenuLeaderFluff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuLeaderFluff_actionPerformed();
			}
		});
        
        jMenuLeaderMute.setText("Mute Player");
        jMenuLeaderMute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuLeaderMute_actionPerformed();
			}
		});
        
        jMenuHelp.setText("Help");
		jMenuHelp.setMnemonic('E');
		
		jMenuHelpAbout.setText("About");
		jMenuHelpAbout.setMnemonic('A');
		jMenuHelpAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuHelpAbout_actionPerformed();
			}
		});
		
		jMenuHelpHelp.setText("Online Help");
		jMenuHelpHelp.setMnemonic('H');
		jMenuHelpHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuHelpHelp_actionPerformed();
			}
		});
		
		jMenuHelpViewUnit.setText("Unit Viewer");
		jMenuHelpViewUnit.setMnemonic('U');
		jMenuHelpViewUnit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuHelpViewUnit_actionPerformed();
			}
		});
		
		jMenuHelpViewBuildTables.setText("Build Table Viewer");
		jMenuHelpViewBuildTables.setMnemonic('B');
		jMenuHelpViewBuildTables.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*
				 * Show the client side GUI if the requisite
				 * file is available. Otherwise, make use of
				 * server commands.
				 */
				File f = new File("./data/buildtables.zip");
				if (f.exists()) {
					//TableViewerDialog tvDialog = 
					new TableViewerDialog(mwclient);
				} else {
					mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c buildtablelist");
				}           	
			}
		});
		
		jMenuHelpViewTraits.setText("View Faction Traits");
		jMenuHelpViewTraits.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new TraitDialog(mwclient,true);
			}
		});
		
		jMenuHelpPilotSkills.setText("Pilot Skill Descriptions");
		jMenuHelpPilotSkills.setMnemonic('P');
		jMenuHelpPilotSkills.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuHelpPilotSkills_actionPerformed();
			}
		});
		
		/*
		 * Display Report "MekWars Bug" and "Report MegaMek Bug" links in the
		 * Help Menu. Create the actual menu options, with browsers calls, here
		 * in order to add them to the menu in the formatting blocks that
		 * follow. These are hardcoded. Server ops can add their own links with
		 * the links.txt detailed above. @urgru 12.5.04
		 */
		JMenuItem jMenuMekwarsBug = new JMenuItem("Report Bug (MekWars)");
		JMenuItem jMenuMegamekBug = new JMenuItem("Report Bug (MegaMek)");
		JMenuItem jMenuMekwarsRFE = new JMenuItem("RFE (MekWars)");
		JMenuItem jMenuMegamekRFE = new JMenuItem("RFE (MegaMek)");
		ActionListener mekwarsListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Browser.displayURL("http://sourceforge.net/tracker/?group_id=122002&atid=692058");
				} catch (Exception ex) {
					MWClient.mwClientLog.clientErrLog(ex);
				}
			}
		};
		ActionListener megamekListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Browser.displayURL("http://sourceforge.net/tracker/?group_id=47079&atid=448394");
				} catch (Exception ex) {
					MWClient.mwClientLog.clientErrLog(ex);
				}
			}
		};
		ActionListener megamekRFEListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Browser.displayURL("http://sourceforge.net/tracker/?group_id=47079&atid=448397");
				} catch (Exception ex) {
					MWClient.mwClientLog.clientErrLog(ex);
				}
			}
		};
		ActionListener mekwarsRFEListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Browser.displayURL("http://sourceforge.net/tracker/?group_id=122002&atid=692061");
				} catch (Exception ex) {
					MWClient.mwClientLog.clientErrLog(ex);
				}
			}
		};
		jMenuMekwarsBug.addActionListener(mekwarsListener);
		jMenuMegamekBug.addActionListener(megamekListener);
		jMenuMegamekRFE.addActionListener(megamekRFEListener);
		jMenuMekwarsRFE.addActionListener(mekwarsRFEListener);
		
		/*
		 * FORMATTING BLOCK
		 */
		jMenuFile.add(jMenuFileConnect);
		jMenuFile.add(jMenuFileRegister);
		jMenuFile.add(jMenuFileMail);
		jMenuFile.add(jMenuFileLastOnline);
		jMenuFile.add(jMenuFileConfig);
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileDisconnect);
		jMenuFile.add(jMenuFileExit);
		//jMenuFile.add(jMenuFileDebugPlayer);
		
		/*
		 * Put together the campaign menu. Start by assembling the sub-menus,
		 * then add the manus and line items all together ...
		 */
		
		//front-line submenu
		jMenuCampaignSubAttack.add(jMenuCampaignCheckAttack);
		jMenuCampaignSubAttack.add(jMenuCampaignRange);
		
		//send submenu
		jMenuCampaignSubTransfer.add(jMenuCampaignTransferMoney);
		jMenuCampaignSubTransfer.add(jMenuCampaignTransferUnit);
		jMenuCampaignSubTransfer.add(jMenuCampaignTransferPilot);
		
		//techs sub menu
		jMenuCampaignSubTechs.add(jMenuSubCampaignHireTechs);
		jMenuCampaignSubTechs.add(jMenuSubCampaignFireTechs);
        //Pilots sub menus.
		if ( usePersonalPilotQueues ) {
			jMenuCampaignSubTechs.add(jMenuCampaignPersonalPilotQueue);
			jMenuCampaignSubTechs.add(jMenuCampaignBuyPilots);
			jMenuCampaignSubTechs.add(jMenuCampaignDonatePersonalPilot);
		}
		
		//Bay sub menu
		jMenuCampaignSubBays.add(jMenuSubCampaignBuyBays);
		jMenuCampaignSubBays.add(jMenuSubCampaignSellBays);
		
        //status sub menu
		jMenuCampaignSubStatus.add(jMenuCampaignPlayers);
		jMenuCampaignSubStatus.add(jMenuCampaignISStatus);
		jMenuCampaignSubStatus.add(jMenuCampaignHouses);
		
		//mercs sub menu
		jMenuCampaignSubMerc.add(jMenuMercStatus);
		jMenuCampaignSubMerc.add(jMenuMercUnemployed);
		jMenuCampaignSubMerc.add(jMenuMercContracted);
		jMenuCampaignSubMerc.add(jMenuMercOfferContract);
		
		//other sub menu
		jMenuCampaignSubOther.add(jMenuCampaignLogo);
		jMenuCampaignSubOther.add(jMenuCampaignDefect);
		jMenuCampaignSubOther.add(jMenuCampaignRewardPoints);
		if ( Boolean.parseBoolean(mwclient.getserverConfigs("UsePartsBlackMarket")) ) 
				jMenuCampaignSubOther.add(jMenuCampaignPartsCache);

		jMenuCampaignSubOther.add(jMenuCampaignDirectSell);
		
		//assemble the actual campaign menu...
		
		jMenuCampaign.add(jMenuCampaignMyStatus);
		//jMenuCommander.add(jMenuCampaignHouseStatus);
		jMenuCampaign.add(jMenuCampaignSubAttack);
		jMenuCampaign.addSeparator();
		jMenuCampaign.add(jMenuCampaignSubMerc);
		jMenuCampaign.add(jMenuCampaignSubTransfer);
		jMenuCampaign.add(jMenuCampaignSubStatus);
		jMenuCampaign.add(jMenuCampaignSubBays);
		jMenuCampaign.add(jMenuCampaignSubTechs);
		jMenuCampaign.add(jMenuCampaignSubOther);
		jMenuCampaign.addSeparator();
		jMenuCampaign.add(jMenuCampaignLogin);
		jMenuCampaign.add(jMenuCampaignActivate);
		jMenuCampaign.add(jMenuCampaignDeactivate);
		jMenuCampaign.add(jMenuCampaignLogout);
		/*
		 * Games menu is assembled in a Menu Factory
		 */
		
		//assemble host menu
		jMenuHost.add(jMenuCSHostAndJoin);
		jMenuHost.add(jMenuCSHostLoadAndJoin);
		jMenuHost.add(jMenuCSHostDedicated);
		jMenuHost.add(jMenuCSHostLoad);
		jMenuHost.add(jMenuCSHostStop);
		
		jMenuOptions.add(jMenuOptionsAutoScroll);
		jMenuOptions.add(jMenuOptionsMute);
		jMenuOptions.addSeparator();
        jMenuOptions.add(jMenuOptionsReloadAllData);
        
        jMenuLeaderShip.add(jMenuLeaderFluff);
        jMenuLeaderShip.add(jMenuLeaderMute);
        jMenuLeaderShip.add(jMenuLeaderPromote);
        jMenuLeaderShip.add(jMenuLeaderDemote);
				
		jMenuHelp.add(jMenuHelpAbout);
		jMenuHelp.addSeparator();
		jMenuHelp.add(jMenuHelpViewUnit);
		jMenuHelp.add(jMenuHelpViewBuildTables);
		
		/*
		 * Only add the trait viewer if the server allows traits.
		 * We'll use BattleMech traits as a proxy for ALL trait
		 * types when deciding whether or not to show.
		 */
		if (Integer.parseInt(mwclient.getserverConfigs("chanceforTNforMek")) > 0)
			jMenuHelp.add(jMenuHelpViewTraits);
		
		jMenuHelp.add(jMenuHelpHelp);
		jMenuHelp.add(jMenuHelpPilotSkills);
		jMenuHelp.addSeparator();
		jMenuHelp.add(jMenuMekwarsBug);
		jMenuHelp.add(jMenuMegamekBug);
		jMenuHelp.addSeparator();
		jMenuHelp.add(jMenuMekwarsRFE);
		jMenuHelp.add(jMenuMegamekRFE);
		
		/*
		 * Admin menu setup used to be here.
		 * @urgru
		 */
		
		/*
		 * Mod menu setup used to be here.
		 * @urgru
		 */
		
		jMenuBar1.add(jMenuFile);
		jMenuBar1.add(jMenuCampaign);
		jMenuBar1.add(jMenuAttackMenu);
		jMenuBar1.add(jMenuHost);
		jMenuBar1.add(jMenuOptions);
		jMenuBar1.add(jMenuLeaderShip);
		jMenuBar1.add(jMenuHelp);
		jMenuBar1.add(jMenuMod);
		//jMenuBar1.add(jMenuAdmin);
	}
	
	public void jMenuFileNick_actionPerformed() {
		String NewNick, Password;
		NewNick = JOptionPane.showInputDialog(this.getContentPane(), "NewNick");
		if (NewNick == null)
			return;
		Password = JOptionPane.showInputDialog(this.getContentPane(), "Message");
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "nick " + NewNick + "," + Password);
	}
	
	public void jMenuFileConnect_actionPerformed() {
		mwclient.connectToServer();
		//Set Version upon reconnect.
		if ( !mwclient.getStatus().equals("Not connected") ) {
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setclientversion#" + mwclient.myUsername.trim()+ "#" + MWClient.CLIENT_VERSION);
		}
	}
	
	public void jMenuFileRegister_actionPerformed() {
		new RegisterNameDialog(mwclient);
	}
	
	public void jMenuFileMail_actionPerformed(String Nickname) {
		String message;
		if (Nickname == null) {
			Nickname = JOptionPane.showInputDialog(this.getContentPane(), "Nickname","Send mail to whom?", JOptionPane.PLAIN_MESSAGE);
			if (Nickname == null)
				return;
		}
		message = JOptionPane.showInputDialog(this.getContentPane(), "message","Send mail to " + Nickname, JOptionPane.PLAIN_MESSAGE);
		if (message == null)
			return;
		mwclient.processGUIInput(MWClient.GUI_PREFIX + "mail " + Nickname + "," + message);
	}
	
	public void jMenuFileLastOnline_actionPerformed() {
		String Nickname;
		Nickname = JOptionPane.showInputDialog(this.getContentPane(),"Player name?");
		if (Nickname == null)
			return;
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c lastonline#" + Nickname);
	}
	
	public void jMenuFileExit_actionPerformed() {
		mwclient.goodbye();
		System.exit(0);
	}
	
	public void jMenuCampaignISStatus_actionPerformed() {
		
		String House = "";
		String House2 = "";
		
		HouseNameDialog factionDialog = new HouseNameDialog(mwclient, "Faction",true,false);
		factionDialog.setVisible(true);
		House = factionDialog.getHouseName();
		factionDialog.dispose();
		
		if (House == null)
			return;
		
		if (!House.equals("")) {
			factionDialog = new HouseNameDialog(mwclient, "Secondary Faction", true,false);
			factionDialog.setVisible(true);
			House2 = factionDialog.getHouseName();
			factionDialog.dispose();
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c isstatus#" + House + "#" + House2);
		} else
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c isstatus");
	}
	
	public void jMenuMercStatus_actionPerformed() {
		PlayerNameDialog playerDialog = new PlayerNameDialog(mwclient,"Which Merc do you want info on?", PlayerNameDialog.MERCS_ONLY);
		playerDialog.setVisible(true);
		String Merc = playerDialog.getPlayerName();
		playerDialog.dispose();
		
		if (Merc == null)
			return;
		
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c mstatus#" + Merc);
	}
	
	public void jMenuCommanderCheckAttack_actionPerformed(int lid) {
		if (lid == -1) {
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c ca");
		} else
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c ca#" + lid);
	}
	
	public void jMenuCommanderRange_actionPerformed() {
		String range;
		String faction;
		
		range = JOptionPane.showInputDialog(this.getContentPane(), "Max distance in Lightyears?");
		if (range == null || range.length() < 1)
			return;
		
		HouseNameDialog factionDialog = new HouseNameDialog(mwclient, "Faction", false,false);
		factionDialog.setVisible(true);
		faction = factionDialog.getHouseName();
		factionDialog.dispose();
		
		if (faction == null || faction.length() < 1)
			return;
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c range#" + range + "#" + faction);
	}
	
	public void jMenuCommanderTransferMoney_actionPerformed(String name) {
		
		String targetPlayer;
		String Amount;
		
		if (name == null || name.trim().equals("")) {
			PlayerNameDialog pnd = new PlayerNameDialog(mwclient, "Transfer Recipient", PlayerNameDialog.FACTION_ONLY);
			pnd.setVisible(true);
			targetPlayer = pnd.getPlayerName();
			pnd.dispose();
		} else
			targetPlayer = name;
		
		if (targetPlayer == null)
			return;
		
		Amount = JOptionPane.showInputDialog(this.getContentPane(), "Amount",
				"Send " + mwclient.moneyOrFluMessage(true,true,-2) + " to "
				+ targetPlayer, JOptionPane.PLAIN_MESSAGE);
		
		if (Amount == null)
			return;
		
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c transfermoney#" + targetPlayer + "#" + Amount);
	}
	
	public void jMenuCommanderTransferUnit_actionPerformed(String name, int mid) {
		
		String targetPlayer;
		
		if (name == null || name.trim().equals("")) {
			PlayerNameDialog pnd = new PlayerNameDialog(mwclient, "Transfer Recipient", PlayerNameDialog.FACTION_ONLY);
			pnd.setVisible(true);
			targetPlayer = pnd.getPlayerName();
			pnd.dispose();
		} else
			targetPlayer = name;
		
		if (targetPlayer == null)
			return;
		
		if (mid == -1) {
			UnitSelectionDialog usd = new UnitSelectionDialog(mwclient, "Transfer Unit", "Select unit to transfer:");
			usd.setVisible(true);
			mid = Integer.parseInt(usd.getUnitID());
			usd.dispose();
		}
		
		if (mid == -1)
			return;
		
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c transferunit#" + targetPlayer + "#" + mid);
	}
	
	public void jMenuCommanderAddToBM_actionPerformed(int mid) {
		
		Vector<CUnit> toSell = new Vector<CUnit>(1,1);
		toSell.add(mwclient.getPlayer().getUnit(mid));
		
		SellUnitDialog sud = new SellUnitDialog(this, mwclient, toSell);
		sud.setVisible(true);
	}
	
	public void jMenuCommanderRemoveLance_actionPerformed(int lid) {
		String LanceID;
		if (lid == -1) {
			LanceID = JOptionPane.showInputDialog(this.getContentPane(),
			"Army ID?");
			if (LanceID == null)
				return;
			lid = Integer.parseInt(LanceID);
		}
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c rma#" + lid);
	}
	
	/*
	 * Only called from HQ, via MechTableMouseAdapter. Will always have valid unit id.
	 */
	public void jMenuCommanderNamePilot_actionPerformed(int uid) {
		String newName = JOptionPane.showInputDialog(this.getContentPane(), "Pilot's Name?");
		if (newName == null)
			return;
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c namepilot#" + uid + "#" + newName);
	}
	
	/*
	 * Only called from HQ, via MechTableMouseAdapter. Will always have valid army id.
	 */
	public void jMenuCommanderNameArmy_actionPerformed(int aid) {
		CArmy selectedArmy = mwclient.getPlayer().getArmies().get(aid);
		if (selectedArmy == null)
			return;
		
		String newName = JOptionPane.showInputDialog(this.getContentPane(), "New army name? [Leave blank to clear]", selectedArmy.getName());
		if (newName == null)
			return;
		
		if (newName.trim().length() == 0)
			newName = "clear";
		
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c namearmy#" + aid + "#" + newName);
	}
	
	public void jMenuCommanderPlayerLockArmy_actionPerformed(int aid) {
		
//		CArmy selectedArmy = mwclient.getPlayer().getArmies().get(aid);
//		if(selectedArmy == null)
//			return;
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c playerlockarmy#" + aid);
	}
	
	public void jMenuCommanderPlayerUnlockArmy_actionPerformed(int aid) {
//		CArmy selectedArmy = mwclient.getPlayer().getArmies().get(aid);
//		if(selectedArmy == null)
//			return;
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c playerunlockarmy#" + aid);
	}
	
	/*
	 * Only called from HQ, via MechTableMouseAdapter. Will always have valid army id.
	 */
	public void jMenuCommanderSetLowerUnitLimit_actionPerformed(int aid) {
		
		CArmy selectedArmy = mwclient.getPlayer().getArmies().get(aid);
		if (selectedArmy == null)
			return;
		
		int newLimit = -1;
		
		String example = ""
			+ "Example: An Army of 8 units with a Lower Limit of<br>"
			+ "4 will not be able to fight an Army with only 3 units.<br>"
			+ "This can be useful if you want to avoid fighting a<br>"
			+ "small number of super heavy/levelled units.";
		
		String limit = JOptionPane.showInputDialog(this.getContentPane(),
				"<HTML>" + "Lower Limit? [-1 to disable the limit]<i><br><br>"
				+ example + "<br></i></HTML>", Integer.toString(selectedArmy.getLowerLimiter()),
				JOptionPane.PLAIN_MESSAGE);
		
		if (limit == null)
			return;
		
		newLimit = Integer.parseInt(limit);
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c all#" + aid + "#" + newLimit);
	}
	
	/*
	 * Only called from HQ, via MechTableMouseAdapter. Will always have valid army id.
	 */
	public void jMenuCommanderSetUpperUnitLimit_actionPerformed(int aid) {
		
		CArmy selectedArmy = mwclient.getPlayer().getArmies().get(aid);
		if (selectedArmy == null)
			return;
		
		int newLimit = -1;
		
		//generate an example string.
		String example = ""
			+ "Example: An Army of 4 units with an Upper Limit of 5<br>"
			+ "will not be able to fight againt Armies with more than<br>"
			+ "9 units. This can be useful if you don't want to play<br>"
			+ "againts swarms";
		
		String limit = JOptionPane.showInputDialog(this.getContentPane(),
				"<HTML>" + "Upper Limit? [-1 to disable the limit]<i><br><br>" + example
				+ "<br></i></HTML>", Integer.toString(selectedArmy.getLowerLimiter()).toString(),
				JOptionPane.PLAIN_MESSAGE);
		
		if (limit == null)
			return;
		
		newLimit = Integer.parseInt(limit);
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c aul#" + aid + "#" + newLimit);
	}
	
	/*
	 * Only called from HQ, via MechTableMouseAdapter. Will always have valid army id.
	 */
	public void jMenuCommanderSetForceSizeToFace_actionPerformed(int aid) {
		
		CArmy selectedArmy = mwclient.getPlayer().getArmies().get(aid);
		if (selectedArmy == null)
			return;
		
		//generate an example string.
		String example = ""
			+ "This is the force size you expect to face when you request a match";
		
		String force = JOptionPane.showInputDialog(this.getContentPane(),
				"<HTML>" + "Force Size To Face	? [-1 to disable the limit]<i><br><br>" + example
				+ "<br></i></HTML>", Float.toString(selectedArmy.getOpForceSize()),
				JOptionPane.PLAIN_MESSAGE);
		
		if (force == null)
			return;

		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c aofs#" + aid + "#" + force);
		
	}
	
	public void jMenuCommanderLogo_actionPerformed() {
		String LogoURL;
		LogoURL = JOptionPane.showInputDialog(this.getContentPane(), "URL? (i.e. http://www.mysite.com/mypic.jpg)",mwclient.getPlayer().getMyLogo());
		if (LogoURL == null)
			return;
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setmylogo#" + LogoURL);
	}
	
	public void jMenuCommanderPersonalPilotQueue_actionPerformed() {
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c displayplayerpersonalpilotqueue");
	}
	
	public void jMenuCommanderTransferPilot_actionPerformed(String name) {
		
		//get player
		String targetPlayer;
		
		if (name == null || name.trim().equals("")) {
			PlayerNameDialog pnd = new PlayerNameDialog(mwclient, "Transfer Recipient", PlayerNameDialog.FACTION_ONLY);
			pnd.setVisible(true);
			targetPlayer = pnd.getPlayerName();
			pnd.dispose();
		} else
			targetPlayer = name;
		
		if (targetPlayer == null)
			return;
		
		//arrays for message box usage
		Object[] pWeightClass = { "Light", "Medium", "Heavy", "Assault" };
		Object[] pUnitType = { "Mek", "Proto" };
		
		int weightClass = 0;
		int unitType = 0;
		
		//determine the unit type to use
		String pUnitTypeString = (String) JOptionPane.showInputDialog(mwclient.getMainFrame(),
				"Select a pilot unit type", "Unit Type Selection",
				JOptionPane.INFORMATION_MESSAGE, null, pUnitType,
				pUnitType[0]);
		
		if (pUnitTypeString == null || pUnitTypeString.length() == 0)
			return;
		
		if (pUnitTypeString.equals("Mek"))
			unitType = Unit.MEK;
		else
			unitType = Unit.PROTOMEK;
		
		//determine the weight class to use
		String pWeightClassString = (String) JOptionPane.showInputDialog(mwclient.getMainFrame(),
				"Select a pilot unit size", "Weight Class Selection",
				JOptionPane.INFORMATION_MESSAGE, null, pWeightClass,
				pWeightClass[0]);
		
		if (pWeightClassString == null || pWeightClassString.length() == 0)
			return;
		
		weightClass = Unit.getWeightIDForName(pWeightClassString);
		
		if ( mwclient.getPlayer().getPersonalPilotQueue().getPilotQueue(unitType, weightClass).size() < 1){
			JOptionPane.showMessageDialog(null,"You do not have any pilots for "
					+ StringUtils.aOrAn(pWeightClassString, true) + " " + pUnitTypeString,"No Pilots!",JOptionPane.CLOSED_OPTION);
			return;
		}
		
		Object[] pilots = mwclient.getPlayer().getPersonalPilotQueue().getPilotQueue(unitType, weightClass).toArray();
		
		JComboBox combo = new JComboBox();
		
		for(int i = 0; i < pilots.length; i++) {
			Pilot mm = (Pilot)pilots[i];
			if (unitType == Unit.MEK )
				combo.addItem(mm.getName() + " (" + mm.getGunnery() + "/" + mm.getPiloting() + ")["+mm.getSkillString(true)+"]");
			else
				combo.addItem(mm.getName() + " (" + mm.getGunnery() + ")["+mm.getSkillString(true)+"]");
		}     
		
		combo.setEditable(false);
		JOptionPane jop = new JOptionPane(combo, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		
		JDialog dlg = jop.createDialog(mwclient.getMainFrame(), "Select a pilot.");
		combo.grabFocus();
		combo.getEditor().selectAll();
		
		dlg.setVisible(true);
		
		int position = combo.getSelectedIndex();
		
		int value = ((Integer) jop.getValue()).intValue();
		
		if (value == JOptionPane.CANCEL_OPTION)
			return;
		
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c transferpilot#" + targetPlayer+"#"+unitType+"#"+weightClass+"#"+position);
	}
	
	public void jMenuCommanderDonatePersonalPilot_actionPerformed() {
		boolean allowProto = Boolean.parseBoolean(mwclient.getserverConfigs("UseProtoMek"));

		Object[] pWeightClass = { "Light", "Medium", "Heavy", "Assault" };
		Object[] pUnitType;
		
		if ( allowProto )
			pUnitType = new Object[]{ "Mek", "Proto" };
		else
			pUnitType = new Object[]{"Mek"};

		int weightClass = 0;
		int unitType = 0;
		
		//determine the unit type to use
		String pUnitTypeString = (String) JOptionPane.showInputDialog(mwclient.getMainFrame(),
				"Select a pilot unit type", "Unit Type Selection",
				JOptionPane.INFORMATION_MESSAGE, null, pUnitType,
				pUnitType[0]);
		
		if (pUnitTypeString == null || pUnitTypeString.length() == 0)
			return;
		
		if (pUnitTypeString.equals("Mek"))
			unitType = Unit.MEK;
		else
			unitType = Unit.PROTOMEK;
		
		//determine the weight class to use
		String pWeightClassString = (String) JOptionPane.showInputDialog(mwclient.getMainFrame(),
				"Select a pilot unit size", "Weight Class Selection",
				JOptionPane.INFORMATION_MESSAGE, null, pWeightClass,
				pWeightClass[0]);
		
		if (pWeightClassString == null || pWeightClassString.length() == 0)
			return;
		
		weightClass = Unit.getWeightIDForName(pWeightClassString);
		
		if ( mwclient.getPlayer().getPersonalPilotQueue().getPilotQueue(unitType,weightClass).size() < 1){
			JOptionPane.showMessageDialog(null,"You do not have any pilots for "+StringUtils.aOrAn(pWeightClassString, true) +
					" " + pUnitTypeString + ".","No Pilots!",JOptionPane.CLOSED_OPTION);
			return;
		}
		
		Object[] pilots = mwclient.getPlayer().getPersonalPilotQueue().getPilotQueue(unitType,weightClass).toArray();
		
		JComboBox combo = new JComboBox();
		
		for(int i = 0; i < pilots.length; i++) {
			Pilot mm = (Pilot)pilots[i];
			if (unitType == Unit.MEK)
				combo.addItem(mm.getName() + " (" + mm.getGunnery() + "/" + mm.getPiloting() + ")[" + mm.getSkillString(true) + "]");
			else
				combo.addItem(mm.getName() + " (" + mm.getGunnery() + ")[" + mm.getSkillString(true) + "]");
		}
		
		
		combo.setEditable(false);
		JOptionPane jop = new JOptionPane(combo, JOptionPane.QUESTION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		
		JDialog dlg = jop.createDialog(mwclient.getMainFrame(), "Select a pilot.");
		combo.grabFocus();
		combo.getEditor().selectAll();
		
		dlg.setVisible(true);
		
		int position = combo.getSelectedIndex();
		
		int value = ((Integer) jop.getValue()).intValue();
		
		if (value == JOptionPane.CANCEL_OPTION)
			return;
		
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c donatepilot#" + unitType+"#"+weightClass+"#"+position);
	}
	
	public void jMenuCommanderDirectSell_actionPerformed(String name, String id) {
		
		//get player
		String buyer;
		String unitID;
		String price;
		
		if (name == null || name.trim().equals("")) {
			PlayerNameDialog pnd = new PlayerNameDialog(mwclient, "Buyer", PlayerNameDialog.ANY_PLAYER);
			pnd.setVisible(true);
			buyer = pnd.getPlayerName();
			pnd.dispose();
		} else
			buyer = name;
		
		if (buyer == null)
			return;
		
		if (id == null || id.trim().equals("")) {
			UnitSelectionDialog usd = new UnitSelectionDialog(mwclient, "Unit","Select a unit to sell");
			usd.setVisible(true);
			unitID= usd.getUnitID();
			usd.dispose();
		}
		else
			unitID = id;
		
		CUnit unit = mwclient.getPlayer().getUnit(Integer.parseInt(unitID));
		
		String serviceFee = "SellDirect"+Unit.getWeightClassDesc(unit.getWeightclass())+Unit.getTypeClassDesc(unit.getType())+"Price";
		price = JOptionPane.showInputDialog(this.getContentPane(),
				"How much do you wish to offer? ("
				+ mwclient.moneyOrFluMessage(true,true,-2) + ")\n\r" +
				"Please note a service charge of " +mwclient.moneyOrFluMessage(true,true,Integer.parseInt(mwclient.getserverConfigs(serviceFee)))+
		" will be added.");
		
		if ( price == null || price.length() < 1 )
			return;
		
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c directsellunit#" + buyer+"#"+mwclient.getPlayer().getName()+"#"+unitID+"#"+price);
	}
	
	public void jMenuMercOfferContract_actionPerformed() {
		String Amount;
		String Duration;
		PlayerNameDialog playerDialog = new PlayerNameDialog(mwclient,"Which Merc do you want to offer a contract?", PlayerNameDialog.MERCS_ONLY);
		playerDialog.setVisible(true);
		String Merc = playerDialog.getPlayerName();
		playerDialog.dispose();
		
		if (Merc == null)
			return;
		
		Amount = JOptionPane.showInputDialog(this.getContentPane(),
				"How much do you wish to offer? ("
				+ mwclient.moneyOrFluMessage(true,true,-2) + ")");
		if ( Amount == null)
			return;
		
		
		Vector<String> techTypes = new Vector<String>(5,1);
		techTypes.add("Exp");
		techTypes.add("Land");
		techTypes.add("Units");
		techTypes.add("Components");
		techTypes.add("Delay");
		JComboBox combo = new JComboBox(techTypes);
		combo.setEditable(false);
		JOptionPane jop = new JOptionPane(combo, JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
		
		JDialog dlg = jop.createDialog(this, "Select contract type.");
		combo.grabFocus();
		combo.getEditor().selectAll();
		
		dlg.setVisible(true);
		
		String Type  = (String)combo.getSelectedItem();
		
		int value = ((Integer) jop.getValue()).intValue();
		
		if (value == JOptionPane.CANCEL_OPTION)
			return;
		
		Duration = JOptionPane.showInputDialog(this.getContentPane(),
		"Duration of the contract?");
		if ( Duration == null)
			return;
		
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c offercontract#" + Merc
				+ "#" + Amount + "#" + Duration + "#" + Type);
	}
	
	public void jMenuCommanderDefect_actionPerformed() {
		//String Confirmation;
		String House = "";
		
		HouseNameDialog factionDialog = new HouseNameDialog(mwclient, "Defect to faction:", false,true);
		factionDialog.setVisible(true);
		House = factionDialog.getHouseName();
		factionDialog.dispose();
		
		if (House == null)
			return;
		
		//send unconfirmed defection command
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c defect#" + House);
	}
	
	public void jMenuCommanderFireTechs_actionPerformed() {
		
		String techsToFire = JOptionPane.showInputDialog(this.getContentPane(),"How many techs do you want to fire?");
		if (techsToFire == null || techsToFire.trim().length() == 0)
			return;
		
		int techs = Integer.parseInt(techsToFire);
		if (!useAdvanceRepairs && thePlayer.getTechs() <= 0) {
			mwclient.addToChat("<b>You have no hired techs to fire.<b>");
			return;
		}
		
		
		if (!useAdvanceRepairs && (techs < 1 || techs > thePlayer.getTechs()) ) {
			mwclient.addToChat("<b>Try picking a number between 1 and " + thePlayer.getTechs() + "<b>");
			return;
		}
		
		if ( useAdvanceRepairs ){
			
			Vector<String> techTypes = new Vector<String>(4,1);
			techTypes.add("Green");
			techTypes.add("Regular");
			techTypes.add("Vet");
			techTypes.add("Elite");
			JComboBox combo = new JComboBox(techTypes);
			combo.setEditable(true);
			JOptionPane jop = new JOptionPane(combo, JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
			
			JDialog dlg = jop.createDialog(mwclient.getMainFrame(), "Select tech to fire.");
			combo.grabFocus();
			combo.getEditor().selectAll();
			
			dlg.setVisible(true);
			
			int techType = combo.getSelectedIndex();
			
			if ( techType < 0 )
				return;

            int value = ((Integer) jop.getValue()).intValue();
            
            if (value == JOptionPane.CANCEL_OPTION)
                return;

			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c firetechs#" + techs+"#"+techType);
		}
		else
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c firetechs#" + techs);
	}
	
	public void jMenuCommanderHireTechs_actionPerformed() {
		boolean allowRegTechs = Boolean.parseBoolean(mwclient.getserverConfigs("AllowRegTechsToBeHired"));
		
		String techsToHire ="";
		
		if ( useAdvanceRepairs && !allowRegTechs )
			techsToHire = JOptionPane.showInputDialog(this.getContentPane(),
			"How many green techs do you want to hire?("+Integer.parseInt(mwclient.getserverConfigs("GreenTechHireCost"))+mwclient.moneyOrFluMessage(true,true,-2)+")");
		else
			techsToHire = JOptionPane.showInputDialog(this.getContentPane(),
			"How many techs do you want to hire?");
		
		if (techsToHire == null || techsToHire.length() == 0)
			return;
		
		int techs = Integer.parseInt(techsToHire);
		if (techs < 1) {
			mwclient.addToChat("Try picking a number greater then 0");
			return;
		}
		
		if ( useAdvanceRepairs && allowRegTechs){
			
			Vector<String> techTypes = new Vector<String>(2,1);
			techTypes.add("Green "+Integer.parseInt(mwclient.getserverConfigs("GreenTechHireCost"))+mwclient.moneyOrFluMessage(true,true,-2));
            techTypes.add("Regular "+Integer.parseInt(mwclient.getserverConfigs("RegTechHireCost"))+mwclient.moneyOrFluMessage(true,true,-2));
			JComboBox combo = new JComboBox(techTypes);
			combo.setEditable(false);
			JOptionPane jop = new JOptionPane(combo, JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
			
			JDialog dlg = jop.createDialog(mwclient.getMainFrame(), "Select tech to hire.");
			combo.grabFocus();
			combo.getEditor().selectAll();
			
			dlg.setVisible(true);
			
			int techType = combo.getSelectedIndex();
			
            
			if ( techType < 0 )
				return;
			
            int value = ((Integer) jop.getValue()).intValue();
            
            if (value == JOptionPane.CANCEL_OPTION)
                return;

            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c hiretechs#" + techs+"#"+techType);
		}
		else
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c hiretechs#" + techs);
	}
	
	public void jMenuCampaignSubOtherBuyPilots_actionPerformed(){
		boolean allowProto = Boolean.parseBoolean(mwclient.getserverConfigs("UseProtoMek"));
		int unitType = Unit.MEK;
		int unitClass = Unit.LIGHT;
		
		Object[] pWeightClass = { "Light", "Medium", "Heavy", "Assault" };
		Object[] pUnitType;
		
		if ( allowProto )
			pUnitType = new Object[]{ "Mek", "Proto" };
		else
			pUnitType = new Object[]{"Mek"};

		
		//determine the unit type to use
		String pUnitTypeString = (String) JOptionPane.showInputDialog(mwclient.getMainFrame(),
				"Select unit type", "Unit Type Selection",
				JOptionPane.INFORMATION_MESSAGE, null, pUnitType,
				pUnitType[0]);
		
		if (pUnitTypeString == null || pUnitTypeString.length() == 0)
			return;
		
		if (pUnitTypeString.equals("Mek"))
			unitType = Unit.MEK;
		else
			unitType = Unit.PROTOMEK;
		
		//determine the weight class to use
		String pWeightClassString = (String) JOptionPane.showInputDialog(mwclient.getMainFrame(),
				"Select unit size", "Weight Class Selection",
				JOptionPane.INFORMATION_MESSAGE, null, pWeightClass,
				pWeightClass[0]);
		
		if (pWeightClassString == null || pWeightClassString.length() == 0)
			return;
		
		unitClass = Unit.getWeightIDForName(pWeightClassString);
		
		String numberOfPilots= JOptionPane.showInputDialog(this.getContentPane(),
		"How many pilots do you want to hire?",1);
	
	if (numberOfPilots == null || numberOfPilots.length() == 0)
		return;
		
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c buypilotsfromhouse#"+unitType+"#"+unitClass+"#"+numberOfPilots);
	}
	
	public void jMenuCommanderSellBays_actionPerformed() {
		String baysToFire = JOptionPane.showInputDialog(this.getContentPane(),
		"How many bays do you want to return??");
		
		if (baysToFire == null || baysToFire.length() == 0)
			return;
		
		int bays = Integer.parseInt(baysToFire);
		if (thePlayer.getFreeBays() <= 0) {
			mwclient.addToChat("<b>You have no free bays to return.<b>");
			return;
		}
		if (bays < 1 || bays > thePlayer.getFreeBays()) {
			mwclient.addToChat("<b>Try picking a number between 1 and " + thePlayer.getFreeBays() + "<b>");
			return;
		}
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c sellbays#" + bays);
	}
	
	public void jMenuCampaignPartsCache_actionPerformed() {
		CPlayer p = mwclient.getPlayer();
		StringBuilder result = new StringBuilder();

		result.append(p.getPartsCache().tableizeComponents());
		mwclient.doParseDataInput("SM|" + result.toString());
	}

	public void jMenuCommanderBuyBays_actionPerformed() {
		String baysToHire = JOptionPane.showInputDialog(this.getContentPane(),
		"How many bays do you want to lease?("+Integer.parseInt(mwclient.getserverConfigs("CostToBuyNewBay"))+mwclient.moneyOrFluMessage(true,true,-2)+")");
		
		if (baysToHire == null || baysToHire.length() == 0)
			return;
		
		int bays = Integer.parseInt(baysToHire);
		if (bays < 1) {
			mwclient.addToChat("Try picking a number greater then 0");
			return;
		}
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c buybays#" + bays);
	}
	
	public void jMenuHelpViewUnit_actionPerformed() {
		
		UnitLoadingDialog unitLoadingDialog = new UnitLoadingDialog(mwclient.getMainFrame());
		UnitViewerDialog unitSelector = new UnitViewerDialog(mwclient.getMainFrame(), unitLoadingDialog, mwclient,UnitViewerDialog.UNIT_VIEWER);
		new Thread(unitSelector).start();
		//unitSelector.setVisible(true);
	}
	
	public void jMenuLeaderPromote_actionPerformed(){
		String targetPlayer;
	
		int menuType = mwclient.isMod() ? PlayerNameDialog.ANY_PLAYER : PlayerNameDialog.FACTION_ONLY;
		
		PlayerNameDialog pnd = new PlayerNameDialog(mwclient, "Promote", menuType);
		pnd.setVisible(true);
		targetPlayer = pnd.getPlayerName();
		pnd.dispose();
		
		if (targetPlayer == null)
			return;
		
		
        SubFactionNameDialog subFactionDialog = new SubFactionNameDialog(mwclient, "SubFaction",mwclient.getUser(targetPlayer).getHouse());
        subFactionDialog.setVisible(true);
        String subFactionName = subFactionDialog.getSubFactionName();
        subFactionDialog.dispose();

        if (subFactionName == null || subFactionName.length() == 0)
            return;
		
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c promoteplayer#" + targetPlayer + "#" + subFactionName);
	
	}
	
	public void jMenuLeaderDemote_actionPerformed(){
		String targetPlayer;
	
		int menuType = mwclient.isMod() ? PlayerNameDialog.ANY_PLAYER : PlayerNameDialog.FACTION_ONLY;
		
		PlayerNameDialog pnd = new PlayerNameDialog(mwclient, "Demote Player", menuType);
		pnd.setVisible(true);
		targetPlayer = pnd.getPlayerName();
		pnd.dispose();
		
		if (targetPlayer == null)
			return;
		
		
        SubFactionNameDialog subFactionDialog = new SubFactionNameDialog(mwclient, "Use None to remove completely",mwclient.getUser(targetPlayer).getHouse());
        subFactionDialog.setVisible(true);
        String subFactionName = subFactionDialog.getSubFactionName();
        subFactionDialog.dispose();

        if (subFactionName == null || subFactionName.length() == 0)
            return;
		
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c demoteplayer#" + targetPlayer + "#" + subFactionName);
	
	}
	
	public void jMenuLeaderFluff_actionPerformed(){
		String targetPlayer;
	
		int menuType = PlayerNameDialog.FACTION_ONLY;
		
		PlayerNameDialog pnd = new PlayerNameDialog(mwclient, "Fluff Player", menuType);
		pnd.setVisible(true);
		targetPlayer = pnd.getPlayerName();
		pnd.dispose();
		
		if (targetPlayer == null)
			return;
		
		CUser user = mwclient.getUser(targetPlayer);
		
		String newfluff = JOptionPane.showInputDialog(this, "Fluff? (Leave blank to remove)",user.getFluff());
		
		if (newfluff != null)
			mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c FactionLeaderFluff#" + targetPlayer + "#" + newfluff);
	}
	
	public void jMenuLeaderMute_actionPerformed(){
		String targetPlayer;
	
		int menuType = PlayerNameDialog.FACTION_ONLY;
		
		PlayerNameDialog pnd = new PlayerNameDialog(mwclient, "Mute Player", menuType);
		pnd.setVisible(true);
		targetPlayer = pnd.getPlayerName();
		pnd.dispose();
		
		if (targetPlayer == null)
			return;
		
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c FactionLeaderMute#" + targetPlayer);
	}
	
	//Show data about the mek wars client and server
	public void jMenuHelpAbout_actionPerformed() {
		
		//make the dialog
		JDialog dlg = new JDialog(this, "MekWars Client Info");
		
		//set up the contents
		JPanel child = new JPanel();
		child.setLayout(new BoxLayout(child, BoxLayout.Y_AXIS));
		
		//set the text up.
		JLabel mekwars = new JLabel("MekWars Client Version: " + MWClient.CLIENT_VERSION);
		JLabel version = new JLabel("MegaMek Version: " + MegaMek.VERSION);
		JLabel license1 = new JLabel(
		"MekWars Client software is under GPL. See");
		JLabel license2 = new JLabel(
		"license.txt in ./MekWars Docs/ for details.");
		JLabel license3 = new JLabel("Project Info and Server Packages:");
		JLabel license4 = new JLabel(
		"       http://www.sourceforge.net/projects/mekwars       ");
		JLabel data1 = new JLabel(
		"       Datasets are prepared by server operators.       ");
		JLabel data2 = new JLabel(
		"       Contact a server administrator for information       ");
		JLabel data3 = new JLabel(
		"       regarding data use and redistribution.       ");
		
		//center everything
		mekwars.setAlignmentX(Component.CENTER_ALIGNMENT);
		version.setAlignmentX(Component.CENTER_ALIGNMENT);
		license1.setAlignmentX(Component.CENTER_ALIGNMENT);
		license2.setAlignmentX(Component.CENTER_ALIGNMENT);
		license3.setAlignmentX(Component.CENTER_ALIGNMENT);
		license4.setAlignmentX(Component.CENTER_ALIGNMENT);
		data1.setAlignmentX(Component.CENTER_ALIGNMENT);
		data2.setAlignmentX(Component.CENTER_ALIGNMENT);
		data3.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//add to child panel
		child.add(new JLabel("\n"));
		child.add(mekwars);
		child.add(version);
		child.add(new JLabel("\n"));
		child.add(license1);
		child.add(license2);
		child.add(new JLabel("\n"));
		child.add(license3);
		child.add(license4);
		child.add(new JLabel("\n"));
		child.add(data1);
		child.add(data2);
		child.add(data3);
		child.add(new JLabel("\n"));
		
		//then add child panel to the content pane.
		dlg.getContentPane().add(child);
		
		//set the location of the dialog
		Dimension dlgSize = dlg.getPreferredSize();
		Dimension frmSize = getSize();
		Point loc = getLocation();
		dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x,
				(frmSize.height - dlgSize.height) / 2 + loc.y);
		dlg.setModal(true);
		dlg.setResizable(false);
		dlg.pack();
		dlg.setVisible(true);
	}
	
	public void jMenuHelpHelp_actionPerformed() {
		CPlayer p = mwclient.getPlayer();
		boolean trueCost = Boolean.parseBoolean(mwclient.getserverConfigs("UseCalculatedCosts")); 
		StringBuilder result = new StringBuilder();
		result.append("<font color=\"black\">");
		result.append("MEKWARS ONLINE HELP<br>");
		
		result.append("<table><tr><th>Name</th><th>"
			+ mwclient.moneyOrFluMessage(true,false,-2) + "</th><th>"
			+ mwclient.moneyOrFluMessage(false,false,-2)
			+ "</th><th>Components</th>");
		
		if ( useAdvanceRepairs )
			result.append("<th>Bays</th></tr>");
		else
			result.append("<th>Techs</th></tr>");
		
		int typeamount = Unit.MAXBUILD;
		for (int type = 0; type < typeamount; type++) {
			String useIt = "Use" + Unit.getDescriptionForID(type);
			
			if (!Boolean.parseBoolean(mwclient.getserverConfigs(useIt)))
				continue;
			
			for (int weight = 0; weight < 4; weight++) {

                //No reason to cycle through med-assault infantry if you are only using light
                if (Boolean.parseBoolean(mwclient.getserverConfigs("UseOnlyLightInfantry")) && type == Unit.INFANTRY && weight != Unit.LIGHT)
                    break;
                //only using one vee size means only lights are used. so no reason to keep cycling if we are past the lights.
                if (Boolean.parseBoolean(mwclient.getserverConfigs("UseOnlyOneVehicleSize")) && type == Unit.VEHICLE && weight != Unit.LIGHT)
                    break;
                
				result.append("<tr><td>" + Unit.getWeightClassDesc(weight) + " " + Unit.getTypeClassDesc(type) + "</td><td>");
				if (trueCost)
					result.append("See Unit Viwer");
				else
					result.append(CUnit.getPriceForUnit(mwclient, weight, type, p.getMyHouse())); 
				result.append("</td><td>"+CUnit.getInfluenceForUnit(mwclient, weight, type, p.getMyHouse()) + "</td>");
				result.append("<td>" + CUnit.getPPForUnit(mwclient, weight, type, p.getMyHouse()) + "</td><td>");
				if (type == Unit.PROTOMEK)
					result.append(mwclient.getserverConfigs("TechsToProtoPointRatio") + " per 5");
				else
					result.append(p.getHangarSpaceRequired(type, weight, 0, ""));
				result.append("</td></tr>");
				
			}
		}
		result.append("</table>");
		
		result.append("<br><b><i>Repoding facts:</b></i>");
		result.append("<table><tr><th>Class</th><th>"
			+ mwclient.moneyOrFluMessage(true,false,-2) + "</th><th>"
			+ mwclient.moneyOrFluMessage(false,false,-2)
			+ "</th><th>Components</th></tr>");
		typeamount = 1;
		for (int type = 0; type < typeamount; type++) {
			for (int weight = 0; weight < 4; weight++) {
				String repodFlu = "RepodFlu" + Unit.getWeightClassDesc(weight);
				String repodCost = "RepodCost" + Unit.getWeightClassDesc(weight);
				String repodComponents = "RepodComp" + Unit.getWeightClassDesc(weight);
				
				int repodCostInt = Integer.parseInt(mwclient.getserverConfigs(repodCost));
				int repodComponentsInt = Integer.parseInt(mwclient.getserverConfigs(repodComponents));
				
				if (!Boolean.parseBoolean(mwclient.getserverConfigs("DoesRepodCost")))
					repodCostInt = 0;
				if (!Boolean.parseBoolean(mwclient.getserverConfigs("RepodUsesComp")))
					repodComponentsInt = 0;
				
				result.append("<tr><td>" + Unit.getWeightClassDesc(weight)
				+ "</td><td>" + repodCostInt + "</td><td>"
				+ mwclient.getserverConfigs(repodFlu) + "</td><td>"
				+ repodComponentsInt + "</td></tr>");
			}
		}
		
		result.append("</table><br>");
        if ( useAdvanceRepairs ){
            result.append("<br><b><i>Tech/BayCosts:</b></i>");
            result.append("<table><tr><th>Type</th><th>" + mwclient.moneyOrFluMessage(true,false,-2) + "</th>");
            result.append("<tr><td>Bay Cost</td><td>"+Integer.parseInt(mwclient.getserverConfigs("CostToBuyNewBay"))+"</td></tr>");
            result.append("<tr><td>Bay Sale</td><td>"+Integer.parseInt(mwclient.getserverConfigs("BaySellBackPrice"))+"</td></tr>");
            result.append("<tr><td>Green Tech</td><td>"+Integer.parseInt(mwclient.getserverConfigs("GreenTechHireCost"))+"</td></tr>");

            if ( Boolean.parseBoolean(mwclient.getserverConfigs("AllowRegTechsToBeHired")) )
                result.append("<tr><td>Reg Tech</td><td>"+Integer.parseInt(mwclient.getserverConfigs("RegTechHireCost"))+"</td></tr>");
            result.append("</table>");
        }
		result.append("<table><tr><th>Size of Unit</th><th>EXP needed</th></tr>");
		result.append("<tr><td>Medium</td><td>" + mwclient.getserverConfigs("MinEXPforMedium") + "</td></tr>");
		result.append("<tr><td>Heavy</td><td>" + mwclient.getserverConfigs("MinEXPforHeavy") + "</td></tr>");
		result.append("<tr><td>Assault</td><td>" + mwclient.getserverConfigs("MinEXPforAssault") + "</td></tr>");
		result.append("</table>");
		result.append("EXP needed to Buy/Sell on the Black Market: "
			+ mwclient.getserverConfigs("MinEXPforBMBuying") + "/"
			+ mwclient.getserverConfigs("MinEXPforBMSelling") + "<br>");
		result.append("EXP needed to defect from "+mwclient.getserverConfigs("NewbieHouseName")+" to a Faction: " 
			+ mwclient.getserverConfigs("MinEXPforDefecting") + "<br>");
		
		
		result.append("</table><br>");

        result.append("<b><i>Unit Status Icons</B></i><br>");
        result.append("<table><tr><th>Icon</th><th>Description</th></tr>");
        result.append("<tr><td><img src=\"data/images/status/ammo.gif\"></td><td>Unit has all ammo bins loaded</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/low.gif\"></td><td>Unit has 1 or more ammo bins that are low on ammo</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/empty.gif\"></td><td>Unit has 1 or more ammo bins that are empty</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/armor.gif\"></td><td>Unit has armor damage</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/structure.gif\"></td><td>Unit has IS damage</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/critical.gif\"></td><td>Unit has criticals damaged</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/engine.gif\"></td><td>Unit is engined</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/nopilot.gif\"></td><td>Unit has no pilot</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/pilot.gif\"></td><td>Unit has a pilot</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/wound.gif\"></td><td>Unit has a wounded pilot</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/eject.gif\"></td><td>Unit has autoejection enabled</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/noeject.gif\"></td><td>Unit has autoejection disabled</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/pending.gif\"></td><td>Unit has pending repairs</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/repairing.gif\"></td><td>Unit is currently under repair</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/unmaint.gif\"></td><td>Unit is unmaintained/damaged</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/maint.gif\"></td><td>Unit is fully maintained</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/search.gif\"></td><td>Unit is equiped with a slite</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/searchon.gif\"></td><td>Unit is equiped with a slite and defaults to on</td></tr>");
        result.append("<tr><td><img src=\"data/images/status/nosearch.gif\"></td><td>Unit is not equiped with a slite</td></tr>");
        result.append("</table><br>");

        result.append("</table><br>");

        if ( mwclient.getData().getServerBannedAmmo().size() > 0 ){
            result.append("<b><i>Server Banned ammo</b></i><br>");
            for ( String key : mwclient.getData().getServerBannedAmmo().keySet() )
                result.append(mwclient.getData().getMunitionsByNumber().get(Long.parseLong(key))+"<br>");
        }
        
        House faction = mwclient.getData().getHouseByName(mwclient.getPlayer().getHouse());
        if ( faction.getBannedAmmo().size() > 0 ){
            result.append("<b><i>House Banned Ammo</b></i><br>");
            for ( String key : faction.getBannedAmmo().keySet() ){
                result.append(mwclient.getData().getMunitionsByNumber().get(Long.parseLong(key))+"<br>");
            }
        }
        
     /*   
        for ( Object prop : System.getProperties().keySet()){
        	
        	result.append(prop.toString());
        	result.append(" = ");
        	result.append(System.getProperty(prop.toString()));
        	result.append("<br>");
        }*/
        /*
         * use process incoming, instead of adding directly to misc, so that
         * output is directly to main if misc in main is enabled or players has
         * the misc. tab off.
         */
		mwclient.doParseDataInput("SM|" + result.toString());
	}
	
	public void jMenuHelpPilotSkills_actionPerformed() {
		String result = "";
		result += "<font color=\"black\">";
		result += "<b><i>MekWars/MegaMek Pilot Skills</b></i><br>";
		result += "<table><tr><th>Name</th>"
			+ "<th>Abbrivation</th>"
			+ "<th>Description</th></tr>";
		
		//to make the code a bit more stream line going to do everything based on meks. if its off for a mek but on for something else oh well.
		
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforATforMek")) > 0){
			result += "<tr>"
				+ "<td>Astech</td>"
				+ "<td>AT</td>";
                if ( useAdvanceRepairs )
                    result += "<td>Pilot acts as a tech with repairs only costing parts</td>";
                else
                    result += "<td>Reduces the number of techs needed to repair a unit by 1</td>";
				result += "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforDMforMek")) > 0){
			result += "<tr>"
				+ "<td>Dodge Maneuver</td>"
				+ "<td>DM</td>"
				+ "<td>Enables the unit to make a dodge maneuver instead of a physical attack.<br>This maneuver adds +2 to the BTH to physical attacks against the unit.</td>"
				+ "</tr>";
		}
        if ( Integer.parseInt(mwclient.getserverConfigs("chanceforEDforMek")) > 0){
            result += "<tr>"
                + "<td>Edge</td>"
                + "<td>ED</td>"
                + "<td>Allows Pilot to reroll 1 roll(per level) per game.</td>"
                + "</tr>";
        }
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforEIforMek")) > 0){
			result += "<tr>"
				+ "<td>Enhanced Interface</td>"
				+ "<td>EI</td>"
				+ "<td>Neural interface to the clan enhanced imaging system<br>-1 To PSR<br>+2 when targeting with TC instead of +3<br>Can Target without TC at +6<br>Reduces all forest and Smoke mods to 1<br>Pilot receives 1 point of damage every time Units IS is hit,<br>If you fail a roll of 7+<br>BA's recieve 1 extra point of damage every time they are hit</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforGTforMek")) > 0){
			result += "<tr>"
				+ "<td>Gifted</td>"
				+ "<td>GT</td>"
				+ "<td>Pilots receive an extra 5% chance to gain a skill when they fail<br>to level Piloting or Gunnery after a win.</td>"
				+ "</tr>";
		}        
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforGBforMek")) > 0){
			result += "<tr>"
				+ "<td>Gunnery Ballistic</td>"
				+ "<td>GB</td>"
				+ "<td>NOTE: This is an unofficial rule. Pilot gets a -1 to-hit bonus on all<br>ballistic weapons (MGs, all ACs, Gaussrifles).</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforGLforMek")) > 0){
			result += "<tr>"
				+ "<td>Gunnery Laser</td>"
				+ "<td>GL</td>"
				+ "<td>NOTE: This is an unofficial rule. Pilot gets a -1 to-hit bonus on all<br>energy-based weapons (Laser, PPC, and Flamer).</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforGMforMek")) > 0){
			result += "<tr>"
				+ "<td>Gunnery Missile</td>"
				+ "<td>GM</td>"
				+ "<td>NOTE: This is an unofficial rule. Pilot gets a -1 to-hit bonus on all<br>missile weapons (LRM, MRM, SRM).</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforIMforMek")) > 0){
			result += "<tr>"
				+ "<td>Iron Man</td>"
				+ "<td>IM</td>"
				+ "<td>NOTE: This is an unofficial rule. A pilot with this skill receives only<br>1 pilot hit from ammunition explosions.</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforMAforMek")) > 0){
			result += "<tr>"
				+ "<td>Maneuvering Ace</td>"
				+ "<td>MA</td>"
				+ "<td>Enables the unit to move laterally like a Quad. Units also receive a -1<br>BTH to rolls against skidding.</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforMSforMek")) > 0){
			result += "<tr>"
				+ "<td>Melee Specialist</td>"
				+ "<td>MS</td>"
				+ "<td>Enables the unit to do 1 additional point of damage with physical attacks<br>and subtracts one from the attacker movement modifier (to a minimum of zero).<br>Note: This ability is only used for BattleMechs.</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforMTforMek")) > 0){
			result += "<tr>"
				+ "<td>MedTech</td>"
				+ "<td>MT</td>"
				+ "<td>A pilot with the MedTech skill will heal 1 extra point per tick.</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforNAGforMek")) > 0){
			result += "<tr>"
				+ "<td>Natural Aptitude: Gunnery</td>"
				+ "<td>NAG</td>"
				+ "<td>The pilot checks leveling for gunnery at one level higher then current i.e.<br>5 instead of 4 for a 4/5 pilot</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforNAPforMek")) > 0){
			result += "<tr>"
				+ "<td>Natural Aptitude: Piloting</td>"
				+ "<td>NAP</td>"
				+ "<td>The pilot checks leveling for piloting at one level higher then current i.e.<br>5 instead of 4 for a 4/5 pilot</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforPRforMek")) > 0){
			result += "<tr>"
				+ "<td>Pain Resistance</td>"
				+ "<td>PR</td>"
				+ "<td>When making consciousness rolls, 1 is added to all rolls. Also, damage received<BR>from ammo explosions is reduced to 1.<BR>Note: This ability is only used for BattleMechs.</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforQSforMek")) > 0){
			result += "<tr>"
				+ "<td>Quick Study</td>"
				+ "<td>QS</td>"
				+ "<td>Pilots with the Quick Study skill gain a 5% bonus to all XP earned.</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforSVforMek")) > 0){
			result += "<tr>"
				+ "<td>Survivalist</td>"
				+ "<td>SV</td>"
				+ "<td>If a pilot has this skill they will have a +20% of returning home if ejected and<br>left on the field.</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforTGforMek")) > 0){
			result += "<tr>"
				+ "<td>Tactical Genius</td>"
				+ "<td>TG</td>"
				+ "<td>A pilot who has a Tactical Genius may reroll their initiative once per turn.<br>The second roll must be accepted.</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforTNforMek")) > 0){
			result += "<tr>"
				+ "<td>Trait</td>"
				+ "<td>TN</td>"
				+ "<td>Pilot traits for use with moding the gaining of other skills</td>"
				+ "</tr>";
		}
		if ( Integer.parseInt(mwclient.getserverConfigs("chanceforWSforMek")) > 0){
			result += "<tr>"
				+ "<td>Weapon Specialist</td>"
				+ "<td>WS</td>"
				+ "<td>A pilot who specializes in a particular weapon receives a -2 to hit modifier<br>on all attacks with that weapon.</td>"
				+ "</tr>";
		}
		result += "<tr>"
			+ "<td>Clan Pilot Training</td>"
			+ "<td>CPT</td>"
			+ "<td>Pilot has a +1 penalty for physical attacks,<br>because clans do not train for dishonourable combat.</td>"
			+ "</tr>";
		result += "</table>";
		
		/*
         * use process incoming, instead of adding directly to misc, so that
         * output is directly to main if misc in main is enabled or player's
         * misc. tab is disabled.
         */
		mwclient.doParseDataInput("SM|" + result);
		
	}
	
	public void showMulFileList(String data) {
		
		StringTokenizer mulList = new StringTokenizer(data,"#");
		
		Vector<String> list = new Vector<String>(1,1);
		
		while ( mulList.hasMoreElements() )
			list.add(mulList.nextToken());
		
		JComboBox combo = new JComboBox(list);
		combo.setEditable(false);
		JOptionPane jop = new JOptionPane(combo, JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
		
		JDialog dlg = jop.createDialog(mwclient.getMainFrame(), "Select mul file.");
		combo.grabFocus();
		combo.getEditor().selectAll();
		
		dlg.setVisible(true);
		
		if ( combo.getSelectedIndex() < 0 )
			return;

		
        int value = ((Integer) jop.getValue()).intValue();
        
        if (value == JOptionPane.CANCEL_OPTION)
            return;
		String selectedMul = list.elementAt(combo.getSelectedIndex());

		
		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c retrievemul#" + selectedMul);
	}

	/*
	 * Admin methods used to be here. These have been
	 * extracted into a seperate .jar file. @urgru
	 */
	
	/*
	 * Moderator-specific methods used to be here. These
	 * have been extracted into a seperate .jar file. @urgru
	 */
	
	public void refreshBattleTable() {
		MainPanel.refreshBattleTable();
	}
	
	void this_componentResized(ComponentEvent e) {
	}
	
	public void setSoundMuted(boolean b) {
		jMenuOptionsMute.setState(b);
	}
	
	public void updateAttackMenu() {
		
		//login call of UOE occures before the menu is
		//created. return to stop NPE's.
		if (jMenuAttackMenu == null){
			MWClient.mwClientLog.clientErrLog("Attack Menu is Null!");
			return;
		}
		
		jMenuAttackMenu.updateMenuItems(true);
	}
	
	public void startHost() {
		jMenuHost.setForeground(Color.red);
		jMenuCSHostAndJoin.setEnabled(false);
		jMenuCSHostDedicated.setEnabled(false);
		jMenuCSHostStop.setEnabled(true);
		jMenuCSHostAndJoin.setVisible(false);
		jMenuCSHostDedicated.setVisible(false);
		jMenuCSHostLoad.setVisible(false);
		jMenuCSHostLoadAndJoin.setVisible(false);
		jMenuCSHostStop.setVisible(true);
	}
	
	public void stopHost() {
		jMenuHost.setForeground(Color.black);
		jMenuCSHostAndJoin.setEnabled(true);
		jMenuCSHostDedicated.setEnabled(true);
		jMenuCSHostStop.setEnabled(false);
		jMenuCSHostAndJoin.setVisible(true);
		jMenuCSHostDedicated.setVisible(true);
		jMenuCSHostLoad.setVisible(true);
		jMenuCSHostLoadAndJoin.setVisible(true);
		jMenuCSHostStop.setVisible(false);
	}
	
	public void changeStatus(int status, int laststatus) {
		
		if (mwclient.getConfig().isParam("STATUSINTRAYICON")) {
			if (status == MWClient.STATUS_RESERVE) {
				try {
					this.setIconImage(mwclient.getConfig().getImage("RESERVE").getImage());
				} catch (Exception ex) {
					MWClient.mwClientLog.clientErrLog(ex);
				}
			} else if (status == MWClient.STATUS_ACTIVE) {
				try {
					this.setIconImage(mwclient.getConfig().getImage("ACTIVE").getImage());
				} catch (Exception ex) {
					MWClient.mwClientLog.clientErrLog(ex);
				}
			} else if (status == MWClient.STATUS_FIGHTING) {
				try {
					this.setIconImage(mwclient.getConfig().getImage("FIGHT").getImage());
				} catch (Exception ex) {
					MWClient.mwClientLog.clientErrLog(ex);
				}
			} else if (status == MWClient.STATUS_LOGGEDOUT || status == MWClient.STATUS_DISCONNECTED) {
				try {
					this.setIconImage(mwclient.getConfig().getImage("LOGOUT").getImage());
				} catch (Exception ex) {
					MWClient.mwClientLog.clientErrLog(ex);
				}
			}
		}
		
		//if not showing status, show the operator's custom icon
		else {
			try {
				this.setIconImage(mwclient.getConfig().getImage("TRAY").getImage());
			} catch (Exception ex) {
				MWClient.mwClientLog.clientErrLog(ex);
			}
		}
		
		MainPanel.changeStatus(status, laststatus);
		enableMenu();
		repaint();
	}
	
	public void createArmyFromMul(String data) {
		PlayerNameDialog playerDialog = new PlayerNameDialog(mwclient,"Choose a Player.", PlayerNameDialog.ANY_PLAYER);
		playerDialog.setVisible(true);
		String player = playerDialog.getPlayerName();
		playerDialog.dispose();
		
		if (player == null)
			player = "";
		
		System.err.println("String Tokenizer called");
		StringTokenizer mulList = new StringTokenizer(data,"#");
		
		Vector<String> list = new Vector<String>(1,1);
		
		//System.err.println("adding mul's to vector");
		while ( mulList.hasMoreElements() )
			list.add(mulList.nextToken());
		
		//System.err.println("creating combo box.");

		JComboBox combo = new JComboBox(list);
		combo.setEditable(false);
		JOptionPane jop = new JOptionPane(combo, JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
		
		JDialog dlg = jop.createDialog(mwclient.getMainFrame(), "Select mul file.");
		combo.grabFocus();
		combo.getEditor().selectAll();
		
		dlg.setVisible(true);
		
		if ( combo.getSelectedIndex() < 0 )
			return;

		
        int value = ((Integer) jop.getValue()).intValue();
        
        if (value == JOptionPane.CANCEL_OPTION)
            return;
		String selectedMul = list.elementAt(combo.getSelectedIndex());

		String fluff = JOptionPane.showInputDialog(this.getContentPane(), "Army Name.");
		if (fluff == null || fluff.length() < 1)
			return;

		mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "createarmyfrommul "+selectedMul+"#"+fluff+"#"+player);
	}

    public void jMenuSendAllOperationFiles_actionPerformed(ActionEvent e) {
        

        int result = JOptionPane.showConfirmDialog(null,"Upload All local OpFiles?","Upload Ops",JOptionPane.YES_NO_OPTION);
        
        if ( result == JOptionPane.NO_OPTION )
            return;

    	File opFiles = new File("./data/operations/short/");

        
        if ( !opFiles.exists() ){
            return;
        }

        StringBuilder opData = new StringBuilder();
        
        for (File opFile : opFiles.listFiles() ){
            try{
            	
                FileInputStream fis = new FileInputStream(opFile);
                BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
                opData.append(opFile.getName().substring(0,opFile.getName().lastIndexOf(".txt"))+"#");
                while (dis.ready()){
                    opData.append(dis.readLine().replaceAll("#","(pound)")+"#");
                }
                dis.close();
                fis.close();
                
            }catch(Exception ex){
                MWClient.mwClientLog.clientErrLog("Unable to read "+opFile);
                return;
            }
            mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setoperation#short#"+opData.toString());
            opData.setLength(0);
            opData.trimToSize();
        }
    }

    public void jMenuSetNewOperationFile_actionPerformed(ActionEvent e) {
        
        
        String opName = JOptionPane.showInputDialog(mwclient.getMainFrame().getContentPane(),"New Op Name?");

        if ( opName == null || opName.trim().length() < 1)
            return;
        
        File opFile = new File("./data/operations/short/"+opName+".txt");

        
        if ( !opFile.exists() ){
            return;
        }

        StringBuilder opData = new StringBuilder();
        
        try{
            FileInputStream fis = new FileInputStream(opFile);
            BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
            opData.append(opName+"#");
            while (dis.ready()){
                opData.append(dis.readLine().replaceAll("#","(pound)")+"#");
            }
            dis.close();
            fis.close();
            
        }catch(Exception ex){
            MWClient.mwClientLog.clientErrLog("Unable to read "+opFile);
            return;
        }

        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setoperation#short#"+opData.toString());
    }

    public void jMenuUpdateOperations_actionPerformed(ActionEvent e) {
    	mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminlockcampaign");
        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c updateoperations");
    	mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c adminunlockcampaign");

    }
    
    public void jMenuRetrieveOperationFile_actionPerformed(ActionEvent e) {
        
        JComboBox opCombo = new JComboBox(mwclient.getAllOps().keySet().toArray());
        opCombo.setEditable(false);

        JOptionPane jop = new JOptionPane(opCombo, JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
        JDialog dlg = jop.createDialog(this, "Select Op.");
        opCombo.grabFocus();
        opCombo.getEditor().selectAll();

        dlg.setVisible(true);

        if ( (Integer)jop.getValue()  == JOptionPane.CANCEL_OPTION )
            return;
        
        String opName = (String)opCombo.getSelectedItem();
        
        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c RETRIEVEOPERATION#short#"+opName);
    }

    public void jMenuSetOperationFile_actionPerformed(ActionEvent e) {
        
        JComboBox opCombo = new JComboBox(mwclient.getAllOps().keySet().toArray());
        opCombo.setEditable(false);

        JOptionPane jop = new JOptionPane(opCombo, JOptionPane.QUESTION_MESSAGE,JOptionPane.OK_CANCEL_OPTION);
        JDialog dlg = jop.createDialog(this, "Select Op.");
        opCombo.grabFocus();
        opCombo.getEditor().selectAll();

        dlg.setVisible(true);

        if ( (Integer)jop.getValue()  == JOptionPane.CANCEL_OPTION )
            return;
        
        String opName = (String)opCombo.getSelectedItem();
        
        File opFile = new File("./data/operations/short/"+opName+".txt");

        
        if ( !opFile.exists() ){
            return;
        }

        StringBuilder opData = new StringBuilder();
        
        try{
            FileInputStream fis = new FileInputStream(opFile);
            BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
            opData.append(opName+"#");
            while (dis.ready()){
                opData.append(dis.readLine().replaceAll("#","(pound)")+"#");
            }
            dis.close();
            fis.close();
            
        }catch(Exception ex){
            MWClient.mwClientLog.clientErrLog("Unable to read "+opFile);
            return;
        }

        mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "c setoperation#short#"+opData.toString());
    }
}
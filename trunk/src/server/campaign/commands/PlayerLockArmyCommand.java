package server.campaign.commands;

import java.util.StringTokenizer;

import server.campaign.CampaignMain;
import server.campaign.SArmy;
import server.campaign.SPlayer;

public class PlayerLockArmyCommand implements Command {
	int accessLevel = 0;
	String syntax = "";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}

	public void process(StringTokenizer command,String Username) {
		
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		SPlayer p = CampaignMain.cm.getPlayer(Username);
		if (p == null) {
			CampaignMain.cm.toUser("Null Player while locking army. Report This!.",Username,true);
			return;
		}
		int aid = -1;
		try {
			aid = Integer.parseInt((String)command.nextElement());
		} catch (Exception e) {
			CampaignMain.cm.toUser("Improper format. Try: /c playerlockarmy#ID",Username,true);
			return;
		}
		SArmy army = p.getArmy(aid);
		if (army == null) {
			CampaignMain.cm.toUser("Could not find an Army #" + aid + ".",Username,true);
			return;
		}
		army.setPlayerLock(aid, true);
		CampaignMain.cm.toUser("Army " + aid + " locked.",Username,true);
	}
}

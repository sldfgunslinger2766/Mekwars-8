package client.protocol.commands;

import java.util.StringTokenizer;

import client.MWClient;

import common.CampaignData;

/**
 * AckSignon command
 */

public class AckSignonPCmd extends CProtCommand {

	public AckSignonPCmd(MWClient mwclient) {
		super(mwclient);
		name = "ack_signon";
	}

	// execute command
	@Override
	public boolean execute(String input) {
		StringTokenizer ST = new StringTokenizer(input, delimiter);
		if (check(ST.nextToken()) && ST.hasMoreTokens()) {
			input = decompose(input);
			ST = new StringTokenizer(input, delimiter);
			mwclient.setUsername(ST.nextToken());
			echo(input);
			if (mwclient.isDedicated()) {
				
				try {Thread.sleep(5000);}
				catch (Exception ex) {CampaignData.mwlog.errLog(ex);}
				
				try {
					mwclient.startHost(true,false,false);
				} catch (Exception ex) {
					CampaignData.mwlog.errLog("AckSignonPCmd: Error attempting to start host on signon.");
					CampaignData.mwlog.errLog(ex);
				}
			}
			
			return true;
		}
		//else
		return false; 
	}

	// echo command in GUI
	@Override
	protected void echo(String input) {
		CampaignData.mwlog.infoLog("Signon acknowledged");
		CampaignData.mwlog.errLog("Signon acknowledged");
	}

}
package client.cmd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.StringTokenizer;

import client.MWClient;
import client.gui.CCommPanel;

/**
 * @author Spork
 * 
 * Handles Build Table up/downloading for admins/mods
 * 
 */
public class BT extends Command {

	/**
	 * @param client
	 */
	public BT(MWClient mwclient) {
		super(mwclient);
	}

	/**
	 * @see client.cmd.Command#execute(java.lang.String)
	 */
	@Override
	public void execute(String input) {
		StringTokenizer st = decode(input);
		
		String cmd = st.nextToken();
		
		if(cmd.equalsIgnoreCase("LS")) {
			StringTokenizer folderT = new StringTokenizer(st.nextToken(), "?");
			while(folderT.hasMoreTokens()) {
				//Token 1 is the folder
				String dName = folderT.nextToken();
				MWClient.mwClientLog.clientOutputLog(dName);
				// Token 2 is the names of the lists
				if(folderT.hasMoreTokens()) {
					StringTokenizer listT = new StringTokenizer(folderT.nextToken(), "*");
					while (listT.hasMoreTokens()) {
						String fileName = listT.nextToken();
						mwclient.sendChat(MWClient.CAMPAIGN_PREFIX + "AdminRequestBuildTable get#" + dName + "#" + fileName);
					}
				}
					
			}
		} else if (cmd.equalsIgnoreCase("BT")) {
			String folder = st.nextToken();
			String table = st.nextToken();
			File file = new File("./data/buildtables");
			if(!file.exists())
				file.mkdir();
			file = new File("./data/buildtables/" + folder);
			if(!file.exists())
				file.mkdir();
			file = new File("./data/buildtables/" + folder + "/" + table);
			try {
				file.createNewFile();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			FileOutputStream out;
			try {
				out = new FileOutputStream(file);
				PrintStream p = new PrintStream(out);
				while(st.hasMoreTokens())
					p.println(st.nextToken());
				mwclient.addToChat("Received build table " + folder + "/" + table,CCommPanel.CHANNEL_MISC);				
				p.close();
				try {
					out.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

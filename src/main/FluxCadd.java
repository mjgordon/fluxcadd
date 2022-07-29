package main;

import backend.*;
import iofile.Plaintext;
import ui.PanelManager;

public class FluxCadd {

	public static Backend backend;
	
	public static PanelManager panelManager;
	
	public static void main(String[] argv) {
		backend = new Backend_LWJGL();
		backend.init();
		
		Config.loadTextFile("config/config.txt");
		
		panelManager = new PanelManager();
		
		//panelManager.initMattersiteWindows();
		//panelManager.initCAMWindows();
		panelManager.initSDFWindows();
		
		backend.loop();
	}
}
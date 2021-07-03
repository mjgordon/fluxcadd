package main;

import java.util.HashMap;

import backend.*;
import iofile.Plaintext;
import ui.PanelManager;

public class FluxCadd {

	public static Backend backend;
	
	public static PanelManager panelManager;
	
	public static HashMap<String,String> config;
	
	public static void main(String[] argv) {
		backend = new Backend_LWJGL();
		backend.init();
		
		config = Plaintext.loadKVSimple("config/config.txt");
		
		panelManager = new PanelManager();
		
		panelManager.initMattersiteWindows();
		
		backend.loop();
	}
}
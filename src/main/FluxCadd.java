package main;

import java.io.FileNotFoundException;

import jscheme.JS;
import backend.*;
import ui.PanelManager;

public class FluxCadd {

	public static Backend backend;
	
	public static PanelManager panelManager;
	
	public static void main(String[] argv) {
		
		backend = new Backend_LWJGL();
		backend.init();
		
		panelManager = new PanelManager();
		
		panelManager.initCADWindows();
		
		backend.loop();
		
		
	}
}
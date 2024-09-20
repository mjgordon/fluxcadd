package main;

import javax.swing.UIManager;

import ui.PanelManager;

public class FluxCadd {

	public static Backend_LWJGL backend;

	public static PanelManager panelManager;


	public static void main(String[] argv) {
		backend = new Backend_LWJGL();
		backend.init();

		try {
			Config.loadTextFile("config/config.txt");	
		}
		catch (java.io.IOException e) {
			try {
				Config.loadTextFile("config/config_default.txt");	
			}
			catch(java.io.IOException e2) {
				System.out.println("Couldn't load 'config/config.txt' or config/config_default.txt'");
				System.exit(1);
			}
		}
		
		
		// Match JavaX Swing ui elements to the native OS styling
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		panelManager = new PanelManager();

		// panelManager.initCAMWindows();
		panelManager.initSDFWindows();

		backend.loop();
	}
}
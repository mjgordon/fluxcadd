package main;

import javax.swing.UIManager;

import ui.PanelManager;

public class FluxCadd {

	public static Backend_LWJGL backend;

	public static PanelManager panelManager;


	public static void main(String[] argv) {
		backend = new Backend_LWJGL();
		backend.init();

		Config.loadTextFile("config/config.txt");
		
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
package main;

import ui.PanelManager;

public class FluxCadd {

	public static Backend_LWJGL backend;

	public static PanelManager panelManager;


	public static void main(String[] argv) {
		backend = new Backend_LWJGL();
		backend.init();

		Config.loadTextFile("config/config.txt");

		panelManager = new PanelManager();

		// panelManager.initCAMWindows();
		panelManager.initSDFWindows();

		backend.loop();
	}
}
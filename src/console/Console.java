package console;

import event.EventManager;

public class Console extends EventManager {

	private static Console instance = null;
	
	protected Console() {
		super();
	}
	
	public void log(String s) {
		sendMessage(new ConsoleEvent(s));
	}
	
	public static Console instance() {
		if (instance == null) {
			instance = new Console();
		}
		return(instance);
	}
}

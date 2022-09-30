package console;

import event.EventManager;

public class Console extends EventManager<ConsoleEvent> {

	private static Console instance = null;


	protected Console() {
		super();
	}


	public static void log(String s) {
		Console.instance().sendMessage(new ConsoleEvent(s));
	}


	public static Console instance() {
		if (instance == null) {
			instance = new Console();
		}
		return (instance);
	}
}

package console;

import event.EventManager;

public class Console extends EventManager{

	private static Console instance = null;

	public static boolean mirrorToJavaConsole = true;

	protected Console() {
		super();
	}


	public static void log(String s) {
		Console.instance().sendMessage(new ConsoleEvent(s));
		
		if (mirrorToJavaConsole) {
			System.out.println(s);
		}
	}


	public static Console instance() {
		if (instance == null) {
			instance = new Console();
		}
		return (instance);
	}
}

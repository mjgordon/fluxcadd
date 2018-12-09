package io;

import java.util.ArrayList;

public abstract class OutputGeneric {
	public abstract void send(ArrayList<CommandMessage> messages);
	public abstract void stop();
}

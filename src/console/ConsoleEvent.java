package console;

import java.util.Date;

import event.EventMessage;

public class ConsoleEvent extends EventMessage {

	public final Date timeStamp;
	public final String description;
	
	public ConsoleEvent(String description) {
		this(new Date(),description);
	}
	
	public ConsoleEvent(Date timeStamp, String description) {
		this.timeStamp = timeStamp;
		this.description = description;
	}
	
	int type;
	
	public static final int TYPE_MESSAGE = 0;
	public static final int TYPE_WARNING = 1;
	public static final int TYPE_ERROR = 2;
	
	@Override
	public String toString() {
		return(timeStamp.toString() + " : " + description);
	}

}

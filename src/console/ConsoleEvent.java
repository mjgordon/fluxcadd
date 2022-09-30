package console;

import java.util.Date;

import event.EventMessage;

public class ConsoleEvent extends EventMessage {
	
	public static final int TYPE_MESSAGE = 0;
	public static final int TYPE_WARNING = 1;
	public static final int TYPE_ERROR = 2;

	public final Date timeStamp;
	
	private int type;
	
	public ConsoleEvent(String description) {
		this(new Date(),description);
	}
	
	public ConsoleEvent(Date timeStamp, String description) {
		this.timeStamp = timeStamp;
		this.data = description;
		this.type = TYPE_MESSAGE;
	}
	
	@Override
	public String toString() {
		return(timeStamp.toString() + " : " + data);
	}
}

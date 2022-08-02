package io;

import robocam.DrawbotConstants;

public class CommandMessage {
	public byte id;
	public byte[] data;
	
	public CommandMessage(byte command) {
		this.id = command;
		this.data = DrawbotConstants.EMPTY_DATA;
	}
	
	public CommandMessage(byte command, byte[] data) {
		this.id = command;
		this.data = data;
	}
	
	public CommandMessage(int command, byte[] data) {
		this.id = (byte)command;
		this.data = data;
	}
	
	@Override
	public String toString() {
		String out = id + "";
		out += " : ";
		for (byte b : data) {
			out += b + "";
			out +=',';
		}
		return(out);
	}
}

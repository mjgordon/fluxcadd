package io;

import utility.DrawbotConstants;

public class SerialMessage {
	public byte id;
	public byte[] data;
	
	public SerialMessage(byte command) {
		this.id = command;
		this.data = DrawbotConstants.EMPTY_DATA;
	}
	
	public SerialMessage(byte command, byte[] data) {
		this.id = command;
		this.data = data;
	}
	
	public SerialMessage(int command, byte[] data) {
		this.id = (byte)command;
		this.data = data;
	}
}

package io;

public class SerialMessage {
	public byte id;
	public byte[] data;
	
	public SerialMessage(byte command, byte[] data) {
		this.id = command;
		this.data = data;
	}
}

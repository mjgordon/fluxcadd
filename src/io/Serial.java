package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.openmuc.jrxtx.*;

public class Serial {

	private String port;

	private Thread currentConnection;

	public Serial(String port) {
		this.port = port;
	}

	public void send(ArrayList<SerialMessage> messages) {
		currentConnection = new Thread(new SerialConnection(port, messages));
		currentConnection.start();
	}

	private class SerialConnection implements Runnable {
		private SerialPort serial;
		private OutputStream outputStream;
		private InputStream inputStream;
		private ArrayList<SerialMessage> messages;

		private SerialConnection(String port, ArrayList<SerialMessage> messages) {
			try {
				this.messages = messages;
				serial = SerialPortBuilder.newBuilder(port).setBaudRate(9600).build();
				outputStream = serial.getOutputStream();
				inputStream = serial.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		public void run() {
			try {
				
				for (int i = 0; i < messages.size(); i++) {
					SerialMessage message = messages.get(i);
					outputStream.write(message.id);
					outputStream.write(message.data);

					byte status = (byte) inputStream.read();
					if (status != 0xA0) {
						break;
					}
				}
				serial.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}

package io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.openmuc.jrxtx.*;

public class OutputSerial extends OutputGeneric {

	private String port;

	private Thread currentConnection;

	private volatile boolean alive = true;

	public OutputSerial(String port) {
		this.port = port;
	}

	public void send(ArrayList<CommandMessage> messages) {
		currentConnection = new Thread(new SerialConnection(port, messages));
		currentConnection.start();
	}

	private class SerialConnection implements Runnable {
		private SerialPort serial;
		private OutputStream outputStream;
		private InputStream inputStream;
		private ArrayList<CommandMessage> messages;

		private SerialConnection(String port, ArrayList<CommandMessage> messages) {
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
			System.out.println("Streaming " + messages.size() + " messages");
			try {

				for (int i = 0; i < messages.size(); i++) {
					
					if (alive == false)
						break;
					CommandMessage message = messages.get(i);
					
					outputStream.write(message.id);
					if (message.data.length > 0) {
						outputStream.write(message.data);
					}
					System.out.println(message);

					byte status = (byte) inputStream.read();
//					System.out.println(i);
//					System.out.println(status);
					if (status != 0xA0) {
						System.out.println("Bad Response");
						break;
					}
				}
				//Flush out input buffer
				while(inputStream.available() > 0) {
					inputStream.read();
				}
				serial.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	public void stop() {
		alive = false;
	}
}

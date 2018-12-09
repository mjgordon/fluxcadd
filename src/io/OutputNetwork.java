package io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class OutputNetwork extends OutputGeneric {
	Socket socket;
	DataOutputStream dOut;

	public OutputNetwork(String ip, int port) {
		socket = null;
		try {
			socket = new Socket(ip, port);
			dOut = new DataOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(ArrayList<CommandMessage> messages) {
		for (CommandMessage message : messages) {

			try {
				dOut.write(message.id);
				dOut.write(message.data);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stop() {
		
	}

}

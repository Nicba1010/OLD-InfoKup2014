package infoKupProcess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Recieve implements Runnable {

	@Override
	public void run() {
		while (true) {
			try {
				Socket clientSocket = TCPClient.clientSocket;
				BufferedReader inFromServer = new BufferedReader(
						new InputStreamReader(clientSocket.getInputStream()));
				String messageRecieve = inFromServer.readLine();
				System.out.println(messageRecieve);
				if (messageRecieve != null)System.out.println(messageRecieve);
			} catch (Exception e) {
				System.out.print(e);
			}
		}
	}

}

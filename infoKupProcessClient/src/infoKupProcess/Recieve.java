package infoKupProcess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Recieve implements Runnable {

	@Override
	public void run() {
		while (true) {
			try {
				Socket clientSocket = new Socket("127.0.0.1", 25565);
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				System.out.print("Received string: '");

				while (!in.ready()) {
				}
				System.out.println(in.readLine());

				System.out.print("'\n");
				in.close();
			} catch (Exception e) {
				System.out.print(e);
			}
		}
	}

}

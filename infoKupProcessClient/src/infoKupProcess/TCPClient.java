package infoKupProcess;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;

class TCPClient {
	public static void main(String args[]) throws Exception {
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				sendProcesses();
			}
		}, 0, 2000);
//		Thread recieve = new Thread(new Recieve());
//		recieve.run();
	}

	public static void sendMessage(String msg) {
		String messageRecieve=null;
		Socket clientSocket;
		try {
			clientSocket = new Socket("127.0.0.1", 25565);
			DataOutputStream outToServer = new DataOutputStream(
					clientSocket.getOutputStream());
			outToServer.writeBytes(msg + '\n');
//			BufferedReader inFromServer = new BufferedReader(
//					new InputStreamReader(clientSocket.getInputStream()));
//			messageRecieve = inFromServer.readLine();
//			System.out.println(messageRecieve);
//			if (messageRecieve != null)
//				processMessage(messageRecieve);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void processMessage(String message) {
		System.out.println(message);
		if(message.contains("killproc ")){
			message = message.replace("killproc ", "");
			killProcess(message);
		}
	}

	private static void killProcess(String processName){
		try {
			Runtime.getRuntime().exec("taskkill /F /IM "+processName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void sendProcesses() {
		try {
			String processes = "";
			String line;
			Process proc = Runtime.getRuntime().exec("tasklist");
			BufferedReader input = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			while ((line = input.readLine()) != null) {
				line = StringUtils.substring(line, 0, 30);
				if (!(line.contains("=======") || line.contains("Image Name")
						|| line.contains("System") || line.contains("csrss")
						|| line.contains("dwm") || line.contains("winlogon")
						|| line.contains("svc") || line.contains("taskhost"))) {
					if (!processes.equalsIgnoreCase(""))
						processes = processes + ":" + line;
					else
						processes = line;
				}
			}
			processes = processes.replaceAll(" ", "");
			input.close();
			sendMessage(processes);
		} catch (Exception err) {
//			err.printStackTrace();
		}
	}
}
package infoKupProcess;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang3.StringUtils;

class TCPClient {
	public static Socket clientSocket;
	public static boolean debug = false, rand = false;
	static String ip;
	static int sock;

	public static void main(String args[]) throws Exception {
		if (!debug) {
			ip = args[0];
			sock = Integer.parseInt(args[1]);
		}
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				sendProcesses();
			}
		}, 0, 2000);
	}

	public static void sendMessage(String msg) {
		String messageRecieve = null;
		try {
			clientSocket = new Socket(ip, sock);
			DataOutputStream outToServer = new DataOutputStream(
					clientSocket.getOutputStream());
			outToServer.writeBytes(msg + '\n');
			BufferedReader inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			messageRecieve = inFromServer.readLine();
			processMessage(messageRecieve);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void processMessage(String message) {
		debugPrint(message);
		if (message.contains(" killproc ")
				&& message.contains(System.getenv("computername"))) {
			message = message.replace(System.getenv("computername")
					+ " killproc ", "");
			killProcess(message);
		}
	}

	public static void debugPrint(String s) {
		System.out.println("DEBUG: " + s);
	}

	private static void killProcess(String processName) {
		try {
			Runtime.getRuntime().exec("taskkill /F /IM " + processName);
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
			if (debug)
				processes = Float.toString(new Random().nextFloat()) + ";"
						+ processes;
			else
				processes = System.getenv("computername") + ";" + processes;
			processes = processes.replaceAll(" ", "");
			input.close();
			sendMessage(processes);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
}
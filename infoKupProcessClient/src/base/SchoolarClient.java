package base;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.StringUtils;

import base.plugins.PluginLoader;

class SchoolarClient {
	public static Socket clientSocket;
	public static boolean debug = false, defaultSettings = false, rand = false;
	static String ip = "127.0.0.1";
	static int socket = 25565;
	static PluginLoader pl;
	static String ftpServerIP, ftpServerUsername, ftpServerPassword;

	public static void settings() {
		JTextField clientIp = new JTextField("" + ip);
		JTextField clientSocket = new JTextField("" + socket);
		final JPanel settingsSocketPanel = new JPanel(new GridLayout(0, 1));
		settingsSocketPanel.add(new JLabel("IP Servera: "));
		settingsSocketPanel.add(clientIp);
		settingsSocketPanel.add(new JLabel("Port Servera: "));
		settingsSocketPanel.add(clientSocket);
		int input = JOptionPane.showConfirmDialog(null, settingsSocketPanel,
				"Postavke", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);

		if (input == JOptionPane.OK_OPTION) {
			ip = clientIp.getText();
			socket = Integer.parseInt(clientSocket.getText());

		} else {
			System.out.println("Using default port!");
		}
		System.out.println("IP: " + ip);
		System.out.println("Socket: " + socket);

	}

	public static void main(String args[]) throws Exception {
		settings();
		if (!debug) {
			if (args.length == 1 && args[0].toString() == "-defaultip") {
				defaultSettings = true;
			}
		}
		pl = new PluginLoader(false);
		pl.runClient("Test", new String[] { "nis" });
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
			System.out.println(msg);
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
		} else if (message.contains("FTP")) {
			if (!message.contains("FTPNOTON")) {
				message = message.replaceAll("FTP:", "");
				String[] ftpInfo = message.split(":");
				ftpServerIP = ftpInfo[0];
				ftpServerUsername = ftpInfo[1];
				ftpServerPassword = ftpInfo[2];
				System.out.println(ftpServerIP + ":" + ftpServerUsername + ":"
						+ ftpServerPassword);
				Image image = new Image();
				image.runClient(ftpServerIP, ftpServerUsername,
						ftpServerPassword);
			}
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

		JOptionPane pane = new JOptionPane(text, JOptionPane.PLAIN_MESSAGE);

		JDialog dialog = pane.createDialog("Poruka");
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
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
						|| line.contains("svc") || line.contains("taskhost")
						|| line.contains("CCC") || line.contains("nvtray")
						|| line.contains("nvvsvc") || line.contains("nvxdsync"))
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
				processes = System.getenv("computername") + "" + ";"
						+ processes;
			processes = processes.replaceAll(" ", "");
			input.close();
			sendMessage(processes);
		} catch (Exception err) {
			// err.printStackTrace();
		}
	}
}
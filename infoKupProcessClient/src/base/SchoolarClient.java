package base;

import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
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
import base.security.RSA;
import base.util.SettingsClient;

class SchoolarClient {
	public static String computerName = System.getenv("computername") + "1";
	public static Socket clientSocket;
	public static boolean debug = false, defaultSettings = false, rand = false;
	static String ip = "127.0.0.1";
	static int socket = 25565;
	static PluginLoader pl;
	static String ftpServerIP, ftpServerUsername, ftpServerPassword;
	static SettingsClient settings;
	static Object[] objectSettings;
	static String path = System.getenv("APPDATA")
			+ "\\.Schoolar\\settingsClient" + computerName
			+ ".xml";
	static File settingsFile = new File(path);
	static boolean first = false;
	static RSA encryption;

	public static void settings(boolean b) {
		if (!settingsFile.exists() || b) {
			settings = new SettingsClient(computerName);
			getSettings();
			JTextField clientIp = new JTextField("" + ip);
			JTextField clientSocket = new JTextField("" + socket);
			final JPanel settingsSocketPanel = new JPanel(new GridLayout(0, 1));
			settingsSocketPanel.add(new JLabel("IP Servera: "));
			settingsSocketPanel.add(clientIp);
			settingsSocketPanel.add(new JLabel("Port Servera: "));
			settingsSocketPanel.add(clientSocket);
			int input = JOptionPane.showConfirmDialog(null,
					settingsSocketPanel, "Postavke",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (input == JOptionPane.OK_OPTION) {
				ip = clientIp.getText();
				socket = Integer.parseInt(clientSocket.getText());
			} else {
				System.exit(0);
			}
			System.out.println("IP: " + ip);
			System.out.println("Socket: " + socket);
			setSettings();
		}
	}

	public static void getSettings() {
		objectSettings = settings.getSettings();
		ip = objectSettings[0].toString();
		socket = Integer.parseInt(objectSettings[1].toString());
	}

	public static void setSettings() {
		objectSettings[0] = ip;
		objectSettings[1] = socket;
		settings.setSettings(objectSettings);
	}

	public static void main(String args[]) throws Exception {
		settings(false);
		if (!debug) {
			if (args.length == 1 && args[0].toString().contains("settings")) {
				settings(true);
			}
		}
		encryption = new RSA(computerName);
		pl = new PluginLoader(false);
		pl.runClient("Test", new String[] { "nis" });
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				sendProcesses();
			}
		}, 0, 2000);
	}

	public static void sendMessage(String msg) throws IOException {
		if (!first) {
			msg = msg + "-:-" + encryption.readModulusAndExponent()[0] + "-:-"
					+ encryption.readModulusAndExponent()[1];
			first = true;
		}
		String messageRecieve = null;
		try {
			if (defaultSettings) {
				clientSocket = new Socket("127.0.0.1", 25565);
			} else {
				clientSocket = new Socket("127.0.0.1", 25566);
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

	private static void processMessage(String message) throws IOException {
		debugPrint(message);
		message = message.replaceFirst(";", "");
		String[] messageArray = message.split(";");
		byte[] bytes = new byte[messageArray.length];
		int pos = 0;
		for (String s : messageArray) {
			bytes[pos++] = Byte.parseByte(s);
		}
		message = encryption.decryptData(bytes);
		if (message.contains(" killproc ")
				&& message.contains(computerName)) {
			message = message.replace(computerName
					+ " killproc ", "");
			killProcess(message);
		} else if (message.contains(" command ")
				&& message.contains(computerName)) {
			message = message.replace(computerName
					+ " command ", "");
			command(message);
		} else if (message.contains(" popup ")
				&& message.contains(computerName)) {
			message = message.replace(
					computerName + " popup ", "");
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
		} else if (message.contains("ShutdownClient")) {
			System.exit(0);
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
				processes = computerName + "" + ";"
						+ processes;
			processes = processes.replaceAll(" ", "");
			input.close();
			sendMessage(processes);
		} catch (Exception err) {
			// err.printStackTrace();
		}
	}
}
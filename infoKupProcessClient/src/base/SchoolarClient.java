package base;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
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
	public static Freeze freeze;
	static Thread freezeThread;
	public static Socket clientSocket;
	static String computerName;
	static String osInfo = System.getProperty("os.name") + " "
			+ System.getProperty("os.version") + " "
			+ System.getProperty("os.arch");
	static String javaInfo = System.getProperty("java.version") + " "
			+ System.getProperty("java.vendor");
	static String javaPath = System.getProperty("java.home");
	static String homeDir = System.getProperty("user.home");
	static String extIp = null;
	static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	static double width = screenSize.getWidth();
	static double height = screenSize.getHeight();
	static int x = (int) Math.round(width);
	static int y = (int) Math.round(height);
	public static boolean debug = false, defaultSettings = false, rand = false;
	static String ip = "127.0.0.1";
	static int socket = 25565;
	static PluginLoader pl;
	static String ftpServerIP, ftpServerUsername, ftpServerPassword;
	static SettingsClient settings;
	static Object[] objectSettings;
	static String path = System.getenv("APPDATA")
			+ "\\.Schoolar\\settingsClient" + System.getenv("computername")
			+ ".xml";
	static File settingsFile = new File(path);
	static boolean first = false;
	static RSA encryption;
	private static boolean connected = false;
	static Socket availbilityCheckSocket;
	static boolean availbilityCheckLoop = true;

	public static void settings(boolean b) {
		if (!settingsFile.exists() || b) {
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
		} else {
			getSettings();
		}
	}

	public static void getSettings() {
		objectSettings = settings.getSettings();
		ip = objectSettings[0].toString();
		socket = Integer.parseInt(objectSettings[1].toString());
		System.out.println(ip + ":" + socket);
	}

	public static void setSettings() {
		objectSettings[0] = ip;
		objectSettings[1] = socket;
		settings.setSettings(objectSettings);
	}

	public static void main(String args[]) {
		settings = new SettingsClient(computerName);
		try {
			URL connection = new URL("http://checkip.amazonaws.com/");
			URLConnection con = connection.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			extIp = reader.readLine();
		} catch (Exception e) {
			extIp = "Nema pristup WAN-u";
			e.printStackTrace();
		}
		computerName = System.getenv("computername");
		computerName = computerName.replaceAll("è", "c");
		computerName = computerName.replaceAll("æ", "c");
		computerName = computerName.replaceAll("Æ", "C");
		computerName = computerName.replaceAll("È", "C");
		computerName = computerName.replaceAll("ž", "z");
		computerName = computerName.replaceAll("Ž", "z");
		computerName = computerName.replaceAll("š", "s");
		computerName = computerName.replaceAll("Š", "S");
		computerName = computerName.replaceAll("ð", "d");
		computerName = computerName.replaceAll("Ð", "D");
		Timer timer1 = new Timer();
		timer1.schedule(task, 01, 5001);
		freeze = new Freeze();
		freezeThread = new Thread(freeze);
		freezeThread.start();
		settings(false);
		if (!debug) {
			if (args.length == 1 && args[0].toString().contains("settings")) {
				settings(true);
			}
		}
		try {
			encryption = new RSA(computerName);
			pl = new PluginLoader(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pl.runClient("Test", new String[] { "nis" });
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				sendProcesses();
			}
		}, 0, 2000);
	}

	static TimerTask task = new TimerTask() {

		@Override
		public void run() {
			if (connected == false) {
			}
		}
	};

	public static boolean hostAvailabilityCheck() {
		try (Socket s = new Socket(ip, socket)) {
			return true;
		} catch (IOException ex) {
		}
		return false;
	}

	public static void sendMessage(String msg) throws IOException {
		while (availbilityCheckLoop) {
			if (hostAvailabilityCheck()) {
				System.out.println("Server Online");
				availbilityCheckLoop = false;
			} else if (!hostAvailabilityCheck()) {
				System.out.println("Server Offline");
				availbilityCheckLoop = true;
			}
		}
		if (!first) {
			msg = msg + "-:-" + encryption.readModulusAndExponent()[0] + "-:-"
					+ encryption.readModulusAndExponent()[1] + "-:-" + osInfo
					+ "-:-" + javaInfo + "-:-" + javaPath + "-:-" + homeDir
					+ "-:-" + extIp;
			System.out.println(msg);
			first = true;
		}
		String messageRecieve = null;
		try {
			clientSocket = new Socket(ip, socket);
			DataOutputStream outToServer = new DataOutputStream(
					clientSocket.getOutputStream());
			outToServer.writeBytes(msg + '\n');
			BufferedReader inFromServer = new BufferedReader(
					new InputStreamReader(clientSocket.getInputStream()));
			messageRecieve = inFromServer.readLine();
			processMessage(messageRecieve);
		} catch (UnknownHostException e) {
			// e.printStackTrace();
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	private static void processMessage(String message) throws IOException {
		if (message != null) {
			debugPrint(message);
			message = message.replaceFirst(";", "");
			String[] messageArray = message.split(";");
			byte[] bytes = new byte[messageArray.length];
			int pos = 0;
			for (String s : messageArray) {
				bytes[pos++] = Byte.parseByte(s);
			}
			message = encryption.decryptData(bytes);
			System.out.println(message);
			if (message.contains(" killproc ")
					&& message.contains(computerName)) {
				message = message.replace(computerName + " killproc ", "");
				killProcess(message);
			} else if (message.contains(" command ")
					&& message.contains(computerName)) {
				message = message.replace(computerName + " command ", "");
				command(message);
			} else if (message.contains(" popup ")
					&& message.contains(computerName)) {
				message = message.replace(computerName + " popup ", "");
				textPopup(message);
			} else if (message.contains("FTP")) {
				if (!message.contains("FTPNOTON")) {
					message = message.replaceAll("FTP:", "");
					String[] ftpInfo = message.split(":");
					ftpServerIP = ftpInfo[0];
					ftpServerUsername = ftpInfo[1];
					ftpServerPassword = ftpInfo[2];
					System.out.println(ftpServerIP + ":" + ftpServerUsername
							+ ":" + ftpServerPassword);
					Image image = new Image();
					image.runClient(ftpServerIP, ftpServerUsername,
							ftpServerPassword);
				}
			} else if (message.contains("ShutdownClient")) {
				System.exit(0);
			} else if (message.contains("freezeClient")) {
				freeze.toggle();
				System.out.println("Freeze test");
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
				if (!(line.contains("=======") || line.contains("Image")
						|| line.contains("javaw") || line.contains("LMl")
						|| line.contains("Rtl") || line.contains("arm")
						|| line.contains("audio") || line.contains("jusched")
						|| line.contains("svchost") || line.contains("dwm")
						|| line.contains("java") || line.contains("CCC")
						|| line.contains("spoolsv") || line.contains("Fuel")
						|| line.contains("service") || line.contains("MOM")
						|| line.contains("csrss") || line.contains("winlogon")
						|| line.contains("System") || line.contains("svs")
						|| line.contains("taskhost") || line.contains("nvtray")
						|| line.contains("nvvsvc") || line.contains("nvxdsync")
						|| line.contains("wininit")
						|| line.contains("atiesrxx")
						|| line.contains("conhost") || line.contains("lsm")
						|| line.contains("taskeng")
						|| line.contains("TrustedInstaller")
						|| line.contains("WUDFHost") || line.contains("RtWLan")
						|| line.contains("ati") || line.contains("amd")
						|| line.contains("nv") || line.contains("sqlwriter")
						|| line.contains("RAV") || line.contains("lsass")
						|| line.contains("Search") || line.contains("mDNS")
						|| line.contains("wmp") || line.contains("smss")
						|| line.contains("tasklist")
						|| line.contains("AdminService")
						|| line.contains("AssistantServices")
						|| line.contains("Ath") || line.contains("BtvStack")
						|| line.contains("CDA")
						|| line.contains("CancelAutoPlay")
						|| line.contains("EsaySpeedUp") || line.contains("IA")
						|| line.contains("Intel") || line.contains("LM")
						|| line.contains("MovieColorEnhancer")
						|| line.contains("NASvc") || line.contains("SWM")
						|| line.contains("SamoyedAgent")
						|| line.contains("SmartSetting")
						|| line.contains("Syn") || line.contains("igf"))
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
				processes = computerName + "" + ";" + processes;
			processes = processes.replaceAll(" ", "");
			input.close();
			sendMessage(processes);
		} catch (Exception err) {
			// err.printStackTrace();
		}
	}
}
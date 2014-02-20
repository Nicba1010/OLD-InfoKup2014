package base;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import base.UIComponents.Client;
import base.plugins.PluginLoader;
import base.security.RSA;
import base.splash.SplashScreen;
import base.util.Buffer;
import base.util.Settings;

@SuppressWarnings("serial")
public class SchoolarServer extends JFrame {
	static int screenWidth = 1280;
	static int screenHeight = 720;
	JButton settingsButton;
	JButton quitButton;
	JButton closeConButton;
	JButton sendAllButton;
	JPanel defaultButtonPanel;
	public static ServerSocket inSocket;
	private static boolean running = false;

	public static ArrayList<Client> clientList = new ArrayList<Client>();

	public JFrame mainFrame = this;
	public static Socket connectionSocket;
	private static String[] TCPData = new String[2];
	private static String[] TCPDataWithKey = new String[2];

	public static ArrayList<String> clients = new ArrayList<String>();
	public static ArrayList<String> removedClients = new ArrayList<String>();
	public static Buffer buffer = new Buffer();

	public static JPanel mainPanel;
	public static JPanel infoScrollPanel;
	public static int socketTCP = 25565;
	public static int cond = 0;
	public static boolean defaultSettings = false, nosplash = false;
	static JScrollPane scrollablePCinfo;
	static JFrame splashFrame;
	static PluginLoader pluginLoader;
	static String ftpServerIP = "127.0.0.1", ftpServerUsername = "infokup",
			ftpServerPassword = "12346789", ftpServerIPRemote = "127.0.0.1";
	static boolean ftpOn;
	private boolean firstTime = true;
	static Object[] objectSettings = new Object[8];
	static Settings settings;
	static RSA encryption = new RSA();

	public SchoolarServer() {
		settings = new Settings();
		settingsPopup();
		setResizable(false);
		try {
			pluginLoader = new PluginLoader(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		initUI();
		updateBounds();
		firstTime = false;
	}

	public static void getSettings() {
		objectSettings = settings.getSettings();
		screenWidth = (int) objectSettings[0];
		screenHeight = (int) objectSettings[1];
		System.out.println(objectSettings[1].toString());
		socketTCP = (int) objectSettings[2];
		ftpOn = (boolean) objectSettings[3];
		ftpServerIP = (String) objectSettings[4];
		ftpServerIPRemote = (String) objectSettings[5];
		ftpServerUsername = (String) objectSettings[6];
		ftpServerPassword = (String) objectSettings[7];
	}

	public static void setSettings() {
		objectSettings[0] = screenWidth;
		objectSettings[1] = screenHeight;
		objectSettings[2] = socketTCP;
		objectSettings[3] = ftpOn;
		objectSettings[4] = ftpServerIP;
		objectSettings[5] = ftpServerIPRemote;
		objectSettings[6] = ftpServerUsername;
		objectSettings[7] = ftpServerPassword;
		settings.setSettings(objectSettings);
	}

	public void settingsPopup() {
		getSettings();
		JTextField width = new JTextField("" + screenWidth);
		JTextField height = new JTextField("" + screenHeight);
		final JTextField serverIP = new JTextField("" + ftpServerIP);
		final JTextField serverIpRemote = new JTextField("" + ftpServerIPRemote);
		final JTextField serverUser = new JTextField("" + ftpServerUsername);
		final JTextField serverPass = new JTextField("" + ftpServerPassword);
		JCheckBox checkbox = new JCheckBox("Prikaz klijentske slike");
		JTextField TCPsocket = new JTextField("" + socketTCP);
		final JPanel settingsSocketPanel = new JPanel(new GridLayout(0, 1));
		settingsSocketPanel.add(new JLabel("Sirina: "));
		settingsSocketPanel.add(width);
		settingsSocketPanel.add(new JLabel("Visina: "));
		settingsSocketPanel.add(height);
		checkbox.setSelected(ftpOn);
		if (firstTime) {
			settingsSocketPanel.add(new JLabel("Port: "));
			settingsSocketPanel.add(TCPsocket);
			settingsSocketPanel.add(checkbox);
			settingsSocketPanel.add(new JLabel("FTP Server IP"));
			settingsSocketPanel.add(serverIP);
			settingsSocketPanel.add(new JLabel("FTP Remote Server IP"));
			settingsSocketPanel.add(serverIpRemote);
			settingsSocketPanel.add(new JLabel("FTP Server Username"));
			settingsSocketPanel.add(serverUser);
			settingsSocketPanel.add(new JLabel("FTP Server Password"));
			settingsSocketPanel.add(serverPass);
			if (checkbox.isSelected()) {
				serverIP.setEnabled(true);
				serverIpRemote.setEnabled(true);
				serverUser.setEnabled(true);
				serverPass.setEnabled(true);
				settingsSocketPanel.revalidate();
				settingsSocketPanel.repaint();
			} else {
				serverIP.setEnabled(false);
				serverIpRemote.setEnabled(false);
				serverUser.setEnabled(false);
				serverPass.setEnabled(false);
				settingsSocketPanel.revalidate();
				settingsSocketPanel.repaint();
			}
			checkbox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					JCheckBox checkbox = (JCheckBox) event.getSource();
					if (checkbox.isSelected()) {
						serverIP.setEnabled(true);
						serverIpRemote.setEnabled(true);
						serverUser.setEnabled(true);
						serverPass.setEnabled(true);
						settingsSocketPanel.revalidate();
						settingsSocketPanel.repaint();
					} else {
						serverIP.setEnabled(false);
						serverIpRemote.setEnabled(false);
						serverUser.setEnabled(false);
						serverPass.setEnabled(false);
						settingsSocketPanel.revalidate();
						settingsSocketPanel.repaint();
					}
				}
			});
		}
		int input = JOptionPane.showConfirmDialog(null, settingsSocketPanel,
				"Postavke", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (input == JOptionPane.CANCEL_OPTION && firstTime) {
			System.exit(0);
		}
		if (firstTime) {
			if (checkbox.isSelected()) {
				ftpOn = true;
			} else {
				ftpOn = false;
			}
			if (input == JOptionPane.OK_OPTION) {
				socketTCP = Integer.parseInt(TCPsocket.getText());
				ftpServerIP = serverIP.getText();
				ftpServerIPRemote = serverIpRemote.getText();
				ftpServerUsername = serverUser.getText();
				ftpServerPassword = serverPass.getText();

			} else {
				System.out.println("Using default port!");
			}
		}
		if (input == JOptionPane.OK_OPTION) {
			screenWidth = Integer.parseInt(width.getText());
			screenHeight = Integer.parseInt(height.getText());
		}
		if (!firstTime)
			updateBounds();
		setSettings();
	}

	public void updateBounds() {
		this.setSize(screenWidth, screenHeight);
		quitButton.setBounds(screenWidth - 100 - 4, screenHeight - 30 * 2 + 4,
				100, 30);
		settingsButton.setBounds(screenWidth - 100 - 4,
				screenHeight - 85, 100, 30);
		closeConButton.setBounds(screenWidth - 300 - 4, screenHeight - 30*2+4, 200,
				30);
		sendAllButton.setBounds(screenWidth - 300 - 4, screenHeight - 85, 200,
				30);
		scrollablePCinfo.setBounds(0, 0, screenWidth - 3, screenHeight - 100);
		for (Client client : clientList) {
			client.setSize(new Dimension(250, screenHeight - 125));
		}
		infoScrollPanel.repaint();
		infoScrollPanel.revalidate();
		this.repaint();
		this.revalidate();
	}

	public void initUI() {
		int offsetWidth = 5;
		int offsetHeight = -3;
		setTitle("Schoolar Server");
		mainPanel = new JPanel();
		infoScrollPanel = new JPanel();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().add(mainPanel);
		mainPanel.setLayout(null);
		infoScrollPanel.setLayout(new BoxLayout(infoScrollPanel,
				BoxLayout.X_AXIS));

		setSize(screenWidth, screenHeight);
		setLocationRelativeTo(null);
		{
			quitButton = new JButton("Ugasi");
			quitButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					running = false;
					System.exit(0);
				}
			});
		}
		{
			settingsButton = new JButton("Postavke");
			settingsButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event1) {
					settingsPopup();
					mainFrame.setSize(screenWidth, screenHeight);
				}
			});
		}
		{
			closeConButton = new JButton("Ugasi sve konekcije");
			closeConButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event1) {
					shutdownConnections();
				}
			});
		}
		{
			sendAllButton = new JButton("Posalji komandu svima");
			sendAllButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event1) {
					sendCommandToAll(JOptionPane.showInputDialog("Unesi komandu:"));
				}
			});
		}
        mainPanel.add(sendAllButton);
		mainPanel.add(closeConButton);
		mainPanel.add(quitButton);
		mainPanel.add(settingsButton);

		this.addWindowStateListener(new WindowStateListener() {

			@Override
			public void windowStateChanged(WindowEvent e) {
				updateBounds();
			}
		});
		scrollablePCinfo = new JScrollPane(infoScrollPanel);
		scrollablePCinfo.setBounds(0, 0, mainFrame.getWidth() - offsetWidth,
				mainFrame.getHeight() - 110 - offsetHeight);
		scrollablePCinfo
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		mainPanel.add(scrollablePCinfo);
		setVisible(false);
	}

	private static void parseArgs(String args[]) {
		if (args.length == 1 && args[0].toString().equalsIgnoreCase("nosplash")) {
			nosplash = true;
		} else {
			System.out.println(args.length);
			for (int i = 0; i < args.length; i++) {
				System.out.println(i + ":" + args[i]);
			}

		}
	}

	public static void main(String args[]) throws Exception {
		parseArgs(args);
		running = true;
		if (!nosplash)
			new SplashScreen("images/splash.png", 500, 2, 750);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SchoolarServer server = new SchoolarServer();
				server.setVisible(true);
				Image image = null;
				try {
					image = ImageIO.read(getClass().getResource(
							"images/icon.png"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				server.setIconImage(image);
			}
		});
		start();
	}

	public static void start() {
		String clientSentence;
		try {
			inSocket = new ServerSocket(25566);
			while (running) {
				connectionSocket = inSocket.accept();
				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));
				clientSentence = inFromClient.readLine();
				if (clientSentence != null) {
					boolean newClient = false;
					TCPData = clientSentence.split(";");
					if (clients.contains(TCPData[0])) {
						System.out.println(TCPData[1]);
					} else {
						TCPDataWithKey = clientSentence.split("-:-");
						BigInteger modulus = null;
						BigInteger exponent = null;
						if (TCPDataWithKey.length == 3) {
							modulus = new BigInteger(TCPDataWithKey[1]);
							exponent = new BigInteger(TCPDataWithKey[2]);
						}
						TCPData = TCPDataWithKey[0].split(";");
						clientList.add(new Client(0, 0, 250,
								screenHeight - 125, infoScrollPanel,
								TCPData[0], pluginLoader, ftpServerIP,
								ftpServerUsername, ftpServerPassword, ftpOn,
								modulus, exponent));
						clients.add(TCPData[0]);
						newClient = true;
					}

					for (Client client : clientList) {
						if (client.getName().equalsIgnoreCase(TCPData[0])) {
							client.setData(TCPData[1].split(":"));
						}
					}
					sendResponse(newClient);
					connectionSocket.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void sendCommandToAll(String msg) {
		for (Client client : clientList) {
			buffer.addToBuffer("command", msg, client.getName());
		}
	}

	private static void shutdownConnections() {
		for (Client client : clientList) {
			buffer.addToBuffer("ShutdownClient", "", client.getName());
		}
		for (int i = 0; i <= infoScrollPanel.getComponentCount(); i++) {
			System.out.println(i);
			infoScrollPanel.remove(i);
		}
		infoScrollPanel.repaint();
		infoScrollPanel.revalidate();
	}
	

	private static String getEncryptedData(BigInteger modulus,
			BigInteger exponent, String msg) throws NoSuchAlgorithmException,
			InvalidKeySpecException, IOException {
		byte[] encryptedData = encryption.encryptData(msg, modulus, 
				exponent);
		String bytes = "";
		for (byte b : encryptedData) {
			bytes = bytes + ";" + Byte.toString(b);
		}
		return bytes;
	}

	private static void sendResponse(boolean newClient) {
		try {
			if (buffer.len() > 0) {
				for (int i = 0; i < buffer.len(); i++) {
					String arg0 = buffer.get(i)[0];
					String arg1 = buffer.get(i)[1];
					String clientName = buffer.get(i)[2];
					if (clientName.equalsIgnoreCase(TCPData[0])) {
						BigInteger modulus = null, exponent = null;
						for (Client client : clientList) {
							if (client.getName().equalsIgnoreCase(TCPData[0])) {
								modulus = client.getModulus();
								exponent = client.getExponent();
							}
						}
						System.out
								.println(arg0 + ":" + arg1 + ":" + clientName);
						DataOutputStream outToClient = new DataOutputStream(
								connectionSocket.getOutputStream());
						String msg;
						msg = clientName + " " + arg0 + " " + arg1 + "\n";

						outToClient.writeBytes(getEncryptedData(modulus,
								exponent, msg));
						buffer.remove(i);
						break;
					}

				}
			} else {
				BigInteger modulus = null, exponent = null;
				for (Client client : clientList) {
					if (client.getName().equalsIgnoreCase(TCPData[0])) {
						modulus = client.getModulus();
						exponent = client.getExponent();
					}

				}
				DataOutputStream outToClient = new DataOutputStream(
						connectionSocket.getOutputStream());
				String message;
				if (newClient && ftpOn) {
					message = ("FTP:" + ftpServerIPRemote + ":"
							+ ftpServerUsername + ":" + ftpServerPassword + "\n");
				} else if (!ftpOn) {
					message = ("FTPNOTON\n");
				} else {
					message = ("OK" + "\n");
				}
				outToClient.writeBytes(getEncryptedData(modulus, exponent,
						message));
			}
		} catch (Exception e) {
		}
	}

	public static void addPC(Component comp, int index) {
		infoScrollPanel.add(comp, index);
		infoScrollPanel.revalidate();
		infoScrollPanel.repaint();
	}
}
package base;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

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
	JScrollPane infoScrollPane;
	JPanel defaultButtonPanel;
	public static ServerSocket inSocket;
	private static boolean running = false;

	public static ArrayList<Client> clientList = new ArrayList<Client>();

	public JFrame mainFrame = this;
	public static Socket connectionSocket;
	private static String[] TCPData = new String[2];

	public static ArrayList<String> clients = new ArrayList<String>();
	public static ArrayList<String> removedClients = new ArrayList<String>();
	public static Buffer buffer = new Buffer();

	public static JPanel mainPanel, infoScrollPanel;
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

	public static void color() {
		Color color = new Color(40, 40, 43);
		infoScrollPanel.setBackground(color);
		mainPanel.setBackground(color);
		scrollablePCinfo.setBackground(color);

	}

	public void settingsPopup() {
		/*
		 * nemoj ovo pobrisat Color colorbg = new Color(40, 40, 43); Color color
		 * = new Color(104, 33, 122);
		 */
		getSettings();
		// Color colortxt = new Color(211, 255, 236);
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
		if (input == JOptionPane.CANCEL_OPTION) {
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
		settingsButton.setBounds(screenWidth - 200 - 4,
				screenHeight - 30 * 2 + 4, 100, 30);
		closeConButton.setBounds(screenWidth - 200 - 4, screenHeight - 85, 200,
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
		// color();
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
			}
		});
		start();

	}

	public static void start() {
		String clientSentence;
		try {
			inSocket = new ServerSocket(socketTCP);
			while (running) {
				connectionSocket = inSocket.accept();
				BufferedReader inFromClient = new BufferedReader(
						new InputStreamReader(connectionSocket.getInputStream()));

				clientSentence = inFromClient.readLine();
				if (clientSentence != null) {
					TCPData = clientSentence.split(";");
					boolean newClient = false;
					if (clients.contains(TCPData[0])) {

					} else {
						System.out.println(TCPData[0]);
						clientList.add(new Client(0, 0, 250,
								screenHeight - 125, infoScrollPanel,
								TCPData[0], pluginLoader, ftpServerIP,
								ftpServerUsername, ftpServerPassword, ftpOn));
						System.out.println(ftpServerIP + ":"
								+ ftpServerUsername + ":" + ftpServerPassword);
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

	private static void shutdownConnections() {
		for (Client client : clientList) {
			buffer.addToBuffer("ShutdownClient", "", client.getName());
		}
		for (int i = 0; i < infoScrollPanel.getComponentCount(); i++) {
			infoScrollPanel.remove(i);
		}
		infoScrollPanel.repaint();
		infoScrollPanel.revalidate();
	}

	private static void sendResponse(boolean newClient) {
		try {
			if (buffer.len() > 0) {
				for (int i = 0; i < buffer.len(); i++) {
					String arg0 = buffer.get(i)[0];
					String arg1 = buffer.get(i)[1];
					String clientName = buffer.get(i)[2];
					if (clientName.equalsIgnoreCase(TCPData[0])) {
						System.out
								.println(arg0 + ":" + arg1 + ":" + clientName);
						System.out.println("OK");
						DataOutputStream outToClient = new DataOutputStream(
								connectionSocket.getOutputStream());
						String msg;
						msg = clientName + " " + arg0 + " " + arg1 + "\n";
						outToClient.writeBytes(msg);
						System.out.println(msg);
						buffer.remove(i);
						break;
					}

				}
			} else {
				DataOutputStream outToClient = new DataOutputStream(
						connectionSocket.getOutputStream());
				if (newClient && ftpOn) {
					outToClient.writeBytes("FTP:" + ftpServerIPRemote + ":"
							+ ftpServerUsername + ":" + ftpServerPassword
							+ "\n");
				} else if (!ftpOn) {
					outToClient.writeBytes("FTPNOTON\n");
				} else
					outToClient.writeBytes("OK" + "\n");
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